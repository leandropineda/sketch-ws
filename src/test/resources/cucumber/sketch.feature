Feature: Sketch
  Scenario Outline: add random elements to a sketch
    Given I create a sketch of dimensions <rows>x<cols> with a prime number p=<prime>
    When I add <elements> random elements to the sketch
    Then The sketch has <elements> elements

  Examples:
    | rows | cols | prime | elements |
    | 5    | 40   | 7919  | 1000     |
    | 4    | 60   | 7919  | 100000   |
    | 9    | 30   | 7919  | 100000   |

  Scenario Outline: get heavy hitters
    Given I create a sketch of dimensions <rows>x<cols> with a prime number p=<prime>
    When I add the elements on <elements_file> to the sketch
    Then I obtain a heavy key <heavy_hitter> exceeding the threshold <threshold>
    Examples:
      | rows | cols | prime | elements_file                   | threshold  | heavy_hitter |
      | 5    | 40   | 7919  | 200keys_4000elements.txt        | 66         | 8            |
      | 5    | 40   | 7919  | 1000keys_100000elements.txt     | 280        | 52           |
      | 5    | 80   | 7919  | 1000keys_100000elements.txt     | 280        | 52           |

  Scenario Outline: get heavy changers
    Given I create a sketch of dimensions <rows>x<cols> with a prime number p=<prime>
    Given I create a second sketch with the same parameters
    When I add the elements on <elements_file> to the sketch
    When I add the elements on <second_elements_file> to the second sketch
    Then There is one heavy changer <element> exceeding threshold <threshold>
    Examples:
      | rows | cols | prime | elements_file                   | second_elements_file                 | element | threshold |
      | 5    | 40   | 7919  | 1000keys_100000elements.txt     | 1000keys_100000elements_changers.txt | 52      | 500       |
