package com.perigrine.Helper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;

import com.perigrine.Model.VisitorModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by deepika on 11/4/16.
 */

public class APICalls {

    private static String testURL = "http://192.168.0.17:8080/BusinessCard/";
    private static String liveURL = "http://192.168.0.21:8080/BusinessCard";
    private static String prathapLocalURL = "http://192.168.0.102:8080/SAASBCNEW";
    private static String devURL = "http://192.168.2.98:8080/BusinessCard";

    private static String localURL = "http://192.168.0.102:8080/SAASBCNEW";
    private static String localURL1 = "http://192.168.3.174:8085/BusinessCardDeV";
    private static String localURL2 = "http://192.168.2.102:8085/BusinessCardDeV";

    /*----------------------URL's------------------------------*/
    public static String serverURL = devURL;
    public static String URL_issueBadgeWithImage = serverURL + "/card/issueBadgeWithImage";
    public static String URL_Login = serverURL + "/user/login";
    public static String URL_UserLogout = serverURL +"/user/logout";//user logout
    public static String URL_Registration = serverURL + "/user/createRegistration";
    public static String URL_getAbbyyOcrDetails = serverURL + "/user/getAbbyyOcrDetails";
    //public static String URL_getVisotorsList = serverURL + "/card/getVisitorListUpdate";
    public static String URL_getVisitorsList = serverURL + "/card/getVisitorDetailsByCenterId";
    public static String URL_verifyCardDetailsWithOutCardImg = serverURL + "/card/verifyCardDetailsWithOutCardImg";
    public static String URL_verifyCardDetails = serverURL + "/card/verifyCardDetails";
    public static String URL_addOverrideDetails = serverURL + "/card/addOverrideDetails";
    public static String URL_issueBadge = serverURL + "/card/issueBadge";
    public static String URL_visitorOutTime = serverURL + "/card/visitorOutTime";
    public static String URL_reVerifyVisitor = serverURL + "/card/reVerifyVisitor";
    public static String URL_findAllbyCenterID = serverURL + "/department/findAllbyCenetrID";//getDepartments by centerID
    public static String URL_getEmployeeData = serverURL + "/card/getEmployeeData";//getEmployee list
    public static String URL_getEmployeeWithID = serverURL + "/card/getEmloyeeWithID";//getEmployee details by ID
    public static String URL_getVisitorWithID = serverURL +"/visitor/getVisitorDetailsByUID";//getVisitor details by ID
    public static String URL_getOrgCutsomFields = serverURL +"/organization/getOrganisationCustFields";//get Org custom fields
    public static String URL_addvisitorbasicInfoWithoutCardImg = serverURL +"/card/addVistiorDetailsWithOutCardImg";//Adding basic info without card Img
    public static String URL_addvisitorbasicInfo = serverURL +"/card/addVistiorDetails";//Adding basic info
    public static String URL_organizationLogo = serverURL +"/organization/getOrganizationWithID";//organization logo
    public static String URL_VisitorLogout = serverURL +"/card/visitorOutTime";//visitor logout
    //public static String URL_getVisitorsList = serverURL + "/card/getVisitorDetailsByCenterId";
    /*
        meetings api calls
     */
    public static String URL_MeetingByEmail = serverURL +"/meeting/getMeetingDetailsByEmail";//meeting details by email
    public static String URL_MeetingDetails = serverURL +"/meeting/getMeetingDetailsByTodaysDate";//today's meeting details

    public static String employeeID="";
    public static VisitorModel vm;
    public static String filterRequesKey = "issued";
    public static JSONObject advanceFilterJsonRequest = null;
    public static int filterPosition = 3;
    public static boolean isOldFilter = false;
    public static int sortPosition = 5;
    public static String sortStr = "Date ASC";
    public static int REQUEST_CODE_FOR_SORT_FILTER = 111;
    public static int RESULT_CODE_FOR_SORTING = 222;
    public static int RESULT_CODE_FOR_FILTER = 333;
    public static boolean masterResetFilter = false;
    //logs enabled or not
    public static boolean loggable = true;
    static ArrayList<String> centerBaseddepartmentsIDList = new ArrayList<String>();
    static ArrayList<String> centerBaseddepartmentsNameList = new ArrayList<String>();
    static ArrayList<String> employeeNameList = new ArrayList<String>();
    static ArrayList<String> employeeIDList = new ArrayList<String>();

    public static void setSelectedCenter(Context ctx, String centerName) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("selected_center_name", centerName);
        editor.commit();
    }

    public static void setSelectedCenterID(Context ctx, String centerID) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("selected_center_ID", centerID);
        editor.commit();
    }

    public static String getSelectedCenter(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        return sharedpreferences.getString("selected_center_name", "");
    }

    public static String getSelectedCenterID(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        return sharedpreferences.getString("selected_center_ID", "");
    }

    public static void setDeptIDList(Context ctx,ArrayList<String> departmentIDlist) {
        centerBaseddepartmentsIDList = departmentIDlist;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        //Set the values
        Set<String> set = new HashSet<String>();
        set.addAll(departmentIDlist);
        editor.putStringSet("centerBaseddepartmentsIDs", set);
        editor.commit();
    }

    public static void setDeptNameList(Context ctx,ArrayList<String> departmentNamelist) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        centerBaseddepartmentsNameList = departmentNamelist;
        //Set the values
        Set<String> set = new HashSet<String>();
        set.addAll(departmentNamelist);
        editor.putStringSet("centerBaseddepartmentsNames", set);
        editor.commit();
    }

    public static ArrayList<String> getcenterBasedDeptIDList(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet("centerBaseddepartmentsIDs", null);
        ArrayList<String> sample=new ArrayList<String>(set);
        return sample;
    }

    public static ArrayList<String> getcenterBasedDeptNameList(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet("centerBaseddepartmentsNames", null);
        ArrayList<String> sample=new ArrayList<String>(set);
        return sample;
    }

    public static void setSelectedDepartmentID(Context ctx, String selectedDepartmentID) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("selected_department_ID", selectedDepartmentID);
        editor.commit();
    }

    public static String getSelectedDepartmentID(Context ctx) {
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("first_prefs", Context.MODE_PRIVATE);
        return sharedpreferences.getString("selected_department_ID", "");
    }

    //Storing the employee data
    /*public static void setEmployeeListData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray dataObject = jsonObject.getJSONArray("data");
            employeeIDList = new ArrayList<String>();
            employeeNameList = new ArrayList<String>();
            for (int i = 0; i < dataObject.length(); i++) {
                JSONObject jobj = dataObject.getJSONObject(i);
                employeeIDList.add(jobj.getString("id"));
                employeeNameList.add(jobj.getString("empName"));
            }
            Log.e("empid list" + employeeIDList.size(), "employeename list" + employeeNameList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getEmployeeNameList() {
        return employeeNameList;
    }

    public static ArrayList<String> getEmployeeIDList() {
        return employeeIDList;
    }*/

    public static Drawable getDrawable(EditText et, String color) {
        Drawable dr = et.getBackground();
        dr.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
        return dr;
    }
}

