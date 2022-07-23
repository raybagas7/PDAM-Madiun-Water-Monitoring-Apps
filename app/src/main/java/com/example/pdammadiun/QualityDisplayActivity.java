package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class QualityDisplayActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String PdamUserEmail;
    private DatabaseReference QualRef, LokasiRef, SumurRef;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayListSumur = new ArrayList<>();
    private String alatpilih, saveCurrentDate, saveCurrentTime, getsumur, idactivityrecord, saveCurrentTimeFull, saveCurrentTimePlus, nextAvailableTime, lokasibaru, Sroundph;
    private Spinner spinner, spinnersumur;
    private TextView Nvolt, Norp, Nph, Ntds, Ntemperature, Nturbidity, namalokasi, Nklorin, Nalamatlengkap;
    private ImageView tambahlokasi, recordqual, setmaxtds, setmaxsuhu, setmaxturbi, setmaxorp, setmaxph, penyegaranqual;
    private ImageView onlinestat, offlinestat;
    private EditText inmaxtds, inmaxsuhu, inmaxturbi, inmaxorp, inmaxph;
    private int vmaxtds, vmaxsuhu, vmaxturbi, vmaxorp, vmaxph, realtds, realsuhu, realturbi, realorp, realph;
    private int hasilorp, hasilph, hasiltds, hasiltemperature, hasilturbidity, hasilklorin, counterquality = -1;
    private float floatvolt, floatorp, floatph, floattds, floattemperature, floatturbidity, floatklorin, floatklorindivide;
    private Calendar calendar, calendarplus;
    private Button kirimsubmit;
    private CustomGauge temperaturegauge, tdsgauge, orpgauge, turbigauge, phgauge, kloringauge;
    private SharedPreferences spmax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_display);

        mAuth = FirebaseAuth.getInstance();
        PdamUserEmail = mAuth.getCurrentUser().getEmail();
        LokasiRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiQuality");
        SumurRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("JenisSumur");
        QualRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("MonitoringQuality");

        alatpilih = getIntent().getExtras().get("alatmonitoring").toString();

        calendar = Calendar.getInstance();
        calendarplus = Calendar.getInstance();
        calendarplus.add(Calendar.MINUTE, 65);

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        SimpleDateFormat currentTimeFull = new SimpleDateFormat("MMM dd, yyy hh:mm a");
        saveCurrentTimeFull = currentTimeFull.format(calendar.getTime());
        saveCurrentTimePlus = currentTimeFull.format(calendarplus.getTime());

        spinner = findViewById(R.id.listlokasi);
        spinnersumur = findViewById(R.id.spinnersumur);

        showDataSpinner();
        showDataSpinnersumur();

        Norp = (TextView) findViewById(R.id.nilaiorp);
        Nvolt = (TextView) findViewById(R.id.nilaivolt);
        Nph = (TextView) findViewById(R.id.nilaiph);
        Ntds = (TextView) findViewById(R.id.nilaitds);
        Ntemperature = (TextView) findViewById(R.id.nilaitemperatur);
        Nturbidity = (TextView) findViewById(R.id.nilaiturbidity);
        Nklorin = (TextView) findViewById(R.id.nilaiklorin);
        namalokasi = (TextView) findViewById(R.id.namalokasi);
        Nalamatlengkap = (TextView) findViewById(R.id.alamatlengkap);
        tambahlokasi = (ImageView) findViewById(R.id.tambahlokasibaru);
        recordqual = (ImageView) findViewById(R.id.recordqual);
        penyegaranqual = (ImageView) findViewById(R.id.penyegaranquuality);
        onlinestat = (ImageView) findViewById(R.id.online_status_kualitas);
        offlinestat = (ImageView) findViewById(R.id.offline_status_kualitas);
        temperaturegauge = (CustomGauge) findViewById(R.id.gaugesuhu);
        orpgauge = (CustomGauge) findViewById(R.id.gaugeorp);
        turbigauge = (CustomGauge) findViewById(R.id.gaugeturbi);
        phgauge = (CustomGauge) findViewById(R.id.gaugeph);
        tdsgauge = (CustomGauge) findViewById(R.id.gaugetds);
        kloringauge = (CustomGauge) findViewById(R.id.kloringauge);
        kirimsubmit = (Button) findViewById(R.id.kirimqual);
        setmaxtds = (ImageView) findViewById(R.id.setmaxtds);
        setmaxsuhu = (ImageView) findViewById(R.id.setmaxsuhu);
        setmaxorp = (ImageView) findViewById(R.id.setmaxorp);
        setmaxturbi = (ImageView) findViewById(R.id.setmaxturbidity);
        setmaxph = (ImageView) findViewById(R.id.setmaxph);
        inmaxtds = (EditText) findViewById(R.id.textmaxtds);
        inmaxsuhu = (EditText) findViewById(R.id.textmaxsuhu);
        inmaxorp = (EditText) findViewById(R.id.textmaxorp);
        inmaxturbi = (EditText) findViewById(R.id.textmaxturbidity);
        inmaxph = (EditText) findViewById(R.id.textmaxph);
        spmax = getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);


        RetrieveQualityInfo();
        getAllMax();
        changeMaxTds();
        changeMaxSuhu();
        changeMaxTurbi();
        changeMaxOrp();
        changeMaxPh();
        StatusDevice();

        tambahlokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestNewSite();
            }
        });

        recordqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RecordIntent = new Intent(QualityDisplayActivity.this, RecordListQuality.class);
                RecordIntent.putExtra("alatmonitoring", alatpilih);
                RecordIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RecordIntent);
            }
        });

        penyegaranqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ActivityQualityDisplay", "Refreshed");
                finish();
                startActivity(getIntent());
            }
        });

    }

    private void changeMaxTds() {
        setmaxtds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxtds = Integer.parseInt(inmaxtds.getText().toString());

                SharedPreferences.Editor editor = spmax.edit();

                editor.putInt("tds", vmaxtds);
                editor.commit();

                SharedPreferences spcall = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
                realtds = spcall.getInt("tds", 100);
                Log.d("realtdsbut", Integer.toString(realtds));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxSuhu() {
        setmaxsuhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxsuhu = Integer.parseInt(inmaxsuhu.getText().toString());

                SharedPreferences.Editor editor = spmax.edit();

                editor.putInt("suhu", vmaxsuhu);
                editor.commit();

                SharedPreferences spcall = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
                realsuhu = spcall.getInt("suhu", 100);
                Log.d("realsuhubut", Integer.toString(realsuhu));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxTurbi() {
        setmaxturbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxturbi = Integer.parseInt(inmaxturbi.getText().toString());

                SharedPreferences.Editor editor = spmax.edit();

                editor.putInt("turbi", vmaxturbi);
                editor.commit();

                SharedPreferences spcall = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
                realturbi = spcall.getInt("turbi", 100);
                Log.d("realturbibut", Integer.toString(realturbi));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxOrp() {
        setmaxorp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxorp = Integer.parseInt(inmaxorp.getText().toString());

                SharedPreferences.Editor editor = spmax.edit();

                editor.putInt("orp", vmaxorp);
                editor.commit();

                SharedPreferences spcall = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
                realorp = spcall.getInt("orp", 100);
                Log.d("realorpbut", Integer.toString(realorp));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxPh() {
        setmaxph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxph = Integer.parseInt(inmaxph.getText().toString());

                SharedPreferences.Editor editor = spmax.edit();

                editor.putInt("ph", vmaxph);
                editor.commit();

                SharedPreferences spcall = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
                realph = spcall.getInt("ph", 100);
                Log.d("realphbut", Integer.toString(realph));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void showDataSpinner() {
        LokasiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayList.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(QualityDisplayActivity.this, R.layout.style_spinner, arrayList);
                spinner.setAdapter(arrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object location = parent.getItemAtPosition(position);
                        String ambillokasi = location.toString();
                        lokasibaru = ambillokasi;
                        Log.d("Chosen Location", ambillokasi);
                        String hasilalamat = snapshot.child(ambillokasi).child("alamat").getValue().toString();
                        Nalamatlengkap.setText(hasilalamat);

                        //TimeNext
                        LokasiRef.child(ambillokasi).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                nextAvailableTime = snapshot.child("nextattempt").getValue().toString();
                                Log.d("nextattempt", nextAvailableTime);
                                String pattern = "MMM dd, yyy hh:mm a";
                                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                                if (nextAvailableTime.equals("Ready")) {
                                    kirimsubmit.setEnabled(true);
                                } else {
                                    kirimsubmit.setEnabled(false);
                                }
                                try {
                                    Date date1 = sdf.parse(saveCurrentTimeFull);
                                    Date date2 = sdf.parse(nextAvailableTime);

                                    if (date2.before(date1)) {
                                        LokasiRef.child(lokasibaru).child("nextattempt").setValue("Ready");
                                    } else {
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        kirimsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(QualityDisplayActivity.this);
                                builder.setCancelable(true);
                                builder.setTitle("Konfirmasi Kualitas Air");
                                builder.setMessage(Html.fromHtml("Apakan anda ingin mengirimkan data kualitas air sekarang pada lokasi " + "<b>" + ambillokasi +
                                        "</b>" + "?" + "<p style=\"color:red\"><b>" + " Anda tidak dapat mengirim data kembali dalam kurun waktu 1 jam bila terkonfirmasi" + "</p></b>"));
                                builder.setPositiveButton("Konfirmasi",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference lokasisubmit = LokasiRef.child(ambillokasi).child("Submitted").push();
                                                String PushID = lokasisubmit.getKey();
                                                String orp = new DecimalFormat("##.##").format(floatorp);
                                                String ph = new DecimalFormat("##.##").format(floatph);
                                                String tds = new DecimalFormat("##.##").format(floattds);
                                                String suhu = new DecimalFormat("##.##").format(floattemperature);
                                                String turbi = new DecimalFormat("##.##").format(floatturbidity);
                                                String klorin = String.valueOf(floatklorindivide);
                                                LokasiRef.child(ambillokasi).child("nextattempt").setValue(saveCurrentTimePlus);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("orp").setValue(orp);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("ph").setValue(ph);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("sumur").setValue(getsumur);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("tds").setValue(tds);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("temperature").setValue(suhu);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("turbidity").setValue(turbi);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("time").setValue(saveCurrentTime);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("date").setValue(saveCurrentDate);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("klorin").setValue(klorin);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("submitter").setValue(PdamUserEmail);
                                                //For Last Attempt
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("orp").setValue(orp);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("ph").setValue(ph);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("sumur").setValue(getsumur);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("tds").setValue(tds);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("temperature").setValue(suhu);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("turbidity").setValue(turbi);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("time").setValue(saveCurrentTime);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("date").setValue(saveCurrentDate);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("klorin").setValue(klorin);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("submitter").setValue(PdamUserEmail);
                                                Toast.makeText(QualityDisplayActivity.this, "Berhasil Terkirim", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
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

    private void showDataSpinnersumur() {
        SumurRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListSumur.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayListSumur.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(QualityDisplayActivity.this, R.layout.style_spinner, arrayListSumur);
                spinnersumur.setAdapter(arrayAdapter);

                spinnersumur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object sumur = parent.getItemAtPosition(position);
                        getsumur = sumur.toString();
                        Log.d("Chosen Sumur", getsumur);
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


    private void RetrieveQualityInfo() {
        Trace retrievequalityinfoTrace = FirebasePerformance.getInstance().newTrace("retrieve_quality_realtime_info");
        retrievequalityinfoTrace.start();
        QualRef.child(alatpilih).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && snapshot.hasChild("orp")
                && snapshot.hasChild("ph") && snapshot.hasChild("tds")
                && snapshot.hasChild("temperature") && snapshot.hasChild("turbidity")
                && snapshot.hasChild("VoltTurbi") && snapshot.hasChild("status")
                && snapshot.child("status").hasChild("current")){
                    QualRef.child(alatpilih).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            floatorp = Float.parseFloat(snapshot.child("orp").getValue().toString());
                            floatvolt = Float.parseFloat(snapshot.child("VoltTurbi").getValue().toString());
                            floatph = Float.parseFloat(snapshot.child("ph").getValue().toString());
                            floattds = Float.parseFloat(snapshot.child("tds").getValue().toString());
                            floattemperature = Float.parseFloat(snapshot.child("temperature").getValue().toString());
                            floatturbidity = Float.parseFloat(snapshot.child("turbidity").getValue().toString());

                            hasilorp = Math.round(floatorp);
                            hasilph = Math.round(floatph);
                            hasiltds = Math.round(floattds);
                            hasiltemperature = Math.round(floattemperature);
                            hasilturbidity = Math.round(floatturbidity);
                            String namaalat = alatpilih;

                            Sroundph = new DecimalFormat("##.#").format(floatph);

                            if (Sroundph.equals("7")){
                                if (hasilorp >= 735 && hasilorp < 760){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 760 && hasilorp < 772){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 772 && hasilorp < 780){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 780 && hasilorp < 788){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 788 && hasilorp < 794){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 794 && hasilorp < 800){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 800){
                                    floatklorin = 35;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.1")){
                                if (hasilorp >= 725 && hasilorp < 750){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 750 && hasilorp < 765){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 765 && hasilorp < 772){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 772 && hasilorp < 780){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 780 && hasilorp < 785){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 785 && hasilorp < 792){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 792 && hasilorp < 798){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 798){
                                    floatklorin = 40;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.2")){
                                if (hasilorp >= 720 && hasilorp < 742){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 742 && hasilorp < 756){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 756 && hasilorp < 763){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 763 && hasilorp < 772){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 772 && hasilorp < 778){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 778 && hasilorp < 782){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 782 && hasilorp < 786){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 786 && hasilorp < 792){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 792 && hasilorp < 798){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 798){
                                    floatklorin = 50;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.3")){
                                if (hasilorp >= 712 && hasilorp < 734){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 734 && hasilorp < 748){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 748 && hasilorp < 756){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 756 && hasilorp < 763){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 763 && hasilorp < 769){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 769 && hasilorp < 774){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 774 && hasilorp < 778){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 778 && hasilorp < 782){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 782 && hasilorp < 785){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 785 && hasilorp < 789){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 789 && hasilorp < 793){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 793 && hasilorp < 796){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 796){
                                    floatklorin = 65;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.4")){
                                if (hasilorp >= 704 && hasilorp < 725){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 725 && hasilorp < 738){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 738 && hasilorp < 748){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 748 && hasilorp < 754){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 754 && hasilorp < 761){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 761 && hasilorp < 765){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 765 && hasilorp < 770){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 770 && hasilorp < 774){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 774 && hasilorp < 778){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 778 && hasilorp < 781){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 781 && hasilorp < 783){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 783 && hasilorp < 786){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 786 && hasilorp < 788){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 788 && hasilorp < 792){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 792){
                                    floatklorin = 75;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.5")){
                                if (hasilorp >= 695 && hasilorp < 716){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 716 && hasilorp < 731){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 731 && hasilorp < 740){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 740 && hasilorp < 746){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 746 && hasilorp < 753){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 753 && hasilorp < 758){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 758 && hasilorp < 762){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 762 && hasilorp < 766){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 766 && hasilorp < 769){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 769 && hasilorp < 772){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 772 && hasilorp < 775){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 775 && hasilorp < 777){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 777 && hasilorp < 780){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 780 && hasilorp < 782){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 782 && hasilorp < 784){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 784 && hasilorp < 786){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 786 && hasilorp < 788){
                                    floatklorin = 85;
                                }
                                else if (hasilorp >= 788 && hasilorp < 790){
                                    floatklorin = 90;
                                }
                                else if (hasilorp >= 790 && hasilorp < 792){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 792){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.6")){
                                if (hasilorp >= 687 && hasilorp < 709){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 709 && hasilorp < 722){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 722 && hasilorp < 732){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 732 && hasilorp < 738){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 738 && hasilorp < 745){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 745 && hasilorp < 750){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 750 && hasilorp < 754){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 754 && hasilorp < 757){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 757 && hasilorp < 761){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 761 && hasilorp < 764){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 764 && hasilorp < 767){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 767 && hasilorp < 770){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 770 && hasilorp < 772){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 772 && hasilorp < 774){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 774 && hasilorp < 776){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 776 && hasilorp < 778){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 778 && hasilorp < 780){
                                    floatklorin = 85;
                                }
                                else if (hasilorp >= 780 && hasilorp < 782){
                                    floatklorin = 90;
                                }
                                else if (hasilorp >= 782 && hasilorp < 784){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 784){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.7")){
                                if (hasilorp >= 680 && hasilorp < 703){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 703 && hasilorp < 715){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 715 && hasilorp < 724){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 724 && hasilorp < 732){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 732 && hasilorp < 737){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 737 && hasilorp < 742){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 742 && hasilorp < 746){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 746 && hasilorp < 751){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 751 && hasilorp < 754){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 754 && hasilorp < 756){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 756 && hasilorp < 760){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 760 && hasilorp < 762){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 762 && hasilorp < 765){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 765 && hasilorp < 767){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 767 && hasilorp < 769){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 769 && hasilorp < 771){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 771 && hasilorp < 773){
                                    floatklorin = 85;
                                }
                                else if (hasilorp >= 773 && hasilorp < 775){
                                    floatklorin = 90;
                                }
                                else if (hasilorp >= 775 && hasilorp < 777){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 777){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.8")){
                                if (hasilorp >= 675 && hasilorp < 695){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 695 && hasilorp < 708){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 708 && hasilorp < 717){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 717 && hasilorp < 725){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 725 && hasilorp < 731){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 731 && hasilorp < 735){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 735 && hasilorp < 739){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 739 && hasilorp < 743){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 743 && hasilorp < 746){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 746 && hasilorp < 750){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 750 && hasilorp < 753){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 753 && hasilorp < 756){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 756 && hasilorp < 758){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 758 && hasilorp < 760){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 760 && hasilorp < 762){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 762 && hasilorp < 764){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 764 && hasilorp < 766){
                                    floatklorin = 85;
                                }
                                else if (hasilorp == 766) {
                                    floatklorin = 90;
                                }
                                else if (hasilorp >= 767 && hasilorp < 769){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 769){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("7.9")){
                                if (hasilorp >= 668 && hasilorp < 689){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 689 && hasilorp < 702){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 702 && hasilorp < 712){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 712 && hasilorp < 718){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 718 && hasilorp < 724){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 724 && hasilorp < 729){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 729 && hasilorp < 734){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 734 && hasilorp < 736){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 736 && hasilorp < 741){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 741 && hasilorp < 744){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 744 && hasilorp < 746){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 746 && hasilorp < 749){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 749 && hasilorp < 752){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 752 && hasilorp < 754){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 754 && hasilorp < 756){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 756 && hasilorp < 758){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 758 && hasilorp < 760){
                                    floatklorin = 85;
                                }
                                else if (hasilorp >= 760 && hasilorp < 762) {
                                    floatklorin = 90;
                                }
                                else if (hasilorp == 762){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 763){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else if (Sroundph.equals("8")){
                                if (hasilorp >= 662 && hasilorp < 684){
                                    floatklorin = 5;
                                }
                                else if (hasilorp >= 684 && hasilorp < 697){
                                    floatklorin = 10;
                                }
                                else if (hasilorp >= 697 && hasilorp < 705){
                                    floatklorin = 15;
                                }
                                else if (hasilorp >= 705 && hasilorp < 714){
                                    floatklorin = 20;
                                }
                                else if (hasilorp >= 714 && hasilorp < 719){
                                    floatklorin = 25;
                                }
                                else if (hasilorp >= 719 && hasilorp < 724){
                                    floatklorin = 30;
                                }
                                else if (hasilorp >= 724 && hasilorp < 728){
                                    floatklorin = 35;
                                }
                                else if (hasilorp >= 728 && hasilorp < 733){
                                    floatklorin = 40;
                                }
                                else if (hasilorp >= 733 && hasilorp < 736){
                                    floatklorin = 45;
                                }
                                else if (hasilorp >= 736 && hasilorp < 738){
                                    floatklorin = 50;
                                }
                                else if (hasilorp >= 738 && hasilorp < 742){
                                    floatklorin = 55;
                                }
                                else if (hasilorp >= 742 && hasilorp < 744){
                                    floatklorin = 60;
                                }
                                else if (hasilorp >= 744 && hasilorp < 746){
                                    floatklorin = 65;
                                }
                                else if (hasilorp >= 746 && hasilorp < 749){
                                    floatklorin = 70;
                                }
                                else if (hasilorp >= 749 && hasilorp < 751){
                                    floatklorin = 75;
                                }
                                else if (hasilorp >= 751 && hasilorp < 753){
                                    floatklorin = 80;
                                }
                                else if (hasilorp >= 753 && hasilorp < 755){
                                    floatklorin = 85;
                                }
                                else if (hasilorp == 755) {
                                    floatklorin = 90;
                                }
                                else if (hasilorp >= 756 && hasilorp < 758){
                                    floatklorin = 95;
                                }
                                else if (hasilorp >= 758){
                                    floatklorin = 100;
                                } else {
                                    floatklorin = 0;
                                }
                            }
                            else {
                                floatklorin = 0;
                            }

                            hasilklorin = Math.round(floatklorin);
                            floatklorindivide = floatklorin/10;
                            Norp.setText(new DecimalFormat("##.##").format(floatorp) + "\nmV");
                            Nvolt.setText("(" + new DecimalFormat("##.##").format(floatvolt) + " V)");
                            Nph.setText(new DecimalFormat("##.##").format(floatph) + " pH");
                            Ntds.setText(new DecimalFormat("##.##").format(floattds) + "\nppm");
                            Ntemperature.setText(new DecimalFormat("##.##").format(floattemperature) + " °C");
                            Nturbidity.setText(new DecimalFormat("##.##").format(floatturbidity) + "\nNTU");
                            Nklorin.setText(new DecimalFormat("##.##").format(floatklorindivide) + " PPM");
                            namalokasi.setText("Monitoring Kualitas Air " + namaalat);
                            temperaturegauge.setValue(hasiltemperature);
                            orpgauge.setValue(hasilorp);
                            phgauge.setValue(hasilph);
                            tdsgauge.setValue(hasiltds);
                            turbigauge.setValue(hasilturbidity);
                            kloringauge.setValue(hasilklorin);

                            Log.d("alatpilih", alatpilih);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QualityDisplayActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else {
                    QualRef.child(alatpilih).child("orp").setValue(0);
                    QualRef.child(alatpilih).child("ph").setValue(0);
                    QualRef.child(alatpilih).child("tds").setValue(0);
                    QualRef.child(alatpilih).child("temperature").setValue(0);
                    QualRef.child(alatpilih).child("turbidity").setValue(0);
                    QualRef.child(alatpilih).child("VoltTurbi").setValue(0);
                    QualRef.child(alatpilih).child("status").child("current").setValue("");

                    finish();
                    getIntent().addFlags(getIntent().FLAG_ACTIVITY_NO_HISTORY).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getIntent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        retrievequalityinfoTrace.stop();


    }

    private void RequestNewSite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QualityDisplayActivity.this);
        builder.setTitle("Masukan Nama Lokasi Monitoring Kualitas Air :");

        final EditText groupNameField = new EditText(QualityDisplayActivity.this);
        groupNameField.setHint("Nama Lokasi");
        builder.setView(groupNameField);

        builder.setPositiveButton("Buat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String siteName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(siteName)) {
                    Toast.makeText(QualityDisplayActivity.this, "Tolong isi nama lokasi", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewSite(siteName);
                }
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewSite(final String siteName) {
        LokasiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && snapshot.hasChild(siteName)){
                    Toast.makeText(QualityDisplayActivity.this, "Nama Lokasi " + siteName + " Telah Ada", Toast.LENGTH_SHORT).show();
                    finish();
                    getIntent().addFlags(getIntent().FLAG_ACTIVITY_NO_HISTORY).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getIntent());
                }
                else {
                    String idactivity = "Quality";
                    Toast.makeText(QualityDisplayActivity.this, "Silahkan Pilih Lokasi", Toast.LENGTH_SHORT).show();
                    Intent GeoIntent = new Intent(QualityDisplayActivity.this, GeomapActivity.class);
                    GeoIntent.putExtra("lokasibaru", siteName);
                    GeoIntent.putExtra("idactivity", idactivity);
                    GeoIntent.putExtra("alatmonitoring", alatpilih);
                    startActivity(GeoIntent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void StatusDevice(){
        QualRef.child(alatpilih).child("status").child("current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String statuscurrent = snapshot.getValue().toString();
                if (statuscurrent.equals("Online")){
                    onlinestat.setVisibility(View.VISIBLE);
                    offlinestat.setVisibility(View.GONE);
                } else {
                    onlinestat.setVisibility(View.GONE);
                    offlinestat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        onlinestat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualityDisplayActivity.this);
                builder.setCancelable(true);
                builder.setTitle(Html.fromHtml("Set Status " + "<font color=blue><b>" + alatpilih + "</b></font>" + " Ke Mode Offline"));
                builder.setMessage(Html.fromHtml("Apakan anda ingin mengubah status alat ini <font color=blue><b>(" + alatpilih + ")</b></font> menjadi " + "<font color=red> <b>" + "Offline" + "</b></font>" + "?"));
                builder.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                QualRef.child(alatpilih).child("status").child("current").setValue("");
                            }
                        });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void getAllMax(){
        SharedPreferences spcallr = getApplicationContext().getSharedPreferences("MaxValueGauge", Context.MODE_PRIVATE);
        int realtds = spcallr.getInt("tds", 100);
        int realsuhu = spcallr.getInt("suhu", 100);
        int realturbi = spcallr.getInt("turbi", 100);
        int realorp = spcallr.getInt("orp", 100);
        int realph = spcallr.getInt("ph", 100);
        Log.d("realtds", Integer.toString(realtds));
        Log.d("realsuhu", Integer.toString(realsuhu));
        Log.d("realturbi", Integer.toString(realturbi));
        Log.d("realorp", Integer.toString(realorp));
        Log.d("realph", Integer.toString(realph));
        inmaxtds.setText(Integer.toString(realtds));
        inmaxsuhu.setText(Integer.toString(realsuhu));
        inmaxturbi.setText(Integer.toString(realturbi));
        inmaxorp.setText(Integer.toString(realorp));
        inmaxph.setText(Integer.toString(realph));
        tdsgauge.setEndValue(realtds);
        temperaturegauge.setEndValue(realsuhu);
        turbigauge.setEndValue(realturbi);
        orpgauge.setEndValue(realorp);
        phgauge.setEndValue(realph);
    }
}