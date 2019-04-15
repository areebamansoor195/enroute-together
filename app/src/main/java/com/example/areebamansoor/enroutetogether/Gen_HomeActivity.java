package com.example.areebamansoor.enroutetogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;

public class Gen_HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btn_offer, btn_book, btn_help, btn_manageprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_home);


        //defining cards
        btn_offer = (Button) findViewById(R.id.buttonOffer);
        btn_book = (Button) findViewById(R.id.buttonBook);
        btn_help = (Button) findViewById(R.id.buttonhelp);
        btn_manageprofile = (Button) findViewById(R.id.buttonmanageprofile);


        Toast.makeText(Gen_HomeActivity.this, "In Main", Toast.LENGTH_SHORT).show();

        btn_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Gen_HomeActivity.this, Offer_OfferRide.class);
                Intent intent = new Intent(Gen_HomeActivity.this, BookRideActivity.class);
                intent.putExtra("map", "offer_ride");
                startActivity(intent);
                Toast.makeText(Gen_HomeActivity.this, "Offer Ride", Toast.LENGTH_SHORT).show();
            }
        });

        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Gen_HomeActivity.this, Book_DriversList.class);
                Intent intent = new Intent(Gen_HomeActivity.this, BookRideActivity.class);
                intent.putExtra("map", "book_ride");
                startActivity(intent);
                Toast.makeText(Gen_HomeActivity.this, "Book Ride", Toast.LENGTH_SHORT).show();
            }
        });

        btn_manageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gen_HomeActivity.this, ManageProfile.class);
                startActivity(intent);
                Toast.makeText(Gen_HomeActivity.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gen_HomeActivity.this, HelpActivity.class);
                startActivity(intent);
                Toast.makeText(Gen_HomeActivity.this, "Help", Toast.LENGTH_SHORT).show();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.manage_profile) {

            startActivity(new Intent(this, ManageProfile.class));

        } else if (id == R.id.action_settings) {

        } else if (id == R.id.logout) {
            SharedPreferencHandler.setIsLogin(false);
            SharedPreferencHandler.setUser("");
            finish();
            startActivity(new Intent(this, Gen_LoginForm.class));

        } else if (id == R.id.help) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
