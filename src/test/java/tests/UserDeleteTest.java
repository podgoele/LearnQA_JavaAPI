package tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    // Тест на удаление пользователя по id2
    @Test
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteProtectedUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // Авторизация
        Response authResponse = apiCoreRequests.loginUser(authData.get("email"), authData.get("password"));
        String token = getHeader(authResponse, "x-csrf-token");
        String cookie = getCookie(authResponse, "auth_sid");

        // Попытка удалить пользователя с ID 2
        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                apiCoreRequests.getBaseUrl() + "2",
                token,
                cookie
        );

        // Проверка, что система не позволяет удалить этого пользователя
        Assertions.assertResponseCodeEquals(deleteResponse, 400);
    }

    // Удаление созданного пользователя
    @Test
    @TmsLink("User-Delete-01")
    public void testDeleteJustCreatedUser() {
        // 1. Создание нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response createResponse = apiCoreRequests.makePostRequest(userData);
        String userId = createResponse.jsonPath().getString("id");
        String userEmail = userData.get("email");

        // 2. Авторизация под этим пользователем
        Response loginResponse = apiCoreRequests.loginUser(userEmail, userData.get("password"));
        String token = loginResponse.getHeader("x-csrf-token");
        String cookie = loginResponse.getCookie("auth_sid");

        // 3. Проверка, что авторизация прошла успешно
        Assertions.assertResponseCodeEquals(loginResponse, 200);
        Assertions.assertJsonHasField(loginResponse, "user_id");

        // 4. Удаляем пользователя
        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                apiCoreRequests.getBaseUrl() + userId,
                token,
                cookie
        );
        Assertions.assertResponseCodeEquals(deleteResponse, 200);

        // 5. Проверяем, что пользователь действительно удален
        Response getResponse = apiCoreRequests.makeGetRequestWithoutAuth(
                apiCoreRequests.getBaseUrl() + userId
        );
        Assertions.assertResponseCodeEquals(getResponse, 404);

    }

    @Test
    public void testDeleteUserWithAnotherAuth() {
        // 1. Создаем пользователя, которого будем пытаться удалить
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response createUserResponse = apiCoreRequests.makePostRequest(userData);
        String userId = createUserResponse.jsonPath().getString("id");
        String userEmail = userData.get("email");

        // 2. Создание другого пользователя для авторизации
        Map<String, String> anotherUserData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest(anotherUserData);
        String anotherUserEmail = anotherUserData.get("email");

        // 3. Авторизация под вторым пользователем
        Response authResponse = apiCoreRequests.loginUser(anotherUserEmail, anotherUserData.get("password"));
        String token = authResponse.getHeader("x-csrf-token");
        String cookie = authResponse.getCookie("auth_sid");

        // 4. Пытаемся удалить первого пользователя
        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                apiCoreRequests.getBaseUrl() + userId,
                token,
                cookie
        );

        // 5. Проверка, что сервер возвращает ошибку
        Assertions.assertResponseCodeEquals(deleteResponse, 400);

        // 6. Проверка, что первый пользователь все еще существует
        Response getUserResponse = apiCoreRequests.makeGetRequestWithoutAuth(
                apiCoreRequests.getBaseUrl() + userId
        );
        Assertions.assertResponseCodeEquals(getUserResponse, 200);
    }
}
