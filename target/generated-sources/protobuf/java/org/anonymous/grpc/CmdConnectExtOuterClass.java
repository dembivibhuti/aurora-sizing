// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cmd_connect_ext.proto

package org.anonymous.grpc;

public final class CmdConnectExtOuterClass {
  private CmdConnectExtOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_anonymous_grpc_CmdConnectExt_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_anonymous_grpc_CmdConnectExt_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_anonymous_grpc_CmdConnectExtResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_anonymous_grpc_CmdConnectExtResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025cmd_connect_ext.proto\022\022org.anonymous.g" +
      "rpc\032\016cmd_type.proto\"\252\001\n\rCmdConnectExt\022\020\n" +
      "\010msg_size\030\001 \001(\005\0221\n\014message_type\030\002 \001(\0162\033." +
      "org.anonymous.grpc.CmdType\022\020\n\010app_name\030\003" +
      " \001(\t\022\021\n\tuser_name\030\004 \001(\t\022\026\n\016client_versio" +
      "n\030\005 \001(\005\022\027\n\017client_revision\030\006 \001(\005\"T\n\025CmdC" +
      "onnectExtResponse\022\020\n\010msg_size\030\001 \001(\r\022\023\n\013v" +
      "er_and_rev\030\002 \001(\005\022\024\n\014feature_flag\030\003 \001(\rB<" +
      "P\001Z8github.com/somnath67643/aurora-sizin" +
      "g/clientgo/baseprotob\006proto3"
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
          org.anonymous.grpc.CmdTypeOuterClass.getDescriptor(),
        }, assigner);
    internal_static_org_anonymous_grpc_CmdConnectExt_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_anonymous_grpc_CmdConnectExt_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_anonymous_grpc_CmdConnectExt_descriptor,
        new java.lang.String[] { "MsgSize", "MessageType", "AppName", "UserName", "ClientVersion", "ClientRevision", });
    internal_static_org_anonymous_grpc_CmdConnectExtResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_anonymous_grpc_CmdConnectExtResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_anonymous_grpc_CmdConnectExtResponse_descriptor,
        new java.lang.String[] { "MsgSize", "VerAndRev", "FeatureFlag", });
    org.anonymous.grpc.CmdTypeOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
