package com.evertz.devtools.changelog.platformhost;

public interface PlatformHost {
  String getFileContent(String path);

  void writeFileContent(String path, String content);
}
