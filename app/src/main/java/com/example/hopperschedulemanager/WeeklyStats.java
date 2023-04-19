package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

import hirondelle.date4j.DateTime;

public class WeeklyStats extends AppCompatActivity {

    DrawerLayout drawerLayout;
    public ListView nameView, nameNonView;
    int empId1, empId2, empId3;
    int tempEmpId;
    int totalNum, weekDay, weekDayBusy, weekEnd, weekEndBusy;
    int shiftTacker;

    EditText startDate, endDate;

    DateTime tempDate;

    ArrayList<Employee> allEmpList = new ArrayList<>();
    ArrayList<Employee> workEmpList = new ArrayList<>();
    ArrayList<Shift> shiftList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weekly_stats);
        drawerLayout = findViewById(R.id.drawer_weekly_stats);
        nameView = findViewById(R.id.employeeList);
        nameNonView = findViewById(R.id.employeeListNon);

        DBHandler db = new DBHandler(WeeklyStats.this);

        //Get date
        Intent intentFromSchedule = getIntent();
        tempDate = (DateTime) intentFromSchedule.getSerializableExtra("DateTime");
        //This date is set to the monday of the week that the user was viewing

        //Display date range to the user
        //Start
        startDate = (EditText) findViewById(R.id.startTextDate);
        startDate.setText(tempDate.minusDays(1).toString());
        startDate.setEnabled(false);
        //End
        endDate = (EditText) findViewById(R.id.endTextDate);
        endDate.setText(tempDate.plusDays(5).toString());
        endDate.setEnabled(false);

        //Use date to get week of shifts
        shiftList = db.getShiftList(
                tempDate.minusDays(1),  //Sun
                tempDate.plusDays(5));  //Sat

        //Get all employees
        allEmpList = db.selectEmployeeList(true);

        //Get working on that week employees
        //itr through week and get all employees from the week
        for (Shift shift : shiftList) {
            empId1 = shift.getEmployee1();
            if (empId1 != -1) {
                workEmpList.add(db.selectEmployee(empId1));
            }
            empId2 = shift.getEmployee2();
            if (empId2 != -1) {
                workEmpList.add(db.selectEmployee(empId2));
            }
            empId3 = shift.getEmployee3();
            if (empId3 != -1) {
                workEmpList.add(db.selectEmployee(empId3));
            }
        }

        //Get non working employees
        ArrayList<Employee> notEmpList = new ArrayList<>(allEmpList);
        notEmpList.removeAll(workEmpList);

        //Remove duplicates from list for working employees
        HashSet<Employee> set1 = new HashSet<>();
        set1.addAll(workEmpList);

        // NOTE IF CHANGE THE STARTING POINT REWORK THIS AS WELL BECAUSE THIS ASSUMES MONDAY IS STARTING POINT
        //This gets the numbers of shifts the employee works in the given weed
        ArrayList<Object> disWorkEmpList = new ArrayList<>();
        shiftTacker = 0;
        for (Employee emp : set1) {
            tempEmpId = emp.getId();
            shiftTacker = 0;
            totalNum=0; weekDay=0; weekDayBusy=0; weekEnd=0; weekEndBusy=0;
            for (Shift shift : shiftList) {
                //The list should be organized such that it goes m-s
                if (tempEmpId == shift.getEmployee1()
                        || tempEmpId == shift.getEmployee2()
                        || tempEmpId == shift.getEmployee3()) {
                    totalNum++;
                    //Weekdays
                    if (shiftTacker > 0 && shiftTacker < 11) {
                        if (shift.isBusy()) {
                            weekDayBusy++;
                        } else {
                            weekDay++;
                        }
                    }
                    //Weekends
                    if (shiftTacker == 11 || shiftTacker == 0 ) {
                        if (shift.isBusy()) {
                            weekEndBusy++;
                        } else {
                            weekEnd++;
                        }
                    }
                }
                shiftTacker++;
            }

            //Prep results
            String results = "Total Number of Shift(s): " + totalNum +
                    "\nWeekday Shifts: " + weekDay + "    Busy Weekday Shifts: " + weekDayBusy +
                    "\nWeekend Shifts: " + weekEnd + "    Busy Weekday Shifts: " + weekEndBusy;

            //Add emp and results to the display list
            disWorkEmpList.add(emp);
            disWorkEmpList.add(results);
        }

        //Display working employees and their stats
        ArrayAdapter<Object> employeeAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, disWorkEmpList);
        nameView.setAdapter(employeeAdapter);

        //Display non working employees
        ArrayAdapter<Employee> employeeNonAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notEmpList);
        nameNonView.setAdapter(employeeNonAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectClick(this, WeekView.class);
        this.finish();
    }

    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view){MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){MainActivity.closeDrawer(drawerLayout);}

    public void clickHome(View view) {redirectClick(this, MainActivity.class);}

    public void clickSchedule(View view) {
        redirectClick(this, Schedule.class);
    }

    public void clickSettings(View view){
        redirectClick(this,Settings.class);
    }
}