package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private String PdamUserEmail;
    private DatabaseReference UserRef, DebitLokasiRef, KualitasLokasiRef;
    private String PdamUserID, getdebitlocation, getqualitylocation, mapscondition = "";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;
    private GoogleMap litmap, litmapdebit;
    private double maplong, maplat;
    private Spinner spinnerdebit, spinnerkualitas;
    private double debitlat, debitlong;
    private double kuallat, kuallong;
    LatLng DebitShowLocation;
    private ArrayList<String> arrayListDebit = new ArrayList<>();
    private ArrayList<String> arrayListQuality = new ArrayList<>();
    private ImageButton qualbut, debbut;
    private Menu menu;
    private TextView currentuseremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.litmap);
        mapFragment.getMapAsync(onMapReadyCallback1());


        SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.litmapdebit);
        mapFragment1.getMapAsync(onMapReadyCallback2());

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DebitLokasiRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiDebit");
        KualitasLokasiRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiQuality");
        PdamUserID = mAuth.getCurrentUser().getUid();
        PdamUserEmail = mAuth.getCurrentUser().getEmail();
        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        qualbut = findViewById(R.id.searchqual);
        debbut = findViewById(R.id.searchdebit);
        toolbar = findViewById(R.id.toolbar);
        //ToolBar
        setSupportActionBar(toolbar);
        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        spinnerdebit = findViewById(R.id.homelokasidebit);
        spinnerkualitas = findViewById(R.id.homelokasiquality);
        currentuseremail = findViewById(R.id.currentuseremail);

        currentuseremail.setText(PdamUserEmail);

        ShowDataSpinnerQuality();
        ShowDataSpinnerDebit();
        Log.d("EmailUser", PdamUserEmail);

        qualbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapscondition = "Quality";
            }
        });
        debbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapscondition = "debit";
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*MenuItem profileEmail = findViewById(R.id.profile_item);
        profileEmail.setTitle(PdamUserEmail);*/
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_quality:
                Intent intent = new Intent(HomeActivity.this, QualityActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_debit:
                Intent intent1 = new Intent(HomeActivity.this, DebitActivity.class);
                startActivity(intent1);
                break;
            /*ase R.id.nav_profile:
                Intent intent2 = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent2);
                break;*/
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(HomeActivity.this, LoginActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent3);
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ShowDataSpinnerQuality() {
        KualitasLokasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListQuality.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayListQuality.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.style_spinner, arrayListQuality);
                spinnerkualitas.setAdapter(arrayAdapter);

                spinnerkualitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object alamatdebit = parent.getItemAtPosition(position);
                        getqualitylocation = alamatdebit.toString();
                        Log.d("Chosen DebitLocation", getqualitylocation);
                        KualitasLokasiRef.child(getqualitylocation).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String quallat, quallong;
                                kuallat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                kuallong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                quallat = String.valueOf(kuallat);
                                quallong = String.valueOf(kuallong);
                                Log.d("Chosen QualLat", quallat);
                                Log.d("Chosen QualLong", quallong);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ShowDataSpinnerDebit() {
        DebitLokasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListDebit.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayListDebit.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.style_spinner, arrayListDebit);
                spinnerdebit.setAdapter(arrayAdapter);

                spinnerdebit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object alamatdebit = parent.getItemAtPosition(position);
                        getdebitlocation = alamatdebit.toString();
                        Log.d("Chosen DebitLocation", getdebitlocation);
                        DebitLokasiRef.child(getdebitlocation).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String debilat, debilong;
                                debitlat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                debitlong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                debilat = String.valueOf(debitlat);
                                debilong = String.valueOf(debitlong);
                                Log.d("Chosen DebLat", debilat);
                                Log.d("Chosen DebLong", debilong);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public OnMapReadyCallback onMapReadyCallback1() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                qualbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        litmap = googleMap;
                        LatLng mapQuality = new LatLng(kuallat, kuallong);
                        litmap.addMarker(new MarkerOptions().position(mapQuality).title(getqualitylocation));
                        litmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapQuality, 16F));
                    }
                });
            }
        };
    }

    public OnMapReadyCallback onMapReadyCallback2() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                debbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        litmapdebit = googleMap;
                        LatLng mapDebit = new LatLng(debitlat, debitlong);
                        litmapdebit.addMarker(new MarkerOptions().position(mapDebit).title(getdebitlocation));
                        litmapdebit.moveCamera(CameraUpdateFactory.newLatLngZoom(mapDebit, 16F));

                    }
                });
            }
        };
    }
}