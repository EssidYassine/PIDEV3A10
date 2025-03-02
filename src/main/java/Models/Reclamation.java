package Models;


public class Reclamation {
    private int id;
    private int userId;
    private String userMessage;
    private String chatResponse;





    public Reclamation(int userId, String userMessage, String chatResponse) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.chatResponse = chatResponse;
    }
    public Reclamation(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getChatResponse() {
        return chatResponse;
    }

    public void setChatResponse(String chatResponse) {
        this.chatResponse = chatResponse;
    }
}
