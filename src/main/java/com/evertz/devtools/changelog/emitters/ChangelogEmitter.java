package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types.Changelog;
import com.evertz.devtools.changelog.Types.ChangelogSection;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.google.common.collect.ImmutableMap;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class ChangelogEmitter {
  static final String NEW_LINE = "\n";

  final ImmutableMap<String, String> increments = ImmutableMap.of(
      "major",
      "https://s3.us-east-2.amazonaws.com/mediator-ui-static-assets/changelog-major-12x12.png",
      "minor",
      "https://s3.us-east-2.amazonaws.com/mediator-ui-static-assets/changelog-minor-12x12.png",
      "patch",
      "https://s3.us-east-2.amazonaws.com/mediator-ui-static-assets/changelog-patch-12x12.png"
  );

  protected final EmitterFlags flags;

  ChangelogEmitter(EmitterFlags flags) {
    this.flags = flags;
  }

  public String emit(Changelog changelog) {
    StringBuilder builder = new StringBuilder();
    changelog.getLogsList()
        .forEach(entry -> {
          emit(entry, builder);
          builder.append(NEW_LINE);
          builder.append(NEW_LINE);
        });

    return builder.toString();
  }

  public String emit(ChangelogEntrySet changelogEntrySet) {
    StringBuilder builder = new StringBuilder();
    return emit(changelogEntrySet, builder);
  }

  protected String emit(ChangelogEntrySet changelogEntrySet, StringBuilder builder) {
    List<ChangelogSection> sections = changelogEntrySet.getSectionsList();
    if (flags.getEmitLegend()) {
      emitLegend(builder);
      print(NEW_LINE, builder);
    }

    emitHeader(changelogEntrySet, builder);
    println("", builder);

    if (!changelogEntrySet.getBreakingChanges().getScope().isEmpty()) {
      emitBreakingOrConfigSection(changelogEntrySet.getBreakingChanges(), builder);
      println("", builder);
    }

    if (!changelogEntrySet.getConfigChanges().getScope().isEmpty()) {
      emitBreakingOrConfigSection(changelogEntrySet.getConfigChanges(), builder);
      println("", builder);
    }

    sections.forEach(section -> {
      if (section.getScope().isEmpty()) { return; }

      emitSectionHeading(section.getScope(), builder);
      print(NEW_LINE, builder);

      section.getEntriesList()
          .forEach(entry -> {
            emitEntry(entry, builder);
            print(NEW_LINE, builder);
          });
    });

    print(NEW_LINE, builder);
    emitFooter(changelogEntrySet, builder);

    return builder.toString().trim();
  }

  protected void emitLegend(StringBuilder builder) {}

  protected void emitHeader(ChangelogEntrySet set, StringBuilder builder) {}

  protected void emitFooter(ChangelogEntrySet set, StringBuilder builder) {}

  protected void emitBreakingOrConfigSection(ChangelogSection section, StringBuilder builder) {}

  protected void emitSectionHeading(String scope, StringBuilder builder) {}

  protected void emitEntry(ChangelogEntry entry, StringBuilder builder) {}

  protected void println(String text, StringBuilder builder) {
    builder.append(text);
    builder.append(NEW_LINE);
  }

  protected void print(String text, StringBuilder builder) {
    builder.append(text);
  }

  String formatHumanReadableDate(String date) {
    return ZonedDateTime.parse(date)
        .format(DateTimeFormatter.ofPattern("E d MMM uuuu"));
  }
}