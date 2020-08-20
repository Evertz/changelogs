load("@rules_java//java:defs.bzl", "java_binary", "java_plugin")
load("@rules_jvm_external//:defs.bzl", "artifact")



java_binary(
    name = "changelog",
    data = [
        "src/main/resources/example.md",
    ],
    main_class = "com.evertz.devtools.changelog.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//src/main/java/com/evertz/devtools/changelog",
    ],
)

java_plugin(
    name = "lombok",
    generates_api = 1,
    processor_class = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
    visibility = ["//visibility:public"],
    deps = [
        artifact("org.projectlombok:lombok"),
    ],
)


