package org.anonymous.server;

import org.anonymous.grpc.TransactionServiceGrpc;
import org.anonymous.module.ObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionServiceImpl extends TransactionServiceGrpc.TransactionServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepository;

    TransactionServiceImpl(ObjectRepository objectRepositiory) {
        TransactionServiceImpl.objectRepository = objectRepositiory;
    }
}
