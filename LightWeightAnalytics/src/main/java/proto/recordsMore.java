// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: more_mesage.proto

package proto;

public final class recordsMore {
  private recordsMore() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_CsvRow_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_CsvRow_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_Message_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_Message_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021more_mesage.proto\022\005proto\".\n\006CsvRow\022\024\n\014" +
      "generated_at\030\001 \001(\003\022\016\n\006values\030\002 \003(\001\"C\n\007Me" +
      "ssage\022\027\n\017message_sent_at\030\001 \001(\003\022\037\n\010csv_ro" +
      "ws\030\002 \003(\0132\r.proto.CsvRowB\026\n\005protoB\013record" +
      "sMoreP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_proto_CsvRow_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proto_CsvRow_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proto_CsvRow_descriptor,
        new java.lang.String[] { "GeneratedAt", "Values", });
    internal_static_proto_Message_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_proto_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proto_Message_descriptor,
        new java.lang.String[] { "MessageSentAt", "CsvRows", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
