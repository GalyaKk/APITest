package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import io.restassured.common.mapper.resolver.ObjectMapperResolver;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lombok.var;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class ItemApiTest {

    private String token;
    ItemApi itemApi = new ItemApi();

    @Before
    public void setToken(){
        Login loginLogin = new Login();
        token = loginLogin.getToken();
    }
    @After
    public void deleteAllItems(){
        Response getAllItemsResponse = itemApi.getAllItems(token);
        ResponseBody body = getAllItemsResponse.body();
        String response = body.asString();
        List<Integer> itemIds = JsonPath.read(response, "$.items..id");
        for (int id:itemIds) {
           itemApi.deleteItem(token, id);
        }
    }

@Test
    public void canCreateAnItemOnlyWithRequiredFields(){
    Item beer = new Item.ItemBuilder()
            .name("beer"+LocalDateTime.now().toString().substring(0,16))
            .price_for_quantity(1)
            .quantity_unit("can").build();
    Response createRespose = itemApi.createItem(token, beer);
    Assertions.assertEquals(201, createRespose.statusCode());
    Integer id = createRespose.jsonPath().getInt("id");
    Response getCreatedItemResponse = itemApi.getSingleItem(token, id);
    Assertions.assertEquals("beer" + LocalDateTime.now().toString().substring(0,16), getCreatedItemResponse.jsonPath().getString("name"));
    Assertions.assertEquals(1, getCreatedItemResponse.jsonPath().getInt("price_for_quantity"));
    Assertions.assertEquals("can", getCreatedItemResponse.jsonPath().getString("quantity_unit"));
}
@Test
public void canCreateAnItemWithPrice(){
    Item beer = new Item.ItemBuilder()
            .name("beer"+LocalDateTime.now().toString().substring(0,16))
            .price_for_quantity(1)
            .quantity_unit("can")
            .price(5.5).build();
    Response createRespose = itemApi.createItem(token, beer);
    Assertions.assertEquals(201, createRespose.statusCode());
    Integer id = createRespose.jsonPath().getInt("id");
    Response getCreatedItemResponse = itemApi.getSingleItem(token, id);
    Assertions.assertEquals("beer" + LocalDateTime.now().toString().substring(0,16), getCreatedItemResponse.jsonPath().getString("name"));
    Assertions.assertEquals(5.5, getCreatedItemResponse.jsonPath().getDouble("price"));
    Assertions.assertEquals(1, getCreatedItemResponse.jsonPath().getInt("price_for_quantity"));
    Assertions.assertEquals("can", getCreatedItemResponse.jsonPath().getString("quantity_unit"));
    Assertions.assertEquals("BGN", getCreatedItemResponse.jsonPath().getString("currency"));
}
@Test
public void cantCreateItemWithSameName(){
        Item beer = itemApi.itemBuilder("beer", "can", 5.5);
        Response createResponse = itemApi.createItem(token, beer);
        Assertions.assertEquals(201, createResponse.statusCode());
        Item sameBeer = itemApi.itemBuilder("beer", "bottle", 6.0);
        Response createSameBeerResponse = itemApi.createItem(token, sameBeer);
        Assertions.assertEquals(400, createSameBeerResponse.statusCode());
        Assertions.assertEquals("Съществува артикул с това име!", createSameBeerResponse.jsonPath().getString("error"));
}

@Test
public void canGetSingleItem(){
    //preconditions - have an item created
    Item soda = new Item.ItemBuilder()
            .name("soda")
            .price(1.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response createRespose = itemApi.createItem(token, soda);
    int id = createRespose.jsonPath().getInt("id");
    //get the created item
    Response getResponse = itemApi.getSingleItem(token, id);
    Assertions.assertEquals(200, getResponse.statusCode());
    Assertions.assertEquals("soda", getResponse.jsonPath().getString("name"));
    Assertions.assertEquals(1.5, getResponse.jsonPath().getDouble("price"));
    Assertions.assertEquals(1, getResponse.jsonPath().getInt("price_for_quantity"));
    Assertions.assertEquals("BGN", getResponse.jsonPath().getString("currency"));
    Assertions.assertEquals("can", getResponse.jsonPath().getString("quantity_unit"));
}
@Test
public void cantGetAnItemWhenMissing(){
    //preconditions - have an item created
    Item soda = new Item.ItemBuilder()
            .name("soda")
            .price(1.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response createRespose = itemApi.createItem(token, soda);
    int id = createRespose.jsonPath().getInt("id");
    //delete the item
    itemApi.deleteItem(token, id);
    //get the same item
    Response getResponse = itemApi.getSingleItem(token, id);
    Assertions.assertEquals(404, getResponse.statusCode());
}
@Test
public void canDeleteAnItem(){
    //preconditions - have an item created
    Item soda = new Item.ItemBuilder()
            .name("soda")
            .price(1.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response createRespose = itemApi.createItem(token, soda);
    //delete the item
    int id = createRespose.jsonPath().getInt("id");
    Response deleteRespose = itemApi.deleteItem(token, id);
    Assertions.assertEquals(204, deleteRespose.statusCode());
    Response getSingleItemRespose = itemApi.getSingleItem(token, id);
    Assertions.assertEquals(404, getSingleItemRespose.statusCode());
}
@Test
    public void canDeleteAllItems(){
    //preconditions - have some items created
    itemApi.createItem(token, itemApi.itemBuilder("beer", "can", 2.5));
    itemApi.createItem(token, itemApi.itemBuilder("tea", "pack", 6.6));
    itemApi.createItem(token, itemApi.itemBuilder("beerCarlsberg", "can", 3.5));
    itemApi.createItem(token, itemApi.itemBuilder("beerGlasrus", "can", 4.5));
    itemApi.createItem(token, itemApi.itemBuilder("beerRhombus", "can", 5.5));
    //get all items in order to take their ids
    Response getAllItemsResponse = itemApi.getAllItems(token);
    ResponseBody body = getAllItemsResponse.body();
    String response = body.asString();
    List<Integer> itemIds = JsonPath.read(response, "$.items..id");
    //delete all items
    for (int id:itemIds) {
        Response deleteResponse = itemApi.deleteItem(token, id);
        Assertions.assertEquals(204, deleteResponse.statusCode());
    }
    Response getAllResponse = itemApi.getAllItems(token);
    Assertions.assertEquals(200, getAllResponse.statusCode());
    Assertions.assertEquals(0,getAllResponse.jsonPath().getInt("total"));
}
@Test
    public void canUpdateAnItem (){
    //String token = Login.getToken();
    Item beerGlarus =new Item.ItemBuilder()
            .name("GlarusBeer")
            .price(4.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response createResponse = itemApi.createItem(token, beerGlarus);
    int id = createResponse.jsonPath().getInt("id");
    //update the created item
    Item beerGlarusUpdated =new Item.ItemBuilder()
            .name("GlarusBeer")
            .price(5.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response updateResponse = itemApi.updateItem(token, id, beerGlarusUpdated);
    Assertions.assertEquals(204, updateResponse.statusCode());
    Assertions.assertEquals("beer-Carlsberg", updateResponse.jsonPath().getString("name"));
    Assertions.assertEquals(2.5, updateResponse.jsonPath().getInt("price"));
    Assertions.assertEquals(1, updateResponse.jsonPath().getString("price_for_quantity"));
    Assertions.assertEquals("BGN", updateResponse.jsonPath().getString("currency"));
    Assertions.assertEquals("can", updateResponse.jsonPath().getString("quantity_unit"));
}
@Test
    public void retrieveItemsWhenMissing(){
    //precondition - delete all items
    Response getAllItemsResponse = itemApi.getAllItems(token);
    ResponseBody body = getAllItemsResponse.body();
    String response = body.asString();
    List<Integer> itemIds = JsonPath.read(response, "$.items..id");
    for (int id:itemIds) {
        itemApi.deleteItem(token, id);
    }
    //get all items
    Response getAllResponse = itemApi.getAllItems(token);
    Assertions.assertEquals(200, getAllResponse.statusCode());
    Assertions.assertEquals(0, getAllResponse.jsonPath().getInt("total"));
    }
@Test
public void canGetAllItems(){
        //have some items created
    itemApi.createItem(token, itemApi.itemBuilder("beer", "can", 2.5));
    itemApi.createItem(token, itemApi.itemBuilder("tea", "pack", 6.6));
    itemApi.createItem(token, itemApi.itemBuilder("beerCarlsberg", "can", 3.5));
    itemApi.createItem(token, itemApi.itemBuilder("beerGlasrus", "can", 4.5));
    itemApi.createItem(token, itemApi.itemBuilder("beerRhombus", "can", 5.5));
    //get all items
        Response getResponse = itemApi.getAllItems(token);
        Assertions.assertEquals(200, getResponse.statusCode());
        String body = getResponse.body().asString();
        List<Integer> itemIds = JsonPath.read(body,"$.items..id");
        Assertions.assertEquals(itemIds.size(), getResponse.jsonPath().getInt("total"));
}
@Test
public void getAnItemThatsMissing(){
        //precondition - create an item
    Item beerCarlsberg = new Item.ItemBuilder()
            .name("CarlsbergBeer")
            .price(3.5)
            .price_for_quantity(1)
            .currency("BGN")
            .quantity_unit("can").build();
    Response createResponse = itemApi.createItem(token,beerCarlsberg);
    int id = createResponse.jsonPath().getInt("id");
      //delete the item
    itemApi.deleteItem(token, id);
      //get the deleted item
    Response getDeletedItemResponse = itemApi.getSingleItem(token, id);
    Assertions.assertEquals(404, getDeletedItemResponse.statusCode());
    Assertions.assertEquals("Item Not Found", getDeletedItemResponse.jsonPath().getString("error"));
}
    @Test
    public void cantCreateAnItemWithoutNameField(){
        Item beer = new Item.ItemBuilder()
                .price(2.5)
                .price_for_quantity(1)
                .currency("BGN")
                .quantity_unit("can").build();
        Response createRespose = itemApi.createItem(token, beer);
        Assertions.assertEquals(400, createRespose.statusCode());
        Assertions.assertEquals("The key \"name\" is missing in the request body JSON", createRespose.jsonPath().getString("error"));
    }
    @Test
    public void cantCreateAnItemWithoutPriceForQuantityField(){
        Item beer = new Item.ItemBuilder()
                .name("beer")
                .price(2.5)
                .currency("BGN")
                .quantity_unit("can").build();
        Response createRespose = itemApi.createItem(token, beer);
        Assertions.assertEquals(400, createRespose.statusCode());
    }
    @Test
    public void cantCreateAnItemWithoutQuantityUnitField(){
        Item beer = new Item.ItemBuilder()
                .name("beerGlarus")
                .price(5.5)
                .price_for_quantity(1)
                .currency("BGN")
                .build();
        Response createRespose = itemApi.createItem(token, beer);
        Assertions.assertEquals(400, createRespose.statusCode());
        Assertions.assertEquals("The key \"quantity_unit\" is missing in the request body JSON", createRespose.jsonPath().getString("error"));
    }
    @Test
    public void canCreateItemWithAllFields(){
        Item tea = new Item.ItemBuilder()
                .name("чай")
                .price_for_quantity(1)
                .quantity_unit("package")
                .currency("BGN")
                .price(5.0)
                .name_en("tea")
                .catalog_number(5)
                .outside_id(3112)
                .is_limited(true).build();
        Response createResponse = itemApi.createItem(token, tea);
        Response getTeaItemResponse = itemApi.getSingleItem(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("чай", getTeaItemResponse.jsonPath().getString("name"));
        Assertions.assertEquals("package", getTeaItemResponse.jsonPath().getString("quantity_unit"));
        Assertions.assertEquals("BGN", getTeaItemResponse.jsonPath().getString("currency"));
        Assertions.assertEquals(1, getTeaItemResponse.jsonPath().getInt("price_for_quantity"));
        Assertions.assertEquals(5.0, getTeaItemResponse.jsonPath().getInt("price"));
        Assertions.assertEquals("tea", getTeaItemResponse.jsonPath().getString("name_en"));
        Assertions.assertEquals(3, getTeaItemResponse.jsonPath().getInt("catalog_number"));
        Assertions.assertEquals(3112, getTeaItemResponse.jsonPath().getInt("outside_id"));
        Assertions.assertEquals(true, getTeaItemResponse.jsonPath().getBoolean("is_limited"));
    }

    @Test
    public void cantCreateAnItemWithSameName(){
        //precondition - have an item created
        Item tea = new Item.ItemBuilder()
                .name("tea")
                .price(5.5)
                .price_for_quantity(1)
                .quantity_unit("package")
                .currency("BGN").build();
        Response createResponseFirst = itemApi.createItem(token, tea);
        Item anotherTea = new Item.ItemBuilder()
                .name("tea")
                .price(8.0)
                .price_for_quantity(2)
                .quantity_unit("double package")
                .currency("BGN").build();
        Response createResponseSecond = itemApi.createItem(token, anotherTea);
        Assertions.assertEquals(400, createResponseSecond.statusCode());
        Assertions.assertEquals("Съществува артикул с това име!", createResponseSecond.jsonPath().getString("error"));
        Response getResponse = itemApi.getSingleItem(token, createResponseFirst.jsonPath().getInt("id"));
        Assertions.assertEquals("tea", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals(5.5, getResponse.jsonPath().getDouble("price"));
        Assertions.assertEquals(1, getResponse.jsonPath().getInt("price_for_quantity"));
        Assertions.assertEquals("package", getResponse.jsonPath().getString("quantity_unit"));
        Assertions.assertEquals("BGN", getResponse.jsonPath().getString("currency"));
    }
    @Test
    public void cantCreateAnItemWithSameCatalogNumber(){
        //precondition - have an item created
        Item tea = new Item.ItemBuilder()
                .name("tea")
                .price(5.5)
                .price_for_quantity(1)
                .quantity_unit("package")
                .currency("BGN")
                .catalog_number(1).build();
        itemApi.createItem(token, tea);
        Item anotherTea = new Item.ItemBuilder()
                .name("double tea")
                .price(8.0)
                .price_for_quantity(1)
                .quantity_unit("double package")
                .currency("BGN")
                .catalog_number(1).build();
        Response createResponseSecond = itemApi.createItem(token, anotherTea);
        Assertions.assertEquals(400, createResponseSecond.statusCode());
        Assertions.assertEquals("Дублиран артикул с каталожен номер: 1!", createResponseSecond.jsonPath().getString("error"));
    }

    @Test
    public void canGetListWithUnits(){
    Response getUnitsRespose = itemApi.getItemUnits(token);
    Assertions.assertEquals(200, getUnitsRespose.statusCode());
    List<String> units = JsonPath.read(getUnitsRespose.asString(), "$");
    Assertions.assertTrue(units.contains("бр."));
    Assertions.assertTrue(units.contains("дни"));
    Assertions.assertTrue(units.contains("кг."));
    Assertions.assertTrue(units.contains("месец"));
}
@Test
    public void canCreateUnit(){
    Response createUnitResponse = itemApi.createUnit(token, "kilogram"+LocalDateTime.now().toString().substring(0,16));
    Assertions.assertEquals(201, createUnitResponse.statusCode());
    Response getUnitsRespose = itemApi.getItemUnits(token);
    List<String> units = JsonPath.read(getUnitsRespose.asString(), "$");
    Assertions.assertTrue(units.contains("kilogram"+LocalDateTime.now().toString().substring(0,16)));
}
}

