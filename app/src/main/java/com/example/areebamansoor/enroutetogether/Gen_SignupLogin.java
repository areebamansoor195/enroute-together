package com.example.areebamansoor.enroutetogether;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Gen_SignupLogin extends AppCompatActivity {

    Context context = Gen_SignupLogin.this;
    Button btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_signuplogin);
        btn1=(Button) findViewById(R.id.login_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Gen_SignupLogin.this,Gen_SignupActivity.class);
                startActivity(intent);
            }
        });
        btn2 =(Button) findViewById(R.id.signup_button);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Gen_SignupLogin.this,Gen_LoginForm.class);
                startActivity(intent);
            }
        });


    }
}
