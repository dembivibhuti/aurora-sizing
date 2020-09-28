package org.anonymous.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.SecSrvSvcGrpc;
import org.anonymous.grpc.SrvMsgGetManyByName;
import org.anonymous.grpc.SrvMsgGetManyByNameResponse;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

public class SecSrvSvcImpl extends SecSrvSvcGrpc.SecSrvSvcImplBase {
    @Override
    public void processMsgGetManyByName(SrvMsgGetManyByName request, StreamObserver<SrvMsgGetManyByNameResponse> responseObserver) {
        System.out.println(request);
        int reqSecCount = request.getSecurityCount();
        ProtocolStringList securityNameList = request.getSecurityNameList();

        String sql = String.format(
                "select * from objects where name in (%s)",
                String.join(",", Collections.nCopies(securityNameList.size(), "?")));

        SrvMsgGetManyByNameResponse response = SrvMsgGetManyByNameResponse.getDefaultInstance();
        System.out.println(sql);
        try(ConnectionProvider connectionProvider = new ConnectionProvider()){
            System.out.println("connection established");
            new ObjectRepository(null, connectionProvider).runDDL(false);
            new ObjectRepository(null, connectionProvider).load(1, 1 , new TimeKeeper()).join();

            try (Connection connection = connectionProvider.getConnection();
                 PreparedStatement getManyStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < securityNameList.toArray().length; i++) {
                    getManyStmt.setString(i + 1, securityNameList.get(i));
                }
                System.out.println(getManyStmt.toString());
                ResultSet rs = getManyStmt.executeQuery();
                connection.commit();
                SrvMsgGetManyByNameResponse.Builder responseBuilder = SrvMsgGetManyByNameResponse.newBuilder();
                int totalRows = 0;
                while(rs.next()){
                    totalRows++;
                    SrvMsgGetManyByNameResponse.RequestResponse.MsgOnSuccess msgOnSuccess = SrvMsgGetManyByNameResponse.RequestResponse.MsgOnSuccess.newBuilder().setMem(ByteString.copyFrom(rs.getBytes("mem"))).build();
                    SrvMsgGetManyByNameResponse.RequestResponse requestResponse = SrvMsgGetManyByNameResponse.RequestResponse.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                    responseBuilder.addRequestResponse(requestResponse);
                }
                System.out.println("tr: " + totalRows);
                responseBuilder.setSecurityCount(totalRows);
                response = responseBuilder.build();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }



//        SrvMsgGetManyByNameResponse response = SrvMsgGetManyByNameResponse.newBuilder().setSecurityCount(5).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
