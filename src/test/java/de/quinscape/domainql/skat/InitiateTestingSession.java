package de.quinscape.domainql.skat;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class InitiateTestingSession
{

    private final static Logger log = LoggerFactory.getLogger(InitiateTestingSession.class);


    private final String driverPath = "/home/sven/tools/geckodriver";
    public WebDriver driver;

    @Before
    public void startBrowser() {
        System.setProperty("webdriver.gecko.driver", driverPath);
        final FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);

    }

    @Test
    public void launchThree() throws InterruptedException
    {

        urlInNewTab("http://localhost:8080/game/");
        Thread.sleep(1000);

        if (driver.findElements(By.cssSelector(".action-join-game")).size() > 0)
        {
            log.info("Clear Games");

            driver.findElement(By.cssSelector("button.action-flush-games")).click();
            driver.close();

            urlInNewTab("http://localhost:8080/game/");
            Thread.sleep(1000);

        }
        driver.findElement(By.xpath("//button[text()='Random Game']")).click();

//        urlInNewTab("http://localhost:8080/game/");
//        Thread.sleep(2000);
//        driver.findElement(By.cssSelector(".action-join-game")).click();
//
//        urlInNewTab("http://localhost:8080/game/");
//        Thread.sleep(2000);
//        driver.findElement(By.cssSelector(".action-join-game")).click();

    }


    private Object urlInNewTab(String url)
    {
        return ((JavascriptExecutor) driver).executeScript("window.open('" + url + "');");
    }

}
