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
    name = "scopes",
    srcs = ["Scopes.java"],
)

java_library(
    name = "compiler",
    srcs = [
        "ChangelogCompiler.java",
        "ChangelogParser.java",
        "ChangelogValidator.java",
    ],
    plugins = [
        "//:lombok",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":changelog_proto_java",
        ":flags_proto_java",
        ":scopes",
        "//src/main/java/com/evertz/devtools/changelog/platformhost",
        artifact("com.vladsch.flexmark:flexmark"),
        artifact("com.vladsch.flexmark:flexmark-util"),
        artifact("org.projectlombok:lombok"),
        artifact("com.google.guava:guava"),
        artifact("com.github.zafarkhaja:java-semver"),
    ],
)
