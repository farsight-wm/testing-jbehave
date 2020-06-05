# wmBehave Story Syntax 

## Given

### pipeline manipulation

```story
Given pipeline value $jexlExpressions
```

Parses and executes multiple jexl expressions separated by semicolons (``;``).

The syntax is explained in detail in chapter [Jexel-Epressions](Jexl-Expressions.md).

```story
Given pipeline from file $idataFile
```

Replaces the current test pipeline with the contents of ``$idataFile``.

### mocking service

Service mocks are Interceptors that are registered on the `invoke`- `intereptPoint`. Thus are executed instead of the actual service. There are different types of mocks that differ in:

* Triggering
  * unconditionally (always)
  * conditionally (when pipeline matches a given jexl-expression)
* Definition of returned values
  * nothing
  * by `$idataFile(s)`
  * by Jaxl-Variable expression

Mocks generate an AdiviceID of the  (unconditionally) or `mock/$serviceName/$stepIndex` (conditionally, where `$stepIndex` is automatically derived from the steps position in the current scenario)

#### Unconditional mocks

Unconditional mocks generate an AdviceID of the form `mock/$serviceName`

```story
Given mock $serviceName returning nothing
```
Creates a mock for ``$serviceName`` that returns no data unconditionally.


```story
Given mock $serviceName returning $idataFiles
```
Creates a mock for ``$serviceName`` that returns the given ``$idataFiles`` in a round robin manner.

```story
Given mock $serviceName returning values $jexlValueExpression
```

Creates a mock for ``$serviceName`` that returns the values defined in `$jexlValueExpression`. (Note as of now, you cannot access the actual pipeline content - this may change in the future)

#### Conditional mocks

Conditional mocks have an appended condition `when $jexlPipelineExpression`. (Details to Jexl see: [Jexl-Expressions](Jexl-Expressions.md#$jexlPipelineExpression). They generate an AdviceID of the form `mock/$serviceName/$stepIndex`, where `$stepIndex` is automatically derived from the steps position in the current scenario.

```story
Given mock $serviceName returning $idataFiles when $jexlPipelineExpression
```

Creates a mock for ``$serviceName`` that returns the given ``$idataFiles`` in a round robin manner when the ``$jexlPipelineExpression`` evaluates to ``true``.

```story
Given mock $serviceName returning values $jexlValuesExpression when $jexlPipelineExpression
```

Creates a mock for ``$serviceName`` that returns the values defined in `$jexlValueExpression` when the ``$jexlPipelineExpression`` evaluates to ``true``.

#### Conditional mocks with default

It is possible to write several conditions for the same `$serviceName` that are triggered conditionally followed by an unconditional mock. The given Mocks (Advices) are processed in definition order. Thus, if no conditional mock triggered execution the unconditional mock will 'catch the rest'.

Note: This applies to all Advices that are bound to the `invoke` InterceptPoint. The first Advice that is triggered (whether it is conditional or not) stops the processing of further Advices for the InterceptPoint.

### Capturing pipeline content

Sometimes it is necessary to check if a called services was provided with expected input. A mock is not sufficient for that, since it only defines the returned values. For that you can use Captures.

A Capture stores the pipeline content that was present during the invocation. Captures may be installed to all InterceptPoints. Use `before` when you want the called service to be executed or if you need to combine the capture with a mock. Use `invoke` if the captured service should not be executed. Use `after` if you need to capture outputs of a service.

Syntax:

````story
Given $captureId capture pipeline[ $interceptPont] calling service $serviceName[ when $jexlPipelineExpression]
````



* ``Given $captureId capture pipeline $interceptPoint calling service $serviceName when $jexlPipelineExpression``

  Creates a pipeline capture interceptor for given capture id, service and intercept point thats enabled if the jexl expression evaluates to true.

* ``Given $captureId capture pipeline $interceptPoint calling service $serviceName``

  Creates a pipeline capture interceptor for given capture id, service and intercept point thats always enabled.

* ``Given $captureId capture pipeline calling service $serviceName when $jexlPipelineExpression``

  Creates a pipeline capture interceptor for given capture id and service at the invoke intercept point thats enabled if the jexl expression evaluates to true.

* ``Given $captureId capture pipeline calling service $serviceName`` 

Creates a pipeline capture interceptor for given capture id and service at the invoke intercept point thats always enabled.

``Given max capture-capacity is $value``

Sets the default capture capacity for all interceptors (default = 10).

* ``Given intercept next $capacity JMSMessages`` 

Creates an interceptor for JMS publishes and the given capacity.
(remove and use below?)

* ``Given intercept JMSMessages``

Creates an interceptor for JMS publishes and the default capacity.

### assertions

* ``Given $assertionId assertion $interceptPoint service $serviceName when $jexlPipelineExpression``
  Counts calls to given service conditionally
* ``Given $assertionId assertion $interceptPoint service $serviceName``
  Counts calls to given service 

### exceptions

* ``Given exception $exception thrown calling service $serviceName when $jexlPipelineExpression``
  Throws a service exception when calling given service conditionally
  * ``Given exception $exception thrown calling service $serviceName``
    Throws a service exception when calling given service
* ``Given exception $exception thrown $interceptPoint calling service $serviceName when $jexlPipelineExpression``
  Throws a service exception when calling given service at intercept point conditionally
* ``Given exception $exception thrown $interceptPoint calling service $serviceName``
  Throws a service exception when calling given service at intercept point

The values for ``$exception`` may be one of:

- ``FlowException`` | ``flow``
  FlowExecptions are thrown by EXIT-Flow steps
- ``ServiceException`` | ``service``
  ServiceException are thrown by Java-Services (jep! It was a good Idea of SAG to not use a common base exception for flow and java!)
- Any exception name from ``java.lang.*`` (e.g. ``NullPointerException``)
- A fully qualified Name of any exception that is known by the Integration Server

## When

### invoke Service

* ``When invoke $serviceName``
  ``When invoke $serviceName without idata`` (Alias)
  Invokes the service ``$serviceName`` with the current test pipeline.
* ``invoke $serviceName with $idataFile``
  Merges the current test pipeline with the given ``$idataFile`` (format: XMLIdataCoder). Then invokes the service ``$serviceName``. 
  This means, that if a variable is present in the test pipeline, this value will be used. If a value is not present in the test pipeline but in the given ``$idataFile``, then the value of the file will be used.

## Then

### pipeline

* ``Then pipeline has $jexlPipelineExpression``
  Evaluates the expression against the test pipeline
* ``Then pipeline document $document matches $idataFile``
  Matches a given $idataFile against the given pipeline document.
  Notice: This is a one way match that is satisfied if equal values exist for each value in the file. Additional data in the test pipeline do not yield to a failed assertion!
* ``Then pipeline document $document exactly matches $idataFile``
  Like above, but this time additional data in the test pipeline yield a failed assertion. (two way match)

### intercepts

* ``Then capture $captureId $no matches $idataFile``
  Matches a given capture (by capture id and number) to an $idataFile (one way match)
* ``Then capture $captureId $no has $jexlPipelineExpression``
  Matches  a given capture (by capture id and number) against a jexl expression.
* ``Then JMSMessage $no is sent to $destinationType $destinationName``
  Checks a captured JMS messages destination type and name
* ``Then JMSMessage $no matches $idataFile``
  Checks the content (document JMSMessage) of a captured JMS message against given $idataFile
* ``Then JMSMessage $no has $jexlPipelineExpression``
  Checks the content (document JMSMessage) of a captured JMS message against given jexl expression

### assertions

* ``Then assertion $assertionId was invoked $invokeCount times``
  Evaluates to true if the assertion was invoked exactly $invokeCount times.

### exceptions

* ``Then exception $exception was thrown``
  Evaluates to true if a (uncaught) exception with the given exception of type ``$exception`` was thrown.
  The exception type may be one of:
  
  * ``any``
    Any type is acceptable
  * ``FlowException`` | ``flow``
    FlowExecptions are thrown by EXIT-Flow steps
  * ``ServiceException`` | ``service``
    ServiceException are thrown by Java-Services (jep! It was a good Idea of SAG to not use a common base exception for flow and java!)
  * Any exception name from ``java.lang.*`` (e.g. ``NullPointerException``)
  * A fully qualified Name of any exception that is known by the Integration Server
  
  You may check the exception's message by checking the pipeline variable ``exceptionMessage``

  ```story
  Then exception flow was thrown
  And piepline has exceptionMessage.matches("some message")
  ```
```

Mocked exceptions contain the message ``WMAOP <ServiceName>``.

### utilities

* ``Then show pipeline in console``
  Dumps content of the test pipeline into standard output (eclipse console)
* ``Then show all captures from $captureId in console``
  Dumps all captures for ``$captureId`into standard output (eclipse console)
* ``Then show capture $no from $captureId in console``
  Dumps capture ``$no`` for ``$captureId`into standard output (eclipse console)



## Parameter Types

### $jexlExpressions / $jexlValueExpressions

One or multiple jexl assignment expressions. If multiple expressions are supported, the single expressions are separated by a semicolon.

- uses a single ``=`` as operator
- preserves type (e.g. String, Number, Boolean)
- documents are traversed by dots
- can use Arrays and Maps

​```jexl
// sets key 'value' of document 'some' to a String
some.value = "a string"

// sets a number
pi = 3.141

// an array
stringArray = ["apple", "banana", "citrus"]

// a map
some.document = {
	"key1" : "value1",
	"key2" : "value2"
}
// Note the qoutes around the keys are important. If they are left out,
// the jexl interpreter tries to get a value from the context for the key e.g.:
foo = "bar";
faultyMap = {
	foo = "bar"
}
// this is not equivalent to
foo = "bar"
faultyMap.foo = "bar"
// but, due to the derefernece of the variable foo, it is equivalent to
foo = "bar"
faultyMap.bar = "bar"
```

Syntax documentation available at: https://commons.apache.org/proper/commons-jexl/reference/syntax.html.

#### Available Functions

* ``load:string(<path to $file>)``
* ``load:bytes(<path to $file>)``
* ``load:stream(<path to $file>)``
* ``load:idata(<path to $idateFile>)``
* ``load:pipeline(<path to $idateFile>)``
  Replaces the current pipeline with the content from $idataFile
* ``load:streamFromString(<string>)``

There are a bunch of functions available to load additional data from test resources. Most of them have a ``<path>`` argument which is a path relative to test root. The load functions load and return data from files as String, Byte array, InputStream or IData.

There is another function that converts a String into an InputStream.


#### $jexlPipelineExpression

An boolean jexl expression that is evaluated against the contents of the test pipeline.

* use ``==`` as equals operator
* may use other comparison operators (e.g. ``>``, ``>=``, ...)
* may use boolean operators: ``&&``, ``||``,` `!``, ``^``,...
  (see https://commons.apache.org/proper/commons-jexl/reference/syntax.html#Operators)

````jexl
some.value.from.document == "some string"
````

Syntax documentation available at: https://commons.apache.org/proper/commons-jexl/reference/syntax.html.

### $idataFile(s)

One ore multiple paths to files (relative to test-root). The files my contain an ``IDataXmlCoder``-coded pipeline or a plain xml document. If a plain xml document is used, the contents will be parsed to a pipeline like the service ``pub.xml:xmlStringToNode`` would do it without any parameter but the xml-input.
*Notice: xml-Documents cannot define arrays with less then two items!*

### $serviceName

A fully qualified  ``NSName`` of a service (e.g. ``myRoot.subInterface:serviceName``)

### $captureId

Alphanumeric ID for a pipeline capture

### $interceptPoint

InterceptPoint for mocks/captures/etc. May be one of:

* before (intercepts before the actual call)
* invoke (intercepts instead of the actual call - Service will not be executed)
* after (intercepts after the actual call)

Notice: xml-Documents cannot define arrays with less then two items!

### $assertionId

Unique ID that identifies an assertation capture.

### $exception 

Message of an exception (normally a ``ServiceException``)

### $no  /  $value / $capture

Simple numeric values

## Including from other stories

It is possible to include all steps of another scenario. This is useful if test require a common setup to avoid repetitions. This may be some mocks or pipeline content, etc.

Inclusion is done with a special comment syntax:

````story
!-- include <list of scenarios> from <file>
````

You may include one or more stories in one step. Story names are separated by comma or semicolon. 

The file parameter is a path relative to the test root. If no the filename does not contain a directory, the same directory as the story file is assumed. If the value ``#`` is provided then the story is looked up from the same file.

You may define stories that are ment for inclusion and give them the extension ``.inc.story`` this will prevent them from being executed as a normal test.

### Including Stories from Include-Locations (Packages)

By perpending an `@` character to the path, The file is looked up in all include locations (first match is imported). Include paths must always be relative to the include location root (no automatically added path), but you may omit the file ending if the include story is ending in `.inc.story`

## Examples

You may use examples in your Scenarios. Examples are used to run the same scenario with a different set of parameters.

You may access parameters in any Step by writing ``<PARAMETER_NAME>`` where the name "PARAMETER_NAME" must be provided in the ``Examples:``-section in form of a piped-table. Notice, this is a simply a string substitution feature

````story
Scenario: demonstration of examples
Given pipeline values <values>
Then pipeline has <expression>
Examples:
|values|expression|
|foo="bar"|foo == "bar"|
|map={"key":"value","foo":"bar"}|map.key == "value" and map.foo == "bar"|

Scenario: other example
Given pipeline values num1="<num1>"; num2="<num2>"
When invoke pub.math:addInts
Then pipline has value=="<value>"
Examples:
|num1|num2|value|
|1|2|3|
|17|04|21|
|145|1001|1146|
````

## AdviceIDs

Many Steps require an ``adviceId`` e.g. in form of a ``$captuireId``, ``$assertionId``, ... These IDs are used to control the mocking framework. Even mock steps generate an advicdeId from the ``$serviceName`` parameter.

To prevent collisions and therefor unexpected test behavior all adviceIds have a ``scope`` and some of them an additional ``qualifier`` that is added automatically.

For example, the Step

````story
Given $captureId capture pipeline calling service $serviceName
````

Generates an AdviceID of the actual form ``capture/$captureId``. Since a capture is nothing else than an intercept point you can use an assert Step to check, how often this capture was actually called like this

````story
Then assertion capture/$captureId was invoked $count times
````

Every time an AdviceID is used wmBehave checks if the scope is given explicitly, if not it adds a default scope depending on the type of steps. The default scope for assertions is ``assertion``. If you write

```story
Then assertion $captureId was invoked $count times
```

Then this step checks the invoke count for ``assertion/$captureId`` that will most likely be zero, since or capture was defined with a different scope.

**Why all this scoping?** 

This enables you to write less Steps into a story with the same meaning. Without this mechanism in order to check how often an capture was triggerd you would have to write two ``Given``-Steps one for the assertion and the other one for the capture itself.

