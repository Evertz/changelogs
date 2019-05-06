package com.evertz.devtools.changelog;

import com.evertz.devtools.changelog.platformhost.PlatformHost;
import com.google.common.base.Strings;
import com.google.protobuf.TextFormat;

import java.util.HashMap;
import java.util.Map;

public final class ChangelogConfigurationLoader {
  private ChangelogConfigurationLoader() {}

  public static Flags.Configurations fromFile(PlatformHost host, String path) {
    String protoTextFormatConf = host.getFileContent(path);

    if (Strings.isNullOrEmpty(protoTextFormatConf)) {
      return null;
    }

    try {
      Flags.Configurations.Builder configurations = Flags.Configurations.newBuilder();
      TextFormat.merge(protoTextFormatConf, configurations);

      return configurations.build();
    } catch (TextFormat.ParseException e) {
      return null;
    }
  }

  public static Flags.Configuration configurationFromFile(PlatformHost host, String path, String name) {
    Flags.Configurations configurations = ChangelogConfigurationLoader.fromFile(host, path);
    if (configurations == null) { return  null; }

    return configurations.getConfigurationsOrDefault(name, null);
  }
}