package com.perigrine.businesscardverification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.perigrine.Helper.Common;
import com.perigrine.Model.VisitorModel;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/3/17.
 */
public class VisitorDetailsForm extends AppCompatActivity {

    public static ViewPager viewPager;
    public static VisitorModel vm;
    public static String jdata = "";
    String primary_color;
    String secondary_color;
    String logo_url;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vistor_details);
        SharedPreferences sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        secondary_color = sharedPreferences.getString("secondary_Color", "#aaaaaa");
        logo_url = sharedPreferences.getString("org_Logo","");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_visitor_details));
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

        if(!logo_url.isEmpty()){
            Picasso.with(VisitorDetailsForm.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }

        jdata = getIntent().getStringExtra("jdata");

        System.out.println("jdata is:::" + jdata);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (true) {//need to change the condition
            getMenuInflater().inflate(R.menu.menu_addvisitor, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_home, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    Uri mImageCaptureUri;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_camera) {


        } else if (item.getItemId() == R.id.action_home) {
            Intent in = new Intent(VisitorDetailsForm.this, HomeVistorsList.class);
            startActivity(in);
        }
        return false;
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddViewVisitor(), "BasicDetails");
        //adapter.addFragment(new VisitorWhomToMeetDetails(), "WhomToMeet");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}