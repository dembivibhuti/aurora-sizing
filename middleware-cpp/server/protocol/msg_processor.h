//
// Created by rahul on 28/01/21.
//

#ifndef MIDDLEWARE_MSG_PROCESSOR_H
#define MIDDLEWARE_MSG_PROCESSOR_H

#include "messages/attach.h"
#include "messages/getbyname.h"
#include "messages/namelookup.h"
#include "messages/message.h"
#include "../connection.h"
#include "../metrics.h"


class MsgProcessor {
public:
    MsgProcessor() {
        type = MessageType::SRV_MSG_UNDEFINED;
        attachMsg = new SrvAttach();
        getByNameMsg = new SrvMsgGetByName();
        lookupMsg = new SRVMsgNameLookup();
    }

    Message *get_msg() const {
        return msg;
    }

    MessageType get_type() const {
        return type;
    }

    void decode(char *data, Gauge *gauge) {
        msg = nullptr;
        std::memcpy(&type, data, sizeof(short));
        switch (type) {
            case MessageType::SRV_MSG_ATTACH: {
                msg = attachMsg;
                break;
            }
            case MessageType::SRV_MSG_GET_BY_NAME: {
                msg = getByNameMsg;
                break;
            }
            case MessageType::SRV_MSG_NAME_LOOKUP: {
                msg = lookupMsg;
                break;
            }
        }
        if (msg) {
            msg->decode(data + 2, gauge);
        }

    }

    void process(Gauge *gauge) {
        msg->process(gauge);
    }

    size_t encode(char *data_, Gauge *gauge) {
        return msg->encode(data_, gauge);
    }

    virtual ~MsgProcessor() {
        delete attachMsg;
        delete lookupMsg;
        delete getByNameMsg;
    }

private:
    size_t size;
    MessageType type;
    Message *msg;
    Message *attachMsg = nullptr;
    Message *lookupMsg = nullptr;
    Message *getByNameMsg = nullptr;
};

#endif //MIDDLEWARE_MSG_PROCESSOR_H
