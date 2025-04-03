import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Cookie {

    @Test
    public void CookieTest() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookieValue = response.getCookie("HomeWork");
        assertEquals("hw_value", cookieValue, "Некорректное значение Cookie");
    }
}
