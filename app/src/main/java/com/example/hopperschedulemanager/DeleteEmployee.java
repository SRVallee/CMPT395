package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.EmployeeList.actemplist;
import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DeleteEmployee extends AppCompatActivity {

    DrawerLayout drawerLayout;

    //Initialize
    //Employee info
    TextView name;
    TextView primaryPhone, secondaryPhone;
    TextView primaryMail, secondaryMail;
    TextView confirmMessage;
    EditText check;

    DBHandler db;
    Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHandler(DeleteEmployee.this);

        setContentView(R.layout.activity_delete_employee);
        drawerLayout = findViewById(R.id.drawer_delete_employee);

        //Set uo employee info
        name = findViewById(R.id.employeeNameTextView);
        primaryPhone = findViewById(R.id.employeePrimaryPhoneNumberTextView);
        secondaryPhone = findViewById(R.id.employeeSecondaryPhoneNumberTextView);
        primaryMail = findViewById(R.id.employeePrimaryEmailTextView);
        secondaryMail = findViewById(R.id.employeeSecondaryEmailTextView);
        confirmMessage = findViewById(R.id.confirmMessageTextView);

        check = findViewById(R.id.editTextConfirm);

        //Get employee info
        Intent intentFromEmployeeList = getIntent();
        employee = (Employee) intentFromEmployeeList.getSerializableExtra("employee");

        //Display user info
        name.setText(employee.getName());
        primaryPhone.setText(employee.getPhoneNumbers()[0]);
        secondaryPhone.setText(employee.getPhoneNumbers()[1]);
        primaryMail.setText(employee.getEmails()[0]);
        secondaryMail.setText(employee.getEmails()[1]);

        confirmMessage.setText(
                "Type \"confirm\" and then press the confirm deletion button to delete the Employee." +
                "\nWarning!" +
                "\nThis will remove them from all shifts today onward.");

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
    }

    public void clickSchedule(View view) {

        redirectClick(this, Schedule.class);
    }

    public void clickEmployeeList(View view) {

        redirectClick(this, EmployeeList.class);
    }

    public void cancelDeleteEmployee(View view){
        //If use getters and setters will need to then delete the object
        Intent intentinfoEmployee = new Intent(this,EmployeeInfo.class);
        intentinfoEmployee.putExtra("employee",employee);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intentinfoEmployee,b);
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

    public void confirmDeleteEmployee(View view){
        //Check if "confirm" is typed into the field
        if (check.getText().toString().equalsIgnoreCase("confirm")){
            //If Checked then delete
            db.deleteEmployee(employee);
            actemplist.finish();
            redirectClick(this,EmployeeList.class);
            this.finish();
        }
        //If use getters and setters will need to then delete the object
    }

}