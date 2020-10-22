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
    comments = "Source: ObjectService.proto")
public final class ObjectServiceGrpc {

  private ObjectServiceGrpc() {}

  public static final String SERVICE_NAME = "org.anonymous.grpc.ObjectService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.anonymous.grpc.ObjectRequest,
      org.anonymous.grpc.ObjectResponse> getExistsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "exists",
      requestType = org.anonymous.grpc.ObjectRequest.class,
      responseType = org.anonymous.grpc.ObjectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.anonymous.grpc.ObjectRequest,
      org.anonymous.grpc.ObjectResponse> getExistsMethod() {
    io.grpc.MethodDescriptor<org.anonymous.grpc.ObjectRequest, org.anonymous.grpc.ObjectResponse> getExistsMethod;
    if ((getExistsMethod = ObjectServiceGrpc.getExistsMethod) == null) {
      synchronized (ObjectServiceGrpc.class) {
        if ((getExistsMethod = ObjectServiceGrpc.getExistsMethod) == null) {
          ObjectServiceGrpc.getExistsMethod = getExistsMethod = 
              io.grpc.MethodDescriptor.<org.anonymous.grpc.ObjectRequest, org.anonymous.grpc.ObjectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.anonymous.grpc.ObjectService", "exists"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.anonymous.grpc.ObjectResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ObjectServiceMethodDescriptorSupplier("exists"))
                  .build();
          }
        }
     }
     return getExistsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ObjectServiceStub newStub(io.grpc.Channel channel) {
    return new ObjectServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ObjectServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ObjectServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ObjectServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ObjectServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ObjectServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void exists(org.anonymous.grpc.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.ObjectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getExistsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExistsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.anonymous.grpc.ObjectRequest,
                org.anonymous.grpc.ObjectResponse>(
                  this, METHODID_EXISTS)))
          .build();
    }
  }

  /**
   */
  public static final class ObjectServiceStub extends io.grpc.stub.AbstractStub<ObjectServiceStub> {
    private ObjectServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjectServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjectServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjectServiceStub(channel, callOptions);
    }

    /**
     */
    public void exists(org.anonymous.grpc.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.anonymous.grpc.ObjectResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExistsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ObjectServiceBlockingStub extends io.grpc.stub.AbstractStub<ObjectServiceBlockingStub> {
    private ObjectServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjectServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjectServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjectServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.anonymous.grpc.ObjectResponse exists(org.anonymous.grpc.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getExistsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ObjectServiceFutureStub extends io.grpc.stub.AbstractStub<ObjectServiceFutureStub> {
    private ObjectServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ObjectServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ObjectServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ObjectServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.anonymous.grpc.ObjectResponse> exists(
        org.anonymous.grpc.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getExistsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXISTS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ObjectServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ObjectServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXISTS:
          serviceImpl.exists((org.anonymous.grpc.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.anonymous.grpc.ObjectResponse>) responseObserver);
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

  private static abstract class ObjectServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ObjectServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.anonymous.grpc.ObjectServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ObjectService");
    }
  }

  private static final class ObjectServiceFileDescriptorSupplier
      extends ObjectServiceBaseDescriptorSupplier {
    ObjectServiceFileDescriptorSupplier() {}
  }

  private static final class ObjectServiceMethodDescriptorSupplier
      extends ObjectServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ObjectServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ObjectServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ObjectServiceFileDescriptorSupplier())
              .addMethod(getExistsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
