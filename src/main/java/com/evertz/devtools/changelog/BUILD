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
    name = "changelog_configuration",
    srcs = ["ChangelogConfigurationLoader.java"],
    deps = [
        ":flags_proto_java",
        "//src/main/java/com/evertz/devtools/changelog/platformhost",
        "@com_google_protobuf//:protobuf_java",
        artifact("com.google.guava:guava"),
    ],
)

java_library(
    name = "changelog",
    srcs = [
        "ArgParsers.java",
        "Main.java",
        "Runner.java",
    ],
    plugins = [
        "//:lombok",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":changelog_configuration",
        ":changelog_proto_java",
        ":flags_proto_java",
        "//src/main/java/com/evertz/devtools/changelog/compiler",
        "//src/main/java/com/evertz/devtools/changelog/emitters",
        "//src/main/java/com/evertz/devtools/changelog/platformhost",
        "@com_google_protobuf//:protobuf_java",
        artifact("com.github.zafarkhaja:java-semver"),
        artifact("net.sourceforge.argparse4j:argparse4j"),
        artifact("org.projectlombok:lombok"),
        artifact("com.google.guava:guava"),
    ],
)
