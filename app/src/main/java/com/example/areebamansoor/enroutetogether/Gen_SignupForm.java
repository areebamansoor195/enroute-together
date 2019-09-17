package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityGenSignupformBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.gmail.Gmail;
import com.example.areebamansoor.enroutetogether.gmail.SendEmailTask;
import com.example.areebamansoor.enroutetogether.model.Register;
import com.example.areebamansoor.enroutetogether.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.areebamansoor.enroutetogether.utils.Constants.USERS;

public class Gen_SignupForm extends AppCompatActivity {

    private long user_id = 0;
    private ActivityGenSignupformBinding binding;
    private String gender = "";
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gen_signupform);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

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

                if (!validateFields()) {
                    Toast.makeText(Gen_SignupForm.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!binding.etPwd.getText().toString().equalsIgnoreCase(binding.etCpwd.getText().toString())) {
                    Toast.makeText(Gen_SignupForm.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmail(binding.etEmail.getText().toString())) {
                    Toast.makeText(Gen_SignupForm.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.etPwd.getText().length() < 4) {
                    Toast.makeText(Gen_SignupForm.this, "Password should be 4 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                createUser();
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void createUser() {

        try {

            final String name = binding.etName.getText().toString().trim();
            final String email = binding.etEmail.getText().toString().trim();
            final String organizationId = binding.etId.getText().toString().trim();
            final String password = AESCrypt.encrypt(binding.etPwd.getText().toString().trim());
            final String OTP = generateOTP();

            progressDialog.show();


            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        User tempUser = user.getValue(User.class);

                        if (tempUser.getEmail().equalsIgnoreCase(email)) {
                            progressDialog.dismiss();
                            Toast.makeText(Gen_SignupForm.this, "Email already registered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                    user_id = dataSnapshot.getChildrenCount();
                    user_id++;

                    Firebase.getInstance().mDatabase.child(USERS).removeEventListener(valueEventListener);

                    Register registerUser = new Register(user_id + "", name, organizationId, email, password, gender.trim(), OTP, false);

                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Firebase.getInstance().mDatabase.child(USERS).child(user_id + "").removeEventListener(valueEventListener);

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

                            progressDialog.dismiss();
                            Toast.makeText(Gen_SignupForm.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Gen_SignupForm.this, Gen_LoginForm.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Firebase.getInstance().mDatabase.child(USERS).child(user_id + "").removeEventListener(valueEventListener);
                        }
                    };

                    Firebase.getInstance().mDatabase.child(USERS).child(user_id + "").setValue(registerUser);
                    Firebase.getInstance().mDatabase.child(USERS).child(user_id + "").addValueEventListener(valueEventListener);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Firebase.getInstance().mDatabase.child(USERS).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };

            Firebase.getInstance().mDatabase.child(USERS).addListenerForSingleValueEvent(valueEventListener);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
