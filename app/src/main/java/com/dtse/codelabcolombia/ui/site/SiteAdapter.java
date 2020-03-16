package com.dtse.codelabcolombia.ui.site;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtse.codelabcolombia.R;
import com.huawei.hms.site.api.model.Site;

import java.util.ArrayList;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder> {
    private ArrayList<Site> list = new  ArrayList<Site>();
    private ISiteAdapter listener;

    SiteAdapter(ISiteAdapter listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_site, parent, false);

        return new ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText( "Name :" + list.get(position).getName());
        holder.address.setText("Address :" +list.get(position).getFormatAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClicked(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.adress);
        }

    }

    public void setList(ArrayList<Site> list){
        this.list = list;
        notifyDataSetChanged();
    }

    interface ISiteAdapter {
        void itemClicked(Site site);
    }
}
