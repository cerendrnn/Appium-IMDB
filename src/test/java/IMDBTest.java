import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.gen5.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.*;
import java.time.Duration;
import java.util.*;

public class IMDBTest {

    private AppiumDriver driver;


    @BeforeTest
    public void setUp() throws MalformedURLException {

        //setting up desired capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName","Android");
        caps.setCapability("automationName","UiAutomator2");
        caps.setCapability("platformVersion", "11.0");
        caps.setCapability("deviceName","Pixel 4 XL");
        caps.setCapability("app","/Users/aybukeceren.duran/Downloads/imdb.apk");
        caps.setCapability("appActivity","com.imdb.mobile.HomeActivity");
        caps.setCapability("autoGrantPermissions",true);

        driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps);
    }

    public void closeDialogTapSearch(){

        //2 cases in this assignment has closing dialog and tapping search icon scenarios.
        //For code readability, this function covers these 2 scenarios.
        AndroidElement button = (AndroidElement) driver.findElement(By.id("android:id/button2"));
        waitElement(button);
        button.click();//locator which finds "OK" button to close dialog
        AndroidElement search_icon = (AndroidElement) driver.findElementByXPath("//android.widget.FrameLayout[@content-desc=\"Search\"]/android.widget.ImageView");
        waitElement(search_icon);
        search_icon.click();

    }


    public void search(String s){

        //function for writing parameter on search bar.
        AndroidElement search_bar = (AndroidElement) driver.findElement(By.id("com.imdb.mobile:id/search_src_text"));
        waitElement(search_bar);
        search_bar.click();
        search_bar.sendKeys(s);
    }

    public void tapTop250Movies(){

        //function for tapping top 250 movies section.
        AndroidElement element = (AndroidElement) driver.findElement(By.xpath("//*[@text='" + "Top 250 movies" + "']"));
        waitElement(element);
        element.click();
    }

    public void tapSortByIMDBRating(){
        //function for tapping "sort by IMDB rating"
        AndroidElement element1 = (AndroidElement) driver.findElement(By.id("com.imdb.mobile:id/refine_button"));
        waitElement(element1);
        element1.click();
        AndroidElement element2 = (AndroidElement) driver.findElement(By.id("com.imdb.mobile:id/refinements"));
        waitElement(element2);
        element2.click();
        AndroidElement element3 = (AndroidElement) driver.findElement(By.xpath("//*[@text='" + "IMDb rating" + "']"));
        waitElement(element3);
        element3.click();
    }

    public void tapNumberOfRatings(){

        //function for tapping number of ratings
        AndroidElement element = (AndroidElement) driver.findElement(By.xpath("//*[@text='" + "Number of ratings" + "']"));
        waitElement(element);
        element.click();
    }

    public void waitElement(WebElement element){

        FluentWait<AppiumDriver> wait = new FluentWait<>(driver)
                                             .withTimeout(Duration.ofSeconds(10))
                                             .pollingEvery(Duration.ofSeconds(1))
                                             .ignoring(NoSuchElementException.class)
                                             .withMessage("Element cannot be found");
        wait.until(ExpectedConditions.visibilityOf(element));
        Assertions.assertTrue(element.isDisplayed());
    }

    @Test
    public void test1() throws InterruptedException {

        closeDialogTapSearch();//close the dialog
        search("hobbit"); //search hobbit movies
        Thread.sleep(5000);
        List<AndroidElement> hobbitYears = driver.findElements(By.id("com.imdb.mobile:id/context_first")); //collecting years data of Hobbit movies
        List<WebElement> hobbitMovieTitles = driver.findElements(By.id("com.imdb.mobile:id/suggestion"));//collecting movie titles data of Hobbit movies

        int hobbitSize = hobbitYears.size();
        int index_year2014 = 0;

        for(int i=0; i<hobbitSize; i++){

            if(hobbitYears.get(i).getText().equals("2014")){
                index_year2014 = i; //finding the index of "Hobbit" movie with release year "2014"
                break;
            }
        }

        if(hobbitMovieTitles.get(index_year2014).getText().equals("The Hobbit: The Battle of the Five Armies"))

            System.out.println("correct title");

         else

            System.out.println("wrong title");
    }

    @Test
    public void test2() throws InterruptedException {

         closeDialogTapSearch();
         tapTop250Movies();
         Thread.sleep(5000);

         List<WebElement> top250movies = driver.findElements(By.id("com.imdb.mobile:id/primaryText"));//collecting top 250 movies data
         List<String> t250movies = new ArrayList<>();//list for titles of top 250 movies
         int size_top250movies = top250movies.size();

         for(int i=3; i<size_top250movies; i++)
             top250movies.remove(i);//we only need first 3 data. So we should delete unnecessary data.

        size_top250movies = 3;

        for(int i=0; i<3; i++){
            t250movies.add(top250movies.get(i).getText());//add titles of first 3 movies of top 250 movies.
        }
         tapSortByIMDBRating();
         tapNumberOfRatings();
         //driver.findElement(By.id("com.imdb.mobile:id/refinements"));

         for(int i=0; i<size_top250movies; i++)
            System.out.println(top250movies.get(i).getText());

         List<WebElement> sorted_byrating_movies = driver.findElements(By.id("com.imdb.mobile:id/primaryText"));
         List<String> sb_movies = new ArrayList<>();//list for titles of sorted movies
         int size_sorted_byrating_movies = sorted_byrating_movies.size();

         for(int i=3; i<size_sorted_byrating_movies; i++)
             sorted_byrating_movies.remove(i);//we only need first 3 data. So we should delete unnecessary data.

        size_sorted_byrating_movies = 3; //after deletions size must be 3.

         for(int i=0; i<size_sorted_byrating_movies; i++)
             System.out.println(sorted_byrating_movies.get(i).getText());

         for(int i=0; i<3; i++){
             sb_movies.add(sorted_byrating_movies.get(i).getText());
         }

        Collections.sort(t250movies);
        Collections.sort(sb_movies);

        t250movies.retainAll(sb_movies);
        System.out.println(t250movies + " is/are on both lists");
    }

    @AfterTest
    public void tearDown(){

        driver.quit();
    }
}
