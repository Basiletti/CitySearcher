package com.basiletti.gino.citysearcher;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basiletti.gino.citysearcher.objects.CityObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class SearchFragment extends Fragment {
    private TreeMap<String, ArrayList<CityObject>> cities;
    ImageView mCancelIV;
    RecyclerView mLocationsRV;
    ProgressBar mLoadingPB;
    LinearLayout mSearchLL;
    TextView mLoadingTV;
    Activity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mActivity = getActivity();

        findViews(view);
        loadJsonAsync();

        mCancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "size of tree = " + cities.size(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


    private void findViews(View view) {
        mCancelIV = view.findViewById(R.id.cancelIV);
        mLocationsRV = view.findViewById(R.id.locationsRV);
        mLoadingPB = view.findViewById(R.id.loadingPB);
        mSearchLL = view.findViewById(R.id.searchLL);
        mLoadingTV = view.findViewById(R.id.loadingTV);
    }

    private void loadJsonAsync() {
        new ProcessJsonTask(mActivity, mLoadingPB, mSearchLL, mLoadingTV, this).execute();
    }

    private static ArrayList<CityObject> loadJsonAsync(Activity activity) {
        try {
            AssetManager assetManager = activity.getAssets();
            InputStream is = assetManager.open("cities.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Gson gson = new Gson();

            return gson.fromJson(bufferedReader, new TypeToken<ArrayList<CityObject>>(){}.getType());

        } catch (Exception e) {
            Log.d("TAG", "error = " + e.toString());
        }
        return null;
    }


    private static class ProcessJsonTask extends AsyncTask<Void, Void, TreeMap<String, ArrayList<CityObject>>> {
        //https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
        //AsyncTask declared as static following the above stackoverflow post to prevent missing it in the garbage collection.
        //This approach using Async task / making it static isn't how i would normally approach a problem like this, More recently I have moved onto kotlin where you can just call things off of the UI thread
        //(see here: https://antonioleiva.com/anko-background-kotlin-android/ )
        private WeakReference<Activity> activityWeakReference;
        private WeakReference<ProgressBar> progressBar;
        private WeakReference<LinearLayout> searchLL;
        private WeakReference<TextView> loadingTV;
        private WeakReference<SearchFragment> searchFragmentWeakReference;

        ProcessJsonTask(Activity activity, ProgressBar progressBar, LinearLayout searchLL, TextView loadingTV, SearchFragment searchFragment) {
            activityWeakReference = new WeakReference<>(activity);
            this.progressBar = new WeakReference<>(progressBar);
            this.searchLL = new WeakReference<>(searchLL);
            this.loadingTV = new WeakReference<>(loadingTV);
            this.searchFragmentWeakReference = new WeakReference<>(searchFragment);
        }

        @Override
        protected void onPreExecute() {
            progressBar.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected TreeMap<String, ArrayList<CityObject>> doInBackground(Void... params) {
            // Runs on the background thread
            ArrayList<CityObject> jsonData = loadJsonAsync(activityWeakReference.get());
            HashMap<String, ArrayList<CityObject>> citiesHashMap = new HashMap<>();
            TreeMap<String, ArrayList<CityObject>> cityTreeMap = new TreeMap<>();

            if (jsonData != null) {
                for (CityObject city : jsonData) {
                    if (!citiesHashMap.containsKey(city.getCityName())) {
                        ArrayList<CityObject> objects = new ArrayList<>();
                        objects.add(city);
                        citiesHashMap.put(city.getCityName(), objects);

                    } else {
                        citiesHashMap.get(city.getCityName()).add(city);
                        Log.d("TAG", "city already found = " + city.getCityName() + " - array size after adding another city to the list = " + citiesHashMap.get(city.getCityName()).size());
                    }
                }

                cityTreeMap.putAll(citiesHashMap);
                for (Map.Entry<String, ArrayList<CityObject>> entry : cityTreeMap.entrySet()) {
                    Log.d("TAG", "sorted Key = " + entry.getKey());
                }

            }
            return cityTreeMap;
        }

        @Override
        protected void onPostExecute(TreeMap<String, ArrayList<CityObject>> hashMap) {
            progressBar.get().setVisibility(View.GONE);
            loadingTV.get().setVisibility(View.GONE);
            searchLL.get().setVisibility(View.VISIBLE);

            searchFragmentWeakReference.get().cities = hashMap;
            Toast.makeText(activityWeakReference.get(), "all done. Size of unique city names = " + hashMap.size(), Toast.LENGTH_LONG).show();

        }

    }



}


