package api;

import Utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Login {

    private String ENDPOINT = "/login/token";

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Response obtainToken(String email, String password, String domain){
        Credentials credentials = new Credentials(email, password, domain);
        return RestAssured.given()
                .log().all()
                .baseUri(Config.BASE_URL)
                .basePath(Config.BASE_PATH)
                .header("Content-type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .body(gson.toJson(credentials))
                .post(ENDPOINT).prettyPeek();
    }
    public String getToken(){
        Login login = new Login();
        return
                login.obtainToken(Config.EMAIL, Config.PASSWORD, Config.DOMAIN).jsonPath().getString("token");
    }

    public static void main(String[] args) {
        Login login = new Login();
        System.out.println(login.getToken());
    }
}
