package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.HashSet;

import hirondelle.date4j.DateTime;

public class MonthlyStats extends AppCompatActivity {

    DrawerLayout drawerLayout;
    public ListView nameView, nameNonView;
    int empId1, empId2, empId3;
    int tempEmpId;
    int totalNum, monthDay, monthDayBusy, monthEnd, monthEndBusy;
    int shiftTacker;
    String temp1, temp2;
    EditText startDate, endDate;
    DateTime tempDate;
    ArrayList<Employee> allEmpList = new ArrayList<>();
    ArrayList<Employee> workEmpList = new ArrayList<>();
    ArrayList<Shift> shiftList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_stats);
        drawerLayout = findViewById(R.id.drawer_monthly_stats);
        nameView = findViewById(R.id.employeeList);
        nameNonView = findViewById(R.id.employeeListNon);

        DBHandler db = new DBHandler(MonthlyStats.this);

        //Get date
        Intent intentFromSchedule = getIntent();
        tempDate = (DateTime) intentFromSchedule.getSerializableExtra("DateTime");
        //This date is first day of the month

        //Display date range to the user
        //Start
        startDate = (EditText) findViewById(R.id.startMonthTextDate);
        temp1 = tempDate.toString();
        startDate.setText(temp1.substring(0, 10));
        startDate.setEnabled(false);
        //End
        endDate = (EditText) findViewById(R.id.endMonthTextDate);
        temp2 = tempDate.getEndOfMonth().toString();
        endDate.setText(temp2.substring(0, 10));
        endDate.setEnabled(false);

        //Use date to get last day of the month
        shiftList = db.getShiftList(
                tempDate.getStartOfMonth().minusDays(1),  //First day of the month
                tempDate.getEndOfMonth());  //Last day of the month

        //Get all employees
        allEmpList = db.selectEmployeeList(true);

        //Get working that month employees
        //itr through the month and get all employees from the month
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
        ArrayList<Employee> notEmpList = new ArrayList<Employee>(allEmpList);
        notEmpList.removeAll(workEmpList);

        //Remove duplicates from list for working employees
        HashSet<Employee> set1 = new HashSet<Employee>();
        set1.addAll(workEmpList);

        //This gets the numbers of shifts the employee works in the given weed
        ArrayList<Object> disWorkEmpList = new ArrayList<>();
        shiftTacker = 0;
        for (Employee emp : set1) {
            tempEmpId = emp.getId();
            totalNum = 0; monthDay = 0; monthDayBusy = 0; monthEnd = 0; monthEndBusy = 0;
            for (Shift shift : shiftList) {
                if (tempEmpId == shift.getEmployee1()
                        || tempEmpId == shift.getEmployee2()
                        || tempEmpId == shift.getEmployee3()) {
                    totalNum++;

                    //OLD CODE HERE JUST INCASE
                    //Weekdays
                    //Take shift and itr through and check if there is another shift with the same day but different id
                    //for (Shift shift2 : shiftList) {
                    //    if ((shift.getDate().getDayOfYear() == shift2.getDate().getDayOfYear()) && (shift.getId() != shift2.getId())) {
                    //        if (shift.isBusy() == true) {
                    //            monthDayBusy++;
                    //        } else {
                    //            monthDay++;
                    //        }
                    //    }
                    //}
                    //Weekends
                    //for (Shift shift2 : shiftList) {
                    //    if (((shift.getSlot() == 0) || (shift.getSlot() == 11)) && (shift.getId() == shift2.getId())) {
                    //        if (shift.isBusy() == true) {
                    //            monthEndBusy++;
                    //        } else {
                    //            monthEnd++;
                    //        }
                    //    }
                    //}

                    //Gets weekends
                    if ((shift.getSlot() == 1) || (shift.getSlot() == 12)) {
                        if (shift.isBusy()) {
                            monthEndBusy++;
                        } else {
                            monthEnd++;
                        }
                    //Gets weekdays
                    } else {
                        if (shift.isBusy()) {
                            monthDayBusy++;
                        } else {
                            monthDay++;
                        }
                    }
                }
                shiftTacker++;
            }

            //Prep results
            String results = "Total Number of Shift(s): " + totalNum +
                    "\nWeekday Shifts: " + monthDay + "    Busy Weekday Shifts: " + monthDayBusy +
                    "\nWeekend Shifts: " + monthEnd + "    Busy Weekday Shifts: " + monthEndBusy;

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
        redirectClick(this,Schedule.class);
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