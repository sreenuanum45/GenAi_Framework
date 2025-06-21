Feature: OrangeHRM Login functionality
  @smoke
  Scenario: Successful login
    Given I am on the OrangeHRM login page
    When I enter username 'Admin'
    And I enter password 'admin123'
    And I click the login button
    And I click on the user menu
    And I click on the logout button
    Then I should be logged out
