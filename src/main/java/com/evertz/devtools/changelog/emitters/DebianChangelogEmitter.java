package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types.Changelog;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DebianChangelogEmitter extends ChangelogEmitter {

  public DebianChangelogEmitter(EmitterFlags flags) {
    super(flags);
  }

  @Override
  public String emit(Changelog changelog) {
    StringBuilder builder = new StringBuilder();
    changelog.getLogsList()
        .forEach(entry -> {
          emit(entry, builder);
          builder.append(NEW_LINE);
        });

    return builder.toString();
  }

  @Override
  protected String emit(ChangelogEntrySet entrySet, StringBuilder builder) {
    emitHeader(entrySet, builder);

    builder.append(NEW_LINE);
    builder.append(NEW_LINE);

    entrySet.getSectionsList()
        .stream()
        .flatMap(section -> section.getEntriesList().stream())
        .forEach(entry -> {
          emitEntry(entry, builder);
          builder.append(NEW_LINE);
        });

    builder.append(NEW_LINE);

    emitFooter(entrySet, builder);

    builder.append(NEW_LINE);

    return builder.toString();
  }

  @Override
  protected void emitHeader(ChangelogEntrySet set, StringBuilder builder) {
    builder.append(String.format("%s (%s) BIONIC; urgency=medium", set.getComponentName(), set.getVersion()));
  }

  @Override
  protected void emitEntry(ChangelogEntry entry, StringBuilder builder) {
    String line = String.format("  * [%s, %s %s] %s", entry.getScope(), entry.getIncrement(), entry.getType(), entry.getNote());
    builder.append(line);

    if (!entry.getTicket().isEmpty()) {
      builder.append(" Closes: ");
      builder.append(entry.getTicket());
    }
  }

  @Override
  protected void emitFooter(ChangelogEntrySet set, StringBuilder builder) {
    String formattedDate = ZonedDateTime.parse(set.getRelease()).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    String footer = String.format(" -- %s <%s>  %s", set.getOwner(), set.getOwnerEmail(), formattedDate);

    builder.append(footer);
  }
}