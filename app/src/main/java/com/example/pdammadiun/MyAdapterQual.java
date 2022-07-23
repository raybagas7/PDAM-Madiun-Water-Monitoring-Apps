package com.example.pdammadiun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterQual extends RecyclerView.Adapter<MyAdapterQual.MyViewHolder> {

    Context contextqual;

    ArrayList<RecordQual> listqual;

    public MyAdapterQual(Context contextqual, ArrayList<RecordQual> listqual) {
        this.contextqual = contextqual;
        this.listqual = listqual;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v2 = LayoutInflater.from(contextqual).inflate(R.layout.itemqual, parent, false);
        return new MyViewHolder(v2);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RecordQual recordQual = listqual.get(position);
        holder.orp.setText(recordQual.getOrp() + " mV");
        holder.ph.setText(recordQual.getPh() + " pH");
        holder.sumur.setText(recordQual.getSumur());
        holder.tds.setText(recordQual.getTds() + " ppm");
        holder.temperature.setText(recordQual.getTemperature() + " °C");
        holder.turbidity.setText(recordQual.getTurbidity() + " NTU");
        holder.date.setText(recordQual.getDate());
        holder.time.setText(recordQual.getTime());
        holder.klorin.setText(recordQual.getKlorin() + " PPM");
        holder.submitter.setText(recordQual.getSubmitter());

    }

    @Override
    public int getItemCount() {
        return listqual.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orp, ph, sumur, tds, temperature, turbidity, date, time, klorin, submitter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orp = itemView.findViewById(R.id.orprecord);
            ph = itemView.findViewById(R.id.phrecord);
            sumur = itemView.findViewById(R.id.sumurrecord);
            tds = itemView.findViewById(R.id.tdsrecord);
            temperature = itemView.findViewById(R.id.temperaturequalrecord);
            turbidity = itemView.findViewById(R.id.turbidityrecord);
            date = itemView.findViewById(R.id.datequal);
            time = itemView.findViewById(R.id.timequal);
            klorin = itemView.findViewById(R.id.klorinrecord);
            submitter = itemView.findViewById(R.id.submitterqual);
        }
    }
}
