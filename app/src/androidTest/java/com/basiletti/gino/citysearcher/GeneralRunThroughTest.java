package com.basiletti.gino.citysearcher;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.File;
import java.net.URL;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class GeneralRunThroughTest  {


    @Test
    @UiThread
    public void myTest() {

        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.basiletti.gino.citysearcher", appContext.getPackageName());


    }

    @Test
    public void fileObjectShouldNotBeNull() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("/cities.json");
        assertThat(resource, notNullValue());
        File file = new File(resource.getPath());
        assertThat(file, notNullValue());
    }




}
