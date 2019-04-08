package com.example.areebamansoor.enroutetogether;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Gen_SignupActivity extends AppCompatActivity {
    Context context = Gen_SignupActivity.this;
    Button btn1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_signup);
        Button btn1=(Button) findViewById(R.id.btnnext);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Gen_SignupActivity.this,Gen_SignupForm.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
