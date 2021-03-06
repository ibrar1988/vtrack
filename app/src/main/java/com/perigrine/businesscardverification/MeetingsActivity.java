package com.perigrine.businesscardverification;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.Model.MeetingDetails;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MeetingsActivity extends AppCompatActivity {

    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    RecyclerView recycler_list_details;

    LinearLayout layout_details;
    String primary_color, secondary_color, logo_url;
    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo","");
        Log.i("logo url",logo_url);
        initViews();
        getMeetingDetails();
    }

    private void initViews() {
        recycler_list_details = (RecyclerView) findViewById(R.id.recycler_list_details);
        layout_details = (LinearLayout) findViewById(R.id.layout_details);
        layout_details.setVisibility(View.GONE);
        recylerViewLayoutManager = new LinearLayoutManager(MeetingsActivity.this);
        recycler_list_details.setLayoutManager(recylerViewLayoutManager);
        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_meetings));
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

        if(!logo_url.isEmpty()){
            Picasso.with(MeetingsActivity.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }

    }

    private void getMeetingDetails() {

            if (Common.isNetworkAvailable(MeetingsActivity.this)) {
                final ProgressDialog pDialog = new ProgressDialog(MeetingsActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                if(!pDialog.isShowing())
                pDialog.show();
                StringEntity entity = null;
                try {
                    JSONObject js = new JSONObject();
                    js.put("centerId", Integer.parseInt(APICalls.getSelectedCenterID(this)));
                    js.put("timezone", TimeZone.getDefault().getID());
                    js.put("currentDate", Common.getDateTime(System.currentTimeMillis(),"yyyy-MM-dd"));
                    System.out.println("js::" + js.toString());
                    entity = new StringEntity(js.toString());
                    List<Header> headers = NetworkAdapter.getHeaders(MeetingsActivity.this);
                    JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
                            try {
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                if (response.getString("statusCode").toString().trim().equals("200")) {
                                    UpdateUI(response);
                                } else if (response.getString("statusCode").toString().trim().equals("404")) {
                                    Common.gotoLoginPage(MeetingsActivity.this);
                                } else {
                                    new android.app.AlertDialog.Builder(MeetingsActivity.this)
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
                            Intent in = new Intent(MeetingsActivity.this, HomeVistorsList.class);
                            startActivity(in);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) {
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            System.out.println("errorResponse" + errorResponse);
                            super.onFailure(statusCode, headers, throwable,
                                    errorResponse);

                        }
                    };

                    NetworkAdapter.postWithHttpHeader(MeetingsActivity.this, APICalls.URL_MeetingDetails,
                            entity, "application/json", reponseHandler, headers);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Common.alertDialog(MeetingsActivity.this,"No internet connection.Please check the internet connection");
            }
    }

    private void UpdateUI(JSONObject response) {
        try{
            layout_details.setVisibility(View.VISIBLE);

            JSONObject  data = response.getJSONObject("data");
            JSONArray meetings = data.getJSONArray("meetingDetails");
            JSONArray meetings_today=new JSONArray();

            if(meetings != null && meetings.length() > 0) {
                Type listType = new TypeToken<List<MeetingDetails>>() {}.getType();
                List<MeetingDetails> meetingDetails = new Gson().fromJson(meetings.toString(), listType);
                List<MeetingDetails> meetingsList = new ArrayList<MeetingDetails>();

                for(int i = 0 ; i < meetingDetails.size() ; i++){
                    if(DateUtils.isToday(meetingDetails.get(i).getStartTime())){
                        meetingsList.add(meetingDetails.get(i));
                        meetings_today.put(meetings.get(i));
                    }
                }

                Log.i("meetings", Arrays.asList(meetingDetails).toString());

                recyclerViewAdapter = new MeetingDetailsAdapter(MeetingsActivity.this, meetingsList,meetings_today);
                recycler_list_details.setAdapter(recyclerViewAdapter);
            }

        }catch (Exception e){e.printStackTrace();}
    }

    private void showAlert(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(MeetingsActivity.this);
        builder.setMessage("Email not found")
                .setTitle("Error")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meeting, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            //visitor tracking details navigation
            Intent in = new Intent(this, VisitorDetailsForm.class);
            in.putExtra("jdata", "");
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}
