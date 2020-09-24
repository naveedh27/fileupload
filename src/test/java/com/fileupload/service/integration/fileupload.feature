Feature: Testing Fileupload

  Scenario: Share File
    Given user "hello@world.com" loggedin with password "V:x5Z)K'h"
    Given user "abc@xyz.com" loggedin with password "^cHF9PmH^"
    Given "hello@world.com" uploads file "a.txt"
    Given "hello@world.com" uploads file "b.txt"
    When "hello@world.com" share file "a.txt" with "abc@xyz.com"
    Then "a.txt" download "succeeds" for "abc@xyz.com"
    Then "b.txt" download "fails" for "abc@xyz.com"
