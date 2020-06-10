import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit.ScreenShooter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;


import static com.codeborne.selenide.Condition.*;

import java.util.Calendar;
import java.util.Formatter;
import java.util.Random;

public class GoogleForms {
    @Rule
    public ScreenShooter screenShooter = ScreenShooter.failedTests();
    public Random Rand = new Random();


    public String FormMonth = String.format("%02d" , Rand.nextInt(12)+1);
    public String FormDay = String.format("%02d" , Rand.nextInt(12)+1);
    public String FormYear = "2020";

    public Formatter fmt = new Formatter();
    public Calendar cal = Calendar.getInstance();
    public String FormCurrentMonthByWords = fmt.format("%tB", cal).toString();
    public String[] Movies = {"Terminator I", "Terminator II", "Commando", "Conan", "True Lies"};

    @BeforeClass
    public static void PageOpen() {
        timeout = 6000;
        baseUrl = "https://docs.google.com/";
        startMaximized = false;
        browser = "chrome";
        browserPosition = "0x0";
        browserSize = "1440x700";
        open("/forms/d/e/1FAIpQLScNx9xK2LM-G3Z3fJXOQapiSK1IAoNXc_67MyS-soTfhDXotA/viewform");
    }

    @Test
    public void CheckTest() {
        // CHECK IF WE'RE ON THE FIRST PAGE
        $(byText("This is a form for the QA Automation test.")).exists();

        // CHECKERS

        ElementsCollection checkers = $(By.cssSelector("div[jsname='JNdkSc']")).waitUntil(visible, 2000).findAll("div[data-value='Check this']");

        checkers.forEach(SelenideElement::click);
        checkers.shouldHave(CollectionCondition.size(checkers.size()));

        // DATE FIELD
        $(By.className("quantumWizTextinputPaperinputInput")).val(FormDay + "/" + FormMonth + "/" + FormYear);
        $(By.className("quantumWizTextinputPaperinputInput")).shouldHave(attribute("value", FormYear + "-" + FormMonth + "-" + FormDay));

        // INPUT FIELD (MANDATORY/NOT)
        $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).val("");
        $(By.cssSelector("div[id='i.err.1806505028']")).shouldNotBe(visible);
        $(By.className("quantumWizButtonPaperbuttonLabel")).click();
        $(By.cssSelector("div[id='i.err.1806505028']")).shouldBe(visible);
        $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).val(FormCurrentMonthByWords);
        $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).shouldHave(attribute("value", FormCurrentMonthByWords));

        // MOVE TO PAGE 2 (NO WAY TO DO W/O SLEEP)
        sleep(1000);
        $(By.className("quantumWizButtonPaperbuttonLabel")).waitUntil(visible, 3000).click();


        // CHECK IF YOU'RE ON PAGE 2
        $(byText("Create list of your favorite movies and series (5 or more) and fill three random of them each on new line")).exists();

        // FILL 5 RANDOM MOVIES
        $(By.cssSelector(".quantumWizTextinputPapertextareaInput")).sendKeys(Movies[Rand.nextInt(5)] + Keys.RETURN + Movies[Rand.nextInt(5)] + Keys.RETURN + Movies[Rand.nextInt(5)] + Keys.TAB);
        String TextValueToCheck = $(By.cssSelector(".quantumWizTextinputPapertextareaInput")).getValue();

        // CLICK ON A FAVOURITE COLOR
        $(By.xpath("//span[text()=\"Red\"]")).click();

        // GET BACK TO THE FIRST PAGE
        $$("span[class='appsMaterialWizButtonPaperbuttonLabel quantumWizButtonPaperbuttonLabel exportLabel']").first().click();

        // CHECH IF IT'S REALLY FIRST PAGE
        $(byText("This is a form for the QA Automation test.")).exists();

        //REVERS ANSWER FOR LAST QUESTION
        String answer = $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).val();
        StringBuilder sb = new StringBuilder(answer);
        String reverse_answer = sb.reverse().toString();
        $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).val(reverse_answer);
        $(By.cssSelector("input[aria-label='Check that this question is mandatory, than fill it with name of current month']")).shouldHave(attribute("value", reverse_answer));

        // MOVE TO PAGE 2 WITH CHECK
        $(By.className("quantumWizButtonPaperbuttonLabel")).waitUntil(visible, 2000).click();
        $(byText("Create list of your favorite movies and series (5 or more) and fill three random of them each on new line")).exists();

        // MOVE TO PAGE 3
        $$("span[class='appsMaterialWizButtonPaperbuttonLabel quantumWizButtonPaperbuttonLabel exportLabel']").get(1).click();
        $(byText("Just fill last question and send the form.")).exists();

        // LAST RADIOBUTTON
        $(By.xpath("//span[text()='Yes']")).click();

        // SUBMIT
        $$("div[role='button']").get(1).click();

        // CHECK THE RESPONSE
        $(byText("Thank you for your response.")).exists();
    }


    @AfterClass
    public static void logout() {
        closeWebDriver();
    }


}

