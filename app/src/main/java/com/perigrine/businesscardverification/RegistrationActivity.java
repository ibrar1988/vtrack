package com.perigrine.businesscardverification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.preferences.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by root on 19/12/16.
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_done;
    private EditText et_Org_Name;
    private Spinner spinner_Center;
    private Spinner spinner_Department;
    private ArrayList<String> centersList,departmentsList,departmentIDlist;
    private String selectedCenterID,selectedDeptID,selectedCenterName;
    String primary_color;
    String secondary_color;
    String logo_url;
    String securityToken;
    AppPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        preference = new AppPreferences(RegistrationActivity.this);
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        findViews();
    }

    private void findViews() {
        btn_done= (Button) findViewById(R.id.btn_done);
        btn_done.setBackgroundColor(Color.parseColor(primary_color));
        et_Org_Name= (EditText) findViewById(R.id.et_login_organization_name);
        String data = Common.getSavedUserLoginData(RegistrationActivity.this);
        try {
            JSONObject dataObj = new JSONObject(data).getJSONObject("data");
            et_Org_Name.setText(dataObj.getString("organizationName"));
            et_Org_Name.setEnabled(false);
        } catch (Exception e) {
            e.toString();
        }
        btn_done.setOnClickListener(this);
        spinner_Center= (Spinner) findViewById(R.id.spinner_centers);
        spinner_Department= (Spinner) findViewById(R.id.spinner_departments);
        centersList=new ArrayList<String>();
        departmentsList=new ArrayList<String>();
        try {
            centersList = Common.getAllCenterNames(this);

            ArrayAdapter<String> centersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, centersList);
            // Drop down layout style - list view with radio button
            centersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Center.setAdapter(centersAdapter);

            spinner_Center.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                    selectedCenterID = Common.getCenterIdFromLoginData(RegistrationActivity.this).get(i).toString();
                    getDepartmentsForCenter(selectedCenterID);
                    preference.setCenterId(selectedCenterID);

                    selectedCenterName = Common.getAllCenterNames(RegistrationActivity.this).get(i);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinner_Department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    Toast.makeText(getApplicationContext(),"selected item"+departmentsList.get(i),Toast.LENGTH_SHORT).show();
                    try{
                        selectedDeptID = departmentIDlist.get(i);}
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
            e.toString();
        }
    }

    private void getDepartmentsForCenter(String selectedCenterID) {
        StringEntity entity = null;
        JSONObject jsonData = new JSONObject();
        String data = Common.getSavedUserLoginData(RegistrationActivity.this);
        try {
            jsonData = new JSONObject(data).getJSONObject("data");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("centerIds",getCenterArray(selectedCenterID));
            entity = new StringEntity(jsonObject.toString());
            Log.e("entity",":"+entity.toString());
            //  NetworkAdapter_Global.getResponse(LoginActivity.this,APICalls.URL_Login,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        try {
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            if(!pDialog.isShowing())
                pDialog.show();

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Content-Type", "application/json"));
            headers.add(new BasicHeader("securityToken",jsonData.getString("securityToken") ));
            securityToken = jsonData.getString("securityToken");

            JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      JSONObject response) { // If the response is JSONObject
                    // insteadof expected
                    // JSONArray
                    System.out.println("response"+departmentsList.size() + response);
                    try {
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        if (response.getString("statusCode").toString().trim().equals("200")) {
                            String statusMessage = response.get("statusMessage").toString().trim();
                            Log.e("departments",":"+response);
                            departmentsList=new ArrayList<String>();
                            departmentIDlist=new ArrayList<String>();
                            System.out.println("size"+departmentsList.size() );
                            Common.saveDepartmentsData(RegistrationActivity.this,response.toString());
                            JSONArray dataObj= response.getJSONArray("data");
                            for(int i=0;i<dataObj.length();i++)
                            {
                                departmentsList.add(dataObj.getJSONObject(i).getString("departmentName"));
                                departmentIDlist.add(dataObj.getJSONObject(i).getString("id"));
                            }
                            ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_item, departmentsList);
                            // Drop down layout style - list view with radio button
                            deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinner_Department.setAdapter(deptAdapter);
                            APICalls.setDeptNameList(RegistrationActivity.this,departmentsList);
                            APICalls.setDeptIDList(RegistrationActivity.this,departmentIDlist);

                        } else {
                            new AlertDialog.Builder(RegistrationActivity.this)
                                    .setTitle("Visitor Tracking")
                                    .setMessage("Something is not right here now, Please feel free to close the app and come back later")
                                    .setIcon(R.drawable.menu_app_icon)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    System.out.println("errorResponse" + errorResponse);
                           /* super.onFailure(statusCode, headers, throwable,
                                    errorResponse);*/
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }

            };

            NetworkAdapter.postWithHttpHeader(RegistrationActivity.this, APICalls.URL_findAllbyCenterID,entity, "application/json", reponseHandler, headers);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray getCenterArray(String selectedCenterID) {
        JSONArray array = new JSONArray();
        array.put(Integer.parseInt(selectedCenterID));
        return array;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_done)
        {
            StringEntity entity=null;
            JSONObject dataobj = new JSONObject();
            String data = Common.getSavedUserLoginData(RegistrationActivity.this);
            try {
                dataobj = new JSONObject(data).getJSONObject("data");
                JSONObject newJson = new JSONObject();
                newJson.put("email", dataobj.getString("email"));
                newJson.put("password", "1234567");
                newJson.put("deviceToken",Common.getDevicetoken(this));
                newJson.put("deviceUID",Common.getDevicetoken(this));
                newJson.put("deviceType", "android");
                newJson.put("organizationId", dataobj.getString("organizationId"));
                newJson.put("departmentId",selectedDeptID);
                newJson.put("centerId",selectedCenterID);
                System.out.println("login request jsonObject" + newJson);
                entity = new StringEntity(newJson.toString());
                Log.e("entity",":"+entity.toString());
                //  NetworkAdapter_Global.getResponse(LoginActivity.this,APICalls.URL_Login,jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final ProgressDialog pDialog = new ProgressDialog(this);
            try {
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                if(!pDialog.isShowing())
                pDialog.show();

                List<Header> headers = new ArrayList<Header>();
                headers.add(new BasicHeader("Content-Type", "application/json"));
                headers.add(new BasicHeader("securityToken",dataobj.getString("securityToken") ));

                JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) { // If the response is JSONObject
                        // insteadof expected
                        // JSONArray
                        System.out.println("response" + response);
                        try {
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            if (response.getString("statusCode").toString().trim().equals("200")) {
                                String statusMessage = response.get("statusMessage").toString().trim();
                                if(APICalls.loggable)
                                    Log.e("Registration Response",":"+response.toString());

//                                APICalls.filterPosition = 0;
//                                APICalls.filterRequesKey = null;
//                                APICalls.advanceFilterJsonRequest = null;
                                APICalls.setSelectedCenter(RegistrationActivity.this,selectedCenterName);
                                APICalls.setSelectedCenterID(RegistrationActivity.this,selectedCenterID);
                                Log.e("selected department di",":"+selectedDeptID);
                                APICalls.setSelectedDepartmentID(RegistrationActivity.this,selectedDeptID);
                                Log.e("selected department di",":"+APICalls.getSelectedDepartmentID(RegistrationActivity.this));

                                preference.setToken(securityToken);

                                Intent intent = new Intent(RegistrationActivity.this,HomeVistorsList.class);
                                startActivity(intent);
                                finish();
                            }else if(response.getString("statusCode").toString().trim().equals("404")){
                                Common.gotoLoginPage(RegistrationActivity.this);
                            } else {
                                new AlertDialog.Builder(RegistrationActivity.this)
                                        .setTitle("Visitor Tracking")
                                        .setMessage("Something is not right here now, Please feel free to close the app and come back later")
                                        .setIcon(R.drawable.menu_app_icon)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        System.out.println("errorResponse" + errorResponse);
                           /* super.onFailure(statusCode, headers, throwable,
                                    errorResponse);*/
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }
                };

                NetworkAdapter.postWithHttpHeader(RegistrationActivity.this, APICalls.URL_Registration,entity, "application/json", reponseHandler, headers);


            } catch (Exception e) {
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
                e.printStackTrace();
            }
        }
        }

    }




