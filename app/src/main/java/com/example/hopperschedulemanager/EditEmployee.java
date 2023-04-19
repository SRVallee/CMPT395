package com.example.hopperschedulemanager;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.io.IOException;

public class EditEmployee extends AppCompatActivity {

    //Initialize
    //User info
    String name, primaryEmail, secondaryEmail, primaryPhoneNumber,secondaryPhoneNumber;
    boolean open, close;

    //Booleans to store availability
    boolean editAvailabilityMondayMorning, editAvailabilityMondayEvening;
    boolean editAvailabilityTuesdayMorning, editAvailabilityTuesdayEvening;
    boolean editAvailabilityWednesdayMorning, editAvailabilityWednesdayEvening;
    boolean editAvailabilityThursdayMorning, editAvailabilityThursdayEvening;
    boolean editAvailabilityFridayMorning, editAvailabilityFridayEvening;
    boolean editAvailabilitySaturdayAll, editAvailabilitySundayAll;

    //Make Availability Array
    Integer size = 12;
    boolean[] array = new boolean[size];

    //Generate employee object
    Employee employee;

    //User info
    EditText inputEditEmployeeName;
    EditText inputEditEmployeePrimaryMail, inputEditEmployeeSecondaryMail;
    EditText inputEditEmployeePrimaryPhone, inputEditEmployeeSecondaryPhone;
    CheckBox inputEditEmployeeCanOpen, inputEditEmployeeCanClose;

    //Chips to get the input of Availability
    Chip editAvailInputMondayMorning, editAvailInputMondayEvening;
    Chip editAvailInputTuesdayMorning, editAvailInputTuesdayEvening;
    Chip editAvailInputWednesdayMorning, editAvailInputWednesdayEvening;
    Chip editAvailInputThursdayMorning, editAvailInputThursdayEvening;
    Chip editAvailInputFridayMorning, editAvailInputFridayEvening;
    Chip editAvailInputSaturday, editAvailInputSunday;

    DBHandler db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_employee);

        db = new DBHandler(EditEmployee.this);

        inputEditEmployeeName = (EditText) findViewById(R.id.editEmployeeNameEditText);

        //Get and set employee info
        Intent intentFromEmployeeInfo = getIntent();
        employee = (Employee) intentFromEmployeeInfo.getSerializableExtra("employee");

        inputEditEmployeeName.setText(employee.getName());
        inputEditEmployeePrimaryMail = (EditText) findViewById(R.id.editEmployeePrimaryMailEditText);
        inputEditEmployeePrimaryMail.setText(employee.getEmails()[0]);
        inputEditEmployeeSecondaryMail = (EditText) findViewById(R.id.editEmployeeSecondaryMailEditText);
        inputEditEmployeeSecondaryMail.setText(employee.getEmails()[1]);
        inputEditEmployeePrimaryPhone = (EditText) findViewById(R.id.editEmployeePrimaryPhoneEditText);
        inputEditEmployeePrimaryPhone.setText(employee.getPhoneNumbers()[0]);
        inputEditEmployeeSecondaryPhone = (EditText) findViewById(R.id.editEmployeeSecondaryPhoneEditText);
        inputEditEmployeeSecondaryPhone.setText(employee.getPhoneNumbers()[1]);
        inputEditEmployeeCanOpen = (CheckBox) findViewById(R.id.editEmployeeCanOpenCheckBox);
        inputEditEmployeeCanOpen.setChecked(employee.canOpen());
        inputEditEmployeeCanClose = (CheckBox) findViewById(R.id.editEmployeeCanCloseCheckBox);
        inputEditEmployeeCanClose.setChecked(employee.canClose());
        array = employee.getBoolAvailabilities();

        //Get availability info
        //Morning
        editAvailInputMondayMorning = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipMondayMorning));
        editAvailInputTuesdayMorning = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipTuesdayMorning));
        editAvailInputWednesdayMorning = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipWednesdayMorning));
        editAvailInputThursdayMorning = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipThursdayMorning));
        editAvailInputFridayMorning = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipFridayMorning));
        //Evening
        editAvailInputMondayEvening = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipMondayEvening));
        editAvailInputTuesdayEvening = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipTuesdayEvening));
        editAvailInputWednesdayEvening = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipWednesdayEvening));
        editAvailInputThursdayEvening = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipThursdayEvening));
        editAvailInputFridayEvening = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipFridayEvening));
        //Weekend
        editAvailInputSaturday = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipSaturdayAll));
        editAvailInputSunday = (Chip) (findViewById(R.id.editEmployeeAvailabilityChipSundayAll));

        //Set availability info
        editAvailInputSunday.setChecked(array[0]);

        editAvailInputMondayMorning.setChecked(array[1]);
        editAvailInputMondayEvening.setChecked(array[2]);

        editAvailInputTuesdayMorning.setChecked(array[3]);
        editAvailInputTuesdayEvening.setChecked(array[4]);

        editAvailInputWednesdayMorning.setChecked(array[5]);
        editAvailInputWednesdayEvening.setChecked(array[6]);

        editAvailInputThursdayMorning.setChecked(array[7]);
        editAvailInputThursdayEvening.setChecked(array[8]);

        editAvailInputFridayMorning.setChecked(array[9]);
        editAvailInputFridayEvening.setChecked(array[10]);

        editAvailInputSaturday.setChecked(array[11]);


    }


    public void cancelsaveEmployee(View view){
        //Do nothing and just return to the employee list page
        Intent intentinfoEmployee = new Intent(this,EmployeeInfo.class);
        intentinfoEmployee.putExtra("employee",employee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentinfoEmployee,b);
        this.finish();
    }

    public void saveEmployeeButton(View view) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        //Set user data to then put into the db
        //Employee Object
        name = inputEditEmployeeName.getText().toString();

        if(name.isEmpty()){
            alertDialog.setTitle("No name");
            alertDialog.setMessage("A name is required");
            alertDialog.show();
            return;
        }

        primaryEmail = inputEditEmployeePrimaryMail.getText().toString();
        secondaryEmail = inputEditEmployeeSecondaryMail.getText().toString();
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

        primaryPhoneNumber = inputEditEmployeePrimaryPhone.getText().toString();
        secondaryPhoneNumber = inputEditEmployeeSecondaryPhone.getText().toString();

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


        open = inputEditEmployeeCanOpen.isChecked();
        close = inputEditEmployeeCanClose.isChecked();
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


        //Availability
        //Morning
        editAvailabilityMondayMorning = editAvailInputMondayMorning.isChecked();
        editAvailabilityTuesdayMorning = editAvailInputTuesdayMorning.isChecked();
        editAvailabilityWednesdayMorning = editAvailInputWednesdayMorning.isChecked();
        editAvailabilityThursdayMorning = editAvailInputThursdayMorning.isChecked();
        editAvailabilityFridayMorning = editAvailInputFridayMorning.isChecked();
        //Evening
        editAvailabilityMondayEvening = editAvailInputMondayEvening.isChecked();
        editAvailabilityTuesdayEvening = editAvailInputTuesdayEvening.isChecked();
        editAvailabilityWednesdayEvening = editAvailInputWednesdayEvening.isChecked();
        editAvailabilityThursdayEvening = editAvailInputThursdayEvening.isChecked();
        editAvailabilityFridayEvening = editAvailInputFridayEvening.isChecked();
        //Weekend
        editAvailabilitySaturdayAll = editAvailInputSaturday.isChecked();
        editAvailabilitySundayAll = editAvailInputSunday.isChecked();

        boolean[] bools = {
                editAvailabilitySundayAll,
                editAvailabilityMondayMorning, editAvailabilityMondayEvening,
                editAvailabilityTuesdayMorning, editAvailabilityTuesdayEvening,
                editAvailabilityWednesdayMorning, editAvailabilityWednesdayEvening,
                editAvailabilityThursdayMorning, editAvailabilityThursdayEvening,
                editAvailabilityFridayMorning, editAvailabilityFridayEvening,
                editAvailabilitySaturdayAll
        };

        //Replace Employee
        int rowNum = db.addOrReplaceEmployee(employee);
        //Availabilities
        employee.setAvailabilities(bools);
        int[] slotIDs = Employee.convertToAvailabilityArray(bools); //Convert to DB availabilities
        if (rowNum !=- 1) { // don't add availabilities if failed to replace employee
            db.replaceAvailability(slotIDs, employee.getId());
        }
        /*this is to update the list and employee info by finishing them and
         *recreating the pages that are connected first is the list than the
         *info so that the back button will revert to a updated list*/

        //Log.d("edit1", employee.getName());
        //Log.d("edit1 ID","ID: "+employee.getId());
        Intent intentinfoEmployee = new Intent(this,EmployeeInfo.class);
        intentinfoEmployee.putExtra("employee",employee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentinfoEmployee,b);

        //Availability
        //Storeasanarrayofbooleans

        this.finish();

    }

    @Override
    public void onBackPressed(){
        Intent intentinfoEmployee = new Intent(this,EmployeeInfo.class);
        intentinfoEmployee.putExtra("employee",employee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentinfoEmployee,b);
        this.finish();
    }

    /**
     * trdjyfjytfjy
     * @param phoneNumber a potential phone number
     * @return if it's valid
     */
    private boolean phoneNumberIsValid(String phoneNumber ){
        return (phoneNumber.matches("\\d+")
                &&
                phoneNumber.length() == 10);
    }

    private boolean emailIsValid(String email){
        return (email.matches("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)"));
    }

}