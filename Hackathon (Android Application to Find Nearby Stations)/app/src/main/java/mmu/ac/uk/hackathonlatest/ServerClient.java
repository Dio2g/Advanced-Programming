package mmu.ac.uk.hackathonlatest;

import android.text.SpannableString;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerClient {

    public List<Station> getStationsFromURL(URL u){//parsing JSON
        List<Station> stationsFromURL = new ArrayList<Station>();
        try {
            HttpURLConnection tc = (HttpURLConnection) u.openConnection(); //create http connection
            InputStreamReader isr = new InputStreamReader(tc.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            String line;
            while((line = in.readLine()) != null){
                JSONArray ja = new JSONArray(line); //create JSON array for each line
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jo = (JSONObject) ja.get(i); //create JSON object for each station
                    Log.e("debug", jo.getString("StationName"));
                    stationsFromURL.add(new Station(jo.getString("StationName"), Double.parseDouble(jo.getString("Latitude")), Double.parseDouble(jo.getString("Longitude")))); //add new station object to list for each station parsed
                }
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stationsFromURL; //return list of station objects
    }

}
