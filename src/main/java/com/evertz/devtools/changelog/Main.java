package com.evertz.devtools.changelog;

import com.evertz.devtools.changelog.platformhost.LocalPlatformHost;
import com.evertz.devtools.changelog.platformhost.PlatformHost;
import net.sourceforge.argparse4j.inf.Namespace;

import java.nio.file.Path;

public class Main {
  public static void main(String... args) {
    Namespace namespace = ArgParsers.setupParseOrFail(args);

    if (namespace.getInt("verbose") > 0) {
      System.out.println(namespace);
    }

    Path path = namespace.get("conf");
    String confName = namespace.getString("conf_name");

    PlatformHost host = new LocalPlatformHost();
    Flags.Configuration configuration = ChangelogConfigurationLoader.configurationFromFile(host, path.toString(), confName);

    if (configuration == null) {
      System.err.println(String.format("Configuration '%s' was not found", confName));
      System.exit(1);
    }

    Runner runner = Runner.builder()
        .configuration(configuration)
        .platformHost(host)
        .args(namespace)
        .build();

    switch (namespace.getString("command")) {
      case "compile":
        runner.compileChangelogAndEmit();
        break;
      case "regen":
        runner.regenerateChangelogAndEmit();
        break;
      case "merge":
        runner.mergeChangelogAndEmit();
        break;
      default:
        throw new IllegalArgumentException("Unknown command " + namespace.getString("command"));
    }
  }
}