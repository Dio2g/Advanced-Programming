package mmu.ac.uk.hackathonlatest;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.collection.LongSparseArray;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment {
    private static MapView mapView;
    private static MapboxMap map;
    private static View view;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_token));
        view = inflater.inflate(R.layout.fragment_map, container, false); //set view to layout of map fragment
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() { //set style to DARK
                    @Override
                    public void onStyleLoaded(@NonNull Style style) { //when style is loaded custom markers are added
                        Resources res = Objects.requireNonNull(getActivity()).getResources();
                        Drawable usermarker = ResourcesCompat.getDrawable(res, R.drawable.usermarker, null); //create Drawable os custom marker (bmp)
                        Drawable stationmarker = ResourcesCompat.getDrawable(res, R.drawable.stationmarker, null);
                        assert usermarker != null;
                        style.addImage("usermarker", usermarker); //add custom marker to style
                        assert stationmarker != null;
                        style.addImage("stationmarker", stationmarker); //add custom marker to style
                    }
                });


            }
        });


        return view;
    }



    public static void populateMap(List<Station> r, Double lat, Double lng){
        float[] results = new float[1];
        final Activity activity = (Activity)view.getContext();
        SymbolManager sm = new SymbolManager(mapView, map, Objects.requireNonNull(map.getStyle()));

        List<Symbol> symbols = new ArrayList<>();
        LongSparseArray<Symbol> symbolArray = sm.getAnnotations();
        for (int i = 0; i < symbolArray.size(); i++) { //loop through each symbol
            symbols.add(symbolArray.valueAt(i)); //add to symbols array
        }
        sm.delete(symbols); //delete all symbols

        if(!r.isEmpty()) {
            map.setCameraPosition( //set camera position at users location
                    new CameraPosition.Builder()
                            .target(new LatLng(lat, lng))
                            .zoom(9.0)
                            .build()
            );
        }

        SymbolOptions symbolOptionsUser = new SymbolOptions()
                .withLatLng(new LatLng(lat, lng))
                .withIconImage("usermarker")
                .withIconSize(0.90f)
                .withTextField("You")
                .withTextOpacity(0f);
        Symbol symbolUser = sm.create(symbolOptionsUser); //create marker at users location


        for (Station i: r) {//loop through each station
            Location.distanceBetween(lat, lng, i.getLat(), i.getLng(),results);
            SymbolOptions symbolOptionsStation = new SymbolOptions()
                    .withLatLng(new LatLng(i.getLat(), i.getLng()))
                    .withIconImage("stationmarker")
                    .withIconSize(0.80f)
                    .withTextField(i.getName()+" " + String.valueOf(Math.round(results[0]))+"m Away") //this is the string that the toast will display as the symbol is passed to the onclick handler
                    .withTextOpacity(0f);
            Symbol symbol = sm.create(symbolOptionsStation); //create marker for each station
        }
        sm.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Toast.makeText(activity, symbol.getTextField().toString(), Toast.LENGTH_SHORT).show(); //show toast when station marker is clicked
            }
        });
    }
}
