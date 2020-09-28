package org.anonymous.server;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import io.grpc.stub.StreamObserver;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjServiceImpl extends ObjServiceImplBase {

    private static ConnectionProvider roConnectionProvider;
    private static ConnectionProvider rwConnectionProvider;
    private static ObjectRepository objectRepositiory;
    private static TimeKeeper timekeeper;
    private static Logger LOGGER= LoggerFactory.getLogger(ObjServiceImpl.class);

    ObjServiceImpl(ConnectionProvider connectionProvider, ObjectRepository objectRepositiory, TimeKeeper timeKeeper){
        this.roConnectionProvider= connectionProvider;
        this.rwConnectionProvider= connectionProvider;
        this.objectRepositiory= objectRepositiory;
        this.timekeeper=timeKeeper;
    }
    @Override
    public void lookupByName(CmdLookupByName request, StreamObserver<CmdLookupByNameResponse> responseObserver) {
        System.out.println("reached lookup service"+ request);
        List<String> secKeys= new ArrayList<>();
        try(Connection connection = rwConnectionProvider.getConnection()) { // h2
            int limit= request.getCount();
            String prefix="";
            if(request.getSecurityNamePrefix()!= null) {
                prefix = request.getSecurityNamePrefix();
            }

            secKeys = objectRepositiory.lookup(prefix, limit, timekeeper);
            CmdLookupByNameResponse.Builder b= CmdLookupByNameResponse.newBuilder();
            for(String keys: secKeys){
                System.out.println(keys);
                b.addSecurityNames(keys);
            }
        }catch(Exception e){
            LOGGER.info("Caught Exception");
            e.printStackTrace();
        }
        CmdLookupByNameResponse response= CmdLookupByNameResponse.newBuilder().addAllSecurityNames((Iterable<String>) secKeys).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void lookupByNameStream(CmdLookupByName request, StreamObserver<CmdLookupByNameResponseStream> responseObserver) {
        //super.lookupByNameStream(request, responseObserver);
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
        System.out.println("reached lookup service"+ request);
        List<String> secKeys= new ArrayList<>();
        try(Connection connection = rwConnectionProvider.getConnection()) { // h2
            int limit= request.getCount();
            String prefix="";
            if(request.getSecurityNamePrefix()!= null) {
                prefix = request.getSecurityNamePrefix();
            }
            int typeid= request.getGetType().getNumber();
            secKeys = objectRepositiory.lookupById(prefix, typeid, limit, timekeeper);
            CmdNameLookupByTypeResponse.Builder b= CmdNameLookupByTypeResponse.newBuilder();
            for(String keys: secKeys){
                System.out.println(keys);
                b.addSecurityNames(keys);
            }
        }catch(Exception e){
            LOGGER.info("Caught Exception");
            e.printStackTrace();
        }
        CmdNameLookupByTypeResponse response= CmdNameLookupByTypeResponse.newBuilder().addAllSecurityNames((Iterable<String>) secKeys).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
       // super.lookupByTypeStream(request, responseObserver);
    }
}