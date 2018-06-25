package com.basiletti.gino.citysearcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, searchFragment, "searchFragment").addToBackStack("searchFragment").commit();
    }

    private void findViews() {
        fragmentContainer = findViewById(R.id.fragmentContainer);
    }

}
