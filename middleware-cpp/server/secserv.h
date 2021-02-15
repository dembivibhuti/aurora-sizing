//
// Created by rahul on 20/01/21.
//

#ifndef MIDDLEWARE_SECSERV_H
#define MIDDLEWARE_SECSERV_H

#include <boost/asio.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/make_shared.hpp>
#include <iostream>
#include "io_context_pool.h"
#include "connection.h"

typedef boost::shared_ptr<Connection> ConnectionPTR;

class Server {
public:
    Server(int ioContextPoolSize, int ioThreadPerContext,int dbContextPoolSize, int dbThreadPerContext, int port) : ioContext(ioContext),
                                                             contextPool(ioContextPoolSize, ioThreadPerContext),
                                                             repoContextPool(dbContextPoolSize, dbThreadPerContext),
                                                             acceptor(contextPool.getIOContext()) {
        boost::asio::ip::tcp::endpoint endpoint(boost::asio::ip::tcp::v4(), port);
        acceptor.open(endpoint.protocol());
        acceptor.set_option(boost::asio::ip::tcp::acceptor::reuse_address(true));
        acceptor.bind(endpoint);
        acceptor.listen();
        eventLoop();
    }

    void eventLoop() {
        boost::asio::io_context &context = contextPool.getIOContext();
        ConnectionPTR connection = Connection::create(context, repoContextPool);
        acceptor.async_accept(connection->socket(), [this, connection, &context](const boost::system::error_code &ec) {
            if (ec) {
                std::cerr << "Failed to accept connection: " << ec.message() << std::endl;
            } else {
                //connection->socket().set_option(boost::asio::ip::tcp::no_delay(true));
                connection->read(context);
                eventLoop();
            }
        });
    }

    void run() {
        std::vector<std::future<void>> futures;
        auto ioFut = std::async([this] {
            this->contextPool.run();
        });
        auto repoFut = std::async([this] {
            this->repoContextPool.run();
        });
        futures.push_back(std::move(ioFut));
        futures.push_back(std::move(repoFut));

        std::for_each(futures.begin(), futures.end(), [](std::future<void> &fut) {
            fut.wait();
        });
    }

private:
    IOContextPool contextPool;
    IOContextPool repoContextPool;
    boost::asio::ip::tcp::acceptor acceptor;
    boost::asio::io_context &ioContext;
};

#endif //MIDDLEWARE_SECSERV_H
