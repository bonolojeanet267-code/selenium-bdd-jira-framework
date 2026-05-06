Feature: Mbhoni digital portfolio

  Background:
    Member is already on the Mbhoni digital portal

    Scenario: Open portfolio page
      Given User opens the portfolio
      Then Projects page gets displayed
      When User clicks the Home button
      Then Welcome to my World page gets displayed
      When Member clicks About
      Then Hi there page gets displayed
      When Member clicks the Read more button
      Then About Me and Quick Facts page gets displayed
      When Member scrolls down to My Skills
      Then multiple programming languages page gets displayed
      When Member clicks Experience
      Then Experience That Counts page gets displayed
      When Member clicks Achievements
      Then Achievements & Awards page gets displayed
      When Member scrolls down to the contact section
      Then Services page gets displayed
      When Member scrolls down to Full-Stack Dev
#      Then Full-Stack Dev and Healthcare Analytics projects get displayed
      And Member clicks the slick-next button
      Then Climate Analytics project get displayed
      When Member clicks the slick-next button
      Then Predictive Analytics project get displayed
      When Member clicks the slick-next button
      Then AI-Based Learning project get displayed
      When Member clicks the slick-next button
      Then Economic Modeling project get displayed
      When Member clicks the slick-next button
      Then Machine Learning project get displayed
      When Member clicks the slick-next button
      Then Sustainability project get displayed

















