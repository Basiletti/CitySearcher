package com.basiletti.gino.citysearcher;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basiletti.gino.citysearcher.adapters.CitiesAdapter;
import com.basiletti.gino.citysearcher.objects.CityObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


public class SearchFragment extends Fragment {
    private TreeMap<String, ArrayList<CityObject>> treeMapCities;
    private CitiesAdapter citiesAdapter;
    private boolean citiesPopulated = false;
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
        setupListeners();


        return view;
    }

    private void setupListeners() {
        mCancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchET.setText("");
                if (citiesPopulated) {
                    filterList(null);
                }
            }
        });


        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mSearchET.getText().toString().trim().length() > 0) {
                    mSearchET.setTypeface(null, Typeface.NORMAL);
                } else {
                    mSearchET.setTypeface(null, Typeface.ITALIC);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (citiesPopulated) {
                    String searchText = mSearchET.getText().toString();
                    if (searchText.length() > 0) {
                        filterList(searchText);
                    }
                }
            }
        });

    }

    private void filterList(String searchCriteria) {
        ArrayList<CityObject> searchedCities = new ArrayList<>();

        for (Map.Entry<String, ArrayList<CityObject>> e : treeMapCities.entrySet()) {
            if (searchCriteria == null || e.getKey().contains(searchCriteria)) {
                searchedCities.addAll(e.getValue());

            }
        }
        Collections.sort(searchedCities);
        citiesAdapter.replaceData(searchedCities);
    }


    private void setupAdapter() {
        citiesAdapter = new CitiesAdapter(getActivity(), new ArrayList<CityObject>());
        mLocationsRV.setAdapter(citiesAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLocationsRV.setLayoutManager(mLinearLayoutManager);
    }

    private void loadJsonAsync() {
        new ProcessJsonTask(mActivity, mLoadingPB, mSearchLL, mLoadingTV, this).execute();
    }


    private void findViews(View view) {
        mCancelIV = view.findViewById(R.id.cancelIV);
        mLocationsRV = view.findViewById(R.id.locationsRV);
        mLoadingPB = view.findViewById(R.id.loadingPB);
        mSearchLL = view.findViewById(R.id.searchLL);
        mLoadingTV = view.findViewById(R.id.loadingTV);
        mSearchET = view.findViewById(R.id.searchET);
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


        private ArrayList<CityObject> processJSONdata() {
            ArrayList<CityObject> cities = new ArrayList<>();
            try {
                AssetManager assetManager = activityWeakReference.get().getAssets();
                InputStream is = assetManager.open("cities.json");

                JsonParser parser = new JsonFactory().createParser(is);
                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JsonToken token = parser.nextToken();

                while (!JsonToken.START_ARRAY.equals(token) && token != null && !JsonToken.START_OBJECT.equals(token)) { // Try find at least one object or array.
                    parser.nextToken();
                }

                if (token == null) { // No content found
                    return null;
                }

                boolean scanMore = false;

                while (true) {
                    if (!JsonToken.START_OBJECT.equals(token) || scanMore) {
                        token = parser.nextToken();
                    }
                    if (!JsonToken.START_OBJECT.equals(token)) {
                        break;
                    }

                    CityObject node = mapper.readValue(parser, CityObject.class);
                    cities.add(node);
                    scanMore = true;
                }

            } catch (Exception e) {
                Log.d("TAG", "exception =  " + e.toString());
            }
            return cities;
        }

        private TreeMap<String, ArrayList<CityObject>> convertToTreeMap(ArrayList<CityObject> citiesArray) {
            TreeMap<String, ArrayList<CityObject>> citiesTreeMap = new TreeMap<>();

            for (CityObject city : citiesArray) {
                if (citiesTreeMap.containsKey(city.getCityName())) {
                    citiesTreeMap.get(city.getCityName()).add(city);

                } else {
                    ArrayList<CityObject> newList = new ArrayList<>();
                    newList.add(city);
                    citiesTreeMap.put(city.getCityName(), newList);
                }
            }

            return citiesTreeMap;
        }

        @Override
        protected void onPreExecute() {
            progressBar.get().setVisibility(View.VISIBLE);
        }


        @Override
        protected TreeMap<String, ArrayList<CityObject>> doInBackground(Void... params) {
            ArrayList<CityObject> cities = processJSONdata();
            assert cities != null;
            return convertToTreeMap(cities);
        }

        @Override
        protected void onPostExecute(TreeMap<String, ArrayList<CityObject>> citiesList) {
            if (citiesList != null) {
                progressBar.get().setVisibility(View.GONE);
                loadingTV.get().setVisibility(View.GONE);
                searchLL.get().setVisibility(View.VISIBLE);

                searchFragmentWeakReference.get().treeMapCities = citiesList;
                searchFragmentWeakReference.get().filterList(null);
                searchFragmentWeakReference.get().citiesPopulated = true;
            }
        }

    }


}


