package com.evertz.devtools.changelog.platformhost;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class LocalPlatformHost implements PlatformHost {

  @Override
  public String getFileContent(String path) {
    try {
      byte[] bytes = Files.readAllBytes(Paths.get(path));
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void writeFileContent(String path, String content) {
    try {
      File file = new File(path);
      file.getParentFile().mkdirs();

      Files.write(file.toPath(), content.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
