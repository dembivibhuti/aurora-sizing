//
// Created by rahul on 19/01/21.
//

#ifndef MIDDLEWARE_ATTACH_H
#define MIDDLEWARE_ATTACH_H

#include <iostream>
#include <cstring>
#include <vector>
#include <boost/asio.hpp>
#include "message.h"

static const size_t APP_NAME_SIZE = 32;
static const size_t USER_NAME_SIZE = 32;

class AttachResponse {
public:
    AttachResponse(short versionRevision, int serverFeatures) : version_revision(versionRevision),
                                                                server_features(serverFeatures) {
        size = sizeof(short) + sizeof(short) + sizeof(int);
    }

    AttachResponse() = default;

    ~AttachResponse() = default;

    short size;
    short version_revision = 269;
    int server_features;
};

class AttachRequest {
public:
    AttachRequest() = default;

    ~AttachRequest() = default;

    std::string user_name;
    std::string app_name;
    short version;
    short revision;
};

class SrvAttach : public Message {
public:
    SrvAttach() {
        request = new AttachRequest();
    }

    ~SrvAttach() {
        delete request;
        if (response) {
            delete response;
        }
    }

    void decode(char *data, Gauge *gauge) {
        int str_length = (char *) memchr(data, 0, USER_NAME_SIZE) - data;
        request->user_name = std::string(data, data + str_length);
        data += str_length + 1;

        str_length = (char *) memchr(data, 0, APP_NAME_SIZE) - data;
        request->app_name = std::string(data, data + str_length);
        data += str_length + 1;

        memcpy(&(request->version), data, sizeof(request->version));
        data += sizeof(request->version);

        memcpy(&(request->revision), data, sizeof(request->revision));
    }

    void process(Gauge *gauge) {
        int server_features = 1;
        server_features |= 0x00000008;
        server_features |= 0x00008000;
        server_features |= 0x00200000;
        server_features |= 0x00010000;

        response = new AttachResponse(269, server_features);
    }

    size_t encode(char *data_,Gauge *gauge) {
        int index = 0;
        memcpy(data_, &response->size, sizeof(short));
        index += sizeof(short);
        memcpy(data_ + index, &response->version_revision, sizeof(response->version_revision));
        index += sizeof(response->version_revision);
        memcpy(data_ + index, &(response->server_features), sizeof(response->server_features));
        return response->size;
    }

private:
    AttachRequest *request;
    AttachResponse *response = nullptr;
};

#endif //MIDDLEWARE_ATTACH_H
