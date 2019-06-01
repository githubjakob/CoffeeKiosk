package io.github.projectblackalert.coffeeclient.cloudMessaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.github.projectblackalert.coffeeclient.popup.PushPopupActivity;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MessagingService", "Message received ");

        // wenn eine neue push notification eingegangen ist, zeigen wir immer eine popUp
        Intent popUp = new Intent(MessagingService.this, PushPopupActivity.class);
        popUp.putExtra("message", remoteMessage.getNotification().getBody());
        startActivity(popUp);

        // nur für den Händler aktualisieren wir die Liste der Bestellungen
        if (remoteMessage.getData().size()>0 && "newOrderReceived".equals(remoteMessage.getData().get("type"))) {
            Log.d("messaging service", "new order received");
            Intent intent = new Intent("newOrderPushMessageReceived");
            LocalBroadcastManager.getInstance(MessagingService.this).sendBroadcast(intent);
        }


    }
}