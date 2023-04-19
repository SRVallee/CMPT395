package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.CalendarUtils.selectedDate;
import static com.example.hopperschedulemanager.MainActivity.redirectClick;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowId;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    private CaldroidFragment caldroidFragment;
    private DBHandler db = new DBHandler(Schedule.this);
    private ArrayList<Shift> shiftsFromDB;
    private View legendView;
    private LinearLayout sched;

    DateTime tempDate, senderDate;
    int p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        drawerLayout=findViewById(R.id.drawer_schedule);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        legendView = findViewById(R.id.legend_view);
        legendView.setVisibility(View.GONE);
        sched = (LinearLayout) findViewById(R.id.full_sched_layout);
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, selectedDate.getMonthValue());
            args.putInt(CaldroidFragment.YEAR, selectedDate.getYear());
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);


        }

        Calendar cal = Calendar.getInstance();
        cal.set(selectedDate.getYear(), selectedDate.getMonthValue()-1, selectedDate.getDayOfMonth());
        setShiftInfo(cal);


        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        cal = Calendar.getInstance();
        cal.set(selectedDate.getYear(), selectedDate.getMonthValue()-1, selectedDate.getDayOfMonth());

        //Set Month Spinner
        Spinner spinnerMonth = findViewById(R.id.spinnerScheduleMonth);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,R.array.months, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);
        //Set spinner to current month
        spinnerMonth.setSelection(cal.get(Calendar.MONTH));
        spinnerMonth.setOnItemSelectedListener(this);

        //Set Year Spinner
        Spinner spinnerYear = findViewById(R.id.spinnerScheduleYear);
        //Make an array that goes 50 years back and forward from current year
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = cal.get(Calendar.YEAR);
        for (int i = thisYear-10; i <= thisYear+10; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        spinnerYear.setAdapter(adapterYear);
        //Set spinner to current year
        spinnerYear.setSelection(10);
        spinnerYear.setOnItemSelectedListener(this);

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                redirectClick(Schedule.this, WeekView.class);
                finish();

            }

            @Override
            public void onChangeMonth(int month, int year) {

                Calendar cal = Calendar.getInstance();
                cal.set(year, month-1, 1);

                spinnerMonth.setSelection(month-1);

                for (int i = 0; i < spinnerYear.getCount(); i++) {
                    if(spinnerYear.getItemAtPosition(i).equals(String.valueOf(year))){
                        spinnerYear.setSelection(i);
                        break;
                    }
                }

                setShiftInfo(cal);
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };
        caldroidFragment.setCaldroidListener(listener);
//        if((selectedDate.getMonthValue() != LocalDate.now().getMonthValue())
//                || (selectedDate.getYear() != LocalDate.now().getYear())){
//            try {
//                caldroidFragment.setCalendarDateTime(DateTime.forDateOnly(selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }


    }
    public void clickLegend(View view){
        legendView.setVisibility(View.VISIBLE);
        legendView.setFocusable(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }

    public void clickHome(View view){
        redirectClick(this,MainActivity.class);
        finish();
    }

    public void clickEmployeeList(View view){
        redirectClick(this,EmployeeList.class);
        finish();
    }

    public void clickSchedule(View view){
        MainActivity.closeDrawer(drawerLayout);
    }

    public void clickSettings(View view){

        redirectClick(this, Settings.class);
        this.finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar cal = Calendar.getInstance();
        cal.set(caldroidFragment.getYear(), caldroidFragment.getMonth()-1, 1);
        setShiftInfo(cal);

    }

    @Override
    public void onBackPressed(){
        if(legendView.getVisibility() == View.VISIBLE){
            legendView.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            redirectClick(this,MainActivity.class);
            this.finish();
        }

    }



    /**
     *
     * @param cal Calendar with set date to change
     * @param state What to change to ()
     */
    private void colorDay(Calendar cal, Shift[] state){

        if (caldroidFragment == null) {
            return;
        }

        Date dateToColor = cal.getTime();
        ColorDrawable color;

        //if weekday
        if (cal.get(Calendar.DAY_OF_WEEK) != 1 && cal.get(Calendar.DAY_OF_WEEK) != 7) {

            if (state[0].isEmpty() && state[1].isEmpty()) {

                color = new ColorDrawable(getResources().getColor(R.color.Unscheduled));
                caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);
                return;

            }
            else if ((state[0].isReady() && state[1].isReady())){

                color = new ColorDrawable(getResources().getColor(R.color.Scheduled));
                caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);
                return;
            }

            color = new ColorDrawable(getResources().getColor(R.color.PartiallyScheduled));
            caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);

        }
        else{ //if weekend
            if (state[0].isEmpty()) {
                color = new ColorDrawable(getResources().getColor(R.color.Unscheduled));
                caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);

            }
            else if (state[0].isReady()){
                color = new ColorDrawable(getResources().getColor(R.color.Scheduled));
                caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);
            }
            else{
                color = new ColorDrawable(getResources().getColor(R.color.PartiallyScheduled));
                caldroidFragment.setBackgroundDrawableForDate(color, dateToColor);
            }
        }
    }

    /**
     * makes shifts for the entire month and sends it to the database
     * It also sets all colors in the calendar to the unscheduled color
     * @param cal Calendar object
     */
    private void makeShifts(Calendar cal, ArrayList<Shift> shifts){

        int numberOfDays = Month.of(cal.get(Calendar.MONTH)+1).length(Year.of(cal.get(Calendar.YEAR)).isLeap());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);

        for (int day = 1; day < (numberOfDays+1); day++) {

            Shift[] zero = {new Shift(), new Shift()};
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            Shift newShift;

            if (dayOfWeek == 1){    //Sundays
                newShift = new Shift();
                newShift.setDate(DateTime.forDateOnly(cal.get(1),cal.get(2)+1,day));
                newShift.setSlot(1);
                shifts.add(newShift);
//                db.addShift(newShift);
            }
            else if(dayOfWeek == 7){    //Saturdays
                newShift = new Shift();
                newShift.setDate(DateTime.forDateOnly(cal.get(1),cal.get(2)+1,day));
                newShift.setSlot(12);
                shifts.add(newShift);
//                db.addShift(newShift);
            }

            else{
                newShift = new Shift();
                Shift newShiftAfternoon = new Shift();
                int[] slots = getSlots(dayOfWeek);
                newShift.setDate(DateTime.forDateOnly(cal.get(1),cal.get(2)+1,day));
                newShift.setSlot(slots[0]);
                newShiftAfternoon.setDate(DateTime.forDateOnly(cal.get(1),cal.get(2)+1,day));
                newShiftAfternoon.setSlot(slots[1]);
                shifts.add(newShift);
//                db.addShift(newShift);
                shifts.add(newShiftAfternoon);
//                db.addShift(newShiftAfternoon);
            }

            colorDay(cal,zero);

            cal.add(cal.DATE, 1);
        }

        db.addShifts(shifts);

    }

    /**
     * Get slots for weekdays
     * @param day : int
     *            day of week 1-7
     * @return an array with afternoon slot id and morning slot id respectively
     */
    public static int[] getSlots(int day){
        int[] slots = {2,3};
        if(day == 2) return slots;

        slots[0] = (day-1) *2;
        slots[1] = slots[0]+1;

        return slots;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String selectedMonth;
        String selectedYear;
        int tempMonthInt;
        int tempYearInt;

        if (adapterView.getId() == R.id.spinnerScheduleMonth) {
            //If the user changes the month
            //Takes month from the spinner
            selectedMonth = adapterView.getItemAtPosition(i).toString();
            //Takes the currently displayed year
            tempYearInt = caldroidFragment.getYear();
            selectedYear = String.valueOf(tempYearInt);
        } else if (adapterView.getId() == R.id.spinnerScheduleYear) {
            //If the user changes the year
            //Takes the year from the spinner
            selectedYear = adapterView.getItemAtPosition(i).toString();
            //Takes the currently displayed month
            tempMonthInt = caldroidFragment.getMonth();
            Month.of(tempMonthInt).name();
            selectedMonth = Month.of(tempMonthInt).name();
        } else {
            //This will run on the first time the calendar is made, will gen the current month+year
            //tempMonthInt = caldroidFragment.getMonth();
            //selectedMonth = Month.of(tempMonthInt).name();
            //Takes the currently displayed month
            tempMonthInt = caldroidFragment.getMonth();
            Month.of(tempMonthInt).name();
            selectedMonth = Month.of(tempMonthInt).name();
            //Takes the currently displayed year
            tempYearInt = caldroidFragment.getYear();
            selectedYear = String.valueOf(tempYearInt);
        }
        //Make the string for the first day of the month
        String string = selectedMonth + " 1, " + selectedYear;
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date date = null;

        try {
            date = format.parse(string);
            caldroidFragment.moveToDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void ClickWeekView(View view) {

        redirectClick(this, WeekView.class);
        this.finish();
    }

    public void monthlyStatsViewClick(View view) {

        //get tempDate
        tempDate = DateTime.forDateOnly(
                selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());

        senderDate = tempDate.getStartOfMonth();

        Intent intentWeeklyStats = new Intent(Schedule.this, MonthlyStats.class);
        intentWeeklyStats.putExtra("DateTime", senderDate);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentWeeklyStats,b);
        this.finish();

    }

    public void setShiftInfo(Calendar cal){

        DateTime date = DateTime.forDateOnly(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1);
        DateTime date2 = date.minusDays(1);
        shiftsFromDB = db.getShiftList(date2, date.getEndOfMonth());
        //This function is inconsistent

        if(shiftsFromDB.isEmpty()){

            makeShifts(cal, shiftsFromDB);

        }

        else{
            if(shiftsFromDB.get(0).getDate().getDay() != 1){
                shiftsFromDB = db.getShiftList(date, date.getEndOfMonth());
            }

            cal.set(cal.get(1), cal.get(2), 1);

            int i = 0;
            for(int day = 1; day < date.getEndOfMonth().getDay()+1; day++){
                Shift[] state = {new Shift(),  new Shift()};//[morning, evening]
                int shiftInDay = 0;

                while (shiftsFromDB.size() > i
                        && day == shiftsFromDB.get(i).getDate().getDay() && shiftInDay < 2){

                    state[shiftInDay] = shiftsFromDB.get(i);
                    shiftInDay++;
                    i++;
                }
                cal.set(cal.get(1), cal.get(2), day);
                colorDay(cal, state);

            }
        }
    }
}
