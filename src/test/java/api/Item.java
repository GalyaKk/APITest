package api;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.lang.reflect.Array;

@Builder
public class Item {
    private String name;
    private Double price;
    private Integer price_for_quantity;
    private String currency;
    private String quantity_unit;
    private String name_en;
    private boolean is_limited;
    private Integer catalog_number;
    private Integer outside_id;
    private Array tags;
    public Integer id;


//    public Item(String name, Double price, Integer priceForQuatity, String currency, String quantityUnit) {
//        this.name = name;
//        this.price = price;
//        this.priceForQuatity = priceForQuatity;
//        this.currency = currency;
//        this.quantity_unit = quantityUnit;
//    }
}
