package com.perigrine.businesscardverification;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Extras.AlertDialogClass;
import com.perigrine.Extras.Utils;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.Interfaces.AlertCallBack;
import com.perigrine.preferences.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PERMISSION = 100;
    private EditText et_userName;
    private EditText et_password;
    private Button btn_login;
    private LinearLayout layout_centerInfo;
    private TextView txt_centerName;
    private ImageView img_Logo;
    Toolbar toolbar;
    String primary_color;
    String secondary_color;
    String logo_url;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    AppPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setVisibility(View.GONE);
        findViews();
        et_userName.setText("");
        et_password.setText("");//Peregrine/peregrine
    }

    private void findViews() {
        et_userName = (EditText) findViewById(R.id.et_login_userName);
        et_password = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        layout_centerInfo = (LinearLayout) findViewById(R.id.ll_login_center_information);
        txt_centerName = (TextView) findViewById(R.id.tv_login_center_name);

        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        preference = new AppPreferences(this);
        primary_color = sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo", "");
        btn_login.setBackgroundColor(Color.parseColor(primary_color));

        et_userName.setBackground(APICalls.getDrawable(et_userName, primary_color));
        et_userName.setBackground(APICalls.getDrawable(et_password, primary_color));


        txt_centerName.setTextColor(Color.parseColor(primary_color));
        toolbar.setBackgroundColor(Color.parseColor(primary_color));

        //img_Logo= (ImageView) findViewById(R.id.img_Logo);

//        if(logo_url.length() > 0)
//            Picasso.with(this).load(logo_url).into(img_Logo);

        if (!APICalls.getSelectedCenter(this).equals("")) {
            layout_centerInfo.setVisibility(View.VISIBLE);
            txt_centerName.setText(APICalls.getSelectedCenter(this));
        }
        btn_login.setOnClickListener(this);
        Common.clearErrorMask(et_userName);
        Common.clearErrorMask(et_password);


    }

    @Override
    public void onClick(View v) {
        // APICalls.serverURL="http://"+et_domain.getText().toString()+"/BusinessCard";
        checkAndRequestPermissions();

        /*if (v == btn_login) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                getLoginButtonClickEvent();
            } else {
                if (checkAndRequestPermissions()) {
                    //If you have already permitted the permission
                }
            }
        }*/
    }


    private void getSDKDetails(JSONObject je, final ProgressDialog pDialog) {
        StringEntity entity = null;
        try {
            JSONObject js = new JSONObject();
            js.put("loginType", "app");
            js.put("appType", "android");
            System.out.println("js::" + js.toString());
            entity = new StringEntity(js.toString());
            List<Header> headers = NetworkAdapter.getHeaders(this);
            JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
                    System.out.println("URL_getAbbyyOcrDetails-response" + response);

                    if(pDialog!=null && pDialog.isShowing()){
                        pDialog.dismiss();
                    }

                    try {
                        if (response.getString("statusCode").toString().trim().equals("200")) {
                            Common.setCredentials(LoginActivity.this, response.toString());
                            if (APICalls.getSelectedCenter(LoginActivity.this).equals("")) {
                                Intent in = new Intent(LoginActivity.this, RegistrationActivity.class);
                                startActivity(in);
                            } else {
                                Intent in = new Intent(LoginActivity.this, HomeVistorsList.class);
                                startActivity(in);
                            }
                        } else if (response.getString("statusCode").toString().trim().equals("404")) {
                            Common.gotoLoginPage(LoginActivity.this);
                        } else {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Visitor Tracking")
                                    .setMessage("Something is not right here now, Please feel free to close the app and come back later")
                                    .setIcon(R.drawable.menu_app_icon)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    System.out.println("response" + responseString);
                    if (pDialog != null && pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Intent in = new Intent(LoginActivity.this, HomeVistorsList.class);
                    startActivity(in);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    System.out.println("errorResponse" + errorResponse);
                    super.onFailure(statusCode, headers, throwable,
                            errorResponse);
                    if (pDialog != null && pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    if (pDialog != null && pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }

            };

            NetworkAdapter.postWithHttpHeader(LoginActivity.this, APICalls.URL_getAbbyyOcrDetails,
                    entity, "application/json", reponseHandler, headers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION);

            }else{
                getLoginButtonClickEvent();}
        }else{
            getLoginButtonClickEvent();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        getLoginButtonClickEvent();
                    } else {
                        Utils.showToast(LoginActivity.this, "Enable all permissions to enter the app");
                        if (!shouldShowRequestPermissionRationale(permissions[1])) {
                            Common.goToSettings("Enable all permissions to enter the app", LoginActivity.this);

                        }
                    }
                } else {
                    Utils.showToast(LoginActivity.this, "Enable all permissions to enter the app");
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        Common.goToSettings("Enable all permissions to enter the app", LoginActivity.this);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*public void goToSettings(String message, final Context context) {

        AlertDialogClass adc = new AlertDialogClass(context, message, false, new AlertCallBack() {
            @Override
            public void positivte(boolean value) {
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myAppSettings);
            }
        });
        adc.showAlert();

    }*/

    public void getLoginButtonClickEvent() {

        //Permission Granted Successfully. Write working code here.
        //api selection
        boolean checkFlag = false;
        if (et_userName.getText().toString().trim().equals("")) {
            checkFlag = true;
            et_userName.setError("Enter Username");
        }
        if (et_password.getText().toString().trim().equals("")) {
            checkFlag = true;
            et_password.setError("Enter Password");
        }

        if (!checkFlag) {
            if (Common.isNetworkAvailable(LoginActivity.this)) {
                StringEntity entity = null;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", et_userName.getText().toString().trim());
                    jsonObject.put("password", et_password.getText().toString().trim());
                    jsonObject.put("deviceToken", Common.getDevicetoken(this));
                    jsonObject.put("deviceUID", Common.getDevicetoken(this));
                    jsonObject.put("loginType", "app");
                    jsonObject.put("deviceType", "android");
                    if (!APICalls.getSelectedCenter(this).equals("")) {
                        jsonObject.put("isRegistered", "yes");
                    } else {
                        jsonObject.put("isRegistered", "no");
                    }

                    System.out.println("login request jsonObject" + jsonObject);
                    entity = new StringEntity(jsonObject.toString());
                    Log.e("entity", ":" + entity.toString());
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
                    if(!pDialog.isShowing()){
                        pDialog.show();
                    }

                    List<Header> headers = new ArrayList<Header>();
                    headers.add(new BasicHeader("Content-Type", "application/json"));

                    JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) { // If the response is JSONObject
                            // insteadof expected
                            // JSONArray
                            System.out.println("response" + response);
                            try {
                                if (response.getString("statusCode").toString().trim().equals("200")) {
                                    //String statusMessage = response.get("statusMessage").toString().trim();
                                    if (APICalls.loggable)
                                        Log.e("Login Response", ":" + response.toString());
                                    Common.setOrganization_NewTheme(LoginActivity.this, response);

                                    Common.saveUserLoginData(LoginActivity.this, response.toString());
                                    if (!APICalls.getSelectedCenter(LoginActivity.this).equals("")) {
                                        preference.setToken(response.getJSONObject("data").getString("securityToken"));
                                    }

//                                    APICalls.filterPosition = 0;
//                                    APICalls.filterRequesKey = null;
//                                    APICalls.advanceFilterJsonRequest = null;
                                    getSDKDetails(response, pDialog);
                                } else if (response.getString("statusCode").toString().trim().equals("501")) {
                                    if(pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle("Visitor Tracking")
                                            .setMessage(response.getString("statusMessage").toString())
                                            .setIcon(R.drawable.menu_app_icon)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                } else if (response.getString("statusCode").toString().trim().equals("404")) {
                                    if(pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                    Common.gotoLoginPage(LoginActivity.this);
                                } else {
                                    if(pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                    new AlertDialog.Builder(LoginActivity.this)
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
                                if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }

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

                    NetworkAdapter.postWithHttpHeader(LoginActivity.this, APICalls.URL_Login,
                            entity, "application/json", reponseHandler, headers);


                } catch (Exception e) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    e.printStackTrace();
                }
            } else {
                Common.alertDialog(LoginActivity.this,"No internet connection.Please check the internet connection");
            }
        }


    }
}