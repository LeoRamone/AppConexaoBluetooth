package com.example.leonardo_soares.apppigormito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/**
 * Created by Leonardo Soares on 24/09/18.
 * leonardo_soares_santos@outlook.com
 */
public class SplashActivity extends AppCompatActivity {
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        iv=(ImageView)findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.alpha);

        iv.startAnimation(myanim);
        final Intent i = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(4000);
                }catch (InterruptedException e){

                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }


            }





        };
        timer.start();
    }
}
