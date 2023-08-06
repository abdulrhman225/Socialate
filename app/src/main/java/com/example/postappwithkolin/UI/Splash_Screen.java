package com.example.postappwithkolin.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.postappwithkolin.R;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                    Intent intent = new Intent(Splash_Screen.this , MainActivity.class);
                    startActivity(intent);
                }catch (Exception e){

                }
            }
        };
        thread.start();
    }
}