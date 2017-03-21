package com.example.lab2.mortgagecalculator;

import android.app.Fragment;
import android.content.res.Configuration;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private FloatingActionButton edit;
    private FloatingActionButton delete;
    private FloatingActionButton add;
    private MarkerOptions options = new MarkerOptions();

    // VIKAS
    private List<Property> properties;
    private Marker m;


    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    // VIKAS
    private static final String TAG_CURRENT_PROPERTY = "currProperty";

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
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        edit = (FloatingActionButton) rootView.findViewById(R.id.fab_edit);
        delete = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        delete.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        properties = MainActivity.db.getProperties();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment fragment = null;
//                Class  fragmentClass = PropertyFragment.class;
                ((MainActivity)getActivity()).selectDrawerItem(MainActivity.nvDrawer.getMenu().getItem(0), null);
//                try {
//                    fragment = (Fragment) fragmentClass.newInstance();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if(fragment !=null) {
//                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_RETAINED_FRAGMENT).commit();
//                }
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(inflater));

                //disable directions on marker selection
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                // For dropping a marker at a point on the Map
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Property property : properties) {
                    options.position(property.getLatlng());
                    googleMap.addMarker(options).setTag(property);
                    builder.include(property.getLatlng());
                }
                if(properties.size() > 0) {
                    LatLngBounds bounds = builder.build();
                    // For zooming automatically to the location of the marker
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
                    googleMap.animateCamera(cu);
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        m = marker;
                        add.setVisibility(View.GONE);
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("delete: " + MainActivity.db.deleteProperty(((Property)m.getTag()).getId()));
                                properties.remove(m.getTag());
                                delete.setVisibility(View.GONE);
                                m.remove();
                            }
                        });

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                    Fragment fragment = null;
//                                    Class  fragmentClass = PropertyFragment.class;
//
//                                    try {
//                                        fragment = (Fragment) fragmentClass.newInstance();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable(TAG_CURRENT_PROPERTY, (Property)m.getTag());
//                                    fragment.setArguments(bundle);
//                                    if(fragment !=null) {
//                                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TAG_RETAINED_FRAGMENT).commit();
//                                    }
                                ((MainActivity)getActivity()).selectDrawerItem(MainActivity.nvDrawer.getMenu().getItem(0), (Property)m.getTag());
                            }
                        });
                        return false;
                    }
                });

                googleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                    @Override
                    public void onInfoWindowClose(Marker marker) {
                        //hide or do something you want
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.GONE);
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

            return myContentsView;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
