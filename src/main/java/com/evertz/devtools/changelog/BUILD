java_proto_library(
    name = "changelog_proto_java",
    deps = ["//proto:changelog_proto"],
)

java_proto_library(
    name = "flags_proto_java",
    deps = ["//proto:flags_proto"],
)

java_library(
    name = "changelog_configuration",
    srcs = ["ChangelogConfigurationLoader.java"],
    deps = [
        ":flags_proto_java",
        "//src/main/java/com/evertz/devtools/changelog/platformhost",
        "@com_google_protobuf//:protobuf_java",
        "//3rdparty/jvm/com/google/guava",
    ]
)

java_library(
    name = "changelog",
    srcs = ["Main.java", "ArgParsers.java", "Runner.java"],
    deps = [
        ":changelog_proto_java",
        ":flags_proto_java",
        ":changelog_configuration",

        "//src/main/java/com/evertz/devtools/changelog/compiler",
        "//src/main/java/com/evertz/devtools/changelog/emitters",
        "//src/main/java/com/evertz/devtools/changelog/platformhost",

        "@com_google_protobuf//:protobuf_java",
        "//3rdparty/jvm/com/github/zafarkhaja:java_semver",
        "//3rdparty/jvm/net/sourceforge/argparse4j",
        "//3rdparty/jvm/org/projectlombok:lombok",
        "//3rdparty/jvm/com/google/guava",
    ],
    plugins = [
        "//:lombok"
    ],
    visibility = ["//visibility:public"]
)
