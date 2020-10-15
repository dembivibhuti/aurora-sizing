// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cmd_lookup_by_type.proto

package org.anonymous.grpc;

/**
 * Protobuf type {@code org.anonymous.grpc.CmdNameLookupByTypeResponseStream}
 */
public  final class CmdNameLookupByTypeResponseStream extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:org.anonymous.grpc.CmdNameLookupByTypeResponseStream)
    CmdNameLookupByTypeResponseStreamOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CmdNameLookupByTypeResponseStream.newBuilder() to construct.
  private CmdNameLookupByTypeResponseStream(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CmdNameLookupByTypeResponseStream() {
    securityName_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CmdNameLookupByTypeResponseStream(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            securityName_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.anonymous.grpc.CmdLookupByType.internal_static_org_anonymous_grpc_CmdNameLookupByTypeResponseStream_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.anonymous.grpc.CmdLookupByType.internal_static_org_anonymous_grpc_CmdNameLookupByTypeResponseStream_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.anonymous.grpc.CmdNameLookupByTypeResponseStream.class, org.anonymous.grpc.CmdNameLookupByTypeResponseStream.Builder.class);
  }

  public static final int SECURITY_NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object securityName_;
  /**
   * <code>string security_name = 1;</code>
   */
  public java.lang.String getSecurityName() {
    java.lang.Object ref = securityName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      securityName_ = s;
      return s;
    }
  }
  /**
   * <code>string security_name = 1;</code>
   */
  public com.google.protobuf.ByteString
      getSecurityNameBytes() {
    java.lang.Object ref = securityName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      securityName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getSecurityNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, securityName_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getSecurityNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, securityName_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.anonymous.grpc.CmdNameLookupByTypeResponseStream)) {
      return super.equals(obj);
    }
    org.anonymous.grpc.CmdNameLookupByTypeResponseStream other = (org.anonymous.grpc.CmdNameLookupByTypeResponseStream) obj;

    if (!getSecurityName()
        .equals(other.getSecurityName())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SECURITY_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getSecurityName().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.anonymous.grpc.CmdNameLookupByTypeResponseStream prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code org.anonymous.grpc.CmdNameLookupByTypeResponseStream}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:org.anonymous.grpc.CmdNameLookupByTypeResponseStream)
      org.anonymous.grpc.CmdNameLookupByTypeResponseStreamOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.anonymous.grpc.CmdLookupByType.internal_static_org_anonymous_grpc_CmdNameLookupByTypeResponseStream_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.anonymous.grpc.CmdLookupByType.internal_static_org_anonymous_grpc_CmdNameLookupByTypeResponseStream_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.anonymous.grpc.CmdNameLookupByTypeResponseStream.class, org.anonymous.grpc.CmdNameLookupByTypeResponseStream.Builder.class);
    }

    // Construct using org.anonymous.grpc.CmdNameLookupByTypeResponseStream.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      securityName_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.anonymous.grpc.CmdLookupByType.internal_static_org_anonymous_grpc_CmdNameLookupByTypeResponseStream_descriptor;
    }

    @java.lang.Override
    public org.anonymous.grpc.CmdNameLookupByTypeResponseStream getDefaultInstanceForType() {
      return org.anonymous.grpc.CmdNameLookupByTypeResponseStream.getDefaultInstance();
    }

    @java.lang.Override
    public org.anonymous.grpc.CmdNameLookupByTypeResponseStream build() {
      org.anonymous.grpc.CmdNameLookupByTypeResponseStream result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.anonymous.grpc.CmdNameLookupByTypeResponseStream buildPartial() {
      org.anonymous.grpc.CmdNameLookupByTypeResponseStream result = new org.anonymous.grpc.CmdNameLookupByTypeResponseStream(this);
      result.securityName_ = securityName_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.anonymous.grpc.CmdNameLookupByTypeResponseStream) {
        return mergeFrom((org.anonymous.grpc.CmdNameLookupByTypeResponseStream)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.anonymous.grpc.CmdNameLookupByTypeResponseStream other) {
      if (other == org.anonymous.grpc.CmdNameLookupByTypeResponseStream.getDefaultInstance()) return this;
      if (!other.getSecurityName().isEmpty()) {
        securityName_ = other.securityName_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.anonymous.grpc.CmdNameLookupByTypeResponseStream parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.anonymous.grpc.CmdNameLookupByTypeResponseStream) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object securityName_ = "";
    /**
     * <code>string security_name = 1;</code>
     */
    public java.lang.String getSecurityName() {
      java.lang.Object ref = securityName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        securityName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string security_name = 1;</code>
     */
    public com.google.protobuf.ByteString
        getSecurityNameBytes() {
      java.lang.Object ref = securityName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        securityName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string security_name = 1;</code>
     */
    public Builder setSecurityName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      securityName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string security_name = 1;</code>
     */
    public Builder clearSecurityName() {
      
      securityName_ = getDefaultInstance().getSecurityName();
      onChanged();
      return this;
    }
    /**
     * <code>string security_name = 1;</code>
     */
    public Builder setSecurityNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      securityName_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:org.anonymous.grpc.CmdNameLookupByTypeResponseStream)
  }

  // @@protoc_insertion_point(class_scope:org.anonymous.grpc.CmdNameLookupByTypeResponseStream)
  private static final org.anonymous.grpc.CmdNameLookupByTypeResponseStream DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.anonymous.grpc.CmdNameLookupByTypeResponseStream();
  }

  public static org.anonymous.grpc.CmdNameLookupByTypeResponseStream getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CmdNameLookupByTypeResponseStream>
      PARSER = new com.google.protobuf.AbstractParser<CmdNameLookupByTypeResponseStream>() {
    @java.lang.Override
    public CmdNameLookupByTypeResponseStream parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CmdNameLookupByTypeResponseStream(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CmdNameLookupByTypeResponseStream> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CmdNameLookupByTypeResponseStream> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.anonymous.grpc.CmdNameLookupByTypeResponseStream getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

