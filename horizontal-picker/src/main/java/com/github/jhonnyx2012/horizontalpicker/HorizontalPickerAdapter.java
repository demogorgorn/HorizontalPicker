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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jhonn on 22/02/2017.
 */

public class HorizontalPickerAdapter extends RecyclerView.Adapter<HorizontalPickerAdapter.ViewHolder> {

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
    

    public HorizontalPickerAdapter(int itemWidth, OnItemClickedListener listener, Context context, int daysToCreate, int offset, ArrayList<String> enabledDays, int mBackgroundColor, int mDateSelectedColor, int mDateSelectedTextColor, int mTodayDateTextColor, int mTodayDateBackgroundColor, int mDayOfWeekTextColor, int mUnselectedDayTextColor) {
        items=new ArrayList<>();
        this.itemWidth=itemWidth;
        this.listener=listener;
        this.enabledDays=enabledDays;
        generateDays(daysToCreate,new DateTime().minusDays(offset).getMillis(),false);
        this.mBackgroundColor=mBackgroundColor;
        this.mDateSelectedTextColor=mDateSelectedTextColor;
        this.mDateSelectedColor=mDateSelectedColor;
        this.mTodayDateTextColor=mTodayDateTextColor;
        this.mTodayDateBackgroundColor=mTodayDateBackgroundColor;
        this.mDayOfWeekTextColor=mDayOfWeekTextColor;
        this.mUnselectedDayTextColor=mUnselectedDayTextColor;
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
                    if (!enabledDays.contains(actualDate.toString("dd.MM.YYYY"))) {
                        day.setVisible(false);
                    }
                    items.add(day);
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day item=getItem(position);
        if (!item.isVisible())
            holder.base.setVisibility(View.GONE);

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