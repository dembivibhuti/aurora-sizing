//
// Created by rahul on 04/02/21.
//

#ifndef PERFCLIENT_CLIENT_H
#define PERFCLIENT_CLIENT_H

#include "messages/attach.h"
#include "messages/getbyname.h"
#include "messages/lookup.h"
#include "messages/security.h"
#include <boost/asio.hpp>

#include <vector>
#include <prometheus/counter.h>
#include <prometheus/exposer.h>
#include <prometheus/registry.h>


namespace asio = boost::asio;

class Client {
public:
    Client(const char *host, const char *port, asio::io_service &service) : host_(host), port_(port),
                                                                            socket(service, asio::ip::tcp::v4()) {
        asio::ip::tcp::resolver resolver(service);
        try {
            asio::ip::tcp::resolver::query query(asio::ip::tcp::v4(), host_, port_);
            asio::ip::tcp::resolver::iterator end,
                    iter = resolver.resolve(query);

            asio::ip::tcp::endpoint server(iter->endpoint());

            std::cout << "Connecting to " << server << std::endl;

            socket.connect(server);

        } catch (std::exception &e) {
            std::cerr << "Error reading response from server: " << e.what() << std::endl;
        }
    }

    AttachResponse *attach() {
        AttachRequest a("Rahul", "secdb", 1, 2);
        auto buffer = asio::buffer(a.encode(), a.size + sizeof(size_t));
        auto sz1 = asio::write(socket, buffer);
        memcpy(&sz1, buffer.data(), sizeof(sz1));

        boost::system::error_code ec;
        short response_size;
        short sz = asio::read(socket, asio::buffer(&response_size, sizeof(response_size)), ec);
        char res_buf[response_size - sizeof(response_size)];
        sz = asio::read(socket, asio::buffer(&res_buf, response_size - sizeof(response_size)), ec);
        AttachResponse *res = new AttachResponse();
        if (!ec || ec == asio::error::eof) {
            res->decode(res_buf);
        } else {
            std::cerr << "Error reading response from server: " << ec.message() << std::endl;
        }
        return res;
    }

    std::vector<std::string> *lookup(std::string prefix, short count) {
        NameLookupRequest request(LookupType::SDB_GET_GE, count, prefix);
        auto buffer = asio::buffer(request.encode(), request.size + sizeof(size_t));
        auto sz1 = asio::write(socket, buffer);
        memcpy(&sz1, buffer.data(), sizeof(sz1));

        boost::system::error_code ec;
        int response_size;
        short sz = asio::read(socket, asio::buffer(&response_size, sizeof(response_size)), ec);

        char res_buf[response_size - sizeof(response_size)];
        sz = asio::read(socket, asio::buffer(&res_buf, response_size - sizeof(response_size)), ec);
        NameLookupResponse res;
        if (!ec || ec == asio::error::eof) {
            res.decode(res_buf);
        } else {
            std::cerr << "Error reading response from server: " << ec.message() << std::endl;
        }
        return res.security_names;
    }

    Security *getSecurity(const std::string &sec_name) {
        GetByNameRequest request(sec_name);
        auto buffer = asio::buffer(request.encode(), request.size + sizeof(size_t));
        auto sz1 = asio::write(socket, buffer);
        memcpy(&sz1, buffer.data(), sizeof(sz1));

        boost::system::error_code ec;
        int response_size;
        short sz = asio::read(socket, asio::buffer(&response_size, sizeof(response_size)), ec);

        char res_buf[response_size - sizeof(response_size)];
        sz = asio::read(socket, asio::buffer(&res_buf, response_size - sizeof(response_size)), ec);
        GetByNameResponse res;
        if (!ec || ec == asio::error::eof) {
            res.decode(res_buf);
        } else {
            std::cerr << "Error reading response from server: " << ec.message() << std::endl;
        }
        return res.sec;
    }

private:
    const char *host_;
    const char *port_;
    asio::ip::tcp::socket socket;

};

#endif //PERFCLIENT_CLIENT_H
