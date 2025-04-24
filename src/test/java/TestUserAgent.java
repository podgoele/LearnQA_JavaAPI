import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUserAgent {


    private static final List<String> incorrectUserAgents = new ArrayList<>();


    private static Stream<Arguments> provideUserAgents() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Android",
                        "No",
                        "Mobile"
                ),
                Arguments.of(
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "iOS",
                        "Chrome",
                        "Mobile"
                ),
                Arguments.of(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Unknown",
                        "Unknown",
                        "Googlebot"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
                        "No",
                        "Chrome",
                        "Web"
                ),
                Arguments.of(
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Unknown",
                        "Chrome",
                        "Web"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserAgents")
    public void testUserAgent(String userAgent, String expectedDevice, String expectedBrowser, String expectedPlatform) {

        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();


        String actualDevice = response.jsonPath().getString("device");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualPlatform = response.jsonPath().getString("platform");


        boolean isDeviceCorrect = expectedDevice.equals(actualDevice);
        boolean isBrowserCorrect = expectedBrowser.equals(actualBrowser);
        boolean isPlatformCorrect = expectedPlatform.equals(actualPlatform);


        if (!isDeviceCorrect || !isBrowserCorrect || !isPlatformCorrect) {
            String incorrectParams = getIncorrectParams(isDeviceCorrect, isBrowserCorrect, isPlatformCorrect);
            incorrectUserAgents.add("User-Agent: " + userAgent + " - Неправильные параметры: " + incorrectParams);
        }


        assertEquals(expectedDevice, actualDevice, "Неправильное значение device для User-Agent: " + userAgent);
        assertEquals(expectedBrowser, actualBrowser, "Неправильное значение browser для User-Agent: " + userAgent);
        assertEquals(expectedPlatform, actualPlatform, "Неправильное значение platform для User-Agent: " + userAgent);
    }

    @AfterAll
    public static void printIncorrectUserAgents() {

        if (!incorrectUserAgents.isEmpty()) {
            System.out.println("User-Agent с неправильными ответами:");
            incorrectUserAgents.forEach(System.out::println);
        } else {
            System.out.println("Все User-Agent вернули правильные ответы.");
        }
    }


    private String getIncorrectParams(boolean isDeviceCorrect, boolean isBrowserCorrect, boolean isPlatformCorrect) {
        StringBuilder incorrectParams = new StringBuilder();
        if (!isDeviceCorrect) {
            incorrectParams.append("device, ");
        }
        if (!isBrowserCorrect) {
            incorrectParams.append("browser, ");
        }
        if (!isPlatformCorrect) {
            incorrectParams.append("platform, ");
        }
        return incorrectParams.toString().replaceAll(", $", "");
    }
}