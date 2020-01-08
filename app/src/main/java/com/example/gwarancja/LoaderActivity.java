package com.example.gwarancja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class LoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    go();
                    Log.d("Loader", "działa");
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }).start();


    }
    private void go() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
