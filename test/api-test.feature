Feature: Search testing

  Background:
    * def currentYear = function() {new Date().getFullYear()} # maybe this works…
    * def host = '__REPLACE_WITH_ACTUAL_AWS_HOST__'
    * def basePath = 'https://' + host + '/channel/'
    * def searchPath = basePath + 'search?q='
    * def token = karate.properties['CHANNEL_REGISTRY_API_KEY'] # maybe not necessary
    * def currentEnvironment = karate.properties['CURRENT_ENVIRONMENT']
    * def JOURNAL_SEARCH_BODY = read('test_data/journal_search_body.template').replace('__CURRENT_ENVIRONMENT__', currentEnvironment)
    * def JOURNAL_YEAR_BODY = read('test_data/journal_body.template').replace('__CURRENT_ENVIRONMENT__', currentEnvironment)
    * def PUBLISHER_SEARCH_BODY = read('test_data/publisher_search_body.template').replace('__CURRENT_ENVIRONMENT__', currentEnvironment)
    * def PUBLISHER_YEAR_BODY = read('test_data/publisher_body.template').replace('__CURRENT_ENVIRONMENT__', currentEnvironment)
    * def existingJournal = '446885'
    * def existingPublisher = '18442'
    * def nonExisting = 'not-a-real-thing'
    * def PROBLEM_JSON_MEDIA_TYPE = 'application/problem+json'
    * def JSON_LD_MEDIA_TYPE = 'application/ld+json'
    * def JSON_MEDIA_TYPE = 'application/json'
      """
      {
        'Origin': 'http://localhost:3000',
        'Accept': '*/*',
        'Referer': 'Not sure what the value should be yet',
        'Origin': 'https://' + currentEnvironment + '/registration/aUuid',
        'Connection', 'keep-alive',
        'Accept-Encoding': 'gzip, deflate, br',
        'Access-Control-Request-Method': 'GET',
        'Access-Control-Request-Headers': 'authorization'}
      """

  Scenario Outline: Query and receive CORS preflight response
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method OPTIONS
    Then status 200
    And match contentType == JSON_MEDIA_TYPE
    And match responseHeaders['Access-Control-Allow-Origin'][0] == '*'
    And match responseHeaders['Access-Control-Allow-Methods'][0] == 'GET,OPTIONS'
    And match responseHeaders['Access-Control-Allow-Headers'][0] == 'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'
    And match responseHeaders['Vary'][0] == 'Origin'

    Examples:
      | VALID_URL                                                          |
      | 'https://' + path + '/channel/journal?query=Sensors'               |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     |
      | 'https://' + path + '/channel/publisher?query=Taylor'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher |

  Scenario Outline: Unauthenticated search request is rejected
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    Then status 401
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Unauthorized'
    And match response.status == 401
    And match response.detail == 'You are not authorized to access the resource ' + <VALID_URL>
    And match response.instance == <VALID_URL>
    And match response.requestId == '#notnull'

    Examples:
      | VALID_URL                                                          |
      | 'https://' + path + '/channel/journal?query=Sensors'               |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     |
      | 'https://' + path + '/channel/publisher?query=Taylor'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher |

  Scenario Outline: Requesting non-existing resource returns not found error
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <NON_EXISTING_RESOURCE_URL>
    When method get
    Then status 404
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.type == 'about:blank'
    And match response.title == 'Not found'
    And match response.status == 404
    And match response.detail == 'The requested resource ' + <NON_EXISTING_RESOURCE_URL> + ' does not exist'
    And match response.instance == <NON_EXISTING_RESOURCE_URL>
    And match response.requestId == '#notnull'

    Examples:
      | NON_EXISTING_RESOURCE_URL                                    |
      | 'https://' + path + '/channel/journal/2019/' + nonExisting   |
      | 'https://' + path + '/channel/publisher/2019/' + nonExisting |

  Scenario Outline: Query proxy error gives Bad Gateway problem response
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    And the Channel Register API is down
    When method get
    Then status 502
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title 'Bad Gateway'
    And match response.status == 502
    And match response.detail == 'Your request cannot be processed at this time due to an upstream error'
    And match response.instance == <VALID_URL>
    And match response.requestId == '#notnull'

    Examples:
      | VALID_URL                                                          |
      | 'https://' + path + '/channel/journal?query=Sensors'               |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     |
      | 'https://' + path + '/channel/publisher?query=Taylor'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher |

  Scenario Outline: Slow upstream gives Gateway Timeout problem response
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    And the Channel Register response takes longer than 2 seconds
    When method get
    Then status 504
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Gateway Timeout'
    And match response.status == 504
    And match response.detail == 'Your request cannot be processed at this time because the upstream server response took too long'
    And match response.instance == <VALID_URL>
    And match response.requestId == '#notnull'

    Examples:
      | VALID_URL                                                          |
      | 'https://' + path + '/channel/journal?query=Sensors'               |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     |
      | 'https://' + path + '/channel/publisher?query=Taylor'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher |

  Scenario Outline: Unexpected error returns Internal Server Error problem response
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    And the channels API application experiences an unexpected error
    Then status 500
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Internal Server Error'
    And match response.status == 500
    And match response.detail == 'Your request cannot be processed at this time because of an internal server error'
    And match response.instance == <VALID_URL>
    And match response.requestId == '#notnull'

    Examples:
      | VALID_URL                                                          |
      | 'https://' + path + '/channel/journal?query=Sensors'               |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     |
      | 'https://' + path + '/channel/publisher?query=Taylor'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher |

  Scenario Outline: Query with unacceptable method returns Not acceptable error
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url 'https://' + path + '/channel/journal?query=Sensors'
    When method <METHOD>
    Then status 405
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Method not allowed'
    And match response.status == 405
    And match response.detail == 'Your request cannot be processed because the HTTP method ' + <METHOD> + ' is not supported, use GET'
    And match response.instance == 'https://' + path + '/channel/journal?query=Sensors'
    And match response.requestId == '#notnull'

    Examples:
      | METHOD  |
      | DELETE  |
      | PATCH   |
      | POST    |
      | PUT     |
      | CONNECT |
      | TRACE   |

  Scenario: Query with bad parameters returns Bad Request
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url 'https://' + path + '/channel/journal?woolen=jumper'
    When method get
    Then status 400
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Bad Request'
    And match response.status == 400
    And match response.detail == 'Your request cannot be processed because the supplied parameter(s) "not" cannot be understood'
    And match response.instance == 'https://' + path + '/channel/journal?woolen=jumper'
    And match response.requestId == '#notnull'

  Scenario Outline: Request with bad content type returns Not Acceptable
    * configure headers = { 'Accept': <UNACCEPTABLE_CONTENT_TYPE>, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    Then status 406
    And match contentType == PROBLEM_JSON_MEDIA_TYPE
    And match response.title == 'Not Acceptable'
    And match response.status == 406
    And match response.detail == 'Your request cannot be processed because the supplied content-type ' + <UNACCEPTABLE_CONTENT_TYPE> + ' cannot be understood, acceptable types: application/ld+json, application/json'
    And match response.instance == <VALID_URL>
    And match response.requestId == '#notnull'


    Examples:
      | VALID_URL                                                          | UNACCEPTABLE_CONTENT_TYPE |
      | 'https://' + path + '/channel/journal?query=Sensors'               | 'image/jpeg'              |
      | 'https://' + path + '/channel/journal/2019/' + existingJournal     | 'application/xml'         |
      | 'https://' + path + '/channel/publisher?query=Taylor'              | 'image/jpeg'              |
      | 'https://' + path + '/channel/publisher/2019/' + existingPublisher | 'application/xml'         |

  Scenario Outline: Request with content negotiation returns expected response
    * configure headers = { 'Accept': <CONTENT_TYPE>, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    Then status 200
    And match contentType == <Content-type>
    And match response == <RESPONSE_BODY>.replace('__PROCESSING_TIME__', response.processingTime)

    Examples:
      | CONTENT_TYPE       | VALID_URL                                                          | RESPONSE_BODY         |
      | JSON_LD_MEDIA_TYPE | 'https://' + path + '/channel/journal?query=Sensors'               | JOURNAL_SEARCH_BODY   |
      | JSON_MEDIA_TYPE    | 'https://' + path + '/channel/journal?query=Sensors'               | JOURNAL_SEARCH_BODY   |
      | JSON_LD_MEDIA_TYPE | 'https://' + path + '/channel/journal/2019/' + existingJournal     | JOURNAL_YEAR_BODY     |
      | JSON_MEDIA_TYPE    | 'https://' + path + '/channel/journal/2019/' + existingJournal     | JOURNAL_YEAR_BODY     |
      | JSON_LD_MEDIA_TYPE | 'https://' + path + '/channel/publisher?query=Taylor'              | PUBLISHER_SEARCH_BODY |
      | JSON_MEDIA_TYPE    | 'https://' + path + '/channel/publisher?query=Taylor'              | PUBLISHER_SEARCH_BODY |
      | JSON_LD_MEDIA_TYPE | 'https://' + path + '/channel/publisher/2019/' + existingPublisher | PUBLISHER_YEAR_BODY   |
      | JSON_MEDIA_TYPE    | 'https://' + path + '/channel/publisher/2019/' + existingPublisher | PUBLISHER_YEAR_BODY   |

  Scenario Outline: Search returns no more than ten results
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    Then status 200
    And match response.hits.length <= 10

    Examples:
      | VALID_URL                                             |
      | 'https://' + path + '/channel/journal?query=Sensors'  |
      | 'https://' + path + '/channel/publisher?query=Taylor' |

  Scenario Outline: Search returns next ten results
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL_WITH_START_PARAMETER>
    When method get
    Then status 200
    And match response.hits.length <= 10
    And match response.firstRecord == 11

    Examples:
      | VALID_URL_WITH_START_PARAMETER                              |
      | 'https://' + path + '/channel/journal?query=and&start=11'   |
      | 'https://' + path + '/channel/publisher?query=and&start=11' |

  Scenario Outline: Search returns data for current year when year is unspecified
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL_WITH_PARAMETER_READY_FOR_VALUE> + <QUERY_VALUE>
    When method get
    Then status 200
    And match response = <EXPECTED_BODY>.replace('__YEAR_INPUT__', currentYear).replace('__PROCESSING_TIME__', response.processingTime).replace('__QUERY_INPUT__', <QUERY_VALUE>)

    Examples:
      | VALID_URL_WITH_PARAMETER_READY_FOR_VALUE        | QUERY_VALUE | EXPECTED_BODY         |
      | 'https://' + path + '/channel/journal?query='   | Sensors     | JOURNAL_SEARCH_BODY   |
      | 'https://' + path + '/channel/publisher?query=' | Taylor      | PUBLISHER_SEARCH_BODY |

  Scenario Outline: Search returns data for given year when year is specified
    * configure headers = { 'Accept': JSON_LD_MEDIA_TYPE, 'Authorization: Basic ' + token }
    * contentType = responseHeaders['Content-Type'][0]
    Given url <VALID_URL>
    When method get
    Then status 200
    And match response = And match response = <EXPECTED_BODY>.replace('__YEAR_INPUT__', <YEAR>).replace('__PROCESSING_TIME__', response.processingTime).replace('__QUERY_INPUT__', <QUERY>)

    Examples:
      | VALID_URL                                                       | QUERY   | YEAR |
      | 'https://' + path + '/channel/journal?query=Sensors&year=2018'  | Sensors | 2018 |
      | 'https://' + path + '/channel/publisher?query=Taylor&year=2018' | Taylor  | 2018 |


