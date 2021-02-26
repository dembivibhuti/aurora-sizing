//
// Created by rahul on 05/02/21.
//

#ifndef MIDDLEWARE_NAMELOOKUP_H
#define MIDDLEWARE_NAMELOOKUP_H

#include <string>
#include <vector>
#include <boost/asio.hpp>

#include "message.h"
#include "security.h"
#include "../../metrics.h"

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

class NameLookupRequest {
public:
    LookupType type;
    short count;
    std::string prefix;
};

class NameLookupResponse {
public:
    NameLookupResponse(short count, std::vector<std::string> *securityNames) : count(count),
                                                                               security_names(securityNames) {
        size_ = sizeof(int) + sizeof(short);
        for (auto &security : *security_names) {
            size_ += security.size() + 1;
        }
    }

    virtual ~NameLookupResponse() {
        delete security_names;
    }

    std::size_t size_;
    short count;
    std::vector<std::string> *security_names;
};

class SRVMsgNameLookup : public Message, public boost::enable_shared_from_this<SRVMsgNameLookup> {
public:
    SRVMsgNameLookup() {
        request = new NameLookupRequest();
    }

    ~SRVMsgNameLookup() {
        delete request;
        delete response;
    }

    void decode(char *data, Gauge *gauge) {
        auto start = std::chrono::steady_clock::now();

        int index = 0;
        memcpy(&(request->type), data, sizeof(short));
        index += sizeof(short);

        memcpy(&(request->count), data + index, sizeof(short));
        index += sizeof(short);

        void *new_line_index = memchr(data + index, 0, SEC_NAME_SIZE);
        int str_length = (char *) new_line_index - (data + index);
        request->prefix = std::string(data + index, data + index + str_length);

        auto end = std::chrono::steady_clock::now();
        const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
        gauge->lookupDecode.Set(count);
    }

    void process(boost::asio::io_context &dbContext,
                 boost::shared_ptr<Connection> connPtr) {
        static Repository *pRepository = Repository::getInstance();

        auto handler = [](void* ptr, boost::shared_ptr<Connection> connPtr, std::vector<std::string> *names) mutable {
            reinterpret_cast<SRVMsgNameLookup*>(ptr)->response = new NameLookupResponse(names->size(), names);

            connPtr->ioContext.post([connPtr] {
                connPtr->write(connPtr->ioContext);
            });
        };
        pRepository->lookup_async(dbContext, request->prefix, request->count, handler, connPtr, static_cast<void *>(this));
    }

    size_t encode(char *data_, Gauge *gauge) {
        auto start = std::chrono::steady_clock::now();

        int index = 0;
        size_t s = response->size_;

        memcpy(data_, &s, sizeof(int));
        index += sizeof(int);

        memcpy(data_ + index, &(response->count), sizeof(short));
        index += sizeof(short);

        for (auto &security : *response->security_names) {
            memcpy(data_ + index, security.c_str(), security.size() + 1);
            index += security.size() + 1;
        }
        auto end = std::chrono::steady_clock::now();
        const long count = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
        gauge->lookupEncode.Set(count);
        return s;
    }


private:
    NameLookupRequest *request;
    NameLookupResponse *response = nullptr;
};

#endif //MIDDLEWARE_NAMELOOKUP_H
