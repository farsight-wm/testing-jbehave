Include story

Scenario: foo
Given pipeline values foo_included="true"
!-- foo also include bar!!
!-- include bar from #

Scenario: bar
Given pipeline values bar_included="true"

