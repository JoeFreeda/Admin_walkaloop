package com.example.admin_walkaloop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin_walkaloop.ui.GeneralData;
import com.example.admin_walkaloop.ui.MainActivity;
import com.example.admin_walkaloop.R;
import com.example.admin_walkaloop.ui.activity_map;
import com.example.admin_walkaloop.model.Response;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Place_adapter extends RecyclerView.Adapter<Place_adapter.ViewHolder> {
    List<Response> lis_response;
    Context context;

    public Place_adapter(List<Response> lis_response, MainActivity mainActivity) {
        this.lis_response = lis_response;
        this.context = mainActivity;
    }

    @NonNull
    @Override
    public Place_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.inflate_placelist, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Place_adapter.ViewHolder holder, int position) {
            Response response = lis_response.get(position);
            holder.tv_name.setText(response.getpName());
            holder.tv_markercount.setText("No of Places Marked : "+response.getNoofPlaces());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(context, activity_map.class);
                    GeneralData.response = response;
                    context.startActivity(in);
                }
            });
    }

    @Override
    public int getItemCount() {
        return lis_response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_markercount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_markercount=itemView.findViewById(R.id.tv_markercount);
        }
    }
}
