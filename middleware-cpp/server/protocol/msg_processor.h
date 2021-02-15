//
// Created by rahul on 28/01/21.
//

#ifndef MIDDLEWARE_MSG_PROCESSOR_H
#define MIDDLEWARE_MSG_PROCESSOR_H

#include "messages/attach.h"
#include "messages/getbyname.h"
#include "messages/namelookup.h"
#include "messages/message.h"


class MsgProcessor {
public:
    MsgProcessor() {
        type = MessageType::SRV_MSG_UNDEFINED;
    }

    Message *get_msg() const {
        return msg;
    }

    MessageType get_type() const {
        return type;
    }

    void decode(char *data) {
        msg = nullptr;
        std::memcpy(&type, data, sizeof(short));
        switch (type) {
            case MessageType::SRV_MSG_ATTACH: {
                msg = new SrvAttach();
                break;
            }
            case MessageType::SRV_MSG_GET_BY_NAME: {
                msg = new SrvMsgGetByName();
                break;
            }
            case MessageType::SRV_MSG_NAME_LOOKUP: {
                msg = new SRVMsgNameLookup();
                break;
            }
        }
        if (msg) {
            msg->decode(data + 2);
        }

    }

    template<typename Functor>
    void process(boost::asio::io_context &nw_context, Functor callback) {
        msg->process();
        //std::cout << "Done with async work" << std::endl;
        callback();
        //nw_context.post(callback);
    }

    void encode(std::vector<boost::asio::const_buffer> &buffer, char *data_) {
        msg->encode(buffer, data_);
    }

    virtual ~MsgProcessor() {
        delete msg;
    }

private:
    size_t size;
    MessageType type;
    Message *msg;
};

#endif //MIDDLEWARE_MSG_PROCESSOR_H
