package io.github.projectblackalert;

import com.google.gson.Gson;
import io.github.projectblackalert.enums.State;
import io.github.projectblackalert.model.Order;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class RestClient {

    private static final String GOOGLE_API_SEND = "https://fcm.googleapis.com/fcm/send";

    private static final String GOOGLE_API_KEY = "AAAAep_IqGQ:APA91bFyq-cktaMcn-Lt1cxpuY_BeJo6nBwN93Xcx5CSx_wwmESW6aKiUB1qya3zHz92I5PaN-2jB4eJA94etXqL2pF1JYcGig_zfkGrutw4N89_bwaTIYwbunYcYbaX5mczpnSwQw9W";

    public static void sendPushNotification(String target) {
        RestTemplate restTemplate = new RestTemplate();

        URI url = null;
        try {
            url = new URI(GOOGLE_API_SEND);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject data = new JSONObject()
                .put("type", "newOrderReceived");

        JSONObject notification = new JSONObject()
                .put("body", "Du hast eine neue Bestellung.")
                .put("title", ":-) Neue Bestellung :-)")
                .put("sound", "default")
                .put("click_action", "OPEN_DEALER_ACTIVITY");

        String payload =  new JSONObject()
                .put("to", target)
                .put("notification", notification)
                .put("data", data)
                .toString();

        System.out.println("payload" +  payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "key=" + GOOGLE_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(payload,headers);
        String answer = restTemplate.postForObject(url, entity, String.class);
        System.out.println(answer);
    }

    public static void sendOrderConfirmPushNotification(String target, Order order, State newState) {
        RestTemplate restTemplate = new RestTemplate();

        URI url = null;
        try {
            url = new URI(GOOGLE_API_SEND);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String orderJsonString = gson.toJson(order);

        JSONObject data = new JSONObject()
                .put("order", orderJsonString)
                .put("type", "orderConfirmed");

        String bodyText = State.CONFIRMED.equals(newState) ? "Deine Bestellung ist beim Kiosk eingegangen. " +
                "Dein Kaffe ist gleich fertig." : "Deine Bestellung wurde als Verkauft markiert";

        JSONObject notification = new JSONObject()
                .put("body", bodyText)
                .put("title", ":-) Update zu deiner Bestellung :-)")
                .put("sound", "default");

        String payload =  new JSONObject()
                .put("to", target)
                .put("notification", notification)
                .put("data", data)
                .toString();

        System.out.println("Payload(sendOrderConfirmPushNotification)" +  payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "key=" + GOOGLE_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(payload,headers);
        String answer = restTemplate.postForObject(url, entity, String.class);
        System.out.println(answer);
    }
}
