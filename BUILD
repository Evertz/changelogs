java_binary(
    name = "changelog",
    runtime_deps = [
        "//src/main/java/com/evertz/devtools/changelog"
    ],
    data = [
        "src/main/resources/example.md",
    ],
    main_class = "com.evertz.devtools.changelog.Main",
    visibility = ["//visibility:public"],
)

java_plugin(
    name = "lombok",
    processor_class = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
    deps = [
        "//3rdparty/jvm/org/projectlombok:lombok",
    ],
    generates_api = 1,
    visibility = ["//visibility:public"]
)

