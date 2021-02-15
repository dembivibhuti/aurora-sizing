//
// Created by rahul on 01/02/21.
//

#ifndef CLIENT_CLIENT_H
#define CLIENT_CLIENT_H

#include "messages/attach.h"
#include "messages/getbyname.h"
#include <boost/asio.hpp>
#include "messages/message.h"
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>
#include <boost/serialization/serialization.hpp>
#include <thread>
#include <chrono>

namespace asio = boost::asio;
#define BUFFER_SIZE 32768

class Client {
public:
    Client(const char *host, const char *port) {
        host_ = host;
        port_ = port;
        asio::io_service service;
        asio::ip::tcp::resolver resolver(service);
        try {
            asio::ip::tcp::resolver::query query(asio::ip::tcp::v4(), host_, port_);
            asio::ip::tcp::resolver::iterator end,
                    iter = resolver.resolve(query);

            asio::ip::tcp::endpoint server(iter->endpoint());

            std::cout << "Connecting to " << server << std::endl;
            asio::ip::tcp::socket socket(service, asio::ip::tcp::v4());

            socket.connect(server);
            attach(socket);

            while (true) {
                get_by_name(socket, "148716-48003-29-3516184");
            }

        } catch (std::exception &e) {
            std::cerr << "Error reading response from server: " << e.what() << std::endl;
        }
    }


    void attach(asio::ip::tcp::socket &socket_) {
        AttachRequest a("Rahul", "secdb", 1, 2);
        auto buffer = asio::buffer(a.encode(), a.size + sizeof(size_t));
        auto sz1 = asio::write(socket_, buffer);
        memcpy(&sz1, buffer.data(), sizeof(sz1));

        boost::system::error_code ec;
        short response_size;
        short sz = asio::read(socket_, asio::buffer(&response_size, sizeof(response_size)), ec);
        char res_buf[response_size - sizeof(response_size)];
        sz = asio::read(socket_, asio::buffer(&res_buf, response_size - sizeof(response_size)), ec);
        if (!ec || ec == asio::error::eof) {
            AttachResponse res;
            res.decode(res_buf);
        } else {
            std::cerr << "Error reading response from server: " << ec.message() << std::endl;
        }
    }

    void get_by_name(asio::ip::tcp::socket &socket_, std::string sec_name) {
        GetByNameRequest request(sec_name);
        auto buffer = asio::buffer(request.encode(), request.size + sizeof(size_t));
        auto sz1 = asio::write(socket_, buffer);
        memcpy(&sz1, buffer.data(), sizeof(sz1));

        boost::system::error_code ec;
        int response_size;
        short sz = asio::read(socket_, asio::buffer(&response_size, sizeof(response_size)), ec);

        char res_buf[response_size - sizeof(response_size)];
        sz = asio::read(socket_, asio::buffer(&res_buf, response_size - sizeof(response_size)), ec);
        if (!ec || ec == asio::error::eof) {
            GetByNameResponse res;
            res.decode(res_buf);
        } else {
            std::cerr << "Error reading response from server: " << ec.message() << std::endl;
        }
    }


private:
    const char *host_;
    const char *port_;
    int counter = 0;
};


#endif //CLIENT_CLIENT_H
