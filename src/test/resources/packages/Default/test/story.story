Die Story vom verfluchten Charset

Scenario: Default
Given mock mocks:mockMe always returning pipeline.xml
When invoke mocks:parent without idata
Then show pipeline in console

