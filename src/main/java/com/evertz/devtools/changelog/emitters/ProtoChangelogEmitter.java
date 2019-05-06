package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types.Changelog;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;
import com.google.protobuf.TextFormat;

public class ProtoChangelogEmitter extends ChangelogEmitter {

  public ProtoChangelogEmitter(EmitterFlags flags) {
    super(flags);
  }

  @Override
  public String emit(ChangelogEntrySet changelogEntrySet) {
    return TextFormat.printToString(changelogEntrySet);
  }

  @Override
  public String emit(Changelog changelog) { return TextFormat.printToString(changelog); }
}