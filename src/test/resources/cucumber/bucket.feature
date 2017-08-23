Feature: Bucket
  Scenario Outline: add the same element multiple times to a bucket big enough
    Given A bucket of maximum length <bucket_length>
    When I add <elements> randomly chosen elements to the bucket <times> times
    Then The size of the bucket is <bucket_size>
    Then The number of different elements is <elements>

    Examples:
      | bucket_length | elements    | times     | bucket_size |
      | 10            | 1           | 100000    | 100000      |
      | 4             | 1           | 100000    | 100000      |
      | 20            | 1           | 100000    | 100000      |

  Scenario Outline: add multiple elements multiple times to a bucket just big enough
    Given A bucket of maximum length <bucket_length>
    When I add <elements> randomly chosen elements to the bucket <times> times
    Then The size of the bucket is <bucket_size>
    Then The number of different elements is <bucket_length>

    Examples:
      | bucket_length | elements    | times     | bucket_size |
      | 10            | 10          | 100000    | 1000000     |
      | 4             | 4           | 100000    | 400000      |
      | 15            | 15          | 100000    | 1500000     |

  Scenario Outline: add multiple elements multiple times on a small bucket
    Given A bucket of maximum length <bucket_length>
    When I add <elements> randomly chosen elements to the bucket <times> times
    Then The size of the bucket is <bucket_size>
    Then The size of the bucket is less than <bucket_length>

    Examples:
      | bucket_length | elements    | times     | bucket_size |
      | 10            | 40          | 100000    | 4000000     |
      | 4             | 10          | 100000    | 1000000     |
      | 1             | 5           | 100000    | 500000      |

  Scenario Outline: estimate the frequency of an element
    Given A bucket of maximum length <bucket_length>
    When I insert <times> keys between 0 and <elements> which were generated with a binomial distribution the most probable key is estimated correctly

    Examples:
      | bucket_length | elements    | times     |
      | 10            | 40          | 100000    |
      | 4             | 10          | 100000    |
      | 8             | 5           | 100000    |
      | 8             | 10          | 200000    |