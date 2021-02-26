//
// Created by rahul on 22/01/21.
//

#ifndef MIDDLEWARE_MESSAGE_H
#define MIDDLEWARE_MESSAGE_H

#include "../../metrics.h"


enum MessageType {
    SRV_MSG_UNDEFINED = 0,
    SRV_MSG_ATTACH = 1,
    SRV_MSG_GET_BY_NAME = 4,
    SRV_MSG_NAME_LOOKUP = 11
};

class MsgProcessor;

class Connection : public boost::enable_shared_from_this<Connection> {
public:
    typedef boost::shared_ptr<Connection> Pointer;

    static Pointer create(boost::asio::io_context &ioContext, IOContextPool &repoContextPool, Gauge *gauge) {
        return Pointer(new Connection(ioContext, repoContextPool, gauge));
    }

    void read(boost::asio::io_context &ioContext);


    void write(boost::asio::io_context &ioContext);

    boost::asio::ip::tcp::socket &socket();

public:
    virtual ~Connection() {
        delete msgProcessor;
        delete[]buf;
    }

    Connection(boost::asio::io_context &ioContext_, IOContextPool &_repoContextPool, Gauge *gauge);

    boost::asio::io_context &ioContext;
    MsgProcessor *msgProcessor;
private:
    int index;
    size_t msg_size;
    boost::asio::ip::tcp::socket socket_;
    char *buf;
    size_t header_size;

    IOContextPool repoContextPool;

    Gauge *gauge;
    std::chrono::steady_clock::time_point start;


};

class Message {
public:
    virtual void decode(char *data, Gauge *gauge) = 0;

    virtual size_t encode(char *data_, Gauge *gauge) = 0;

    virtual void process(
            boost::asio::io_context &dbContext,
            boost::shared_ptr<Connection> connPtr) = 0;

    virtual ~Message() {
    }

    std::size_t get_size() {
        return size;
    }

    std::size_t size;
};

#endif //MIDDLEWARE_MESSAGE_H
