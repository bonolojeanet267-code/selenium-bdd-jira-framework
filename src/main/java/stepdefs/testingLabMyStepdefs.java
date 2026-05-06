package stepdefs;

import hooks.Hooks;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

import static Elements.objMbhoni.exploreMyProjects;
import static Elements.objTestingLab.aboutTestingLab;

public class testingLabMyStepdefs {

    private WebDriver driver;

    public testingLabMyStepdefs() {
        this.driver = Hooks.driver;
    }

    @Given("User navigates to the testing lab website")
    public void userNavigatesToTheTestingLabWebsite() {
        driver.get("https://www.dialtestinglab.co.za/");
    }

    @Then("Testing lab homepage gets displayed")
    public void testingLabHomepageGetsDisplayed() {
        driver.findElement(aboutTestingLab).isDisplayed();

    }
}
