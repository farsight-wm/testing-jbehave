Test of inclusions

Scenario: include form other file
!-- include set-value from stories/include.inc.story
Then pipeline has included == "set-value-from-include.inc.story"

Scenario: include indirect form other file
!-- include indirect-set-value from stories/include.inc.story
Then pipeline has included == "set-value-from-include.inc.story"

Scenario: include from same file above
!-- include set-value from #
Then pipeline has included == "set-value-from-include.story"

Scenario: set-value
Given pipeline values included = "set-value-from-include.story"

Scenario: include from same file below
!-- include set-value from #
Then pipeline has included == "set-value-from-include.story"

Scenario: include from same directory
!-- include set-value from include.inc.story
Then pipeline has included == "set-value-from-include.inc.story"

Scenario: include from same directory with partial name
!-- include set-value from include
Then pipeline has included == "set-value-from-include.inc.story"

Scenario: include from include path
!-- not implemented
Then pipeline has true == false

