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


void Connection::read(boost::asio::io_context &ioContext) {
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
                                    long count = std::chrono::duration_cast<std::chrono::microseconds>(
                                            end - thisPtr->start).count();
                                    thisPtr->gauge->msgRead.Set(count);

                                    thisPtr->msgProcessor->decode(thisPtr->buf + thisPtr->header_size,
                                                                  thisPtr->gauge);

                                    end = std::chrono::steady_clock::now();
                                    count = std::chrono::duration_cast<std::chrono::microseconds>(
                                            end - thisPtr->start).count();
                                    thisPtr->gauge->msgDecoded.Set(count);

                                    if (!thisPtr->msgProcessor->get_msg()) {
                                        thisPtr->socket_.close();
                                        return;
                                    }

                                    if (thisPtr->msgProcessor->get_type() == MessageType::SRV_MSG_ATTACH)
                                        thisPtr->header_size = 4;

                                    auto &dbContext = thisPtr->repoContextPool.getIOContext();
                                    thisPtr->msgProcessor->process(dbContext, thisPtr);
                                } else {
                                    std::cerr << "Error receiving data from client " << ec.message() << std::endl;
                                }
                            });
}


void Connection::write(boost::asio::io_context &ioContext) {
    auto thisPtr = shared_from_this();
    size_t bytes = thisPtr->msgProcessor->encode(buf, gauge);
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
                                     const long count = std::chrono::duration_cast<std::chrono::microseconds>(
                                             end - thisPtr->start).count();
                                     thisPtr->gauge->getByNameTotal.Set(count);
                                     thisPtr->read(ioContext);
                                 }
                             });
}

boost::asio::ip::tcp::socket &Connection::socket() {
    return socket_;
}


Connection::Connection(boost::asio::io_context &ioContext_, IOContextPool &_repoContextPool, Gauge *gauge) :
        socket_(ioContext_),
        ioContext(ioContext_),
        repoContextPool(_repoContextPool),
        gauge(gauge) {
    header_size = 2;
    index = 0;
    msg_size = 0;
    buf = new char[BUFFER_SIZE]();
    msgProcessor = new MsgProcessor();
}





#endif //MIDDLEWARE_CONNECTION_H
