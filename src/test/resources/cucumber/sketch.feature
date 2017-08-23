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
    When I add the elements on <elements_file> generated with a binomial distribution
    Then I obtain a heavy key <heavy_hitter> exceeding the threshold <threshold>
    Examples:
      | rows | cols | prime | elements_file                   | threshold  | heavy_hitter |
      | 5    | 40   | 7919  | 200keys_4000elements.txt        | 66         | 8            |
      | 5    | 40   | 7919  | 1000keys_100000elements.txt     | 280        | 52           |
      | 5    | 80   | 7919  | 1000keys_100000elements.txt     | 280        | 52           |