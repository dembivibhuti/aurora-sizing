//
// Created by rahul on 01/02/21.
//

#ifndef CLIENT_MESSAGE_H
#define CLIENT_MESSAGE_H

#include <vector>
#include <boost/asio.hpp>

enum MessageType {
    SRV_MSG_UNDEFINED = 0,
    SRV_MSG_ATTACH = 1,
    SRV_MSG_GET_BY_NAME = 4,
    SRV_MSG_NAME_LOOKUP = 11
};

class Request {
public:
    virtual char* encode() = 0;

    virtual int get_size() = 0;

    virtual ~Request() {
    }
};

class Response {
public:
    virtual void decode(char *data) = 0;

    virtual ~Response() {
    }
};

#endif //CLIENT_MESSAGE_H
