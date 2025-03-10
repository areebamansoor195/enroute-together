package com.juw.areebamansoor.enroutetogether;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juw.areebamansoor.enroutetogether.databinding.ActivityGenLoginformBinding;
import com.juw.areebamansoor.enroutetogether.firebase.Firebase;
import com.juw.areebamansoor.enroutetogether.model.User;
import com.juw.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Gen_LoginForm extends AppCompatActivity {

    private static final String TAG = "Login";
    private ActivityGenLoginformBinding binding;

    private List<User> userList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gen_loginform);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gen_LoginForm.this, Gen_SignupForm.class);
                startActivity(intent);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    getFirebaseUsers();
                    return;
                }

                Toast.makeText(Gen_LoginForm.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });

        binding.forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Gen_LoginForm.this, Forgot_EmailActivitiy.class);
                startActivity(intent);
            }
        });
    }

    private boolean matchUser() {
        try {
            for (User user : userList) {
                if (user.getEmail().equalsIgnoreCase(binding.etEmail.getText().toString().trim())
                        && AESCrypt.decrypt(user.getPassword()).equalsIgnoreCase(binding.etPwd.getText().toString().trim())) {
                    SharedPreferencHandler.setUser(new Gson().toJson(user));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void getFirebaseUsers() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.child("Users").removeEventListener(valueEventListener);
                progressDialog.dismiss();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    userList.add(user);
                }

                if (userList.size() > 0) {
                    if (matchUser()) {
                        Log.e("Login", "User Login");
                        User user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);
                        if (user.getVerified()) {
                            finish();
                            startActivity(new Intent(Gen_LoginForm.this, Gen_HomeActivity.class));
                            SharedPreferencHandler.setIsLogin(true);
                        } else {
                            showVerificationDialog();
                        }
                        return;
                    }
                    Toast.makeText(Gen_LoginForm.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Gen_LoginForm.this, "Unable to login user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Firebase.getInstance().mDatabase.child("Users").removeEventListener(valueEventListener);
            }
        };
        Firebase.getInstance().mDatabase.child("Users").addValueEventListener(valueEventListener);

    }

    private void showVerificationDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_verification_dialog);

        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        Button verifyBtn = dialog.findViewById(R.id.btn_verify);
        TextView verifyMessage = dialog.findViewById(R.id.verify_message);
        final EditText et_code = dialog.findViewById(R.id.et_code);

        final User user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);


        verifyMessage.setText("Please enter the code that was sent to you at " + user.getEmail());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_code.getText().toString())) {
                    if (et_code.getText().toString().equalsIgnoreCase(user.getOtp())) {
                        updateVerifiedStatus(dialog);
                        return;
                    }
                    Toast.makeText(Gen_LoginForm.this, "Invalid code", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(Gen_LoginForm.this, "Please enter code", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    private void updateVerifiedStatus(final Dialog dialog) {

        final User user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);
        user.setVerified(true);
        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.child("Users").child(user.getUserId()).removeEventListener(valueEventListener);
                progressDialog.dismiss();
                dialog.dismiss();
                finish();
                User user = dataSnapshot.getValue(User.class);
                SharedPreferencHandler.setUser(new Gson().toJson(user));
                startActivity(new Intent(Gen_LoginForm.this, Gen_HomeActivity.class));
                SharedPreferencHandler.setIsLogin(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Firebase.getInstance().mDatabase.child("Users").child(user.getUserId()).removeEventListener(valueEventListener);
                progressDialog.dismiss();
            }
        };
        Firebase.getInstance().mDatabase.child("Users").child(user.getUserId()).setValue(user);
        Firebase.getInstance().mDatabase.child("Users").child(user.getUserId()).addValueEventListener(valueEventListener);
    }

    private boolean validateFields() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPwd.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return false;
        }

        return true;
    }
}
