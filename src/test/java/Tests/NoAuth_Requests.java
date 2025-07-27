package Tests;

import Response_Models.BookById;
import Response_Models.ListBooks;
import Response_Models.Status;
import TestData.DataProviders;
import Utiles.DataUtils;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;


public class NoAuth_Requests {
    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = DataUtils.get("Base_URL");
    }
    @Test(description = "Check server health status")
    public void getStatus() {
        Status status = given().when().get(DataUtils.get("Status_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().response().as(Status.class);
        Assert.assertEquals(status.status,"OK");
    }

    @Test(dataProvider = "ListBooksParameters", dataProviderClass = DataProviders.class)
    public void getListOfBooks(String type, int limit) {
        List<ListBooks> listBooks = Arrays.asList(given().queryParam("type",type).queryParam("limit",limit)
                .when().get(DataUtils.get("ListOfBooks_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().response().as(ListBooks[].class));

        for (var i = 0; i < listBooks.size(); i++) {
            Assert.assertEquals(listBooks.get(i).type,type);
        }
        Assert.assertNotNull(listBooks);
        Assert.assertTrue(listBooks.size()<=limit);
    }

    @Test(dataProvider = "BookID", dataProviderClass = DataProviders.class)
    public void getBookById(int id) {
        BookById bookById = given().pathParam("bookId",id).when().get(DataUtils.get("BookByID_endpoint"))
                .then().assertThat().log().all().statusCode(200).extract().as(BookById.class);
        Assert.assertEquals(bookById.id,id);
    }

}
