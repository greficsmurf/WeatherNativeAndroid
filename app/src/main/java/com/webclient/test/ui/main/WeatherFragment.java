package com.webclient.test.ui.main;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.webclient.test.Interfaces.WeatherCallback;
import com.webclient.test.Interfaces.WeatherRecyclerCallback;
import com.webclient.test.MainActivity;
import com.webclient.test.R;
import com.webclient.test.Services.VolleyService;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.loader.app.LoaderManager.getInstance;

/**
 * A placeholder fragment containing a simple view.
 */
public class WeatherFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String[]>  dataSet = new ArrayList<>();
    private boolean isFirstLoad = true;
    private VolleyService serv;
    private String[] CityNames = new String[]{"London", "Ufa", "Moscow", "Berlin"};

    private RequestQueue requestQueue;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public static WeatherFragment newInstance(int index) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    }
    public void deleteData(String cityName){
        editor = sharedPref.edit();
        editor.remove(cityName);
        editor.apply();
    }
    private void refreshData(){
        dataSet.clear();
        mAdapter.notifyDataSetChanged();
        initLoad();
    }
    private void initLoad(){
        Context context = getActivity();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> data = sharedPref.getAll();
        Log.d("all", data.toString());
        boolean lock = false;
        for(String city : data.keySet()){
            serv.weatherRequestByCityName(city, new WeatherCallback() {
                @Override
                public void onResponse(JSONObject obj) {
                    String cityName;
                    String cityTemp;
                    String cityWind;
                    String cityWindDir;
                        //Log.d("onResponse", obj.getString("name"));
                        try {
                            cityName = obj.getString("name");
                        }catch (Exception e){
                            cityName = "Город";
                        }
                        try {
                            cityTemp = obj.getJSONObject("main").getString("temp");
                        }catch (Exception e){
                            cityTemp = "Нет данных о температуре";
                        }
                        try {
                            cityWind = obj.getJSONObject("wind").getString("speed");
                        }catch (Exception e){
                            cityWind = "Нет данных о скорости ветра";
                        }
                        try {
                            cityWindDir = obj.getJSONObject("wind").getString("deg");
                        }catch (Exception e){
                            cityWindDir = "0";
                        }
                        dataSet.add(new String[]{cityName, cityTemp + getString(R.string.degrees), cityWind + getString(R.string.speed), cityWindDir});

                        mAdapter.notifyItemInserted(mAdapter.getItemCount());

                        //mAdapter.notifyDataSetChanged();

                }
                @Override
                public void onError(){
                    Log.d("ERROR", " while fetching init data");
                }
            });

        }


    }
    @Override
    public void onResume() {
        if(!isFirstLoad) {
            refreshData();
        }
        else{
            isFirstLoad = false;
        }
        super.onResume();
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        serv = new VolleyService(getContext());

        View root = inflater.inflate(R.layout.fragment_weather, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_weather);
        initLoad();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)

        mAdapter = new WeatherRecyclerAdapter(dataSet, new WeatherRecyclerCallback() {
            @Override
            public void onDelete(int position) {

                deleteData(dataSet.get(position)[0]);
                dataSet.remove(position);
                mAdapter.notifyDataSetChanged();
                //refreshData();
            }
        });
        recyclerView.setAdapter(mAdapter);
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.weatherRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });



        return root;
    }


}