Test of inclusions

Scenario: include from same file above
!-- include set-value from #
Then pipeline has included == "set-value-from-include.story"

Scenario: set-value
Given pipeline values included = "set-value-from-include.story"
