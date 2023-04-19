package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.closeDrawer;
import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class Settings extends AppCompatActivity {
    /*these functions are to allow the toolbar and navigation bar to work*/
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        drawerLayout = findViewById(R.id.drawer_settings);

    }

    public void clickMenu(View view){
        MainActivity.openDrawer(drawerLayout);

    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);

    }

    public void clickHome(View view){
        redirectClick(this,MainActivity.class);
        this.finish();
    }

    public void clickSettings(View view){
        closeDrawer(drawerLayout);
    }

    public void clickDebug(View view){
        redirectClick(this,Debug.class);
        this.finish();
    }

    public void clickExport(View view){
        redirectClick(this,ExportMainPage.class);
        this.finish();
    }

    public void clickEmployeeList(View view){
        redirectClick(this,EmployeeList.class);
        this.finish();
    }

    public void clickSchedule(View view) {
        redirectClick(this, Schedule.class);
    }

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);

    }
}
