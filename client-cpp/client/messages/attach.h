//
// Created by rahul on 01/02/21.
//

#ifndef CLIENT_ATTACH_H
#define CLIENT_ATTACH_H

#include <string>
#include "message.h"

static const size_t APP_NAME_SIZE = 32;
static const size_t USER_NAME_SIZE = 32;

class AttachResponse : public Response {
public:
    AttachResponse(short versionRevision, int serverFeatures) : version_revision(versionRevision),
                                                                server_features(serverFeatures) {
        size = sizeof(short) + sizeof(short) + sizeof(int);
    }

    AttachResponse() {}

    ~AttachResponse() {}

    void decode(char *data) {
        int index = 0;

        //decode version revision
        memcpy(&version_revision, data + index, sizeof(version_revision));
        index += sizeof(version_revision);

        //decode server features
        memcpy(&server_features, data + index, sizeof(server_features));
        index += sizeof(server_features);


        std::cout << "**************************************" << std::endl;
        std::cout << "Attached" << std::endl;
        std::cout << "Server features " << server_features << std::endl;
        std::cout << "Version revision " << version_revision << std::endl;
        std::cout << "**************************************" << std::endl;
    }

    std::size_t size;
    short version_revision = 269;
    int server_features;
};

class AttachRequest : public Request{
public:
    AttachRequest() {}

    AttachRequest(const std::string &userName, const std::string &appName, short version, short revision) : user_name(
            userName), app_name(appName), version(version), revision(revision) {
        msgType = MessageType::SRV_MSG_ATTACH;
        size = user_name.size() + 1 + app_name.size() +
               1 + sizeof(version) +
               sizeof(revision) + sizeof(msgType) + sizeof(size);
    }

    ~AttachRequest() {}

    std::string user_name;
    std::string app_name;
    short version;
    short revision;
    MessageType msgType;
    short size;
    char *data;

    int get_size() {
        return size;
    }

    char* encode() {
        data = new char[size + sizeof(size)];
        int index = 0;
        memcpy(data, &size, sizeof(size));
        index += sizeof(size);

        memcpy(data + index, &msgType, sizeof(short));
        index += sizeof(short );

        memcpy(data + index, user_name.c_str(), user_name.size() + 1);
        index += user_name.size() + 1;
        memcpy(data + index, app_name.c_str(), app_name.size() + 1);
        index += app_name.size() + 1;
        memcpy(data + index, &version, sizeof(version));
        index += sizeof(version);
        memcpy(data + index, &revision, sizeof(revision));
        return data;
    }
};

#endif //CLIENT_ATTACH_H
