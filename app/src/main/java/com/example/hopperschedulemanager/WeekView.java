package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.CalendarUtils.daysInWeekArray;
import static com.example.hopperschedulemanager.CalendarUtils.monthYearFromDate;
import static com.example.hopperschedulemanager.CalendarUtils.selectedDate;
import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class WeekView extends AppCompatActivity implements CalendarAdapter.OnItemListener, AdapterView.OnItemSelectedListener {
    private TextView weekRange;
    private TextView monthOfWeek;
    private TextView morningError;
    private TextView eveningError;
    private TextView weekendError;
    private ImageView morningWarn;
    private ImageView eveningWarn;
    private ImageView weekendWarn;
    private View nextWeekButton;
    private View prevWeekButton;
    private RecyclerView calendarRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private View weekDayWeekView;
    private View weekendWeekView;
    CheckBox busyDay;
    boolean busycheck;
    private Spinner employee1Morning;
    private Spinner employee2Morning;
    private Spinner employee3Morning;
    private Spinner employee1Evening;
    private Spinner employee2Evening;
    private Spinner employee3Evening;
    private Spinner employee1Weekend;
    private Spinner employee2Weekend;
    private Spinner employee3Weekend;
    DrawerLayout drawerLayout;
    DBHandler db;

    DateTime tempDate, senderDate;
    int p;

    private ArrayList<Shift> weeklyShifts;
    private ArrayList<Employee> employeesAvailableMorning = new ArrayList<>();

    private ArrayList<Employee> employeesAvailableEvening = new ArrayList<>();

    private String errorTop;
    private String errorBottom;

    private AlertDialog busyUncheckAlert;

    private boolean weekChecked; //missing person
    private boolean weekChecked2; // autoSchedule

    private Button semiAutoSchdl;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        //this is to call drawer
        drawerLayout = findViewById(R.id.drawer);
        initWidgets();
    }
    @Override
    protected void onResume(){
        super.onResume();
        setWeekView();
    }

    @Override
    public void onBackPressed(){
        redirectClick(this,Schedule.class);
        this.finish();
    }

    //initializes all variables
    private void initWidgets() {
        db = new DBHandler(WeekView.this);

        //weekRange = findViewById(R.id.weekRange);
        monthOfWeek = findViewById(R.id.monthOfWeek);

        int underline = Paint.UNDERLINE_TEXT_FLAG;
        int linkColor = Color.argb(200, 6,69,173);

        morningError = findViewById(R.id.morningError_TextView);
        morningError.setPaintFlags(underline);
        morningError.setTextColor(linkColor);

        eveningError = findViewById(R.id.eveningError_TextView);
        eveningError.setPaintFlags(underline);
        eveningError.setTextColor(linkColor);

        weekendError = findViewById(R.id.allDayError_TextView);
        weekendError.setPaintFlags(underline);
        weekendError.setTextColor(linkColor);

        nextWeekButton = findViewById(R.id.nextWeekButton);
        prevWeekButton = findViewById(R.id.previousWeekbutton);


        morningWarn = findViewById(R.id.morningWarn);
        eveningWarn = findViewById(R.id.eveningWarn);
        weekendWarn = findViewById(R.id.allDayWarn);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        weekDayWeekView = findViewById(R.id.weekDayWeekView);
        weekendWeekView = findViewById(R.id.weekEndWeekView);
        busyDay = (CheckBox) findViewById(R.id.busycheckBox);

        employee1Morning = findViewById(R.id.mornEmploy1WeekDay);
        employee2Morning = findViewById(R.id.mornEmploy2WeekDay);
        employee3Morning = findViewById(R.id.mornEmploy3WeekDay);


        employee1Evening = findViewById(R.id.evenemploy1Week);
        employee2Evening = findViewById(R.id.evenemploy2Week);
        employee3Evening = findViewById(R.id.evenemploy3Week);


        employee1Weekend = findViewById(R.id.employ1Weekend);
        employee2Weekend = findViewById(R.id.employ2Weekend);
        employee3Weekend = findViewById(R.id.employ3Weekend);

        busyUncheckAlert = new AlertDialog.Builder(this).create();
        busyUncheckAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        busyUncheckAlert.setTitle("Alert! Removing busy shift.");
        busyUncheckAlert.setMessage("This will disable an occupied employee slot.");

        semiAutoSchdl = findViewById(R.id.AutoScheduleButton);
        semiAutoSchdl.setVisibility(View.GONE);

        weekChecked = false;
        weekChecked2 = false;

    }

    /**
     *this is to set the week days using calendar adapter
     * ans also set the day view for selected day
     */
    private void setWeekView() {
        weeklyShifts = getWeeklyShifts();
        nextWeekButton.setEnabled(false);
        prevWeekButton.setEnabled(false);
        monthOfWeek.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(selectedDate); //positions start at 0-6
        CalendarAdapter calendarAdapter = new CalendarAdapter(days,this::onItemClick); //7
        layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        employee1Weekend.setEnabled(false);
        employee2Weekend.setEnabled(false);
        employee3Weekend.setEnabled(false);

        employee1Morning.setEnabled(false);
        employee2Morning.setEnabled(false);
        employee3Morning.setEnabled(false);

        employee1Evening.setEnabled(false);
        employee2Evening.setEnabled(false);
        employee3Evening.setEnabled(false);

        // this if else is to set the different views for weekends and weekdays
        if(selectedDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY){
            weekDayWeekView.setVisibility(View.INVISIBLE);
            weekDayWeekView.setFocusable(false);
            weekendWeekView.setVisibility(View.VISIBLE);
            weekendWeekView.setFocusable(true);

            ArrayAdapter<Employee> employeesAvailableSpinner1;

            Shift shift = null;
            //get people available for the shift
            if (selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY){
                shift = weeklyShifts.get(0);
            }
            else{
                shift = weeklyShifts.get(11);
            }
            employeesAvailableMorning = updateEmployeesAvailable(shift);

            //load first spinner
            employeesAvailableSpinner1 = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1,employeesAvailableMorning);
            employee1Weekend.setAdapter(employeesAvailableSpinner1);

            //reset previous selection
            employee1Weekend.setSelection(0);

            if(employee2Weekend.getCount() > 1){
                employee2Weekend.setSelection(0);
            }
            employee3Weekend.setEnabled(false);
            if(employee3Weekend.getCount() > 1){
                employee3Weekend.setSelection(0);
            }

            //set listener
            employee1Weekend.setOnItemSelectedListener(this);
            employee1Weekend.setEnabled(true);
            //if an employee already scheduled
            if (!shift.isEmpty()){
                findEmployee(shift, employee1Weekend,1);
            }

            //Set busy check
            if(shift.isBusy()){
                busyDay.setChecked(true);
            }
            else{
                busyDay.setChecked(false);
            }
            busyDayClick(busyDay);

        }
        else{
            weekDayWeekView.setVisibility(View.VISIBLE);
            weekDayWeekView.setFocusable(true);
            weekendWeekView.setVisibility(View.INVISIBLE);
            weekendWeekView.setFocusable(false);

            ArrayAdapter<Employee> employeesAvailableSpinner1Morning;
            ArrayAdapter<Employee> employeesAvailableSpinner1Evening;

            int[] slotIds = Schedule.getSlots(quickConvert(selectedDate.getDayOfWeek().getValue()));
            Shift shiftMorning = weeklyShifts.get(slotIds[0]-1);
            Shift shiftEvening= weeklyShifts.get(slotIds[1]-1);
            employeesAvailableMorning = updateEmployeesAvailable(shiftMorning);
            employeesAvailableEvening = updateEmployeesAvailable(shiftEvening);



            // load first spinner for morning shift
            employeesAvailableSpinner1Morning = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1,employeesAvailableMorning);
            employee1Morning.setAdapter(employeesAvailableSpinner1Morning);

            // load first spinner for evening shift
            employeesAvailableSpinner1Evening = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1,employeesAvailableEvening);
            employee1Evening.setAdapter(employeesAvailableSpinner1Evening);

            // reset previous selections
            // Morning
            employee1Morning.setSelection(0);

            if(employee2Morning.getCount() > 1){
                employee2Morning.setSelection(0);
            }

            if(employee3Morning.getCount() > 1){
                employee3Morning.setSelection(0);
            }

            //Evening
            employee1Evening.setSelection(0);

            if(employee2Evening.getCount() > 1){
                employee2Evening.setSelection(0);
            }
            if(employee3Evening.getCount() > 1){
                employee3Evening.setSelection(0);
            }

            //set listeners
            employee1Morning.setOnItemSelectedListener(this);
            employee1Evening.setOnItemSelectedListener(this);
            employee1Morning.setEnabled(true);
            employee1Evening.setEnabled(true);

            //if an employee already scheduled in the morning
            if (!shiftMorning.isEmpty()){
                findEmployee(shiftMorning, employee1Morning,1);
            }

            //if an employee already scheduled in the evening
            if (!shiftEvening.isEmpty()){
                findEmployee(shiftEvening, employee1Evening,1);
            }
            // Set busy check
            if(shiftMorning.isBusy()){
                busyDay.setChecked(true);
            } else {
                busyDay.setChecked(false);
            }
            busyDayClick(busyDay);
        }
        nextWeekButton.setEnabled(true);
        prevWeekButton.setEnabled(true);
    }


    public void colourChanger() {
        calendarRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = calendarRecyclerView.findViewHolderForAdapterPosition(0);
                calendarRecyclerView.animate();
                assert holder != null;
                View view = holder.itemView.findViewById(R.id.parentView);
                Log.d("COLOR","color"+view.getBackground().getConstantState());
                view.setBackgroundResource(R.color.Cornflower120);
                Log.d("COLOR2","color2"+view.getBackground().getConstantState());

            }
        }, 0);

    }


    /**
     * this is for the calendar when we implements it so that clicking on a date you can go
     * to the week view for that date
     * @param position which container it is
     * @param date
     */
    public void onItemClick(int position, LocalDate date){
        if(!selectedDate.equals(date)) {
            selectedDate = date;
            setWeekView();
        }
    }

    /**
     * this is for the previous week where it get the current selected day on that week
     * and it subtracts a week.
     * @param view
     */
    public void nextWeekAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        weeklyShifts = getWeeklyShifts();
        semiAutoSchdl.setVisibility(View.GONE);
        setWeekView();
    }

    // this is for the next week where it get the current selected day on that week
    // and it adds a week.
    public void previousWeekAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        weeklyShifts = getWeeklyShifts();
        semiAutoSchdl.setVisibility(View.GONE);
        setWeekView();
    }

    /**
     * this is for the check box for busy days it makes third employee
     * this will also set in the Dbhandler that this day is busy
     * @param view
     */
    public void busyDayClick(View view){
        boolean checked = ((CheckBox) view).isChecked();
        busycheck = checked;
        Shift shift = null;
        if(selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY || selectedDate.getDayOfWeek() == DayOfWeek.SATURDAY){
            if(selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                shift = weeklyShifts.get(0);
            } else {
                shift = weeklyShifts.get(11);
            }

            if(!busycheck && shift.isBusy() && shift.getEmployee3() != -1){
                busyUncheckAlert.show();
                shift.setEmployee3(-1);
                employee3Weekend.setEnabled(false);
                employee3Weekend.setSelection(0);
            }
            else if(busycheck){
                if(employee2Weekend.getSelectedItemPosition() != 0) {
                    employee3Weekend.setEnabled(true);
                }
            }
            shift.setBusy(busycheck);
            addReplace_HardErrorCheck(shift); //db.replaceShift(shift);
        } else {
            int[] slots = Schedule.getSlots(quickConvert(selectedDate.getDayOfWeek().getValue()));
            Shift shiftMorning = weeklyShifts.get(slots[0] - 1);
            Shift shiftEvening = weeklyShifts.get(slots[1] - 1);

            if(!busycheck && shiftMorning.isBusy() &&
                    (shiftMorning.getEmployee3() != -1 || shiftEvening.getEmployee3() != -1)){

                busyUncheckAlert.show();
                shiftMorning.setEmployee3(-1);
                employee3Morning.setEnabled(false);
                employee3Morning.setSelection(0);

                shiftEvening.setEmployee3(-1);
                employee3Evening.setEnabled(false);
                employee3Evening.setSelection(0);
            }
            else if(busycheck){
                if(employee2Morning.getSelectedItemPosition() != 0) {
                    employee3Morning.setEnabled(true);
                }
                if(employee2Evening.getSelectedItemPosition() != 0) {
                    employee3Evening.setEnabled(true);
                }
            }


            shiftMorning.setBusy(busycheck);
            addReplace_HardErrorCheck(shiftMorning); //db.replaceShift(shiftMorning);

            shiftEvening.setBusy(busycheck);
            addReplace_HardErrorCheck(shiftEvening); //db.replaceShift(shiftEvening);
        }

        if (checked) {
            employee3Morning.setVisibility(View.VISIBLE);
            employee3Evening.setVisibility(View.VISIBLE);
            employee3Weekend.setVisibility(View.VISIBLE);

        } else {
            employee3Morning.setVisibility(View.GONE);
            employee3Evening.setVisibility(View.GONE);
            employee3Weekend.setVisibility(View.GONE);
            employee3Morning.setEnabled(false);
            employee3Evening.setEnabled(false);
            employee3Weekend.setEnabled(false);
        }
    }

    public void clickErrorMorning(View view){
        errorAlert("morning", errorTop);
    }

    public void clickErrorEvening(View view){
        errorAlert("evening", errorBottom);
    }

    public void clickErrorWeekend(View view){
        errorAlert("this", errorTop);
    }

    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);

    }

    public void ClickLogo(View view) {
        MainActivity.closeDrawer(drawerLayout);

    }

    public void clickHome(View view) {
        redirectClick(this, MainActivity.class);
        this.finish();

    }

    public void clickEmployeeList(View view){
        redirectClick(this,EmployeeList.class);
        this.finish();
    }

    public void clickSchedule(View view) {
        redirectClick(this, Schedule.class);
        this.finish();
    }

    public void clickSettings(View view){
        redirectClick(this, Settings.class);
        this.finish();
    }

    /**
     * SpinnerListener in charge of updating the employees in the shift.
     * It triggers every time an employee is selected in a spinner, which is used to load the
     * next spinners. The reason we load the spinners in order is to show employee option from
     * repeating and avoid accidentally assigning the same employee twice in a shift.
     * @param parent The spinner
     * @param view
     * @param pos position selected
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d("Position Selected: ", String.valueOf(pos));
        Log.d("Id: ", String.valueOf(parent.getId()));

        // get employee and initialize shifts
        Employee employeeSelected = (Employee) parent.getItemAtPosition(pos);
        Shift shift = null, shiftMorning = null, shiftEvening = null;

        if (parent.getId() == R.id.employ1Weekend){
            if(pos != 0){ //If an employee was selected

                //get shift
                if(selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY){
                    shift = weeklyShifts.get(0);

                } else {
                    shift = weeklyShifts.get(11);
                }

                //save employee selected into the shift object
                shift.setEmployeeObj1(employeeSelected);

                //sets arrayAdapter to enable second spinner
                ArrayAdapter<Employee> employeesAvailableSpinner2 = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1,new ArrayList<>(employeesAvailableMorning));

                //Removes selected employee from the options in the next spinner
                employeesAvailableSpinner2.remove(employeeSelected);

                employee2Weekend.setAdapter(employeesAvailableSpinner2);

                //set listeners for the next spinner
                employee2Weekend.setOnItemSelectedListener(this);
                employee2Weekend.setEnabled(true);

                //if an employee already scheduled in the second shift slot
                if (shift.getEmployee2() != -1){
                    // find employee position in the spinner and select it.
                    findEmployee(shift, employee2Weekend, 2);

                } else { //enable warning icon that employees are missing by saving to db
                    Error e = addReplace_HardErrorCheck(shift);
                    System.out.println(e.toString());
                }

            } else { //When an employee was not selected or was deselected
                if(selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY){
                    shift = weeklyShifts.get(0);

                } else {
                    shift = weeklyShifts.get(11);
                }

                if(shift.getEmployee1() != -1) { //if deleting an employee
                    employee2Weekend.setEnabled(false);
                    employee3Weekend.setEnabled(false);

                    if(shift.getEmployee2() != -1) {
                        shift.removeEmployee(shift.getEmployee1());
                        findEmployee(shift, employee1Weekend, 1);
                        return;
                    } else{
                        shift.removeEmployee(shift.getEmployee1());
                        addReplace_HardErrorCheck(shift);
                        return;
                    }

                }
                employee2Weekend.setEnabled(false);
                employee3Weekend.setEnabled(false);

                //enable warning icon by saving to db
                Error e = addReplace_HardErrorCheck(shift);
            }
        }

        else if (parent.getId() == R.id.employ2Weekend){
            if(pos != 0){ //If an employee was selected

                //get correct shift
                if(selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY){
                    shift = weeklyShifts.get(0);

                } else {
                    shift = weeklyShifts.get(11);
                }

                //save employee selected into shift object
                shift.setEmployeeObj2(employeeSelected);

                //Set arrayAdapter for the next spinner and remove options already selected
                ArrayAdapter<Employee> employeesAvailableSpinner3 = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, new ArrayList<>(employeesAvailableMorning));
                employeesAvailableSpinner3.remove(employeeSelected);
                employeesAvailableSpinner3.remove((Employee) employee1Weekend.getSelectedItem());

                employee3Weekend.setAdapter(employeesAvailableSpinner3);

                //set listeners for the next spinner
                employee3Weekend.setOnItemSelectedListener(this);
                employee3Weekend.setEnabled(true);

                if(shift.isBusy()){
                    //if shift is busy and an employee already scheduled on 3rd shift slot
                    //Find employee position on the next spinner and select it.
                    if (shift.getEmployee3() != -1) {
                        findEmployee(shift, employee3Weekend, 3);
                    } else {//if no employee on busy shift yet. Enable warningnby saving to db
                        Error e = addReplace_HardErrorCheck(shift);
                        System.out.println(e.toString());
                    }
                } else { //if shift is not busy
                    //save
                    Error e = addReplace_HardErrorCheck(shift);
                    System.out.println(e.toString());

                    //check entire week
                    checkWeekSchedule();
                }
            } else if(employee2Weekend.isEnabled()) { //when an employee is deselected by user

                //get correct shift
                if (selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    shift = weeklyShifts.get(0);
                } else {
                    shift = weeklyShifts.get(11);
                }

                //if not being reset
                if (shift.getEmployee2() != -1) {

                    //disable next slot and
                    employee3Weekend.setEnabled(false);
                    employee3Weekend.setSelection(0);

                    if (shift.isBusy() && shift.getEmployee3() != -1) { //if employee 3 spot was not empty
                        shift.removeEmployee(shift.getEmployee2());
                        findEmployee(shift,employee2Weekend, 2);

                    } else { //save to db
                        shift.removeEmployee(shift.getEmployee2());
                        addReplace_HardErrorCheck(shift);
                    }
                }
            }
        }

        else if (parent.getId() == R.id.employ3Weekend) {

            //select correct shift
            if (selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                shift = weeklyShifts.get(0);

            } else {
                shift = weeklyShifts.get(11);
            }

            if (pos != 0) { //if employee was selected
                shift.setEmployeeObj3(employeeSelected);

            //else if employee deselected by user
            } else if(employee3Weekend.isEnabled()){

                shift.setEmployeeObj3(null);
            }

            //save
            addReplace_HardErrorCheck(shift);
            checkWeekSchedule();

        }

        else if(selectedDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                selectedDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            int[] slots = Schedule.getSlots(quickConvert(selectedDate.getDayOfWeek().getValue()));
            shiftMorning = weeklyShifts.get(slots[0] - 1);
            shiftEvening = weeklyShifts.get(slots[1] - 1);
        }else{
            return;
        }

        if (parent.getId() == R.id.mornEmploy1WeekDay){
            if(pos != 0) {
                //
                shiftMorning.setEmployeeObj1(employeeSelected);

                ArrayAdapter<Employee> employeesAvailableSpinner = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, new ArrayList<>(employeesAvailableMorning));
                employeesAvailableSpinner.remove(employeeSelected);

                employee2Morning.setAdapter(employeesAvailableSpinner);

                //set listeners
                employee2Morning.setOnItemSelectedListener(this);
                employee2Morning.setEnabled(true);

                //if an employee already scheduled in the morning
                if (shiftMorning.getEmployee2() != -1) {
                    findEmployee(shiftMorning, employee2Morning, 2);
                } else {
                    addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
                }

            } else if(employee1Morning.isEnabled() && shiftMorning.getEmployee1() != -1){// if deleting an employee

                employee2Morning.setEnabled(false);
                employee3Morning.setEnabled(false);


                if(shiftMorning.getEmployee2() != -1) { //if second spinner has employee
                    shiftMorning.removeEmployee(shiftMorning.getEmployee1());
                    findEmployee(shiftMorning, employee1Morning, 1);
                    return;
                } else{
                    shiftMorning.removeEmployee(shiftMorning.getEmployee1());
                    addReplace_HardErrorCheck(shiftMorning);
                    return;
                }
            } else{
                employee2Morning.setEnabled(false);
                employee3Morning.setEnabled(false);
            }
        }

        else if (parent.getId() == R.id.mornEmploy2WeekDay){
            if(pos != 0){

                shiftMorning.setEmployeeObj2(employeeSelected);

                ArrayAdapter<Employee> employeesAvailableSpinner = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, new ArrayList<>(employeesAvailableMorning));
                employeesAvailableSpinner.remove(employeeSelected);
                employeesAvailableSpinner.remove((Employee) employee1Morning.getSelectedItem());

                employee3Morning.setAdapter(employeesAvailableSpinner);

                //set listeners
                employee3Morning.setOnItemSelectedListener(this);
                employee3Morning.setEnabled(true);

                if(shiftMorning.isBusy()) {

                    //if an employee already scheduled in the morning
                    if (shiftMorning.getEmployee3() != -1) {
                    findEmployee(shiftMorning, employee3Morning,3);

                    } else {
                        addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
                    }

                } else {
                    addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
                    checkWeekSchedule();
                }

            } else if(employee2Morning.isEnabled() && shiftMorning.getEmployee2() != -1){
                employee3Morning.setEnabled(false);
                employee3Morning.setSelection(0);

                if(shiftMorning.isBusy() && shiftMorning.getEmployee3() != -1){ //if employee 3 spot was not empty
                    shiftMorning.removeEmployee(shiftMorning.getEmployee2());
                    findEmployee(shiftMorning, employee2Morning, 2);

                } else {
                    shiftMorning.setEmployeeObj2(null);
                    addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
                }
            }


        }

        else if (parent.getId() == R.id.mornEmploy3WeekDay){
            if(pos != 0){

                shiftMorning.setEmployeeObj3(employeeSelected);
                addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
                checkWeekSchedule();

            } else if(employee3Morning.isEnabled()){
                if (shiftMorning.getEmployee3() != -1) {
                    shiftMorning.setEmployeeObj3(null);
                }
                addReplace_HardErrorCheck(shiftMorning); //db.addOrReplaceShift(shiftMorning);
            }
        }

        else if (parent.getId() == R.id.evenemploy1Week){
            if(pos != 0){


                shiftEvening.setEmployeeObj1(employeeSelected);

                ArrayAdapter<Employee> employeesAvailableSpinner = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, new ArrayList<>(employeesAvailableEvening));
                employeesAvailableSpinner.remove(employeeSelected);

                employee2Evening.setEnabled(true);
                employee2Evening.setAdapter(employeesAvailableSpinner);

                //set listeners
                employee2Evening.setOnItemSelectedListener(this);

                //if an employee already scheduled in the morning
                if (shiftEvening.getEmployee2() != -1) {
                    findEmployee(shiftEvening, employee2Evening, 2);
                } else {
                    addReplace_HardErrorCheck(shiftEvening); //db.addOrReplaceShift(shiftEvening);
                }


            } else if(employee1Evening.isEnabled() && shiftEvening.getEmployee1() != -1){// if deleting an employee
                employee2Evening.setEnabled(false);
                employee3Evening.setEnabled(false);

                if(shiftEvening.getEmployee2() != -1) { //if second spinner has employee
                    shiftEvening.removeEmployee(shiftEvening.getEmployee1());
                    findEmployee(shiftEvening, employee1Evening, 1);
                    return;
                } else{
                    shiftEvening.removeEmployee(shiftEvening.getEmployee1());
                    addReplace_HardErrorCheck(shiftEvening);
                    return;
                }
            }else{
                employee2Evening.setEnabled(false);
                employee3Evening.setEnabled(false);
            }
        }

        else if (parent.getId() == R.id.evenemploy2Week){
            if(pos != 0){
                shiftEvening.setEmployeeObj2(employeeSelected);



                ArrayAdapter<Employee> employeesAvailableSpinner = new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, new ArrayList<>(employeesAvailableEvening));
                employeesAvailableSpinner.remove(employeeSelected);
                employeesAvailableSpinner.remove((Employee) employee1Evening.getSelectedItem());

                employee3Evening.setAdapter(employeesAvailableSpinner);

                //set listeners
                employee3Evening.setOnItemSelectedListener(this);
                employee3Evening.setEnabled(true);

                if(shiftEvening.isBusy()) {
                    //if an employee already scheduled in the morning
                    if (shiftEvening.getEmployee3() != -1) {
                        findEmployee(shiftEvening, employee3Evening, 3);

                    } else {
                        addReplace_HardErrorCheck(shiftEvening); //db.addOrReplaceShift(shiftEvening);
                    }
                } else {
                    addReplace_HardErrorCheck(shiftEvening); //db.addOrReplaceShift(shiftEvening);
                    checkWeekSchedule();
                }


            } else if(employee2Evening.isEnabled() && shiftEvening.getEmployee2() != -1){
                employee3Evening.setEnabled(false);
                employee3Evening.setSelection(0);

                if(shiftEvening.isBusy() && shiftEvening.getEmployee3() != -1){ //if employee 3 spot was not empty
                    shiftEvening.removeEmployee(shiftEvening.getEmployee2());
                    findEmployee(shiftEvening, employee2Evening, 2);

                } else {
                    shiftEvening.setEmployeeObj2(null);
                    addReplace_HardErrorCheck(shiftEvening);
                }
            }


        }

        else if (parent.getId() == R.id.evenemploy3Week){
            if(pos != 0){
                shiftEvening.setEmployeeObj3(employeeSelected);
                addReplace_HardErrorCheck(shiftEvening); //db.addOrReplaceShift(shiftEvening);
                checkWeekSchedule();


            } else if(employee3Evening.isEnabled()){
                shiftEvening.setEmployeeObj3(null);

                addReplace_HardErrorCheck(shiftEvening); //db.addOrReplaceShift(shiftEvening);
            }
        }
    }


    /**
     * Helper function for the spinner listener. Selects the employee on the spinner specified
     * with the corresponding employee scheduled in the shift.
     * @param shift
     * @param spinner
     * @param employeeNumber position in the shift
     */
    private void findEmployee(Shift shift, Spinner spinner, int employeeNumber){
        int employeeId = -1;//id of selected employee
        ArrayAdapter<Employee> employeesAvailableSpinner = (ArrayAdapter<Employee>) spinner.getAdapter();
        for (int i = 0; i < employeesAvailableSpinner.getCount(); i++) { //find employee and select
            if (((Employee)employeesAvailableSpinner.getItem(i)).getId() == shift.getEmployeeIdByPosition(employeeNumber)) {
                spinner.setSelection(i);
                employeeId = shift.getEmployeeIdByPosition(employeeNumber);
            }
        }
        //If employee not in available, this shift probably already happened
        if(employeeId == -1){//add them from the db
            employeesAvailableSpinner.add(db.selectEmployee(shift.getEmployeeIdByPosition(employeeNumber)));
            spinner.setSelection(employeesAvailableSpinner.getCount()-1);
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);

    }
    // this is for the button to return back to monthly view
    public void monthlyViewClick(View view) {
        redirectClick(this,Schedule.class);
        this.finish();
    }



    public void weeklyStatsViewClick(View view) {
        //get tempDate
        tempDate = DateTime.forDateOnly(
                selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());

        p = selectedDate.getDayOfWeek().getValue();
        //7 = sunday, ext.
        //get monday
        if (p==7) {
            senderDate = tempDate.plusDays(1);
        } else if (p!=7) {
            senderDate = tempDate.minusDays(p - 1);
        }

        Intent intentWeeklyStats = new Intent(WeekView.this, WeeklyStats.class);
        intentWeeklyStats.putExtra("DateTime", senderDate);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentWeeklyStats,b);
        this.finish();

    }



    /**
     * Gets the shifts for selected week from the database.
     * @return 7 shifts from Sunday to Saturday.
     */
    public ArrayList<Shift> getWeeklyShifts() {
        DateTime date = DateTime.forDateOnly(
                selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());
        int weekPosition = selectedDate.getDayOfWeek().getValue();

        // get shifts
        ArrayList<Shift> shifts = db.getFullShiftList(
                date.minusDays(quickConvert(weekPosition)-1),    //Saturday from last week
                date.plusDays(7 - quickConvert(weekPosition)));//to Saturday from this week


        if (shifts.size() == 12){ //expected
            return shifts;
        } else if (shifts.size() == 0){ //if no shift on month
            makeShiftsForMonth(date,db);
            shifts = db.getFullShiftList(
                    date.minusDays(quickConvert(weekPosition)-1),    //Saturday from last week
                    date.plusDays(7 - quickConvert(weekPosition)));
            if(shifts.size() == 12){
                return shifts;
            }
        } else if (shifts.size() > 12){ //if more shift than expected
            Log.e("ERROR", "More than 12 shifts retrieved from DB in weekly shifts");
            return solveDuplicatesInWeek(shifts);
        }

        //This only continues if the list is less than 12

        //if this month is missing
        if(!Objects.equals(shifts.get(0).getDate().getMonth(), date.getMonth())){
            makeShiftsForMonth(date,db);
            shifts = db.getFullShiftList(
                    date.minusDays(quickConvert(weekPosition)-1),    //Saturday from last week
                    date.plusDays(7 - quickConvert(weekPosition)));
            return  shifts;
        }else {
            if (selectedDate.getDayOfMonth() < 7) {
                makeShiftsForMonth(date.minusDays(8), db);
            } else {
                makeShiftsForMonth(date.plusDays(8), db);
            }
        }

        shifts = db.getFullShiftList(
                date.minusDays(quickConvert(weekPosition)-1),    //Saturday from last week
                date.plusDays(7 - quickConvert(weekPosition)));//to Saturday from this week
        return shifts;
    }

    /**
     * A quick function to convert the enumerator from localTime,
     * which starts with monday instead of sunday
     * @param day
     * @return
     */
    private static int quickConvert(int day){
        if(day < 7){
            return day+1;
        }

        return 1;
    }

    /**
     * If a week is not complete.
     * At the start or end of a week, or if a week is from a month where no shifts have been created
     * Or if the user schedules 2 months without leaving the weekly calendar.
     * @param cal
     */
    public static void makeShiftsForMonth(DateTime cal, DBHandler db) {
        ArrayList<Shift> shifts = new ArrayList<>();

        cal = cal.getStartOfMonth();
        int numberOfDays = cal.getNumDaysInMonth();
        cal.minusDays(1);

        for (int day = 1; day < (numberOfDays + 1); day++) {

            DateTime date = DateTime.forDateOnly(cal.getYear(),cal.getMonth(), day);
            int dayOfWeek = date.getWeekDay();
            Shift newShift;
            Log.d("Date: ", date.toString());


            if (dayOfWeek == 1) {    //Sundays
                newShift = new Shift();
                newShift.setDate(date);
                newShift.setSlot(1);
                shifts.add(newShift);
//                db.addShift(newShift);
            } else if (dayOfWeek == 7) {    //Saturdays
                newShift = new Shift();
                newShift.setDate(date);
                newShift.setSlot(12);
                shifts.add(newShift);
//                db.addShift(newShift);
            } else {
                newShift = new Shift();
                Shift newShiftAfternoon = new Shift();
                int[] slots = Schedule.getSlots(dayOfWeek);
                newShift.setDate(date);
                newShift.setSlot(slots[0]);
                newShiftAfternoon.setDate(date);
                newShiftAfternoon.setSlot(slots[1]);
                shifts.add(newShift);
//                db.addShift(newShift);
                shifts.add(newShiftAfternoon);
//                db.addShift(newShiftAfternoon);
            }
        }
        db.addShifts(shifts);
        return;

    }

    /**
     * Gets an Array list with Employees available to work on shift given
     * @param shift
     * @return Employees available to work at shift
     */
    private ArrayList<Employee> updateEmployeesAvailable(Shift shift){
        ArrayList<Employee> employeesAvailable = new ArrayList<>();
        int slot = shift.getSlot();

        int[] employees = db.getSlotAvailability(slot);

        Employee filler = new Employee(-1);
        filler.setName("None");
        employeesAvailable.add(filler);

        for (int id: employees) {
            employeesAvailable.add(db.selectEmployee(id));
        }

        return employeesAvailable;
    }

    /**
     * Loads employees in spinners on specific shift
     * @param shift shift
     */
    private void loadNextSpinner(Shift shift,Spinner spinner, int position ){
        if(shift.getSlot() == 1 || shift.getSlot() == 12){

        } else if(shift.getSlot()%2 != 0){

        } else {

        }
    }

    private void setError(Error e, int slotID){
        // weekend error visibility
        if (slotID == 1 || slotID == 12){
            if (!e.inError){ // no error
                weekendWarn.setVisibility(View.GONE);
                weekendWarn.setFocusable(View.NOT_FOCUSABLE);
                weekendError.setVisibility(View.GONE);
                weekendError.setFocusable(View.NOT_FOCUSABLE);
            } else { // error
                weekendWarn.setVisibility(View.VISIBLE);
                weekendWarn.setFocusable(View.FOCUSABLE);
                weekendError.setVisibility(View.VISIBLE);
                weekendError.setFocusable(View.FOCUSABLE);
            }
            errorTop = e.toString();

        // Weekday morning error visibility
        } else if(slotID % 2 == 0){
            if (!e.inError){ // no error
                morningWarn.setVisibility(View.GONE);
                morningWarn.setFocusable(View.NOT_FOCUSABLE);
                morningError.setVisibility(View.GONE);
                morningError.setFocusable(View.NOT_FOCUSABLE);
            } else { // error
                morningWarn.setVisibility(View.VISIBLE);
                morningWarn.setFocusable(View.FOCUSABLE);
                morningError.setVisibility(View.VISIBLE);
                morningError.setFocusable(View.FOCUSABLE);
            }
            errorTop = e.toString();

        // Weekday afternoon visibility
        } else {
            if (!e.inError){ //no error
                eveningWarn.setVisibility(View.GONE);
                eveningWarn.setFocusable(View.NOT_FOCUSABLE);
                eveningError.setVisibility(View.GONE);
                eveningError.setFocusable(View.NOT_FOCUSABLE);
            } else { // error
                eveningWarn.setVisibility(View.VISIBLE);
                eveningWarn.setFocusable(View.FOCUSABLE);
                eveningError.setVisibility(View.VISIBLE);
                eveningError.setFocusable(View.FOCUSABLE);
            }
            errorBottom = e.toString();

        }
    }

    /**
     * Checks for errors and saves the shift to the database
     * @param shift
     * @return
     */
    public Error addReplace_HardErrorCheck(Shift shift){
        Error e = shift.checkHard(db);
        db.addOrReplaceShift(shift);
        setError(e, shift.getSlot());

        return e;
    }

    public Error addReplace_SoftErrorCheck(Shift shift){
        Error e = shift.checkSoft(db);
        db.addOrReplaceShift(shift);
        setError(e, shift.getSlot());

        return e;
    }

    private void errorAlert(String shiftLocation, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error(s) in " + shiftLocation + " shift:");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickScheduleMonth(View view){
        weekChecked2 = false;
        int toast = checkWeekSchedule();
        switch (toast){

            case 0:
                Toast.makeText(getApplicationContext(),
                        "Week not scheduled",
                        Toast.LENGTH_LONG).show();
                break;

            case 1:
                Toast.makeText(getApplicationContext(),
                        "Old Month",
                        Toast.LENGTH_LONG).show();
                break;

            case 2:
                Toast.makeText(getApplicationContext(),
                        "Month Already Full",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * checks if the week is fully scheduled, and warns if an employee is missing in the week
     *
     */
    private int checkWeekSchedule(){

        ArrayList<Employee> employeesUnscheduled = db.selectEmployeeList(true);
        ArrayList<Integer> employeesScheduled = new ArrayList<>();

        for (Shift shift:weeklyShifts) {

            if(shift.isFull()){
                employeesScheduled.add(shift.getEmployee1());
                employeesScheduled.add(shift.getEmployee2());
                if(shift.isBusy()) {
                    employeesScheduled.add(shift.getEmployee3());
                }
            } else {
                semiAutoSchdl.setVisibility(View.GONE);
                return 0; // Week not full
            }
        }

        semiAutoSchdl.setVisibility(View.VISIBLE);

        employeesUnscheduled.removeIf(employee -> employeesScheduled.contains(employee.getId()));

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();


        if(!employeesUnscheduled.isEmpty() && !weekChecked){
            weekChecked = true;
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            weekChecked = false;
                            dialog.dismiss();
                        }
                    });
            String message = "";
            if(employeesUnscheduled.size() > 1) {
                alertDialog.setTitle("There are Employees not scheduled this week!");
                message += "Employees missing:\n";
            } else {
                alertDialog.setTitle("There's an employee not scheduled this week!");
                message += "Employee missing:\n";
            }
            for (Employee employee:employeesUnscheduled) {
                message = message + employee.getName() + "\n";
            }

            alertDialog.setMessage(message);
            alertDialog.show();
            return 0;

        } else if(!weekChecked2 && !weekChecked) { // Ask to schedule the rest of the month with this week
            weekChecked2 = true;

            DateTime date = DateTime.forDateOnly(selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth());
            DateTime today = DateTime.today(TimeZone.getTimeZone(ZoneId.systemDefault()));
            date = date.getStartOfMonth();

            if(date.getYear() == today.getYear()){
                if(date.getMonth() < today.getMonth()){
                    return 1; //month from the past
                }
            }else if(date.getYear() < today.getYear()){
                return 1;
            }

            //check if the month needs scheduling
            ArrayList<Shift> shiftsThisMonth = db.getShiftList(date.minusDays(1), date.getEndOfMonth());
            boolean ready = true;
            for (Shift shift : shiftsThisMonth) {
                if (!shift.isFull()) {
                    ready = false;
                    semiAutoSchdl.setVisibility(View.VISIBLE);
                    break;
                }
            }

            if (!ready) { //If month is not already scheduled
                // No button
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                // Yes button
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                drawerLayout.setFocusable(false);
                                Toast.makeText(getApplicationContext(),
                                        "Scheduling",
                                        Toast.LENGTH_SHORT).show();

                                semiAutoScheduler(shiftsThisMonth);

                                dialog.dismiss();
                                drawerLayout.setFocusable(false);

                                Toast.makeText(getApplicationContext(),
                                        "Month Scheduled",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                alertDialog.setTitle("Schedule the rest of the month?");
                alertDialog.setMessage("This week is fully scheduled.\n " +
                        "Would you like to schedule the whole month this way?\n" +
                        "This will override other weeks in the month.");
                alertDialog.show();
                return -1; // no toast
            }
            semiAutoSchdl.setVisibility(View.GONE);
            return 2; // Nonth already scheduled
        }
        return -1;// no toast
    }

    /**
     * Removes duplicates from a sorted shift list, in case a mistake happened.
     * @param shifts
     * @return
     */
    public ArrayList<Shift> solveDuplicatesInWeek(ArrayList<Shift> shifts){
        int i = 0;
        ArrayList<Shift> newShifts = new ArrayList<>();
        for (Shift shift:shifts) {
            if(shift.getSlot() != i){
                newShifts.add(shift);
            }else{
                i = shift.getSlot();
            }
        }
        return newShifts;
    }

    private void semiAutoScheduler(ArrayList<Shift> shifts){

        DateTime date = DateTime.now(TimeZone.getTimeZone(ZoneId.systemDefault()));

        ArrayList<Shift> futureShifts = new ArrayList<>();
        for (Shift shift : weeklyShifts) {
            futureShifts.add(shift.clone());
        }

        for (Shift templateShift:futureShifts) {
            int[] employeesInSlot = db.getSlotAvailability(templateShift.getSlot());
            if(!hasEmployee(employeesInSlot, templateShift.getEmployee1())){
                templateShift.removeEmployee(templateShift.getEmployee1());
                templateShift.setReady(false);
            }
            if(!hasEmployee(employeesInSlot, templateShift.getEmployee2())){
                templateShift.removeEmployee(templateShift.getEmployee2());
                templateShift.setReady(false);
            }
            if(templateShift.isBusy() && !hasEmployee(employeesInSlot, templateShift.getEmployee3())){
                templateShift.removeEmployee(templateShift.getEmployee3());
                templateShift.setReady(false);
            }
        }

        for (Shift shift : shifts) {
            if(shift.getDate().gteq(date)){
                shift.copyFrom(futureShifts.get(shift.getSlot()-1));
            }
        }
        db.replaceShifts(shifts);
    }

    private boolean hasEmployee(int[] list, int employee){
        for (int id : list) {
            if(id == employee){
                return true;
            }
        }
        return false;
    }

}
