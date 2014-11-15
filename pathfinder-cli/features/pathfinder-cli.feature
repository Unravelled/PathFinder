Feature: We have an indexer CLI utility
  In order to iterate on PathFinder
  We want to have a quick way of getting source files into the index

  Scenario: App just runs
    When I get help for "pf-indexer"
    Then the exit status should be 0
    And the banner should be present
    And the banner should document that this app takes options
    And the following options should be documented:
      |--version|
    # And the banner should document that this app takes arguments etc.
