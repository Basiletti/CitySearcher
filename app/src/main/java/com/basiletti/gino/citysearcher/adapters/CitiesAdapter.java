package com.basiletti.gino.citysearcher.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.basiletti.gino.citysearcher.R;
import com.basiletti.gino.citysearcher.objects.CityObject;

import java.util.ArrayList;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesAdapterViewHolder> {
    final private Context mContext;
    private ArrayList<CityObject> cities;

    public CitiesAdapter(Context context, ArrayList<CityObject> cities) {
        mContext = context;
        this.cities = cities;
    }

    public void replaceData(ArrayList<CityObject> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    @Override
    public CitiesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item_layout, parent, false);
        final CitiesAdapterViewHolder holder = new CitiesAdapterViewHolder(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(final CitiesAdapterViewHolder holder, int position) {

        holder.cityName.setText(cities.get(holder.getAdapterPosition()).getCityName() + ", " + cities.get(holder.getAdapterPosition()).getCountry());

    }

    @Override
    public void onViewAttachedToWindow(@NonNull CitiesAdapterViewHolder holder) {
        holder.setIsRecyclable(true);
        super.onViewAttachedToWindow(holder);
    }



    @Override
    public int getItemCount() {
        return cities.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("TAG", "onDetachedFromRecyclerView");
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public class CitiesAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;


        public CitiesAdapterViewHolder(View view) {
            super(view);
            cityName = view.findViewById(R.id.cityName);
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

