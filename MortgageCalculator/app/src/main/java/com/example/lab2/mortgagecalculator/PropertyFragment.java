package com.example.lab2.mortgagecalculator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab2.mortgagecalculator.daos.Property;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class PropertyFragment extends Fragment {

    private static String[] states_array;
    private boolean isProperty = false, isLoan = false, isCalculated = false;

    private Property property;


    // VIKAS
    private static final String TAG_CURRENT_PROPERTY = "currProperty";

    public PropertyFragment() {
    }

    public static PropertyFragment newInstance() {
        return new PropertyFragment();
    }

    public static PropertyFragment newInstance(Property property) {
        PropertyFragment this_fragment = new PropertyFragment();
        this_fragment.property = property;
        return this_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        states_array = getResources().getStringArray(R.array.states_array);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_property, container, false);

        final TextView result = (TextView) view.findViewById(R.id.monthly_pay);
        result.setVisibility(View.GONE);

        //fill property details in case of edit property/loan
        if(property != null){
            fillPropertyDetails(view, property);
            isCalculated = true;
        }

        TextView property_txtview = (TextView) view.findViewById(R.id.property_text);
        final LinearLayout property_layout = (LinearLayout) view.findViewById(R.id.property_layout);
        property_txtview.setOnClickListener(new View.OnClickListener() {
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

        TextView loan_txtview = (TextView) view.findViewById(R.id.loan_text);
        final LinearLayout loan_layout = (LinearLayout) view.findViewById(R.id.loan_layout);
        loan_txtview.setOnClickListener(new View.OnClickListener() {
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

        ((Button) view.findViewById(R.id.new_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Do you really want to reset?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                clearPropertyDetails(view);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setVisibility(View.GONE);
            }
        });

        ((Button) view.findViewById(R.id.calculate_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Property temp = getLoanDetails(view, property);
                if(temp != null){
                    property = temp;
                    double princ = property.getLoan_amt() - property.getDown_pay();
                    if(princ < 0){
                        Toast.makeText(getActivity(), "Please fill higher loan amount value than down payment", Toast.LENGTH_SHORT).show();
                    }else{
                        double apr = property.getApr() / 1200;
                        int terms = property.getTerm() * 12;
                        double monthly_payment = Math.round(((apr * princ) * 1000 / (1 - Math.pow((1 + apr), (-1 * terms))))) / 1000;
                        result.setText("Monthly Payment: " + monthly_payment);
                        property.setResult(monthly_payment);
                        result.setVisibility(View.VISIBLE);
                    }
                }else{
                    result.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Please fill all the Loan details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) view.findViewById(R.id.save_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Property temp = getPropertyDetails(view, property);
                if(temp != null){
                    property = temp;
                    GeoCoder geoCoder = new GeoCoder();
                    geoCoder.setContext(getContext());
                    try {
                        LatLng latLong = geoCoder.execute(property).get();
                        property.setLatlng(latLong);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    if(property.getId() > 0){
                        MainActivity.db.updateProperty(property);
                    }else{
                        MainActivity.db.insertProperty(property);
                    }
                    clearPropertyDetails(view);
                    Toast.makeText(getActivity(), "Property Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public static Property getLoanDetails(View view, Property property){
        if(property == null){
            property = new Property();
        }

        String loan_amt = ((EditText) view.findViewById(R.id.editText8)).getText().toString();
        String down_pay = ((EditText) view.findViewById(R.id.editText9)).getText().toString();
        String apr = ((EditText) view.findViewById(R.id.editText10)).getText().toString();

        if(loan_amt.trim().isEmpty() || down_pay.trim().isEmpty() || apr.trim().isEmpty()){
            return null;
        }
        property.setLoan_amt(Double.valueOf(loan_amt));
        property.setDown_pay(Double.valueOf(down_pay));
        property.setApr(Double.valueOf(apr));

        RadioGroup rg_term = (RadioGroup) view.findViewById(R.id.radioGroup2);
        int radioTermID = rg_term.getCheckedRadioButtonId();
        if(radioTermID < 0){
            return null;
        }
        property.setTerm(radioTermID == R.id.radioButton5 ? 15 : 30);
        return property;
    }

    public static Property getPropertyDetails(View view, Property property){
        if(property == null){
            property = new Property();
        }
        RadioGroup rg_type = (RadioGroup)view.findViewById(R.id.radioGroup1);
        int radioTypeID = rg_type.getCheckedRadioButtonId();
        if(radioTypeID < 0){
            return null;
        }
        RadioButton radioType = (RadioButton) rg_type.findViewById(radioTypeID);
        property.setType(radioType.getText().toString());

        String address = ((EditText) view.findViewById(R.id.editText1)).getText().toString();
        String city = ((EditText) view.findViewById(R.id.editText2)).getText().toString();
        String zipcode = ((EditText) view.findViewById(R.id.editText4)).getText().toString();

        if(address.trim().isEmpty() || city.trim().isEmpty() || zipcode.trim().isEmpty()){
            return null;
        }

        property.setAddress(address);
        property.setCity(city);
        property.setZipcode(zipcode);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
        property.setState(states_array[spinner.getSelectedItemPosition()]);

        property = getLoanDetails(view, property);

        return property;
    }

    public static void fillPropertyDetails(View view, Property property){
        if("House".equals(property.getType())){
            RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton1);
            rb.setChecked(true);
        }else if("Townhouse".equals(property.getType())){
            RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton2);
            rb.setChecked(true);
        }else if("Condo".equals(property.getType())){
            RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton3);
            rb.setChecked(true);
        }
        ((EditText) view.findViewById(R.id.editText1)).setText(property.getAddress());
        ((EditText) view.findViewById(R.id.editText2)).setText(property.getCity());
        ((EditText) view.findViewById(R.id.editText4)).setText(property.getZipcode());
        int state_index = Arrays.binarySearch(states_array, property.getState());
        if(state_index >= 0){
            ((Spinner) view.findViewById(R.id.spinner1)).setSelection(state_index);
        }

        ((EditText) view.findViewById(R.id.editText8)).setText(String.valueOf(property.getLoan_amt()));
        ((EditText) view.findViewById(R.id.editText9)).setText(String.valueOf(property.getDown_pay()));
        ((EditText) view.findViewById(R.id.editText10)).setText(String.valueOf(property.getApr()));

        if(property.getTerm() == 15){
            RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton5);
            rb.setChecked(true);
        }else{
            RadioButton rb = (RadioButton) view.findViewById(R.id.radioButton6);
            rb.setChecked(true);
        }
    }

    public static void clearPropertyDetails(View view){
        RadioGroup rg_type = (RadioGroup)view.findViewById(R.id.radioGroup1);
        int radioTypeID = rg_type.getCheckedRadioButtonId();
        if(radioTypeID >= 0){
            ((RadioButton) rg_type.findViewById(radioTypeID)).setChecked(false);
        }

        ((EditText) view.findViewById(R.id.editText1)).setText("");
        ((EditText) view.findViewById(R.id.editText2)).setText("");
        ((EditText) view.findViewById(R.id.editText4)).setText("");
        ((Spinner) view.findViewById(R.id.spinner1)).setSelection(0);

        ((EditText) view.findViewById(R.id.editText8)).setText("");
        ((EditText) view.findViewById(R.id.editText9)).setText("");
        ((EditText) view.findViewById(R.id.editText10)).setText("");

        RadioGroup rg_term = (RadioGroup)view.findViewById(R.id.radioGroup2);
        int radioTermID = rg_term.getCheckedRadioButtonId();
        if(radioTermID >= 0){
            ((RadioButton) rg_term.findViewById(radioTermID)).setChecked(false);
        }
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

class GeoCoder extends AsyncTask<Property, Void, LatLng>{
    ProgressDialog progressDialog;
    Context context;

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    protected LatLng doInBackground(Property... params) {
        LatLng result = null;
        try {
            Property prop = params[0];
            String address = prop.getAddress() + ",+" + prop.getCity() + ",+" + prop.getState();
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(' ', '+');
            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null) {
                    response += temp;
                }
                object = (JSONObject) new JSONTokener(response).nextValue();
                if(object != null){
                    JSONObject json = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    result = new LatLng(json.getDouble("lat"), json.getDouble("lng"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        // this will close the bReader as well
                        inStream.close();
                    } catch (IOException ignored) {
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected void onPostExecute(LatLng result) {
        // execution of result of Long time consuming operation
        progressDialog.dismiss();
    }


    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context,
                "ProgressDialog",
                "Wait for the result");
    }
}
