package com.webclient.test.ui.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.webclient.test.Interfaces.RecyclerCallback;
import com.webclient.test.Interfaces.WeatherCallback;
import com.webclient.test.MainActivity;
import com.webclient.test.R;
import com.webclient.test.Services.VolleyService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private VolleyService serv;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private List<String> dataSet = new ArrayList<>();
    public static SearchFragment newInstance(int index) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

    }
    public void saveData(String cityName){
        editor = sharedPref.edit();
        editor.putString(cityName, cityName);
        editor.apply();
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        serv = new VolleyService(getContext());
        recyclerView = (RecyclerView) root.findViewById(R.id.citySearch_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)

        mAdapter = new SearchRecyclerAdapter(dataSet, new RecyclerCallback() {
            @Override
            public void onClick(String cityName) {
                //saveData(dataSet.get(mAdapter.getItemCount()-1));
                saveData(cityName);
                //dataSet.remove(cityName);
                //mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Город успешно добавлен", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mAdapter);


        SearchView search = root.findViewById(R.id.citySearch);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                Log.d("TEST SEARCH", newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("TEST SEARCH submit", query);
                serv.weatherRequestByCityName(query, new WeatherCallback() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try{
                            dataSet.add(obj.getString("name"));

                            mAdapter.notifyItemInserted(mAdapter.getItemCount());
                            //mAdapter.notifyDataSetChanged();

                        }catch (Exception e){
                            Log.d("Exception", e.toString());
                        }
                    }
                    @Override
                    public void onError(){
                        //dataSet.clear();
                        //mAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Город не найден", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

        });


        root.findViewById(R.id.citySearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView searchBar = v.findViewById(R.id.citySearch);
                searchBar.setIconified(false);
            }
        });
        return root;
    }
    @Override
    public void onPause() {
        dataSet.clear();
        mAdapter.notifyDataSetChanged();
        super.onPause();
    }
}
