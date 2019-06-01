package io.github.projectblackalert.coffeeclient.model;

public class LoginDetails {

    String uid;

    String messagingToken;

    String email;

    public LoginDetails(String uid, String messagingToken, String email) {
        this.uid = uid;
        this.messagingToken = messagingToken;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }
}
