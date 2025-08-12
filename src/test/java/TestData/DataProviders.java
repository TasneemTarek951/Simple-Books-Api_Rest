package TestData;

import Tests.AuthRequire_Requests;
import Tests.Scenario;
import org.testng.annotations.DataProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataProviders {
    @DataProvider(name = "ListBooksParameters")
    public Object[][] getData() {
        return new Object[][]{{"fiction", 4}, {"non-fiction", 6}};
    }
    @DataProvider(name = "BookID")
    public Object[][] getID() {
        return new Object[][]{{4},{6},{2}};
    }
    @DataProvider(name = "Auth")
    public Object[][] getAuth() {
        return new Object[][]{{"Postman", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"@example.com"}};
    }

    @DataProvider(name = "CreateOrder")
    public Object[][] getOrderInfo() {
        return new Object[][]{{3,"john"},{5,"Tasneem"}};
    }

    @DataProvider(name = "OrderID")
    public Object[][] OrderID() {
        Object[][] data = new Object[AuthRequire_Requests.OrderIds.size()][1];
        for (int i = 0; i < AuthRequire_Requests.OrderIds.size(); i++) {
            data[i][0] = AuthRequire_Requests.OrderIds.get(i);
        }
        return data;
    }

    @DataProvider(name = "UpdateOrder")
    public Object[][] UpdateOrder() {
        return new Object[][]{{AuthRequire_Requests.OrderIds.get(0),"Ahmed"},{AuthRequire_Requests.OrderIds.get(1),"ghada"}};
    }

    @DataProvider(name = "scenarioInfo")
    public Object[][] scenarioInfo() {
        return new Object[][]{{"fiction", 4,4,"Postman", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"@example.com",3,"john"}};
    }
}
