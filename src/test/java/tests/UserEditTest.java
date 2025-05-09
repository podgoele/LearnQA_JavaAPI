package tests;

import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void testEditJustCreatedTest() {
        //Генерация пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(apiCoreRequests.getBaseUrl())
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(apiCoreRequests.getBaseUrl() + "login")
                .andReturn();

        //Изменение
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put(apiCoreRequests.getBaseUrl() + userId)
                .andReturn();

        //Получение измененных данных
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get(apiCoreRequests.getBaseUrl() + userId)
                .andReturn();

        Assertions.asserJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @TmsLink ("User_Edit_1")
    public void testEditUserNotAuth() {
        // Регистрация пользователя
        Response responseCreateAuth = apiCoreRequests.registerRandomUser();
        String userId = responseCreateAuth.jsonPath().getString("id");

        // Попытка редактирования без авторизации
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithoutAuth(
                apiCoreRequests.getBaseUrl() + userId,
                editData
        );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.asserJsonByName(responseEditUser, "error","Auth token not supplied");
    }
    @Test
    public void testEditUserAuthAsOtherUser() {
        // 1. Регистрация первого пользователя
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Response responseCreateAuth1 = apiCoreRequests.makePostRequest(userData1);
        String userId1 = responseCreateAuth1.jsonPath().getString("id");

        // 2. Регистрация второго пользователя
        Map<String, String> userData2 = DataGenerator.getRegistrationData();
        Response responseCreateAuth2 = apiCoreRequests.makePostRequest(userData2);

        // 3. Авторизация вторым пользователем
        Response responseGetAuth2 = apiCoreRequests.loginUser(
                userData2.get("email"),
                userData2.get("password")
        );

        String token2 = this.getHeader(responseGetAuth2, "x-csrf-token");
        String cookie2 = this.getCookie(responseGetAuth2, "auth_sid");

        // 4. Попытка редактирования первого пользователя
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "NewName");

        Response responseEditUser = apiCoreRequests.makePutRequest(
                apiCoreRequests.getBaseUrl() + userId1,
                editData,
                token2,
                cookie2
        );

        System.out.println("Edit response: " + responseEditUser.asString());

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser, "error");

    }

    @Test
    public void testEditUserEmailToInvalidFormat() {
        // 1. Регистрация нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.registerRandomUser();
        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");

        String userId = responseCreateAuth.jsonPath().getString("id");
        String originalEmail = userData.get("email");

        // 2. Авторизация пользователя
        Response responseGetAuth = apiCoreRequests.loginUser(
                originalEmail,
                userData.get("password")
        );

        // Проверяем успешную авторизацию
        Assertions.assertResponseCodeEquals(responseGetAuth, 200);

        // Получаем токен и куки
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 3. Подготавливаем невалидные данные для изменения
        Map<String, String> invalidEditData = new HashMap<>();
        invalidEditData.put("email", "invalidemail.com"); // email без @

        // 4. Отправляем запрос на изменение с невалидным email
        Response responseEditUser = apiCoreRequests.editUserWithInvalidEmail(
                apiCoreRequests.getBaseUrl() + userId,
                invalidEditData,
                token,
                cookie
        );

        // 5. Проверяем, что сервер вернул ошибку
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.asserJsonByName(responseEditUser, "error", "Invalid email format");
    }

    @Test
    public void testEditUserFirstNameToVeryShort() {
        // 1. Регистрация пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        // 2. Авторизация
        Response responseGetAuth = apiCoreRequests.loginUser(
                userData.get("email"),
                userData.get("password")
        );

        // Проверяем успешную авторизацию и получаем токен/куки
        Assertions.assertResponseCodeEquals(responseGetAuth, 200);
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // 3. Попытка изменения firstName на 1 символ
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "A");

        Response responseEditUser = apiCoreRequests.makePutRequest(
                apiCoreRequests.getBaseUrl() + userId,
                editData,
                token,
                cookie
        );

    }
}
