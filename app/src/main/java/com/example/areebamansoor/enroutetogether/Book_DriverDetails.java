package com.example.areebamansoor.enroutetogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
public class Book_DriverDetails extends AppCompatActivity {

    Button btn_SendRequest;
    ImageView iv_Message;
    ImageView iv_Call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_driverdetails);
         btn_SendRequest =(Button) findViewById(R.id.sendrequest);
        iv_Message =(ImageView) findViewById(R.id.message);
        iv_Call =(ImageView) findViewById(R.id.call);

        btn_SendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Book_DriverDetails.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        iv_Message.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Book_DriverDetails.this, MessageActivity.class);
                startActivity(intent);
            }

            });
    }
}
