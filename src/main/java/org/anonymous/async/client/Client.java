package org.anonymous.async.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.grpc.*;
import org.anonymous.server.RCachingTester;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080).usePlaintext()
                .build();

        ObjServiceGrpc.ObjServiceBlockingStub objSvcStub = ObjServiceGrpc.newBlockingStub(channel);
        CmdLookupByNameResponse response = objSvcStub.lookupByName(
                CmdLookupByName.newBuilder()
                        .setCount(100)
                        .setMessageType(CmdType.CMD_NAME_LOOKUP)
                        .setGetType(GetType.METADATA_GET_GREATER)
                        .setSecurityNamePrefix("1").build());
        System.out.println("Response received from lookupByName:\n" + response);

        CmdGetByNameExtResponse response1 = objSvcStub.getObjectExt(CmdGetByNameExt.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName(RCachingTester.SEC_KEY).build());
        System.out.println("Response received from getObjectByName: \n" + response1.getMsgOnSuccess().getMetadata().getSecurityName());

    }
}
