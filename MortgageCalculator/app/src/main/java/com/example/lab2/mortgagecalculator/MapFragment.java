package com.example.lab2.mortgagecalculator;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lab2.mortgagecalculator.daos.Property;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    public ArrayList<Property> properties;
    private static MapFragment this_fragment;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        if(this_fragment == null){
            this_fragment = new MapFragment();
        }
        return this_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        fetchData();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(inflater));
                // For dropping a marker at a point on the Map
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Property property : properties) {
                    options.position(property.getLatlng());
                    googleMap.addMarker(options).setTag(property);
                    builder.include(property.getLatlng());
                }
                LatLngBounds bounds = builder.build();
                // For zooming automatically to the location of the marker
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                googleMap.animateCamera(cu);
            }
        });

        return rootView;
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(LayoutInflater inflater){
            myContentsView = inflater.inflate(R.layout.custom_info_layout, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            final Property property = (Property)marker.getTag();
            TextView type = ((TextView)myContentsView.findViewById(R.id.typeVal));
            type.setText(property.getType().toString());

            TextView address = ((TextView)myContentsView.findViewById(R.id.addressVal));
            address.setText(property.getAddress());

            TextView city = ((TextView)myContentsView.findViewById(R.id.cityVal));
            city.setText(property.getCity());

            TextView loan_amt = ((TextView)myContentsView.findViewById(R.id.loanAmntVal));
            loan_amt.setText(String.valueOf(property.getLoan_amt()));

            TextView apr = ((TextView)myContentsView.findViewById(R.id.aprVal));
            apr.setText(String.valueOf(property.getApr()));

            TextView monthly_pay = ((TextView)myContentsView.findViewById(R.id.monthlyPayVal));
            monthly_pay.setText(String.valueOf(property.getDown_pay()));

            Button delete =(Button) myContentsView.findViewById(R.id.deletBtn);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("delete button clicked");
                    properties.remove(property);
                }
            });

            return myContentsView;
        }
    }

    public void fetchData(){

        properties = new ArrayList<>();
        Property p1 = new Property();
        p1.setType("House");
        p1.setAddress("201 S 4th St");
        p1.setCity("San Jose");
        p1.setLoan_amt(12459590.00);
        p1.setApr(123.00);
        p1.setDown_pay(1500.00);
        p1.setLatlng(new LatLng(37.333326, -121.884589));


        Property p2 = new Property();
        p2.setType("TownHouse");
        p2.setAddress("101 S 4th St");
        p2.setCity("San Jose");
        p2.setLoan_amt(9459590.00);
        p2.setApr(112.00);
        p2.setDown_pay(1450.00);
        p2.setLatlng(new LatLng(37.335808, -121.886038));

        Property p3 = new Property();
        p3.setType("Condo");
        p3.setAddress("33rd S 3rd St");
        p3.setCity("San Jose");
        p3.setLoan_amt(11459590.00);
        p3.setApr(118.00);
        p3.setDown_pay(1475.00);
        p3.setLatlng(new LatLng(37.335819, -121.887735));

        properties.add(p1);
        properties.add(p2);
        properties.add(p3);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
