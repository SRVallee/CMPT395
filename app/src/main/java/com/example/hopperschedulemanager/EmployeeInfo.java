package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.chip.Chip;

import java.io.IOException;


public class EmployeeInfo extends AppCompatActivity {

    DrawerLayout drawerLayout;

    //Initialize
    //Employee variables
    TextView name;
    TextView primaryMail, secondaryMail;
    TextView primaryPhone, secondaryPhone;
    ImageView closeIcon, openIcon;
    LinearLayout email2Container, phone2Container;
    Button employStatus;

    Employee employee;

    //Availability
    Chip infoAvailMondayMorning, infoAvailMondayEvening;
    Chip infoAvailTuesdayMorning, infoAvailTuesdayEvening;
    Chip infoAvailWednesdayMorning, infoAvailWednesdayEvening;
    Chip infoAvailThursdayMorning, infoAvailThursdayEvening;
    Chip infoAvailFridayMorning, infoAvailFridayEvening;
    Chip infoAvailSaturdayAll, infoAvailSundayAll;

    //Make Availability Array
    Integer size = 12;
    boolean[] array = new boolean[size];

    DBHandler db;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emoployee_info);

        db = new DBHandler(this);

        drawerLayout = findViewById(R.id.drawer_employee_info);

        name = findViewById(R.id.infoEmployeeNameTextView);
        primaryMail = findViewById(R.id.infoEmployeePrimaryMailDisplayTextView);
        secondaryMail = findViewById(R.id.infoEmployeeSecondaryMailDisplayTextView);
        email2Container = findViewById(R.id.infoEmployeeSecondaryMailLayout);
        primaryPhone = findViewById(R.id.infoEmployeePrimaryPhoneDisplayTextView);
        secondaryPhone = findViewById(R.id.infoEmployeeSecondaryPhoneDisplayTextView);
        phone2Container = findViewById(R.id.infoEmployeeSecondaryPhoneLayout);
        openIcon = findViewById(R.id.infoEmployeeCanOpenIcon);
        closeIcon = findViewById(R.id.infoEmployeeCanCloseIcon);

        Intent intentFromEmployeeList = getIntent();
        employee = (Employee) intentFromEmployeeList.getSerializableExtra("employee");
        name.setText(employee.getName());
        setOpenAndClose(employee);
        setPhones(employee.getPhoneNumbers());
        setEmails(employee.getEmails());
        array = employee.getBoolAvailabilities();

        employStatus = findViewById(R.id.buttonEmployChange);
        if (employee.isEmployed()){
            employStatus.setText("DELETE EMPLOYEE");
        } else {
            employStatus.setText("RE-ENABLE EMPLOYEE");
        }

        //For the availability, set the chip as checkable here so that the user cant check
        //Morning
        infoAvailMondayMorning = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipMondayMorning));
        infoAvailMondayMorning.setCheckable(true);
        infoAvailTuesdayMorning = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipTuesdayMorning));
        infoAvailTuesdayMorning.setCheckable(true);
        infoAvailWednesdayMorning = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipWednesdayMorning));
        infoAvailWednesdayMorning.setCheckable(true);
        infoAvailThursdayMorning = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipThursdayMorning));
        infoAvailThursdayMorning.setCheckable(true);
        infoAvailFridayMorning = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipFridayMorning));
        infoAvailFridayMorning.setCheckable(true);

        //Evening
        infoAvailMondayEvening = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipMondayEvening));
        infoAvailMondayEvening.setCheckable(true);
        infoAvailTuesdayEvening = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipTuesdayEvening));
        infoAvailTuesdayEvening.setCheckable(true);
        infoAvailWednesdayEvening = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipWednesdayEvening));
        infoAvailWednesdayEvening.setCheckable(true);
        infoAvailThursdayEvening = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipThursdayEvening));
        infoAvailThursdayEvening.setCheckable(true);
        infoAvailFridayEvening = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipFridayEvening));
        infoAvailFridayEvening.setCheckable(true);

        infoAvailSaturdayAll = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipSatAll));
        infoAvailSaturdayAll.setCheckable(true);
        infoAvailSundayAll = (Chip) (findViewById(R.id.infoEmployeeAvailabilityChipSunAll));
        infoAvailSundayAll.setCheckable(true);

        //Set availability info
        infoAvailSundayAll.setChecked(array[0]);

        infoAvailMondayMorning.setChecked(array[1]);
        infoAvailMondayEvening.setChecked(array[2]);

        infoAvailTuesdayMorning.setChecked(array[3]);
        infoAvailTuesdayEvening.setChecked(array[4]);

        infoAvailWednesdayMorning.setChecked(array[5]);
        infoAvailWednesdayEvening.setChecked(array[6]);

        infoAvailThursdayMorning.setChecked(array[7]);
        infoAvailThursdayEvening.setChecked(array[8]);

        infoAvailFridayMorning.setChecked(array[9]);
        infoAvailFridayEvening.setChecked(array[10]);

        infoAvailSaturdayAll.setChecked(array[11]);
/*
        FloatingActionButton edit_employee = (FloatingActionButton) findViewById(R.id.edit_employee_button);
        edit_employee.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeInfo.this , EditEmployee.class);
                startActivity(intent);
            }
        });
`*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectClick(this,EmployeeList.class);
        finish();
    }

    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view){
        MainActivity.openDrawer(drawerLayout);

    }

    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);

    }

    public void clickHome(View view){
        EmployeeList.actemplist.finish();
        redirectClick(this,MainActivity.class);
        this.finish();

    }

    public void clickSchedule(View view) {
        redirectClick(this, Schedule.class);
    }

    public void clickSettings(View view) {
        redirectClick(this,Settings.class);
    }

    public void clickEmployeeList(View view) {
        EmployeeList.actemplist.finish();
        redirectClick(this, EmployeeList.class);
        this.finish();
    }

    public void clickEdit(View view) {
        Intent intenteditEmployee = new Intent(EmployeeInfo.this, EditEmployee.class);
        intenteditEmployee.putExtra("employee", employee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intenteditEmployee,b);
        this.finish();
    }

    public void clickAvailability(View view){
        redirectClick(this, Schedule.class); // place holder.
    }

    public void clickDelete(View view){
        if (employee.isEmployed()) {
            Intent intentDeleteEmployee = new Intent(EmployeeInfo.this, DeleteEmployee.class);
            intentDeleteEmployee.putExtra("employee", employee);
            Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intentDeleteEmployee, b);
            this.finish();
        } else {
            employee.setEmployed(true);
            employStatus.setText("DELETE EMPLOYEE");
            db.addOrReplaceEmployee(employee);

            EmployeeList.actemplist.finish();
        }
    }

    private void setOpenAndClose(Employee employee) {
        if(employee.canOpen()){
            openIcon.setImageResource(android.R.drawable.checkbox_on_background);
        }
        else{openIcon.setImageResource(android.R.drawable.checkbox_off_background);}

        if(employee.canClose()){
            closeIcon.setImageResource(android.R.drawable.checkbox_on_background);
        }
        else{closeIcon.setImageResource(android.R.drawable.checkbox_off_background);}
    }

    private void setPhones(String[] numbers) {

        primaryPhone.setText(numbers[0]);

        if (numbers[1] == null  || numbers[1].matches("")){
            phone2Container.setVisibility(View.GONE);
        }
        else{
            phone2Container.setVisibility(View.VISIBLE);
            secondaryPhone.setText(numbers[1]);
        }

    }

    private void setEmails(String[] emails) {
        primaryMail.setText(emails[0]);

        if (emails[1] == null || emails[1].matches("")) {
            email2Container.setVisibility(View.GONE);
        }
        else{
            secondaryMail.setText(emails[1]);
            email2Container.setVisibility(View.VISIBLE);
            }
       }

}
