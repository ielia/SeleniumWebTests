import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainTest {
    private static final Logger log = LoggerFactory.getLogger(MainTest.class);
    protected static final Map<String, List<MessagePair>> testMessages = new LinkedHashMap<>();
    protected WebDriver driver;
    protected long testStartTime;
    protected TestInfo testInfo;

    static Stream<Arguments> localParameters() {
        return Stream.of(
            Arguments.of("http://www.bing.com/"),
            Arguments.of("https://www.google.com/")
        );
    }

    protected void logTime(String category, long startTime) {
        long timespan = new Date().getTime() - startTime;
        String message = category + " Time: " + timespan + "ms";
        String key = testInfo.getTestMethod().get().getName() + " - " + category;
        String arguments = testInfo.getDisplayName().replaceFirst("^\\[\\d+\\] => ", "");
        testMessages.computeIfAbsent(key, k -> new LinkedList<>()).add(new MessagePair(arguments, message, timespan));
        log.info(message);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        this.testInfo = testInfo;
        // System.setProperty("webdriver.chrome.verboseLogging", "true");
        long start = new Date().getTime();
        WebDriverManager.chromedriver().setup();
        logTime("<><><><><><><><><><> PREPARATION", start);
        start = new Date().getTime();
        driver = new ChromeDriver();
        logTime("<><><><><><><><><><> DRIVER CREATION", start);
        testStartTime = new Date().getTime();
    }

    @ParameterizedTest(name = "[{index}] => {0}")
    @MethodSource("localParameters")
    public void test1(String url) {
        long start = new Date().getTime();
        driver.get("data:,");
        //log.info("<><><><><><><><><><> TITLE: {}", driver.getTitle());
        logTime("<><><><><><><><><><> WINDOW OPEN", start);
        start = new Date().getTime();
        driver.get(url);
        logTime("<><><><><><><><><><> URL OPEN", start);
    }

    @ParameterizedTest(name = "[{index}] => {0}")
    @MethodSource("localParameters")
    public void test2(String url) {
        long start = new Date().getTime();
        driver.get(url);
        logTime("<><><><><><><><><><> MY URL OPEN", start);
        start = new Date().getTime();
        WebElement q = driver.findElement(By.name("q"));
        logTime("<><><><><><><><><><> GOT ELEMENT", start);
        start = new Date().getTime();
        String classList = q.getAttribute("class");
        logTime("<><><><><><><><><><> GOT ATTRIBUTE", start);
        log.info("<><><><><><><><><><> Class name: {}", classList);
    }

    @AfterEach
    public void afterEach() {
        logTime("<><><><><><><><><><> TEST RUN", testStartTime);
        long start = new Date().getTime();
        driver.quit();
        logTime("<><><><><><><><><><> DRIVER QUIT", start);
    }

    @AfterAll
    public static void afterAll() {
        log.info("CSV: operation name, {}", localParameters().map(e -> "time in ms for " + e.get()[0]).collect(Collectors.joining(", ")));
        for (Map.Entry<String, List<MessagePair>> entry : testMessages.entrySet()) {
            //log.info("CSV: {}, {}", entry.getKey(), entry.getValue().stream().map(e -> "(" + e.arguments + ") " + e.timespan).collect(Collectors.joining(", ")));
            log.info("CSV: {}, {}", entry.getKey(), entry.getValue().stream().map(e -> "" + e.timespan).collect(Collectors.joining(", ")));
        }
    }
}
