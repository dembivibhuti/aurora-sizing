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

/*
class AttachCodec {
public:


    char *encodeRequest(AttachRequest &request) {
        char *data = new char[request.user_name.size() + 1 + request.app_name.size() + 1 + sizeof(request.version) +
                              sizeof(request.revision)];
        int index = 0;
        memcpy(data, request.user_name.c_str(), request.user_name.size() + 1);
        index += request.user_name.size() + 1;

        memcpy(data + index, request.app_name.c_str(), request.app_name.size() + 1);
        index += request.app_name.size() + 1;
        std::cout << request.version;
        memcpy(data + index, &request.version, sizeof(short));
        index += sizeof(short);

        memcpy(data + index, &request.revision, sizeof(short));
        return data;
    }

    char *encodeResponse(Response *res) {
        AttachResponse *response = static_cast<AttachResponse *>(res);
        char *data = new char[response->size];

        char *ptr = data;

        memcpy(ptr, &response->size, sizeof(short));
        ptr += sizeof(short);

        memcpy(ptr, &response->version_revision, sizeof(response->version_revision));
        ptr += sizeof(response->version_revision);

        memcpy(ptr, &response->server_features, sizeof(response.server_features));

        return data;
    }

    AttachResponse *decodeResponse(char *data) {
        AttachResponse *response = new AttachResponse;

        char *ptr = data;

        memcpy(&response->version_revision, ptr, sizeof(response->version_revision));
        ptr += sizeof(response->version_revision);

        memcpy(&response->server_features, ptr, sizeof(response->server_features));
        return response;
    }
};
 */

class SrvAttach : public Message {
public:
    SrvAttach() {
        request = new AttachRequest();
    }

    ~SrvAttach() {
        delete request;
        delete response;
    }

    void decode(char *data) {
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

    void process() {
        int server_features = 1;
        server_features |= 0x00000008;
        server_features |= 0x00008000;
        server_features |= 0x00200000;
        server_features |= 0x00010000;

        response = new AttachResponse(269, server_features);
    }

    size_t encode(char *data_) {
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
    AttachResponse *response;
};

#endif //MIDDLEWARE_ATTACH_H
