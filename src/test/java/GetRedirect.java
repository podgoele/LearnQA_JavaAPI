import io.restassured.RestAssured;
import  io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class GetRedirect {
    @Test
    public void testGetRedirect(){

         Response response= RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect");

        String headerURL = response.getHeader("Location");
        System.out.println(headerURL);

    }
}