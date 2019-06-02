package io.github.projectblackalert.coffeeclient.async;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;

import io.github.projectblackalert.coffeeclient.Constants;
import io.github.projectblackalert.coffeeclient.api.ApiClient;
import io.github.projectblackalert.coffeeclient.model.LoginDetails;
import io.github.projectblackalert.coffeeclient.model.UserDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInAsyncTask extends AsyncTask<Void, Void, Void>
{
    private final View view;
    private final String email;
    private final String password;
    private final Context context;

    public SignInAsyncTask(View view, Context context, String email, String password) {
        this.view = view;
        this.email = email;
        this.context = context;
        this.password = password;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d("Logedin user: ", user.getUid());
                            Log.d("Logedin email: ", user.getEmail());
                            Log.d("Logedin userName: ", user.getDisplayName());

                            putUidAndDisplayNameInSharedPrefs();
                            getGoogleAuthTokenAndLogin(context);
                            Toast.makeText(context, "Anmelden erfolgreich.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent("signInSuccessfull");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Anmelden fehlgeschlagen.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent("signInFailed");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
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

    private static void loginUserAtServer(final Context context) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Call<UserDetails> userDetailsCall = ApiClient.getAuthenticated().loginUser(new LoginDetails(user.getUid(), FirebaseInstanceId.getInstance().getToken(),
                    user.getEmail()));
            userDetailsCall.enqueue(new Callback<UserDetails>() {
                @Override
                public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                    UserDetails userDetails = response.body();
                    Log.d("LoginUser successfull", "user is dealer " + userDetails.isDealer());
                    SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
                    SharedPreferences.Editor editor = sharedPrefs.edit();

                    editor.putBoolean("isDealer", userDetails.isDealer());
                    editor.commit();

                    Intent intent = new Intent("successfullyLoggedIn");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(Call<UserDetails> call, Throwable t) {
                    Log.e("SignInAsyncTask", "Call failed");
                }
            });

        }
    }

    public static void getGoogleAuthTokenAndLogin(final Context context) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Log.e("UserState", "cannot obtain google auth token because user is null");
            return;
        }
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String authToken = task.getResult().getToken();
                            Log.d("UserState", String.format("Putting authToken '%s' in SharedPrefs", authToken));

                            // put in shared preferences
                            SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, 0);
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString(Constants.FIREBASE_AUTH_TOKEN, authToken);
                            editor.commit();

                            loginUserAtServer(context);

                        } else {
                            Log.e("UserState", "Failed getting the google auth token");
                        }
                    }
                });
    }
}
