package com.example.areebamansoor.enroutetogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Offer_OfferRide extends AppCompatActivity   {
     TextView tv_source, tv_dest;


     String source, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_offerride);
        Button btn1=(Button) findViewById(R.id.offerride);

        tv_source=(TextView) findViewById(R.id.tv_source);
        tv_dest=(TextView) findViewById(R.id.tv_dest);




        Intent intent = getIntent();
        destination = intent.getStringExtra("to");
        source = intent.getStringExtra("from");

        tv_dest.setText(destination);
        tv_source.setText(source);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Offer_OfferRide.this, Offer_PassengersList.class);
                startActivity(intent);
            }
        });

    }
}
