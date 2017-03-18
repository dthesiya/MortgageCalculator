package com.example.lab2.mortgagecalculator;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.plus.PlusOneButton;

public class PropertyFragment extends Fragment {

    private static String[] states_array;
    private boolean isProperty = false, isLoan = false;
    private static final String TAG_CURRENT_PROPERTY = "currProperty";

    public PropertyFragment() {
    }

    public static PropertyFragment newInstance() {
        return new PropertyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        states_array = getResources().getStringArray(R.array.states_array);

        Bundle bundle = getArguments();
        if(bundle != null){
            Property property= (Property) bundle.getSerializable(TAG_CURRENT_PROPERTY);
            System.out.println(property.getCity());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property, container, false);


        TextView property = (TextView) view.findViewById(R.id.property_text);
        final LinearLayout property_layout = (LinearLayout) view.findViewById(R.id.property_layout);
        property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isProperty){
                    collapse(property_layout);
                }else{
                    expand(property_layout);
                }
                isProperty = !isProperty;
            }
        });

        TextView loan = (TextView) view.findViewById(R.id.loan_text);
        final LinearLayout loan_layout = (LinearLayout) view.findViewById(R.id.loan_layout);
        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoan){
                    collapse(loan_layout);
                }else{
                    expand(loan_layout);
                }
                isLoan = !isLoan;
            }
        });


        final Spinner states = (Spinner) view.findViewById(R.id.spinner1);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, states_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        states.setAdapter(spinnerArrayAdapter);
        return view;
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
