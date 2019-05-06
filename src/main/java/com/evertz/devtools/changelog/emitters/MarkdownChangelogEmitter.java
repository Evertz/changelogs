package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;
import com.evertz.devtools.changelog.Types.ChangelogSection;

public class MarkdownChangelogEmitter extends ChangelogEmitter {

  public MarkdownChangelogEmitter(EmitterFlags flags) {
    super(flags);
  }

  @Override
  protected void emitHeader(ChangelogEntrySet set, StringBuilder builder) {
    builder.append(
        heading(2,
            String.format("%s %s (%s)", set.getComponentName(), set.getVersion(), formatHumanReadableDate(set.getRelease()))
        )
    );
  }

  @Override
  protected void emitLegend(StringBuilder builder) {
    increments.forEach((increment, url) -> {
      String img = String.format("![alt text](%s \"%s\")", increments.get(increment), increment);
      println(img, builder);
      println(increment, builder);
    });
  }

  @Override
  protected void emitSectionHeading(String scope, StringBuilder builder) {
    builder.append(heading(3, scope));
  }

  @Override
  protected void emitEntry(ChangelogEntry entry, StringBuilder builder) {
    builder.append("* ");

    // increment
    String img = String.format("![alt text](%s \"%s\")", increments.get(entry.getIncrement()), entry.getIncrement());
    builder.append(img);

    // type
    String type = String.format(" __%s__: ", entry.getType());
    builder.append(type);

    // ticket
    if (!entry.getTicket().isEmpty()) {
      String ticket = String.format("[%s](%s%s) ", entry.getTicket(), flags.getTicketBaseUrl(), entry.getTicket());
      builder.append(ticket);
    }

    // note
    builder.append(entry.getNote());
  }

  @Override
  protected void emitBreakingOrConfigSection(ChangelogSection section, StringBuilder builder) {
    print(heading(2, section.getScope()), builder);
    section.getEntriesList().forEach(log -> {
      builder.append(NEW_LINE);
      builder.append(String.format("* %s", log.getNote()));
    });
  }

  private String heading(int level, String title) {
    return new String(new char[level]).replace("\0", "#") + " " + title;
  }
}