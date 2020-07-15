package com.evertz.devtools.changelog.compiler;

import com.evertz.devtools.changelog.Types;
import com.evertz.devtools.changelog.Types.ChangelogSection;
import com.evertz.devtools.changelog.Types.ChangelogEntry;
import com.evertz.devtools.changelog.Types.ChangelogEntrySet;
import com.evertz.devtools.changelog.Types.ChangelogValidationResult;
import com.evertz.devtools.changelog.Types.ChangelogCompileResult;

import com.github.zafarkhaja.semver.Version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ChangelogCompiler {
  private final ChangelogParser parser;
  private final ChangelogValidator validator;

  public ChangelogCompileResult compile(ImmutableSet<String> paths,
                                        ZonedDateTime releaseDateTime,
                                        String baseVersion,
                                        String relativeToDir,
                                        String componentName,
                                        String owner,
                                        String ownerEmail,
                                        boolean isNoAutoVersionIncrement) {
    ImmutableList<ChangelogEntry> entries = parser.parse(paths, relativeToDir);
    ImmutableList<ChangelogValidationResult> changelogValidationResults = validator.validate(entries);

    // build up a section based off the list of entries
    Map<String, ChangelogSection.Builder> sectionsBuilder = new HashMap<>();
    entries.forEach(entry -> {
      String scope = entry.getScope();
      if (!sectionsBuilder.containsKey(scope)) {
        ChangelogSection.Builder sectionBuilder = Types.ChangelogSection.newBuilder().setScope(scope);
        sectionsBuilder.put(scope, sectionBuilder);
      }
      sectionsBuilder.get(scope).addEntries(entry);
    });

    List<String> increments = entries.stream()
        .map(ChangelogEntry::getIncrement)
        .filter(ic -> !"".equals(ic))
        .sorted(Comparator.naturalOrder())
        .collect(Collectors.toList());

    String increment = "patch";
    if (increments.size() > 0) {
      increment = increments.get(0);
    }

    List<ChangelogSection> sections = sectionsBuilder.values()
        .stream()
        .filter(section -> !Scopes.isBreakingOrConfigChangeScope(section.getScope()))
        .map(ChangelogSection.Builder::build)
        .sorted(Comparator.comparing(ChangelogSection::getScope))
        .collect(Collectors.toList());

    String version = isNoAutoVersionIncrement ? baseVersion : incrementVersion(baseVersion, increment).toString();

    ChangelogEntrySet.Builder entrySetBuilder = ChangelogEntrySet.newBuilder()
        .setRelease(releaseDateTime.toString())
        .setBaseVersion(baseVersion)
        .setVersion(version)
        .setComponentName(componentName)
        .setIncrement(isNoAutoVersionIncrement ? "" : increment)
        .setOwner(owner)
        .setOwnerEmail(ownerEmail)
        .addAllSections(sections);

    if (sectionsBuilder.containsKey(Scopes.BREAKING_CHANGE_SCOPE)) {
      entrySetBuilder.setBreakingChanges(sectionsBuilder.get(Scopes.BREAKING_CHANGE_SCOPE));
    }

    if (sectionsBuilder.containsKey(Scopes.CONFIG_CHANGE_SCOPE)) {
      entrySetBuilder.setConfigChanges(sectionsBuilder.get(Scopes.CONFIG_CHANGE_SCOPE));
    }

    return ChangelogCompileResult.newBuilder()
        .setLogs(entrySetBuilder)
        .addAllValidationResult(changelogValidationResults)
        .build();
  }

  private Version incrementVersion(String base, String increment) {
    Version version = Version.valueOf(base);

    switch (increment.toLowerCase()) {
      case "major": return version.incrementMajorVersion();
      case "minor": return version.incrementMinorVersion();
      case "patch": return version.incrementPatchVersion();
    }

    throw new IllegalArgumentException("Unknown increment " + increment);
  }
}
