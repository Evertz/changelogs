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
    deps = [
        ":changelog_proto_java",
        ":flags_proto_java",

        "//3rdparty/jvm/com/google/guava",
        "//3rdparty/jvm/org/projectlombok:lombok",
        "@com_google_protobuf//:protobuf_java",
    ],
    plugins = [
        "//:lombok",
    ],
    visibility = ["//visibility:public"]
)
