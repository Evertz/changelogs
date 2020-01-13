workspace(name = "changelog")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

skylib_version = "0.8.0"
http_archive(
    name = "bazel_skylib",
    type = "tar.gz",
    url = "https://github.com/bazelbuild/bazel-skylib/releases/download/{}/bazel-skylib.{}.tar.gz".format(skylib_version, skylib_version),
    sha256 = "2ef429f5d7ce7111263289644d233707dba35e39696377ebab8b0bc701f7818e",
)

git_repository(
    name = "com_google_protobuf",
    remote = "https://github.com/protocolbuffers/protobuf.git",
    commit = "3e1bd5b81e6d3d806c8b7e1282face57ef7bb0be",
)

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-2.9",
    sha256 = "e5b97a31a3e8feed91636f42e19b11c49487b85e5de2f387c999ea14d77c7f45",
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/2.9.zip",
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")

protobuf_deps()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "org.projectlombok:lombok:1.18.4",
        "com.github.zafarkhaja:java-semver:0.9.0",
        "net.sourceforge.argparse4j:argparse4j:0.8.1",
        "com.google.guava:guava:27.0.1-jre",
        "com.vladsch.flexmark:flexmark:0.40.16",
        "com.vladsch.flexmark:flexmark-util:0.40.16",
        "com.vladsch.flexmark:flexmark-formatter:0.40.16",
    ],
    repositories = [
        "https://jcenter.bintray.com/",
        "https://repo.maven.apache.org/maven2"
    ],
)
