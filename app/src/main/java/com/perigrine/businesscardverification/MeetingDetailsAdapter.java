package com.perigrine.businesscardverification;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.perigrine.Extras.Utils;
import com.perigrine.Model.MeetingDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ragamai on 8/9/17.
 */
public class MeetingDetailsAdapter extends RecyclerView.Adapter<MeetingDetailsAdapter.ViewHolder> {

    Context context;
    List<MeetingDetails> meetingDetailsList;
    JSONArray meetings;


    public MeetingDetailsAdapter(Context context, List<MeetingDetails> meetingDetailsList, JSONArray meetings) {
        this.context = context;
        this.meetingDetailsList = meetingDetailsList;
        this.meetings = meetings;
    }

    @Override
    public MeetingDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeetingDetailsAdapter.ViewHolder holder, final int position) {
        try {
            MeetingDetails meetingDetails = meetingDetailsList.get(position);
                if (meetingDetails.getVisitorObj().getName() != null) {
                    holder.textView_name.setText(meetingDetails.getVisitorObj().getName());
                }
                if (meetingDetails.getTitle() != null) {
                    holder.textView_title.setText(meetingDetails.getTitle());
                }
                if (meetingDetails.getOrganizerEmail() != null) {
                    holder.textView_email.setText(meetingDetails.getOrganizerEmail());
                }
                if (meetingDetails.getStartTime() != 0) {
                    holder.textView_time.setText(Utils.convertToDate(meetingDetails.getStartTime()));
                }

                holder.imageView_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //visitor tracking details navigation
                        try {
                            Intent intent = new Intent(context, VisitorDetailsForm.class);
                            final JSONObject jdata = (JSONObject) meetings.get(position);
                            intent.putExtra("jdata", jdata.toString().trim());
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return meetingDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView textView_title, textView_email, textView_time, textView_name;
        ImageView imageView_add;

        public ViewHolder(View view) {
            super(view);
            textView_name = (TextView)view.findViewById(R.id.textView_name);
            textView_title = (TextView)view.findViewById(R.id.textView_title);
            textView_email = (TextView)view.findViewById(R.id.textView_email);
            textView_time = (TextView)view.findViewById(R.id.textView_time);
            imageView_add = (ImageView) view.findViewById(R.id.imageView_add);
        }



    }
}
