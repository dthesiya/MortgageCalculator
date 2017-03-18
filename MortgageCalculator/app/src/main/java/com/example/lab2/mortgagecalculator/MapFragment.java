package com.example.lab2.mortgagecalculator;

import android.app.Fragment;

import android.app.FragmentManager;
import android.content.res.Configuration;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private FloatingActionButton edit;
    private FloatingActionButton delete;
    private FloatingActionButton add;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Property> properties;
    private boolean isDelete = false;
    private Marker m;

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    private static final String TAG_CURRENT_PROPERTY = "currProperty";
    private FragmentManager fragmentManager;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentManager = getFragmentManager();
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        edit = (FloatingActionButton) rootView.findViewById(R.id.fab_edit);
        delete = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        delete.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        fetchData();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("add clicked");
                Fragment fragment = null;
                Class  fragmentClass = PropertyFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(fragment !=null) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_RETAINED_FRAGMENT).commit();
                }
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
//                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = properties.get(0).getLatlng();
                for (Property property : properties) {
                    options.position(property.getLatlng());
                    googleMap.addMarker(options).setTag(property);
                }
                // googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(inflater));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        System.out.println("marker is clicked");
                            m = marker;
                            add.setVisibility(View.INVISIBLE);
                            delete.setVisibility(View.VISIBLE);
                            edit.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("delete is clicked");
                                    properties.remove(m.getTag());
                                    delete.setVisibility(View.INVISIBLE);
                                    m.remove();
                                }
                            });

                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("Edit clicked");
                                    Fragment fragment = null;
                                    Class  fragmentClass = PropertyFragment.class;

                                    try {
                                        fragment = (Fragment) fragmentClass.newInstance();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(TAG_CURRENT_PROPERTY, (Property)m.getTag());
                                    fragment.setArguments(bundle);
                                    if(fragment !=null) {
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_RETAINED_FRAGMENT).commit();
                                    }
                                }
                            });
                        return false;
                    }
                });

                googleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                    @Override
                    public void onInfoWindowClose(Marker marker) {
                        //hide or do something you want
                        delete.setVisibility(View.INVISIBLE);
                        edit.setVisibility(View.INVISIBLE);
                        add.setVisibility(View.VISIBLE);
                        m = null;
                    }
                });
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
            Property property = (Property)marker.getTag();
            TextView type = ((TextView)myContentsView.findViewById(R.id.typeVal));
            type.setText(property.getType());

            TextView address = ((TextView)myContentsView.findViewById(R.id.addressVal));
            address.setText(property.getAddress());

            TextView city = ((TextView)myContentsView.findViewById(R.id.cityVal));
            city.setText(property.getCity());

            TextView loan_amt = ((TextView)myContentsView.findViewById(R.id.loanAmntVal));
            loan_amt.setText(String.valueOf(property.getLoan_amt()));

            TextView apr = ((TextView)myContentsView.findViewById(R.id.aprVal));
            apr.setText(String.valueOf(property.getApr()));

            TextView monthly_pay = ((TextView)myContentsView.findViewById(R.id.monthlyPayVal));
            monthly_pay.setText(String.valueOf(property.getMonthly_pay()));

            return myContentsView;
        }
    }

    public void fetchData(){

        properties = new ArrayList<>();
        Property p1 = new Property();
        p1.setType("Appartment");
        p1.setAddress("201 S 4th St");
        p1.setCity("San Jose");
        p1.setLoan_amt(12459590.00);
        p1.setApr(123.00);
        p1.setMonthly_pay(1500.00);
        p1.setLatlng(new LatLng(37.333326, -121.884589));


        Property p2 = new Property();
        p2.setType("Appartment");
        p2.setAddress("101 S 4th St");
        p2.setCity("San Jose");
        p2.setLoan_amt(9459590.00);
        p2.setApr(112.00);
        p2.setMonthly_pay(1450.00);
        p2.setLatlng(new LatLng(37.335808, -121.886038));

        Property p3 = new Property();
        p3.setType("Appartment");
        p3.setAddress("33rd S 3rd St");
        p3.setCity("San Jose");
        p3.setLoan_amt(11459590.00);
        p3.setApr(118.00);
        p3.setMonthly_pay(1475.00);
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
