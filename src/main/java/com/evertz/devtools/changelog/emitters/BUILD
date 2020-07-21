load("@rules_jvm_external//:defs.bzl", "artifact")

java_proto_library(
    name = "changelog_proto_java",
    deps = ["//proto:changelog_proto"],
)

java_proto_library(
    name = "flags_proto_java",
    deps = ["//proto:flags_proto"],
)

java_library(
    name = "emitters",
    srcs = glob(["*.java"]),
    plugins = [
        "//:lombok",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":changelog_proto_java",
        ":flags_proto_java",
        artifact("com.google.guava:guava"),
        artifact("org.projectlombok:lombok"),
        "@com_google_protobuf//:protobuf_java",
        "@com_google_protobuf//:protobuf_java_util",
    ],
)
