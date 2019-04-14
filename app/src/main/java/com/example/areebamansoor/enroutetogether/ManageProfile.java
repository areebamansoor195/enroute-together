package com.example.areebamansoor.enroutetogether;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityManageprofileBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ManageProfile extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    private ActivityManageprofileBinding binding;
    private User user;
    private String gender;
    private Uri filePath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manageprofile);
        setSupportActionBar(binding.toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        gender = user.getGender();
        binding.etName.setText(user.getName());
        binding.etEmail.setText(user.getEmail());
        binding.etOrganization.setText(user.getOrganizationId());

        if (user.getPhone_number() != null)
            binding.etNumber.setText(user.getPhone_number());

        if (user.getImage_url() != null)
            Picasso.get().load(user.getImage_url()).into(binding.profileImage);


        if (user.getGender().equalsIgnoreCase("Male"))
            binding.radioGroup.check(R.id.rb_male);
        else
            binding.radioGroup.check(R.id.rb_female);


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

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    user.setName(binding.etName.getText().toString());
                    user.setOrganizationId(binding.etOrganization.getText().toString());
                    user.setPhone_number(binding.etNumber.getText().toString());
                    user.setGender(gender);

                    uploadImage();

                    return;
                }
                Toast.makeText(ManageProfile.this, "Please fill required fields", Toast.LENGTH_SHORT).show();

            }
        });

        binding.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    chooseImage();
                }
            }
        });

    }

    private void updateProfile() {
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        Firebase.getInstance().mDatabase.child(user.getFirebaseId()).setValue(user);

        Firebase.getInstance().mDatabase.child(user.getFirebaseId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                SharedPreferencHandler.setUser(new Gson().toJson(user));
                Toast.makeText(ManageProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ManageProfile.this, "Unable to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {

        if (!binding.profileImage.getDrawable().getConstantState().equals
                (getResources().getDrawable(R.drawable.icon2).getConstantState())) {

            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference userImageRef = Firebase.getInstance().storageReference.child("User images/" + user.getFirebaseId() + ".jpg");

            binding.profileImage.setDrawingCacheEnabled(true);
            binding.profileImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) binding.profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = userImageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    exception.printStackTrace();
                    Toast.makeText(ManageProfile.this, "Uploading image failed", Toast.LENGTH_SHORT).show();
                    updateProfile();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("Manage Profile", "onSuccess: uri= " + uri.toString());
                            user.setImage_url(uri.toString());
                            progressDialog.dismiss();
                            updateProfile();
                        }
                    });


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });


        } else {
            updateProfile();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseImage();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean validateFields() {
        String name = binding.etName.getText().toString().trim();
        String organizationId = binding.etOrganization.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(organizationId)) {
            return false;
        }

        return true;
    }


}
