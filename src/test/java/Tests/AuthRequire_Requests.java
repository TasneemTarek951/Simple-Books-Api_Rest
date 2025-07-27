package Tests;

import Request_Models.Authentication;
import Request_Models.CreateOrder;
import Request_Models.UpdateOrder;
import Response_Models.CreateOrderResponse;
import Response_Models.Order;
import Response_Models.Token;
import TestData.DataProviders;
import Utiles.DataUtils;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class AuthRequire_Requests {

    public static List<String> OrderIds = new ArrayList<>();

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = DataUtils.get("Base_URL");
    }


    @Test(dataProvider = "Auth", dataProviderClass = DataProviders.class,priority = 0)
    public void PostAuth(String email, String password){
        Authentication authentication = new Authentication(email,password);
        Token token = given().header("Content-Type",DataUtils.get("Content-Type")).body(authentication)
                        .when().post(DataUtils.get("Authentication_endpoint"))
                .then().assertThat().log().all().statusCode(201).extract().response().as(Token.class);
        Assert.assertNotNull(token);
        DataUtils.set("Token",token.accessToken);

    }

    @Test(dataProvider = "CreateOrder", dataProviderClass = DataProviders.class,priority = 1)
    public void SubmitOrder(int bookId, String customerName){
        CreateOrder createOrder = new CreateOrder(bookId,customerName);
        CreateOrderResponse createOrderResponse = given().header("Content-Type",DataUtils.get("Content-Type"))
                .header("Authorization", "Bearer " + DataUtils.get("Token")).body(createOrder)
                .when().post(DataUtils.get("SubmitOrder_endpoint")).then().assertThat().log().all().statusCode(201).extract()
                .response().as(CreateOrderResponse.class);
        if(createOrderResponse != null){
            Assert.assertEquals(createOrderResponse.created,true);
            OrderIds.add(createOrderResponse.orderId);
        }
    }

    @Test(priority = 2)
    public void getAllOrders(){
        List<Order> orders = Arrays.asList(given().header("Authorization", "Bearer " + DataUtils.get("Token")).when()
                .get(DataUtils.get("SubmitOrder_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order[].class));

    }

    @Test(dataProvider = "OrderID", dataProviderClass = DataProviders.class,priority = 3)
    public void getOrderById(String orderId){
        Order order = given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .log().all().when().get(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order.class);
        Assert.assertEquals(order.id,orderId);
    }

    @Test(dataProvider = "UpdateOrder", dataProviderClass = DataProviders.class,priority = 4)
    public void UpdateOrder(String orderId,String customerName){
        UpdateOrder updateOrder = new UpdateOrder(customerName);
        given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .header("Content-Type",DataUtils.get("Content-Type")).body(updateOrder).when()
                .patch(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);
    }

    @Test(dataProvider = "OrderID", dataProviderClass = DataProviders.class,priority = 5)
    public void DeleteOrder(String orderId){
        given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .when().delete(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);
    }


}
