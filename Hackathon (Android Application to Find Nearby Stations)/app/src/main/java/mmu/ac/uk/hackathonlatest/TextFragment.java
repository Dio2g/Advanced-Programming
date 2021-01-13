package mmu.ac.uk.hackathonlatest;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.List;

public class TextFragment extends Fragment {
    private static View view;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_text, container, false); //set view to fragment text layout
        view.setBackgroundColor(Color.parseColor("#858585")); //set background color to light grey
        return view;
    }

    public static void populateTable(List<Station> s, Double lat, Double lng) {
        Activity activity = (Activity)view.getContext();
        float[] results = new float[1]; //results for distance between
        String nameString;
        String distString;
        TableLayout resultsTable = view.findViewById(R.id.resultsTable);
        resultsTable.removeAllViews(); //remove previous results if any

        for (Station i : s) { //loop through each returned station
            TableRow resultsTableRow = new TableRow(activity); //new row
            TextView nameTextView = new TextView(activity); //new text view
            TextView distTextView = new TextView(activity);
            nameString = ("    "+ i.getName());
            nameTextView.setText(nameString); //set text to name of station
            nameTextView.setTextSize(18.0f);
            Location.distanceBetween(lat, lng, i.getLat(), i.getLng(),results); //calc distance between
            distString = ("  "+String.valueOf(Math.round(results[0]))+"m Away");
            distTextView.setText(distString); //set text to distance between
            distTextView.setTextSize(18.0f);
            resultsTable.addView(resultsTableRow); //add row to table
            resultsTableRow.addView(nameTextView);
            resultsTableRow.addView(distTextView);
            distTextView.setGravity(Gravity.END);
        }
    }
}
