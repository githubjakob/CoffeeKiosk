package io.github.projectblackalert.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by jakob on 18.06.18.
 */
@Document(collection = "User")
public class User {

    /**
     * Das ist nicht die Unique User Id von Google,
     * sondern eine zufällige Id, die von MongoDb vergeben und verwendet wird
     */
    @Id
    String id;

    /**
     * Das ist die Unique User Id von Google Authentication
     */
    String uid;

    public String getMessagingToken() {
        return messagingToken;
    }

    /**
     * Das ist das Token, das das Gerät eines Uses identifiziert
     * Das Token wird verwendet um Push Notifactions über Google Firebase an den User zu schicken
     */
    String messagingToken;

    /**
     * Feld ist true, wenn der User ein Händler ist
     */
    boolean dealer;

    /**
     * Die aktuelle Position in der Warteschlange
     * -10: User existiert nicht
     * -5:  User existiert aber nicht in der Warteschlange
     */
    int position;

    public User(String uid, String messagingToken) {
        this.uid = uid;
        this.messagingToken = messagingToken;
        this.position = -5;
    }

    public void setDealer(boolean dealer) {
        this.dealer = dealer;
    }

    public String getUid() {
        return uid;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
