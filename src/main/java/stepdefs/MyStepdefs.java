package stepdefs;

import hooks.Hooks;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static Elements.objMbhoni.*;

public class MyStepdefs {

    private void scrollAndroidByText(WebDriver driver, String skills) {
    }

    private WebDriver driver;

    public MyStepdefs() {
        this.driver = Hooks.driver;
    }

    @Given("User opens the portfolio")
    public void userOpensThePortfolio() {
        driver.get("https://mbhonichau.github.io/mbusocosby/projects.html");
    }

    @Then("Projects page gets displayed")
    public void projectsPageGetsDisplayed() {
        driver.findElement(exploreMyProjects).isDisplayed();
    }

    @When("User clicks the Home button")
    public void userClicksTheHomeButton() {
        driver.findElement(homePage).click();
    }

    @Then("Welcome to my World page gets displayed")
    public void welcomeToMyWorldPageGetsDisplayed() {
        driver.findElement(welcomeToMyWorld).isDisplayed();
    }

    @When("Member clicks About")
    public void memberClicksAbout() {
        driver.findElement(about).click();
    }

    @Then("Hi there page gets displayed")
    public void hiTherePageGetsDisplayed() {
        driver.findElement(hiThere).isDisplayed();
    }

    @When("Member clicks the Read more button")
    public void memberClicksTheReadMoreButton() {
        driver.findElement(readMore).click();
    }

    @Then("About Me and Quick Facts page gets displayed")
    public void aboutMeAndQuickFactsPageGetsDisplayed() {
        driver.findElement(aboutMe).isDisplayed();
        driver.findElement(quickFacts).isDisplayed();
    }

    @When("Member scrolls down to My Skills")
    public void memberScrollsDownToMySkills() {
        scrollAndroidByText(driver, "Skills");
    }

    @Then("multiple programming languages page gets displayed")
    public void multipleProgrammingLanguagesPageGetsDisplayed() {
        driver.findElement(mySkills).isDisplayed();
    }

    @When("Member clicks Experience")
    public void memberClicksExperience() {
        driver.findElement(experience).click();
    }

    @Then("Experience That Counts page gets displayed")
    public void experienceThatCountsPageGetsDisplayed() {
        driver.findElement(experienceCounts).isDisplayed();
    }

    @When("Member clicks Achievements")
    public void memberClicksAchievements() {
        driver.findElement(achievements).click();

    }

    @Then("Achievements & Awards page gets displayed")
    public void achievementsAwardsPageGetsDisplayed() {
        driver.findElement(achievementsAndRewards).isDisplayed();
    }

    @When("Member scrolls down to the contact section")
    public void memberScrollsDownToTheContactSection() {
        scrollAndroidByText(driver, "Contact");
    }

    @Then("Services page gets displayed")
    public void servicesPageGetsDisplayed() {
        driver.findElement(services).isDisplayed();
    }


    @When("Member scrolls down to Full-Stack Dev")
    public void memberScrollsDownToFullStackDev() {
        scrollAndroidByText(driver, "Full-Stack Dev");
    }


    @Then("Full-Stack Dev and Healthcare Analytics projects get displayed")
    public void fullStackDevAndHealthcareAnalyticsProjectsGetDisplayed() {
        driver.findElement(fullStackDevDecking).isDisplayed();
        driver.findElement(fullStackDevJusaqua).isDisplayed();
    }


    @And("Member clicks the slick-next button")
    public void memberClicksTheSlickNextButton() {
        driver.findElement(slickNextButton).click();

    }

    @Then("Climate Analytics project get displayed")
    public void climateAnalyticsProjectGetDisplayed() {
        driver.findElement(climate).isDisplayed();

    }

    @Then("Predictive Analytics project get displayed")
    public void predictiveAnalyticsProjectGetDisplayed() {
        driver.findElement(predictiveAnalytics).isDisplayed();

    }

    @Then("AI-Based Learning project get displayed")
    public void aiBasedLearningProjectGetDisplayed() {
        driver.findElement(aiBasedLearning).isDisplayed();

    }

    @Then("Economic Modeling project get displayed")
    public void economicModelingProjectGetDisplayed() {
        driver.findElement(economicModelling).isDisplayed();

    }

    @Then("Machine Learning project get displayed")
    public void machineLearningProjectGetDisplayed() {
        driver.findElement(machineLearning).isDisplayed();

    }

    @Then("Sustainability project get displayed")
    public void sustainabilityProjectGetDisplayed() {
        driver.findElement(sustainability).isDisplayed();

    }
}
