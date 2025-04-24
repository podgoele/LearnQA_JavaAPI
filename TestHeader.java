import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHeader {

    @Test
    public void TestHeader() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String headerValue = response.getHeader("x-secret-homework-header");

        assertEquals("Some secret value", headerValue, "Значение header не соответствует ожидаемому");
    }
}
