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
#include "metrics.h"
#include <prometheus/counter.h>
#include <prometheus/exposer.h>
#include <prometheus/registry.h>


typedef boost::shared_ptr<Connection> ConnectionPTR;

class Server {
public:
    Server(int ioContextPoolSize, int ioThreadPerContext, int dbContextPoolSize, int dbThreadPerContext, int port)
            : ioContext(ioContext),
              contextPool(ioContextPoolSize, ioThreadPerContext),
              repoContextPool(dbContextPoolSize, dbThreadPerContext),
              acceptor(contextPool.getIOContext()) {

        // create an http server running on port 8080
        static prometheus::Exposer exposer{boost::asio::ip::host_name() + ":8080"};
        //static prometheus::Exposer exposer{"127.0.0.1:8181"};
        // create a metrics registry
        // @note it's the users responsibility to keep the object alive
        static auto registry = std::make_shared<prometheus::Registry>();

        static auto &msgRead = prometheus::BuildGauge()
                .Name("msg_read")
                .Register(*registry);

        static auto &msgDecoded = prometheus::BuildGauge()
                .Name("msg_decoded")
                .Register(*registry);

        static auto &msgProcessed = prometheus::BuildGauge()
                .Name("msg_processed")
                .Register(*registry);

        static auto &msgEncoded = prometheus::BuildGauge()
                .Name("msg_encoded")
                .Register(*registry);

        static auto &getByNameTotal = prometheus::BuildGauge()
                .Name("get_object_total")
                .Register(*registry);

        static auto &getByNameDecode = prometheus::BuildGauge()
                .Name("get_object_decode")
                .Register(*registry);
        static auto &getByNameProcess = prometheus::BuildGauge()
                .Name("get_object_process")
                .Register(*registry);
        static auto &getByNameEncode = prometheus::BuildGauge()
                .Name("get_object_encode")
                .Register(*registry);

        static auto &lookupDecode = prometheus::BuildGauge()
                .Name("lookup_decode")
                .Register(*registry);
        static auto &lookupProcess = prometheus::BuildGauge()
                .Name("lookup_process")
                .Register(*registry);
        static auto &lookupEncode = prometheus::BuildGauge()
                .Name("lookup_encode")
                .Register(*registry);

        static auto &getConnection = prometheus::BuildGauge()
                .Name("get_connection")
                .Register(*registry);

        static auto &execQuery = prometheus::BuildGauge()
                .Name("exec_query")
                .Register(*registry);
        exposer.RegisterCollectable(registry);

        static Gauges gauges{msgRead,msgDecoded,msgProcessed,msgEncoded,getByNameTotal,getByNameDecode, getByNameProcess, getByNameEncode, lookupDecode, lookupProcess,
                             lookupEncode, getConnection, execQuery};

        boost::asio::ip::tcp::endpoint endpoint(boost::asio::ip::tcp::v4(), port);
        acceptor.open(endpoint.protocol());
        acceptor.set_option(boost::asio::ip::tcp::acceptor::reuse_address(true));
        acceptor.bind(endpoint);
        acceptor.listen();
        eventLoop(gauges);
    }


    void eventLoop(Gauges &gauges) {
        boost::asio::io_context &context = contextPool.getIOContext();
        static int counter = 0;
        Gauge *gauge = gauges.create(++counter);
        ConnectionPTR connection = Connection::create(context, repoContextPool, gauge);
        acceptor.async_accept(connection->socket(),
                              [this, connection, &context, &gauges](const boost::system::error_code &ec) {
                                  if (ec) {
                                      std::cerr << "Failed to accept connection: " << ec.message() << std::endl;
                                  } else {
                                      connection->socket().set_option(boost::asio::ip::tcp::no_delay(true));
                                      connection->read(context);
                                      eventLoop(gauges);
                                  }
                              });
        //connections.push_back(connection);
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
    std::vector<ConnectionPTR> connections;
};

#endif //MIDDLEWARE_SECSERV_H
