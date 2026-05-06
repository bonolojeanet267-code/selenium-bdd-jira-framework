package stepdefs;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebDriver;

public class apiStepdefs
{

    private WebDriver driver;

    public apiStepdefs(WebDriver driver) {
        this.driver = driver;
    }

    @Given("the user enters the url of get products api endpoint")
    public void theUserEntersTheUrlOfGetProductsApiEndpoint() {
        driver.get("https://fakestoreapi.com/products");
    }
}
