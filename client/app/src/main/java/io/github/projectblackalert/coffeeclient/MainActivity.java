package io.github.projectblackalert.coffeeclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import io.github.projectblackalert.coffeeclient.async.SignInAsyncTask;
import io.github.projectblackalert.coffeeclient.dealerArea.DealerActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BuyFragment buyFragment = new BuyFragment();
    LogInFragment logInFragment = new LogInFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        this.context = this;

        startWelcomeActivityOnFirstStartup();
        setupNavigation();
        SignInAsyncTask.getGoogleAuthTokenAndLogin(this);
        forwardUserToStartActivity();
    }

    private void forwardUserToStartActivity() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        setSelectedItemInMenu(R.id.nav_buy);
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame
                        , buyFragment)
                .commit();
    }

    private void setupNavigation() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                    @Override
                    public void onDrawerStateChanged(int newState) {
                        super.onDrawerStateChanged(newState);
                        updateMenu();
                    }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void updateMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem logOutItem = navigationView.getMenu().findItem(R.id.nav_logOut);
        MenuItem sellerItem = navigationView.getMenu().findItem(R.id.nav_seller);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView nav_bar_name = findViewById(R.id.nav_header_main_userName);
        if (isUserLoggedIn()) {
            logOutItem.setTitle("Logout");
            nav_bar_name.setText("Du bist angemeldet als:" + System.getProperty ("line.separator") + getUserName());
            sellerItem.setVisible(false);
            if (isUserDealer()) {
                sellerItem.setVisible(true);
            }
        } else {
            logOutItem.setTitle("Login");
            nav_bar_name.setText("Hallo, Kaffeefreund! ☕ \uD83D\uDE00");
            sellerItem.setVisible(false);
        }


    }

    private void startWelcomeActivityOnFirstStartup() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        boolean firstStartup = sharedPreferences.getBoolean("firstStartup", true);
        if (firstStartup) {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            startActivityForResult(welcomeActivity, 1);
        }
        return;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //TODO: Shuffle through fragment stack
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        View view = new View(this);

        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_buy) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                        , buyFragment)
                    .commit();
        }

        if (id == R.id.nav_profile) {
            System.out.println(isUserLoggedIn());
            if (isUserLoggedIn()) {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , profileFragment)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , logInFragment)
                        .commit();
            }

        }  else if (id == R.id.nav_logOut) {
            if (isUserLoggedIn()) {
                signOut(view);
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , logInFragment)
                        .commit();
            }

        }  else if (id == R.id.nav_seller) {
            if (!isUserLoggedIn()) {
                Log.w("Switch to Seller", "Not logged in");
                Toast.makeText(MainActivity.this, "Du bist nicht eingeloggt.",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isUserDealer()) {
                Log.w("Switch to Seller", "User is not a dealer");
                Toast.makeText(MainActivity.this, "Du bist kein Händler. Nur Händler können diesen Bereich sehen.",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                    startMyActivity(DealerActivity.class);
                    return true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startMyActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /** REGISTRIERUNG **/

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build());

    private static final int RC_SIGN_IN = 123;

    public void register(View view) {
        if (!isUserLoggedIn()) {
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            Toast.makeText(MainActivity.this, "Du bist bereits eingeloggt. Logge dich aus um ein neues Konto zu erstellen.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Wird aufgerufen, wenn man von der Google Register Activity zurück kommt.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                putUidAndDisplayNameInSharedPrefs();
                SignInAsyncTask.getGoogleAuthTokenAndLogin(context);
                Toast.makeText(MainActivity.this, "Du hast Dich erfolgreich registriert und bist nun eingeloggt.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void signOut(View view) {
        Log.d("UserState", "Signing out");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            auth.signOut();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.FIREBASE_AUTH_TOKEN);
            editor.remove(Constants.DISPLAY_NAME);
            editor.remove(Constants.UID);
            editor.commit();

            Toast.makeText(MainActivity.this, "Du bist nun ausgeloggt.",
                    Toast.LENGTH_SHORT).show();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame
                        , logInFragment)
                .commit();
    }

    private void putUidAndDisplayNameInSharedPrefs() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("MainActivity", "User is null");
            return;
        }
        String displayName = user.getDisplayName();
        String uid = user.getUid();
        Log.d("UserState", String.format("Putting Uid '%s' and displayName '%s' in SharedPrefs", uid, displayName ));
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.DISPLAY_NAME, displayName);
        editor.putString(Constants.UID, uid);
        editor.commit();
    }

    private String getAuthToken() {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        return sharedPrefs.getString(Constants.FIREBASE_AUTH_TOKEN, "");
    }

    public boolean isUserLoggedIn() {
        String uid = getUid();
        String authToken = getAuthToken();

        Log.d("UserState", String.format("Uid: %s, AuthToken: %s", uid, authToken));

        if (!uid.isEmpty() && !authToken.isEmpty()) {
            Log.d("UserState", String.format("User is logged in, Uid: %s, AuthToken: %s", uid, authToken));
            return true;
        }
        Log.d("UserState", String.format("User is not logged in"));
        return false;
    }

    public boolean isUserDealer() {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        boolean isDealer = sharedPrefs.getBoolean("isDealer", false);
        Log.d("UserState", String.format("Receiving userState, isDealer " + isDealer));
        return isDealer;
    }

    public String getUid() {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        return sharedPrefs.getString(Constants.UID, "");
    }

    public String getUserName() {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
        return sharedPrefs.getString(Constants.DISPLAY_NAME, "");
    }

    public void setSelectedItemInMenu(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_buy).setChecked(true);
    }
}
