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
        "ChangelogValidator.java"
    ],
    plugins = [
        "//:lombok",
    ],
    deps = [
        ":changelog_proto_java",
        ":flags_proto_java",
        ":scopes",

        "//src/main/java/com/evertz/devtools/changelog/platformhost",

        "//3rdparty/jvm/com/vladsch/flexmark",
        "//3rdparty/jvm/com/vladsch/flexmark:flexmark_util",
        "//3rdparty/jvm/org/projectlombok:lombok",
        "//3rdparty/jvm/com/google/guava",
        "//3rdparty/jvm/com/github/zafarkhaja:java_semver",
    ],
    visibility = ["//visibility:public"]
)
