package api;

import Utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.policy.AssertionSet;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static Utils.Config.BASE_PATH;
import static Utils.Config.BASE_URL;

public class ItemApi {

    private String ENDPOINT = "/items";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String UNIT_ENDPOINT = "/items/units";

    public Response getAllItems(String token){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .get(ENDPOINT).prettyPeek();
    }

    public Response createItem (String token, Item item){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .body(gson.toJson(item))
                .post(ENDPOINT).prettyPeek();
    }
    public Item itemBuilder(String name, String quantityUnit, double price){
        Item item = new Item.ItemBuilder()
                .name(name)
                .quantity_unit(quantityUnit)
                .price_for_quantity(1)
                .currency("BGN")
                .price(price).build();
        return item;
    }
    public Response deleteItem(String token, Integer id){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .delete(ENDPOINT+"/"+id).prettyPeek();
    }
    public Response getSingleItem(String token, Integer id){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .get(ENDPOINT + "/" + id).prettyPeek();
    }

    public Response updateItem(String token, int id, Item updatedItem){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .body(gson.toJson(updatedItem))
                .patch(ENDPOINT + "/" + id).prettyPeek();
    }
    public Response getItemUnits(String token){
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .get(UNIT_ENDPOINT).prettyPeek();
    }
    public Response createUnit(String token, String name){
        Item unit = new Item.ItemBuilder().name(name).build();
        return RestAssured.given()
                .log().all()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .auth().oauth2(token)
                .header("Content-Type", "application/json")
                .header("User-agent", "Mozilla")
                .accept(ContentType.JSON)
                .body(gson.toJson(unit))
                .post(UNIT_ENDPOINT).prettyPeek();
    }

    public static void main(String[] args) {
        ItemApi items = new ItemApi();
        Login login = new Login();
        String token = login.getToken();
     //   items.deleteItem(token, 75);
        items.getAllItems(token);

    }
}
