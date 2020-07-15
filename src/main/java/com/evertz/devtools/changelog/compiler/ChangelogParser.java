package com.evertz.devtools.changelog.compiler;

import com.evertz.devtools.changelog.platformhost.PlatformHost;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.util.ast.*;
import lombok.NonNull;
import com.vladsch.flexmark.parser.Parser;
import com.evertz.devtools.changelog.Types.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ChangelogParser {
  private final PlatformHost host;
  private final Parser parser;
  private final Comparator<ChangelogEntry> comparator;

  public ChangelogParser(PlatformHost host) {
    this.host = host;
    this.comparator = Comparator
        .comparing(ChangelogEntry::getScope)
        .thenComparing(ChangelogEntry::getIncrement)
        .thenComparing(ChangelogEntry::getType);

    parser = Parser.builder().build();
  }

  public ImmutableList<ChangelogEntry> parse(@NonNull ImmutableSet<String> paths, String relativeToDir) {
    List<ChangelogEntry> entryList = paths
        .stream()
        .map(path -> {
          String content = host.getFileContent(path);
          String[] absolute_path_split = path.split(relativeToDir, 2);
          if (absolute_path_split.length == 2) {
            path = absolute_path_split[1];
            } else if (absolute_path_split.length == 1) {
                throw new IllegalArgumentException("The directory \"" + relativeToDir +  "\" passed via the \"relativeToDir\" command line option is not in the absolute path to the source markdown file: \"" + path + "\"");
            }
          return parseString(content, path);
        })
        .flatMap(List::stream)
        .sorted(comparator)
        .collect(Collectors.toList());

    return ImmutableList.copyOf(entryList);
  }

  public ImmutableList<ChangelogEntry> parseString(@NonNull String source, String filename) {
    Document document = parser.parse(source);
    return collectEntriesFromDocument(document, filename);
  }

  private ImmutableList<ChangelogEntry> collectEntriesFromDocument(Document document, String filename) {
    final ChangelogEntryCollector collector = new ChangelogEntryCollector(document, filename);
    return collector.collectEntries();
  }

  private static final class ChangelogEntryCollector {
    private ChangelogEntry.Builder builder;
    private final ImmutableList.Builder<ChangelogEntry> entries = new ImmutableList.Builder<>();

    private final NodeVisitor visitor = new NodeVisitor(
        new VisitHandler<>(Heading.class, this::visitScope),
        new VisitHandler<>(BulletListItem.class, this::visitItemEntry)
    );

    private final String filename;

    private String scope;

    ChangelogEntryCollector(@NonNull Document document, @NonNull String filename) {
      this.filename = filename;
      visitor.visit(document);
    }

    ImmutableList<ChangelogEntry> collectEntries() {
      return entries.build();
    }

    private void visitScope(Heading heading) {
      if (builder != null) {
        // we've hit a heading inside a note
        // this is fine, but a little awkward
        return;
      }

      Text text = (Text) heading.getFirstChildAny(Text.class);
      if (text != null) {
        scope = text.getChars().unescape();
      }

      visitor.visitChildren(heading);
    }

    private void visitItemEntry(BulletListItem item) {
      if (builder != null) {
        throw new IllegalStateException("ChangelogEntry builder must be null when starting parse");
      }

      builder = ChangelogEntry.newBuilder();

      Paragraph paragraph = (Paragraph) item.getFirstChild();
      Text incrementOrNote = (Text) paragraph.getFirstChildAny(Text.class);

      if (Scopes.isBreakingOrConfigChangeScope(scope)) {
        builder.setScope(scope.toUpperCase());
        // consume the rest as a note
        visitNote(incrementOrNote, builder);
      } else {
        builder.setScope(scope.substring(0, 1).toUpperCase() + scope.substring(1).toLowerCase());
        visitIncrement(incrementOrNote, builder);
      }

      ParsedSource.Builder sourceBuilder = ParsedSource
          .newBuilder()
          .setFile(filename)
          .setLine(item.getLineNumber())
          .setOffset(item.getStartOffset());

      builder.setSource(sourceBuilder);

      entries.add(builder.build());
      builder = null;
    }

    private void visitIncrement(Text increment, ChangelogEntry.Builder builder) {
      if (increment == null) { return; }

      builder.setIncrement(increment.getChars().trim().toString());

      StrongEmphasis type = (StrongEmphasis) increment.getNextAny(StrongEmphasis.class);
      visitType(type, builder);
    }

    private void visitType(StrongEmphasis type, ChangelogEntry.Builder builder) {
      if (type == null) { return; }

      builder.setType(type.getText().trim().unescape());

      maybeVisitTickets(type, builder);
    }

    private void maybeVisitTickets(Node node, ChangelogEntry.Builder builder) {
      if (node == null) { return; }

      // move forward until we hit start start of the note, this is marked by ':'
      Node maybeNoteStart = node;
      boolean isNoteStart = maybeNoteStart.getChars().startsWith(":");

      while (!isNoteStart) {
        maybeNoteStart  = maybeNoteStart.getNextAny(Text.class);

        // reached the end and no : was found, we can't determine where the note starts
        if (maybeNoteStart == null) {
          throw new RuntimeException("Attempted to find start of Changelog notes, but failed. Is the note missing a ':'?");
        }

        isNoteStart = maybeNoteStart.getChars().startsWith(":");
      }

      // we now have the start of the note, look back and find the links
      // this prevents us finding links within the note itself
      Link ticket = (Link) maybeNoteStart.getPreviousAny(Link.class);
      while (ticket != null) {
        visitTicket(ticket, builder);
        ticket = (Link) ticket.getPreviousAny(Link.class);
      }

      // we have the start of the note in maybeNoteStart, consume this, nom nom nom
      visitNote((Text) maybeNoteStart, builder);
    }

    private void visitTicket(Link ticket, ChangelogEntry.Builder builder) {
      if (ticket == null) { return; }
      builder.addTickets(ticket.getText().trim().unescape());
    }

    private void visitNote(Text note, ChangelogEntry.Builder builder) {
      if (note == null) { return; }

      List<String> notes = new ArrayList<>();
      notes.add(note.getChars().trimStart(":").trim().toString());

      Node next = note.getNext();
      while (next != null) {
        notes.add(next.getChars().trim().toString());
        next = next.getNext();
      }

      builder.setNote(Joiner.on(" ").join(notes));
    }
  }
}
