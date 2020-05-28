package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread=new Thread()
        {
            @Override
            public void run() {
                try{
                    sleep(1000);
                    Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }; //THREAD CLASS ENDS
        thread.start();
    }
}
