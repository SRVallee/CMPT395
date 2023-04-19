package com.example.hopperschedulemanager;

import static com.example.hopperschedulemanager.MainActivity.redirectClick;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hirondelle.date4j.DateTime;

public class ExportMainPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DrawerLayout drawerLayout;
    private DBHandler db = new DBHandler(ExportMainPage.this);
    int p;
    private ArrayList<Shift> shiftsForExport;
    public String exportMonth, exportYear;
    public Date exportDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_main_page);
        drawerLayout = findViewById(R.id.drawer_export_main_page);

        DBHandler db = new DBHandler(ExportMainPage.this);

        //Set Month Spinner
        Spinner spinnerMonth = findViewById(R.id.spinnerExportMonth);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,R.array.months, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);
        //Set spinner to current month
        spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        spinnerMonth.setOnItemSelectedListener(this);

        //Set Year Spinner
        Spinner spinnerYear = findViewById(R.id.spinnerExportYear);
        //Make an array that goes 50 years back and forward from current year
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear-10; i <= thisYear+10; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        spinnerYear.setAdapter(adapterYear);
        //Set spinner to current year
        spinnerYear.setSelection(10);
        spinnerYear.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String selectedMonth;
        String selectedYear;
        int tempMonthInt;
        int tempYearInt;

        //DONT KNOW WHY BUT THIS CODE COMMENTED OUT BREAKS THE YEAR SPINNER BUT NOT THE MONTH SPINNER
        //Auto set Spinners and public vars
        //Month
        //tempMonthInt = Calendar.getInstance().get(Calendar.MONTH);
        //selectedMonth = Month.of(tempMonthInt).name();
        //exportMonth = selectedMonth;

        //Year
        //tempYearInt = Calendar.getInstance().get(Calendar.YEAR);
        //selectedYear = String.valueOf(tempYearInt);
        //exportYear = selectedYear;

        if (adapterView.getId() == R.id.spinnerExportMonth) {
            //If the user changes the month

            //Takes month from the spinner
            selectedMonth = adapterView.getItemAtPosition(i).toString();
            exportMonth = selectedMonth;

            //Takes the currently displayed year
            selectedYear = exportYear;

        } else if (adapterView.getId() == R.id.spinnerExportYear) {
            //If the user changes the year

            //Takes the year from the spinner
            selectedYear = adapterView.getItemAtPosition(i).toString();
            exportYear = selectedYear;

            //Takes the currently displayed month
            selectedMonth = exportMonth;
        } else {
            //This will run on the first time the calendar is made, will gen the current month+year

            //Takes the currently displayed month
            tempMonthInt = Calendar.getInstance().get(Calendar.MONTH);
            selectedMonth = Month.of(tempMonthInt).name();
            exportMonth = selectedMonth;

            //Takes the currently displayed year
            tempYearInt = Calendar.getInstance().get(Calendar.YEAR);
            selectedYear = Year.of(tempYearInt).toString();
            exportYear = selectedYear;
        }

        //Make the string for the first day of the month
        String string = selectedMonth + " 1, " + selectedYear;
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        try {
            exportDate = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void exportClick(View view) {
        try {
            createPDF(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPDF(View view) throws Exception {

        //Get shifts from db
        //Get tempDate
        DateTime tempDate = DateTime.forDateOnly(
                Integer.parseInt(exportYear.toString()),
                Month.valueOf(exportMonth.toUpperCase(Locale.ROOT)).getValue(),
                1);

        //For the first week
        p = tempDate.getWeekDay();

        //Make doc, values set to (3500, 1600) if changed, change the rest of the values of the pdf
        Rectangle size = new Rectangle(3500, 2100);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 36);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 32);
        Font font = new Font(Font.FontFamily.HELVETICA, 24);
        Document document = new Document(size,1,1,1,1);

        //Get file
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File root = new File(path, "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }
        File scheduleFile = new File(root, "schedule" + exportMonth + exportYear +".pdf");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(scheduleFile));

        //Open
        document.open();

        //Make columns
        float columnWidth[] = {500, 500, 500, 500, 500, 500, 500};

        //Make top row
        PdfPTable table = new PdfPTable(7);
        table.setTotalWidth(columnWidth);
        table.setLockedWidth(true);

        //Make title
        String title = new String();
        title = "Schedule for " + exportMonth + " " + exportYear;

        //Made background color for the pdf be cornflower, can change if want
        BaseColor titleColor = new BaseColor(100, 149, 237);

        //Put title row
        PdfPCell cell = new PdfPCell(new Phrase(title, titleFont));
        cell.setFixedHeight(60);
        cell.setColspan(7);
        cell.setBackgroundColor(titleColor);
        table.addCell(cell);

        //Put days of the week row
        String daysOfWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : daysOfWeek) {
            cell = new PdfPCell(new Phrase(day, subFont));
            cell.setFixedHeight(60);
            cell.setBackgroundColor(titleColor);
            table.addCell(cell);
        }
        document.add(table);
        table.flushContent();

        //get sunday
        //if (p == 7) {
        //    DateTime altDate = tempDate;
        //} else if (p != 7) {
        //    DateTime altDate = tempDate.minusDays(p);
        //}

        //Set counter values for making pdf
        int x; int h;
        if (p == 7) {
            x = 0; h = 0;
        } else {
            x = p - 1; h = p - 1;
        }
        int y = 1; int a = 1;

        //First week of shifts
        //7 = sunday, ext.
        for (String day : daysOfWeek) {
            if (x > 0) {
                //Put white space
                cell = new PdfPCell();
                cell.setFixedHeight(60);
                //cell.setBorder(Rectangle.NO_BORDER); //Left in just because it looks weird to not have first row, can add back if desired
                table.addCell(cell);
                x--;
            } else {
                //Insert date for the first week
                cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
                cell.setFixedHeight(60);
                cell.setBackgroundColor(titleColor);
                table.addCell(cell);
                y++;
            }
        }
        //Add and flush
        document.add(table);
        table.flushContent();

        //Insert first row of shifts

        //Use date to get last day of the month, prints the date of the shift to error check
        shiftsForExport = db.getShiftList(
                tempDate.getStartOfMonth().minusDays(1),  //First day of the month
                tempDate.getEndOfMonth());  //Last day of the month

        //Init values and set counter
        int emp1id, emp2id, emp3id;
        Employee emp1, emp2, emp3;
        String name;
        String[] num;
        a = 0;
        //DateTime currentDate = tempDate.getStartOfMonth();
        //int f = 0;

        int dayOfMonth = 1;
        for (String day : daysOfWeek) {
            if (h > 0) {
                //Put white space
                cell = new PdfPCell();
                cell.setFixedHeight(250);
                //cell.setBorder(Rectangle.NO_BORDER); //Same as title row
                table.addCell(cell);
                h--;
            } else {
                //Make res str
                String results = new String();
                //Weekends
                if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                    results += "Weekends: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    //Get and add employees
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + '(' + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                    //Weekend
                } else {
                    //Morning
                    results += "Mornings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    //Get employees
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    a++;

                    //Evening
                    results += "Evenings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    //Get employees
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                }
            }
        }
        document.add(table);
        table.flushContent();

        //Header for week two
        for (String day : daysOfWeek) {
            cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
            cell.setFixedHeight(60);
            cell.setBackgroundColor(titleColor);
            table.addCell(cell);
            y++;
        }
        document.add(table);
        table.flushContent();

        //Insert second row of shifts
        for (String day : daysOfWeek) {
            String results = new String();
                //Weekends
                if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                    results += "Weekends: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + '(' + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                    //Weekdays
                } else {
                    //Morning
                    results += "Mornings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    a++;
                    //Evening
                    results += "Evenings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                }
            }
        document.add(table);
        table.flushContent();

        //Header for week three
        for (String day : daysOfWeek) {
            cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
            cell.setFixedHeight(60);
            cell.setBackgroundColor(titleColor);
            table.addCell(cell);
            y++;
        }
        document.add(table);
        table.flushContent();

        //Insert third row of shifts
        for (String day : daysOfWeek) {
            String results = new String();
            //Weekends
            if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                results += "Weekends: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + '(' + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                cell = new PdfPCell(new Phrase(results, font));
                cell.setFixedHeight(250);
                table.addCell(cell);
                a++;
                dayOfMonth++;
                //Weekdays
            } else {
                //Morning
                results += "Mornings: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                a++;
                //Evening
                results += "Evenings: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                cell = new PdfPCell(new Phrase(results, font));
                cell.setFixedHeight(250);
                table.addCell(cell);
                a++;
                dayOfMonth++;
            }
        }
        document.add(table);
        table.flushContent();

        //Header for week four
        for (String day : daysOfWeek) {
            cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
            cell.setFixedHeight(60);
            cell.setBackgroundColor(titleColor);
            table.addCell(cell);
            y++;
        }
        document.add(table);
        table.flushContent();

        //Insert fourth row of shifts
        for (String day : daysOfWeek) {
            String results = new String();
            //Weekends
            if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                results += "Weekends: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + '(' + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                cell = new PdfPCell(new Phrase(results, font));
                cell.setFixedHeight(250);
                table.addCell(cell);
                a++;
                dayOfMonth++;
                //Weekdays
            } else {
                //Morning
                results += "Mornings: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                a++;
                //Evening
                results += "Evenings: (";
                results += shiftsForExport.get(a).getDate().toString();
                results += ")\n";
                emp1id = shiftsForExport.get(a).getEmployee1();
                if (emp1id != -1) {
                    emp1 = db.selectEmployee(emp1id);
                    name = emp1.getName();
                    num = emp1.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp2id = shiftsForExport.get(a).getEmployee2();
                if (emp2id != -1) {
                    emp2 = db.selectEmployee(emp2id);
                    name = emp2.getName();
                    num = emp2.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                emp3id = shiftsForExport.get(a).getEmployee3();
                if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                    emp3 = db.selectEmployee(emp3id);
                    name = emp3.getName();
                    num = emp3.getPhoneNumbers();
                    results += name + " (" + num[0] + ')' + '\n';
                }
                cell = new PdfPCell(new Phrase(results, font));
                cell.setFixedHeight(250);
                table.addCell(cell);
                a++;
                dayOfMonth++;
            }
        }
        document.add(table);
        table.flushContent();

        //Header for week five
        for (String day : daysOfWeek) {
            if (tempDate.getEndOfMonth().getDay() < y) {
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
            } else {
                cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
                cell.setBackgroundColor(titleColor);
            }
            cell.setFixedHeight(60);
            table.addCell(cell);
            y++;
        }
        document.add(table);
        table.flushContent();

        //Insert fifth row of shifts
        for (String day : daysOfWeek) {
            if (tempDate.getEndOfMonth().getDay() < dayOfMonth) {
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(250);
                table.addCell(cell);
            } else {
                String results = new String();
                //Weekends
                if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                    results += "Weekends: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + '(' + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                    //Weekdays
                } else {
                    //Morning
                    results += "Mornings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    a++;
                    //Evening
                    results += "Evenings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                }
            }
        }
        document.add(table);
        table.flushContent();

        //Header for week six
        for (String day : daysOfWeek) {
            if (tempDate.getEndOfMonth().getDay() < y) {
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
            } else {
                cell = new PdfPCell(new Phrase(exportMonth + " " + y, subFont));
                cell.setBackgroundColor(titleColor);
            }
            cell.setFixedHeight(60);
            table.addCell(cell);
            y++;
        }
        document.add(table);
        table.flushContent();

        //Insert sixth row of shifts
        for (String day : daysOfWeek) {
            if (tempDate.getEndOfMonth().getDay() < dayOfMonth) {
                cell = new PdfPCell();
                cell.setFixedHeight(250);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                a++;
                dayOfMonth++;
            } else {
                String results = new String();
                //Weekends
                if (day.equals(daysOfWeek[0]) || day.equals(daysOfWeek[6])) {
                    results += "Weekends: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + '(' + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                    //Weekdays
                } else {
                    //Morning
                    results += "Mornings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    a++;
                    //Evening
                    results += "Evenings: (";
                    results += shiftsForExport.get(a).getDate().toString();
                    results += ")\n";
                    emp1id = shiftsForExport.get(a).getEmployee1();
                    if (emp1id != -1) {
                        emp1 = db.selectEmployee(emp1id);
                        name = emp1.getName();
                        num = emp1.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp2id = shiftsForExport.get(a).getEmployee2();
                    if (emp2id != -1) {
                        emp2 = db.selectEmployee(emp2id);
                        name = emp2.getName();
                        num = emp2.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    emp3id = shiftsForExport.get(a).getEmployee3();
                    if (emp3id != -1 && shiftsForExport.get(a).isBusy()) {
                        emp3 = db.selectEmployee(emp3id);
                        name = emp3.getName();
                        num = emp3.getPhoneNumbers();
                        results += name + " (" + num[0] + ')' + '\n';
                    }
                    cell = new PdfPCell(new Phrase(results, font));
                    cell.setFixedHeight(250);
                    table.addCell(cell);
                    a++;
                    dayOfMonth++;
                }
            }
        }
        document.add(table);
        table.flushContent();

        //Close
        document.close();

        //Inform user
        Toast.makeText(this,"Pdf Created", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);

    }
}