// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ObjectService.proto

package org.anonymous.grpc;

public final class ObjectServiceOuterClass {
  private ObjectServiceOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_anonymous_grpc_ObjectRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_anonymous_grpc_ObjectRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_anonymous_grpc_ObjectResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_anonymous_grpc_ObjectResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023ObjectService.proto\022\022org.anonymous.grp" +
      "c\"-\n\rObjectRequest\022\014\n\004name\030\001 \001(\t\022\016\n\006type" +
      "Id\030\002 \001(\r\"\037\n\016ObjectResponse\022\r\n\005found\030\001 \001(" +
      "\0102`\n\rObjectService\022O\n\006exists\022!.org.anony" +
      "mous.grpc.ObjectRequest\032\".org.anonymous." +
      "grpc.ObjectResponseB<P\001Z8github.com/somn" +
      "ath67643/aurora-sizing/clientgo/baseprot" +
      "ob\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_org_anonymous_grpc_ObjectRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_anonymous_grpc_ObjectRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_anonymous_grpc_ObjectRequest_descriptor,
        new java.lang.String[] { "Name", "TypeId", });
    internal_static_org_anonymous_grpc_ObjectResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_anonymous_grpc_ObjectResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_anonymous_grpc_ObjectResponse_descriptor,
        new java.lang.String[] { "Found", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
