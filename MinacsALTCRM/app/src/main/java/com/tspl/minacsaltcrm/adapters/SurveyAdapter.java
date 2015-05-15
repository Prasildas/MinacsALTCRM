package com.tspl.minacsaltcrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.Ratings;
import com.tspl.minacsaltcrm.ClassObject.SurveyItem;
import com.tspl.minacsaltcrm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by venupai on 23/4/15.
 */
public class SurveyAdapter extends BaseAdapter {
    List<Ratings> ratingsList;

    private List<SurveyItem> surveyList;
    LayoutInflater inflater;
    Context context;

    public SurveyAdapter(Context context) {
        this.context = context;
        initDatas();
        inflater = LayoutInflater.from(context);
        ratingsList = new ArrayList<Ratings>();
    }

    public void setSurveyList(List<SurveyItem> surveyList) {
        this.surveyList = new ArrayList<SurveyItem>(surveyList);
    }

    public void initDatas() {
        surveyList = new ArrayList<SurveyItem>();
    }


    @Override
    public int getCount() {
        return surveyList.size();
    }

    @Override
    public Object getItem(int position) {
        return surveyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.survey_list_element, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.survey_list_image);
//        TextView title = (TextView) convertView.findViewById(R.id.survey_list_title);
        TextView subtitle = (TextView) convertView.findViewById(R.id.survey_list_subtitle);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);


        subtitle.setText(surveyList.get(position).question);
        try {
            ratingBar.setRating(Float.parseFloat(surveyList.get(position).rating + ""));
        } catch (Exception e) {

        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int ratingVal = Math.round(rating);
                surveyList.get(position).rating = ratingVal;
            }
        });
        String img = surveyList.get(position).id + ".png";
        int getID = surveyList.get(position).id;
        switch (getID) {
            case 1:
                imageView.setImageResource(R.drawable.survey_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.survey_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.survey_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.survey_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.survey_5);
                break;

        }
        return convertView;
    }

    /**
     * fetches the rating list
     * @return
     */
    public List<Ratings> getRatingsList() {

        for (int i = 0; i < surveyList.size(); ++i) {
            Ratings ratings = new Ratings();
            ratings.id = surveyList.get(i).id;
            ratings.rating = surveyList.get(i).rating;
            ratingsList.add(ratings);
        }
        return ratingsList;
    }
}
