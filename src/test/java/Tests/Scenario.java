package Tests;

import Request_Models.Authentication;
import Request_Models.CreateOrder;
import Request_Models.UpdateOrder;
import Response_Models.*;
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

public class Scenario {

    public static List<String> OrderIds = new ArrayList<>();

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = DataUtils.get("Base_URL");
    }

    @Test(dataProvider = "scenarioInfo", dataProviderClass = DataProviders.class)
    public void end2endScenario(String type, int limit,int id,String email, String password,int bookId, String customerName){
        Status status = given().when().get(DataUtils.get("Status_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().response().as(Status.class);
        Assert.assertEquals(status.status,"OK");


        List<ListBooks> listBooks = Arrays.asList(given().queryParam("type",type).queryParam("limit",limit)
                .when().get(DataUtils.get("ListOfBooks_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().response().as(ListBooks[].class));

        for (var i = 0; i < listBooks.size(); i++) {
            Assert.assertEquals(listBooks.get(i).type,type);
        }
        Assert.assertNotNull(listBooks);
        Assert.assertTrue(listBooks.size()<=limit);


        BookById bookById = given().pathParam("bookId",id).when().get(DataUtils.get("BookByID_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().as(BookById.class);
        Assert.assertEquals(bookById.id,id);

        Authentication authentication = new Authentication(email,password);
        Token token = given().header("Content-Type",DataUtils.get("Content-Type")).body(authentication)
                .when().post(DataUtils.get("Authentication_endpoint"))
                .then().assertThat().log().all().statusCode(201).extract().response().as(Token.class);
        Assert.assertNotNull(token);
        DataUtils.set("Token",token.accessToken);

        CreateOrder createOrder = new CreateOrder(bookId,customerName);
        CreateOrderResponse createOrderResponse = given().header("Content-Type",DataUtils.get("Content-Type"))
                .header("Authorization", "Bearer " + DataUtils.get("Token")).body(createOrder)
                .when().post(DataUtils.get("SubmitOrder_endpoint")).then().assertThat().log().all().statusCode(201).extract()
                .response().as(CreateOrderResponse.class);
        if(createOrderResponse != null){
            Assert.assertEquals(createOrderResponse.created,true);
            OrderIds.add(createOrderResponse.orderId);
        }

        List<Order> orders = Arrays.asList(given().header("Authorization", "Bearer " + DataUtils.get("Token")).when()
                .get(DataUtils.get("SubmitOrder_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order[].class));

        Order order = given().pathParam("orderId",Scenario.OrderIds.get(0)).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .log().all().when().get(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order.class);
        Assert.assertEquals(order.id,Scenario.OrderIds.get(0));


        UpdateOrder updateOrder = new UpdateOrder(customerName);
        given().pathParam("orderId",Scenario.OrderIds.get(0)).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .header("Content-Type",DataUtils.get("Content-Type")).body(updateOrder).when()
                .patch(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);

        given().pathParam("orderId",Scenario.OrderIds.get(0)).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .when().delete(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);
    }
}
