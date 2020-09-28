package org.anonymous.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import io.grpc.stub.StreamObserver;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class ObjServiceImpl extends ObjServiceImplBase {
    ConnectionProvider connectionProvider;
    public ObjServiceImpl(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    public void connect(CmdConnect request, StreamObserver<CmdConnectResponse> responseObserver) {

        System.out.println("yo......");
    }

    public void getObjectManyByName(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponse> responseObserver) {
        System.out.println(request);
        ProtocolStringList securityNameList = request.getSecurityNameList();
        String sql = String.format(
                "select * from objects where name in (%s)",
                String.join(",", Collections.nCopies(securityNameList.size(), "?")));

        CmdGetManyByNameResponse response = CmdGetManyByNameResponse.getDefaultInstance();
        System.out.println(sql);

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement getManyStmt = connection.prepareStatement(sql)) {
            System.out.println("connection established");
            for (int i = 0; i < securityNameList.toArray().length; i++) {
                getManyStmt.setString(i + 1, securityNameList.get(i));
            }
            System.out.println(getManyStmt.toString());
            ResultSet rs = getManyStmt.executeQuery();
            connection.commit();
            CmdGetManyByNameResponse.Builder responseBuilder = CmdGetManyByNameResponse.newBuilder();
            int totalRows = 0;
            while(rs.next()){
                totalRows++;
                CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess.newBuilder().setMem(ByteString.copyFrom(rs.getBytes("mem"))).build();
                CmdGetManyByNameResponse.RequestResponse requestResponse = CmdGetManyByNameResponse.RequestResponse.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseBuilder.addRequestResponse(requestResponse);
            }
            System.out.println("tr: " + totalRows);
            responseBuilder.setSecurityCount(totalRows);
            response = responseBuilder.build();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}