//
// Created by rahul on 21/01/21.
//

#ifndef MIDDLEWARE_CONNECTION_H
#define MIDDLEWARE_CONNECTION_H

#include <boost/asio.hpp>
#include <boost/thread.hpp>
#include <boost/shared_ptr.hpp>
#include "protocol/msg_processor.h"
#include "protocol/messages/message.h"

#define BUFFER_SIZE 32768

class Connection : public boost::enable_shared_from_this<Connection> {
public:
    typedef boost::shared_ptr<Connection> Pointer;

    static Pointer create(boost::asio::io_context &ioContext, IOContextPool &repoContextPool) {
        return Pointer(new Connection(ioContext, repoContextPool));
    }

    void read(boost::asio::io_context &context) {

        auto thisPtr = shared_from_this();
        socket_.async_read_some(boost::asio::buffer(buf + index, BUFFER_SIZE - index),
                                [thisPtr, &context](const boost::system::error_code &ec, size_t sz) {
                                    if (!ec || ec == boost::asio::error::eof) {
                                        thisPtr->index += sz;

                                        if (thisPtr->index >= thisPtr->header_size)
                                            memcpy(&thisPtr->msg_size, thisPtr->buf, thisPtr->header_size);

                                        if (thisPtr->index < thisPtr->msg_size) {
                                            thisPtr->read(context);
                                            return;
                                        } else
                                            thisPtr->index = 0;

                                        thisPtr->msgProcessor = new MsgProcessor();
                                        thisPtr->msgProcessor->decode(thisPtr->buf + thisPtr->header_size);

                                        if (!thisPtr->msgProcessor->get_msg()) {
                                            //std::cerr << "Message not implemented" << std::endl;
                                            thisPtr->socket_.close();
                                            delete thisPtr->msgProcessor;
                                            return;
                                        }

                                        if (thisPtr->msgProcessor->get_type() == MessageType::SRV_MSG_ATTACH)
                                            thisPtr->header_size = 4;

                                        thisPtr->repoContextPool.getIOContext().post([thisPtr, &context]() {
                                            thisPtr->msgProcessor->process(context, [thisPtr,&context]{thisPtr->write(context);});
                                        });
                                    } else {
                                        std::cerr << "Error receiving data from client " << ec.message() << std::endl;
                                    }
                                });
    }

    void write(boost::asio::io_context &context) {
        auto thisPtr = shared_from_this();
        std::vector<boost::asio::const_buffer> buffer;
        msgProcessor->encode(buffer, buf);
        boost::asio::async_write(socket_, buffer, [thisPtr, &context](const boost::system::error_code &ec, size_t sz) {
            if (ec) {
                if (ec == boost::asio::error::eof) {
                    thisPtr->socket_.close();
                }
                std::cerr << "Failed to send response to client: " << ec.message() << std::endl;
            } else {
                delete thisPtr->msgProcessor;
                thisPtr->read(context);
            }
        });
    }

    boost::asio::ip::tcp::socket &socket() {
        return socket_;
    }

private:
    Connection(boost::asio::io_context &ioContext, IOContextPool &_repoContextPool) : socket_(ioContext),
                                                                                            repoContextPool(_repoContextPool) {
        header_size = 2;
        index = 0;
        msg_size = 0;
        buf = new char[BUFFER_SIZE];
    }

    int index;
    size_t msg_size;
    boost::asio::ip::tcp::socket socket_;
    char *buf;
    size_t header_size;
    MsgProcessor *msgProcessor;
    IOContextPool repoContextPool;
};

#endif //MIDDLEWARE_CONNECTION_H
