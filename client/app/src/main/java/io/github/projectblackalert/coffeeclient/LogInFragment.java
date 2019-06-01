package io.github.projectblackalert.coffeeclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.github.projectblackalert.coffeeclient.async.SignInAsyncTask;


public class LogInFragment extends Fragment {

    private View view;
    private Context context;
    private BroadcastReceiver singInSuccessfullReceiver;

    public LogInFragment() {
        singInSuccessfullReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setSpinnerVisible(false);
                setButtonEnabled(true);
                final Fragment buyFragment = ((MainActivity) getActivity()).buyFragment;
                ((MainActivity) getActivity()).setSelectedItemInMenu(R.id.nav_buy);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame
                                , buyFragment)
                        .commit();

            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log_in, container, false);
        return view;
    }

    private EditText email;
    private EditText password;

    private TextWatcher watchForFilledEmailAndPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues(){
        Button loginButton = view.findViewById(R.id.loginButton);

        String s1 = email.getText().toString();
        String s2 = password.getText().toString();

        if(s1.equals("")|| s2.equals("")){
            loginButton.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        email.addTextChangedListener(watchForFilledEmailAndPassword);
        password.addTextChangedListener(watchForFilledEmailAndPassword);

        boolean isUserLoggedIn = ((MainActivity) getActivity()).isUserLoggedIn();
        Button loginButton = view.findViewById(R.id.loginButton);

        String loginText =  isUserLoggedIn ? "Logout" : "Login";
        loginButton.setText(loginText);
        checkFieldsForEmptyValues();

        View.OnClickListener loginListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinnerAndSignIn(view);
            }
        };
        View.OnClickListener logoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).signOut(view);
            }
        };
        loginButton.setOnClickListener(isUserLoggedIn ? logoutListener : loginListener);

        LocalBroadcastManager.getInstance(context)
                .registerReceiver(singInSuccessfullReceiver,
                        new IntentFilter("signInSuccessfull"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Hides the keyboard after it is no longer neccessary.
    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Pressing the button disables the LogIn-Button, shows the spinner while the process is ongoing and starts the sign in process to the firebase service.
    public void showSpinnerAndSignIn(View view) {
        hideKeyboardFrom(context, view);
        setSpinnerVisible(true);
        setButtonEnabled(false);

        EditText emailTextField = view.findViewById(R.id.email);
        String email = emailTextField.getText().toString();

        EditText passwordTextField = view.findViewById(R.id.password);
        String password = passwordTextField.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Bitte Benutzername und/oder Passwort eingeben",
                    Toast.LENGTH_SHORT).show();
            setSpinnerVisible(false);
            setButtonEnabled(true);
            return;
        }
        new SignInAsyncTask(view, context, email, password).execute();
    }

    //Calls functions form the MainActivity to use in this fragment-
    public void register(View view) {
        ((MainActivity) getActivity()).register(view);
    }

    public void signOut(View view) {
        ((MainActivity) getActivity()).signOut(view);
    }

    private void setSpinnerVisible(boolean visible) {
        ProgressBar spinner = view.findViewById(R.id.loginSpinner);
        spinner.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    //LogIn-Button is only enables, when all fields are filled with sufficient input.
    private void setButtonEnabled(boolean enabled) {
        Button button = view.findViewById(R.id.loginButton);
        button.setEnabled(enabled);
    }


}
