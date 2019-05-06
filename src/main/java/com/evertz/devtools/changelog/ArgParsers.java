package com.evertz.devtools.changelog;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.nio.file.Paths;
import java.time.ZonedDateTime;

final class ArgParsers {

  private ArgParsers() {}

  static Namespace setupParseOrFail(String ...args) {
    ArgumentParser argumentParser = ArgumentParsers.newFor("changelog")
        .fromFilePrefix("@")
        .build()
        .defaultHelp(true);

    compileCommandArgParser(argumentParser);
    regenCommandPickArgParser(argumentParser);
    mergeCommandArgParser(argumentParser);

    return argumentParser.parseArgsOrFail(args);
  }

  private static Subparser compileCommandArgParser(ArgumentParser argumentParser) {
    Subparser subparser = argumentParser.addSubparsers()
        .dest("command")
        .metavar("command")
        .addParser("compile")
        .help("Compiles a set of changelog entries into a single changelog file");

    subparser.addArgument("version")
        .help("base version to base this changelog, ie: the version the resulting increment is applied to");

    subparser.addArgument("srcs")
        .nargs("+")
        .type(Arguments.fileType())
        .help("list of source markdown files to compile");

    subparser.addArgument("--date")
        .metavar("iso")
        .type((parser, argument, value) -> ZonedDateTime.parse(value))
        .setDefault(ZonedDateTime.now())
        .help("optional override for the release date, formatted as ISO8601");

    subparser.addArgument("--skip-validation")
        .action(Arguments.storeTrue())
        .setDefault(false)
        .help("skips the validation stage of the changelog generation");

    subparser.addArgument("--no-version-increment")
        .action(Arguments.storeTrue())
        .setDefault(false)
        .help("don't increment the version automatically, use version as final");

    addEmitArgs(subparser);
    addCommonArgs(subparser);

    return subparser;
  }

  private static void regenCommandPickArgParser(ArgumentParser argumentParser) {
    Subparser subparser = argumentParser.addSubparsers()
        .dest("command")
        .metavar("command")
        .addParser("regen")
        .help("Regenerate an archived changelog and emits it in the given format");

    subparser.addArgument("src")
        .type(Arguments.fileType())
        .help("archive proto file to regenerate");

    addEmitArgs(subparser);
    addCommonArgs(subparser);
  }

  private static void mergeCommandArgParser(ArgumentParser argumentParser) {
    Subparser subparser = argumentParser.addSubparsers()
        .dest("command")
        .metavar("command")
        .addParser("merge")
        .help("Merges changelog entry sets into a changelog archive");

    subparser.addArgument("srcs")
        .nargs("+")
        .type(Arguments.fileType())
        .help("list of archive proto files to merge");

    addEmitArgs(subparser);
    addCommonArgs(subparser);
  }

  private static void addCommonArgs(Subparser subparser) {
    subparser.addArgument("--conf")
        .type((parser, arg, value) -> Paths.get(value))
        .setDefault(Paths.get(System.getProperty("user.dir"), "changelog.conf"))
        .help("optional override for the configuration file, defaults to 'changelog.conf' in the current working directory");

    subparser.addArgument("--conf_name")
        .setDefault("default")
        .help("name of the configuration set to use");

    ArgumentGroup group = subparser.addArgumentGroup("Debug")
        .description("Debug options");

    group.addArgument("--verbose", "-v")
        .action(Arguments.count());
  }

  private static void addEmitArgs(Subparser subparser) {
    ArgumentGroup group = subparser.addArgumentGroup("Emit Flags")
        .description("Flags related to changelog emitters");

    group.addArgument("--markdown")
        .metavar("path")
        .type(Arguments.fileType())
        .help("emit markdown file to the given path");

    group.addArgument("--debian")
        .metavar("path")
        .type(Arguments.fileType())
        .help("emit debian file to the given path");

    group.addArgument("--html")
        .metavar("path")
        .type(Arguments.fileType())
        .help("emit html file to the given path");

    group.addArgument("--proto")
        .metavar("path")
        .type(Arguments.fileType())
        .help("emit proto file to the given path");

    group.addArgument("--emmit-to-stdout")
        .action(Arguments.storeTrue())
        .setDefault(false)
        .help("emits the changelog to stdout");

    group.addArgument("--emit-to-remote-haystack-host")
        .metavar("host:port")
        .help("the remote haystack server to emit the changelog to, " +
            "expects a component matching this project name and version to already exist");
  }

  private static void addOverrideArgs(Subparser subparser) {
    ArgumentGroup group = subparser.addArgumentGroup("Config overrides")
        .description("Options for overriding config options");

    group.addArgument("--override-owner")
        .help("overrides the owner set in configuration");

    group.addArgument("--override-owner-email")
        .help("overrides the owner email set in configuration");

    group.addArgument("--override-project-name")
        .help("overrides the project name set in configuration");

    group.addArgument("--override-ticket-base-url")
        .help("overrides the ticket base url set in configuration");
  }

}