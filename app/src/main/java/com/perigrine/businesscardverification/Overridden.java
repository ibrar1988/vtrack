package com.perigrine.businesscardverification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Overridden extends AppCompatActivity implements View.OnClickListener {
    private EditText et_override_name, et_override_email, et_override_phno;
    private Button btn_override;
    String primary_color;
    String secondary_color;
    String logo_url;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overridden);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo","");
        findViews();
    }

    private void findViews() {
        et_override_email = (EditText) findViewById(R.id.et_override_email);
        et_override_name = (EditText) findViewById(R.id.et_override_name);
        et_override_phno = (EditText) findViewById(R.id.et_override_phno);
       // et_override_phno.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        btn_override = (Button) findViewById(R.id.btn_override);
        btn_override.setOnClickListener(this);
        btn_override.setBackgroundColor(Color.parseColor(primary_color));
        Common.clearErrorMask(et_override_name);
        Common.clearErrorMask(et_override_email);
        Common.clearErrorMask(et_override_phno);

        et_override_name.setText(getIntent().getStringExtra("name"));

        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_overridden));
        imageView_back.setVisibility(View.GONE);

        if(!logo_url.isEmpty()){
            Picasso.with(Overridden.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }
    }

    @Override
    public void onClick(View v) {
        if (et_override_name.getText().toString().trim().equals("")) {
            et_override_name.requestFocus();
            et_override_name.setError("Enter Name");
        }else if(validateName(et_override_name.getText().toString()).equals("Only digits not allowed.Name should be alphanumeric"))
        {
            et_override_name.requestFocus();
            et_override_name.setError("Only digits not allowed.Name should be alphanumeric");
        }else if(validateName(et_override_name.getText().toString()).equals("Only special character not allowed.Name should be alphanumeric")) {
            et_override_name.requestFocus();
            et_override_name.setError("Only special character not allowed.Name should be alphanumeric");
        }
        else if (et_override_email.getText().toString().trim().equals("")) {
            et_override_email.requestFocus();
            et_override_email.setError("Enter Email");
        } else if (!Common.isValidEmail(et_override_email.getText().toString().trim())) {
            et_override_email.setError("Enter valid Email");
        } else if (et_override_phno.getText().toString().trim().equals("")) {
            et_override_phno.requestFocus();
            et_override_phno.setError("Enter Phone Number");
        } else {

            final ProgressDialog pDialog = new ProgressDialog(this);
            try {
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                if(!pDialog.isShowing())
                pDialog.show();
                StringEntity entity = null;
                JSONObject json = new JSONObject();
                //{"visitorId":960,"name":"Shravan Kumar","mobileNo":"9912520438","email":"shravaniosdev@gmail.com"}
                Log.e("visitor id",":"+APICalls.vm.getVisitorId());
                json.put("visitingId", APICalls.vm.getVisitingId());
                json.put("name", et_override_name.getText().toString().trim());
                json.put("mobileNo", et_override_phno.getText().toString().trim());
                json.put("email", et_override_email.getText().toString().trim());


                System.out.println("resuest json11::" + json);
                entity = new StringEntity(json.toString());

                List<Header> headers = NetworkAdapter.getHeaders(this);
                JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        System.out.println("response" + response);
                        try {
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            if (response.getString("statusCode").toString().trim().equals("200")) {
                                Intent in = new Intent(Overridden.this, IssueBadge.class);
                                startActivity(in);
                            }else if(response.getString("statusCode").toString().trim().equals("404")){
                                Common.gotoLoginPage(Overridden.this);
                            } else {
                                new AlertDialog.Builder(Overridden.this)
                                        .setTitle("Visitor Tracking")
                                        .setMessage(response.getString("statusMessage").toString().trim())
                                        .setIcon(R.drawable.menu_app_icon)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        System.out.println("errorResponse" + errorResponse);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);

                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }
                };
                if(Common.isNetworkAvailable(Overridden.this)) {
                    NetworkAdapter.postWithHttpHeader(Overridden.this, APICalls.URL_addOverrideDetails,
                            entity, "application/json", reponseHandler, headers);
                }else{
                    Common.alertDialog(Overridden.this,"No internet connection.Please check the internet connection");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String validateName(String s) {
        boolean valid=false;
        String splChrs = "-/@#*$%^&_+=()<>?{}!~!" ;
        if(s.matches("[0-9]+")){
            return "Only digits not allowed.Name should be alphanumeric";
        }
        else if(s.matches("[" + splChrs + "]+"))
        {
            return "Only special character not allowed.Name should be alphanumeric";
        }
        else{
            return "true";
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            Intent in = new Intent(this, HomeVistorsList.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}