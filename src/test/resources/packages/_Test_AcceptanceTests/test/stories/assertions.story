Assertions

Scenario: some basic assertions
Given pipeline values
	foo.bar.key = "value";
	foo.bar.other = "other";
	test.integer = 42;
	test.strInteger = "42";
	test.bool = true;
	test.strBool = "false";
Then assert that
	path('foo/bar/key').isString().isNotNull().isEqualTo("value");
	path('foo').asDocument().isNotEmpty();
	then(foo).asDocument().isNotEmpty();
	then(test.strInteger).asInt().isEqualTo(42);
	path('test/strBool').asBool().isFalse();
	path('test/bool').asBool().isTrue();

	
Scenario: null-or-empty
Given pipeline values
	test = {
		"foo" : "exists",
		"emptyStr" : "",
		"nullRef" : null
	};
Then dump pipeline
Then assert that
	then(test.foo).isNotNullOrEmpty();
	path('test/foo').isNotNullOrEmpty();
	then(test.emptyStr).isNullOrEmpty();
	path('test/emptyStr').isNullOrEmpty();
	then(test.nullRef).isNullOrEmpty();
	path('test/nullRef').isNullOrEmpty();
	then(test.notExistent).isNullOrEmpty();
	path('test/notExistent').isNullOrEmpty();
	