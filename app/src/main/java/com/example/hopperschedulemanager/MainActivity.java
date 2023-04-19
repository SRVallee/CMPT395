package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.CalendarUtils.selectedDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private CaldroidFragment caldroidFragment;
    private TextView monthDayText;
    private TextView dayOfWeekTv;
    private ListView hourListView;
    private View weekDayView;
    private View weekendView;
    private View mainPage;
    private View busyDayView;
    private View busyDayViewWeekend;
    static Context appContext;
    DrawerLayout drawerLayout;
    private TextView employ1morn;
    private TextView employ2morn;
    private TextView employ3morn;
    private TextView employ1even;
    private TextView employ2even;
    private TextView employ3even;
    private TextView employ1weekEnd;
    private TextView employ2weekEnd;
    private TextView employ3weekEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database handler - starts the database
        dbHandler = new DBHandler(MainActivity.this);
        drawerLayout = findViewById(R.id.drawer);
        setDrawerLeftEdgeSize(this, drawerLayout, 1);
        selectedDate = LocalDate.now();
        weekDayView = findViewById(R.id.weekDayDailyView);
        weekendView = findViewById(R.id.weekendDailyView);
        MainActivity.appContext = getApplicationContext();
        weekendView.setFocusable(false);
        weekDayView.setFocusable(false);

        // if there is none or an incorrect number of slots in the DB, delete all and recreate
        ArrayList<Slot> s = dbHandler.getSlotList();
        if (s.size() != 12){
            dbHandler.deleteAllSlots();
            dbHandler.generateSlots();
        }


        initWidgets();
    }
    //all of this will be for the daily calendar
    private void initWidgets() {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTv = findViewById(R.id.dayOfWeekTv);
        mainPage =findViewById(R.id.drawer);
        employ1morn = findViewById(R.id.mornEmploy1);
        employ2morn = findViewById(R.id.mornEmploy2);
        employ3morn = findViewById(R.id.mornEmploy3);
        employ1even = findViewById(R.id.evenemploy1);
        employ2even = findViewById(R.id.evenemploy2);
        employ3even = findViewById(R.id.evenemploy3);
        employ1weekEnd = findViewById(R.id.AllDayemploy1);
        employ2weekEnd = findViewById(R.id.AllDayemploy2);
        employ3weekEnd = findViewById(R.id.AllDayemploy3);
        busyDayView =findViewById(R.id.busyDayView);
        busyDayViewWeekend = findViewById(R.id.busyDayViewWeekend);

        //hourListView = findViewById(R.id.hourListView);
    }

    public static Context getAppContext(){
        return MainActivity.appContext;
    }
    @Override
    protected void onResume(){
        super.onResume();
        selectedDate = LocalDate.now();
        setDayView();
    }

    private void setDayView() {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CANADA);
        dayOfWeekTv.setText(dayOfWeek);
        Shift shiftmorn;
        Shift shifteven;
        ArrayList<Shift> shiftlist;
        DateTime startDateTime = DateTime.forDateOnly(selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());

        DateTime endDateTime = DateTime.forDateOnly(selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());
        startDateTime.minusDays(1);
        shiftlist = dbHandler.getShiftList(startDateTime, endDateTime);
        Log.d("shift size","size: "+ shiftlist.size());
        // this if else is to set the different views for weekends and weekdays
        if(selectedDate.getDayOfWeek() == DayOfWeek.SATURDAY || selectedDate.getDayOfWeek() ==DayOfWeek.SUNDAY){
            weekDayView.setVisibility(View.INVISIBLE);
            weekendView.setVisibility(View.VISIBLE);
            if(!shiftlist.isEmpty()) {
                shiftmorn = shiftlist.get(0);
                get_weekEnd_emps(shiftmorn);
            }
       }
        else{
            weekDayView.setVisibility(View.VISIBLE);
            weekendView.setVisibility(View.INVISIBLE);
            if(!shiftlist.isEmpty()) {
                shiftmorn = shiftlist.get(0);
                get_morn_emps(shiftmorn);
            }
            if(shiftlist.size() >1){
                shifteven = shiftlist.get(1);
                get_even_emps(shifteven);
            }
        }
    }

    private void get_weekEnd_emps(Shift shiftmorn) {
        Employee employee;
        int idmorn1 = shiftmorn.getEmployee1();
        int idmorn2 = shiftmorn.getEmployee2();
        int idmorn3 = shiftmorn.getEmployee3();
        Log.d("the emp morn id1", "id " + idmorn1);
        if (idmorn1 != -1) {
            employee = dbHandler.selectEmployee(idmorn1);
            employ1weekEnd.setText(employee.getName());
        }
        else
            employ1weekEnd.setText("None");

        if(idmorn2 !=-1) {
            employee = dbHandler.selectEmployee(idmorn2);
            employ2weekEnd.setText(employee.getName());
        }
        else
            employ2weekEnd.setText("None");

        if(shiftmorn.isBusy()) {
            employ3weekEnd.setVisibility(View.VISIBLE);
            busyDayViewWeekend.setVisibility(View.VISIBLE);
            employ3weekEnd.setFocusable(true);
            if (idmorn3 != -1) {
                employee = dbHandler.selectEmployee(idmorn3);
                employ3weekEnd.setText(employee.getName());
            } else
                employ3weekEnd.setText("None");
        }
        else{
            employ3weekEnd.setFocusable(true);
            employ3weekEnd.setVisibility(View.GONE);
            busyDayViewWeekend.setVisibility(View.GONE);
        }
    }

    private void get_even_emps(Shift shifteven) {
        Employee employee;
        int ideven1 = shifteven.getEmployee1();
        int ideven2 = shifteven.getEmployee2();
        int ideven3 = shifteven.getEmployee3();
        if(ideven1 != -1){
            employee = dbHandler.selectEmployee(ideven1);
            employ1even.setText(employee.getName());
        }
        else
            employ1even.setText("None");
        if(ideven2 != -1){
            employee = dbHandler.selectEmployee(ideven2);
            employ2even.setText(employee.getName());
        }
        else
            employ2even.setText("None");

        if(shifteven.isBusy()) {
            employ3even.setVisibility(View.VISIBLE);
            busyDayView.setVisibility(View.VISIBLE);
            employ3even.setFocusable(true);
            if (ideven3 != -1) {
                employee = dbHandler.selectEmployee(ideven3);
                employ3even.setText(employee.getName());
            } else
                employ3even.setText("None");
        }
        else{
            employ3even.setVisibility(View.GONE);
            busyDayView.setVisibility(View.GONE);
            employ3even.setFocusable(false);
        }
    }

    private void get_morn_emps(Shift shiftmorn) {
        Employee employee;
        int idmorn1 = shiftmorn.getEmployee1();
        int idmorn2 = shiftmorn.getEmployee2();
        int idmorn3 = shiftmorn.getEmployee3();
        Log.d("the emp morn id1", "id " + idmorn1);
        if (idmorn1 != -1) {
            employee = dbHandler.selectEmployee(idmorn1);
            employ1morn.setText(employee.getName());
        }
        else
            employ1morn.setText("None");

        if(idmorn2 !=-1) {
            employee = dbHandler.selectEmployee(idmorn2);
            employ2morn.setText(employee.getName());
        }
        else
            employ2morn.setText("None");

        if(shiftmorn.isBusy()) {
            employ3morn.setVisibility(View.VISIBLE);
            busyDayView.setVisibility(View.VISIBLE);
            employ3morn.setFocusable(true);
            if (idmorn3 != -1) {
                employee = dbHandler.selectEmployee(idmorn3);
                employ3morn.setText(employee.getName());
            } else
                employ3morn.setText("None");
        }
        else{
            employ3morn.setVisibility(View.GONE);
            busyDayView.setVisibility(View.GONE);
            employ3morn.setFocusable(false);
        }

    }

    public void previousDayAction(View view) {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    public void nextDayAction(View view) {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    public void dayAddEdit(View view){
        redirectClick(this, WeekView.class);
    }

    /*this is for all the onClick in the drawer aka nav_drawer*/
    public void clickMenu(View view) {
        openDrawer(drawerLayout);

    }

    /*this is to open drawer of the parent drawer Layout in activity_main.xml*/
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) {
            Log.d("what", "what");
            return;
        }
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            Log.d("displaysize","size: "+displaySize);
            Log.d("displaysize.x","sizeS: "+displaySize.x);
            Log.d("oriedge","edger"+ edgeSize );
            Log.d("edgesize","edge: "+ Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));
            int newEdgeSize = (int) (600);
            edgeSizeField.setInt(leftDraggerField, newEdgeSize);
            edgeSize = edgeSizeField.getInt(leftDragger);
            Log.d("newedge","edger "+ edgeSize );
        } catch (NoSuchFieldException | IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    /*this is for the clicking of the logo*/
    public void ClickLogo(View view) {
        closeDrawer(drawerLayout);

    }

    /*this checks if the drawer is opened already than if it is closes it*/
    public static void closeDrawer(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /*this recreates the home page where everything starts at*/
    public void clickHome(View view) {
        selectedDate = LocalDate.now();
        setDayView();
        closeDrawer(drawerLayout);
    }

    /*this is to redirect the user to the correct page containing the info*/
    public void clickSchedule(View view) {
        redirectClick(this, Schedule.class);
    }


    public void clickEmployeeList(View view){
        redirectClick(this,EmployeeList.class);
    }

    public void clickEdit(View view){
        redirectClick(this,EditEmployee.class);
    }

    public void clickSettings(View view) { redirectClick(this, Settings.class); }


    /**intent is to launch a request upon a request
     * Activity is to invoke another application to do something in this case it will
     * be invoking the dashboard application
     * FLAG_ACTIVITY_NEW_TASK: setting the dashboard as the current task
     *
     * this function is to redirect clicks to the correct page
     * */
    public static void redirectClick(Activity activity, Class<?> Class) {
        Intent intent = new Intent(activity, Class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle b = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        activity.startActivity(intent,b);
    }

    /**
     * Convert a list of Integers into an int array
     *
     * @param list Integer list
     * @return int array
     */
    public static int[] intList2Array(List<Integer> list){
        // Convert list to array
        return list.stream().mapToInt(i->i).toArray();

    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


}