package com.basiletti.gino.citysearcher;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.basiletti.gino.citysearcher.objects.CityObject;

public class MainActivity extends AppCompatActivity {
    FrameLayout fragmentContainer;
    ImageView cancelIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        loadSearchFragment();


    }

    private void loadSearchFragment() {
        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.search_fragment));
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, searchFragment, getString(R.string.search_fragment)).addToBackStack(getString(R.string.search_fragment)).commit();
    }

    public void displayMapFragment(CityObject cityObject) {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.map_fragment));
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(cityObject);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, mapFragment, getString(R.string.map_fragment)).addToBackStack(getString(R.string.map_fragment)).commit();
    }

    private void findViews() {
        fragmentContainer = findViewById(R.id.fragmentContainer);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(getString(R.string.map_fragment)) != null) {
            getSupportFragmentManager().popBackStack(getString(R.string.map_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

        }
    }
}
