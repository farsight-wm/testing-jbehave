pipeline capture tests

Scenario: test mock returning nothing
Given mock _test.acceptanceTests.testSets.shouldInterceptSet:shouldBeIntercepted returning nothing
When invoke _test.acceptanceTests.testSets.shouldInterceptSet:testSet
!-- no exception should be thrown

Scenario: test mock returning nothing negative test
When invoke _test.acceptanceTests.testSets.shouldInterceptSet:testSet
Then exception FlowException was thrown
!-- exception sould be caught in test case

