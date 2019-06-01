package io.github.projectblackalert.dto;

/**
 * Created by jakob on 02.07.18.
 */
public class UserDetailsDto {

    String uid;

    String messagingToken;

    String email;

    boolean dealer;

    public UserDetailsDto(String uid, String messagingToken, String email, boolean dealer) {
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
