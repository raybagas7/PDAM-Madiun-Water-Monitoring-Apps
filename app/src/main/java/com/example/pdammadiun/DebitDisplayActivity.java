package com.example.pdammadiun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import com.google.firebase.perf.metrics.Trace;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class DebitDisplayActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference DebitRef, LokasiRef, PipaRef, RootRef, KirimRef, GeoDebRef;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayListpipa = new ArrayList<>();
    private String alatpilih, getpipa, hasilestimasi, saveCurrentDate, saveCurrentTime, Sflow, lokasibaru, saveCurrentTimePlus, saveCurrentTimeFull, nextAvailableTime;
    private String countcondition, PdamUserEmail;
    private Spinner spinner, spinnerpipa;
    private TextView Nflow, Nvelo, Nfss, Ntemperaturedebit, Nestimasi, namalokasidebit, waktu, Nalamatlengkap, countTimer, Nflowliter;
    private TextView textrataflow, textratafss, textratasuhu, textratavelo, textrataflowliter, estimasibulanflow, textdynamicpressure;
    private ImageView hapuslokasi, tambahlokasi, recorddebit, setmaxflow, setmaxflowliter, setmaxvelo, setmaxfss, setmaxsuhudebit, penyegaran, startcount, measurementtype;
    private ImageView onlinestat, offlinestat;
    private EditText inmaxflow, inmaxvelo, inmaxfss, inmaxsuhudebit, inputrunningtime, inmaxflowliter;
    private int vmaxflow, vmaxflowliter, vmaxvelo, vmaxfss, vmaxsuhudebit, realflow, realflowliter, realvelo, realfss, realsuhudebit, countlenght = 1;
    private CustomGauge gaugeflow, gaugevelo, gaugefss, gaugesuhudebit, gaugeflowliter;
    private Button kirimsubmit;
    private Calendar calendar, calendarplus, maxday;
    private int hasilflow, hasilvelo, hasilfss, hasiltemperaturedebit, hasilflowliter, maksimalBulanIni;
    private float getcurrentflow, getcurrentfss, getcurrenttemperature, getcurrentvelocity, rataflow, ratavelo, ratafss, ratatemperaturebit;
    private float totalrataflow = 0, totalratafss = 0, totalratatemperature = 0, totalratavelo = 0, estimasiflow = 0, flowratemeasure = 0, rataflowliter = 0;
    private float floatflow, floatvelo, floatfss, floattemperature, counter1, stackdatain;
    private String stavgflow, stavgfss, stavgsuhu, stavgvelo, stestiflow, stavgflowliter, sdynamicpress;
    private SharedPreferences spmaxdebit;
    private float pressurevelo, squarevelo;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_display);

        mAuth = FirebaseAuth.getInstance();
        PdamUserEmail = mAuth.getCurrentUser().getEmail();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LokasiRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("LokasiDebit");
        PipaRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("JenisPipa");
        DebitRef = FirebaseDatabase.getInstance().getReference().child("MonitoringDebitQualityApp").child("MonitoringDebit");


        alatpilih = getIntent().getExtras().get("alatmonitoring").toString();

        calendar = Calendar.getInstance();
        calendarplus = Calendar.getInstance();
        maxday = Calendar.getInstance();
        calendarplus.add(Calendar.MINUTE, 65);

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        SimpleDateFormat currentTimeFull = new SimpleDateFormat("MMM dd, yyy hh:mm a");
        saveCurrentTimeFull = currentTimeFull.format(calendar.getTime());
        saveCurrentTimePlus = currentTimeFull.format(calendarplus.getTime());
        maksimalBulanIni = maxday.getActualMaximum(Calendar.DAY_OF_MONTH);
        String maxbulan = String.valueOf(maksimalBulanIni);

        Log.d("CurrentTime", saveCurrentTime);
        Log.d("CurrentTimePlus", saveCurrentTimePlus);
        Log.d("CurrentTimeFull", saveCurrentTimeFull);
        Log.d("CurrentMonthDays", maxbulan);

        spinner = findViewById(R.id.listlokasidebit);
        spinnerpipa = findViewById(R.id.spinnerpipa);

        showDataSpinner();
        showDataSpinnerPipa();

        Nflow = (TextView) findViewById(R.id.nilaiflow);
        Nflowliter = (TextView) findViewById(R.id.nilaiflowliter);
        Nvelo = (TextView) findViewById(R.id.nilaivelo);
        Nfss = (TextView) findViewById(R.id.nilaifss);
        Ntemperaturedebit = (TextView) findViewById(R.id.nilaitemperaturedebit);
        namalokasidebit = (TextView) findViewById(R.id.namalokasidebit);
        Nalamatlengkap = (TextView) findViewById(R.id.alamatlengkap);
        countTimer = (TextView) findViewById(R.id.countdowntimer);
        textrataflow = (TextView) findViewById(R.id.rataflowratetext);
        textrataflowliter = (TextView) findViewById(R.id.rataflowratelitertext);
        textratafss = (TextView) findViewById(R.id.ratafluidsoundspeedtext);
        textratasuhu = (TextView) findViewById(R.id.ratasuhutext);
        textratavelo = (TextView) findViewById(R.id.ratavelocitytext);
        estimasibulanflow = (TextView) findViewById(R.id.estimasi1mflow);
        textdynamicpressure = (TextView) findViewById(R.id.dynamicpressuure);
        /*hapuslokasi = (ImageView) findViewById(R.id.deletelocation);*/
        tambahlokasi = (ImageView) findViewById(R.id.tambahlokasibarudebit);
        recorddebit = (ImageView) findViewById(R.id.recorddebit);
        penyegaran = (ImageView) findViewById(R.id.penyegarandebit);
        startcount = (ImageView) findViewById(R.id.startcounting);
        onlinestat = (ImageView) findViewById(R.id.online_status);
        offlinestat = (ImageView) findViewById(R.id.offline_status);
        gaugeflow = (CustomGauge) findViewById(R.id.gaugeflow);
        gaugeflowliter = (CustomGauge) findViewById(R.id.gaugeflowliter);
        gaugefss = (CustomGauge) findViewById(R.id.gaugefss);
        gaugevelo = (CustomGauge) findViewById(R.id.gaugevelo);
        gaugesuhudebit = (CustomGauge) findViewById(R.id.gaugetemperaturedebit);
        kirimsubmit = (Button) findViewById(R.id.kirimdebit);
        setmaxflow = (ImageView) findViewById(R.id.setmaxflow);
        setmaxflowliter = (ImageView) findViewById(R.id.setmaxflowliter);
        setmaxvelo = (ImageView) findViewById(R.id.setmaxvelo);
        setmaxfss = (ImageView) findViewById(R.id.setmaxfss);
        setmaxsuhudebit = (ImageView) findViewById(R.id.setmaxsuhudebit);
        inmaxflow = (EditText) findViewById(R.id.textmaxflow);
        inmaxflowliter = (EditText) findViewById(R.id.textmaxflowliter);
        inmaxvelo = (EditText) findViewById(R.id.textmaxvelo);
        inmaxfss = (EditText) findViewById(R.id.textmaxfss);
        inputrunningtime = (EditText) findViewById(R.id.inputrunninghours);
        inmaxsuhudebit = (EditText) findViewById(R.id.textmaxsuhudebit);
        measurementtype = (ImageView) findViewById(R.id.measurement_but);
        dialog = new Dialog(this);
        spmaxdebit = getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);

        String hasilnamaalat = alatpilih;
        namalokasidebit.setText("Monitoring Debit Air " + hasilnamaalat);

        startcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputrunningtimeval = inputrunningtime.getText().toString();
                if (inputrunningtimeval.matches("")) {
                    Toast.makeText(DebitDisplayActivity.this, "Silahkan Masukan Counting Time", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    countlenght = Integer.parseInt(inputrunningtime.getText().toString());
                }
                countcondition = "yes";
                counter1 = -1;
                DebitRef.child(alatpilih).child("countinput").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (countcondition.equals("yes")) {
                            counter1++;
                            stackdatain = counter1;
                            Log.d("ChangeCountAfterButton", Float.toString(counter1));
                            totalrataflow = totalrataflow + getcurrentflow;
                            totalratafss = totalratafss + getcurrentfss;
                            totalratatemperature = totalratatemperature + getcurrenttemperature;
                            totalratavelo = totalratavelo + getcurrentvelocity;
                            //No NAN FLOW
                            if (totalrataflow > 0) {
                                rataflow = totalrataflow / counter1;
                            } else {
                                rataflow = 0;
                            }
                            //No NAN FSS
                            if (totalratafss > 0) {
                                ratafss = totalratafss / counter1;
                            } else {
                                ratafss = 0;
                            }
                            //No NAN Temperature
                            if (totalratatemperature > 0) {
                                ratatemperaturebit = totalratatemperature / counter1;
                            } else {
                                ratatemperaturebit = 0;
                            }
                            //No NAN VELOCITY
                            if (totalratavelo > 0) {
                                ratavelo = totalratavelo / counter1;
                            } else {
                                ratavelo = 0;
                            }
                            estimasiflow = (rataflow * 24) * maksimalBulanIni;
                            rataflowliter = ((rataflow*1000))/3600;
                            /*stavgflowliter.equals(new DecimalFormat("##.##").format(rataflowliter));*/
                            squarevelo = ratavelo * ratavelo;
                            pressurevelo = (((ratavelo * ratavelo) * 997)/2)/1000;
                            Log.d("ChangeCountRataRataFlow", Float.toString(totalrataflow));
                            Log.d("ChangeCountFlowDivide", Float.toString(rataflow));
                            Log.d("ChangeCountSquareVelo", Float.toString(squarevelo));
                            Log.d("ChangeCountVeloPressure", Float.toString(pressurevelo));
                            DebitRef.child(alatpilih).child("calculation").child("avgflowrate").setValue(new DecimalFormat("##.##").format(rataflow));
                            DebitRef.child(alatpilih).child("calculation").child("avgflowrateliter").setValue(new DecimalFormat("##.##").format(rataflowliter));
                            DebitRef.child(alatpilih).child("calculation").child("avgfss").setValue(new DecimalFormat("##.##").format(ratafss));
                            DebitRef.child(alatpilih).child("calculation").child("avgtemperature").setValue(new DecimalFormat("##.##").format(ratatemperaturebit));
                            DebitRef.child(alatpilih).child("calculation").child("avgvelo").setValue(new DecimalFormat("##.##").format(ratavelo));
                            DebitRef.child(alatpilih).child("calculation").child("estimasiflow").setValue(new DecimalFormat("##.##").format(estimasiflow));
                            DebitRef.child(alatpilih).child("calculation").child("estimasipressure").setValue(new DecimalFormat("##.##").format(pressurevelo));
                        } else {
                            counter1 = -1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //AmbilDataRealTime
                RetrieveDebitInfo();
                //Inisiasi durasi Timer
                long duration = TimeUnit.MINUTES.toMillis(countlenght);
                new CountDownTimer(duration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //GetRataOnTick
                        DebitRef.child(alatpilih).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                getcurrentflow = Float.parseFloat(snapshot.child("flowrate").getValue().toString());
                                getcurrentfss = Float.parseFloat(snapshot.child("fss").getValue().toString());
                                getcurrenttemperature = Float.parseFloat(snapshot.child("temperature").getValue().toString());
                                getcurrentvelocity = Float.parseFloat(snapshot.child("velocity").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Convert Milisecond ke menit dan detik
                        String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                                , TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        //Masukin String ke TextView
                        countTimer.setText(sDuration);
                        startcount.setVisibility(View.GONE);
                        inputrunningtime.setVisibility(View.GONE);
                        spinner.setEnabled(false);
                        spinnerpipa.setEnabled(false);
                        setmaxflow.setVisibility(View.GONE);
                        setmaxflowliter.setVisibility(View.GONE);
                        setmaxfss.setVisibility(View.GONE);
                        setmaxsuhudebit.setVisibility(View.GONE);
                        setmaxvelo.setVisibility(View.GONE);
                        tambahlokasi.setVisibility(View.GONE);
                        recorddebit.setVisibility(View.GONE);
                        kirimsubmit.setEnabled(false);
                        penyegaran.setVisibility(View.GONE);
                        inmaxflow.setEnabled(false);
                        inmaxflowliter.setEnabled(false);
                        inmaxfss.setEnabled(false);
                        inmaxvelo.setEnabled(false);
                        inmaxsuhudebit.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        Log.d("ChangeCountStackNumber", Float.toString(stackdatain));
                        Log.d("ChangeCountStackFlow", Float.toString(totalrataflow));
                        Log.d("ChangeCountFlowSDivide", Float.toString(rataflow));
                        countcondition = "no";
                        //Output Finish
                        Toast.makeText(DebitDisplayActivity.this, "Perhitungan Rata Rata Selesai", Toast.LENGTH_SHORT).show();
                        startcount.setVisibility(View.VISIBLE);
                        inputrunningtime.setVisibility(View.VISIBLE);
                        spinner.setEnabled(true);
                        spinnerpipa.setEnabled(true);
                        setmaxflow.setVisibility(View.VISIBLE);
                        setmaxflowliter.setVisibility(View.VISIBLE);
                        setmaxfss.setVisibility(View.VISIBLE);
                        setmaxsuhudebit.setVisibility(View.VISIBLE);
                        setmaxvelo.setVisibility(View.VISIBLE);
                        tambahlokasi.setVisibility(View.VISIBLE);
                        recorddebit.setVisibility(View.VISIBLE);
                        kirimsubmit.setEnabled(true);
                        penyegaran.setVisibility(View.VISIBLE);
                        inmaxflow.setEnabled(true);
                        inmaxflowliter.setEnabled(true);
                        inmaxfss.setEnabled(true);
                        inmaxvelo.setEnabled(true);
                        inmaxsuhudebit.setEnabled(true);
                        finish();
                        getIntent().addFlags(getIntent().FLAG_ACTIVITY_NO_HISTORY).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(getIntent());
                    }
                }.start();
            }
        });

        RetrieveDebitRataRata();
        RetrieveDebitInfo();
        getAllMax();
        changeMaxFlow();
        changeMaxFlowLiter();
        changeMaxVelo();
        changeMaxFss();
        changeMaxSuhuDebit();
        StatusDevice();

        tambahlokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestNewSite();
            }
        });

        recorddebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RecordIntent = new Intent(DebitDisplayActivity.this, RecordList.class);
                RecordIntent.putExtra("alatmonitoring", alatpilih);
                RecordIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RecordIntent);
            }
        });

        penyegaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ActivityDebitDisplay", "Refreshed");
                finish();
                startActivity(getIntent());
            }
        });

    }

    private void changeMaxFlow() {
        setmaxflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxflow = Integer.parseInt(inmaxflow.getText().toString());

                SharedPreferences.Editor editor = spmaxdebit.edit();

                editor.putInt("flow", vmaxflow);
                editor.commit();

                SharedPreferences spcalldebit = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
                realflow = spcalldebit.getInt("flow", 100);
                Log.d("realflowbut", Integer.toString(realflow));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxFlowLiter() {
        setmaxflowliter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxflowliter = Integer.parseInt(inmaxflowliter.getText().toString());

                SharedPreferences.Editor editor = spmaxdebit.edit();

                editor.putInt("flowliter", vmaxflowliter);
                editor.commit();

                SharedPreferences spcalldebit = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
                realflowliter = spcalldebit.getInt("flowliter", 100);
                Log.d("realflowliterbut", Integer.toString(realflowliter));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxVelo() {
        setmaxvelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxvelo = Integer.parseInt(inmaxvelo.getText().toString());

                SharedPreferences.Editor editor = spmaxdebit.edit();

                editor.putInt("velo", vmaxvelo);
                editor.commit();

                SharedPreferences spcalldebit = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
                realvelo = spcalldebit.getInt("velo", 100);
                Log.d("realvelobut", Integer.toString(realvelo));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxFss() {
        setmaxfss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxfss = Integer.parseInt(inmaxfss.getText().toString());

                SharedPreferences.Editor editor = spmaxdebit.edit();

                editor.putInt("fss", vmaxfss);
                editor.commit();

                SharedPreferences spcalldebit = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
                realfss = spcalldebit.getInt("fss", 100);
                Log.d("realfssbut", Integer.toString(realfss));

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void changeMaxSuhuDebit() {
        setmaxsuhudebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vmaxsuhudebit = Integer.parseInt(inmaxsuhudebit.getText().toString());

                SharedPreferences.Editor editor = spmaxdebit.edit();

                editor.putInt("suhudebit", vmaxsuhudebit);
                editor.commit();

                SharedPreferences spcalldebit = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
                realsuhudebit = spcalldebit.getInt("suhudebit", 100);
                Log.d("realsuhudebitbut", Integer.toString(realsuhudebit));

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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DebitDisplayActivity.this, R.layout.style_spinner, arrayList);
                spinner.setAdapter(arrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object location = parent.getItemAtPosition(position);
                        String ambillokasi = location.toString();
                        lokasibaru = ambillokasi;
                        String ambillokasirecord = location.toString();
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

                        /*hapuslokasi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DebitDisplayActivity.this);
                                builder.setCancelable(true);
                                builder.setTitle("Konfirmasi");
                                builder.setMessage(Html.fromHtml("<p style=\"color:red\"><b>" +
                                        "Apakan anda ingin menghapus lokasi data debit air pada lokasi " + ambillokasi +
                                        " ?" + "</p></b>"));
                                builder.setPositiveButton("Konfirmasi",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                LokasiRef.child(ambillokasi).removeValue();
                                                *//*Intent RestartIntent = new Intent(DebitDisplayActivity.this, MainActivity.class);
                                                RestartIntent.addFlags(getIntent().FLAG_ACTIVITY_NEW_TASK).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(RestartIntent);*//*
                                                *//*finish();
                                                getIntent().addFlags(getIntent().FLAG_ACTIVITY_NEW_TASK).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(getIntent());*//*
                                                Toast.makeText(DebitDisplayActivity.this, "Berhasil Terhapus", Toast.LENGTH_SHORT).show();
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
                        });*/

                        kirimsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(DebitDisplayActivity.this);
                                builder.setCancelable(true);
                                builder.setTitle("Konfirmasi Debit Air");
                                builder.setMessage(Html.fromHtml("Apakan anda ingin mengirimkan data debit air sekarang pada lokasi " + "<b>" + ambillokasi +
                                        "</b>" + "?" + "<p style=\"color:red\"><b>" + " Anda tidak dapat mengirim data kembali dalam kurun waktu 1 jam bila terkonfirmasi" + "</p></b>"));
                                builder.setPositiveButton("Konfirmasi",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference lokasisubmit = LokasiRef.child(ambillokasi).child("Submitted").push();
                                                String PushID = lokasisubmit.getKey();
                                                LokasiRef.child(ambillokasi).child("nextattempt").setValue(saveCurrentTimePlus);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("flowrate").setValue(stavgflow);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("flowrateliter").setValue(stavgflowliter);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("fss").setValue(stavgfss);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("pipa").setValue(getpipa);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("temperature").setValue(stavgsuhu);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("velocity").setValue(stavgvelo);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("flowestimasi").setValue(stestiflow);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("dynamicpressure").setValue(sdynamicpress);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("time").setValue(saveCurrentTime);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("date").setValue(saveCurrentDate);
                                                LokasiRef.child(ambillokasi).child("Submitted").child(PushID).child("submitter").setValue(PdamUserEmail);
                                                //ForLastAttempt
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("flowrate").setValue(stavgflow);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("flowrateliter").setValue(stavgflowliter);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("fss").setValue(stavgfss);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("pipa").setValue(getpipa);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("temperature").setValue(stavgsuhu);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("velocity").setValue(stavgvelo);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("flowestimasi").setValue(stestiflow);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("dynamicpressure").setValue(sdynamicpress);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("time").setValue(saveCurrentTime);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("date").setValue(saveCurrentDate);
                                                LokasiRef.child(ambillokasi).child("LastSubmit").child("submitter").setValue(PdamUserEmail);
                                                //SetTo0
                                                DebitRef.child(alatpilih).child("calculation").child("avgflowrate").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("avgflowrateliter").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("avgfss").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("avgtemperature").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("avgvelo").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("estimasiflow").setValue(0);
                                                DebitRef.child(alatpilih).child("calculation").child("estimasipressure").setValue(0);
                                                Toast.makeText(DebitDisplayActivity.this, "Berhasil Terkirim", Toast.LENGTH_SHORT).show();
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

    private void showDataSpinnerPipa() {
        PipaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListpipa.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayListpipa.add(item.child("nama").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DebitDisplayActivity.this, R.layout.style_spinner, arrayListpipa);
                spinnerpipa.setAdapter(arrayAdapter);

                spinnerpipa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object pipa = parent.getItemAtPosition(position);
                        getpipa = pipa.toString();
                        Log.d("Chosen pipa", getpipa);
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

    private void RetrieveDebitInfo() {
        Trace retrievedebitinfoTrace = FirebasePerformance.getInstance().newTrace("retrieve_debit_realtime_info");
        retrievedebitinfoTrace.start();
        DebitRef.child(alatpilih).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                floatflow = Float.parseFloat(snapshot.child("flowrate").getValue().toString());
                flowratemeasure = ((floatflow*1000))/3600;
                floatvelo = Float.parseFloat(snapshot.child("velocity").getValue().toString());
                floatfss = Float.parseFloat(snapshot.child("fss").getValue().toString());
                floattemperature = Float.parseFloat(snapshot.child("temperature").getValue().toString());
                hasilflow = Math.round(floatflow);
                hasilflowliter = Math.round(flowratemeasure);
                hasilvelo = Math.round(floatvelo);
                hasilfss = Math.round(floatfss);

                hasiltemperaturedebit = Math.round(floattemperature);
                Sflow = Float.toString(floatflow);


                Nflow.setText(new DecimalFormat("##.##").format(floatflow) + "\nm^3/h");
                Nflowliter.setText(new DecimalFormat("##.##").format(flowratemeasure) + "\nliter/s");
                Nvelo.setText(new DecimalFormat("##.##").format(floatvelo) + "\nm/s");
                Nfss.setText(new DecimalFormat("##.##").format(floatfss) + "\nm/s");
                Ntemperaturedebit.setText(new DecimalFormat("##.##").format(floattemperature) + " °C");
                gaugeflow.setValue(hasilflow);
                gaugeflowliter.setValue(hasilflowliter);
                gaugevelo.setValue(hasilvelo);
                gaugefss.setValue(hasilfss);
                gaugesuhudebit.setValue(hasiltemperaturedebit);

                Log.d("alatpilih", alatpilih);
                Log.d("FlowSting", Sflow);

                measurementtype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.setContentView(R.layout.measure_layout_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        ImageView closedialog = dialog.findViewById(R.id.closedialog);
                        Button btnkubik = dialog.findViewById(R.id.kubik);
                        Button btnliter = dialog.findViewById(R.id.liter);

                        closedialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        btnkubik.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmaxflowliter.setVisibility(View.GONE);
                                Nflowliter.setVisibility(View.INVISIBLE);
                                gaugeflowliter.setVisibility(View.GONE);
                                inmaxflowliter.setVisibility(View.GONE);
                                setmaxflow.setVisibility(View.VISIBLE);
                                gaugeflow.setVisibility(View.VISIBLE);
                                Nflow.setVisibility(View.VISIBLE);
                                inmaxflow.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                Toast.makeText(DebitDisplayActivity.this, "Satuan menjasi m^3/h", Toast.LENGTH_SHORT).show();

                            }
                        });

                        btnliter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setmaxflow.setVisibility(View.GONE);
                                Nflow.setVisibility(View.INVISIBLE);
                                gaugeflow.setVisibility(View.GONE);
                                inmaxflow.setVisibility(View.GONE);
                                setmaxflowliter.setVisibility(View.VISIBLE);
                                gaugeflowliter.setVisibility(View.VISIBLE);
                                inmaxflowliter.setVisibility(View.VISIBLE);
                                Nflowliter.setVisibility(View.VISIBLE);
                                /*Nflowliter.setText(new DecimalFormat("##.##").format(flowratemeasure) + "\nl/s");*/
                                dialog.dismiss();
                                Toast.makeText(DebitDisplayActivity.this, "Satuan menjasi liter/s", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DebitDisplayActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();

            }
        });
        retrievedebitinfoTrace.stop();
    }

    private void RetrieveDebitRataRata() {
        Trace retrieverataratainfoTrace = FirebasePerformance.getInstance().newTrace("retrieve_debit_ratarata_info");
        retrieverataratainfoTrace.start();
        DebitRef.child(alatpilih).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && snapshot.hasChild("calculation")
                        && snapshot.hasChild("countinput") && snapshot.hasChild("flowrate")
                        && snapshot.hasChild("fss") && snapshot.hasChild("temperature")
                        && snapshot.hasChild("velocity") && snapshot.child("calculation").hasChild("avgflowrate")
                        && snapshot.child("calculation").hasChild("avgflowrateliter") && snapshot.child("calculation").hasChild("avgfss")
                        && snapshot.child("calculation").hasChild("avgtemperature") && snapshot.child("calculation").hasChild("avgvelo")
                        && snapshot.child("calculation").hasChild("estimasiflow") && snapshot.child("calculation").hasChild("estimasipressure")) {
                    DebitRef.child(alatpilih).child("calculation").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            stavgflow = snapshot.child("avgflowrate").getValue().toString();
                            stavgflowliter = snapshot.child("avgflowrateliter").getValue().toString();
                            stavgfss = snapshot.child("avgfss").getValue().toString();
                            stavgsuhu = snapshot.child("avgtemperature").getValue().toString();
                            stavgvelo = snapshot.child("avgvelo").getValue().toString();
                            stestiflow = snapshot.child("estimasiflow").getValue().toString();
                            sdynamicpress = snapshot.child("estimasipressure").getValue().toString();

                            textrataflow.setText(stavgflow + " m^3/h");
                            textrataflowliter.setText(stavgflowliter + " liter/s");
                            textratafss.setText(stavgfss + " m/s");
                            textratasuhu.setText(stavgsuhu + " °C");
                            textratavelo.setText(stavgvelo + " m/s");
                            estimasibulanflow.setText(stestiflow + " m^3");
                            textdynamicpressure.setText(sdynamicpress + " bar");
                            /*estimasibulanflow.setText(new DecimalFormat("##.##").format(estiflow));*/

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    DebitRef.child(alatpilih).child("flowrate").setValue(0);
                    DebitRef.child(alatpilih).child("countinput").setValue(0);
                    DebitRef.child(alatpilih).child("velocity").setValue(0);
                    DebitRef.child(alatpilih).child("fss").setValue(0);
                    DebitRef.child(alatpilih).child("temperature").setValue(0);
                    DebitRef.child(alatpilih).child("calculation").child("avgflowrate").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("avgflowrateliter").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("avgfss").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("avgtemperature").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("avgvelo").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("estimasiflow").setValue("0");
                    DebitRef.child(alatpilih).child("calculation").child("estimasipressure").setValue("0");

                    finish();
                    getIntent().addFlags(getIntent().FLAG_ACTIVITY_NO_HISTORY).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getIntent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        retrieverataratainfoTrace.stop();

    }

    private void RequestNewSite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DebitDisplayActivity.this);
        builder.setTitle("Masukan Nama Lokasi Monitoring Debit Air :");

        final EditText groupNameField = new EditText(DebitDisplayActivity.this);
        groupNameField.setHint("Nama Lokasi");
        builder.setView(groupNameField);

        builder.setPositiveButton("Buat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String siteName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(siteName)) {
                    Toast.makeText(DebitDisplayActivity.this, "Tolong isi nama lokasi", Toast.LENGTH_SHORT).show();
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
                if ((snapshot.exists()) && snapshot.hasChild(siteName)) {
                    Toast.makeText(DebitDisplayActivity.this, "Nama Lokasi Telah Ada", Toast.LENGTH_SHORT).show();
                    finish();
                    getIntent().addFlags(getIntent().FLAG_ACTIVITY_NO_HISTORY).addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getIntent());
                } else {
                    String idactivity = "Debit";
                    Toast.makeText(DebitDisplayActivity.this, "Silahkan Pilih Lokasi", Toast.LENGTH_SHORT).show();
                    Intent GeoIntent = new Intent(DebitDisplayActivity.this, GeomapActivity.class);
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
        DebitRef.child(alatpilih).child("status").child("current").addValueEventListener(new ValueEventListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(DebitDisplayActivity.this);
                builder.setCancelable(true);
                builder.setTitle(Html.fromHtml("Set Status " + "<font color=blue><b>" + alatpilih + "</b></font>" + " Ke Mode Offline"));
                builder.setMessage(Html.fromHtml("Apakan anda ingin mengubah status alat ini <font color=blue><b>(" + alatpilih + ")</b></font> menjadi " + "<font color=red> <b>" + "Offline" + "</b></font>" + "?"));
                builder.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DebitRef.child(alatpilih).child("status").child("current").setValue("");
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

    private void getAllMax() {
        SharedPreferences spcallrd = getApplicationContext().getSharedPreferences("MaxValueGaugeDebit", Context.MODE_PRIVATE);
        int realflow = spcallrd.getInt("flow", 100);
        int realflowliter = spcallrd.getInt("flowliter", 100);
        int realvelo = spcallrd.getInt("velo", 100);
        int realfss = spcallrd.getInt("fss", 100);
        int realsuhudebit = spcallrd.getInt("suhudebit", 100);
        Log.d("realflow", Integer.toString(realflow));
        Log.d("realflowliter", Integer.toString(realflowliter));
        Log.d("realvelo", Integer.toString(realvelo));
        Log.d("realfss", Integer.toString(realfss));
        Log.d("realshubudebit", Integer.toString(realsuhudebit));
        inmaxflow.setText(Integer.toString(realflow));
        inmaxflowliter.setText(Integer.toString(realflowliter));
        inmaxvelo.setText(Integer.toString(realvelo));
        inmaxfss.setText(Integer.toString(realfss));
        inmaxsuhudebit.setText(Integer.toString(realsuhudebit));
        gaugeflow.setEndValue(realflow);
        gaugeflowliter.setEndValue(realflowliter);
        gaugevelo.setEndValue(realvelo);
        gaugefss.setEndValue(realfss);
        gaugesuhudebit.setEndValue(realsuhudebit);
    }
}