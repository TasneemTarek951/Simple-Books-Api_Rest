package Request_Models;

public class CreateOrder {
    public int bookId;
    public String customerName;

    public CreateOrder(int bookId, String customerName) {
        this.bookId = bookId;
        this.customerName = customerName;
    }
}
