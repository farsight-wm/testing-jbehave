Test of all matches

Scenario: matches exactly 1
Given pipeline values load:pipeline("resources/xml.applepear.xml")
Then pipeline document document exactly matches resources/idata.applepear.xml
Then show pipeline in console

Scenario: matches exactly 2 - negative test
Given pipeline values load:pipeline("resources/xml.applepear.xml"); document.banana = "gamma"
!-- This test should fail!
!-- Is there a possibility to write a story to expect a failure!? Would be good for testing ;)
!-- Then pipeline document document exactly matches resources/idata.applepear.xml
