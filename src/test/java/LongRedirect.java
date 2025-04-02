import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirect {
    @Test
    public void testLongRedirect(){
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int count = 0;
        int statusCode = 0;
        while (statusCode!=200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(url);

            if(response.getStatusCode()!=200) {
                count+=1;
                url = response.getHeader("Location");
            }
            statusCode = response.getStatusCode();
        }

        System.out.println(url);
        System.out.println("Url редиректа: " + url);
        System.out.println("Кол-во редиректов: " + count);

    }
}

