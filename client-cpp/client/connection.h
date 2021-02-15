//
// Created by rahul on 03/02/21.
//

#ifndef CLIENT_CONNECTION_H
#define CLIENT_CONNECTION_H
#include "messages/message.h"
/*
class Connection  : public boost::enable_shared_from_this<Connection> {
public:
    typedef boost::shared_ptr<Connection> Pointer;

    static Pointer create(boost::asio::ip::tcp::socket &socket) {
        return Pointer(new Connection(socket));
    }

    void write(Request &request) {
        auto thisPtr = shared_from_this();
        std::vector<boost::asio::const_buffer> buffer;
        auto buffer = asio::buffer(request.encode(), request.get_size() + sizeof(size_t));
        boost::asio::async_write(socket_, buffer, [thisPtr](const boost::system::error_code &ec, size_t sz) {
            if (ec) {
                if (ec == boost::asio::error::eof) {
                    thisPtr->socket_.close();
                }
                std::cerr << "Failed to send response to client: " << ec.message() << std::endl;
            } else {
                thisPtr->read(context);
            }
        });
    }

    void read() {
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
                                            std::cerr << "Message not implemented" << std::endl;
                                            thisPtr->socket_.close();
                                            return;
                                        }

                                        if (thisPtr->msgProcessor->get_type() == MessageType::SRV_MSG_ATTACH)
                                            thisPtr->header_size = 4;

                                        //context.post([&thisPtr, &context]() {
                                        thisPtr->msgProcessor->process();
                                        thisPtr->write(context);
                                        //});
                                    } else {
                                        std::cerr << "Error receiving data from client " << ec.message() << std::endl;
                                    }
                                });
    }

private:
    Connection(boost::asio::ip::tcp::socket &socket)  : socket_(socket) {
            header_size = 2;
            index = 0;
            msg_size = 0;
            buf = new char[BUFFER_SIZE];
    }

    boost::asio::ip::tcp::socket &socket_;
    char *buf;
    size_t header_size;
}; */
#endif //CLIENT_CONNECTION_H
