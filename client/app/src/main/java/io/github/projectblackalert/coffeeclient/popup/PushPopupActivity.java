package io.github.projectblackalert.coffeeclient.popup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import io.github.projectblackalert.coffeeclient.R;

public class PushPopupActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_popup);

        String message = (String) getIntent().getExtras().get("message");
        TextView popupText = (TextView) findViewById(R.id.popup_text);
        popupText.setText(message);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*.3));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        new Timer().execute();

    }

    private class Timer extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            PushPopupActivity.this.finish();
        }
    }



}
