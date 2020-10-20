package com.webclient.test.ui.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.webclient.test.Interfaces.RecyclerCallback;
import com.webclient.test.R;
import com.webclient.test.Services.VolleyService;

import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.MyViewHolder> {
    private List<String> mDataset;
    private RecyclerCallback _callback;

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
    public SearchRecyclerAdapter(List<String> myDataset, RecyclerCallback callback) {
        mDataset = myDataset;
        _callback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_searchrecycler, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.textView.setText(mDataset.get(0)[position]);
        final TextView cityName = holder.textView.findViewById(R.id.foundCity);
        final Button foundCityButton = holder.textView.findViewById(R.id.foundCityBtn);

        cityName.setText(mDataset.get(getItemCount()-1));
        foundCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(cityName.getText().toString(), "TEST");
                _callback.onClick(cityName.getText().toString());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}