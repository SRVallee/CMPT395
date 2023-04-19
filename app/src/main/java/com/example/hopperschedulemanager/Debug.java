package com.example.hopperschedulemanager;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import hirondelle.date4j.DateTime;

public class Debug extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Button addBtn;
    Button editBtn;
    Button deleteBtn;
    Button selectBtn;
    Button slotsBtn;
    TextView mainText;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHandler(Debug.this);

        setContentView(R.layout.activity_debug);
        drawerLayout = findViewById(R.id.drawer_settings);
        addBtn = findViewById(R.id.button_add);
        editBtn = findViewById(R.id.button_edit);
        deleteBtn = findViewById(R.id.button_delete);
        selectBtn = findViewById(R.id.button_list);
        slotsBtn = findViewById(R.id.button_slots);
        deleteBtn = findViewById(R.id.button_delete_slt);

        mainText = findViewById(R.id.main_txt);

        mainText.setMovementMethod(new ScrollingMovementMethod());
        mainText.setText("Debug Page, Please Ignore.");

    }

    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view){
        MainActivity.openDrawer(drawerLayout);

    }

    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);

    }

    public void clickHome(View view){
        MainActivity.redirectClick(this,MainActivity.class);
        this.finish();

    }

    public void clickEmployeeList(View view){
        MainActivity.redirectClick(this,EmployeeList.class);
        this.finish();
    }

    public void clickSchedule(View view){
        MainActivity.redirectClick(this,Schedule.class);
        this.finish();
    }

    public void clickSettings(View view) {
        MainActivity.redirectClick(this,Settings.class);
    }

    public void clickAdd(View view){
        boolean[] avabs = new boolean[12];
        avabs[0] = true;
        avabs[11] = true;
        boolean[] avabsOpen = new boolean[12];
        for (int i = 1; i < 12; i = i+2) {
            avabsOpen[i] = true;
        }
        avabsOpen[0] = true;
        boolean[] avabsClose = new boolean[12];
        for (int i = 0; i < 12; i = i+2) {
            avabsClose[i] = true;
        }
        avabsClose[11] = true;

        boolean[] avabsAll = new boolean[12];
        Arrays.fill(avabsAll, true);

        Employee e_test = new Employee();
        e_test.setName("Test Dude 1");
        e_test.setClose(true);
        e_test.setOpen(true);
        e_test.setEmails("goof@lol.ca", null);
        e_test.setPhoneNumbers("7804727272", "1234567890");
        e_test.setEmployed(true);
        e_test.setAvailabilities(avabs);

        Employee e_test2 = new Employee();
        e_test2.setName("Emily Open");
        e_test2.setClose(false);
        e_test2.setOpen(true);
        e_test2.setEmails("ewong@fcc.citadel.space", null);
        e_test2.setPhoneNumbers("7771230912", null);
        e_test2.setEmployed(true);
        e_test2.setAvailabilities(avabsOpen);

        Employee e_test3 = new Employee();
        e_test3.setName("Garrus Close");
        e_test3.setClose(true);
        e_test3.setOpen(false);
        e_test3.setEmails("scoped@dropped.th", "garrusv@c-sec.citadel.space");
        e_test3.setPhoneNumbers("0091691337", null);
        e_test3.setEmployed(true);
        e_test3.setAvailabilities(avabsClose);

        Employee e_test4 = new Employee();
        e_test4.setName("Everyday noOpenClose");
        e_test4.setClose(false);
        e_test4.setOpen(false);
        e_test4.setEmails("lol@.a.ca", null);
        e_test4.setPhoneNumbers("1234567890", null);
        e_test4.setEmployed(true);
        e_test4.setAvailabilities(avabsAll);

        Employee[] emps = {e_test, e_test2, e_test3, e_test4};
        for (int i = 0; i < emps.length; i++){
            int rowNum = -1;
            rowNum = db.addOrReplaceEmployee(emps[i]);
            db.addAvailRecords(emps[i].getSlotAvailabilities(), rowNum);
        }

        String s = "Added Employees";
        mainText.setText(s);

    }

    public void clickEdit(View view){
        ArrayList<Employee> es = db.selectEmployeeList(true);
        Employee e = es.get(0);
        e.setName("CHANGED NAME");
        e.setEmails("EDIT@successful.org", "ravioli@me.co.uk");

        db.addOrReplaceEmployee(e);


        String s = "Edited Employee";
        mainText.setText(s);

    }


    public void clickUnemploy(View view){
        ArrayList<Employee> es = db.selectEmployeeList(true);
        if (es.size() >0) {
            db.deleteEmployee(es.get(0));
        }
        String s = "Employee Unemployed";
        mainText.setText(s);
    }

    public void clickDeleteAllEmps(View view){
        int x = db.forceDeleteAll();
        String s = "DELETED" + x + "Employees";
        mainText.setText(s);
    }

    public void clickDeleteSlots(View view){
        db.deleteAllSlots();
        String s = "DELETED Slots";
        mainText.setText(s);
    }

    public void clickSelectAll(View view){
        ArrayList<Employee> es = db.selectEmployeeList(true);
        StringBuilder s = new StringBuilder("Employees:\n");
        for (Employee e : es){
            s.append("ID: ");
            s.append(e.getId());
            s.append("\nName: ");
            s.append(e.getName());
            s.append("\nEmails: ");
            for (int i = 0; i < 2; i++) {
                s.append(e.getEmails()[i]).append(' ');
            }
            s.append("\nPhone Numbers: ");
            for (int i = 0; i < 2; i++) {
                s.append(e.getPhoneNumbers()[i]).append(' ');
            }
            s.append("\n Avabs: ");
            int[] avabs = e.getSlotAvailabilities();
            for (int i = 0; i < avabs.length; i++) {
                s.append(avabs[i]).append(" ");
            }

            s.append("\n\n");
        }

        mainText.setText(s.toString());

    }

    public void clickSelectSlots(View view){
        ArrayList<Slot> slts = db.getSlotList();
        StringBuilder str = new StringBuilder("Slots:");
        for (Slot s:slts) {
            str.append("\n\nID: ").append(s.getId());
            str.append("\nDay: ").append(s.getDayOfWeek());
            str.append("\nTimes start: ").append(s.getStartTime());
            str.append("\nTimes end: ").append(s.getEndTime());
            boolean opening = s.isOpeningSlot();
            boolean closing = s.isClosingSlot();
            if (opening){
                str.append("\nOpening slot");
            }
            if (closing){
                str.append("\nClosing slot");
            }
        }

        mainText.setText(str.toString());
    }

    public void clickAddShifts(View view){
        Shift s1 = new Shift();
        Shift s2 = new Shift();
        Shift s3 = new Shift();
        DateTime d1 = new DateTime(2022, 10, 4, 12, 0, 0, 0);
        DateTime d2 = new DateTime(2022, 4,  4, 12, 0, 0, 0);
        DateTime d3 = new DateTime(2022, 11, 4, 12, 0, 0, 0);

        s1.setDate(d1);
        s2.setDate(d2);
        s3.setDate(d3);

        ArrayList<Employee> x = db.selectEmployeeList(true);
        s1.setEmployee1(x.get(0).getId());
        s2.setEmployee1(x.get(1).getId());
        s2.setEmployee2(x.get(2).getId());

        s1.setSlot(4);
        s2.setSlot(2);
        s3.setSlot(10);

        db.addOrReplaceShift(s1);
        db.addOrReplaceShift(s2);
        db.addOrReplaceShift(s3);

        mainText.setText("Shifts added");
    }

    public void clickEditShift(View view){
        Shift s1 = new Shift();
        ArrayList<Employee> x = db.selectEmployeeList(true);
        s1.setId(3);
        s1.setEmployee1(x.get(0).getId());
        s1.setSlot(10);
        DateTime d3 = new DateTime(2022, 11, 4, 12, 0, 0, 0);
        s1.setDate(d3);

        db.addOrReplaceShift(s1);

        mainText.setText("Shift edited");
    }

    public void clickDeleteShift(View view){
        ArrayList<Shift> shfs = db.getShiftList(
                new DateTime(2000, 1, 1, 0, 0, 0, 0),
                new DateTime(2025, 1, 1, 0, 0, 0, 0));
        for (Shift s:shfs) {
            db.deleteShift(s);
        }

        mainText.setText("Shift deleted");
    }

    public void clickSelectShifts(View view){
        ArrayList<Shift> shfs = db.getShiftList(
                new DateTime(2022, 1, 1, 0, 0, 0, 0),
                new DateTime(2023, 12, 31, 23, 59, 59, 0));
        StringBuilder str = new StringBuilder("Shifts:\n");
        for (Shift s:shfs) {
            str.append("ID: " + s.getId() + '\n');
            String[] es = {String.valueOf(s.getEmployee1()), String.valueOf(s.getEmployee2()), String.valueOf(s.getEmployee3())};
            String emps = String.join(", ", es);
            str.append("Employee IDs: " + emps + '\n');
            str.append("Slot ID: " + s.getSlot());
            str.append("\n" + s.getDate());
            str.append("\n\n");
        }

        mainText.setText(str.toString());

    }

    public void clickSelectFullShift(View view){
        StringBuilder str = new StringBuilder();
        Shift s = db.getFullShift(1);
        str.append("ID: " + s.getId() + '\n');
        String[] es = {String.valueOf(s.getEmployee1()), String.valueOf(s.getEmployee2()), String.valueOf(s.getEmployee3())};
        String emps = String.join(", ", es);
        str.append("Employee IDs: " + emps + '\n');
        str.append("Slot ID: " + s.getSlot());
        str.append("\n" + s.getDate());
        Employee[] employees = {s.getEmployeeObj1(), s.getEmployeeObj2(), s.getEmployeeObj3()};
        for (int i = 0; i < employees.length; i++) {
            str.append("\nEmployee " + (i+1) + ": ");
            if (employees[i] != null) {
                str.append(employees[i].getName());
                str.append("\nOpen: " + employees[i].canOpen() + " Close: " + employees[i].canClose());
                str.append("\nEmployed: " + employees[i].isEmployed());
                str.append("\n");
            } else {
                str.append("NONE");
            }
        }

        mainText.setText(str.toString());

    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);

    }

}
