package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.closeDrawer;

import static com.example.hopperschedulemanager.MainActivity.openDrawer;
import static com.example.hopperschedulemanager.MainActivity.redirectClick;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class EmployeeList extends AppCompatActivity {
    DrawerLayout drawerLayout;
    public ListView listView;
    private Switch employedSwitch;

    private ArrayList<Employee> useList = new ArrayList<>();
    public static Activity actemplist;

    Button buttonAddEmployee;

    public class CustomAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<Employee> employees;
        private TextView employeeName;
        private TextView opener, closer;


        public CustomAdapter(Context context, ArrayList<Employee> employees){
            this.employees = employees;
            this.context = context;
        }

        @Override
        public int getCount() {
            return employees.size();
        }

        @Override
        public Object getItem(int i) {
            return employees.get(i);
        }

        @Override
        public long getItemId(int i) {
            return employees.get(i).getId();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.employee_row, parent, false);
            }
            employeeName = convertView.findViewById(R.id.employeeName);
            if (!employees.get(i).isEmployed()){
                employeeName.setTextColor(Color.RED);
            } else {
                employeeName.setTextColor(Color.BLACK);
            }

            opener = convertView.findViewById(R.id.opener_imageView);
            closer = convertView.findViewById(R.id.closer_imageView);

            employeeName.setText(employees.get(i).getName());
            boolean open = employees.get(i).canOpen();
            if (open){
                opener.setVisibility(View.VISIBLE);
            } else {
                opener.setVisibility(View.GONE);
            }

            boolean close = employees.get(i).canClose();
            if (close){
                closer.setVisibility(View.VISIBLE);
            } else {
                closer.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
    private CustomAdapter employeeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        actemplist=this;
        setContentView(R.layout.activity_employee_list);
        drawerLayout = findViewById(R.id.drawer_employee);
        listView = findViewById(R.id.employeeList);

        DBHandler db = new DBHandler(EmployeeList.this);

        updateUseList(db.selectEmployeeList(true));
        buttonAddEmployee = (Button) findViewById(R.id.floatingAddButton);

        //create adapter to display employees from array
//        ArrayAdapter<Employee> employeeListAdapter =
//                new ArrayAdapter<Employee>(this, android.R.layout.simple_list_item_1, employeeList);  //change testlist
        employeeListAdapter = new CustomAdapter(this, useList);

        listView.setAdapter(employeeListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentToEmployeeInfo = new Intent(EmployeeList.this, EmployeeInfo.class);

                intentToEmployeeInfo.putExtra("employee", useList.get(i));
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(EmployeeList.this).toBundle();
                startActivity(intentToEmployeeInfo,b);
                EmployeeList.this.finish();

            }
        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent intent = new Intent(EmployeeList.this, DeleteEmployee.class);
                //intent.putExtra("employee", employeeList.get(i));
                //startActivity(intent);

                return false;
            }
        });

        employedSwitch = (Switch) findViewById(R.id.employedSwitch);
        employedSwitch.setChecked(false);
        employedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    updateUseList(db.selectEmployeeList(false));
                    employeeListAdapter.notifyDataSetChanged();
                } else {
                    updateUseList(db.selectEmployeeList(true));
                    employeeListAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void floatingAddButtonclicked(View view) {
        Intent i = new Intent(EmployeeList.this, AddEmployee.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(i,b);
        this.finish();
    }
    private void updateUseList(ArrayList<Employee> newList){
        useList.clear();
        useList.addAll(newList);
    }


    /*these functions are to allow the toolbar and navigation bar to work*/
    public void clickMenu(View view) {
        openDrawer(drawerLayout);

    }

    public void ClickLogo(View view) {
        closeDrawer(drawerLayout);

    }

    public void clickHome(View view) {
        redirectClick(this, MainActivity.class);
        this.finish();
    }

    public void clickEmployeeList(View view) {
        closeDrawer(drawerLayout);
    }

    public void addEmployee(View view) {
        redirectClick(this, AddEmployee.class);
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


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);

    }
}