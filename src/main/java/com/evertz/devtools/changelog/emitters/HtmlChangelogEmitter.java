package com.evertz.devtools.changelog.emitters;

import com.evertz.devtools.changelog.Flags.EmitterFlags;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.evertz.devtools.changelog.Types.ChangelogSection;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;

public class HtmlChangelogEmitter extends ChangelogEmitter {

  public HtmlChangelogEmitter(EmitterFlags flags) {
    super(flags);
  }

  @Override
  protected void emitHeader(ChangelogEntrySet set, StringBuilder builder) {
    builder.append(
        heading(2,
            String.format("%s (%s)", set.getVersion(), formatHumanReadableDate(set.getRelease()))
        )
    );
  }

  @Override
  protected void emitBreakingOrConfigSection(ChangelogSection section, StringBuilder builder) {
    print(heading(2, section.getScope()), builder);
    section.getEntriesList().forEach(log -> {
      builder.append(NEW_LINE);
      builder.append("<p style=\"margin-left: 12px\">");
      builder.append(log.getNote());
      builder.append("</p>");
    });
  }

  @Override
  protected void emitSectionHeading(String scope, StringBuilder builder) {
    builder.append(heading(3, scope));
  }

  @Override
  protected void emitEntry(ChangelogEntry entry, StringBuilder builder) {
    builder.append("<p style=\"margin-left: 12px\">");

    // increment
    String increment = String.format("<img src=\"%s\" alt=\"%s\">", increments.get(entry.getIncrement()), entry.getIncrement());
    builder.append(increment);

    // type
    String type = String.format("<b>%s</b>: ", entry.getType());
    builder.append(type);

    //ticket
    if (!entry.getTicket().isEmpty()) {
      String ticket = String.format("<a href=\"%s%s\">%s</a> ", flags.getTicketBaseUrl(), entry.getTicket(), entry.getTicket());
      builder.append(ticket);
    }

    // note
    builder.append(entry.getNote());
    builder.append("</p>");
  }

  @Override
  protected void emitLegend(StringBuilder builder) {
    builder.append("<div align=\"right\">");
    increments.forEach((increment, url) -> {
      builder.append(NEW_LINE);
      String img = String.format("  <img src=\"%s\" alt=\"%s\">", url, increment);
      builder.append(img);
      builder.append(increment);
    });
    builder.append(NEW_LINE);
    builder.append("</div>");
  }

  private String heading(int level, String text) {
    return String.format("<h%d>%s</h%d>", level, text, level);
  }
}