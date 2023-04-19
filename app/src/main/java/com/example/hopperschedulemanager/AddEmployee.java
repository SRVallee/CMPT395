package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

public class AddEmployee extends AppCompatActivity {

    //Initialize
    //User info
    String name, primaryEmail, secondaryEmail, primaryPhoneNumber,secondaryPhoneNumber;
    boolean open, close;
    //Availability
    boolean[] slotBools;

    //Booleans to store availability
    boolean addAvailabilityMondayMorning, addAvailabilityMondayEvening;
    boolean addAvailabilityTuesdayMorning, addAvailabilityTuesdayEvening;
    boolean addAvailabilityWednesdayMorning, addAvailabilityWednesdayEvening;
    boolean addAvailabilityThursdayMorning, addAvailabilityThursdayEvening;
    boolean addAvailabilityFridayMorning, addAvailabilityFridayEvening;
    boolean addAvailabilitySaturdayAll, addAvailabilitySundayAll;

    Employee employee = new Employee();

    //User info
    EditText inputAddEmployeeName;
    EditText inputAddEmployeePrimaryMail, inputAddEmployeeSecondaryMail;
    EditText inputAddEmployeePrimaryPhone, inputAddEmployeeSecondaryPhone;
    CheckBox inputAddEmployeeCanOpen, inputAddEmployeeCanClose;

    //Chips to get the input of Availability
    Chip addAvailInputMondayMorning, addAvailInputMondayEvening;
    Chip addAvailInputTuesdayMorning, addAvailInputTuesdayEvening;
    Chip addAvailInputWednesdayMorning, addAvailInputWednesdayEvening;
    Chip addAvailInputThursdayMorning, addAvailInputThursdayEvening;
    Chip addAvailInputFridayMorning, addAvailInputFridayEvening;
    Chip addAvailInputSaturdayAll, addAvailInputSundayAll;

    Button finishAddingButton;
    DBHandler db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        db = new DBHandler(AddEmployee.this);

        inputAddEmployeeName = (EditText) findViewById(R.id.addEmployeeNameEditText);
        inputAddEmployeePrimaryMail = (EditText) findViewById(R.id.addEmployeePrimaryMailEditText);
        inputAddEmployeeSecondaryMail = (EditText) findViewById(R.id.addEmployeeSecondaryMailEditText);
        inputAddEmployeePrimaryPhone = (EditText) findViewById(R.id.addEmployeePrimaryPhoneEditText);
        inputAddEmployeeSecondaryPhone = (EditText) findViewById(R.id.addEmployeeSecondaryPhoneEditText);

        inputAddEmployeeCanOpen = (CheckBox) findViewById(R.id.addEmployeeCanOpenCheckBox);
        inputAddEmployeeCanClose = (CheckBox) findViewById(R.id.addEmployeeCanCloseCheckBox);

        addAvailInputMondayMorning = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipMondayMorning));
        addAvailInputTuesdayMorning = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipTuesdayMorning));
        addAvailInputWednesdayMorning = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipWednesdayMorning));
        addAvailInputThursdayMorning = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipThursdayMorning));
        addAvailInputFridayMorning = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipFridayMorning));

        addAvailInputMondayEvening = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipMondayEvening));
        addAvailInputTuesdayEvening = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipTuesdayEvening));
        addAvailInputWednesdayEvening = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipWednesdayEvening));
        addAvailInputThursdayEvening = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipThursdayEvening));
        addAvailInputFridayEvening = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipFridayEvening));

        addAvailInputSaturdayAll = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipSaturdayAll));
        addAvailInputSundayAll = (Chip) (findViewById(R.id.addEmployeeAvailabilityChipSundayAll));

    }

    public void cancelAddingEmployee(View view){
        redirectClick(this,EmployeeList.class);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectClick(this, EmployeeList.class);
        this.finish();
    }

    public void addEmployeeButton( Employee employee) {
        //Add Employee to DB
        int rowNum = -1;
        try {
            rowNum = db.addOrReplaceEmployee(employee);
        } catch (Exception e) {
            System.out.println("FAILED TO ADD");
            System.out.println(e);
        }

        //Add Availabilities to DB
        int[] slotIDs = employee.getSlotAvailabilities();
        if (rowNum !=- 1) {
            db.addAvailRecords(slotIDs, rowNum);
        }

        //Update Employee list
        Employee senderEmployee= db.selectEmployee(rowNum);
        Intent intentInfoEmployee = new Intent(this,EmployeeInfo.class);
        intentInfoEmployee.putExtra("employee",senderEmployee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentInfoEmployee,b);

        //Then return to the employee list page
        this.finish();
    }

    public void addEmployee(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        //Set user data to then put into the db
        //Employee information input
        name = inputAddEmployeeName.getText().toString();

        if(name.isEmpty()){
            alertDialog.setTitle("No name");
            alertDialog.setMessage("A name is required");
            alertDialog.show();
            return;
        }

        primaryEmail = inputAddEmployeePrimaryMail.getText().toString();
        secondaryEmail = inputAddEmployeeSecondaryMail.getText().toString();
        if(primaryEmail.isEmpty() && secondaryEmail.isEmpty()){
            alertDialog.setTitle("No Email");
            alertDialog.setMessage("An email is Required");
            alertDialog.show();
            return;
        }

        if(!primaryEmail.isEmpty() && !emailIsValid(primaryEmail)){
            alertDialog.setTitle("Invalid email 1 :" + primaryEmail);
            alertDialog.setMessage("Please enter a valid email");
            alertDialog.show();
            return;
        }
        if(!secondaryEmail.isEmpty() && !emailIsValid(secondaryEmail)){
            alertDialog.setTitle("Invalid email 2");
            alertDialog.setMessage("Please enter a valid email");
            alertDialog.show();
            return;
        }

        primaryPhoneNumber = inputAddEmployeePrimaryPhone.getText().toString();
        secondaryPhoneNumber = inputAddEmployeeSecondaryPhone.getText().toString();

        if(primaryPhoneNumber.isEmpty() && secondaryPhoneNumber.isEmpty()){
            alertDialog.setTitle("No Phone Number");
            alertDialog.setMessage("A phone number is Required");
            alertDialog.show();
            return;
        }

        if(!primaryPhoneNumber.isEmpty() && !phoneNumberIsValid(primaryPhoneNumber)){
            alertDialog.setTitle("Invalid Phone Number 1");
            alertDialog.setMessage("Please enter a valid Phone number");
            alertDialog.show();
            return;
        }
        if(!secondaryPhoneNumber.isEmpty() && !phoneNumberIsValid(secondaryPhoneNumber)){
            alertDialog.setTitle("Invalid Phone Number 2");
            alertDialog.setMessage("Please enter a valid Phone number");
            alertDialog.show();
            return;
        }
        open = inputAddEmployeeCanOpen.isChecked();
        close = inputAddEmployeeCanClose.isChecked();
        //Employee Availability
        addAvailabilitySundayAll = addAvailInputSundayAll.isChecked();
        //Morning
        addAvailabilityMondayMorning = addAvailInputMondayMorning.isChecked();
        addAvailabilityTuesdayMorning = addAvailInputTuesdayMorning.isChecked();
        addAvailabilityWednesdayMorning = addAvailInputWednesdayMorning.isChecked();
        addAvailabilityThursdayMorning = addAvailInputThursdayMorning.isChecked();
        addAvailabilityFridayMorning = addAvailInputFridayMorning.isChecked();
        //Evening
        addAvailabilityMondayEvening = addAvailInputMondayEvening.isChecked();
        addAvailabilityTuesdayEvening = addAvailInputTuesdayEvening.isChecked();
        addAvailabilityWednesdayEvening = addAvailInputWednesdayEvening.isChecked();
        addAvailabilityThursdayEvening = addAvailInputThursdayEvening.isChecked();
        addAvailabilityFridayEvening = addAvailInputFridayEvening.isChecked();

        addAvailabilitySaturdayAll = addAvailInputSaturdayAll.isChecked();


        boolean[] bools = {
                addAvailabilitySundayAll,
                addAvailabilityMondayMorning, addAvailabilityMondayEvening,
                addAvailabilityTuesdayMorning, addAvailabilityTuesdayEvening,
                addAvailabilityWednesdayMorning, addAvailabilityWednesdayEvening,
                addAvailabilityThursdayMorning, addAvailabilityThursdayEvening,
                addAvailabilityFridayMorning, addAvailabilityFridayEvening,
                addAvailabilitySaturdayAll
        };
        slotBools = bools;

        //User id as input?
        //Generate new employee

        //Set values
        employee.setName(name);
        if(!primaryEmail.isEmpty()){
            employee.setEmails(primaryEmail, secondaryEmail);
        }
        else{
            employee.setEmails(secondaryEmail, primaryEmail);
        }

        if(!primaryPhoneNumber.isEmpty()){
            employee.setPhoneNumbers(primaryPhoneNumber, secondaryPhoneNumber);
        }
        else{
            employee.setPhoneNumbers(secondaryPhoneNumber, primaryPhoneNumber);
        }
        employee.setOpen(open);
        employee.setClose(close);
        employee.setAvailabilities(slotBools);

        //employee = newEmployee;

        //Availability
        //Store as an array of booleans

        addEmployeeButton(employee);
    }


    private boolean phoneNumberIsValid(String phoneNumber ){
        return (phoneNumber.matches("\\d+")
                &&
                phoneNumber.length() == 10);
    }

    private boolean emailIsValid(String email){
        return (email.matches("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)"));
    }

}