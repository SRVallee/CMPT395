package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.getAppContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import hirondelle.date4j.DateTime;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>  {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private Context context = getAppContext();
    DBHandler db = new DBHandler(context);


    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.days = days;
        this.onItemListener = onItemListener;




    }

    // this is the creation of the calendar day view which holds them in place
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view this is 7 so goes to week view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }

    //this is to bind the calendar day view to a position and also set the text to the
    // correct day
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        ArrayList<Shift> shiftlist;
        Shift shift;
        DateTime startDateTime = DateTime.forDateOnly(date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth());
        DateTime endDateTime = DateTime.forDateOnly(date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth());
        startDateTime.minusDays(1);

        if(date == null)
            holder.dayOfMonth.setText("");
        else{
            Log.d("day of month", String.valueOf(date.getDayOfMonth()));
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

            if(date.equals(CalendarUtils.selectedDate)) {
                holder.parentView.setBackgroundResource(R.color.eggshell);
            }
            else {

                shiftlist = db.getShiftList(startDateTime, endDateTime);
                if(shiftlist.size() == 0){
                    WeekView.makeShiftsForMonth(endDateTime, db);
                }
                shiftlist = db.getShiftList(startDateTime, endDateTime);
                shift = shiftlist.get(0);
                Log.d("SHIFT LIST ID ","id: "+ shiftlist.get(0));
                Log.d("SHIFT GET ID", "id2: "+shift.getId());
                // this is for if size if bigger than 1 meaning a shift in
                //morn and evening
                if(shiftlist.size() >1) {
                    Shift shift_even = shiftlist.get(1);
                    colour_check_even(shift_even, shift, holder);
                }
                else {
                    // this is for if there are only shifts in morning
                    if (shift.isEmpty())
                        holder.parentView.setBackgroundResource(R.color.Unscheduled);
                    else if (shift.isReady())
                        holder.parentView.setBackgroundResource(R.color.Scheduled);
                    else
                        holder.parentView.setBackgroundResource(R.color.PartiallyScheduled);
                }
            }
        }
    }

    private void colour_check_even(Shift shift_even, Shift shift_morn, CalendarViewHolder holder) {
        if(shift_even.isEmpty() && shift_morn.isEmpty())
            holder.parentView.setBackgroundResource(R.color.Unscheduled);
        else if(shift_even.isReady() && shift_morn.isReady())
            holder.parentView.setBackgroundResource(R.color.Scheduled);
        else
            holder.parentView.setBackgroundResource(R.color.PartiallyScheduled);
    }

    // this is to just get the size of the week
    @Override
    public int getItemCount()
    {
        return days.size();
    }
    // this is so that when you click a date it will change color
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
