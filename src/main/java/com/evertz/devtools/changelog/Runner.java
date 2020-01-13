package com.evertz.devtools.changelog;

import com.evertz.devtools.changelog.compiler.ChangelogCompiler;
import com.evertz.devtools.changelog.compiler.ChangelogParser;
import com.evertz.devtools.changelog.compiler.ChangelogValidator;
import com.evertz.devtools.changelog.emitters.*;
import com.evertz.devtools.changelog.platformhost.LocalPlatformHost;
import com.evertz.devtools.changelog.platformhost.PlatformHost;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.TextFormat;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
public class Runner {
  private final PlatformHost platformHost;
  private final Flags.Configuration configuration;
  private final Namespace args;

  public void mergeChangelogAndEmit() {
    List<File> srcs = args.get("srcs");
    List<Types.ChangelogEntrySet> logs;

    logs = srcs.stream()
        .map(File::toPath)
        .map(path -> platformHost.getFileContent(path.toString()))
        .map(proto -> {
          Types.ChangelogEntrySet.Builder entrySetBuilder = Types.ChangelogEntrySet.newBuilder();
          try {
            TextFormat.merge(proto, entrySetBuilder);
          } catch (TextFormat.ParseException e) {
            e.printStackTrace();
            System.exit(1);
          }
          return entrySetBuilder.build();
        })
        .sorted((Types.ChangelogEntrySet entry1, Types.ChangelogEntrySet entry2) -> {
          Version version1 = Version.valueOf(entry1.getVersion());
          Version version2 = Version.valueOf(entry2.getVersion());
          return Version.BUILD_AWARE_ORDER.reversed().compare(version1, version2);
        })
        .collect(Collectors.toList());

    Types.Changelog changelog = Types.Changelog.newBuilder()
        .addAllLogs(logs)
        .build();

    emitChangelogOrEntrySet(changelog, null);
  }

  public void compileChangelogAndEmit() {
    List<File> srcs = args.get("srcs");
    Set<String> files = srcs
        .stream()
        .filter(file -> !"readme.md".equals(file.getName().toLowerCase()) && file.getName().toLowerCase().endsWith(".md"))
        .map(File::getAbsolutePath)
        .collect(Collectors.toSet());

    String version = null;
    try {
      Path path = args.get("version_file");
      version = platformHost.getFileContent(path.toString()).trim();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (Strings.isNullOrEmpty(version)) {
      System.err.println("Unable to determine version info from version_file");
      System.exit(1);
    }

    ChangelogCompiler compiler = getChangelogCompiler();

    Types.ChangelogCompileResult result = compiler.compile(
        ImmutableSet.copyOf(files),
        args.get("date"),
        version,
        configuration.getEmitterFlags().getProject(),
        configuration.getEmitterFlags().getOwner(),
        configuration.getEmitterFlags().getOwnerEmail(),
        args.getBoolean("no_version_increment")
    );

    if (!args.getBoolean("skip_validation")) {
      boolean shouldExit = reportDiagnostics(result.getValidationResultList());
      if (shouldExit) {
        System.exit(1);
      }
    }

    emitChangelogOrEntrySet(null, result.getLogs());
  }

  public void regenerateChangelogAndEmit() {
    PlatformHost host = new LocalPlatformHost();

    File src = args.get("src");
    String proto = host.getFileContent(src.getAbsolutePath());

    if (proto != null && !proto.isEmpty()) {
      try {
        Types.ChangelogEntrySet.Builder entrySetBuilder = Types.ChangelogEntrySet.newBuilder();
        TextFormat.merge(proto, entrySetBuilder);
        Types.ChangelogEntrySet entrySet = entrySetBuilder.build();

        emitChangelogOrEntrySet(null, entrySet);
      } catch (TextFormat.ParseException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  private void emitChangelogOrEntrySet(Types.Changelog changelog, Types.ChangelogEntrySet entrySet) {
    boolean emitToStdout = args.getBoolean("emmit_to_stdout");

    if (args.get("markdown") != null) {
      File file = args.get("markdown");
      emitChangelog(
          new MarkdownChangelogEmitter(configuration.getEmitterFlags()),
          changelog,
          entrySet,
          file.toPath(),
          emitToStdout
      );
    }

    if (args.get("html") != null) {
      File file = args.get("html");
      emitChangelog(
          new HtmlChangelogEmitter(configuration.getEmitterFlags()),
          changelog,
          entrySet,
          file.toPath(),
          emitToStdout
      );
    }

    if (args.get("debian") != null) {
      File file = args.get("debian");
      emitChangelog(
          new DebianChangelogEmitter(configuration.getEmitterFlags()),
          changelog,
          entrySet,
          file.toPath(),
          emitToStdout
      );
    }

    if (args.get("proto") != null) {
      File file = args.get("proto");
      emitChangelog(
          new ProtoChangelogEmitter(configuration.getEmitterFlags()),
          changelog,
          entrySet,
          file.toPath(),
          emitToStdout
      );
    }
  }

  private void emitChangelog(ChangelogEmitter emitter, Types.Changelog changelog, Types.ChangelogEntrySet entrySet,
                             Path out, boolean emitToStdout) {
    String content = changelog != null ? emitter.emit(changelog) : emitter.emit(entrySet);

    if (emitToStdout) System.out.println(content);
    String path = out.toAbsolutePath().toString();
    platformHost.writeFileContent(path, content);
  }

  private ChangelogCompiler getChangelogCompiler() {
    ChangelogParser parser = new ChangelogParser(platformHost);
    ChangelogValidator validator = new ChangelogValidator(configuration.getValidatorFlags());

    return new ChangelogCompiler(parser, validator);
  }

  private boolean reportDiagnostics(List<Types.ChangelogValidationResult> validationResults) {
    final boolean[] exit = {false};

    validationResults
        .stream()
        .flatMap(results -> results.getDiagnosticsList().stream())
        .forEach(diagnostic -> {
          if (diagnostic.getCategory() == Types.ChangelogDiagnostic.Category.ERROR) {
            exit[0] = true;
          }

          System.out.println(diagnostic.getMessage());
        });

    return exit[0];
  }
}
