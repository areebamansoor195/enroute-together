package com.juw.areebamansoor.enroutetogether;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.juw.areebamansoor.enroutetogether.databinding.ActivityGenSplashscreenBinding;
import com.juw.areebamansoor.enroutetogether.utils.SharedPreferencHandler;

public class Gen_SplashScreen extends AppCompatActivity {

    private ActivityGenSplashscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gen_splashscreen);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharedPreferencHandler.getLogin()) {
                    Intent i = new Intent(Gen_SplashScreen.this, Gen_HomeActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }

                Intent i = new Intent(Gen_SplashScreen.this, Gen_LoginForm.class);
                startActivity(i);
                finish();
            }
        }, 3000);

    }

    private String getAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
