// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: more_mesage.proto

package proto;

/**
 * Protobuf type {@code proto.Message}
 */
public final class Message extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:proto.Message)
    MessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Message.newBuilder() to construct.
  private Message(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Message() {
    csvRows_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Message();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return proto.recordsMore.internal_static_proto_Message_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return proto.recordsMore.internal_static_proto_Message_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            proto.Message.class, proto.Message.Builder.class);
  }

  public static final int MESSAGE_SENT_AT_FIELD_NUMBER = 1;
  private long messageSentAt_ = 0L;
  /**
   * <code>int64 message_sent_at = 1;</code>
   * @return The messageSentAt.
   */
  @java.lang.Override
  public long getMessageSentAt() {
    return messageSentAt_;
  }

  public static final int CSV_ROWS_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private java.util.List<proto.CsvRow> csvRows_;
  /**
   * <code>repeated .proto.CsvRow csv_rows = 2;</code>
   */
  @java.lang.Override
  public java.util.List<proto.CsvRow> getCsvRowsList() {
    return csvRows_;
  }
  /**
   * <code>repeated .proto.CsvRow csv_rows = 2;</code>
   */
  @java.lang.Override
  public java.util.List<? extends proto.CsvRowOrBuilder> 
      getCsvRowsOrBuilderList() {
    return csvRows_;
  }
  /**
   * <code>repeated .proto.CsvRow csv_rows = 2;</code>
   */
  @java.lang.Override
  public int getCsvRowsCount() {
    return csvRows_.size();
  }
  /**
   * <code>repeated .proto.CsvRow csv_rows = 2;</code>
   */
  @java.lang.Override
  public proto.CsvRow getCsvRows(int index) {
    return csvRows_.get(index);
  }
  /**
   * <code>repeated .proto.CsvRow csv_rows = 2;</code>
   */
  @java.lang.Override
  public proto.CsvRowOrBuilder getCsvRowsOrBuilder(
      int index) {
    return csvRows_.get(index);
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
    if (messageSentAt_ != 0L) {
      output.writeInt64(1, messageSentAt_);
    }
    for (int i = 0; i < csvRows_.size(); i++) {
      output.writeMessage(2, csvRows_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (messageSentAt_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, messageSentAt_);
    }
    for (int i = 0; i < csvRows_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, csvRows_.get(i));
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof proto.Message)) {
      return super.equals(obj);
    }
    proto.Message other = (proto.Message) obj;

    if (getMessageSentAt()
        != other.getMessageSentAt()) return false;
    if (!getCsvRowsList()
        .equals(other.getCsvRowsList())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + MESSAGE_SENT_AT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getMessageSentAt());
    if (getCsvRowsCount() > 0) {
      hash = (37 * hash) + CSV_ROWS_FIELD_NUMBER;
      hash = (53 * hash) + getCsvRowsList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static proto.Message parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static proto.Message parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static proto.Message parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static proto.Message parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static proto.Message parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static proto.Message parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static proto.Message parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static proto.Message parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static proto.Message parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static proto.Message parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static proto.Message parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static proto.Message parseFrom(
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
  public static Builder newBuilder(proto.Message prototype) {
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
   * Protobuf type {@code proto.Message}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:proto.Message)
      proto.MessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.recordsMore.internal_static_proto_Message_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.recordsMore.internal_static_proto_Message_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.Message.class, proto.Message.Builder.class);
    }

    // Construct using proto.Message.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      messageSentAt_ = 0L;
      if (csvRowsBuilder_ == null) {
        csvRows_ = java.util.Collections.emptyList();
      } else {
        csvRows_ = null;
        csvRowsBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return proto.recordsMore.internal_static_proto_Message_descriptor;
    }

    @java.lang.Override
    public proto.Message getDefaultInstanceForType() {
      return proto.Message.getDefaultInstance();
    }

    @java.lang.Override
    public proto.Message build() {
      proto.Message result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public proto.Message buildPartial() {
      proto.Message result = new proto.Message(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(proto.Message result) {
      if (csvRowsBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0)) {
          csvRows_ = java.util.Collections.unmodifiableList(csvRows_);
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.csvRows_ = csvRows_;
      } else {
        result.csvRows_ = csvRowsBuilder_.build();
      }
    }

    private void buildPartial0(proto.Message result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.messageSentAt_ = messageSentAt_;
      }
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
      if (other instanceof proto.Message) {
        return mergeFrom((proto.Message)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(proto.Message other) {
      if (other == proto.Message.getDefaultInstance()) return this;
      if (other.getMessageSentAt() != 0L) {
        setMessageSentAt(other.getMessageSentAt());
      }
      if (csvRowsBuilder_ == null) {
        if (!other.csvRows_.isEmpty()) {
          if (csvRows_.isEmpty()) {
            csvRows_ = other.csvRows_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureCsvRowsIsMutable();
            csvRows_.addAll(other.csvRows_);
          }
          onChanged();
        }
      } else {
        if (!other.csvRows_.isEmpty()) {
          if (csvRowsBuilder_.isEmpty()) {
            csvRowsBuilder_.dispose();
            csvRowsBuilder_ = null;
            csvRows_ = other.csvRows_;
            bitField0_ = (bitField0_ & ~0x00000002);
            csvRowsBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getCsvRowsFieldBuilder() : null;
          } else {
            csvRowsBuilder_.addAllMessages(other.csvRows_);
          }
        }
      }
      this.mergeUnknownFields(other.getUnknownFields());
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
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              messageSentAt_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 18: {
              proto.CsvRow m =
                  input.readMessage(
                      proto.CsvRow.parser(),
                      extensionRegistry);
              if (csvRowsBuilder_ == null) {
                ensureCsvRowsIsMutable();
                csvRows_.add(m);
              } else {
                csvRowsBuilder_.addMessage(m);
              }
              break;
            } // case 18
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private long messageSentAt_ ;
    /**
     * <code>int64 message_sent_at = 1;</code>
     * @return The messageSentAt.
     */
    @java.lang.Override
    public long getMessageSentAt() {
      return messageSentAt_;
    }
    /**
     * <code>int64 message_sent_at = 1;</code>
     * @param value The messageSentAt to set.
     * @return This builder for chaining.
     */
    public Builder setMessageSentAt(long value) {

      messageSentAt_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int64 message_sent_at = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessageSentAt() {
      bitField0_ = (bitField0_ & ~0x00000001);
      messageSentAt_ = 0L;
      onChanged();
      return this;
    }

    private java.util.List<proto.CsvRow> csvRows_ =
      java.util.Collections.emptyList();
    private void ensureCsvRowsIsMutable() {
      if (!((bitField0_ & 0x00000002) != 0)) {
        csvRows_ = new java.util.ArrayList<proto.CsvRow>(csvRows_);
        bitField0_ |= 0x00000002;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        proto.CsvRow, proto.CsvRow.Builder, proto.CsvRowOrBuilder> csvRowsBuilder_;

    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public java.util.List<proto.CsvRow> getCsvRowsList() {
      if (csvRowsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(csvRows_);
      } else {
        return csvRowsBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public int getCsvRowsCount() {
      if (csvRowsBuilder_ == null) {
        return csvRows_.size();
      } else {
        return csvRowsBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public proto.CsvRow getCsvRows(int index) {
      if (csvRowsBuilder_ == null) {
        return csvRows_.get(index);
      } else {
        return csvRowsBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder setCsvRows(
        int index, proto.CsvRow value) {
      if (csvRowsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCsvRowsIsMutable();
        csvRows_.set(index, value);
        onChanged();
      } else {
        csvRowsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder setCsvRows(
        int index, proto.CsvRow.Builder builderForValue) {
      if (csvRowsBuilder_ == null) {
        ensureCsvRowsIsMutable();
        csvRows_.set(index, builderForValue.build());
        onChanged();
      } else {
        csvRowsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder addCsvRows(proto.CsvRow value) {
      if (csvRowsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCsvRowsIsMutable();
        csvRows_.add(value);
        onChanged();
      } else {
        csvRowsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder addCsvRows(
        int index, proto.CsvRow value) {
      if (csvRowsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCsvRowsIsMutable();
        csvRows_.add(index, value);
        onChanged();
      } else {
        csvRowsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder addCsvRows(
        proto.CsvRow.Builder builderForValue) {
      if (csvRowsBuilder_ == null) {
        ensureCsvRowsIsMutable();
        csvRows_.add(builderForValue.build());
        onChanged();
      } else {
        csvRowsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder addCsvRows(
        int index, proto.CsvRow.Builder builderForValue) {
      if (csvRowsBuilder_ == null) {
        ensureCsvRowsIsMutable();
        csvRows_.add(index, builderForValue.build());
        onChanged();
      } else {
        csvRowsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder addAllCsvRows(
        java.lang.Iterable<? extends proto.CsvRow> values) {
      if (csvRowsBuilder_ == null) {
        ensureCsvRowsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, csvRows_);
        onChanged();
      } else {
        csvRowsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder clearCsvRows() {
      if (csvRowsBuilder_ == null) {
        csvRows_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
      } else {
        csvRowsBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public Builder removeCsvRows(int index) {
      if (csvRowsBuilder_ == null) {
        ensureCsvRowsIsMutable();
        csvRows_.remove(index);
        onChanged();
      } else {
        csvRowsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public proto.CsvRow.Builder getCsvRowsBuilder(
        int index) {
      return getCsvRowsFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public proto.CsvRowOrBuilder getCsvRowsOrBuilder(
        int index) {
      if (csvRowsBuilder_ == null) {
        return csvRows_.get(index);  } else {
        return csvRowsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public java.util.List<? extends proto.CsvRowOrBuilder> 
         getCsvRowsOrBuilderList() {
      if (csvRowsBuilder_ != null) {
        return csvRowsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(csvRows_);
      }
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public proto.CsvRow.Builder addCsvRowsBuilder() {
      return getCsvRowsFieldBuilder().addBuilder(
          proto.CsvRow.getDefaultInstance());
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public proto.CsvRow.Builder addCsvRowsBuilder(
        int index) {
      return getCsvRowsFieldBuilder().addBuilder(
          index, proto.CsvRow.getDefaultInstance());
    }
    /**
     * <code>repeated .proto.CsvRow csv_rows = 2;</code>
     */
    public java.util.List<proto.CsvRow.Builder> 
         getCsvRowsBuilderList() {
      return getCsvRowsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        proto.CsvRow, proto.CsvRow.Builder, proto.CsvRowOrBuilder> 
        getCsvRowsFieldBuilder() {
      if (csvRowsBuilder_ == null) {
        csvRowsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            proto.CsvRow, proto.CsvRow.Builder, proto.CsvRowOrBuilder>(
                csvRows_,
                ((bitField0_ & 0x00000002) != 0),
                getParentForChildren(),
                isClean());
        csvRows_ = null;
      }
      return csvRowsBuilder_;
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


    // @@protoc_insertion_point(builder_scope:proto.Message)
  }

  // @@protoc_insertion_point(class_scope:proto.Message)
  private static final proto.Message DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new proto.Message();
  }

  public static proto.Message getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Message>
      PARSER = new com.google.protobuf.AbstractParser<Message>() {
    @java.lang.Override
    public Message parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<Message> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Message> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public proto.Message getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

