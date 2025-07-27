package Response_Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookById {
    public int id;
    public Boolean available;
    public String name;
    @JsonProperty("current-stock")
    public int currentStock;
    public int price;
    public String author;
    public String type;
}
