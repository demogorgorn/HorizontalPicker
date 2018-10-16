package com.github.jhonnyx2012.horizontalpicker;


import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jhonn on 22/02/2017.
 */

public class HorizontalPickerAdapter extends RecyclerView.Adapter<HorizontalPickerAdapter.ViewHolder> {

    public static final int MODE_HIDE_DISABLED_DAYS = 0;
    public static final int MODE_GREY_DISABLED_DAYS = 1;


    private static final long DAY_MILLIS = AlarmManager.INTERVAL_DAY;
    private final int mBackgroundColor;
    private final int mDateSelectedTextColor;
    private final int mDateSelectedColor;
    private final int mTodayDateTextColor;
    private final int mTodayDateBackgroundColor;
    private final int mDayOfWeekTextColor;
    private final int mUnselectedDayTextColor;
    private int itemWidth;
    private final OnItemClickedListener listener;
    private ArrayList<Day> items;
    private ArrayList<String> enabledDays;
    private final int mEnabledMode;
    private final DateTime mStartDate;
    private final DateTime mEndDate;
    

    public HorizontalPickerAdapter(int itemWidth, OnItemClickedListener listener, Context context, int daysToCreate, int offset, ArrayList<String> enabledDays, int enabledMode, DateTime startDate, DateTime endDate, int mBackgroundColor, int mDateSelectedColor, int mDateSelectedTextColor, int mTodayDateTextColor, int mTodayDateBackgroundColor, int mDayOfWeekTextColor, int mUnselectedDayTextColor) {
        items=new ArrayList<>();
        this.itemWidth=itemWidth;
        this.listener=listener;
        this.enabledDays=enabledDays;
        this.mEnabledMode = enabledMode;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        if (mStartDate != null && mEndDate != null)
            generateSpecifiedDays();
        else
            generateDays(daysToCreate,new DateTime().minusDays(offset).getMillis(),false);
        this.mBackgroundColor=mBackgroundColor;
        this.mDateSelectedTextColor=mDateSelectedTextColor;
        this.mDateSelectedColor=mDateSelectedColor;
        this.mTodayDateTextColor=mTodayDateTextColor;
        this.mTodayDateBackgroundColor=mTodayDateBackgroundColor;
        this.mDayOfWeekTextColor=mDayOfWeekTextColor;
        this.mUnselectedDayTextColor=mUnselectedDayTextColor;
    }

    public  void generateSpecifiedDays() {

        DateTime date = mStartDate;
        DateTime currentDate = new DateTime();
        boolean enabledDaysMode = enabledDays.size() > 0;

        for (int e = 8; e > 1; e++ ) {
            items.add(new Day(date.minusDays(e)));
        }

        while(date.getMillis() <= mEndDate.getMillis())
        {
            if (enabledDaysMode) {
                //add offset

                    Day day = new Day(date);

                    if (mEnabledMode == MODE_GREY_DISABLED_DAYS) {
                        if (!enabledDays.contains(date.toString("dd.MM.YYYY")))
                            day.setVisible(false);
                        items.add(day);
                    } else {
                        if (enabledDays.contains(date.toString("dd.MM.YYYY")))
                            items.add(day);
                    }




            } else
                items.add(new Day(date));

            date.plusDays(1);
        }

        if (enabledDaysMode) {
            if (items.size() > 0) {

                for (int e = 1; e < 8; e++ ) {
                    items.add(new Day(mEndDate.plusDays(e)));

                }
            }
        }

    }

    public  void generateDays(int n, long initialDate, boolean cleanArray) {
        if(cleanArray)
            items.clear();
        int i=0;
        DateTime currentDate = new DateTime();
        boolean enabledDaysMode = enabledDays.size() > 0;



        while(i<n)
        {
            DateTime actualDate = new DateTime(initialDate + (DAY_MILLIS * i++));

            if (enabledDaysMode) {
                //add offset
                if (actualDate.getMillis() < currentDate.getMillis())
                    items.add(new Day(actualDate));
                else if (actualDate.getMillis() >= currentDate.getMillis()) {
                    Day day = new Day(actualDate);

                    if (mEnabledMode == MODE_GREY_DISABLED_DAYS) {
                        if (!enabledDays.contains(actualDate.toString("dd.MM.YYYY")))
                            day.setVisible(false);
                        items.add(day);
                    } else {
                        if (enabledDays.contains(actualDate.toString("dd.MM.YYYY")))
                            items.add(day);
                    }


                }

            } else
                items.add(new Day(actualDate));
        }

        if (enabledDaysMode) {
            if (items.size() > 0) {
                DateTime lastDay = items.get(items.size() - 1).getDate();
                for (int e = 1; e < 8; e++ ) {
                    items.add(new Day(lastDay.plusDays(e)));
                    
                }
            }
        }
        
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_day,parent,false));
    }

    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day item=getItem(position);

        if (mEnabledMode == MODE_GREY_DISABLED_DAYS) {
            if (!item.isVisible())
                setViewAndChildrenEnabled(holder.base, false);
                //holder.base.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));
            else
                setViewAndChildrenEnabled(holder.base, true);
            //holder.base.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        holder.tvDay.setText(item.getDay());
        holder.tvWeekDay.setText(item.getWeekDay());
        holder.tvWeekDay.setTextColor(mDayOfWeekTextColor);
        if(item.isSelected())
        {
            holder.tvDay.setBackgroundDrawable(getDaySelectedBackground(holder.itemView));
            holder.tvDay.setTextColor(mDateSelectedTextColor);
        }
        else if(item.isToday())
        {
            holder.tvDay.setBackgroundDrawable(getDayTodayBackground(holder.itemView));
            holder.tvDay.setTextColor(mTodayDateTextColor);
        }
        else
        {
            holder.tvDay.setBackgroundColor(mBackgroundColor);
            holder.tvDay.setTextColor(mUnselectedDayTextColor);
        }
    }

    private Drawable getDaySelectedBackground(View view) {
        Drawable drawable=view.getResources().getDrawable(R.drawable.background_day_selected);
        DrawableCompat.setTint(drawable,mDateSelectedColor);
        return drawable;
    }

    private Drawable getDayTodayBackground(View view) {
        Drawable drawable=view.getResources().getDrawable(R.drawable.background_day_today);
        if(mTodayDateBackgroundColor!=-1)
            DrawableCompat.setTint(drawable,mTodayDateBackgroundColor);
        return drawable;
    }

    public Day getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getAdapterPositionForDate(DateTime date) {

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getDate().toString("dd.MM.yyyy").equals(date.toString("dd.MM.yyyy")))
                return i;
        }

        return -1;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDay,tvWeekDay;
        LinearLayout base;

        public ViewHolder(View itemView) {
            super(itemView);
            base = (LinearLayout) itemView.findViewById(R.id.base);
            tvDay= (TextView) itemView.findViewById(R.id.tvDay);
            tvDay.setWidth(itemWidth);
            tvWeekDay= (TextView) itemView.findViewById(R.id.tvWeekDay);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickView(v,getAdapterPosition());
        }
    }
}