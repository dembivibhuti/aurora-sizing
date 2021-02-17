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
#include "metrics.h"
#include <chrono>

#define BUFFER_SIZE 32768

class Connection : public boost::enable_shared_from_this<Connection> {
public:
    typedef boost::shared_ptr<Connection> Pointer;

    static Pointer create(boost::asio::io_context &ioContext, IOContextPool &repoContextPool, Gauge *gauge) {
        return Pointer(new Connection(ioContext, repoContextPool, gauge));
    }

    void read(boost::asio::io_context &ioContext) {
        auto thisPtr = shared_from_this();
        socket_.async_read_some(boost::asio::buffer(buf + index, BUFFER_SIZE - index),
                                [thisPtr, &ioContext](const boost::system::error_code &ec, size_t sz) {
                                    thisPtr->start = std::chrono::steady_clock::now();
                                    if (!ec || ec == boost::asio::error::eof) {
                                        thisPtr->index += sz;

                                        if (thisPtr->index >= thisPtr->header_size)
                                            memcpy(&thisPtr->msg_size, thisPtr->buf, thisPtr->header_size);

                                        if (thisPtr->index < thisPtr->msg_size) {
                                            thisPtr->read(ioContext);
                                            return;
                                        } else
                                            thisPtr->index = 0;

                                        auto end = std::chrono::steady_clock::now();
                                        long count = std::chrono::duration_cast<std::chrono::microseconds>(end - thisPtr->start).count();
                                        thisPtr->gauge->msgRead.Set(count);

                                        thisPtr->msgProcessor->decode(thisPtr->buf + thisPtr->header_size, thisPtr->gauge);

                                        end = std::chrono::steady_clock::now();
                                        count = std::chrono::duration_cast<std::chrono::microseconds>(end - thisPtr->start).count();
                                        thisPtr->gauge->msgDecoded.Set(count);

                                        if (!thisPtr->msgProcessor->get_msg()) {
                                            //std::cerr << "Message not implemented" << std::endl;
                                            thisPtr->socket_.close();
                                            return;
                                        }

                                        if (thisPtr->msgProcessor->get_type() == MessageType::SRV_MSG_ATTACH)
                                            thisPtr->header_size = 4;

                                        auto &dbContext = thisPtr->repoContextPool.getIOContext();
                                        dbContext.post([thisPtr, &ioContext]() {
                                            thisPtr->msgProcessor->process(thisPtr->gauge);
                                            auto end = std::chrono::steady_clock::now();
                                            long count = std::chrono::duration_cast<std::chrono::microseconds>(end - thisPtr->start).count();
                                            thisPtr->gauge->msgProcessed.Set(count);
                                            ioContext.post([thisPtr, &ioContext]() {
                                                thisPtr->write(ioContext);
                                            });
                                        });
                                    } else {
                                        std::cerr << "Error receiving data from client " << ec.message() << std::endl;
                                    }
                                });
    }


    void write(boost::asio::io_context &ioContext) {
        auto thisPtr = shared_from_this();
        size_t bytes = msgProcessor->encode(buf, gauge);
        auto end = std::chrono::steady_clock::now();
        long count = std::chrono::duration_cast<std::chrono::microseconds>(end - thisPtr->start).count();
        thisPtr->gauge->msgEncoded.Set(count);
        boost::asio::async_write(socket_, boost::asio::buffer(buf, bytes),
                                 [thisPtr, &ioContext](const boost::system::error_code &ec, size_t sz) {
                                     if (ec) {
                                         if (ec == boost::asio::error::eof) {
                                             thisPtr->socket_.close();
                                         }
                                         std::cerr << "Failed to send response to client: " << ec.message()
                                                   << std::endl;
                                     } else {
                                         auto end = std::chrono::steady_clock::now();
                                         const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - thisPtr->start).count();
                                         thisPtr->gauge->getByNameTotal.Set(count);
                                         thisPtr->read(ioContext);
                                     }
                                 });
    }

    boost::asio::ip::tcp::socket &socket() {
        return socket_;
    }

private:
    Connection(boost::asio::io_context &ioContext, IOContextPool &_repoContextPool, Gauge *gauge) :
            socket_(ioContext),
            repoContextPool(_repoContextPool),
            gauge(gauge) {
        header_size = 2;
        index = 0;
        msg_size = 0;
        buf = new char[BUFFER_SIZE]();
        msgProcessor = new MsgProcessor();
    }

public:
    virtual ~Connection() {
        delete msgProcessor;
        delete[]buf;
    }

private:

    int index;
    size_t msg_size;
    boost::asio::ip::tcp::socket socket_;
    char *buf;
    size_t header_size;
    MsgProcessor *msgProcessor;
    IOContextPool repoContextPool;
    Gauge *gauge;
    std::chrono::steady_clock::time_point start;
};

#endif //MIDDLEWARE_CONNECTION_H
