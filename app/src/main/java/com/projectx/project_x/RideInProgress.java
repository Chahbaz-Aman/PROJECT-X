package com.projectx.project_x;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class RideInProgress extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);

        imageView = findViewById(R.id.animation);
        imageView.setBackgroundResource(R.drawable.anim);

        final AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        imageView.post(new Runnable(){
            public void run(){
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                frameAnimation.start();
            }
        });
    }

}
