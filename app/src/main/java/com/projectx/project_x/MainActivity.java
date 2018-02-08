package com.projectx.project_x;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button ride_button = findViewById(R.id.ride_button);
             ride_button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent = new Intent(getApplicationContext(),PreAuth.class);
                     startActivity(intent);
                 }
             });
        final Button status_button = findViewById(R.id.status_button);
              status_button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent = new Intent(getApplicationContext(),StatusCheck.class);
                      startActivity(intent);
                  }
              });
        final Button book_button = findViewById(R.id.book_button);
              book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                      Intent intent = new Intent(getApplicationContext(),RideInProgress.class);
                      startActivity(intent);
            }
        });
    }
}
