package io.github.projectblackalert.coffeeclient.model;

public class UserDetails {

    String uid;

    String messagingToken;

    String email;

    boolean dealer;

    public UserDetails() {
        // empty constructor for deserialization
    }

    public UserDetails(String uid, String messagingToken, String email, boolean dealer) {
        this.uid = uid;
        this.messagingToken = messagingToken;
        this.email = email;
        this.dealer = dealer;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDealer() {
        return dealer;
    }

    public void setDealer(boolean dealer) {
        this.dealer = dealer;
    }

}
