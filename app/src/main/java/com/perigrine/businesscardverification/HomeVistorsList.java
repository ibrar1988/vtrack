package com.perigrine.businesscardverification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.Helper.VisitorListAdapter;
import com.perigrine.Model.VisitorModel;
import com.perigrine.preferences.AppPreferences;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HomeVistorsList extends AppCompatActivity{

    String primary_color;
    String secondary_color;
    String logo_url;
    private EditText inputSearch;
    private boolean isSearch = false;
    private VisitorListAdapter adapter;
    private List<VisitorModel> mVisitorList;
    private ListView visitorListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View ftView;
    private Handler mHandle;
    private Thread thread;
    private boolean isLoading = false;
    private LinearLayout layout_search;
    AppPreferences preference;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_vistors_list);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        preference = new AppPreferences(HomeVistorsList.this);
        primary_color = sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo", "");
        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_login));
        imageView_back.setVisibility(View.GONE);

        if(!logo_url.isEmpty()){
            Picasso.with(HomeVistorsList.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        layout_search = (LinearLayout) findViewById(R.id.layout_search);

        // Ibrar code

        visitorListView = (ListView) findViewById(R.id.visitor_ListView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ll_update_vistordata);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view_loader, null);
        ftView.setTag("visitorList_footer");

        mHandle = new mHandler();
        mVisitorList = new ArrayList<>();

        visitorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Clicked visitor id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });

        visitorListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !isLoading && inputSearch.getText().length() == 0) {
                    if(thread == null){
                        isLoading = true;
                        thread = new threadGetMoreVisitorList();
                        thread.start();
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getVisitorsList(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if ((inputSearch.getText().toString().equals("") || inputSearch.getText().length() == 0)&&!isLoading) {
                    isLoading = true;
                    mVisitorList = new ArrayList<>();
                    adapter = null;
                    getVisitorsList(false);
                } else {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }
        });
    }

    private void getVisitorsList(final boolean isFirstTime) {
        StringEntity entity;
        if (Common.isNetworkAvailable(HomeVistorsList.this)) {
            try {
                final ProgressDialog pDialog = new ProgressDialog(HomeVistorsList.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);

                if (isFirstTime && !pDialog.isShowing()) {
                    pDialog.show();
                } else {
                    swipeRefreshLayout.setRefreshing(true);
                }

                JSONObject jsonRequest = new JSONObject();
                // Create json request for login
//                jsonRequest.put("centerId", Common.getCenterIdFromLoginData(HomeVistorsList.this));
                jsonRequest.put("centerId",new JSONArray().put(preference.getCenterId()));
                jsonRequest.put("departmentId",new JSONArray() /*Common.getDepartmentIdFromLoginData(HomeVistorsList.this)*/);
                jsonRequest.put("month", "0");

                // Handling filter in request
                if (APICalls.filterRequesKey != null) {
                    if (APICalls.filterPosition == 5) {
                        jsonRequest.put(APICalls.filterRequesKey, "Red");
                    } else if (APICalls.filterPosition == 6) {
                        jsonRequest.put(APICalls.filterRequesKey, "Blue");
                    } else {
                        jsonRequest.put(APICalls.filterRequesKey, "true");
                    }
                }

                // Handling advanced filter in request
                if (APICalls.advanceFilterJsonRequest != null) {
                    Iterator iterator = APICalls.advanceFilterJsonRequest.keys();
                    String temp_key;
                    while (iterator.hasNext()) {
                        temp_key = (String) iterator.next();
                        jsonRequest.put(temp_key, APICalls.advanceFilterJsonRequest.get(temp_key));
                    }
                }

                System.out.println("**** Request ****" + jsonRequest.toString());

                entity = new StringEntity(jsonRequest.toString());
                List<Header> headers = NetworkAdapter.getHeaders(this);

                JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        if (isFirstTime && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }

                        try {

                            if (response.getString("statusCode").trim().equals("200")) {
                                ArrayList<JSONObject> list;
                                list =  Common.applySorting(response);
                                updateUIView(list);
//                                getEmployeeData();
                            } else if (response.getString("statusCode").trim().equals("404")) {
                                isLoading = false;
                                swipeRefreshLayout.setRefreshing(false);
                                Common.gotoLoginPage(HomeVistorsList.this);

                            } else {
                                isLoading = false;
                                swipeRefreshLayout.setRefreshing(false);
                                new AlertDialog.Builder(HomeVistorsList.this)
                                        .setTitle("Visitor Tracking")
                                        .setMessage(response.getString("statusMessage").trim())
                                        .setIcon(R.drawable.menu_app_icon)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException je) {
                            isLoading = false;
                            je.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        System.out.println("errorResponse JSONObject" + errorResponse);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        if (isFirstTime && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        if (isFirstTime && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        if (isFirstTime && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                };

                NetworkAdapter.postWithHttpHeader(HomeVistorsList.this, APICalls.URL_getVisitorsList,
                        entity, "application/json", reponseHandler, headers);

            } catch (Exception e) {
                isLoading = false;
                e.printStackTrace();
            }
        } else {
            Common.alertDialog(HomeVistorsList.this, "No internet connection.Please check the internet connection");
        }
    }

    private void updateUIView(ArrayList<JSONObject> visitorList) {

        System.out.println(visitorList.toString());
        adapter = new VisitorListAdapter(HomeVistorsList.this, logo_url, mVisitorList, visitorList);
        visitorListView.setAdapter(adapter);
        VisitorModel vm;

        try {
            if (visitorList.size() > 0) {
                for (int i = 0; i < visitorList.size(); i++) {
                    final JSONObject visitor = visitorList.get(i);
                    String name = visitor.get("name").toString().trim();
                    String companyName = visitor.get("company").toString().trim();
                    String email = visitor.get("email").toString().trim();
                    String designation = visitor.get("designation").toString().trim();
                    String signIn = visitor.get("visitedDateTime").toString().trim();
                    String signOut = visitor.get("VisitorOutTime").toString().trim();
                    String card_status = "";
                    String IdCardImage = visitor.getString("cardImage").toString().trim();
                    int visitingId = visitor.getInt("visitingId");

                    String badgeId = "";
                    if(visitor.has("badgeDetails")) {
                        JSONObject badgeDetails = visitor.getJSONObject("badgeDetails");
                        badgeId = badgeDetails.getString("badgeId");
                    }

                    vm = new VisitorModel(name, email, companyName, designation, signIn,
                            signOut, card_status, IdCardImage, badgeId, visitingId);
                    mVisitorList.add(vm);
                }

                //mVisitorList = Common.sortAdapterDate(mVisitorList);

            } else {
                isLoading = false;
                Common.alertDialog(HomeVistorsList.this, "Sorry no record found");
            }
        } catch (JSONException je) {
            isLoading = false;
            je.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDisplayCount(10);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        }, 50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visitors, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_meeting) {
            Intent in = new Intent(this, MeetingsActivity.class);
            startActivity(in);
        } else if (item.getItemId() == R.id.action_search) {
            if (isSearch) {
                isSearch = false;
                layout_search.setVisibility(View.GONE);
            } else {
                isSearch = true;
                layout_search.setVisibility(View.VISIBLE);
            }
        } else if (item.getItemId() == R.id.action_filter) {
            Intent filterScreen = new Intent(this, ApplyFilters.class);
            startActivityForResult(filterScreen, APICalls.REQUEST_CODE_FOR_SORT_FILTER);
        } else if (item.getItemId() == R.id.action_plus) {
            Intent in = new Intent(this, VisitorDetailsForm.class);
            in.putExtra("jdata", "");
            startActivity(in);
        } else if (item.getItemId() == R.id.action_settings) {
            Intent in = new Intent(this, SettingsActivity.class);
            startActivity(in);
        } else if (item.getItemId() == R.id.action_logout) {
            userLogoutAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    public void userLogoutAlert(){
        new AlertDialog.Builder(HomeVistorsList.this)
                .setTitle("Visitor Tracking")
                .setMessage("Do you want logout from the application...!")
                .setIcon(R.drawable.menu_app_icon)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userLogout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void userLogout() {
        try {
            if (Common.isNetworkAvailable(HomeVistorsList.this)) {
                final ProgressDialog pDialog = new ProgressDialog(HomeVistorsList.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                if(!pDialog.isShowing()){
                    pDialog.show();
                }
                StringEntity entity = null;
                try {
                    JSONObject js = new JSONObject();
                    System.out.println("js::" + js.toString());
                    entity = new StringEntity(js.toString());
                    List<Header> headers = NetworkAdapter.getHeaders(HomeVistorsList.this);
                    JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {
                            try {
                                Log.i("user logout", response.toString());
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                if (response.getString("statusCode").toString().trim().equals("200")) {
                                    logout();
                                } else if (response.getString("statusCode").toString().trim().equals("404")) {
                                    Common.gotoLoginPage(HomeVistorsList.this);
                                } else {
                                    new AlertDialog.Builder(HomeVistorsList.this)
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
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            System.out.println("response" + responseString);
                            Intent in = new Intent(HomeVistorsList.this, HomeVistorsList.class);
                            startActivity(in);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              Throwable throwable, JSONObject errorResponse) {
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            System.out.println("errorResponse" + errorResponse);
                            super.onFailure(statusCode, headers, throwable,
                                    errorResponse);

                        }
                    };

                    NetworkAdapter.postWithHttpHeader(HomeVistorsList.this, APICalls.URL_UserLogout,
                            entity, "application/json", reponseHandler, headers);

                } catch (Exception e) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    e.printStackTrace();
                }
            } else {
                Common.alertDialog(HomeVistorsList.this, "No internet connection.Please check the internet connection");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            preference.setToken("");
            Intent intent = new Intent(HomeVistorsList.this, LoginActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateSignout(String id) {
        StringEntity entity = null;
        final ProgressDialog pDialog = new ProgressDialog(this);
        try {
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("timeZone", TimeZone.getDefault().getID());
            System.out.println("Json:::::" + json);

            entity = new StringEntity(json.toString());
            List<Header> headers = NetworkAdapter.getHeaders(this);
            JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      JSONObject response) {
                    pDialog.hide();
                    System.out.println("response in signout time:::" + response);
                    try {
                        if (response.getString("statusCode").toString().trim().equals("200")) {
                            getVisitorsList(false);
                        } else if (response.getString("statusCode").toString().trim().equals("404")) {
                            Common.gotoLoginPage(HomeVistorsList.this);
                        } else {
                            new AlertDialog.Builder(HomeVistorsList.this)
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
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    System.out.println("errorResponse" + errorResponse);
                    super.onFailure(statusCode, headers, throwable,
                            errorResponse);
                    System.out.println("response in signout time:::" + errorResponse);
                    pDialog.hide();
                }
            };

            NetworkAdapter.postWithHttpHeader(HomeVistorsList.this, APICalls.URL_visitorOutTime,
                    entity, "application/json", reponseHandler, headers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            userLogoutAlert();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getLogo() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("new_theme", MODE_PRIVATE);
        final String logo_url = sharedPreferences.getString("org_Logo", "");
        //if(logo_url.length() > 0) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                                        getSupportActionBar().setIcon(R.drawable.icon);
                final ActionBar ab = getSupportActionBar();
                Picasso.with(HomeVistorsList.this)
                        .load(logo_url).resize(100, 100).placeholder(R.drawable.default_card)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Drawable d = new BitmapDrawable(getResources(), bitmap);
                                ab.setIcon(d);

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });
            }
        });
    }

    public void visitorLogout(int visitorId) {
        try {
            if (Common.isNetworkAvailable(HomeVistorsList.this)) {
                final ProgressDialog pDialog = new ProgressDialog(HomeVistorsList.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                if(!pDialog.isShowing())
                pDialog.show();
                StringEntity entity = null;
                try {
                    //visitor ID
                    JSONObject js = new JSONObject();
                    js.put("id", "" + visitorId);
                    js.put("timeZone", "IST");
                    System.out.println("js::" + js.toString());
                    entity = new StringEntity(js.toString());
                    List<Header> headers = NetworkAdapter.getHeaders(HomeVistorsList.this);
                    JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {
                            try {
                                Log.i("visitor logout", response.toString());
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                if (response.getString("statusCode").toString().trim().equals("200")) {
                                    getVisitorsList(false);
                                } else if (response.getString("statusCode").toString().trim().equals("404")) {
                                    Common.gotoLoginPage(HomeVistorsList.this);
                                } else {
                                    new AlertDialog.Builder(HomeVistorsList.this)
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
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            System.out.println("response" + responseString);
                            Intent in = new Intent(HomeVistorsList.this, HomeVistorsList.class);
                            startActivity(in);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              Throwable throwable, JSONObject errorResponse) {
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            System.out.println("errorResponse" + errorResponse);
                            super.onFailure(statusCode, headers, throwable,
                                    errorResponse);

                        }
                    };

                    NetworkAdapter.postWithHttpHeader(HomeVistorsList.this, APICalls.URL_VisitorLogout,
                            entity, "application/json", reponseHandler, headers);

                } catch (Exception e) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    e.printStackTrace();
                }
            } else {
                Common.alertDialog(HomeVistorsList.this, "No internet connection.Please check the internet connection");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    visitorListView.addFooterView(ftView);
                    break;
                case 1:
                    try {
                        if(adapter != null) {
                            if (adapter.getAdapterSize() > 9 && adapter.getAdapterSize() > adapter.getDisplaySize()) {
                                int size = 10;
                                if (adapter != null){
                                    if (adapter != null && adapter.getAdapterSize() - adapter.getDisplaySize() < 10) {
                                        size = adapter.getAdapterSize() - adapter.getDisplaySize();
                                    }
                                    adapter.setDisplayCount(adapter.getDisplaySize() + size);
                                }
                            }
                        }

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP){
                            if(visitorListView.getFooterViewsCount()>0) {
                                View footerView = visitorListView.findViewWithTag("visitorList_footer");
                                if(footerView!=null) {
                                    visitorListView.removeFooterView(ftView);
                                }
                            }
                        } else {
                            visitorListView.removeFooterView(ftView);
                        }
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                        break;
                    }
                    if(thread != null){
                        thread.interrupt();
                        thread = null;
                    }
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    private class threadGetMoreVisitorList extends Thread {
        @Override
        public void run() {
            try {
                if(!isLoading){
                    return;
                }

                mHandle.sendEmptyMessage(0);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandle.obtainMessage(1, null);
                mHandle.sendMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here
        if (requestCode == APICalls.REQUEST_CODE_FOR_SORT_FILTER) {
            // Make sure the request was successful
            if (resultCode == APICalls.RESULT_CODE_FOR_SORTING) {
                if(adapter != null){
                    final ProgressDialog pDialog = new ProgressDialog(HomeVistorsList.this);
                    if(!pDialog.isShowing()){
                        pDialog.show();
                    }
                    List<VisitorModel> newSortedDate = Common.sortAdapterDate(adapter.getAdapterData());
                    ArrayList<JSONObject> secondData = Common.sortSecondData(adapter.getSecondData());
                    mVisitorList = new ArrayList<>();
                    ArrayList<JSONObject> visitorList = new ArrayList<>();
                    adapter = null;
                    adapter = new VisitorListAdapter(HomeVistorsList.this, logo_url, mVisitorList, visitorList);
                    visitorListView.setAdapter(adapter);
                    adapter.setDisplayCount(10);
                    mVisitorList = newSortedDate;
                    adapter.changeList(newSortedDate);
                    adapter.setSecondData(secondData);
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }
            } else if(resultCode == APICalls.RESULT_CODE_FOR_FILTER){
                mVisitorList = new ArrayList<>();
                adapter = null;
                getVisitorsList(true);
            } else if(resultCode == 0) {
                if(mVisitorList.size()==0){
                    getVisitorsList(true);
                } else if(APICalls.masterResetFilter&&APICalls.filterRequesKey==null&&APICalls.advanceFilterJsonRequest==null&&APICalls.filterPosition==0) {
                    APICalls.masterResetFilter = false;
                    mVisitorList = new ArrayList<>();
                    adapter = null;
                    getVisitorsList(true);
                }
            }
        }
    }
}