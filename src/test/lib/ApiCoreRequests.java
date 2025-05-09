package lib;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class ApiCoreRequests {
    private static final String BASE_URL = "https://playground.learnqa.ru/api_dev/user/";

    public String getBaseUrl() {
        return BASE_URL;
    }

    @Step("Make POST request to create user")
    public Response makePostRequest(Map<String, String> data) {
        return RestAssured
                .given()
                .body(data)
                .post(BASE_URL)
                .andReturn();
    }

    // Создание пользователя с email
    @Step("Create user with email: {email}")
    public Response createUserWithEmail(String email) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", email);
        return makePostRequest(userData);
    }

    // Создание пользователя без указания одного из полей
    @Step("Create user without field: '{field}'")
    public Response createUserWithoutField(String field) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field);
        return makePostRequest(userData);
    }

    // Создание пользователя с очень коротким именем в один символ
    @Step("Create user with very short name (1 character)")
    public Response createUserWithShortName() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", "A");
        return makePostRequest(userData);
    }

    // Создание пользователя с очень длинным именем - длиннее 250 символов
    @Step("Create user with very long name (251 characters)")
    public Response createUserWithLongName() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", new String(new char[251]).replace("\0", "A"));
        return makePostRequest(userData);
    }

    //Изменение данных пользователя
    @Step("Make PUT request to edit user")
    public Response makePutRequest(String url, Map<String, String> editData, String token, String cookie) {
        return RestAssured
                .given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(editData)
                .put(url)
                .andReturn();
    }

    // Изменение неавторизованным пользователем
    @Step("Make PUT request without auth to edit user")
    public Response makePutRequestWithoutAuth(String url, Map<String, String> editData) {
        return RestAssured
                .given()
                .body(editData)
                .put(url)
                .andReturn();
    }

    // Авторизация
    @Step("Login user with email: {email} and password: {password}")
    public Response loginUser(String email, String password) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        return RestAssured
                .given()
                .body(authData)
                .post(BASE_URL + "login")
                .andReturn();
    }

    // Регистрация новым пользователем
    @Step("Register new random user")
    public Response registerRandomUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        return makePostRequest(userData);
    }

    // Неверный e-mail
    @Step("Make PUT request to edit user with invalid email")
    public Response editUserWithInvalidEmail(String url, Map<String, String> editData, String token, String cookie) {
        return makePutRequest(url, editData, token, cookie);
    }

    // Короткое имя пользователя
    @Step("Make PUT request to edit user with very short firstName")
    public Response editUserWithShortFirstName(String url, Map<String, String> editData, String token, String cookie) {
        return makePutRequest(url, editData, token, cookie);
    }

    // Удаление с авторизацией
    @Step("Make DELETE request with auth")
    public Response makeDeleteRequest(String url, String token, String cookie) {
        return RestAssured
                .given()
                .header("x-csrf-token", token != null ? token : "")
                .cookie("auth_sid", cookie != null ? cookie : "")
                .delete(url)
                .andReturn();
    }

    // Запрос м авторизацией
    @Step("Make GET request with auth")
    public Response makeGetRequest(String url, String token, String cookie) {
        return RestAssured
                .given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    // Запрос без авторизации
    @Step("Make GET request without auth")
    public Response makeGetRequestWithoutAuth(String url) {
        return RestAssured
                .given()
                .get(url)
                .andReturn();
    }

    @Step("Make GET auth request")
    public Response makeGetAuthRequest(String header, String cookie) {
        return RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get(BASE_URL + "auth")
                .andReturn();
    }

    @Step("Make GET auth request with header only")
    public Response makeGetAuthRequestWithHeaderOnly(String header) {
        return RestAssured
                .given()
                .header("x-csrf-token", header)
                .get(BASE_URL + "auth")
                .andReturn();
    }
}