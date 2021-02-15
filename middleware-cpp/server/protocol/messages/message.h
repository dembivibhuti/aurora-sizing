//
// Created by rahul on 22/01/21.
//

#ifndef MIDDLEWARE_MESSAGE_H
#define MIDDLEWARE_MESSAGE_H

enum MessageType {
    SRV_MSG_UNDEFINED = 0,
    SRV_MSG_ATTACH = 1,
    SRV_MSG_GET_BY_NAME = 4,
    SRV_MSG_NAME_LOOKUP = 11
};

class Message {
public:
    virtual void decode(char *data) = 0;

    virtual size_t encode(char *data_) = 0;

    virtual void process() = 0;

    virtual ~Message() {
    }

    std::size_t get_size() {
        return size;
    }

    std::size_t size;
};

#endif //MIDDLEWARE_MESSAGE_H
