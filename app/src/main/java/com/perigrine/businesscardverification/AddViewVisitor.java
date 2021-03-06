package com.perigrine.businesscardverification;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Extras.Utils;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.CropOption;
import com.perigrine.Helper.CropOptionAdapter;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.Model.VisitingDetails;
import com.perigrine.Model.VisitorModel;
import com.perigrine.OCRSDk.BusCardSettings;
import com.perigrine.OCRSDk.Client;
import com.perigrine.OCRSDk.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 10/3/17.
 */
public class AddViewVisitor extends Fragment implements View.OnClickListener {

    int count = 0;
    ProgressDialog dialog;
    String primary_color;
    String secondary_color;
    View recurring_Visits_View;
    ArrayList<String> recurring_selected = new ArrayList<>();
    ArrayList<String> recurring_list = new ArrayList<>();
    ArrayList<String> recurring_upload = new ArrayList<>();
    Uri mImageCaptureUri;
    private String overrideData = "";
    private String resultUrl = "result.txt";
    private ImageView iv_businessCard_captured;
    private LinearLayout ll_extraLayout;
    private Context context;
    // private Button btn_getVisitorDetails;
    // private EditText et_visitorID;
    private Button btn_verify, btn_overRidden;
    private EditText et_add_name, et_add_company, et_add_phno, et_add_email,
            et_add_title, et_add_homepage, et_add_address, et_add_scannedDate, et_add_whomToMeet_email, et_add_whomTomeet_phno, et_add_whomTomeet_role, et_host_name, et_visit_purpose, et_recurring_visits;
    private String imageFilePath = "";
    private Spinner spn_departments;
    private TextView tv_departments,tv_add_recurringvisits;
    private String selected_visitorId = "";
    JSONObject jsonObject;
    String dateFormat = "MMM dd,yyyy HH:mm";;
    ImageView imageView_calendar;
    String meetingId = "0";

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ABBYY Cloud OCR SDK Demo App");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Oops! Failed create  ABBYY Cloud OCR SDK Demo App");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color = sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_visitor_basic_details, container, false);
        context = getContext();
        applyColorToFields(view);
        findViews(view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    private void findViews(View view) {
        //btn_getVisitorDetails = (Button) view.findViewById(R.id.btn_getVisitorDetails);
        //btn_getVisitorDetails.setBackgroundColor(Color.parseColor(primary_color));
        btn_verify = (Button) view.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        btn_verify.setBackgroundColor(Color.parseColor(primary_color));
        // btn_getVisitorDetails.setOnClickListener(this);
        btn_overRidden = (Button) view.findViewById(R.id.btn_overRidden);
        btn_overRidden.setOnClickListener(this);
        btn_overRidden.setBackgroundColor(Color.parseColor(primary_color));
        // et_visitorID = (EditText) view.findViewById(R.id.et_visitor_id);
        //et_visitorID.setBackground(APICalls.getDrawable(et_visitorID, primary_color));
        et_add_name = (EditText) view.findViewById(R.id.et_add_name);
        et_add_name.setBackground(APICalls.getDrawable(et_add_name, primary_color));
        et_add_company = (EditText) view.findViewById(R.id.et_add_company);
        et_add_company.setBackground(APICalls.getDrawable(et_add_company, primary_color));
        et_add_phno = (EditText) view.findViewById(R.id.et_add_phno);
        et_add_phno.setBackground(APICalls.getDrawable(et_add_phno, primary_color));
        et_add_email = (EditText) view.findViewById(R.id.et_add_email);
        et_add_email.setBackground(APICalls.getDrawable(et_add_email, primary_color));
        et_add_title = (EditText) view.findViewById(R.id.et_add_title);
        et_add_title.setBackground(APICalls.getDrawable(et_add_title, primary_color));
        et_add_homepage = (EditText) view.findViewById(R.id.et_add_homepage);
        et_add_homepage.setBackground(APICalls.getDrawable(et_add_homepage, primary_color));
        et_add_address = (EditText) view.findViewById(R.id.et_add_address);
        et_add_address.setBackground(APICalls.getDrawable(et_add_address, primary_color));

        ////
        et_host_name = (EditText) view.findViewById(R.id.et_host_name);
        et_host_name.setBackground(APICalls.getDrawable(et_host_name, primary_color));
        et_visit_purpose = (EditText) view.findViewById(R.id.et_visit_purpose);
        et_visit_purpose.setBackground(APICalls.getDrawable(et_visit_purpose, primary_color));
        spn_departments = (Spinner) view.findViewById(R.id.spn_department);
        //spn_department.setBackground(APICalls.getDrawable(spn_department, primary_color));
        ArrayAdapter<String> deptAdapter = null;
        deptAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Common.getDepartmentNameFromLoginData(context));
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_departments.setAdapter(deptAdapter);

        // Drop down layout style - list view with radio button
        imageView_calendar = (ImageView) view.findViewById(R.id.imageView_calendar);
        et_recurring_visits = (EditText) view.findViewById(R.id.et_recurring_visits);
        et_recurring_visits.setBackground(APICalls.getDrawable(et_recurring_visits, primary_color));
        imageView_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyTimePicker myTimePicker = new MyTimePicker(context, et_recurring_visits);
                showRecurringVisits();
            }
        });
        //DyNAMIC FILDS

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
     //   String data = sharedpreferences.getString("visitor_item", "");
        String data = Common.getSavedUserLoginData(this.getActivity());
        JSONObject jsonnobj = null;
        try {
            JSONObject dataObj = new JSONObject(data).getJSONObject("data");
            String advFeatures = dataObj.getString("advFeatures");
            if (!advFeatures.contains("department")) {
                spn_departments.setVisibility(View.GONE);
                tv_departments.setVisibility(View.GONE);
            }
            if (!advFeatures.contains("meeting")) {
                et_recurring_visits.setVisibility(View.GONE);
                tv_add_recurringvisits.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //hiding the homepage and address fileds
        // et_add_title.setVisibility(View.GONE);
        // et_add_homepage.setVisibility(View.GONE);
        // et_add_address.setVisibility(View.GONE);
        ll_extraLayout = (LinearLayout) view.findViewById(R.id.ll_extrafileds);
        iv_businessCard_captured = (ImageView) view.findViewById(R.id.iv_businessCard_captured);


        //getCustomFields();
        try {
            if (!VisitorDetailsForm.jdata.trim().equals("")) {
                btn_verify.setText("REVISIT");
                System.out.println("jdata:::::::" + VisitorDetailsForm.jdata);
                jsonObject = new JSONObject(VisitorDetailsForm.jdata);
                et_add_name.setText(jsonObject.get("name").toString().trim());
                et_add_company.setText(jsonObject.get("company").toString().trim());
                et_add_phno.setText(jsonObject.get("mobileNo").toString().trim());
                et_add_email.setText(jsonObject.get("email").toString().trim());
                et_add_title.setText(jsonObject.get("designation").toString().trim());
                et_add_homepage.setText(jsonObject.get("homePage").toString().trim());
                et_add_address.setText(jsonObject.get("address").toString().trim());
                //  et_add_scannedDate.setText(jsonObject.get("visitedDateTime").toString().trim());
                selected_visitorId=jsonObject.get("visitorId").toString().trim();
                et_host_name.setText(jsonObject.get("whomToMeet").toString().trim());
                et_visit_purpose.setText(jsonObject.get("purpose").toString().trim());
                //Override details
                if(jsonObject.has("isOverridden")) {
                    if (jsonObject.get("isOverridden").toString().trim().equals("true")) {
                        overrideData = jsonObject.get("overrideDetails").toString().trim();
                        btn_overRidden.setVisibility(View.VISIBLE);
                    } else {
                        btn_overRidden.setVisibility(View.GONE);
                    }
                }

                //
                if(jsonObject.has("cardImage")) {
                    if (jsonObject.get("cardImage").toString().trim().equals("")) {
                        iv_businessCard_captured.setImageDrawable(getResources().getDrawable(R.drawable.default_card));
                    } else {
                        Picasso.with(getActivity()).load(Common.getImageUrl(jsonObject.get("cardImage").toString().trim())).placeholder(R.drawable.default_card).resize(320, 300).into(iv_businessCard_captured);
                    }
                }
                iv_businessCard_captured.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView iv = new ImageView(getActivity());
                        try {
                            if (jsonObject.has("cardImage") && !jsonObject.get("cardImage").toString().trim().equals("")) {
                                Picasso.with(getActivity()).load(Common.getImageUrl(jsonObject.get("cardImage").toString().trim())).placeholder(R.drawable.default_card).resize(320, 300).into(iv);
                            } else {
                                iv.setImageDrawable(getResources().getDrawable(R.drawable.default_card));
                            }
                            new AlertDialog.Builder(getActivity())
                                    .setView(iv)
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                getRecurringMeetings(jsonObject);

                boolean isEditable = false;
                if(jsonObject.has("visitorObj")){
                    btn_verify.setText("VERIFY");
                    et_recurring_visits.setVisibility(View.GONE);
                    tv_add_recurringvisits.setVisibility(View.GONE);
                    isEditable = true;
                }else{
                    et_recurring_visits.setVisibility(View.VISIBLE);
                    tv_add_recurringvisits.setVisibility(View.VISIBLE);
                    isEditable = false;
                }


                et_add_name.setEnabled(isEditable);
                et_add_company.setEnabled(isEditable);
                et_add_phno.setEnabled(isEditable);
                et_add_email.setEnabled(isEditable);
                et_add_title.setEnabled(isEditable);
                et_add_homepage.setEnabled(isEditable);
                et_add_address.setEnabled(isEditable);
                et_add_scannedDate.setEnabled(isEditable);
            } else {
                btn_verify.setText("VERIFY");
                et_add_name.setEnabled(true);
                et_add_company.setEnabled(true);
                et_add_phno.setEnabled(true);
                et_add_email.setEnabled(true);
                et_add_title.setEnabled(true);
                et_add_homepage.setEnabled(true);
                et_add_address.setEnabled(true);
                et_add_scannedDate.setEnabled(true);
//                ll_add_scannedDate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.toString();
        }

    }


    private  void applyColorToFields(View view){
        TextView tv_add_name=(TextView)view.findViewById(R.id.tv_add_name);
        TextView tv_add_company=(TextView)view.findViewById(R.id.tv_add_company);
        TextView tv_add_phno=(TextView)view.findViewById(R.id.tv_add_phno);
        TextView tv_add_email=(TextView)view.findViewById(R.id.tv_add_email);
        TextView tv_add_title=(TextView)view.findViewById(R.id.tv_add_title);
        TextView tv_add_homepage=(TextView)view.findViewById(R.id.tv_add_homepage);
        TextView tv_add_address=(TextView)view.findViewById(R.id.tv_add_address);
        TextView tv_add_hostname=(TextView)view.findViewById(R.id.tv_add_hostname);
        TextView tv_add_purpose=(TextView)view.findViewById(R.id.tv_add_purpose);
        tv_departments = (TextView) view.findViewById(R.id.tv_add_departments);
        tv_add_recurringvisits=(TextView)view.findViewById(R.id.tv_add_recurringvistis);
        tv_add_name.setTextColor(Color.parseColor(primary_color));
        tv_add_company.setTextColor(Color.parseColor(primary_color));
        tv_add_phno.setTextColor(Color.parseColor(primary_color));
        tv_add_email.setTextColor(Color.parseColor(primary_color));
        tv_add_title.setTextColor(Color.parseColor(primary_color));
        tv_add_homepage.setTextColor(Color.parseColor(primary_color));
        tv_add_address.setTextColor(Color.parseColor(primary_color));
        tv_add_hostname.setTextColor(Color.parseColor(primary_color));
        tv_add_purpose.setTextColor(Color.parseColor(primary_color));
        tv_add_recurringvisits.setTextColor(Color.parseColor(primary_color));
        tv_departments.setTextColor(Color.parseColor(primary_color));
    }
    private void getRecurringMeetings(JSONObject dataObj){
        try{
            if(dataObj.has("recurMeetingDetails")){
                JSONArray recurMeetings = dataObj.getJSONArray("recurMeetingDetails");
                for(int i = 0 ; i < recurMeetings.length() ; i++){
                    long startDateTime = ((JSONObject)(recurMeetings.get(i))).getLong("startDateTime");
                    recurring_list.add(Common.getDateTime(startDateTime,dateFormat));
                    recurring_selected.add(Common.getDateTime(startDateTime,dateFormat));
                    meetingId =((JSONObject)(recurMeetings.get(i))).getString("meetingId");
                }
                et_recurring_visits.setText(recurring_list.size() +" Recurring Visits");
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onClick(View v) {

        /*if (v.getId() == R.id.btn_getVisitorDetails) {
            String visitorId = et_visitorID.getText().toString();
            getVisitorDetails(visitorId);
        } else */
        if (v == btn_verify) {
            if (et_add_name.getText().toString().trim().equals("")) {
                et_add_name.requestFocus();
                et_add_name.setError("Please Enter Name:");
//            } else if (et_add_company.getText().toString().trim().equals("")) {
//                et_add_company.requestFocus();
//                et_add_company.setError("Please Enter Company Name");
            } else if (et_add_email.getText().toString().trim().equals("")) {
                et_add_email.requestFocus();
                et_add_email.setError("Please Enter Email");
            } else if (!Common.isValidEmail(et_add_email.getText().toString().trim())) {
                et_add_email.requestFocus();
                et_add_email.setError("Please Enter valid Email");
            } else if (et_host_name.getText().toString().trim().equals("")) {
                et_host_name.requestFocus();
                et_host_name.setError("Please Enter Host Name");
            } else {
                VisitorDetailsForm.vm = new VisitorModel();
                //VisitorDetailsForm.vm.setVisitorId(et_visitorID.getText().toString().trim());
                VisitorDetailsForm.vm.setName(et_add_name.getText().toString().trim());
                VisitorDetailsForm.vm.setCompany(et_add_company.getText().toString().trim());
                VisitorDetailsForm.vm.setPhno(et_add_phno.getText().toString().trim());
                VisitorDetailsForm.vm.setEmail(et_add_email.getText().toString().trim());
                VisitorDetailsForm.vm.setTitle(et_add_title.getText().toString().trim());
                VisitorDetailsForm.vm.setHomePage(et_add_homepage.getText().toString().trim());
                VisitorDetailsForm.vm.setHostName(et_host_name.getText().toString().trim());
                VisitorDetailsForm.vm.setAddress(et_add_address.getText().toString().trim());
                if (!imageFilePath.trim().equals("")) {
                    VisitorDetailsForm.vm.setIsImageTaken(true);
                    VisitorDetailsForm.vm.setImagePath(imageFilePath);
                }
                //Intent intent=new Intent(AddViewVisitor.this.getActivity(),VisitorDetailsDisplay.class);
                //startActivity(intent);
                click();
                //  ((VisitorDetailsForm) getActivity()).viewPager.setCurrentItem(1, true);
            }
        } else if (v.getId() == R.id.et_recurring_visits) {
            //Recurring Visits are going here
            showRecurringVisits();
        } else if (v.getId() == R.id.btn_overRidden) {
            String data = "";
            try {
                JSONObject jobj = new JSONObject(overrideData);
                data = "Name: " + jobj.getString("name") + "\n" + "Email: " + jobj.getString("email") + "\n" + "Mobile No: " + jobj.getString("mobileNo");
            } catch (JSONException j) {
            }
            AlertDialog adb = new AlertDialog.Builder(this.getActivity())
                    .setTitle("Visitor Tracking")
                    .setMessage(data)
                    .setIcon(R.drawable.menu_app_icon)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }

    }

    public void click() {

            if (Common.isNetworkAvailable(AddViewVisitor.this.getActivity())) {
                String server_URL = "";
                if (VisitorDetailsForm.vm.isImageTaken()) {

                    //use image sending
                    final ProgressDialog pDialog = new ProgressDialog(this.getContext());
                    pDialog.setMessage("Uploading data to server...");
                    pDialog.setCancelable(false);
                    if(!pDialog.isShowing())
                    pDialog.show();
                    String result = multipartRequest(APICalls.URL_addvisitorbasicInfo, VisitorDetailsForm.vm.getImagePath(), "image", "image/jpg");
                    try {
                        final JSONObject resultJson = new JSONObject(result);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        if (resultJson.getString("statusCode").trim().equals("200")) {
                            new AlertDialog.Builder(this.getContext())
                                    .setTitle("Visitor Tracking")
                                    .setMessage(resultJson.getString("statusMessage").toString().trim())
                                    .setIcon(R.drawable.menu_app_icon)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                JSONObject json = resultJson.getJSONObject("data");

                                                VisitorDetailsForm.vm.setVisitorId(json.getString("visitorId").toString().trim());
                                                VisitorDetailsForm.vm.setVisitingId(Integer.parseInt(json.getString("visitingId").toString().trim()));
                                                APICalls.vm = VisitorDetailsForm.vm;
                                                Intent intent = new Intent(AddViewVisitor.this.getActivity(), IssueBadge.class);
                                                startActivity(intent);
                                            } catch (JSONException je) {
                                                je.printStackTrace();
                                            }
                                        }
                                    })
                                    .show();
                        } else if (resultJson.getString("statusCode").toString().trim().equals("404")) {
                            Common.gotoLoginPage(this.getActivity());
                        } else {
                            new AlertDialog.Builder(this.getActivity())
                                    .setTitle("Visitor Tracking")
                                    .setMessage(resultJson.getString("statusMessage").toString().trim())
                                    .setIcon(R.drawable.menu_app_icon)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        final ProgressDialog pDialog = new ProgressDialog(this.getActivity());
                        pDialog.setMessage("Uploading data to server...");
                        pDialog.setCancelable(false);
                        if(!pDialog.isShowing())
                        pDialog.show();
                        StringEntity entity = null;
                        JSONObject json = new JSONObject();
                        if (!selected_visitorId.equals("")) {
                            json.put("visitorId", selected_visitorId);
                           // json.put("visitorId",VisitorDetailsForm.vm.getVisitorId());
                            json.put("whomToMeet", et_host_name.getText().toString().trim());
                            json.put("timeZone", TimeZone.getDefault().getID().toString().trim());
                            json.put("purpose", et_visit_purpose.getText().toString().trim());
                            json.put("room", "");
                            json.put("employeeId", "");
                            json.put("meetingId", meetingId/*"0"*/);
                            json.put("recMeetingId", "0");
                            json.put("departmentId", Common.getSelectedDepartmentId(this.getActivity(),spn_departments.getSelectedItem()+"".trim()));
//                                    Common.getDepartmentIdFromLoginData(this.getActivity()).get(spn_departments.getSelectedItemPosition())+"".trim());
                            json.put("centerId",APICalls.getSelectedCenterID(this.getActivity()));
                            VisitorDetailsForm.vm.setDepartmentName_viewVisitor(spn_departments.getSelectedItem().toString().trim());
                            VisitorDetailsForm.vm.setPurpose(et_visit_purpose.getText().toString().trim());
                            server_URL = APICalls.URL_reVerifyVisitor;
                        } else {
                            json.put("name", et_add_name.getText().toString().trim());
                            json.put("mobileNo", et_add_phno.getText().toString().trim());
                            // json.put("workPhone", et_add_phno.getText().toString().trim());
                            json.put("email", et_add_email.getText().toString().trim());
                            json.put("company", et_add_company.getText().toString().trim());
                            json.put("visitorUID", "");
                            json.put("designation", et_add_title.getText().toString().trim());
                            json.put("homePage", et_add_homepage.getText().toString().trim());
                            json.put("whomToMeet", et_host_name.getText().toString().trim());
                            json.put("timeZone", TimeZone.getDefault().getID().toString().trim());
                            json.put("centerId", APICalls.getSelectedCenterID(this.getActivity()));
                            json.put("departmentId", Common.getSelectedDepartmentId(this.getActivity(),spn_departments.getSelectedItem()+"".trim()));
                            json.put("employeeId", "");
                            json.put("meetingId", "0");
                            json.put("recMeetingId", "0");
                            json.put("purpose", et_visit_purpose.getText().toString().trim());
                            VisitorDetailsForm.vm.setDepartmentName_viewVisitor(spn_departments.getSelectedItem().toString().trim());
                            VisitorDetailsForm.vm.setPurpose(et_visit_purpose.getText().toString().trim());
                            json.put("room", "");
                            // json.put("jobTitle", et_add_title.getText().toString().trim());
                            //json.put("address", et_add_address.getText().toString().trim());
                            // json.put("meetingId","0");
                            // json.put("recMeetingId","0");

                            server_URL = APICalls.URL_addvisitorbasicInfoWithoutCardImg;
                        }
                        if (recurring_upload.size()>0) {
                            json.put("isRecursive", "true");
                            JSONArray rec_jsonAry = new JSONArray();
                            for (int i = 0; i < recurring_upload.size(); i++) {
                                JSONObject rec_json = new JSONObject();
                                rec_json.put("startMeetingDateTime", recurring_upload.get(i).toString().trim());
                                rec_json.put("endMeetingDateTime", recurring_upload.get(i).toString().trim());
                                rec_jsonAry.put(rec_json);
                            }
                            json.put("recurringDates", rec_jsonAry);
                        } else {
                            json.put("isRecursive", "false");
                        }
                        System.out.println("json without image::" + json);
                        entity = new StringEntity(json.toString());

                        List<Header> headers = NetworkAdapter.getHeadersWithGovtAPI(this.getActivity());

                        JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  final JSONObject response) {
                                System.out.println("response::::::" + response);
                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                                try {
                                    if (response.getString("statusCode").toString().trim().equals("200")) {

                                        final JSONObject json = response.getJSONObject("data");
                                        if (json.has("isRecordFound")) {
                                            if (json.getString("isRecordFound").toString().trim().equals("true")) {
                                                VisitorDetailsForm.vm.setVisitorId(json.getString("visitorId").toString().trim());
                                                VisitorDetailsForm.vm.setVisitingId(Integer.parseInt(json.getString("visitingId").toString().trim()));
                                                APICalls.vm = VisitorDetailsForm.vm;
                                                new AlertDialog.Builder(AddViewVisitor.this.getActivity())
                                                        .setTitle("Visitor Tracking")
                                                        .setMessage("Details Matched\nDo You Want to Override?")
                                                        .setIcon(R.drawable.menu_app_icon)
                                                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent in = new Intent(AddViewVisitor.this.getActivity(), Overridden.class);
                                                                in.putExtra("name",et_host_name.getText().toString().trim());
                                                                startActivity(in);
                                                            }
                                                        })
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                VisitorDetailsForm.vm.setVisitorId(json.getString("visitorId").toString().trim());
                                                VisitorDetailsForm.vm.setVisitingId(Integer.parseInt(json.getString("visitingId").toString().trim()));
                                                APICalls.vm = VisitorDetailsForm.vm;
                                                Intent in = new Intent(AddViewVisitor.this.getActivity(), IssueBadge.class);
                                                startActivity(in);
                                            }
                                        } else {
                                            new AlertDialog.Builder(AddViewVisitor.this.getActivity())
                                                    .setTitle("Visitor Tracking")
                                                    .setMessage(response.getString("statusMessage").toString().trim())
                                                    .setIcon(R.drawable.menu_app_icon)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            try {
                                                                if (response.getString("statusMessage").
                                                                        contains("Email already exists")) {
                                                                    VisitorDetailsForm.vm.setVisitorId(json.getInt("visitorId")+"");
                                                                    selected_visitorId = json.getInt("visitorId")+"";
                                                                    click();

                                                                }
                                                            }catch (JSONException e){e.printStackTrace();}

                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
                                        }
                                    } else if (response.getString("statusCode").toString().trim().equals("404")) {
                                        Common.gotoLoginPage(AddViewVisitor.this.getActivity());
                                    } else {
                                        new AlertDialog.Builder(AddViewVisitor.this.getActivity())
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

                                if(pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                            }

                        };
                        NetworkAdapter.postWithHttpHeader(AddViewVisitor.this.getActivity(), server_URL,
                                entity, "application/json", reponseHandler, headers);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Common.alertDialog(AddViewVisitor.this.getActivity(), "No internet connection.Please check the internet connection");
            }
        }


    AlertDialog recurring_Visits_Dialog;

    public void showRecurringVisits() {
        final LayoutInflater factory = LayoutInflater.from(getContext());
        recurring_Visits_View = factory.inflate(R.layout.recurring_visits, null);
        recurring_Visits_View.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 380));
        Toolbar toolbar = (Toolbar) recurring_Visits_View.findViewById(R.id.recurring_toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        Button add_btn = (Button) recurring_Visits_View.findViewById(R.id.add);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("add btn", "clicked");
                if(recurring_list!=null && recurring_list.size() > 0){
                    recurring_list.clear();
                    recurring_selected.clear();
                }
                MyDatePicker myDatePicker = new MyDatePicker(context);
            }
        });

        Button done_btn = (Button) recurring_Visits_View.findViewById(R.id.done);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recurring_Visits_View.setVisibility(View.GONE);
                if (recurring_selected != null) {
                    et_recurring_visits.setText(recurring_selected.size() + " Recurring Visits");
                }
                recurring_Visits_Dialog.dismiss();
            }
        });
        recurring_Visits_Dialog = new AlertDialog.Builder(getContext()).create();
        recurring_Visits_Dialog.setView(recurring_Visits_View);
        recurring_Visits_Dialog.show();
        if (recurring_selected != null) {
            updateDisplay(getContext());
        }

    }

    Calendar myCalendar = Calendar.getInstance();

    public class MyDatePicker implements DatePickerDialog.OnDateSetListener {
        Context _context;
        int _year, _month, _date;

        public MyDatePicker(Context ctx) {
            _context = ctx;
            showDatePicker();
        }

        public void showDatePicker() {
            Calendar calendar = Calendar.getInstance();
            _year = calendar.get(Calendar.YEAR);
            _month = calendar.get(Calendar.MONTH);
            _date = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(_context, this, _year, _month, _date);
            //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()/ 86400000L * 86400000L);
            datePickerDialog.show();
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                datePickerDialog.getDatePicker().getCalendarView().setVisibility(View.GONE);
            }
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
            if(datePicker.isShown()) {
                _year = year;
                _month = month;
                _date = date;
                myCalendar.set(Calendar.YEAR, _year);
                myCalendar.set(Calendar.MONTH, _month);
                myCalendar.set(Calendar.DAY_OF_MONTH, _date);
                updateDate();
            }
        }

        public void updateDate() {
            String myFormat = "MMM dd,yyyy "; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            String format = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf1 = new SimpleDateFormat(format, Locale.getDefault());

            MyTimePicker myTimePicker = new MyTimePicker(_context, sdf.format(myCalendar.getTime()),sdf1.format(myCalendar.getTime()));
        }

    }

    public class MyTimePicker implements TimePickerDialog.OnTimeSetListener {
        int _hour, _min, am_pm,_sec;
        Context _context;
        String _dateStr;
        String _dateUploadStr;

        public MyTimePicker(Context context, String date,String uploadDateStr) {
            _context = context;
            _dateStr = date;
            _dateUploadStr=uploadDateStr;
            showTimePicker();
        }

        public void showTimePicker() {
            Calendar cal = Calendar.getInstance();
            _hour = cal.get(Calendar.HOUR_OF_DAY);
            _min = cal.get(Calendar.MINUTE);
            am_pm = cal.get(Calendar.AM_PM);
            _sec=cal.get(Calendar.SECOND);
            TimePickerDialog timePickerDialog = new TimePickerDialog(_context, this, _hour, _min, true);
            timePickerDialog.show();
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            _hour = hour;
            _min = min;
            String hr,minute;
            if(_min < 10){
                minute = "0"+_min;
            }else{
                minute = _min+"";
            }

            if(_hour < 10){
                hr = "0"+_hour;
            }else{
                hr = _hour+"";
            }
            if (!recurring_selected.contains(_dateStr + " " + hr + ":" + minute )) {
                recurring_selected.add(_dateStr + " " + hr + ":" + minute);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                recurring_upload.add(_dateUploadStr+" "+ hr+":"+minute+":"+_sec);
            }
            System.out.println("recurring_upload:::::::"+recurring_upload);
            updateDisplay(_context);
        }
    }

    private void updateDisplay(final Context _context) {
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        LinearLayout linearLayout = (LinearLayout) recurring_Visits_View.findViewById(R.id.ll_visits);
        linearLayout.removeAllViews();
        for (int i = 0; i < recurring_selected.size(); i++) {
            RelativeLayout rl=new RelativeLayout(_context);
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(_context);
            tv.setLayoutParams(params);
            tv.setTextSize(16);
            tv.setPadding(8, 3, 3, 3);
            tv.setId(i);
            tv.setText(recurring_selected.get(i).toString().trim());
            Button btn= new Button(_context);
            btn.setText("Delete");
            btn.setBackgroundColor(Color.WHITE);
            newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            newParams.addRule(RelativeLayout.LEFT_OF, i);
            newParams.rightMargin=80;
            newParams.bottomMargin=20;
            btn.setLayoutParams(newParams);
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recurring_selected.remove(view.getId());
                    updateDisplay(_context);
                }
            });
            rl.addView(tv);
            rl.addView(btn);
            linearLayout.addView(rl);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!VisitorDetailsForm.jdata.trim().equals("")) {
            menu.findItem(R.id.action_camera).setVisible(false);
            if(jsonObject.has("visitorObj")){
                menu.findItem(R.id.action_camera).setVisible(true);
            }

        }else{
            menu.findItem(R.id.action_camera).setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_camera) {
            checkAndRequestPermissions();
        } else if (item.getItemId() == R.id.action_home) {
            Intent in = new Intent(getActivity(), HomeVistorsList.class);
            startActivity(in);
        }

        return false;
    }

    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = getOutputMediaFileUri();
        imageFilePath = mImageCaptureUri.getPath();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 123);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
                if (myBitmap != null) {
                    Bitmap resizedbitmap = Bitmap.createScaledBitmap(myBitmap, 200, 200, true);
                    iv_businessCard_captured.setImageBitmap(resizedbitmap);
                    et_add_name.setText("");
                    et_add_company.setText("");
                    et_add_phno.setText("");
                    et_add_email.setText("");
                    et_add_title.setText("");
                    et_add_homepage.setText("");
                    et_add_address.setText("");
//                    new AsyncProcessTask(getActivity()).execute(imageFilePath, resultUrl);

                    doCrop();//sai
                }

                break;
            case 321:
                Bundle extras = data.getExtras();
                Bitmap photo = null;
                if (extras != null) {
                     photo = extras.getParcelable("data");
                    iv_businessCard_captured.setImageBitmap(photo);

                }
                // delete already existing
                deleteImageFile(imageFilePath);

                //replace cropped image
                if(photo!=null) {
                    saveCroppedImage(photo);
                }
                new AsyncProcessTask(getActivity()).execute(imageFilePath, resultUrl);
                break;


        }
        //System.out.println("Image path:" + imageFilePath);
        //new AsyncProcessTask(this).execute(imageFilePath, resultUrl);
    }

    private void deleteImageFile(String imagePath){
        File file = new File(imagePath);
        if (file.exists()) file.delete();
    }

    private void saveCroppedImage(Bitmap photo){
        try {
            File file = new File(imageFilePath);
            FileOutputStream fOut = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        }catch (IOException e){e.printStackTrace();}
    }


    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        Log.e("size", ":" + list.size());
        if (list.size() == 0) {
            Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
   /*         intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);*/
            intent.putExtra("aspectX", 0);
            intent.putExtra("aspectY", 0);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (list.size() == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, 321);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }
                startActivityForResult(cropOptions.get(0).appIntent, 321);

            }
        }
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = getActivity().openFileInput(resultUrl);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                int i = 0;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(
                            System.getProperty("line.separator"));

                    System.out.println("i=" + i + ":::::::::" + text);
                    updateTextData(text);

                    i++;
                }
            } finally {
                fis.close();
                dialog.dismiss();

            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateTextData(String text) {
        //  NodeList nl = doc.getElementsByTagName("Phone");
        System.out.println("text in OCRSDK::::::"+text);

        if (text.startsWith("N;CHARSET=utf-8")) {
            et_add_name.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("TEL;CHARSET=utf-8")) {
            et_add_phno.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("EMAIL;CHARSET=utf-8:")) {
            et_add_email.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("ORG;CHARSET=utf-8")) {
            et_add_company.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("TITLE;CHARSET=utf-8")) {
            et_add_title.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("URL;CHARSET=utf-8")) {
            et_add_homepage.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
        if (text.startsWith("ADR;CHARSET=utf-8")) {
            et_add_address.setText(text.substring(text.indexOf(":") + 1).replace(';', ' '));
        }
    }

    public String multipartRequest(String urlTo, String filepath, String filefield, String fileMimeType) {
        Log.e("multipartRequest" + filepath, ":" + urlTo);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        System.out.println("urlTo::" + urlTo);

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //-------------headers

            String data = Common.getSavedUserLoginData(getContext());
            JSONObject dataObj = new JSONObject(data).getJSONObject("data");
            connection.setRequestProperty("userId", dataObj.getString("userId"));
            connection.setRequestProperty("logedInUserEmail", dataObj.getString("email"));
            connection.setRequestProperty("securityToken", dataObj.getString("securityToken"));
            connection.setRequestProperty("loggedInOrgId", dataObj.getString("organizationId"));

            //employee id
            connection.setRequestProperty("employeeId", APICalls.employeeID);
            Log.e("loginorg id", ":" + dataObj.getString("organizationId"));
            connection.setRequestProperty("fuzzyLogic", "1");
            connection.setRequestProperty("govAPI", "1");
//            connection.setRequestProperty("siteID", dataobj.getString("siteID"));

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            String key = "visitorData";
            JSONObject json = new JSONObject();
            json.put("name", et_add_name.getText().toString().trim());
            json.put("mobileNo", et_add_phno.getText().toString().trim());
            json.put("workPhone", et_add_phno.getText().toString().trim());
            json.put("email", et_add_email.getText().toString().trim());
            json.put("company", et_add_company.getText().toString().trim());
            json.put("designation", et_add_title.getText().toString().trim());
            json.put("address", et_add_address.getText().toString().trim());
            json.put("homePage", et_add_homepage.getText().toString().trim());
             json.put("whomToMeet", et_host_name.getText().toString().trim());
            json.put("timeZone", TimeZone.getDefault().getID().toString().trim());
            json.put("centerId",APICalls.getSelectedCenterID(this.getActivity()));
            json.put("departmentId", Common.getSelectedDepartmentId(this.getActivity(),spn_departments.getSelectedItem()+"".trim()));
            json.put("meetingId","0");
            json.put("recMeetingId",0);
            json.put("purpose",et_visit_purpose.getText().toString().trim());
            json.put("employeeId", APICalls.employeeID);
            json.put("visitorUID", "");
            json.put("room","");
            if(recurring_upload.size()>0){
                json.put("isRecursive","true");
                JSONArray rec_jsonAry=new JSONArray();
                for (int i=0;i<recurring_selected.size();i++){
                    JSONObject rec_json=new JSONObject();
                    rec_json.put("startMeetingDateTime",recurring_upload.get(i).toString().trim());
                    rec_json.put("endMeetingDateTime",recurring_upload.get(i).toString().trim());
                    rec_jsonAry.put(rec_json);
                }
                json.put("recurringDates",rec_jsonAry);
            }else{
                json.put("isRecursive","false");
            }
            System.out.println("visitor json with image:::" + json);
            String value = json.toString().trim();
            System.out.println("visitor data:::" + value);
            //  while (keys.hasNext()) {
            //  String key = keys.next();
            // String value = parmas.get(key);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(value);
            outputStream.writeBytes(lineEnd);
            //  }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                System.out.println("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            System.out.println("result:::::::" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public interface Listable {
        String getLabel();
    }

    public static class ClickToSelectEditText<T extends Listable> extends android.support.v7.widget.AppCompatEditText {

        List<String> mItems;
        String[] mListableItems;
        CharSequence mHint;

        OnItemSelectedListener<ArrayList<String>> onItemSelectedListener;

        public ClickToSelectEditText(Context context) {
            super(context);

            mHint = getHint();
        }

        public ClickToSelectEditText(Context context, AttributeSet attrs) {
            super(context, attrs);

            mHint = getHint();
        }

        public ClickToSelectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            mHint = getHint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            setFocusable(false);
            setClickable(true);
        }

        public void setItems(List<String> items) {
            this.mItems = items;
            this.mListableItems = new String[items.size()];

            int i = 0;

            for (String item : mItems) {
                mListableItems[i++] = item;
            }

            configureOnClickListener();
        }

        private void configureOnClickListener() {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(mHint);
                    builder.setItems(mListableItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                            setText(mListableItems[selectedIndex]);

                            if (onItemSelectedListener != null) {
                                onItemSelectedListener.onItemSelectedListener(mItems.get(selectedIndex), selectedIndex);
                            }
                        }
                    });
                    builder.setPositiveButton(android.R.string.cancel, null);
                    builder.create().show();
                }
            });
        }

        public void setOnItemSelectedListener(OnItemSelectedListener<ArrayList<String>> onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

        public interface OnItemSelectedListener<T> {
            void onItemSelectedListener(String item, int selectedIndex);
        }
    }

    public class AsyncProcessTask extends AsyncTask<String, String, Boolean> {
        public AsyncProcessTask(Context ctx) {
            if (dialog == null)
                dialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Processing");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String inputFile = params[0];
            String outputFile = params[1];
            try {
                Client restClient = new Client();
                restClient.applicationId = "VisitorTracking";
                restClient.password = "ZLIMMJRpqFGdQ9OJRXEytTWc";

                // Obtain installation id when running the application for the first time
                SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);
                String instIdName = "installationId";
                if (!settings.contains(instIdName)) {
                    // Get installation id from server using device id
                    String deviceId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                            android.provider.Settings.Secure.ANDROID_ID);

                    // Obtain installation id from server
                    publishProgress("First run: obtaining installation id..");
                    String installationId = restClient.activateNewInstallation(deviceId);
                    publishProgress("Done. Installation id is '" + installationId + "'");

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(instIdName, installationId);
                    editor.commit();
                }

                String installationId = settings.getString(instIdName, "");
                restClient.applicationId += installationId;

                publishProgress("Uploading image...");

                String language = "English";
                BusCardSettings busCardSettings = new BusCardSettings();
                busCardSettings.setLanguage(language);
                busCardSettings.setOutputFormat(BusCardSettings.OutputFormat.vCard);
                Task task = restClient.processBusinessCard(inputFile, busCardSettings);
                publishProgress("Uploading..");
                while (task.isTaskActive()) {

                    Thread.sleep(5000);
                    publishProgress("Waiting..");
                    task = restClient.getTaskStatus(task.Id);
                }

                if (task.Status == Task.TaskStatus.Completed) {
                    publishProgress("Downloading..");
                    FileOutputStream fos = getActivity().openFileOutput(outputFile, MODE_PRIVATE);

                    try {
                        restClient.downloadResult(task, fos);
                    } finally {
                        fos.close();
                    }
                    publishProgress("Ready");
                } else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
                    throw new Exception("Not enough credits to process task. Add more pages to your application's account.");
                } else {
                    throw new Exception("Task failed");
                }

                return true;
            } catch (Exception e) {
                final String message = "Error: " + e.getMessage();
                publishProgress(message);
                System.out.println("message::::" + message);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog.dismiss();
            updateResults(result);
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        Common.PERMISSION);
            }
            else{
                captureImage();}
        }else{
            captureImage();
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
            if (requestCode == Common.PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        captureImage();
                    } else {
                        Utils.showToast(getActivity(), "Enable all permissions to enter the app");
                        if (!shouldShowRequestPermissionRationale(permissions[1])) {
                            Common.goToSettings("Enable all permissions to enter the app", getActivity());

                        }
                    }
                } else {
                    Utils.showToast(getActivity(), "Enable all permissions to enter the app");
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        Common.goToSettings("Enable all permissions to enter the app", getActivity());

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
