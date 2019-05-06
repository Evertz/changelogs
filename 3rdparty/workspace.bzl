# Do not edit. bazel-deps autogenerates this file from +public/external_jvm_deps.yaml.
def _jar_artifact_impl(ctx):
    jar_name = "%s.jar" % ctx.name
    ctx.download(
        output=ctx.path("jar/%s" % jar_name),
        url=ctx.attr.urls,
        sha256=ctx.attr.sha256,
        executable=False
    )
    src_name="%s-sources.jar" % ctx.name
    srcjar_attr=""
    has_sources = len(ctx.attr.src_urls) != 0
    if has_sources:
        ctx.download(
            output=ctx.path("jar/%s" % src_name),
            url=ctx.attr.src_urls,
            sha256=ctx.attr.src_sha256,
            executable=False
        )
        srcjar_attr ='\n    srcjar = ":%s",' % src_name

    build_file_contents = """
package(default_visibility = ['//visibility:public'])
java_import(
    name = 'jar',
    tags = ['maven_coordinates={artifact}'],
    jars = ['{jar_name}'],{srcjar_attr}
)
filegroup(
    name = 'file',
    srcs = [
        '{jar_name}',
        '{src_name}'
    ],
    visibility = ['//visibility:public']
)\n""".format(artifact = ctx.attr.artifact, jar_name = jar_name, src_name = src_name, srcjar_attr = srcjar_attr)
    ctx.file(ctx.path("jar/BUILD"), build_file_contents, False)
    return None

jar_artifact = repository_rule(
    attrs = {
        "artifact": attr.string(mandatory = True),
        "sha256": attr.string(mandatory = True),
        "urls": attr.string_list(mandatory = True),
        "src_sha256": attr.string(mandatory = False, default=""),
        "src_urls": attr.string_list(mandatory = False, default=[]),
    },
    implementation = _jar_artifact_impl
)

def jar_artifact_callback(hash):
    src_urls = []
    src_sha256 = ""
    source=hash.get("source", None)
    if source != None:
        src_urls = [source["url"]]
        src_sha256 = source["sha256"]
    jar_artifact(
        artifact = hash["artifact"],
        name = hash["name"],
        urls = [hash["url"]],
        sha256 = hash["sha256"],
        src_urls = src_urls,
        src_sha256 = src_sha256
    )
    native.bind(name = hash["bind"], actual = hash["actual"])


def list_dependencies():
    return [
    {"artifact": "com.github.zafarkhaja:java-semver:0.9.0", "lang": "java", "sha1": "59a83ca73c72a5e25b3f0b1bb305230a11000329", "sha256": "2218c73b40f9af98b570d084420c1b4a81332297bd7fc27ddd552e903be8e93c", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/github/zafarkhaja/java-semver/0.9.0/java-semver-0.9.0.jar", "source": {"sha1": "38d5bf2aa1204792794489a0e2c4c19548bc35c1", "sha256": "9fc828d4be717d5c899ecf5a23e94c915bc6ab728dcfe261ef7f4ab338b3ff93", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/github/zafarkhaja/java-semver/0.9.0/java-semver-0.9.0-sources.jar"} , "name": "com_github_zafarkhaja_java_semver", "actual": "@com_github_zafarkhaja_java_semver//jar", "bind": "jar/com/github/zafarkhaja/java_semver"},
    {"artifact": "com.google.code.findbugs:jsr305:3.0.2", "lang": "java", "sha1": "25ea2e8b0c338a877313bd4672d3fe056ea78f0d", "sha256": "766ad2a0783f2687962c8ad74ceecc38a28b9f72a2d085ee438b7813e928d0c7", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar", "source": {"sha1": "b19b5927c2c25b6c70f093767041e641ae0b1b35", "sha256": "1c9e85e272d0708c6a591dc74828c71603053b48cc75ae83cce56912a2aa063b", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2-sources.jar"} , "name": "com_google_code_findbugs_jsr305", "actual": "@com_google_code_findbugs_jsr305//jar", "bind": "jar/com/google/code/findbugs/jsr305"},
    {"artifact": "com.google.errorprone:error_prone_annotations:2.2.0", "lang": "java", "sha1": "88e3c593e9b3586e1c6177f89267da6fc6986f0c", "sha256": "6ebd22ca1b9d8ec06d41de8d64e0596981d9607b42035f9ed374f9de271a481a", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/errorprone/error_prone_annotations/2.2.0/error_prone_annotations-2.2.0.jar", "source": {"sha1": "a8cd7823aa1dcd2fd6677c0c5988fdde9d1fb0a3", "sha256": "626adccd4894bee72c3f9a0384812240dcc1282fb37a87a3f6cb94924a089496", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/errorprone/error_prone_annotations/2.2.0/error_prone_annotations-2.2.0-sources.jar"} , "name": "com_google_errorprone_error_prone_annotations", "actual": "@com_google_errorprone_error_prone_annotations//jar", "bind": "jar/com/google/errorprone/error_prone_annotations"},
    {"artifact": "com.google.guava:failureaccess:1.0.1", "lang": "java", "sha1": "1dcf1de382a0bf95a3d8b0849546c88bac1292c9", "sha256": "a171ee4c734dd2da837e4b16be9df4661afab72a41adaf31eb84dfdaf936ca26", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar", "source": {"sha1": "1d064e61aad6c51cc77f9b59dc2cccc78e792f5a", "sha256": "092346eebbb1657b51aa7485a246bf602bb464cc0b0e2e1c7e7201fadce1e98f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1-sources.jar"} , "name": "com_google_guava_failureaccess", "actual": "@com_google_guava_failureaccess//jar", "bind": "jar/com/google/guava/failureaccess"},
    {"artifact": "com.google.guava:guava:27.0.1-jre", "lang": "java", "sha1": "bd41a290787b5301e63929676d792c507bbc00ae", "sha256": "e1c814fd04492a27c38e0317eabeaa1b3e950ec8010239e400fe90ad6c9107b4", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/guava/guava/27.0.1-jre/guava-27.0.1-jre.jar", "source": {"sha1": "cb5c1119df8d41a428013289b193eba3ccaf5f60", "sha256": "cba2e5680186062f42998b895a5e9a9ceccbaab94644ccc9f35bb73c2b2c7d8e", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/guava/guava/27.0.1-jre/guava-27.0.1-jre-sources.jar"} , "name": "com_google_guava_guava", "actual": "@com_google_guava_guava//jar", "bind": "jar/com/google/guava/guava"},
    {"artifact": "com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava", "lang": "java", "sha1": "b421526c5f297295adef1c886e5246c39d4ac629", "sha256": "b372a037d4230aa57fbeffdef30fd6123f9c0c2db85d0aced00c91b974f33f99", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar", "name": "com_google_guava_listenablefuture", "actual": "@com_google_guava_listenablefuture//jar", "bind": "jar/com/google/guava/listenablefuture"},
    {"artifact": "com.google.j2objc:j2objc-annotations:1.1", "lang": "java", "sha1": "ed28ded51a8b1c6b112568def5f4b455e6809019", "sha256": "2994a7eb78f2710bd3d3bfb639b2c94e219cedac0d4d084d516e78c16dddecf6", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar", "source": {"sha1": "1efdf5b737b02f9b72ebdec4f72c37ec411302ff", "sha256": "2cd9022a77151d0b574887635cdfcdf3b78155b602abc89d7f8e62aba55cfb4f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1-sources.jar"} , "name": "com_google_j2objc_j2objc_annotations", "actual": "@com_google_j2objc_j2objc_annotations//jar", "bind": "jar/com/google/j2objc/j2objc_annotations"},
    {"artifact": "com.googlecode.protobuf-java-format:protobuf-java-format:1.4", "lang": "java", "sha1": "b8163b6940102c1808814471476f5293dfb419df", "sha256": "80fd09b87c569f2ba76837dcd9096edbbca74246c00726b0e596ab474308eb3c", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/googlecode/protobuf-java-format/protobuf-java-format/1.4/protobuf-java-format-1.4.jar", "source": {"sha1": "4a8fd054855f4045307d0d8608e3d9ee335731d5", "sha256": "641205a83237985680367f96aba0a64f24d6b6307160b73e5abc78e67a649437", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/googlecode/protobuf-java-format/protobuf-java-format/1.4/protobuf-java-format-1.4-sources.jar"} , "name": "com_googlecode_protobuf_java_format_protobuf_java_format", "actual": "@com_googlecode_protobuf_java_format_protobuf_java_format//jar", "bind": "jar/com/googlecode/protobuf_java_format/protobuf_java_format"},
    {"artifact": "com.vladsch.flexmark:flexmark-formatter:0.40.16", "lang": "java", "sha1": "736b62606c3189e941a7a2f0adcbf0e5136abe0d", "sha256": "711996982769c7c500081f83b960261e70ebccd5f9b9f08555f9f75c09be229f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark-formatter/0.40.16/flexmark-formatter-0.40.16.jar", "source": {"sha1": "eed05605f327e5c15203ba04fff32e6980eb21b7", "sha256": "3b24cbf54c3d090d170983ec521d52fe319dd3b28dd8078da39ef97e5db9e0be", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark-formatter/0.40.16/flexmark-formatter-0.40.16-sources.jar"} , "name": "com_vladsch_flexmark_flexmark_formatter", "actual": "@com_vladsch_flexmark_flexmark_formatter//jar", "bind": "jar/com/vladsch/flexmark/flexmark_formatter"},
    {"artifact": "com.vladsch.flexmark:flexmark-util:0.40.16", "lang": "java", "sha1": "473c9172d664dcc90c8874593905a59a5cdae81e", "sha256": "4efcbe96df1d6698748d4f15659857673f8cf8368015f42f48c4e93a2ac7edc5", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark-util/0.40.16/flexmark-util-0.40.16.jar", "source": {"sha1": "88293c3e0214af6599c77dbc74bebd321b0cd107", "sha256": "0befd6dd74a2a326123a6707b3fac7044948f0809c1995b6ea653f5a63bca826", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark-util/0.40.16/flexmark-util-0.40.16-sources.jar"} , "name": "com_vladsch_flexmark_flexmark_util", "actual": "@com_vladsch_flexmark_flexmark_util//jar", "bind": "jar/com/vladsch/flexmark/flexmark_util"},
    {"artifact": "com.vladsch.flexmark:flexmark:0.40.16", "lang": "java", "sha1": "d79b4de8af7c5faebef56b9653eeef05f9451cb0", "sha256": "cb583886cb40b2f98dc6b3ecf7e443ed9c29344b78bd7e16435f770c8827d8a2", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark/0.40.16/flexmark-0.40.16.jar", "source": {"sha1": "5044e44e08e47d9704f0ce57f33f524fe803070d", "sha256": "ada20ce8ed25cf51e30e64b6129f92b592c39fffddbfdf837653220b3f8cf1b0", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/vladsch/flexmark/flexmark/0.40.16/flexmark-0.40.16-sources.jar"} , "name": "com_vladsch_flexmark_flexmark", "actual": "@com_vladsch_flexmark_flexmark//jar", "bind": "jar/com/vladsch/flexmark/flexmark"},
    {"artifact": "net.sourceforge.argparse4j:argparse4j:0.8.1", "lang": "java", "sha1": "2c8241f84acf6c924bd75be0dbd68e8d74fbcd70", "sha256": "98cb5468cac609f3bc07856f2e34088f50dc114181237c48d20ca69c3265d044", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/sourceforge/argparse4j/argparse4j/0.8.1/argparse4j-0.8.1.jar", "source": {"sha1": "779289966bb88f72751923bf2990ddde7f7a6507", "sha256": "6baf8893d69bf3b8cac582de8b6407ebfeac992b1694b11897a9a614fb4b892f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/sourceforge/argparse4j/argparse4j/0.8.1/argparse4j-0.8.1-sources.jar"} , "name": "net_sourceforge_argparse4j_argparse4j", "actual": "@net_sourceforge_argparse4j_argparse4j//jar", "bind": "jar/net/sourceforge/argparse4j/argparse4j"},
    {"artifact": "org.checkerframework:checker-qual:2.5.2", "lang": "java", "sha1": "cea74543d5904a30861a61b4643a5f2bb372efc4", "sha256": "64b02691c8b9d4e7700f8ee2e742dce7ea2c6e81e662b7522c9ee3bf568c040a", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/2.5.2/checker-qual-2.5.2.jar", "source": {"sha1": "ebb8ebccd42218434674f3e1d9022c13df1c19f8", "sha256": "821c5c63a6f156a3bb498c5bbb613580d9d8f4134131a5627d330fc4018669d2", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/checkerframework/checker-qual/2.5.2/checker-qual-2.5.2-sources.jar"} , "name": "org_checkerframework_checker_qual", "actual": "@org_checkerframework_checker_qual//jar", "bind": "jar/org/checkerframework/checker_qual"},
    {"artifact": "org.codehaus.mojo:animal-sniffer-annotations:1.17", "lang": "java", "sha1": "f97ce6decaea32b36101e37979f8b647f00681fb", "sha256": "92654f493ecfec52082e76354f0ebf87648dc3d5cec2e3c3cdb947c016747a53", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/codehaus/mojo/animal-sniffer-annotations/1.17/animal-sniffer-annotations-1.17.jar", "source": {"sha1": "8fb5b5ad9c9723951b9fccaba5bb657fa6064868", "sha256": "2571474a676f775a8cdd15fb9b1da20c4c121ed7f42a5d93fca0e7b6e2015b40", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/codehaus/mojo/animal-sniffer-annotations/1.17/animal-sniffer-annotations-1.17-sources.jar"} , "name": "org_codehaus_mojo_animal_sniffer_annotations", "actual": "@org_codehaus_mojo_animal_sniffer_annotations//jar", "bind": "jar/org/codehaus/mojo/animal_sniffer_annotations"},
    {"artifact": "org.projectlombok:lombok:1.18.4", "lang": "java", "sha1": "7103ab519b1cdbb0642ad4eaf1db209d905d0f96", "sha256": "39f3922deb679b1852af519eb227157ef2dd0a21eec3542c8ce1b45f2df39742", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/projectlombok/lombok/1.18.4/lombok-1.18.4.jar", "source": {"sha1": "ff45275b27cd70c701abed8e53e3b9f20806345b", "sha256": "63a552ea5eca60bbc288959c3c10d8b8b888954c4b05d5a585821ffd3faf52b8", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/projectlombok/lombok/1.18.4/lombok-1.18.4-sources.jar"} , "name": "org_projectlombok_lombok", "actual": "@org_projectlombok_lombok//jar", "bind": "jar/org/projectlombok/lombok"},
    ]

def maven_dependencies(callback = jar_artifact_callback):
    for hash in list_dependencies():
        callback(hash)
