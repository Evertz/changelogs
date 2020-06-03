package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;

public class JsonChangelogEmitter extends ChangelogEmitter {

  public JsonChangelogEmitter(EmitterFlags flags) {
    super(flags);
  }

  @SneakyThrows
  @Override
  public String emit(ChangelogEntrySet changelogEntrySet) {
    return JsonFormat.printer().print(changelogEntrySet);
  }

  @SneakyThrows
  @Override
  public String emit(Types.Changelog changelog) {
    return JsonFormat.printer().print(changelog);
  }
}
