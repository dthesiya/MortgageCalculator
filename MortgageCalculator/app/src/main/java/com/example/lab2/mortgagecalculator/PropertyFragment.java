package com.example.lab2.mortgagecalculator;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link PropertyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PropertyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyFragment extends Fragment {

    public PropertyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PropertyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PropertyFragment newInstance() {
        return new PropertyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property, container, false);
        TextView tv = (TextView) view.findViewById(R.id.textView2);
        tv.setText("Hello World");
        return view;
    }

}
