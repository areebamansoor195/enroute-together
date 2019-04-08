package com.example.areebamansoor.enroutetogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Offer_PassengerDetails extends AppCompatActivity {
 Button btn_OfferrRide;
    ImageView iv_Message;
    ImageView iv_Call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_passengerdetails);
        btn_OfferrRide =(Button) findViewById(R.id.offerride);




    }
}
