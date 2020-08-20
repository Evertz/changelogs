load("@rules_java//java:defs.bzl", "java_library")
load("@rules_jvm_external//:defs.bzl", "artifact")

java_library(
    name = "platformhost",
    srcs = glob(["*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        artifact("com.google.guava:guava"),
    ],
)
