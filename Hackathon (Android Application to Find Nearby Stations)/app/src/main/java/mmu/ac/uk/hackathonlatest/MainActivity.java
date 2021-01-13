package mmu.ac.uk.hackathonlatest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private double lat;
    private double lng;
    private URL url;
    private ToggleButton mapToggleButton;
    private Boolean mapStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#363636")); //set bg color to dark grey

        final MapFragment map = new MapFragment(); //create new map fragment
        final TextFragment text = new TextFragment(); //create new text fragment

        getSupportFragmentManager().beginTransaction().add(R.id.container, text).commit(); //sow text fragment

        mapToggleButton = findViewById(R.id.toggleMapButton);
        mapToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //toggle button listener
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) //if toggle is on
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, map); //replace current fragment with map fragment
                    transaction.addToBackStack(null); //add previous fragment back to stack
                    transaction.commit();
                    mapStatus = true; //map is currently displayed so change map status to true
                }
                else //if toggle is off
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, text); //replace current fragment with text fragment
                    transaction.addToBackStack(null); //add previous fragment back to stack
                    transaction.commit();
                    mapStatus = false; //map is currently not displayed so change map status to false
                }

            }
        });

        String[] requiredPermissions = { //builds array of require permissions
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        boolean ok = true;

        for (int i = 0; i<requiredPermissions.length; i++){ //checks each permission to see if its been granted
            int result = ActivityCompat.checkSelfPermission(this,requiredPermissions[i]);
            if(result != PackageManager.PERMISSION_GRANTED){
                ok = false;
            }
        }

        if(!ok){  //if permissions not granted
            ActivityCompat.requestPermissions(this, requiredPermissions, 1);
            System.exit(0);
        }else{ //if permissions granted
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) { //on location changed get lat and lng
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }


    public void searchButton_onClick(View v){
        try {
            url = new URL("http://10.0.2.2:8080/stations?lat=" + lat + "&lng=" + lng); //build URL
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("debug", String.valueOf(url));
        new serverClientAndDisplay().execute(url); //execute Async task
    }

    private void displayResults(List<Station> station){//displays results as text or map box
        if(mapStatus){ //if map is active populate map
            MapFragment.populateMap(station, lat, lng);
        }
        if(!mapStatus){ //if text fragment is active populate text fragment
            TextFragment.populateTable(station, lat, lng);
        }
    }

    public class serverClientAndDisplay extends AsyncTask<URL, Void, List<Station>> { //Async task
        @Override
        protected List<Station> doInBackground(URL... urls){//call ServerClient in background
            return new ServerClient().getStationsFromURL(urls[0]);
        }
        @Override
        protected void onPostExecute(List<Station> s){
            displayResults(s);
        } //On post execute display results of http request
    }
}
