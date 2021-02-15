//
// Created by rahul on 01/02/21.
//

#ifndef MIDDLEWARE_GETBYNAME_H
#define MIDDLEWARE_GETBYNAME_H

#include <string>
#include <vector>
#include "security.h"
#include "message.h"
#include "../../db/repository.h"
#include <boost/asio.hpp>

#define RESPONSE_FIELD_SIZE 25

class GetByNameRequest {
public:
    std::string security_name;
};

class GetByNameResponse {
public:
    static int const ERROR = 0;
    static int const SUCCESS = 1;

    GetByNameResponse(short status, short errorType, Security *sec) : status_(status), error_type(errorType), sec(sec) {
        size_ = sizeof(int) + sizeof(short);
        if (status == ERROR)
            size_ += sizeof(short);
        else
            size_ += sec->name.size() + RESPONSE_FIELD_SIZE;
    }

    std::size_t size_;
    short status_;
    short error_type;
    Security *sec;
};

class SrvMsgGetByName : public Message {
public:
    SrvMsgGetByName() {
        request = new GetByNameRequest();
    }

    ~SrvMsgGetByName() {
        delete request;
        delete response;
    }

    void decode(char *data) {
        void *new_line_index = memchr(data, 0, SEC_NAME_SIZE);
        int str_length = (char *) new_line_index - data;
        request->security_name = std::string(data, data + str_length);
    }

    void process() {
        static Repository *pRepository = Repository::getInstance();
        Security *security = pRepository->get_security(request->security_name);
        if(response) {
            delete response;
        }
        response = new GetByNameResponse(GetByNameResponse::SUCCESS, 0, security);
    }

    size_t encode(char *data_) {
        char *start = data_;
        Security *sec = response->sec;
        size_t s = response->size_ + sec->blobSize;
        memcpy(data_, &s, sizeof(int)); //4
        data_ += sizeof(int);
        memcpy(data_, &(response->status_), sizeof(response->status_)); //4 + 2
        data_ += sizeof(response->status_);

        if (response->status_ == GetByNameResponse::ERROR) {
            memcpy(data_, &(response->error_type), sizeof(response->error_type));
        } else {
            int string_length = sec->name.size() + 1;
            memcpy(data_, sec->name.c_str(), string_length);
            data_ += string_length;

            memcpy(data_, &(sec->type), sizeof(short));
            data_ += sizeof(short);

            memcpy(data_, &(sec->lastTransactionId), sizeof(int));
            data_ += sizeof(int);

            memcpy(data_, &(sec->timeUpdated), sizeof(int));
            data_ += sizeof(int);

            memcpy(data_, &(sec->updateCount), sizeof(int));
            data_ += sizeof(int);

            memcpy(data_, &(sec->dateCreated), sizeof(short));
            data_ += sizeof(short);

            memcpy(data_, &(sec->dbIDUpdated), sizeof(short));
            data_ += sizeof(short);

            memcpy(data_, &(sec->versionInfo), sizeof(short));
            data_ += sizeof(short);

            memcpy(data_, &(sec->blobSize), sizeof(int));
            data_ += sizeof(int);

            sec->blob = new char[sec->blobSize];
            memcpy(data_, sec->blob, sec->blobSize);
        }
        size_t size =  response->size_ + sec->blobSize;
        delete sec;
        return size;
    }

private:
    GetByNameRequest *request = nullptr;
    GetByNameResponse *response = nullptr;
};

#endif //MIDDLEWARE_GETBYNAME_H
