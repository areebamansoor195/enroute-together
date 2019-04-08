package com.example.areebamansoor.enroutetogether;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityGenSignupformBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.gmail.Gmail;
import com.example.areebamansoor.enroutetogether.gmail.SendEmailTask;
import com.example.areebamansoor.enroutetogether.model.Register;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Gen_SignupForm extends AppCompatActivity {

    private ActivityGenSignupformBinding binding;
    private String gender = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gen_signupform);

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        gender = "Male";
                        break;
                    case R.id.rb_female:
                        gender = "Female";
                        break;
                }
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    if (binding.etPwd.getText().toString().equalsIgnoreCase(binding.etCpwd.getText().toString())) {
                        createUser();
                        return;
                    }
                    Toast.makeText(Gen_SignupForm.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(Gen_SignupForm.this, "Please fill required fields", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void createUser() {

        try {

            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String organizationId = binding.etId.getText().toString().trim();
            String password = AESCrypt.encrypt(binding.etPwd.getText().toString().trim());
            String OTP = generateOTP();


            String user_id = Firebase.getInstance().mDatabase.child("Users").push().getKey();

            Register registerUser = new Register(user_id, name, organizationId, email, password, gender.trim(), OTP, false);

            Firebase.getInstance().mDatabase.child(user_id).setValue(registerUser);

            Firebase.getInstance().mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Toast.makeText(Gen_SignupForm.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                    Register registeredUser = dataSnapshot.getValue(Register.class);

                    //Sending OTP to user registered email
                    String fromEmail = Gmail.ACCOUNT_EMAIL;
                    String fromPassword = Gmail.ACCOUNT_PASSWORD;
                    List<String> toEmailList = new ArrayList();
                    toEmailList.add(registeredUser.getEmail());

                    String emailSubject = "Enroute Together";
                    String emailBody = "Your Verification Code is " + registeredUser.getOtp();


                    new SendEmailTask().execute(fromEmail,
                            fromPassword, toEmailList, emailSubject, emailBody);


                    Intent intent = new Intent(Gen_SignupForm.this, Gen_LoginForm.class);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Gen_SignupForm.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String generateOTP() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return randomPIN + "";
    }

    private boolean validateFields() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String organizationId = binding.etId.getText().toString().trim();
        String password = binding.etPwd.getText().toString().trim();
        String confirmPassword = binding.etCpwd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(organizationId) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(gender)) {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
