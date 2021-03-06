package com.perigrine.Helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.perigrine.Model.VisitorModel;
import com.perigrine.businesscardverification.HomeVistorsList;
import com.perigrine.businesscardverification.R;
import com.perigrine.businesscardverification.VisitorDetailsForm;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Ibrar on 12/09/17.
 */

public class VisitorListAdapter extends BaseAdapter implements Filterable{

    String logo_url = "";
    private Context mContext;
    private List<VisitorModel> mVisitorList;
    private List<VisitorModel> backupList;
    private int displaySize = 0;
    private ArrayList<JSONObject> jData;
    private boolean canDisplayLogout = false;

    //Constructor

    public VisitorListAdapter(Context mContext, String url, List<VisitorModel> list, ArrayList<JSONObject> jdata) {
        this.backupList = list;
        this.mContext = mContext;
        this.logo_url = url;
        this.mVisitorList = list;
        this.jData = jdata;
    }

    public void changeList(List<VisitorModel> list) {
        this.mVisitorList = list;
        this.notifyDataSetChanged();
    }

    public void setDisplayCount(int numberOfEntries) {
        displaySize = numberOfEntries;
        notifyDataSetChanged();
    }

    public int getAdapterSize() {
        return mVisitorList.size();
    }

    public List<VisitorModel> getAdapterData() {
        return mVisitorList;
    }

    public ArrayList<JSONObject> getSecondData(){
        return jData;
    }

    public void setSecondData(ArrayList<JSONObject> secondData){
        jData = null;
        jData = secondData;
        notifyDataSetChanged();
    }

    public int getDisplaySize() {
        return displaySize;
    }

    @Override
    public int getCount() {
        if (displaySize > mVisitorList.size()){
            return mVisitorList.size();
        } else{
            return displaySize;
        }
    }

    @Override
    public Object getItem(int position) {
        return mVisitorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(mContext, R.layout.visitor_item, null);

        TextView tv_display_name = (TextView) view.findViewById(R.id.tv_display_name);
        TextView tv_display_email = (TextView) view.findViewById(R.id.tv_display_email);
        TextView tv_display_company = (TextView) view.findViewById(R.id.tv_display_company);
        TextView tv_display_designation = (TextView) view.findViewById(R.id.tv_display_designation);
        TextView tv_display_signin = (TextView) view.findViewById(R.id.tv_display_signin);
        TextView tv_display_signout = (TextView) view.findViewById(R.id.textView_outTime);
        TextView tv_card_status = (TextView) view.findViewById(R.id.tv_card_status);
        final ImageView iv_display_card = (ImageView) view.findViewById(R.id.iv_display_card);
        final ImageView imageView_logout = (ImageView) view.findViewById(R.id.imageView_logout);
        TextView tv_badge = (TextView) view.findViewById(R.id.tv_badge);

        final String url = mVisitorList.get(position).getIdCardImage();
        if (!url.equals(""))
            Picasso.with(mContext).load(url).placeholder(R.drawable.default_card).into(iv_display_card);
        tv_display_name.setText(mVisitorList.get(position).getName());
        tv_display_email.setText(mVisitorList.get(position).getEmail());
        tv_display_company.setText(mVisitorList.get(position).getCompany());
        tv_display_designation.setText(mVisitorList.get(position).getDesignation());
        tv_display_signin.setText(mVisitorList.get(position).getSignInTime());
        tv_display_signout.setText(mVisitorList.get(position).getSignOutTime());

        String visitingId = mVisitorList.get(position).getVisitorId();
        imageView_logout.setTag(visitingId);

        try {
            JSONObject jsonObject = jData.get(position);
            String card_status;
            if (jsonObject.get("isVerified").toString().trim().equals("true")) {
                card_status = "Verified";
                canDisplayLogout = false;
            } else if (jsonObject.get("isIssued").toString().trim().equals("true")) {
                canDisplayLogout = true;
                if (jsonObject.get("isOverridden").toString().trim().equals("true")) {
                    card_status = "Overridden and Issued";
                } else {
                    card_status = "Issued";
                }
            } else if (jsonObject.get("isOverridden").toString().trim().equals("true")) {
                canDisplayLogout = false;
                card_status = "Overridden";
            } else if (jsonObject.get("isRecordMatched").toString().trim().equals("true")) {
                canDisplayLogout = false;
                card_status = "Matches Found";
            } else {
                canDisplayLogout = false;
                card_status = "Unknown Status";
            }
            tv_card_status.setText(card_status);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (canDisplayLogout) {
            if (!mVisitorList.get(position).getSignOutTime().trim().equals("")) {
                tv_display_signout.setVisibility(View.VISIBLE);
                tv_display_signout.setText(mVisitorList.get(position).getSignOutTime());
                imageView_logout.setVisibility(View.GONE);
            } else {
                tv_display_signout.setText("");
                tv_display_signout.setVisibility(View.GONE);
                imageView_logout.setVisibility(View.VISIBLE);
            }
        } else {
            imageView_logout.setVisibility(View.GONE);
            if (!mVisitorList.get(position).getSignOutTime().trim().equals("")) {
                tv_display_signout.setVisibility(View.VISIBLE);
                tv_display_signout.setText(mVisitorList.get(position).getSignOutTime());
            }
        }

        if (!mVisitorList.get(position).getBadgeId().toString().trim().equals("")) {
            tv_badge.setText("Badge No: " + mVisitorList.get(position).getBadgeId());
        } else {
            tv_badge.setText("");
        }

        iv_display_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View alertLayout = inflater.inflate(R.layout.dialog_image, null);
                    ImageView imageView=(ImageView)alertLayout.findViewById(R.id.iv_dialog);
                    if (!url.equals("")) {
                        Picasso.with(mContext).load(url).placeholder(R.drawable.default_card).resize(320, 300).into(imageView);
                    } else {
                        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_card));
                    }
                    new AlertDialog.Builder(mContext)
                            .setView(alertLayout)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VisitorDetailsForm.class);
                final JSONObject jdata = jData.get(position);
                intent.putExtra("jdata", jdata.toString().trim());
                mContext.startActivity(intent);
            }
        });

        imageView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int visitingId = mVisitorList.get(position).getVisitingId();
                if (mContext instanceof HomeVistorsList) {
                    ((HomeVistorsList) mContext).visitorLogout(visitingId);
                    Log.i("visitor ID", visitingId + "");
                }

            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mVisitorList = (List<VisitorModel>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<VisitorModel> FilteredArrList = new ArrayList<>();

                if (mVisitorList == null) {
                    mVisitorList = new ArrayList<>();
                }

                if (constraint == null || constraint.length() == 0 || constraint == "") {
                    // set the Original result to return
                    results.count = backupList.size();
                    results.values = backupList;

                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < backupList.size(); i++) {
                        String searchName = backupList.get(i).getName();
                        String companyName = backupList.get(i).getCompany();
                        if (searchName.toLowerCase().contains(constraint) || companyName.toLowerCase().trim().contains(constraint)) {
                            FilteredArrList.add(new VisitorModel(backupList.get(i).getName(), backupList.get(i).getEmail(),
                                    backupList.get(i).getCompany(), backupList.get(i).getDesignation(), backupList.get(i).getSignInTime(),
                                    backupList.get(i).getSignOutTime(), backupList.get(i).getCardStatus(), backupList.get(i).getIdCardImage(),
                                    backupList.get(i).getBadgeId(), backupList.get(i).getVisitingId()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
