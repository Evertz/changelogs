package com.evertz.devtools.changelog.compiler;

import com.evertz.devtools.changelog.Flags.ValidatorFlags;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.evertz.devtools.changelog.Types.ChangelogDiagnostic;
import com.evertz.devtools.changelog.Types.ChangelogValidationResult;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import java.util.stream.Collectors;
import java.util.List;

/**
 * Validates a list of changelog entries against a given configuration
 */
public final class ChangelogValidator {
  private final ValidatorFlags flags;

  private final String validScopesJoined;
  private final String validTypesJoined;
  private final String validIncrementsJoined;

  public ChangelogValidator(@NonNull ValidatorFlags flags) {
    this.flags = flags;

    validScopesJoined = Joiner.on(", ").join(flags.getScopesList());
    validTypesJoined = Joiner.on(", ").join(flags.getTypesList());
    validIncrementsJoined = Joiner.on(", ").join(flags.getIncrementsList());
  }

  public ImmutableList<ChangelogValidationResult> validate(@NonNull ImmutableList<ChangelogEntry> entries) {
    List<ChangelogValidationResult> resultList = entries
        .stream()
        .map(this::validate)
        .collect(Collectors.toList());

    return ImmutableList.copyOf(resultList);
  }

  public ChangelogValidationResult validate(@NonNull ChangelogEntry entry) {
    ChangelogValidationResult.Builder validationBuilder = ChangelogValidationResult
        .newBuilder()
        .setLog(entry);

    ImmutableList.Builder<ChangelogDiagnostic> diagnosticsBuilder = new ImmutableList.Builder<>();

    ChangelogDiagnostic scopeDiagnostic = validateScope(entry);
    addDiagnosticAndSetFlags(scopeDiagnostic, diagnosticsBuilder, validationBuilder);

    ChangelogDiagnostic typeDiagnostic = validateType(entry);
    addDiagnosticAndSetFlags(typeDiagnostic, diagnosticsBuilder, validationBuilder);

    ChangelogDiagnostic incrementDiagnostic = validateIncrement(entry);
    addDiagnosticAndSetFlags(incrementDiagnostic, diagnosticsBuilder, validationBuilder);

    ChangelogDiagnostic ticketDiagnostic = validateTicket(entry);
    addDiagnosticAndSetFlags(ticketDiagnostic, diagnosticsBuilder, validationBuilder);

    ChangelogDiagnostic noteDiagnostic = validateNote(entry);
    addDiagnosticAndSetFlags(noteDiagnostic, diagnosticsBuilder, validationBuilder);

    validationBuilder.addAllDiagnostics(diagnosticsBuilder.build());

    return validationBuilder.build();
  }

  private void addDiagnosticAndSetFlags(ChangelogDiagnostic diagnostic,
                                        ImmutableList.Builder<ChangelogDiagnostic> diagnosticsBuilder,
                                        ChangelogValidationResult.Builder validationBuilder) {
    if (diagnostic == null) { return; }

    diagnosticsBuilder.add(diagnostic);
    if (diagnostic.getIsError()) {
      validationBuilder.setHasErrors(true);
    }
  }

  private ChangelogDiagnostic validateScope(ChangelogEntry entry) {
    if (Scopes.isBreakingOrConfigChangeScope(entry.getScope())) { return null; }
    if (flags.getScopesCount() == 0) { return null; }
    if (flags.getScopesList().contains(entry.getScope().toLowerCase())) { return null; }

    String formattedMessage = String.format(
        "Changelog entry on line %d in '%s' has an invalid scope of '%s'. Valid scopes are '%s'",
        entry.getSource().getLine(),
        entry.getSource().getFile(),
        entry.getScope(),
        validScopesJoined
    );

    return reportDiagnostic(formattedMessage, ChangelogDiagnostic.Category.ERROR);
  }

  private ChangelogDiagnostic validateType(ChangelogEntry entry) {
    if (Scopes.isBreakingOrConfigChangeScope(entry.getScope())) { return null; }
    if (flags.getTypesCount() == 0) { return null; }
    if (flags.getTypesList().contains(entry.getType().toLowerCase())) { return null; }

    String formattedMessage = String.format(
        "Changelog entry on line %d in '%s' has an invalid type of '%s'. Valid types are '%s'",
        entry.getSource().getLine(),
        entry.getSource().getFile(),
        entry.getType(),
        validTypesJoined
    );

    return reportDiagnostic(formattedMessage, ChangelogDiagnostic.Category.ERROR);
  }

  private ChangelogDiagnostic validateIncrement(ChangelogEntry entry) {
    if (Scopes.isBreakingOrConfigChangeScope(entry.getScope())) { return null; }
    if (flags.getIncrementsCount() == 0) { return null; }
    if (flags.getIncrementsList().contains(entry.getIncrement().toLowerCase())) { return null; }

    String formattedMessage = String.format(
        "Changelog entry on line %d in '%s' has an invalid increment of '%s'. Valid increments are '%s'",
        entry.getSource().getLine(),
        entry.getSource().getFile(),
        entry.getIncrement(),
        validIncrementsJoined
    );

    return reportDiagnostic(formattedMessage, ChangelogDiagnostic.Category.ERROR);
  }

  private ChangelogDiagnostic validateTicket(ChangelogEntry entry) {
    if (flags.getAllowBlankTicket()) { return null; }
    if (!Strings.isNullOrEmpty(entry.getTicket()) || flags.getAllowBlankTicket()) { return null; }

    String formattedMessage = String.format(
        "Changelog entry on line %d in '%s' is missing a ticket reference",
        entry.getSource().getLine(),
        entry.getSource().getFile()
    );

    return reportDiagnostic(formattedMessage, ChangelogDiagnostic.Category.ERROR);
  }

  private ChangelogDiagnostic validateNote(ChangelogEntry entry) {
    if (!Strings.isNullOrEmpty(entry.getNote())) { return null; }
    if (!Strings.isNullOrEmpty(entry.getTicket()) || flags.getAllowBlankTicket()) { return null; }

    String formattedMessage = String.format(
        "Changelog entry on line %d in '%s' has a blank note",
        entry.getSource().getLine(),
        entry.getSource().getFile()
    );

    return reportDiagnostic(formattedMessage, ChangelogDiagnostic.Category.ERROR);
  }

  private ChangelogDiagnostic reportDiagnostic(String message, ChangelogDiagnostic.Category category) {
    return ChangelogDiagnostic.newBuilder()
        .setCategory(category)
        .setMessage(message)
        .setIsError(category == ChangelogDiagnostic.Category.ERROR)
        .build();
  }

}