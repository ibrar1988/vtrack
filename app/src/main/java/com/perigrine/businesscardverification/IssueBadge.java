package com.perigrine.businesscardverification;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Extras.Utils;
import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.Helper.NetworkAdapter;
import com.perigrine.Model.VisitorModel;
import com.squareup.picasso.Picasso;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class IssueBadge extends AppCompatActivity implements View.OnClickListener {
    String primary_color;
    String secondary_color;
    String logo_url;
    private EditText et_badgeID;
    private Button btn_scan, btn_issueBadge;
    private TextView tv_scanLabel;
    private ImageView iv_scan;
    private String imagePath = "";
    private Context mContext;
    TextView textView_red, textView_blue;
    boolean isRedChecked = true;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ABBYY Cloud OCR SDK Demo App");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ScanImage.jpg");
        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_badge);
        mContext = this;
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color = sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo", "");
        findViews();
        Common.clearErrorMask(et_badgeID);
        blueChecked();
    }

    private void findViews() {
        et_badgeID = (EditText) findViewById(R.id.et_badgeID);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_issueBadge = (Button) findViewById(R.id.btn_issueBadge);
        btn_issueBadge.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        tv_scanLabel = (TextView) findViewById(R.id.tv_scanLabel);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        textView_red = (TextView) findViewById(R.id.textView_red);
        textView_blue = (TextView) findViewById(R.id.textView_blue);

        btn_scan.setBackgroundColor(Color.parseColor(primary_color));
        btn_issueBadge.setBackgroundColor(Color.parseColor(primary_color));

        textView_red.setOnClickListener(this);
        textView_blue.setOnClickListener(this);
        Common.clearErrorMask(et_badgeID);

        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_issue_badge));
        imageView_back.setVisibility(View.GONE);

        if(!logo_url.isEmpty()){
            Picasso.with(IssueBadge.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }

    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, 123);
    }

    private void sendImageToServer() {
        {
            //send image using multipart
            //  try {
//                                    JSONObject json = new JSONObject();
//                                    json.put("visitorId", APICalls.vm.getVisitorId());
//                                    json.put("badgeType", "Blue");
//                                    json.put("timeZone", TimeZone.getDefault().getID());
//                                    json.put("badgeId", et_badgeID.getText().toString().trim());


            final ProgressDialog pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Uploading data to server...");
            pDialog.setCancelable(false);
            if (!pDialog.isShowing()) {
                pDialog.show();
            }
            multipartRequest(APICalls.URL_issueBadgeWithImage, imagePath, "image", "image/jpg", new DataListener() {
                @Override
                public void onDataReady(String data) {
                    try {
                        final JSONObject resultJson = new JSONObject(data);
                        pDialog.dismiss();
                        if (resultJson.getString("statusCode").trim().equals("200")) {
                           /* IssueBadge.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        new AlertDialog.Builder(IssueBadge.this)
                                                .setTitle("Business card Verification")
                                                .setMessage(resultJson.getString("statusMessage").toString().trim())
                                                .setIcon(R.drawable.menu_app_icon)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(IssueBadge.this, HomeVistorsList.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .show();

                                    } catch
(JSONException je) {
                                        je.printStackTrace();
                                    }
                                }
                            });*/
                            Intent in = new Intent(IssueBadge.this, PrintActivity.class);

                            /*if (isRedChecked) {
                                in.putExtra("badgeColor", "Red");
                            } else {
                                in.putExtra("badgeColor", "Blue");
                            }*/
                            startActivity(in);
                        } else if (resultJson.getString("statusCode").toString().trim().equals("404")) {
                            IssueBadge.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Common.gotoLoginPage(IssueBadge.this);
                                }
                            });
                        } else {
                            IssueBadge.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        new AlertDialog.Builder(IssueBadge.this)
                                                .setTitle("Visitor Tracking")
                                                .setMessage(resultJson.getString("statusMessage").toString().trim())
                                                .setIcon(R.drawable.menu_app_icon)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .show();
                                    } catch (JSONException je) {
                                        je.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {

                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void validate() {
        if (et_badgeID.getText().toString().trim().equals("")) {
            et_badgeID.setError("Enter Badge Id");
        }else if(imagePath.equals("")){
            Toast.makeText(this, "Please capture Visitor Image to proceed...", Toast.LENGTH_SHORT).show();
        } else {
            if (isRedChecked) {
                sendBadgeDetails();
            } else {
                if (iv_scan.getVisibility() == View.VISIBLE) {
                    if (imagePath.trim().equals("")) {
                        sendBadgeDetails();
                    } else {
                        sendImageToServer();
                    }
                } else {
                    sendBadgeDetails();
                }
            }
        }
    }

    private void redChecked() {
        isRedChecked = true;
        tv_scanLabel.setVisibility(View.GONE);
        btn_scan.setVisibility(View.GONE);
        iv_scan.setVisibility(View.GONE);

        textView_red.setTextColor(getResources().getColor(android.R.color.white));
        textView_red.setBackground(getResources().getDrawable(R.drawable.red_filled_rectangle));

        textView_blue.setTextColor(getResources().getColor(R.color.blue));
        textView_blue.setBackground(getResources().getDrawable(R.drawable.blue_empty_rectangle));
    }

    private void blueChecked() {
        isRedChecked = false;
        tv_scanLabel.setVisibility(View.GONE);
        btn_scan.setVisibility(View.VISIBLE);
        iv_scan.setVisibility(View.VISIBLE);
        textView_red.setTextColor(getResources().getColor(R.color.appred));
        textView_red.setBackground(getResources().getDrawable(R.drawable.red_empty_rectangle));

        textView_blue.setTextColor(getResources().getColor(android.R.color.white));
        textView_blue.setBackground(getResources().getDrawable(R.drawable.blue_filled_rectangle));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_scan:
                checkAndRequestPermissions();
                break;
            case R.id.btn_issueBadge:
                validate();
                break;
            case R.id.textView_red:
                redChecked();
                break;
            case R.id.textView_blue:
                blueChecked();
                break;
        }


    }

    private void sendBadgeDetails() {
        try {
            StringEntity entity = null;
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            if (!pDialog.isShowing()) {
                pDialog.show();
            }
            JSONObject json = new JSONObject();
            json.put("visitingId", APICalls.vm.getVisitingId());
            json.put("timeZone", TimeZone.getDefault().getID());
            json.put("badgeId", et_badgeID.getText().toString().trim());
            System.out.println("Json:::::" + json);
            if (isRedChecked) {
                json.put("badgeType", "Red");
            } else {
                json.put("badgeType", "Blue");
            }
            System.out.println("Json:::::" + json);
            entity = new StringEntity(json.toString());
            List<Header> headers = NetworkAdapter.getHeaders(this);
            JsonHttpResponseHandler reponseHandler = new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      JSONObject response) {
                    System.out.println("response in signout time:::" + response);
                    try {
                        if (response.getString("statusCode").toString().trim().equals("200")) {
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                           /* new AlertDialog.Builder(IssueBadge.this)
                                    .setTitle("Business card Verification")
                                    .setMessage(response.getString("statusMessage").toString().trim())
                                    .setIcon(R.drawable.menu_app_icon)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent in = new Intent(IssueBadge.this, HomeVistorsList.class);
                                            startActivity(in);
                                        }
                                    })
                                    .show();*/
                            Intent in = new Intent(IssueBadge.this, PrintActivity.class);
                          /*  if (isRedChecked) {
                                in.putExtra("badgeColor", "Red");
                            } else {
                                in.putExtra("badgeColor", "Blue");
                            }*/
                            startActivity(in);
                        } else if (response.getString("statusCode").toString().trim().equals("404")) {
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            Common.gotoLoginPage(IssueBadge.this);
                        } else {
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            new AlertDialog.Builder(IssueBadge.this)
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
                        if (pDialog.isShowing()) {
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

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
            };
            if (Common.isNetworkAvailable(IssueBadge.this)) {
                NetworkAdapter.postWithHttpHeader(IssueBadge.this, APICalls.URL_issueBadge,
                        entity, "application/json", reponseHandler, headers);
            } else {
                Common.alertDialog(IssueBadge.this, "No internet connection.Please check the internet connection");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        imagePath = "";
        switch (requestCode) {
            case 123:
                try {
                    imagePath = getOutputMediaFileUri().getPath();

                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        FileOutputStream fOut = new FileOutputStream(imgFile);
                        //Bitmap resizedbitmap = Bitmap.createScaledBitmap(myBitmap, 150, 100, true);
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        iv_scan.setImageBitmap(myBitmap);
                        iv_scan.setVisibility(View.VISIBLE);
                        System.out.println("imagePath::::" + imagePath);
                    }
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
                break;
        }
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

    private void multipartRequest(final String urlTo, final String filepath, final String filefield, final String fileMimeType, final DataListener dataListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.e("multipartRequest" + filepath, ":" + urlTo);
                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;
                String twoHyphens = "--";
                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                String lineEnd = "\r\n";
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

                    String data = Common.getSavedUserLoginData(mContext);
                    JSONObject dataObj = new JSONObject(data).getJSONObject("data");
                    connection.setRequestProperty("userId", dataObj.getString("userId"));
                    connection.setRequestProperty("logedInUserEmail", dataObj.getString("email"));
                    connection.setRequestProperty("securityToken", dataObj.getString("securityToken"));
                    connection.setRequestProperty("loggedInOrgId", dataObj.getString("organizationId"));


                    //connection.setRequestProperty("fuzzyLogic", "1");
                    //connection.setRequestProperty("govAPI", "1");
                    //connection.setRequestProperty("siteID", dataobj.getString("siteID"));

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
                    JSONObject json = new JSONObject();
                    json.put("visitingId", APICalls.vm.getVisitingId());
                    json.put("badgeType", "Blue");
                    json.put("timeZone", TimeZone.getDefault().getID());
                    json.put("badgeId", et_badgeID.getText().toString().trim());
                    String key = "badgeData";
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

                    String resultData = convertStreamToString(inputStream);

                    fileInputStream.close();
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("result:::::::" + resultData);
                    dataListener.onDataReady(resultData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private interface DataListener {
        void onDataReady(String data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(IssueBadge.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        Common.PERMISSION);
            } else {
                captureImage();
            }
        } else {
            captureImage();
        }
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == Common.PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        captureImage();
                    } else {
                        Utils.showToast(IssueBadge.this, "Enable all permissions to enter the app");
                        if (!shouldShowRequestPermissionRationale(permissions[1])) {
                            Common.goToSettings("Enable all permissions to enter the app", IssueBadge.this);

                        }
                    }
                } else {
                    Utils.showToast(IssueBadge.this, "Enable all permissions to enter the app");
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        Common.goToSettings("Enable all permissions to enter the app", IssueBadge.this);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

