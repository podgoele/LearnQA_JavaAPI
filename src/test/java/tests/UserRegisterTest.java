package tests;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lib.ApiCoreRequests;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration user with uncorrect email.")
    @DisplayName("Test negative register user with uncorrect email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(apiCoreRequests.getBaseUrl())
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
   @Story("Успешная регистрация пользователя")
    @Severity(value = SeverityLevel.BLOCKER)
    @Description("This test successfully register new user with correct user data.")
    @DisplayName("Test positive register new user")
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(apiCoreRequests.getBaseUrl())
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
   @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration user with uncorrect email.")
    @DisplayName("Test negative register user with uncorrect email")
    public void testCreateUserWithInvalidEmail() {
        String email = "invalidemail.com";
        Response response = apiCoreRequests.createUserWithEmail(email);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }
    
    
    @Test
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration user without required fields.")
    @DisplayName("Test negative register user without required field")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutRequiredField(String field) {
        Response response = apiCoreRequests.createUserWithoutField(field);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: " + field);
    }

    @Test
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.NORMAL)
    @DisplayName("Test negative register user with uncorrect user name")
      @DisplayName("Test negative register user with very short name")
    public void testCreateUserWithVeryShortName() {
        Response response = apiCoreRequests.createUserWithShortName();
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.NORMAL)
    @DisplayName("Test negative register user with very long name")
    public void testCreateUserWithVeryLongName() {
        Response response = apiCoreRequests.createUserWithLongName();
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }
}
