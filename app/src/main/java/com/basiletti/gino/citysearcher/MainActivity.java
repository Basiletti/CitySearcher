package com.basiletti.gino.citysearcher;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.basiletti.gino.citysearcher.enums.FragmentType;
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
        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("searchFragment");
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, searchFragment, "searchFragment").addToBackStack("searchFragment").commit();
    }

    public void displayMapFragment(CityObject cityObject) {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(cityObject);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, mapFragment, "mapFragment").addToBackStack("mapFragment").commit();
    }

    private void findViews() {
        fragmentContainer = findViewById(R.id.fragmentContainer);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("mapFragment") != null) {
            getSupportFragmentManager().popBackStack("mapFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
