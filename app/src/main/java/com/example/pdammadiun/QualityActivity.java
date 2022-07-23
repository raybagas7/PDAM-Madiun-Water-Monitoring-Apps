package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class QualityActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //ListView
    ListView myListView, myListViewStatus;
    ArrayList<String> myArrayList = new ArrayList<>();
    ArrayList<String> myArrayListStatus = new ArrayList<>();
    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;
    private DatabaseReference QualRef, ListRef, ListQual, ListQualStatus;
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality);

        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(QualityActivity.this, android.R.layout.simple_list_item_1, myArrayList);
        ArrayAdapter<String> myArrayAdapterStatus = new ArrayAdapter<String>(QualityActivity.this, R.layout.aligned_right_list, myArrayListStatus);

        myListView = (ListView) findViewById(R.id.listview1);
        myListView.setAdapter(myArrayAdapter);

        myListViewStatus = (ListView) findViewById(R.id.listviewstatus);
        myListViewStatus.setAdapter(myArrayAdapterStatus);
        myListViewStatus.setEnabled(false);


        //Firebase
        QualRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp");
        ListRef = QualRef.child("LokasiQuality");
        ListQual = QualRef.child("MonitoringQuality");
        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        //ToolBar
        setSupportActionBar(toolbar);
        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_quality);

        //ListViewShow
        ListQual.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getKey();
                myArrayList.add(value);
                myArrayAdapter.notifyDataSetChanged();

                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = myListView.getItemAtPosition(position);
                        //cekloglist
                        String isilist = listItem.toString();
                        Log.d("QualityYangdiclick", isilist);
                        //Pindah Activity Ke Display Quality

                        Intent displayIntent = new Intent(QualityActivity.this, QualityDisplayActivity.class);
                        displayIntent.putExtra("alatmonitoring", isilist);
                        startActivity(displayIntent);
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Unfinished Online Status At List
        /*ListQual.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getKey();
                ListQual.child(value).child("status").child("current").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String statusValue = snapshot.getValue().toString();
                        myArrayListStatus.add(statusValue);
                        myArrayAdapterStatus.notifyDataSetChanged();
                        ListQual.child(value).child("status").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                finish();
                                startActivity(getIntent());
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayAdapterStatus.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

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
        switch (item.getItemId()) {
            case R.id.nav_quality:
                break;
            case R.id.nav_home:
                Intent intent = new Intent(QualityActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_debit:
                Intent intent1 = new Intent(QualityActivity.this, DebitActivity.class);
                startActivity(intent1);
                break;
            /*case R.id.nav_profile:
                Intent intent2 = new Intent(QualityActivity.this, ProfileActivity.class);
                startActivity(intent2);
                break;*/
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(QualityActivity.this, LoginActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent3);
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /*private void RequestNewSite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QualityActivity.this, R.style.AlertDialog_AppCompat);
        builder.setTitle("Masukan Nama Lokasi Monitoring Kualitas Air :");

        final EditText groupNameField = new EditText(QualityActivity.this);
        groupNameField.setHint("Nama Lokasi");
        builder.setView(groupNameField);

        builder.setPositiveButton("Buat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String siteName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(siteName)) {
                    Toast.makeText(QualityActivity.this, "Tolong isi nama lokasi", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewSite(siteName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }*/

    /*private void CreateNewSite(final String siteName) {
        String idactivity = "Quality";
        Toast.makeText(QualityActivity.this, "Silahkan Pilih Lokasi", Toast.LENGTH_SHORT).show();
        Intent GeoIntent = new Intent(QualityActivity.this, GeomapActivity.class);
        GeoIntent.putExtra("lokasibaru", siteName);
        GeoIntent.putExtra("idactivity", idactivity);
        startActivity(GeoIntent);
    }*/
}