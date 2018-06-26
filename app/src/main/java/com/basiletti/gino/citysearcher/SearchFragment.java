package com.basiletti.gino.citysearcher;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basiletti.gino.citysearcher.adapters.CitiesAdapter;
import com.basiletti.gino.citysearcher.objects.CityObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class SearchFragment extends Fragment {
    private ArrayList<CityObject> cities;
    private CitiesAdapter citiesAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ImageView mCancelIV;
    RecyclerView mLocationsRV;
    ProgressBar mLoadingPB;
    LinearLayout mSearchLL;
    EditText mSearchET;
    TextView mLoadingTV;
    Activity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mActivity = getActivity();

        findViews(view);
        setupAdapter();
        loadJsonAsync();

        mCancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "size of tree = " + cities.size(), Toast.LENGTH_LONG).show();
            }
        });


        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return view;
    }

    private void setupAdapter() {
        citiesAdapter = new CitiesAdapter(getActivity(), new ArrayList<CityObject>());
        mLocationsRV.setAdapter(citiesAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLocationsRV.setLayoutManager(mLinearLayoutManager);
    }

    private void findViews(View view) {
        mCancelIV = view.findViewById(R.id.cancelIV);
        mLocationsRV = view.findViewById(R.id.locationsRV);
        mLoadingPB = view.findViewById(R.id.loadingPB);
        mSearchLL = view.findViewById(R.id.searchLL);
        mLoadingTV = view.findViewById(R.id.loadingTV);
        mSearchET = view.findViewById(R.id.searchET);
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


    private static class ProcessJsonTask extends AsyncTask<Void, Void, ArrayList<CityObject>> {
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
        protected ArrayList<CityObject> /*TreeMap<String, ArrayList<CityObject>>*/ doInBackground(Void... params) {
            ArrayList<CityObject> cities = new ArrayList<>();

            try {
                AssetManager assetManager = activityWeakReference.get().getAssets();
                InputStream is = assetManager.open("cities.json");

                JsonParser parser = new JsonFactory().createParser(is);
                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JsonToken token = parser.nextToken();

                // Try find at least one object or array.
                while (!JsonToken.START_ARRAY.equals(token) && token != null && !JsonToken.START_OBJECT.equals(token)) {
                    parser.nextToken();
                }

                // No content found
                if (token == null) {
                    return null;
                }

                boolean scanMore = false;

                while (true) {
                    // If the first token is the start of object ->
                    // the response contains only one object (no array)
                    // do not try to get the first object from array.
                    try {
                        if (!JsonToken.START_OBJECT.equals(token) || scanMore) {
                            token = parser.nextToken();
                        }
                        if (!JsonToken.START_OBJECT.equals(token)) {
                            break;
                        }

                        CityObject node = mapper.readValue(parser, CityObject.class);
                        cities.add(node);
                        Log.d("TAG", "node content = " + node.toString());

                        scanMore = true;


                    } catch (JsonParseException e) {
                        Log.d("TAG", "error = " + e.toString());
                    }
                }


//            ArrayList<CityObject> jsonData = loadJsonAsync(activityWeakReference.get());
                //            if (jsonData != null) {
//                Collections.sort(jsonData, new Comparator<CityObject>() {
//                    @Override
//                    public int compare(final CityObject city1, final CityObject city2) {
//                        return city1.getCityName().compareTo(city2.getCityName());
//                    }
//                });
//            }

           /* HashMap<String, ArrayList<CityObject>> citiesHashMap = new HashMap<>();
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
            */

//           return jsonData;
            } catch (Exception e) {
                Log.d("TAG", "exception =  " + e.toString());
            }
            return cities;

        }

        @Override
        protected void onPostExecute(ArrayList<CityObject> citiesList) {
            Toast.makeText(activityWeakReference.get(), "onPostExecute", Toast.LENGTH_LONG).show();
            if (citiesList != null) {
                progressBar.get().setVisibility(View.GONE);
                loadingTV.get().setVisibility(View.GONE);
                searchLL.get().setVisibility(View.VISIBLE);

                searchFragmentWeakReference.get().cities = citiesList;
                searchFragmentWeakReference.get().citiesAdapter.replaceData(citiesList);
                Toast.makeText(activityWeakReference.get(), "all done. Size of unique city names = " + citiesList.size(), Toast.LENGTH_LONG).show();
            }
        }

    }



}


