//
// Created by rahul on 04/02/21.
//

#ifndef PERFCLIENT_LOOKUP_H
#define PERFCLIENT_LOOKUP_H

#include <string>
#include <vector>
#include <boost/asio.hpp>

#include "message.h"
#include "security.h"

enum LookupType {
    SDB_GET_FIRST = 1,
    SDB_GET_LAST,
    SDB_GET_EQUAL,
    SDB_GET_LESS,
    SDB_GET_LE,
    SDB_GET_GREATER,
    SDB_GET_GE,
    SDB_GET_NEXT,
    SDB_GET_PREV
};

class NameLookupRequest : public Request {
public:

    NameLookupRequest(LookupType type, short count, const std::string &prefix) : type(type), count(count),
                                                                                 prefix(prefix) {
        msgType = MessageType::SRV_MSG_NAME_LOOKUP;
        size = sizeof(msgType) + sizeof(size) + sizeof(short) + sizeof(short) + prefix.size() + 1;
    }

    char *encode() {
        data = new char[size];
        int index = 0;
        memcpy(data, &size, sizeof(size));
        index += sizeof(size);

        memcpy(data + index, &msgType, sizeof(short));
        index += sizeof(short);

        memcpy(data, &type, sizeof(short));
        index += sizeof(short);

        memcpy(data + index, &count, sizeof(short));
        index += sizeof(short);

        memcpy(data + index, prefix.c_str(), prefix.size() + 1);

        return data;
    }

    int get_size() {
        return size;
    }

    int size;
    char *data;
    LookupType type;
    short count;
    std::string prefix;
    MessageType msgType;
};

class NameLookupResponse : public Response {
public:

    void decode(char *data) {
        int index = 0;
        memcpy(&count, data + index, sizeof(short));
        index += sizeof(short);
        data += index;
        security_names = new std::vector<std::string>();
        for (int i = 0; i < count; i++) {
            void *new_line_index = memchr(data, 0, SEC_NAME_SIZE);
            int str_length = (char *) new_line_index - data;
            std::string securityName(data, data + str_length);
            data += securityName.size() + 1;
            security_names->push_back(securityName);
        }
    }

    std::size_t size_;
    short count;
    std::vector<std::string> *security_names;
};


#endif //PERFCLIENT_LOOKUP_H
