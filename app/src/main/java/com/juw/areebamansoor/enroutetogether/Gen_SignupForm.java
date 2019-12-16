package com.juw.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.isapanah.awesomespinner.AwesomeSpinner;
import com.juw.areebamansoor.enroutetogether.databinding.ActivityGenSignupformBinding;
import com.juw.areebamansoor.enroutetogether.firebase.Firebase;
import com.juw.areebamansoor.enroutetogether.gmail.Gmail;
import com.juw.areebamansoor.enroutetogether.gmail.SendEmailTask;
import com.juw.areebamansoor.enroutetogether.model.Register;
import com.juw.areebamansoor.enroutetogether.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.juw.areebamansoor.enroutetogether.utils.Constants.EMPLOYEE;
import static com.juw.areebamansoor.enroutetogether.utils.Constants.STUDENT;
import static com.juw.areebamansoor.enroutetogether.utils.Constants.USERS;

public class Gen_SignupForm extends AppCompatActivity {

    private long user_id = 0;
    private ActivityGenSignupformBinding binding;
    private String gender = "";
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gen_signupform);

        binding.userType.setHintTextColor(getResources().getColor(R.color.colorWhite));
        binding.userType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.user_types))));

        binding.userType.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {

            }
        });
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

                if (binding.userType.getSelectedItem() == null) {
                    Toast.makeText(Gen_SignupForm.this, "Please select user type", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyUser();

            }
        });
    }

    private void verifyUser() {

        try {

            final String user_type = binding.userType.getSelectedItem() + "";

            progressDialog.show();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    if (user_type.equalsIgnoreCase("Student"))
                        Firebase.getInstance().mDatabase.child(STUDENT).removeEventListener(valueEventListener);
                    else
                        Firebase.getInstance().mDatabase.child(EMPLOYEE).removeEventListener(valueEventListener);

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        Log.e("Signup", user.getKey() + "");
                        if (user.getKey().equalsIgnoreCase(binding.etId.getText().toString())) {
                            createUser();
                            return;
                        }
                    }

                    Toast.makeText(Gen_SignupForm.this, "Please enter valid organization id", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (user_type.equalsIgnoreCase("Student"))
                        Firebase.getInstance().mDatabase.child(STUDENT).removeEventListener(valueEventListener);
                    else
                        Firebase.getInstance().mDatabase.child(EMPLOYEE).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };

            if (user_type.equalsIgnoreCase("Student"))
                Firebase.getInstance().mDatabase.child(STUDENT).addValueEventListener(valueEventListener);
            else
                Firebase.getInstance().mDatabase.child(EMPLOYEE).addValueEventListener(valueEventListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
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
