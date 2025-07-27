package Request_Models;

public class Authentication {
    public String clientName;
    public String clientEmail;
    public  Authentication(String clientName, String clientEmail) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
    }
}
