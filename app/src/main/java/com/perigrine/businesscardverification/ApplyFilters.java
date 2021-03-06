package com.perigrine.businesscardverification;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.squareup.picasso.Picasso;
import com.perigrine.Helper.CustomDatePicker;
import com.perigrine.Interfaces.DateSetCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplyFilters extends AppCompatActivity implements View.OnClickListener{
    String primary_color;
    String secondary_color;
    String logo_url;

    private String[] quickFilterRadioBtnName = new String[]{"All", "Filter by Overridden", "Filter by Matches Found", "Filter by Issued", "Filter by Verified", "Filter by Red Badge", "Filter by Blue Badge"};
    private RadioGroup quickFilterRadioGroup;
    private RadioButton[] quickRadioButtons = new RadioButton[quickFilterRadioBtnName.length];
    private TextView sortByFilter, advancedFilter;
    private RadioButton rb_sort_nameAsc, rb_sort_nameDsc, rb_sort_companyAsc, rb_sort_companyDsc, rb_sort_dateAsc, rb_sort_dateDsc;

    private Button btn_AdvancedFilter_Apply;
    private ImageView dateStartTmage, dateEndImage;
    private static boolean flag;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applyfilters);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color = sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo", "");
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(primary_color));

        Button clearButton = (Button) findViewById(R.id.button_clear_sort);

        findViews();
        updateQuickFilters();
        if (APICalls.filterPosition != 99) {
            quickRadioButtons[APICalls.filterPosition].setChecked(true);
        } else {
            for (int i = 0; i < quickRadioButtons.length; i++) {
                quickRadioButtons[i].setChecked(false);
            }
        }

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICalls.masterResetFilter = true;
                APICalls.filterRequesKey = null;
                APICalls.advanceFilterJsonRequest = null;
                APICalls.filterPosition = 0;
                APICalls.sortPosition = 5;
                APICalls.sortStr = "Date ASC";
                sortByFilter.setText(APICalls.sortStr);
                quickRadioButtons[APICalls.filterPosition].setChecked(true);
            }
        });

        sortByFilter.setText(APICalls.sortStr);
    }

    @Override
    public void onClick(View v) {
        if (v == sortByFilter) {
            showSortDialog();
        } else if (v == advancedFilter) {
            showAdvanceFiltersDialog();
        }
    }

    private void findViews() {
        try {
            quickFilterRadioGroup = (RadioGroup) findViewById(R.id.quick_filter_radioGroup);
            sortByFilter = (TextView) findViewById(R.id.sort_by_filter);
            sortByFilter.setOnClickListener(this);

            advancedFilter = (TextView) findViewById(R.id.advanced_filter);
            advancedFilter.setOnClickListener(this);

            layout_toolbar = findViewById(R.id.layout_toolbar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(Color.parseColor(primary_color));
            setSupportActionBar(toolbar);
            imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
            imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
            textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
            textView_toolbar_title.setText(getResources().getString(R.string.title_activity_apply_filters));
            imageView_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();;
                }
            });

            if(!logo_url.isEmpty()){
                Picasso.with(ApplyFilters.this)
                        .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
            }


        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultSort(){
        if (APICalls.sortPosition == 1) {
            rb_sort_nameAsc.setChecked(true);
            APICalls.sortStr = "Name ASC";
        } else if (APICalls.sortPosition == 2) {
            rb_sort_nameDsc.setChecked(true);
            APICalls.sortStr = "Name DSC";
        } else if (APICalls.sortPosition == 3) {
            rb_sort_companyAsc.setChecked(true);
            APICalls.sortStr = "Company ASC";
        } else if (APICalls.sortPosition == 4) {
            rb_sort_companyDsc.setChecked(true);
            APICalls.sortStr = "Company DSC";
        } else if (APICalls.sortPosition == 5) {
            rb_sort_dateAsc.setChecked(true);
            APICalls.sortStr = "Date ASC";
        } else if (APICalls.sortPosition == 6) {
            rb_sort_dateDsc.setChecked(true);
            APICalls.sortStr = "Date DSC";
        }
    }

    private void showSortDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.sort, null);
        final AlertDialog sortDialog = new AlertDialog.Builder(this).create();
        sortDialog.setCancelable(false);
        sortDialog.setView(deleteDialogView);

        AppBarLayout appBarLayout = (AppBarLayout) deleteDialogView.findViewById(R.id.appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(primary_color));

        Button btn_sort_apply = (Button) deleteDialogView.findViewById(R.id.button_apply_sort);
        btn_sort_apply.setBackgroundColor(Color.parseColor(primary_color));

        Button btn_sort_cancel = (Button) deleteDialogView.findViewById(R.id.button_cancel_sort);
        btn_sort_cancel.setBackgroundColor(Color.parseColor(primary_color));

        Button btn_sort_clear = (Button) deleteDialogView.findViewById(R.id.button_clear_sort);
        btn_sort_clear.setBackgroundColor(Color.TRANSPARENT);

        rb_sort_nameAsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByAscendingName);
        rb_sort_nameDsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByDescendingName);
        rb_sort_companyAsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByAscendingCompany);
        rb_sort_companyDsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByDescendingCompany);
        rb_sort_dateAsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByAscendingDate);
        rb_sort_dateDsc = (RadioButton) deleteDialogView.findViewById(R.id.radioSortByDescendingDate);

        setDefaultSort();

        btn_sort_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICalls.masterResetFilter = false;
                APICalls.sortPosition = 5;
                APICalls.sortStr = "Date ASC";
                sortByFilter.setText(APICalls.sortStr);
                rb_sort_dateAsc.setChecked(true);
            }
        });

        btn_sort_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultSort();
                sortDialog.dismiss();
            }
        });

        btn_sort_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_sort_nameAsc.isChecked()) {
                    APICalls.sortPosition = 1;
                    APICalls.sortStr = "Name ASC";

                    if (APICalls.filterPosition != 99) {
                        quickRadioButtons[APICalls.filterPosition].setChecked(true);
                    } else {
                        for (int i = 0; i < quickFilterRadioBtnName.length; i++) {
                            quickRadioButtons[i].setChecked(false);
                        }
                    }
                } else if (rb_sort_nameDsc.isChecked()) {
                    APICalls.sortPosition = 2;
                    APICalls.sortStr = "Name DSC";
                } else if (rb_sort_companyAsc.isChecked()) {
                    APICalls.sortPosition = 3;
                    APICalls.sortStr = "Company ASC";
                } else if (rb_sort_companyDsc.isChecked()) {
                    APICalls.sortPosition = 4;
                    APICalls.sortStr = "Company DSC";
                } else if (rb_sort_dateAsc.isChecked()) {
                    APICalls.sortPosition = 5;
                    APICalls.sortStr = "Date ASC";
                } else if (rb_sort_dateDsc.isChecked()) {
                    APICalls.sortPosition = 6;
                    APICalls.sortStr = "Date DSC";
                }
                sortDialog.dismiss();
                Intent homeScreen = new Intent(ApplyFilters.this, HomeVistorsList.class);
                APICalls.masterResetFilter = false;
                setResult(APICalls.RESULT_CODE_FOR_SORTING, homeScreen);
                finish();
            }
        });
        sortDialog.show();
    }

    private void updateQuickFilters() {
        quickFilterRadioGroup.removeAllViews();
        for (int i = 0; i < quickRadioButtons.length; i++) {
            quickRadioButtons[i] = new RadioButton(this);
            quickRadioButtons[i].setText(quickFilterRadioBtnName[i]);
            quickRadioButtons[i].setPadding(5, 5, 5, 5);
            quickRadioButtons[i].setTextColor(Color.parseColor("#000000"));
            quickRadioButtons[i].setId(i);

            quickRadioButtons[i].setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int buttonViewID = buttonView.getId();
                    try {
                        if (APICalls.filterPosition != buttonViewID) {
                            if (isChecked) {
                                if (buttonViewID == 0) {
                                    APICalls.filterRequesKey = null;
                                    APICalls.filterPosition = 0;
                                } else if (buttonViewID == 1) {
                                    APICalls.filterRequesKey = "overridden";
                                    APICalls.filterPosition = 1;
                                } else if (buttonViewID == 2) {
                                    APICalls.filterRequesKey = "recordMatched";
                                    APICalls.filterPosition = 2;
                                } else if (buttonViewID == 3) {
                                    APICalls.filterRequesKey = "issued";
                                    APICalls.filterPosition = 3;
                                } else if (buttonViewID == 4) {
                                    APICalls.filterRequesKey = "verified";
                                    APICalls.filterPosition = 4;
                                } else if (buttonViewID == 5) {
                                    APICalls.filterRequesKey = "badgeType";
                                    APICalls.filterPosition = 5;
                                } else if (buttonViewID == 6) {
                                    APICalls.filterRequesKey = "badgeType";
                                    APICalls.filterPosition = 6;
                                }
                                Intent homeScreen = new Intent(ApplyFilters.this, HomeVistorsList.class);
                                setResult(APICalls.RESULT_CODE_FOR_FILTER, homeScreen);
                                finish();
                                APICalls.masterResetFilter = false;
                                APICalls.advanceFilterJsonRequest = null;
                            }

                        }
                    } catch (Exception je) {
                        je.printStackTrace();
                    }
                }
            });
            quickFilterRadioGroup.addView(quickRadioButtons[i]);
        }
    }

    private void showAdvanceFiltersDialog() {
        LayoutInflater factory = LayoutInflater.from(this);

        final View advanceFilterDialog = factory.inflate(R.layout.advanced_filters, null);
        final AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setCancelable(false);
        filterDialog.setView(advanceFilterDialog);

        AppBarLayout appBarLayout = (AppBarLayout) advanceFilterDialog.findViewById(R.id.appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(primary_color));

        Button btn_sort_cancel = (Button) advanceFilterDialog.findViewById(R.id.button_cancel_sort);
        btn_sort_cancel.setBackgroundColor(Color.parseColor(primary_color));

        Button btn_sort_clear = (Button) advanceFilterDialog.findViewById(R.id.button_clear_sort);
        btn_sort_clear.setBackgroundColor(Color.TRANSPARENT);

        Button btn_AdvancedFilter_Apply = (Button) advanceFilterDialog.findViewById(R.id.button_AdvancedFilter_apply);
        btn_AdvancedFilter_Apply.setBackgroundColor(Color.parseColor(primary_color));

        // Get all EditText

        final EditText et_filter_name = (EditText) advanceFilterDialog.findViewById(R.id.advancedFilter_name);
        et_filter_name.setBackground(APICalls.getDrawable(et_filter_name, primary_color));

        final EditText et_filter_company = (EditText) advanceFilterDialog.findViewById(R.id.advancedFilter_companyName);
        et_filter_company.setBackground(APICalls.getDrawable(et_filter_company, primary_color));

        final EditText et_filter_startDate = (EditText) advanceFilterDialog.findViewById(R.id.advancedFilter_startDate);
        et_filter_startDate.setBackground(APICalls.getDrawable(et_filter_startDate, primary_color));

        final EditText et_filter_endDate = (EditText) advanceFilterDialog.findViewById(R.id.advancedFilter_endDate);
        et_filter_endDate.setBackground(APICalls.getDrawable(et_filter_endDate, primary_color));

        final EditText et_filter_whomToMeet = (EditText) advanceFilterDialog.findViewById(R.id.advancedFilter_whomeMeet);
        et_filter_whomToMeet.setBackground(APICalls.getDrawable(et_filter_whomToMeet, primary_color));

        // Get all TextView in order to set primary color

        TextView tv_filter_name = (TextView) advanceFilterDialog.findViewById(R.id.tv_filter_name);
        tv_filter_name.setTextColor(Color.parseColor(primary_color));

        TextView tv_filter_companyName = (TextView) advanceFilterDialog.findViewById(R.id.tv_filter_companyName);
        tv_filter_companyName.setTextColor(Color.parseColor(primary_color));

        TextView tv_filter_startDate = (TextView) advanceFilterDialog.findViewById(R.id.tv_filter_startDate);
        tv_filter_startDate.setTextColor(Color.parseColor(primary_color));

        TextView tv_filter_endDate = (TextView) advanceFilterDialog.findViewById(R.id.tv_filter_endDate);
        tv_filter_endDate.setTextColor(Color.parseColor(primary_color));

        final TextView tv_filter_whomToMeet = (TextView) advanceFilterDialog.findViewById(R.id.tv_filter_whomToMeet);
        tv_filter_whomToMeet.setTextColor(Color.parseColor(primary_color));

        //set already saved data
        if(APICalls.advanceFilterJsonRequest!=null){
            if(APICalls.advanceFilterJsonRequest.has("name")){
                et_filter_name.setText(APICalls.advanceFilterJsonRequest.optString("name"));
            }
            if(APICalls.advanceFilterJsonRequest.has("company")){
                et_filter_company.setText(APICalls.advanceFilterJsonRequest.optString("company"));
            }
            if(APICalls.advanceFilterJsonRequest.has("startDate")){
                String startDate = APICalls.advanceFilterJsonRequest.optString("startDate");
                if(!startDate.equals(""))
                et_filter_startDate.setText(startDate.substring(0, startDate.indexOf(' ')));
            }
            if(APICalls.advanceFilterJsonRequest.has("endDate")){
                String endDate = APICalls.advanceFilterJsonRequest.optString("endDate");
                if(!endDate.equals(""))
                et_filter_endDate.setText(endDate.substring(0, endDate.indexOf(' ')));
            }
            if(APICalls.advanceFilterJsonRequest.has("whomToMeet")){
                et_filter_whomToMeet.setText(APICalls.advanceFilterJsonRequest.optString("whomToMeet"));
            }
        }



        ImageView dateStartImage = (ImageView) advanceFilterDialog.findViewById(R.id.startDateImageBtn);
        dateStartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker customDatePicker = new CustomDatePicker(ApplyFilters.this, new DateSetCallback() {
                    @Override
                    public void onDateReady(String year, String month, String day) {
                        String date = day+"-"+month+"-"+year;
                        et_filter_startDate.setText(date);
                    }
                });
                customDatePicker.onCreateDialog();
            }
        });

        ImageView dateEndImage = (ImageView) advanceFilterDialog.findViewById(R.id.endDateImageBtn);
        dateEndImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker customDatePicker = new CustomDatePicker(ApplyFilters.this, new DateSetCallback() {
                    @Override
                    public void onDateReady(String year, String month, String day) {
                        String date = day+"-"+month+"-"+year;
                        et_filter_endDate.setText(date);
                    }
                });
                customDatePicker.onCreateDialog();
            }
        });

        btn_sort_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APICalls.masterResetFilter = false;
                et_filter_name.setText("");
                et_filter_company.setText("");
                et_filter_startDate.setText("");
                et_filter_endDate.setText("");
                et_filter_whomToMeet.setText("");
            }
        });

        btn_sort_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        // Set on click listener on Apply_Button for filtering
        btn_AdvancedFilter_Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!et_filter_startDate.getText().toString().trim().equals("")) {

                        if(et_filter_endDate.getText().toString().trim().equals("")){
                            Toast.makeText(ApplyFilters.this, "Please enter End Date", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                            Date startDate = simpleDateFormat.parse(et_filter_startDate.getText().toString().trim());
                            Date endDate = simpleDateFormat.parse(et_filter_endDate.getText().toString().trim());
                            if (startDate.after(endDate)) {
                                Toast.makeText(ApplyFilters.this, "Start Date should not be greater than End Date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    JSONObject advancedFilterRequest = new JSONObject();
                    advancedFilterRequest.put("name", et_filter_name.getText().toString().trim());
                    advancedFilterRequest.put("company", et_filter_company.getText().toString().trim());

                    if(!et_filter_startDate.getText().toString().trim().equals("")) {
                        advancedFilterRequest.put("startDate", et_filter_startDate.getText().toString().trim() + " 00:00:00");
                        advancedFilterRequest.put("endDate", et_filter_endDate.getText().toString().trim() + " 23:59:00");
                    } else {
                        advancedFilterRequest.put("startDate", et_filter_startDate.getText().toString().trim());
                        advancedFilterRequest.put("endDate", et_filter_endDate.getText().toString().trim());
                    }

                    advancedFilterRequest.put("whomToMeet", et_filter_whomToMeet.getText().toString().trim());
                    APICalls.advanceFilterJsonRequest = advancedFilterRequest;
                    APICalls.filterPosition = 99;

                    //Reste quick filter
                    APICalls.filterRequesKey = null;
                    APICalls.filterPosition = 0;
                    APICalls.masterResetFilter = false;
                    // -------- //
                    filterDialog.dismiss();
                    Intent homeScreen = new Intent(ApplyFilters.this, HomeVistorsList.class);
                    setResult(APICalls.RESULT_CODE_FOR_FILTER, homeScreen);
                    finish();
                } catch (ParseException pe) {
                    pe.printStackTrace();
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        });
        filterDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_resetFilter){
            APICalls.masterResetFilter = false;
            APICalls.filterRequesKey = null;
            APICalls.advanceFilterJsonRequest = null;
            APICalls.filterPosition = 0;
            APICalls.sortPosition = 5;
            APICalls.sortStr = "Date ASC";
            sortByFilter.setText(APICalls.sortStr);
            quickRadioButtons[APICalls.filterPosition].setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }
}