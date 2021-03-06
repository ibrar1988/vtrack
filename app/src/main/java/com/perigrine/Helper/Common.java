package com.perigrine.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.widget.EditText;

import com.perigrine.Extras.AlertDialogClass;
import com.perigrine.Interfaces.AlertCallBack;
import com.perigrine.Model.VisitorModel;
import com.perigrine.businesscardverification.LoginActivity;
import com.perigrine.businesscardverification.R;
import com.perigrine.preferences.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ibrar on 14/09/17.
 */

public class Common {

    private Common() { /* cannot be instantiated */ }

    public static int PERMISSION = 10;
    private static ProgressDialog progressDialog;
    static AppPreferences preferences;
    public static Drawable organization_logo;

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences("Pref-Values", Context.MODE_PRIVATE);
    }

    public static boolean isFirstTimeLaunch(Context ctx) {
        return Common.getPreference(ctx).getBoolean("launchType", false);
    }

    public static void setLaunchType(Context ctx) {
        SharedPreferences.Editor editor = Common.getPreference(ctx).edit();
        editor.putBoolean("launchType", true);
        editor.commit();
    }

    public static void saveUserLoginData(Context context, String userLoginData) {
        SharedPreferences.Editor editor = Common.getPreference(context).edit();
        editor.putString("userLoginData", userLoginData);
        editor.commit();
    }

    public static String getSavedUserLoginData(Context context) {
        String result = "";
        try {
            result = Common.getPreference(context).getString("userLoginData", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void saveDepartmentsData(Context context, String departmentsData) {
        SharedPreferences.Editor editor = Common.getPreference(context).edit();
        editor.putString("departments", departmentsData);
        editor.commit();
    }

    public static String getDepartmentsData(Context context) {
        String result = "";
        try {
            result = Common.getPreference(context).getString("departments", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String getSecurityToken(Context context) {
        String token = "";
        String loginData = Common.getSavedUserLoginData(context);
        try {
            token = new JSONObject(loginData).getJSONObject("data").getString("securityToken");

        } catch (JSONException je) {
            je.printStackTrace();
        }
        return token;
    }

    public static JSONArray getCenterIdFromLoginData(Context context) {
        JSONArray arrayOfCenterId = new JSONArray();
        String loginData = Common.getSavedUserLoginData(context);
        try {
            JSONArray centerArray = new JSONObject(loginData).getJSONObject("data").getJSONArray("centers");
            for (int i = 0; i < centerArray.length(); i++) {
                String centerId = centerArray.getJSONObject(i).getString("CenterId");
                arrayOfCenterId.put(Integer.parseInt(centerId));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return arrayOfCenterId;
    }

    public static ArrayList<String> getAllCenterNames(Context context) throws JSONException {
        ArrayList<String> centerList = new ArrayList<>();
        String loginData = Common.getSavedUserLoginData(context);
        JSONArray centerArray = new JSONObject(loginData).getJSONObject("data").getJSONArray("centers");
        for (int i = 0; i < centerArray.length(); i++) {
            centerList.add(centerArray.getJSONObject(i).getString("CenterName"));
        }
        return centerList;
    }

    /*public static JSONArray getDepartmentIdFromLoginData(Context context) {
        JSONArray arrayOfDepartmentId = new JSONArray();
        String loginData = Common.getSavedUserLoginData(context);

        try {
            JSONArray DeptArray = new JSONObject(loginData).getJSONObject("data").getJSONArray("departments");
            for (int i = 0; i < DeptArray.length(); i++) {
                String deptId = DeptArray.getJSONObject(i).getString("DepartmentId");
                arrayOfDepartmentId.put(Integer.parseInt(deptId));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return arrayOfDepartmentId;
    }
*/
    public static String getSelectedDepartmentId(Context context, String departmentName){
        String departmentsData = Common.getDepartmentsData(context);
        String deptId = "";
        try{
            JSONArray DeptArray = new JSONObject(departmentsData).getJSONArray("data");
            for(int i = 0 ; i < DeptArray.length() ; i++){
                if(departmentName.equalsIgnoreCase(DeptArray.getJSONObject(i).getString("departmentName"))){
                    deptId = DeptArray.getJSONObject(i).getString("id");
                    break;
                }
            }

        }catch (Exception e){e.printStackTrace();}
        return deptId;
    }

    public static ArrayList<String> getDepartmentNameFromLoginData(Context context) {
        ArrayList<String> arrName = new ArrayList();
//        String loginData = Common.getSavedUserLoginData(context);
        String departmentData = Common.getDepartmentsData(context);

        try {
            JSONArray DeptArray = new JSONObject(departmentData).getJSONArray("data");
            for (int i = 0; i < DeptArray.length(); i++) {
                String deptName = DeptArray.getJSONObject(i).getString("departmentName");
                arrName.add(deptName);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return arrName;
    }

    public static void setCredentials(Context ctx, String credentials) {
        SharedPreferences.Editor editor = Common.getPreference(ctx).edit();
        editor.putString("credentials", credentials);
        editor.commit();
    }

    public static String getCredentials(Context ctx) {
        String result = "";
        return result = Common.getPreference(ctx).getString("credentials", "");
    }

    // Sorting ArrayList of visitors using Comparator interface

    public static ArrayList<JSONObject> applySorting(JSONObject responseObj) {
        ArrayList<JSONObject> array = new ArrayList<>();
        JSONArray jsonArray;

        try {
            jsonArray = responseObj.getJSONArray("Data");

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    array.add(jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (APICalls.sortPosition == 1 || APICalls.sortPosition == 2) {
                Collections.sort(array, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("name").trim().toLowerCase();
                            String st2 = rhs.getString("name").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 2) {
                    Collections.reverse(array);
                }
            } else if (APICalls.sortPosition == 3 || APICalls.sortPosition == 4) {
                Collections.sort(array, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("company").trim().toLowerCase();
                            String st2 = rhs.getString("company").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 4) {
                    Collections.reverse(array);
                }
            } else if (APICalls.sortPosition == 5 || APICalls.sortPosition == 6) {
                Collections.sort(array, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("visitedDateTime_interval").trim().toLowerCase();
                            String st2 = rhs.getString("visitedDateTime_interval").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 5) {
                    Collections.reverse(array);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

    public static void setOrganization_NewTheme(Context ctx, JSONObject loginResponse) {
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences("new_theme", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONObject data = null;
            if (loginResponse.has("data"))
                data = loginResponse.getJSONObject("data");
            if (data != null && data.has("organizationPrimaryColor")) {
                String primary_color = data.getString("organizationPrimaryColor");
                if (primary_color == null || primary_color == "null")
                    editor.putString("primary_Color", "#F00025");
                else
                    editor.putString("primary_Color", primary_color);
            }
            if (data != null && data.has("organizationSecondaryColor")) {
                String secondary_color = data.getString("organizationSecondaryColor");
                if (secondary_color == null || secondary_color == "null")
                    editor.putString("secondary_Color", "#aaaaaa");
                else
                    editor.putString("secondary_Color", secondary_color);
            }
            if (data != null && data.has("organizationLogo")) {
                String org_logo = data.getString("organizationLogo");
                if (org_logo == null || org_logo == "null")
                    editor.putString("org_Logo", "");
                else
                    drawableFromUrl(org_logo.replaceAll(" ","%20"),ctx);
                    editor.putString("org_Logo", org_logo.replaceAll(" ","%20"));
            }
            editor.commit();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private static void setOrganization_logo(Drawable logo){
        organization_logo = logo;
    }

//    public static Drawable getOrganization_logo(){
//        return organization_logo;
//    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void alertDialog(Context ctx, String message) {
        new AlertDialog.Builder(ctx)
                .setTitle("Visitor Tracking")
                .setMessage(message)
                .setIcon(R.drawable.menu_app_icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public static void gotoLoginPage(final Context ctx) {
        new AlertDialog.Builder(ctx)
                .setTitle("Visitor Tracking")
                .setMessage("Your account is already logged on at another location")
                .setIcon(R.drawable.menu_app_icon)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        preferences = new AppPreferences(ctx);
                        preferences.setToken("");
                        Intent intent = new Intent(ctx, LoginActivity.class);
                        ctx.startActivity(intent);
                        ((Activity) ctx).finish();
                    }
                }).show();
    }

    public static void clearErrorMask(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.setError(null);
            }
        });
    }

    public static String getDevicetoken(Context ctx) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getImageUrl(String path) {
        String s = "'\'";
        return path.replaceAll(s, "");
    }

    public static List<VisitorModel> sortAdapterDate(List<VisitorModel> adapterData) {
        //List<VisitorModel> newData = new ArrayList<>();

        if (APICalls.sortPosition == 1 || APICalls.sortPosition == 2) {
            Collections.sort(adapterData, new Comparator<VisitorModel>() {
                @Override
                public int compare(VisitorModel lhs, VisitorModel rhs) {
                    try {
                        String st1 = lhs.getName().trim().toLowerCase();
                        String st2 = rhs.getName().trim().toLowerCase();
                        return (st1.compareTo(st2));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            if (APICalls.sortPosition == 2) {
                Collections.reverse(adapterData);
            }
        } else if (APICalls.sortPosition == 3 || APICalls.sortPosition == 4) {
            Collections.sort(adapterData, new Comparator<VisitorModel>() {
                @Override
                public int compare(VisitorModel lhs, VisitorModel rhs) {
                    try {
                        String st1 = lhs.getCompany().trim().toLowerCase();
                        String st2 = rhs.getCompany().trim().toLowerCase();
                        return (st1.compareTo(st2));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            if (APICalls.sortPosition == 4) {
                Collections.reverse(adapterData);
            }
        } else if (APICalls.sortPosition == 5 || APICalls.sortPosition == 6) {
            Collections.sort(adapterData, new Comparator<VisitorModel>() {
                @Override
                public int compare(VisitorModel lhs, VisitorModel rhs) {
                    try {
                        String st1 = lhs.getSignInTime().trim().toLowerCase();
                        String st2 = rhs.getSignInTime().trim().toLowerCase();
                        return (st1.compareTo(st2));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            if (APICalls.sortPosition == 5) {
                Collections.reverse(adapterData);
            }
        }

        return adapterData;
    }

    public static ArrayList<JSONObject> sortSecondData(ArrayList<JSONObject> secondData) {

        try {

            if (APICalls.sortPosition == 1 || APICalls.sortPosition == 2) {
                Collections.sort(secondData, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("name").trim().toLowerCase();
                            String st2 = rhs.getString("name").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 2) {
                    Collections.reverse(secondData);
                }
            } else if (APICalls.sortPosition == 3 || APICalls.sortPosition == 4) {
                Collections.sort(secondData, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("company").trim().toLowerCase();
                            String st2 = rhs.getString("company").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 4) {
                    Collections.reverse(secondData);
                }
            } else if (APICalls.sortPosition == 5 || APICalls.sortPosition == 6) {
                Collections.sort(secondData, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            String st1 = lhs.getString("visitedDateTime_interval").trim().toLowerCase();
                            String st2 = rhs.getString("visitedDateTime_interval").trim().toLowerCase();
                            return (st1.compareTo(st2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                if (APICalls.sortPosition == 5) {
                    Collections.reverse(secondData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return secondData;

    }

    public static void goToSettings(String message, final Context context) {

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

    }

    public static void showProgressDialog(Context context) {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getCurrentDateTime(String dateFormat){
        // "MMM dd, yyyy hh:mm:ss z";
        return DateFormat.format(dateFormat, System.currentTimeMillis()).toString();
    }

    public static String getDateTime(long millis, String dateFormat){
        return DateFormat.format(dateFormat, millis).toString();
    }

    static Bitmap x;
    static Drawable drawable = null;
    public static Drawable drawableFromUrl(final String url, Context context) {
        if (isNetworkAvailable(context)) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();

                        x = BitmapFactory.decodeStream(input);
                        drawable = new BitmapDrawable(x);
                    } catch (IOException e) {
                        e.printStackTrace();
                        drawable = null;
                    }
                    setOrganization_logo(drawable);
                }
            }).start();
        }
        return drawable;
    }

}
