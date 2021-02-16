//
// Created by rahul on 02/02/21.
//

#ifndef CLIENT_GETBYNAME_H
#define CLIENT_GETBYNAME_H

#include <string>
#include "message.h"
#include "security.h"

class GetByNameRequest : public Request {
public:
    GetByNameRequest(const std::string &securityName) : security_name(securityName) {
        msgType = MessageType::SRV_MSG_GET_BY_NAME;
        size = sizeof(msgType) + sizeof(size) + securityName.size() + 1;
    }

    char *encode() {
        data = new char[size];
        int index = 0;
        memcpy(data, &size, sizeof(size));
        index += sizeof(size);

        memcpy(data + index, &msgType, sizeof(short));
        index += sizeof(short);

        memcpy(data + index, security_name.c_str(), security_name.size() + 1);
        return data;
    }

    int get_size() {
        return size;
    }

    ~GetByNameRequest() {
        delete[] data;
    }

    std::string security_name;
    int size;
    MessageType msgType;
    char *data;
};

class GetByNameResponse : public Response {
public:
    static int const ERROR = 0;
    static int const SUCCESS = 1;

    GetByNameResponse() {
    }

    void decode(char *data_) {
        sec = new Security;
        memcpy(&status, data_, sizeof(status));
        data_ += sizeof(status);

        if (status == GetByNameResponse::ERROR) {
            memcpy(&error_type, data_, sizeof(error_type));
        } else {
            void *new_line_index = memchr(data_, 0, SEC_NAME_SIZE);
            int str_length = (char *) new_line_index - data_;
            sec->name = std::string(data_, data_ + str_length);
            data_ += str_length + 1;

            memcpy(&(sec->type), data_, sizeof(short));
            data_ += sizeof(short);

            memcpy(&(sec->lastTransactionId), data_, sizeof(int));
            data_ += sizeof(int);

            memcpy(&(sec->timeUpdated), data_, sizeof(int));
            data_ += sizeof(int);

            memcpy(&(sec->updateCount), data_, sizeof(int));
            data_ += sizeof(int);

            memcpy(&(sec->dateCreated), data_, sizeof(short));
            data_ += sizeof(short);

            memcpy(&(sec->dbIDUpdated), data_, sizeof(short));
            data_ += sizeof(short);

            memcpy(&(sec->versionInfo), data_, sizeof(short));
            data_ += sizeof(short);

            memcpy(&(sec->blobSize), data_, sizeof(int));
            data_ += sizeof(int);
            sec->blob = new char[sec->blobSize];
            memcpy(sec->blob, data_, sec->blobSize);
        }

    }

    std::size_t size_;
    short status;
    short error_type;
    Security *sec;
};

#endif //CLIENT_GETBYNAME_H
