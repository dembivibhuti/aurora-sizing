package org.anonymous.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.21.0)",
    comments = "Source: obj_svc.proto")
public final class ObjServiceGrpc {

  private ObjServiceGrpc() {}

  public static final String SERVICE_NAME = "org.anonymous.grpc.ObjService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnect,
      org.anonymous.grpc.CmdConnectResponse> getConnectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "connect",
      requestType = org.anonymous.grpc.CmdConnect.class,
      responseType = org.anonymous.grpc.CmdConnectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnect,
      org.anonymous.grpc.CmdConnectResponse> getConnectMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnect, org.anonymous.grpc.CmdConnectResponse> getConnectMethod;
    if ((getConnectMethod = ObjServiceGrpc.getConnectMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getConnectMethod = ObjServiceGrpc.getConnectMethod) == null) {
          ObjServiceGrpc.getConnectMethod = getConnectMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdConnect, org.anonymous.grpc.CmdConnectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "connect"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdConnect.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdConnectResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("connect"))
                  .build();
          }
        }
     }
     return getConnectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnectExt,
      org.anonymous.grpc.CmdConnectExtResponse> getConnectExtMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "connect_ext",
      requestType = org.anonymous.grpc.CmdConnectExt.class,
      responseType = org.anonymous.grpc.CmdConnectExtResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnectExt,
      org.anonymous.grpc.CmdConnectExtResponse> getConnectExtMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdConnectExt, org.anonymous.grpc.CmdConnectExtResponse> getConnectExtMethod;
    if ((getConnectExtMethod = ObjServiceGrpc.getConnectExtMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getConnectExtMethod = ObjServiceGrpc.getConnectExtMethod) == null) {
          ObjServiceGrpc.getConnectExtMethod = getConnectExtMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdConnectExt, org.anonymous.grpc.CmdConnectExtResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "connect_ext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdConnectExt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdConnectExtResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("connect_ext"))
                  .build();
          }
        }
     }
     return getConnectExtMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName,
      org.anonymous.grpc.CmdLookupByNameResponse> getLookupByNameMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "lookup_by_name",
      requestType = org.anonymous.grpc.CmdLookupByName.class,
      responseType = org.anonymous.grpc.CmdLookupByNameResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName,
      org.anonymous.grpc.CmdLookupByNameResponse> getLookupByNameMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName, org.anonymous.grpc.CmdLookupByNameResponse> getLookupByNameMethod;
    if ((getLookupByNameMethod = ObjServiceGrpc.getLookupByNameMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getLookupByNameMethod = ObjServiceGrpc.getLookupByNameMethod) == null) {
          ObjServiceGrpc.getLookupByNameMethod = getLookupByNameMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdLookupByName, org.anonymous.grpc.CmdLookupByNameResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "lookup_by_name"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdLookupByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdLookupByNameResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("lookup_by_name"))
                  .build();
          }
        }
     }
     return getLookupByNameMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName,
      org.anonymous.grpc.CmdLookupByNameResponseStream> getLookupByNameStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "lookup_by_name_stream",
      requestType = org.anonymous.grpc.CmdLookupByName.class,
      responseType = org.anonymous.grpc.CmdLookupByNameResponseStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName,
      org.anonymous.grpc.CmdLookupByNameResponseStream> getLookupByNameStreamMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdLookupByName, org.anonymous.grpc.CmdLookupByNameResponseStream> getLookupByNameStreamMethod;
    if ((getLookupByNameStreamMethod = ObjServiceGrpc.getLookupByNameStreamMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getLookupByNameStreamMethod = ObjServiceGrpc.getLookupByNameStreamMethod) == null) {
          ObjServiceGrpc.getLookupByNameStreamMethod = getLookupByNameStreamMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdLookupByName, org.anonymous.grpc.CmdLookupByNameResponseStream>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "lookup_by_name_stream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdLookupByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdLookupByNameResponseStream.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("lookup_by_name_stream"))
                  .build();
          }
        }
     }
     return getLookupByNameStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType,
      org.anonymous.grpc.CmdNameLookupByTypeResponse> getLookupByTypeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "lookup_by_type",
      requestType = org.anonymous.grpc.CmdNameLookupByType.class,
      responseType = org.anonymous.grpc.CmdNameLookupByTypeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType,
      org.anonymous.grpc.CmdNameLookupByTypeResponse> getLookupByTypeMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType, org.anonymous.grpc.CmdNameLookupByTypeResponse> getLookupByTypeMethod;
    if ((getLookupByTypeMethod = ObjServiceGrpc.getLookupByTypeMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getLookupByTypeMethod = ObjServiceGrpc.getLookupByTypeMethod) == null) {
          ObjServiceGrpc.getLookupByTypeMethod = getLookupByTypeMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdNameLookupByType, org.anonymous.grpc.CmdNameLookupByTypeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "lookup_by_type"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdNameLookupByType.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdNameLookupByTypeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("lookup_by_type"))
                  .build();
          }
        }
     }
     return getLookupByTypeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType,
      org.anonymous.grpc.CmdNameLookupByTypeResponseStream> getLookupByTypeStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "lookup_by_type_stream",
      requestType = org.anonymous.grpc.CmdNameLookupByType.class,
      responseType = org.anonymous.grpc.CmdNameLookupByTypeResponseStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType,
      org.anonymous.grpc.CmdNameLookupByTypeResponseStream> getLookupByTypeStreamMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdNameLookupByType, org.anonymous.grpc.CmdNameLookupByTypeResponseStream> getLookupByTypeStreamMethod;
    if ((getLookupByTypeStreamMethod = ObjServiceGrpc.getLookupByTypeStreamMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getLookupByTypeStreamMethod = ObjServiceGrpc.getLookupByTypeStreamMethod) == null) {
          ObjServiceGrpc.getLookupByTypeStreamMethod = getLookupByTypeStreamMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdNameLookupByType, org.anonymous.grpc.CmdNameLookupByTypeResponseStream>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "lookup_by_type_stream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdNameLookupByType.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdNameLookupByTypeResponseStream.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("lookup_by_type_stream"))
                  .build();
          }
        }
     }
     return getLookupByTypeStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByName,
      org.anonymous.grpc.CmdGetByNameResponse> getGetObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object",
      requestType = org.anonymous.grpc.CmdGetByName.class,
      responseType = org.anonymous.grpc.CmdGetByNameResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByName,
      org.anonymous.grpc.CmdGetByNameResponse> getGetObjectMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByName, org.anonymous.grpc.CmdGetByNameResponse> getGetObjectMethod;
    if ((getGetObjectMethod = ObjServiceGrpc.getGetObjectMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectMethod = ObjServiceGrpc.getGetObjectMethod) == null) {
          ObjServiceGrpc.getGetObjectMethod = getGetObjectMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetByName, org.anonymous.grpc.CmdGetByNameResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetByNameResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object"))
                  .build();
          }
        }
     }
     return getGetObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByNameExt,
      org.anonymous.grpc.CmdGetByNameExtResponse> getGetObjectExtMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object_ext",
      requestType = org.anonymous.grpc.CmdGetByNameExt.class,
      responseType = org.anonymous.grpc.CmdGetByNameExtResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByNameExt,
      org.anonymous.grpc.CmdGetByNameExtResponse> getGetObjectExtMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetByNameExt, org.anonymous.grpc.CmdGetByNameExtResponse> getGetObjectExtMethod;
    if ((getGetObjectExtMethod = ObjServiceGrpc.getGetObjectExtMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectExtMethod = ObjServiceGrpc.getGetObjectExtMethod) == null) {
          ObjServiceGrpc.getGetObjectExtMethod = getGetObjectExtMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetByNameExt, org.anonymous.grpc.CmdGetByNameExtResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object_ext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetByNameExt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetByNameExtResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object_ext"))
                  .build();
          }
        }
     }
     return getGetObjectExtMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName,
      org.anonymous.grpc.CmdGetManyByNameResponse> getGetObjectManyByNameMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object_many_by_name",
      requestType = org.anonymous.grpc.CmdGetManyByName.class,
      responseType = org.anonymous.grpc.CmdGetManyByNameResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName,
      org.anonymous.grpc.CmdGetManyByNameResponse> getGetObjectManyByNameMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName, org.anonymous.grpc.CmdGetManyByNameResponse> getGetObjectManyByNameMethod;
    if ((getGetObjectManyByNameMethod = ObjServiceGrpc.getGetObjectManyByNameMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectManyByNameMethod = ObjServiceGrpc.getGetObjectManyByNameMethod) == null) {
          ObjServiceGrpc.getGetObjectManyByNameMethod = getGetObjectManyByNameMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetManyByName, org.anonymous.grpc.CmdGetManyByNameResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object_many_by_name"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object_many_by_name"))
                  .build();
          }
        }
     }
     return getGetObjectManyByNameMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName,
      org.anonymous.grpc.CmdGetManyByNameResponseStream> getGetObjectManyByNameStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object_many_by_name_stream",
      requestType = org.anonymous.grpc.CmdGetManyByName.class,
      responseType = org.anonymous.grpc.CmdGetManyByNameResponseStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName,
      org.anonymous.grpc.CmdGetManyByNameResponseStream> getGetObjectManyByNameStreamMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByName, org.anonymous.grpc.CmdGetManyByNameResponseStream> getGetObjectManyByNameStreamMethod;
    if ((getGetObjectManyByNameStreamMethod = ObjServiceGrpc.getGetObjectManyByNameStreamMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectManyByNameStreamMethod = ObjServiceGrpc.getGetObjectManyByNameStreamMethod) == null) {
          ObjServiceGrpc.getGetObjectManyByNameStreamMethod = getGetObjectManyByNameStreamMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetManyByName, org.anonymous.grpc.CmdGetManyByNameResponseStream>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object_many_by_name_stream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByName.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameResponseStream.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object_many_by_name_stream"))
                  .build();
          }
        }
     }
     return getGetObjectManyByNameStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt,
      org.anonymous.grpc.CmdGetManyByNameExtResponse> getGetObjectManyByNameExtMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object_many_by_name_ext",
      requestType = org.anonymous.grpc.CmdGetManyByNameExt.class,
      responseType = org.anonymous.grpc.CmdGetManyByNameExtResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt,
      org.anonymous.grpc.CmdGetManyByNameExtResponse> getGetObjectManyByNameExtMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt, org.anonymous.grpc.CmdGetManyByNameExtResponse> getGetObjectManyByNameExtMethod;
    if ((getGetObjectManyByNameExtMethod = ObjServiceGrpc.getGetObjectManyByNameExtMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectManyByNameExtMethod = ObjServiceGrpc.getGetObjectManyByNameExtMethod) == null) {
          ObjServiceGrpc.getGetObjectManyByNameExtMethod = getGetObjectManyByNameExtMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetManyByNameExt, org.anonymous.grpc.CmdGetManyByNameExtResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object_many_by_name_ext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameExt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameExtResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object_many_by_name_ext"))
                  .build();
          }
        }
     }
     return getGetObjectManyByNameExtMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt,
      org.anonymous.grpc.CmdGetManyByNameExtResponseStream> getGetObjectManyByNameExtStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get_object_many_by_name_ext_stream",
      requestType = org.anonymous.grpc.CmdGetManyByNameExt.class,
      responseType = org.anonymous.grpc.CmdGetManyByNameExtResponseStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt,
      org.anonymous.grpc.CmdGetManyByNameExtResponseStream> getGetObjectManyByNameExtStreamMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdGetManyByNameExt, org.anonymous.grpc.CmdGetManyByNameExtResponseStream> getGetObjectManyByNameExtStreamMethod;
    if ((getGetObjectManyByNameExtStreamMethod = ObjServiceGrpc.getGetObjectManyByNameExtStreamMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getGetObjectManyByNameExtStreamMethod = ObjServiceGrpc.getGetObjectManyByNameExtStreamMethod) == null) {
          ObjServiceGrpc.getGetObjectManyByNameExtStreamMethod = getGetObjectManyByNameExtStreamMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdGetManyByNameExt, org.anonymous.grpc.CmdGetManyByNameExtResponseStream>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "get_object_many_by_name_ext_stream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameExt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdGetManyByNameExtResponseStream.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("get_object_many_by_name_ext_stream"))
                  .build();
          }
        }
     }
     return getGetObjectManyByNameExtStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitData,
      org.anonymous.grpc.CmdChangeInitDataResponse> getChangeInitDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "change_init_data",
      requestType = org.anonymous.grpc.CmdChangeInitData.class,
      responseType = org.anonymous.grpc.CmdChangeInitDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitData,
      org.anonymous.grpc.CmdChangeInitDataResponse> getChangeInitDataMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitData, org.anonymous.grpc.CmdChangeInitDataResponse> getChangeInitDataMethod;
    if ((getChangeInitDataMethod = ObjServiceGrpc.getChangeInitDataMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getChangeInitDataMethod = ObjServiceGrpc.getChangeInitDataMethod) == null) {
          ObjServiceGrpc.getChangeInitDataMethod = getChangeInitDataMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdChangeInitData, org.anonymous.grpc.CmdChangeInitDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "change_init_data"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdChangeInitData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdChangeInitDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("change_init_data"))
                  .build();
          }
        }
     }
     return getChangeInitDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitDataExt,
      org.anonymous.grpc.CmdChangeInitDataExtResponse> getChangeInitDataExtMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "change_init_data_ext",
      requestType = org.anonymous.grpc.CmdChangeInitDataExt.class,
      responseType = org.anonymous.grpc.CmdChangeInitDataExtResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitDataExt,
      org.anonymous.grpc.CmdChangeInitDataExtResponse> getChangeInitDataExtMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.CmdChangeInitDataExt, org.anonymous.grpc.CmdChangeInitDataExtResponse> getChangeInitDataExtMethod;
    if ((getChangeInitDataExtMethod = ObjServiceGrpc.getChangeInitDataExtMethod) == null) {
      synchronized (ObjServiceGrpc.class) {
        if ((getChangeInitDataExtMethod = ObjServiceGrpc.getChangeInitDataExtMethod) == null) {
          ObjServiceGrpc.getChangeInitDataExtMethod = getChangeInitDataExtMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.CmdChangeInitDataExt, org.anonymous.grpc.CmdChangeInitDataExtResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjService", "change_init_data_ext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdChangeInitDataExt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.CmdChangeInitDataExtResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjServiceMethodDescriptorSupplier("change_init_data_ext"))
                  .build();
          }
        }
     }
     return getChangeInitDataExtMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ObjServiceStub newStub(io.grpc.Channel channel) {
    return new ObjServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ObjServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ObjServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ObjServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ObjServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ObjServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void connect(org.anonymous.grpc.CmdConnect request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getConnectMethod(), responseObserver);
    }

    /**
     */
    public void connectExt(org.anonymous.grpc.CmdConnectExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectExtResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getConnectExtMethod(), responseObserver);
    }

    /**
     */
    public void lookupByName(org.anonymous.grpc.CmdLookupByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLookupByNameMethod(), responseObserver);
    }

    /**
     */
    public void lookupByNameStream(org.anonymous.grpc.CmdLookupByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponseStream> responseObserver) {
      asyncUnimplementedUnaryCall(getLookupByNameStreamMethod(), responseObserver);
    }

    /**
     */
    public void lookupByType(org.anonymous.grpc.CmdNameLookupByType request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLookupByTypeMethod(), responseObserver);
    }

    /**
     */
    public void lookupByTypeStream(org.anonymous.grpc.CmdNameLookupByType request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponseStream> responseObserver) {
      asyncUnimplementedUnaryCall(getLookupByTypeStreamMethod(), responseObserver);
    }

    /**
     */
    public void getObject(org.anonymous.grpc.CmdGetByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectMethod(), responseObserver);
    }

    /**
     */
    public void getObjectExt(org.anonymous.grpc.CmdGetByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameExtResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectExtMethod(), responseObserver);
    }

    /**
     */
    public void getObjectManyByName(org.anonymous.grpc.CmdGetManyByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectManyByNameMethod(), responseObserver);
    }

    /**
     */
    public void getObjectManyByNameStream(org.anonymous.grpc.CmdGetManyByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponseStream> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectManyByNameStreamMethod(), responseObserver);
    }

    /**
     */
    public void getObjectManyByNameExt(org.anonymous.grpc.CmdGetManyByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectManyByNameExtMethod(), responseObserver);
    }

    /**
     */
    public void getObjectManyByNameExtStream(org.anonymous.grpc.CmdGetManyByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponseStream> responseObserver) {
      asyncUnimplementedUnaryCall(getGetObjectManyByNameExtStreamMethod(), responseObserver);
    }

    /**
     */
    public void changeInitData(org.anonymous.grpc.CmdChangeInitData request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getChangeInitDataMethod(), responseObserver);
    }

    /**
     */
    public void changeInitDataExt(org.anonymous.grpc.CmdChangeInitDataExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataExtResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getChangeInitDataExtMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getConnectMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdConnect,
                org.anonymous.grpc.CmdConnectResponse>(
                  this, METHODID_CONNECT)))
          .addMethod(
            getConnectExtMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdConnectExt,
                org.anonymous.grpc.CmdConnectExtResponse>(
                  this, METHODID_CONNECT_EXT)))
          .addMethod(
            getLookupByNameMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdLookupByName,
                org.anonymous.grpc.CmdLookupByNameResponse>(
                  this, METHODID_LOOKUP_BY_NAME)))
          .addMethod(
            getLookupByNameStreamMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdLookupByName,
                org.anonymous.grpc.CmdLookupByNameResponseStream>(
                  this, METHODID_LOOKUP_BY_NAME_STREAM)))
          .addMethod(
            getLookupByTypeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdNameLookupByType,
                org.anonymous.grpc.CmdNameLookupByTypeResponse>(
                  this, METHODID_LOOKUP_BY_TYPE)))
          .addMethod(
            getLookupByTypeStreamMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdNameLookupByType,
                org.anonymous.grpc.CmdNameLookupByTypeResponseStream>(
                  this, METHODID_LOOKUP_BY_TYPE_STREAM)))
          .addMethod(
            getGetObjectMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetByName,
                org.anonymous.grpc.CmdGetByNameResponse>(
                  this, METHODID_GET_OBJECT)))
          .addMethod(
            getGetObjectExtMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetByNameExt,
                org.anonymous.grpc.CmdGetByNameExtResponse>(
                  this, METHODID_GET_OBJECT_EXT)))
          .addMethod(
            getGetObjectManyByNameMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetManyByName,
                org.anonymous.grpc.CmdGetManyByNameResponse>(
                  this, METHODID_GET_OBJECT_MANY_BY_NAME)))
          .addMethod(
            getGetObjectManyByNameStreamMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetManyByName,
                org.anonymous.grpc.CmdGetManyByNameResponseStream>(
                  this, METHODID_GET_OBJECT_MANY_BY_NAME_STREAM)))
          .addMethod(
            getGetObjectManyByNameExtMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetManyByNameExt,
                org.anonymous.grpc.CmdGetManyByNameExtResponse>(
                  this, METHODID_GET_OBJECT_MANY_BY_NAME_EXT)))
          .addMethod(
            getGetObjectManyByNameExtStreamMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdGetManyByNameExt,
                org.anonymous.grpc.CmdGetManyByNameExtResponseStream>(
                  this, METHODID_GET_OBJECT_MANY_BY_NAME_EXT_STREAM)))
          .addMethod(
            getChangeInitDataMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdChangeInitData,
                org.anonymous.grpc.CmdChangeInitDataResponse>(
                  this, METHODID_CHANGE_INIT_DATA)))
          .addMethod(
            getChangeInitDataExtMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.CmdChangeInitDataExt,
                org.anonymous.grpc.CmdChangeInitDataExtResponse>(
                  this, METHODID_CHANGE_INIT_DATA_EXT)))
          .build();
    }
  }

  /**
   */
  public static final class ObjServiceStub extends io.grpc.stub.AbstractStub<ObjServiceStub> {
    private ObjServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjServiceStub(channel, callOptions);
    }

    /**
     */
    public void connect(org.anonymous.grpc.CmdConnect request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getConnectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void connectExt(org.anonymous.grpc.CmdConnectExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectExtResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getConnectExtMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookupByName(org.anonymous.grpc.CmdLookupByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLookupByNameMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookupByNameStream(org.anonymous.grpc.CmdLookupByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponseStream> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getLookupByNameStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookupByType(org.anonymous.grpc.CmdNameLookupByType request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLookupByTypeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookupByTypeStream(org.anonymous.grpc.CmdNameLookupByType request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponseStream> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getLookupByTypeStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObject(org.anonymous.grpc.CmdGetByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObjectExt(org.anonymous.grpc.CmdGetByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameExtResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectExtMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObjectManyByName(org.anonymous.grpc.CmdGetManyByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectManyByNameMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObjectManyByNameStream(org.anonymous.grpc.CmdGetManyByName request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponseStream> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetObjectManyByNameStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObjectManyByNameExt(org.anonymous.grpc.CmdGetManyByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetObjectManyByNameExtMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getObjectManyByNameExtStream(org.anonymous.grpc.CmdGetManyByNameExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponseStream> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetObjectManyByNameExtStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void changeInitData(org.anonymous.grpc.CmdChangeInitData request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getChangeInitDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void changeInitDataExt(org.anonymous.grpc.CmdChangeInitDataExt request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataExtResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getChangeInitDataExtMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ObjServiceBlockingStub extends io.grpc.stub.AbstractStub<ObjServiceBlockingStub> {
    private ObjServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.anonymous.grpc.CmdConnectResponse connect(org.anonymous.grpc.CmdConnect request) {
      return blockingUnaryCall(
          getChannel(), getConnectMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdConnectExtResponse connectExt(org.anonymous.grpc.CmdConnectExt request) {
      return blockingUnaryCall(
          getChannel(), getConnectExtMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdLookupByNameResponse lookupByName(org.anonymous.grpc.CmdLookupByName request) {
      return blockingUnaryCall(
          getChannel(), getLookupByNameMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.anonymous.grpc.CmdLookupByNameResponseStream> lookupByNameStream(
        org.anonymous.grpc.CmdLookupByName request) {
      return blockingServerStreamingCall(
          getChannel(), getLookupByNameStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdNameLookupByTypeResponse lookupByType(org.anonymous.grpc.CmdNameLookupByType request) {
      return blockingUnaryCall(
          getChannel(), getLookupByTypeMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.anonymous.grpc.CmdNameLookupByTypeResponseStream> lookupByTypeStream(
        org.anonymous.grpc.CmdNameLookupByType request) {
      return blockingServerStreamingCall(
          getChannel(), getLookupByTypeStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdGetByNameResponse getObject(org.anonymous.grpc.CmdGetByName request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdGetByNameExtResponse getObjectExt(org.anonymous.grpc.CmdGetByNameExt request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectExtMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdGetManyByNameResponse getObjectManyByName(org.anonymous.grpc.CmdGetManyByName request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectManyByNameMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.anonymous.grpc.CmdGetManyByNameResponseStream> getObjectManyByNameStream(
        org.anonymous.grpc.CmdGetManyByName request) {
      return blockingServerStreamingCall(
          getChannel(), getGetObjectManyByNameStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdGetManyByNameExtResponse getObjectManyByNameExt(org.anonymous.grpc.CmdGetManyByNameExt request) {
      return blockingUnaryCall(
          getChannel(), getGetObjectManyByNameExtMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.anonymous.grpc.CmdGetManyByNameExtResponseStream> getObjectManyByNameExtStream(
        org.anonymous.grpc.CmdGetManyByNameExt request) {
      return blockingServerStreamingCall(
          getChannel(), getGetObjectManyByNameExtStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdChangeInitDataResponse changeInitData(org.anonymous.grpc.CmdChangeInitData request) {
      return blockingUnaryCall(
          getChannel(), getChangeInitDataMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.anonymous.grpc.CmdChangeInitDataExtResponse changeInitDataExt(org.anonymous.grpc.CmdChangeInitDataExt request) {
      return blockingUnaryCall(
          getChannel(), getChangeInitDataExtMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ObjServiceFutureStub extends io.grpc.stub.AbstractStub<ObjServiceFutureStub> {
    private ObjServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdConnectResponse> connect(
        org.anonymous.grpc.CmdConnect request) {
      return futureUnaryCall(
          getChannel().newCall(getConnectMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdConnectExtResponse> connectExt(
        org.anonymous.grpc.CmdConnectExt request) {
      return futureUnaryCall(
          getChannel().newCall(getConnectExtMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdLookupByNameResponse> lookupByName(
        org.anonymous.grpc.CmdLookupByName request) {
      return futureUnaryCall(
          getChannel().newCall(getLookupByNameMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdNameLookupByTypeResponse> lookupByType(
        org.anonymous.grpc.CmdNameLookupByType request) {
      return futureUnaryCall(
          getChannel().newCall(getLookupByTypeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdGetByNameResponse> getObject(
        org.anonymous.grpc.CmdGetByName request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdGetByNameExtResponse> getObjectExt(
        org.anonymous.grpc.CmdGetByNameExt request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectExtMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdGetManyByNameResponse> getObjectManyByName(
        org.anonymous.grpc.CmdGetManyByName request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectManyByNameMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdGetManyByNameExtResponse> getObjectManyByNameExt(
        org.anonymous.grpc.CmdGetManyByNameExt request) {
      return futureUnaryCall(
          getChannel().newCall(getGetObjectManyByNameExtMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdChangeInitDataResponse> changeInitData(
        org.anonymous.grpc.CmdChangeInitData request) {
      return futureUnaryCall(
          getChannel().newCall(getChangeInitDataMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.CmdChangeInitDataExtResponse> changeInitDataExt(
        org.anonymous.grpc.CmdChangeInitDataExt request) {
      return futureUnaryCall(
          getChannel().newCall(getChangeInitDataExtMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CONNECT = 0;
  private static final int METHODID_CONNECT_EXT = 1;
  private static final int METHODID_LOOKUP_BY_NAME = 2;
  private static final int METHODID_LOOKUP_BY_NAME_STREAM = 3;
  private static final int METHODID_LOOKUP_BY_TYPE = 4;
  private static final int METHODID_LOOKUP_BY_TYPE_STREAM = 5;
  private static final int METHODID_GET_OBJECT = 6;
  private static final int METHODID_GET_OBJECT_EXT = 7;
  private static final int METHODID_GET_OBJECT_MANY_BY_NAME = 8;
  private static final int METHODID_GET_OBJECT_MANY_BY_NAME_STREAM = 9;
  private static final int METHODID_GET_OBJECT_MANY_BY_NAME_EXT = 10;
  private static final int METHODID_GET_OBJECT_MANY_BY_NAME_EXT_STREAM = 11;
  private static final int METHODID_CHANGE_INIT_DATA = 12;
  private static final int METHODID_CHANGE_INIT_DATA_EXT = 13;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ObjServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ObjServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CONNECT:
          serviceImpl.connect((org.anonymous.grpc.CmdConnect) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectResponse>) responseObserver);
          break;
        case METHODID_CONNECT_EXT:
          serviceImpl.connectExt((org.anonymous.grpc.CmdConnectExt) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectExtResponse>) responseObserver);
          break;
        case METHODID_LOOKUP_BY_NAME:
          serviceImpl.lookupByName((org.anonymous.grpc.CmdLookupByName) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponse>) responseObserver);
          break;
        case METHODID_LOOKUP_BY_NAME_STREAM:
          serviceImpl.lookupByNameStream((org.anonymous.grpc.CmdLookupByName) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdLookupByNameResponseStream>) responseObserver);
          break;
        case METHODID_LOOKUP_BY_TYPE:
          serviceImpl.lookupByType((org.anonymous.grpc.CmdNameLookupByType) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponse>) responseObserver);
          break;
        case METHODID_LOOKUP_BY_TYPE_STREAM:
          serviceImpl.lookupByTypeStream((org.anonymous.grpc.CmdNameLookupByType) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdNameLookupByTypeResponseStream>) responseObserver);
          break;
        case METHODID_GET_OBJECT:
          serviceImpl.getObject((org.anonymous.grpc.CmdGetByName) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameResponse>) responseObserver);
          break;
        case METHODID_GET_OBJECT_EXT:
          serviceImpl.getObjectExt((org.anonymous.grpc.CmdGetByNameExt) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetByNameExtResponse>) responseObserver);
          break;
        case METHODID_GET_OBJECT_MANY_BY_NAME:
          serviceImpl.getObjectManyByName((org.anonymous.grpc.CmdGetManyByName) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponse>) responseObserver);
          break;
        case METHODID_GET_OBJECT_MANY_BY_NAME_STREAM:
          serviceImpl.getObjectManyByNameStream((org.anonymous.grpc.CmdGetManyByName) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameResponseStream>) responseObserver);
          break;
        case METHODID_GET_OBJECT_MANY_BY_NAME_EXT:
          serviceImpl.getObjectManyByNameExt((org.anonymous.grpc.CmdGetManyByNameExt) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponse>) responseObserver);
          break;
        case METHODID_GET_OBJECT_MANY_BY_NAME_EXT_STREAM:
          serviceImpl.getObjectManyByNameExtStream((org.anonymous.grpc.CmdGetManyByNameExt) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdGetManyByNameExtResponseStream>) responseObserver);
          break;
        case METHODID_CHANGE_INIT_DATA:
          serviceImpl.changeInitData((org.anonymous.grpc.CmdChangeInitData) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataResponse>) responseObserver);
          break;
        case METHODID_CHANGE_INIT_DATA_EXT:
          serviceImpl.changeInitDataExt((org.anonymous.grpc.CmdChangeInitDataExt) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdChangeInitDataExtResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ObjServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ObjServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.anonymous.grpc.ObjSvc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ObjService");
    }
  }

  private static final class ObjServiceFileDescriptorSupplier
      extends ObjServiceBaseDescriptorSupplier {
    ObjServiceFileDescriptorSupplier() {}
  }

  private static final class ObjServiceMethodDescriptorSupplier
      extends ObjServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ObjServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ObjServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ObjServiceFileDescriptorSupplier())
              .addMethod(getConnectMethod())
              .addMethod(getConnectExtMethod())
              .addMethod(getLookupByNameMethod())
              .addMethod(getLookupByNameStreamMethod())
              .addMethod(getLookupByTypeMethod())
              .addMethod(getLookupByTypeStreamMethod())
              .addMethod(getGetObjectMethod())
              .addMethod(getGetObjectExtMethod())
              .addMethod(getGetObjectManyByNameMethod())
              .addMethod(getGetObjectManyByNameStreamMethod())
              .addMethod(getGetObjectManyByNameExtMethod())
              .addMethod(getGetObjectManyByNameExtStreamMethod())
              .addMethod(getChangeInitDataMethod())
              .addMethod(getChangeInitDataExtMethod())
              .build();
        }
      }
    }
    return result;
  }
}
