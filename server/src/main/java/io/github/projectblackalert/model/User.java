package io.github.projectblackalert.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by jakob on 18.06.18.
 */
@Document(collection = "User")
public class User {

    @Id
    String id;

    /**
     * Unique User Id von Google Authentication
     */
    @Indexed(unique=true)
    String uid;

    /**
     * Das ist das Token, das das Gerät eines Uses identifiziert
     * Das Token wird verwendet um Push Notifactions über Google Firebase an den User zu schicken
     */
    String messagingToken;

    /**
     * Feld ist true, wenn der User ein Händler ist
     */
    boolean dealer;

    String email;

    public User(String uid, String messagingToken, String email, boolean dealer) {
        this.uid = uid;
        this.messagingToken = messagingToken;
        this.email = email;
        this.dealer = dealer;
    }

    public void setDealer(boolean dealer) {
        this.dealer = dealer;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public String getUid() {
        return uid;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isDealer() {
        return dealer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
