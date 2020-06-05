advice cross reference tests

Scenario: mock can be tested in asserts
Given mock _test.acceptanceTests.testSets.shouldInterceptSet:shouldBeIntercepted returning nothing
When invoke _test.acceptanceTests.testSets.shouldInterceptSet:testSet
Then assertion mock/_test.acceptanceTests.testSets.shouldInterceptSet:shouldBeIntercepted was invoked 1 times



!-- All features should have a test here!