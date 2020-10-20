package com.webclient.test.ui.main;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.webclient.test.Interfaces.WeatherRecyclerCallback;
import com.webclient.test.R;

import java.util.HashMap;
import java.util.List;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.MyViewHolder> {
    private List<String[]> mDataset;
    private WeatherRecyclerCallback _callback;
    private String[] directions = new String[]{
            "Север", "Северо-восток", "Восток", "Юго-восток", "Юг", "Юго-запад", "Запад", "Северо-запад"
    };
    private SparseArray<String> dirs = new SparseArray<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewGroup textView;
        public MyViewHolder(ViewGroup v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WeatherRecyclerAdapter(List<String[]> myDataset, WeatherRecyclerCallback callback) {
        _callback = callback;
        mDataset = myDataset;
        sparseInit();
    }
    private void sparseInit(){
        for(int i = 0; i < directions.length; i++){
            dirs.put(i, directions[i]);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WeatherRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_weatherrecycler, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.textView.setText(mDataset.get(0)[position]);
        TextView cityName = holder.textView.findViewById(R.id.cityName);
        TextView cityTemp = holder.textView.findViewById(R.id.cityTemp);
        TextView cityWind = holder.textView.findViewById(R.id.cityWind);
        TextView cityWindDir = holder.textView.findViewById(R.id.cityWindDir);
        Button deleteBtn = holder.itemView.findViewById(R.id.deleteBtn);
        cityName.setText(mDataset.get(position)[0]);
        cityTemp.setText(mDataset.get(position)[1]);
        cityWind.setText(mDataset.get(position)[2]);
        cityWindDir.setText(calcDir(mDataset.get(position)[3]));
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _callback.onDelete(position);
            }
        });

    }
    private String calcDir(String deg){
        return dirs.get(Math.round(Float.parseFloat(deg) / 45));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}