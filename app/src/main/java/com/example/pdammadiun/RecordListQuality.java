package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.ArrayList;
import java.util.Collections;

public class RecordListQuality extends AppCompatActivity {

    RecyclerView recyclerViewQual;
    DatabaseReference LokasiRefQual;
    private ArrayList<String> arrayList = new ArrayList<>();
    MyAdapterQual myAdapter;
    ArrayList<RecordQual> listqual;
    private String alatpilih, lokasirecord;
    private Spinner spinnerrecordqual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list_quality);

        spinnerrecordqual = (Spinner) findViewById(R.id.recordspinnerqual);
        alatpilih = getIntent().getExtras().get("alatmonitoring").toString();
        recyclerViewQual = findViewById(R.id.recordListQual);
        LokasiRefQual = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiQuality");
        recyclerViewQual.setHasFixedSize(true);
        recyclerViewQual.setLayoutManager(new LinearLayoutManager(RecordListQuality.this));
        showDataSpinner();
    }

    private void showDataSpinner() {
        Trace retrievehistoryqualityTrace = FirebasePerformance.getInstance().newTrace("retrieve_quality_history");
        retrievehistoryqualityTrace.start();
        LokasiRefQual.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayList.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecordListQuality.this, R.layout.style_spinner, arrayList);
                spinnerrecordqual.setAdapter(arrayAdapter);

                spinnerrecordqual.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object location = parent.getItemAtPosition(position);
                        String lokasi = location.toString();
                        lokasirecord = lokasi;
                        Log.d("Chosen Location", lokasi);


                        //RecordList
                        listqual = new ArrayList<>();
                        myAdapter = new MyAdapterQual(RecordListQuality.this, listqual);
                        recyclerViewQual.setAdapter(myAdapter);

                        LokasiRefQual.child(lokasirecord).child("Submitted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                listqual = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.d("recordsnapshot", dataSnapshot.toString());
                                    RecordQual record = dataSnapshot.getValue(RecordQual.class);
                                    Log.d("HasilRecod", record.toString());
                                    listqual.add(record);

                                }
                                listqual = new ArrayList<RecordQual>(listqual.subList(listqual.size() > 14 ? listqual.size() - 15 : 0, listqual.size()));
                                Collections.reverse(listqual);
                                myAdapter = new MyAdapterQual(RecordListQuality.this, listqual);
                                recyclerViewQual.setAdapter(myAdapter);
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
}