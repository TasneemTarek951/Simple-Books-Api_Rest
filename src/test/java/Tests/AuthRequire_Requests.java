package Tests;

import Request_Models.Authentication;
import Request_Models.CreateOrder;
import Request_Models.UpdateOrder;
import Response_Models.CreateOrderResponse;
import Response_Models.Order;
import Response_Models.Token;
import TestData.DataProviders;
import Utiles.DataUtils;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

@Epic("Simple Books API")
@Feature("Authenticated Endpoints")
@Listeners(Utiles.AllureListener.class)
public class AuthRequire_Requests {

    public static List<String> OrderIds = new ArrayList<>();

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = DataUtils.get("Base_URL");
    }


    @Test(dataProvider = "Auth", dataProviderClass = DataProviders.class,priority = 0)
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Generate an access token using valid email and password.")
    public void PostAuth(String email, String password){
        Authentication authentication = new Authentication(email,password);
        Token token = given().header("Content-Type",DataUtils.get("Content-Type")).body(authentication)
                        .when().post(DataUtils.get("Authentication_endpoint"))
                .then().assertThat().log().all().statusCode(201).extract().response().as(Token.class);
        Assert.assertNotNull(token);
        DataUtils.set("Token",token.accessToken);

    }

    @Test(dataProvider = "CreateOrder", dataProviderClass = DataProviders.class,priority = 1)
    @Story("Create Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Submit a new order using book ID and customer name.")
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
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve all orders placed by the authenticated user.")
    public void getAllOrders(){
        List<Order> orders = Arrays.asList(given().header("Authorization", "Bearer " + DataUtils.get("Token")).when()
                .get(DataUtils.get("SubmitOrder_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order[].class));

    }

    @Test(dataProvider = "OrderID", dataProviderClass = DataProviders.class,priority = 3)
    @Story("Get Order by ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch the details of a specific order using its ID.")
    public void getOrderById(String orderId){
        Order order = given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .log().all().when().get(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(200).extract()
                .response().as(Order.class);
        Assert.assertEquals(order.id,orderId);
    }

    @Test(dataProvider = "UpdateOrder", dataProviderClass = DataProviders.class,priority = 4)
    @Story("Update Order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update the customer name of an existing order.")
    public void UpdateOrder(String orderId,String customerName){
        UpdateOrder updateOrder = new UpdateOrder(customerName);
        given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .header("Content-Type",DataUtils.get("Content-Type")).body(updateOrder).when()
                .patch(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);
    }

    @Test(dataProvider = "OrderID", dataProviderClass = DataProviders.class,priority = 5)
    @Story("Delete Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete an order using its ID.")
    public void DeleteOrder(String orderId){
        given().pathParam("orderId",orderId).header("Authorization", "Bearer " + DataUtils.get("Token"))
                .when().delete(DataUtils.get("OrderById_endpoint")).then().assertThat().log().all().statusCode(204);
    }


}
