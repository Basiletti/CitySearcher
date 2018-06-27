package com.basiletti.gino.citysearcher;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import com.basiletti.gino.citysearcher.objects.CityObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class DataQualityTest {

    @Test
    public void checkJSONfileContents() {
        ArrayList<CityObject> cities = new ArrayList<>();

        try {
            ClassLoader classLoader = getClass().getClassLoader();

            InputStream in_s = classLoader.getResourceAsStream("assets/cities.json");

            JsonParser parser = new JsonFactory().createParser(in_s);
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonToken token = parser.nextToken();

            while (!JsonToken.START_ARRAY.equals(token) && token != null && !JsonToken.START_OBJECT.equals(token)) { // Try find at least one object or array.
                parser.nextToken();
            }
            if (token == null) { // No content found
                fail();
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
                assertThat(cityObjectExists(node), is(true));
                assertThat(goodCityName(node), is(true));
                assertThat(goodCountrycode(node), is(true));
                assertThat(goodObjectID(node), is(true));
                assertThat(goodCoordinateObject(node), is(true));

                cities.add(node);
                scanMore = true;
            }


            Log.d("TAG", "count of cities in file = " + cities.size());
            assertThat(cities.size(), is(209557));
            assertThat(checkCityCount(cities, "Cardiff", null, 2), is(true));
            assertThat(checkCityCount(cities, "Swansea", "GB", 1), is(true));

        } catch (Exception e) {
            Log.d("TAG", "Exception e : " + e.toString());
        }
    }


    public boolean cityObjectExists(CityObject cityObject) {
        return cityObject != null;
    }

    public boolean goodCityName(CityObject cityObject) {
        return (cityObject.getCityName() != null && !cityObject.getCityName().trim().isEmpty());
    }

    public boolean goodCountrycode(CityObject cityObject) {
        return (cityObject.getCountry() != null && !cityObject.getCountry().trim().isEmpty());
    }

    public boolean goodObjectID(CityObject cityObject) {
        return (cityObject.get_id() != null && !cityObject.get_id().trim().isEmpty());
    }

    public boolean goodCoordinateObject(CityObject cityObject) {
        return (cityObject.getCoordinateObject() != null);
    }



    public boolean checkCityCount(ArrayList<CityObject> cityList, String cityName, String countryCode, int countExpected) {
        int count = 0;
        for (CityObject city : cityList) {
            if (city.getCityName().equals(cityName) && (countryCode == null || countryCode.equals(city.getCountry()))) count ++;
        }
        return count == countExpected;
    }



    public String getStringFromFile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
            Log.d("TAG", "sb: " + sb);
        }
        reader.close();
        return sb.toString();
    }


}
