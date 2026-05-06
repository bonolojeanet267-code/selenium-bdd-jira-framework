package base;

import utilities.DriverFactory;
import io.cucumber.java.After;

public class BaseTest {

    @After
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}