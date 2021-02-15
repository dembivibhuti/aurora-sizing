//
// Created by rahul on 20/01/21.
//
#include <gtest/gtest.h>
#include "attach.h"
#include "message.h"
/*
TEST(Protocol, AttachRequestEcodeDecode) {
    AttachCodec codec;

    AttachRequest request;
    request.user_name = "userName";
    request.app_name = "appName";
    request.version = 1;
    request.revision = 2;

    char *attach_req_stream = codec.encodeRequest(request);

    AttachRequest *conv_request = codec.decodeRequest(attach_req_stream);
    ASSERT_EQ(request.app_name, conv_request->app_name);
    ASSERT_EQ(request.user_name, conv_request->user_name);
    ASSERT_EQ(request.version, conv_request->version);
    ASSERT_EQ(request.revision, conv_request->revision);

    delete[] attach_req_stream;
    delete conv_request;
}

TEST(Protocol, AttachResponseEcodeDecode) {
    AttachCodec codec;

    int server_features = 1;
    server_features |= 0x00000008;
    server_features |= 0x00008000;
    server_features |= 0x00200000;
    server_features |= 0x00010000;

    AttachResponse response(269, server_features);

    char *attach_res_stream = codec.encodeResponse(response);

    AttachResponse *conv_response = codec.decodeResponse(attach_res_stream + sizeof(short));
    ASSERT_EQ(response.server_features, conv_response->server_features);
    ASSERT_EQ(response.version_revision, conv_response->version_revision);

    delete[] attach_res_stream;
    delete conv_response;
}

int main(int argc, char **argv) {
    testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
 */

