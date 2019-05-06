package com.evertz.devtools.changelog.compiler;

public class Scopes {
  static final String BREAKING_CHANGE_SCOPE = "BREAKING CHANGES";
  static final String CONFIG_CHANGE_SCOPE = "CONFIG CHANGES";

  static boolean isBreakingOrConfigChangeScope(String scope) {
    return scope != null &&
        (BREAKING_CHANGE_SCOPE.equals(scope.toUpperCase()) || CONFIG_CHANGE_SCOPE.equals(scope.toUpperCase()));
  }
}
