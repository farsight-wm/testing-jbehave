pipeline capture tests

Scenario: test mock returning nothing
Given mock _test.acceptanceTests.testSets.shouldInterceptSet:shouldBeIntercepted returning values  
	someKey = "someValue";
	someNumber = 42;
	someArray = [ "foo", "bar", "other" ]
When invoke _test.acceptanceTests.testSets.shouldInterceptSet:testSet
Then pipeline has someKey == "someValue"
Then pipeline has someNumber > 41
Then pipeline has someNumber < 42.1
!-- Then pipeline has arrays.contains(someArray, "other") <- geht nicht. Wieso?!
!-- no exception should be thrown

