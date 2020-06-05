InvokeCount

Scenario: invokeCount unconditional
Given test assertion invoke service some:service
Then assertion test was invoked 0 times
When invoke some:service 
Then assertion test was invoked 1 times

Scenario: invokeCount conditional
Given mock some:service returning nothing
Given test assertion before service some:service when foo == "bar" 
Then assertion test was invoked 0 times
When invoke some:service 
Then assertion test was invoked 0 times
Given pipeline values foo = "other"
When invoke some:service 
Then assertion test was invoked 0 times
Given pipeline values foo = "bar"
When invoke some:service 
Then assertion test was invoked 1 times

