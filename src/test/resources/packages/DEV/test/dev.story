Dev Story

Scenario: @test
Given pipeline values
	intValue = 50;
	someValue = "42";
	someString = "13";
	boolean1 = "true";
	boolean2 = "false";
	boolean3 = "true";
	notThere = "true";
	
	foo.bar = "bar";
	
	
	
	foo['test.test'] = "test";
	
	
	
Then show pipeline in console
Then assert that

	then(boolean3).as("boolean3").asBoolean().isTrue();
	path('boolean3').as('haha').asBoolean().isTrue();
	path('notThere').asBoolean().isNotNull();

	
	then(foo).isIData().containsKey('bar');
	
Scenario: @test story
Given pipeline values
	foo.x.bar = "1";
	foo.y.foo = "2";
	foo.put('test.test', "test");
	foo['other;other'] = "other";


	partnerConfig = {
		'file.dateFormat' : "test",
		'file.decimalSeperator' : "test2"
	};
Then dump pipeline with filter foo, boolean1

Scenario: test IDataAssert
Given pipeline values
	test = {
		"key1" : "v1",
		"key2" : "v2",
		"key3" : "v3"
	}
Then assert for test that
	path('testxxx').asDoc().matches({"key1" : "v1", "key2" : "v2"});

