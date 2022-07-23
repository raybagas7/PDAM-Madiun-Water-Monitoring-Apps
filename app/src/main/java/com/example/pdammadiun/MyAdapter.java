package com.example.pdammadiun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<Record> list;

    public MyAdapter(Context context, ArrayList<Record> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Record record = list.get(position);
        holder.date.setText(record.getDate());
        holder.time.setText(record.getTime());
        holder.flowrate.setText(record.getFlowrate() + " m^3/h");
        holder.fss.setText(record.getFss() + " m/s");
        holder.temperature.setText(record.getTemperature() + " °C");
        holder.velocity.setText(record.getVelocity() + " m/s");
        holder.pipa.setText(record.getPipa());
        holder.flowestimasi.setText(record.getFlowestimasi() + " m^3");
        holder.dynamicpressure.setText(record.getDynamicpressure() + " bar");
        holder.submitter.setText(record.getSubmitter());
        holder.flowrateliter.setText(record.getFlowrateliter() + " l/s");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, time, flowrate, fss, temperature, velocity, pipa, flowestimasi, dynamicpressure, submitter, flowrateliter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            flowrate = itemView.findViewById(R.id.flowrate);
            fss = itemView.findViewById(R.id.fss);
            temperature = itemView.findViewById(R.id.temperature);
            velocity = itemView.findViewById(R.id.velocity);
            pipa = itemView.findViewById(R.id.pipa);
            flowestimasi = itemView.findViewById(R.id.estimasipermonth);
            dynamicpressure = itemView.findViewById(R.id.estimasidynapress);
            submitter = itemView.findViewById(R.id.submitteremail);
            flowrateliter = itemView.findViewById(R.id.flowrateliter);
        }
    }
}
