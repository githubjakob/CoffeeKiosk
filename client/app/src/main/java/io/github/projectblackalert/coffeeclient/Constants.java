package io.github.projectblackalert.coffeeclient;

public class Constants {

    /**
     * IP von Server hier eintragen
     */
    public static final String SERVER = "http://192.168.1.8:8000";

    public static final String REST = "/rest";

    public static final String AUTH = "/auth";

    public static final String SHARED_PREFERENCES = "SharedPreferencs";

    public static final String FIREBASE_AUTH_TOKEN = "firebaseAuthToken";

    public static final String UID = "uid";

    public static final String DISPLAY_NAME = "userName";

    public static final int USER_NOTFOUND = -10;

    public static final int USER_NO_ORDER = -5;
    
    public static final String SHOW_ALL_ORDER_ACTION = "showAllOrderAction";
    
    public static final String SHOW_ONLY_CONFIMED_AND_SOLD_ACTION = "showOnlyConfirmedAndSoldAction";

    public static final String UPDATE_DEALER_VIEW = "updateDealerView";

    public static final String ORDER_STATE_CHANGED = "orderStateChanged";

    public enum State {
        ORDERED("Bestellt"), CONFIRMED("Bestätigt"), SOLD("Verkauft");

        String text;

        State(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
