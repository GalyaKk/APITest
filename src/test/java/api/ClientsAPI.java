package api;

import Utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.api.server.EndpointAwareCodec;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static Utils.Config.BASE_PATH;
import static Utils.Config.BASE_URL;

public class ClientsAPI {
    private static final String ENDPOINT = "/clients";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String RESTORE_ENDPOINT = "/restore";

    public Response getAllClients(String token){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("User-Agent", "Mozilla")
                .header("Content-Type", "application/json")
                .accept(ContentType.JSON)
                .get(ENDPOINT).prettyPeek();
    }
    public Response createClient (String token, Clients client){
        return RestAssured.given()
                .log().all()
                .auth().oauth2(token)
                .baseUri(Config.BASE_URL)
                .basePath(BASE_PATH)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla")
                .accept(ContentType.JSON)
                .body(gson.toJson(client))
                .post(ENDPOINT).prettyPeek();
    }
    public Response getSingleClient(String token, int id){
        return RestAssured.given()
                .log().all()
                .auth().oauth2(token)
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla")
                .accept(ContentType.JSON)
                .get(ENDPOINT+"/"+id).prettyPeek();
    }
    public Response deleteClient(String token, int id){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla")
                .accept(ContentType.JSON)
                .delete(ENDPOINT + "/" + id).prettyPeek();
    }
    public Response updateClient(String token, int id){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla")
                .accept(ContentType.JSON)
                .patch(ENDPOINT+ "/" + id).prettyPeek();
    }
    public Response restoreClient(String token, int id){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla")
                .accept(ContentType.JSON)
                .patch(ENDPOINT + "/" + id + RESTORE_ENDPOINT).prettyPeek();
    }

    public static void main(String[] args) {
        ClientsAPI clients = new ClientsAPI();
        Login login = new Login();
        String token = login.getToken();
        clients.getAllClients(token);
    }
}