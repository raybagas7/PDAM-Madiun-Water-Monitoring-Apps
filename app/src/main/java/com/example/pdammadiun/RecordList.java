package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class RecordList extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference LokasiRef;
    private ArrayList<String> arrayList = new ArrayList<>();
    MyAdapter myAdapter;
    ArrayList<Record> list;
    private String alatpilih, lokasirecord;
    private Spinner spinnerrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        spinnerrecord = (Spinner) findViewById(R.id.recordspinner);
        alatpilih = getIntent().getExtras().get("alatmonitoring").toString();
        recyclerView = findViewById(R.id.recordList);
        LokasiRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiDebit");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        showDataSpinner();
    }


    private void showDataSpinner() {
        Trace retrievehistoryqualityTrace = FirebasePerformance.getInstance().newTrace("retrieve_debit_history");
        retrievehistoryqualityTrace.start();
        LokasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayList.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecordList.this, R.layout.style_spinner, arrayList);
                spinnerrecord.setAdapter(arrayAdapter);

                spinnerrecord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object location = parent.getItemAtPosition(position);
                        String lokasi = location.toString();
                        lokasirecord = lokasi;
                        Log.d("Chosen Location", lokasi);


                        //RecordList
                        list = new ArrayList<>();
                        myAdapter = new MyAdapter(RecordList.this, list);
                        recyclerView.setAdapter(myAdapter);

                        LokasiRef.child(lokasirecord).child("Submitted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                list = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.d("recordsnapshot", dataSnapshot.toString());
                                    Record record = dataSnapshot.getValue(Record.class);
                                    Log.d("HasilRecod", record.toString());
                                    list.add(record);

                                }
                                list = new ArrayList<Record>(list.subList(list.size() > 14 ? list.size() - 15 : 0, list.size()));
                                Collections.reverse(list);
                                myAdapter = new MyAdapter(RecordList.this, list);
                                recyclerView.setAdapter(myAdapter);
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
        retrievehistoryqualityTrace.stop();
    }

    @Override
    public void onBackPressed() {
        Intent backintent = new Intent(RecordList.this, DebitDisplayActivity.class);
        backintent.putExtra("alatmonitoring", alatpilih);
        backintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backintent);
        finish();
    }
}

