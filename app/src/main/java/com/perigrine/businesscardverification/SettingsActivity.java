package com.perigrine.businesscardverification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.perigrine.Helper.APICalls;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by root on 21/12/16.
 */
public class SettingsActivity extends AppCompatActivity {
    private ListView lv_Departments;
    private TextView tv_logout;
    ArrayList<String> department_list;
    ArrayList<String> departmentID_list;
    String primary_color;
    String secondary_color;
    String logo_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        //Back Button for toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.backnav));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked

                finish();
            }
        });
        findViews();
    }

    private void findViews() {
        lv_Departments= (ListView) findViewById(R.id.lv_departments);
        department_list=new ArrayList<String>();
        departmentID_list=new ArrayList<String>();
        try {
            department_list = APICalls.getcenterBasedDeptNameList(SettingsActivity.this);
            departmentID_list = APICalls.getcenterBasedDeptIDList(SettingsActivity.this);
        }catch (Exception e)
        {
            e.toString();
        }
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, department_list);
//        SingleListAdapter adapter= new SingleListAdapter(this,department_list);

        lv_Departments.setAdapter(adapter);
        lv_Departments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_Departments.setSelection(departmentID_list.indexOf(APICalls.getSelectedDepartmentID(SettingsActivity.this)));
        lv_Departments.performItemClick(lv_Departments,departmentID_list.indexOf(APICalls.getSelectedDepartmentID(SettingsActivity.this)),12323);
        lv_Departments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    APICalls.setSelectedDepartmentID(SettingsActivity.this,departmentID_list.get(i));
                Log.e("settings",":"+APICalls.getSelectedDepartmentID(SettingsActivity.this));

            }
        });



        tv_logout= (TextView) findViewById(R.id.action_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Visitor Tracking")
                        .setMessage("Do you want logout from the application...!")
                        .setIcon(R.drawable.menu_app_icon)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }
}
