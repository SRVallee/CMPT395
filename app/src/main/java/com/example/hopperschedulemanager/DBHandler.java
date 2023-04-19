package com.example.hopperschedulemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DBHandler extends SQLiteOpenHelper{

    //Database name
    private static final String DB_NAME = "HSB-db.sqlite";
    //Database Version
    private static final int DB_Version = 15;
    //Foreign Key Enforcement flag
    private static final String FOREIGN_KEY_ENFORCE = "TRUE";
    //Tables:
    private static final String EMP_TAB = "employees";              //Table Employee
    private static final String EMP_TAB_ID = "employee_id";
    private static final String EMP_TAB_NAME = "name";
    private static final String EMP_TAB_CLOSE = "close";
    private static final String EMP_TAB_OPEN = "open";
    private static final String EMP_TAB_EMAIL1 = "email_addr1";
    private static final String EMP_TAB_EMAIL2 = "email_addr2";
    private static final String EMP_TAB_PHONE1 = "phone_num1";
    private static final String EMP_TAB_PHONE2 = "phone_num2";
    private static final String EMP_TAB_EMPLOYED = "employed";
    private static final String[] EMP_COLMUNS = {EMP_TAB_ID, EMP_TAB_NAME, EMP_TAB_OPEN,
            EMP_TAB_CLOSE, EMP_TAB_EMAIL1, EMP_TAB_EMAIL2, EMP_TAB_PHONE1, EMP_TAB_PHONE2,
            EMP_TAB_EMPLOYED};

    private static final String SLT_TAB = "slots";                  //Table Slots
    private static final String SLT_TAB_ID = "slot_id";
    private static final String SLT_TAB_DOW = "day_of_week";
    private static final String SLT_TAB_OPEN = "open";
    private static final String SLT_TAB_CLOSE = "close";
    private static final String SLT_TAB_START_TIME = "start_time";
    private static final String SLT_TAB_END_TIME = "end_time";
    private static final String[] SLT_COLUMNS = {SLT_TAB_ID, SLT_TAB_DOW, SLT_TAB_OPEN,
            SLT_TAB_CLOSE, SLT_TAB_START_TIME, SLT_TAB_END_TIME};

    private static final String SHF_TAB = "shifts";                 //Table Shifts
    private static final String SHF_TAB_ID = "shift_id";
    private static final String SHF_TAB_EMP_ID1 = "fk_employee_id1";
    private static final String SHF_TAB_EMP_ID2 = "fk_employee_id2";
    private static final String SHF_TAB_EMP_ID3 = "fk_employee_id3";
    private static final String SHF_TAB_SLT_ID = "fk_slot_id";
    private static final String SHF_TAB_BUSY = "is_busy";
    private static final String SHF_TAB_READY = "is_ready";
    private static final String SHF_TAB_DATE = "date";
    private static final String[] SHF_COLUMNS = {SHF_TAB_ID, SHF_TAB_BUSY, SHF_TAB_READY,
            SHF_TAB_DATE, SHF_TAB_EMP_ID1, SHF_TAB_EMP_ID2, SHF_TAB_EMP_ID3, SHF_TAB_SLT_ID};

    private static final String AVB_TAB = "availability";           //Table Slots
    private static final String AVB_TAB_EMP_ID = "fk_employee_id";
    private static final String AVB_TAB_SLT_ID = "fk_slot_id";

    private static final String[] TABLES = {EMP_TAB,SLT_TAB,SHF_TAB,AVB_TAB};

    private static final int CURRENT_YEAR = LocalDateTime.now().getYear();


    public DBHandler(Context context){
        super(context, DB_NAME, null, DB_Version);
    }

    //Create a Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Query to create employees table
        String emp_create = "CREATE TABLE " + EMP_TAB + " (" +
                EMP_TAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EMP_TAB_NAME + " TEXT NOT NULL," +
                EMP_TAB_CLOSE + " INTEGER NOT NULL," +
                EMP_TAB_OPEN + " INTEGER NOT NULL," +
                EMP_TAB_EMAIL1 + " TEXT," +
                EMP_TAB_EMAIL2 + " TEXT," +
                EMP_TAB_PHONE1 + " TEXT," +
                EMP_TAB_PHONE2 + " TEXT," +
                EMP_TAB_EMPLOYED + " INTEGER NOT NULL)";

        db.execSQL(emp_create);

        //Query to create slots table
        String slt_create = "CREATE TABLE " + SLT_TAB + " (" +
                SLT_TAB_ID + " INTEGER PRIMARY KEY," +
                SLT_TAB_DOW + " TEXT NOT NULL," +
                SLT_TAB_CLOSE + " INTEGER NOT NULL," +
                SLT_TAB_OPEN + " INTEGER NOT NULL," +
                SLT_TAB_START_TIME + " TEXT NOT NULL," +
                SLT_TAB_END_TIME + " TEXT NOT NULL)";

        db.execSQL(slt_create);

        //Query to create shifts table
        String shf_create = "CREATE TABLE " + SHF_TAB + " (" +
                SHF_TAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SHF_TAB_EMP_ID1 + " INTEGER REFERENCES employees(employee_id), " +
                SHF_TAB_EMP_ID2 + " INTEGER REFERENCES employees(employee_id), " +
                SHF_TAB_EMP_ID3 + " INTEGER REFERENCES employees(employee_id), " +
                SHF_TAB_SLT_ID + " INTEGER NOT NULL REFERENCES slots(slot_id)," +
                SHF_TAB_BUSY + " INTEGER," +
                SHF_TAB_READY + " INTEGER," +
                SHF_TAB_DATE + " NUMERIC NOT NULL)";

        db.execSQL(shf_create);

        //Query to create availability table
        String avb_create = "CREATE TABLE " + AVB_TAB + " (" +
                AVB_TAB_EMP_ID + " INTEGER NOT NULL REFERENCES employees(employee_id)," +
                AVB_TAB_SLT_ID + " INTEGER NOT NULL REFERENCES slots(slot_id))";

        db.execSQL(avb_create);

        //FOREIGN KEY ENFORCEMENT
        db.execSQL("PRAGMA foreign_keys = " + FOREIGN_KEY_ENFORCE);

    }

    //Update tables in Database !!!DELETES ALL THE TABLES!!!
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tab : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + tab);
        }
        onCreate(db);
        generateSlots();
    }

    //#########################################
    //######## \/ EMPLOYEE HANDLERS \/ ########                                           //Employee
    //#########################################
    /**
     * Insert a new Employee record or update an existing one.
     *
     * @param e Employee to add or update
     */
    public int addOrReplaceEmployee(Employee e){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowNum;
        if (e.getId() == -1){
            rowNum = addEmployeeRecord(db, e);
        } else {
            rowNum = replaceEmployeeRecord(db, e);
        }
        return rowNum;
    }


    /**
     * Optimizer method to add employee to DB
     *
     * @param db SQLite Database - Has to be writable
     * @param e Employee to add
     * @return Row Number from Database - should be same as ID, but may not be if DB is sorted
     */
    private int addEmployeeRecord(SQLiteDatabase db, Employee e) {
        ContentValues cv = employeeContentBuilder(e);
        if (cv == null){
            return -1;
        }
        int rowsAffected = (int) db.insert(EMP_TAB, null, cv);

        // Insert and get row number from DB
        // !<ROW NUM MAY NOT BE SAME AS ID>! although it should be
        return rowsAffected;
    }

    /**
     * Optimizer method to replace employee to DB
     *
     * @param db SQLite Database - Has to be writable
     * @param e Employee to add
     * @return Row Number from Database - should be same as ID, but may not be if DB is sorted
     */
    private int replaceEmployeeRecord(SQLiteDatabase db, Employee e) {
        ContentValues cv = employeeContentBuilder(e);
        if (cv == null){
            return -1;
        }
        cv.put(EMP_TAB_ID, e.getId());
        // Insert and get row number from DB
        int rowsNumber = (int) db.replace(EMP_TAB, null, cv);

        // Get today and grab 1 year of shifts ahead of today
        DateTime today = DateTime.today(TimeZone.getDefault());
        // may be necessary to add an employee id to this to improve performance
        ArrayList<Shift> shifts = getFullShiftList(today, today.plusDays(365));

        // Check shifts to make sure that any future shifts have their status changed if relevant
        for (Shift shift:shifts) {
            boolean shiftReady = false;
            Error error = new Error("", 0, false);

            // if they exist in the shift, get their current state and check
            if (e.getId() == shift.getEmployee1() || e.getId() == shift.getEmployee2() || e.getId() == shift.getEmployee3()) {
                shiftReady = shift.isReady();
                error = shift.checkSoft(this);
            }

            // if the state of error has changed, replace shift in DB
            if (error.inError ^ !shiftReady) {
                replaceShiftRecord(db, shift);
            }
        }

        // !<ROW NUM MAY NOT BE SAME AS ID>! although it should be
        return rowsNumber;
    }

    /**
     * Builds ContentValue Object with Employee info for SQL statements
     *
     * @param e Employee to serialize
     * @return ContentValues with Employee information
     */
    private ContentValues employeeContentBuilder(Employee e){
        ContentValues cv = new ContentValues();
        // Check that employee has a name before adding to DB
        if (e.getName().equals("Unnamed") | e.getName() == null) {
            return null;
        } else {
            cv.put(EMP_TAB_NAME, e.getName());
        }

        cv.put(EMP_TAB_CLOSE, e.canClose());
        cv.put(EMP_TAB_OPEN, e.canOpen());

        //Check that employee has at least one email before adding to db
        String[] emails = e.getEmails();
        if (emails[0] == null || emails[0].equals("")){
            return null;
        } else {
            cv.put(EMP_TAB_EMAIL1, emails[0]);
        }
        if (emails[1] != null && !emails[1].equals("")) {
            cv.put(EMP_TAB_EMAIL2, emails[1]);
        }

        //Check that employee has at least one email before adding to db
        String[] phones = e.getPhoneNumbers();
        if (phones[0] == null || phones[0].equals("")){
            return null;
        } else {
            cv.put(EMP_TAB_PHONE1, phones[0]);
        }
        if (phones[1] != null && !phones[1].equals("")) {
            cv.put(EMP_TAB_PHONE2, phones[1]);

        }
        cv.put(EMP_TAB_EMPLOYED, true);

        return cv;
    }

    /**
     * Delete Employee
     *
     * @param emp Employee object
     * @return number of rows affected
     */
    public int deleteEmployee(Employee emp){
        return deleteEmployee(emp.getId());
    }

    /**
     * Delete an employee
     *
     * @param id of Employee
     * @return how many rows affected
     */
    public int deleteEmployee(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //construct SQL
        cv.put(EMP_TAB_EMPLOYED, 0);
        String whereClause = EMP_TAB_ID + "=?";
        String[] whereArg = {String.valueOf(id)};

        // Delete employee availabilities so they don't show when scheduling shift
        deleteEmpAvabRecord(db, id);

        // Get today and grab 1 year of shifts ahead of today
        DateTime today = DateTime.today(TimeZone.getDefault());
        ArrayList<Shift> shifts = getShiftList(today, today.plusDays(365));

        // Remove employee fom all shifts from today until a year in the future
        for (Shift shift:shifts){
            // if they exist in the shift, remove them
            boolean removed = shift.removeEmployee(id);
            if (removed) {
                // if they were in the shift, replace the record
                shift.setReady(false);
                replaceShiftRecord(db, shift);
            }
        }

        //Perform delete
        return db.update(EMP_TAB, cv,whereClause, whereArg);

    }

    /**
     * Cascade delete all Employees
     *
     * Deletes all records associated, meaning foreign keys
     *
     * @return Number of rows deleted
     */
    protected int forceDeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AVB_TAB,null, null);
        db.delete(SHF_TAB, null, null);
        return db.delete(EMP_TAB, "1", null);
    }

    /**
     * Select list of all employed Employees
     *
     * @param employed Choose if showing employed
     * @return List of Employee objects
     */
    public ArrayList<Employee> selectEmployeeList(boolean employed){
        ArrayList<Employee> emps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EMP_TAB;
        if (employed) {
            query += " WHERE employed=" + true;
        }
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()){  //Go to first, if possible
            do {
                emps.add(employeeDeserializer(c));
            } while(c.moveToNext());
        }

        return emps;
    }

    /**
     * Select single Employee from database.
     *
     * @param e Employee object to get
     * @return Employee object from DB
     */
    public Employee selectEmployee(Employee e){ return selectEmployee(e.getId()); }

    /**
     * Select single Employee from database by ID.
     *
     * @param id of Employee to get
     * @return Employee object from DB
     */
    public Employee selectEmployee(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EMP_TAB +
                " WHERE " + EMP_TAB_ID + "=" + id + " LIMIT 1", null);
        c.moveToFirst();
        return employeeDeserializer(c);
    }


    /**
     * Create Employee object from database return
     *
     * @param c Cursor from db
     * @return Employee object
     */
    private Employee employeeDeserializer(Cursor c){
        return employeeDeserializer(c, 0, false);
    }

    /**
     * Create Employee object from database return
     *
     * @param c Cursor from db
     * @return Employee object
     */
    private Employee employeeDeserializer(Cursor c, int indexStart, boolean shortForm){
        // Get values at cursor
        Employee e = null;
        int index = indexStart;
        if (!c.isNull(index)) {
            int id = c.getInt(index); //index = 0 + indexStart
            e = new Employee(id);
            e.setName(c.getString(++index)); //index = 1 + indexStart
            e.setClose(c.getInt(++index) > 0); //index = 2 + indexStart
            e.setOpen(c.getInt(++index) > 0); //index = 3 + indexStart
            if (!shortForm) {
                String email1 = c.getString(++index); //index = 4 + indexStart
                String email2 = c.getString(++index); //index = 5 + indexStart
                e.setEmails(email1, email2);
                String phone1 = c.getString(++index); //index = 6 + indexStart
                String phone2 = c.getString(++index); //index = 7 + indexStart
                e.setPhoneNumbers(phone1, phone2);

            }
            e.setEmployed((c.getInt(++index) > 0)); //index = 8(4) + indexStart

            boolean[] avabs = Employee.convertToBooleanArray(getEmployeeAvailabilities(id));
            e.setAvailabilities(avabs);

        }

        return e;
    }

    //######################################
    //######## \/ SLOTS HANDLERS \/ ########                                                 //Slots
    //######################################

    /**
     * Add a slot given params and db connection
     *
     * @param db SQLiteDatabase object
     * @param day String representation for day
     * @param opening slot is an opening shift
     * @param closing slot is a closing shift
     * @param start Slot start time
     * @param end Slot end time
     */
    private void addSlot(SQLiteDatabase db, int id, String day, boolean opening, boolean closing, LocalTime start, LocalTime end){
        ContentValues val = new ContentValues();
        val.put(SLT_TAB_ID, id);
        val.put(SLT_TAB_DOW, day);
        val.put(SLT_TAB_CLOSE, closing);
        val.put(SLT_TAB_OPEN, opening);
        val.put(SLT_TAB_START_TIME, start.toString());
        val.put(SLT_TAB_END_TIME, end.toString());
        db.insert(SLT_TAB, null, val);
    }

    /**
     * Recompute DB Slots from scratch.
     *
     * Only run on DB creation
     */
    protected void generateSlots(){
        SQLiteDatabase db = this.getWritableDatabase();
//        deleteAllSlots(); // Delete all slots
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        String[] weekend = {"Saturday", "Sunday"};
        LocalTime start1 = LocalTime.of(9,0);
        LocalTime end1   = LocalTime.of(15,0);
        //LocalTime start2 = LocalTime.of(15,0);
        LocalTime end2   = LocalTime.of(21,0);

        int j = 0;
        int k = 0;
        int z = 0;
        for (int i = 1; i < 8; i++) {
            if (i == 1 || i == 7) { // Create weekends
                addSlot(db, ++z,weekend[k], true, true, start1, end2);
                k++;
            } else { // Create Weekdays
                addSlot(db, ++z, weekdays[j], true, false, start1, end1);
                addSlot(db, ++z, weekdays[j], false, true, end1, end2);
                j++;
            }
        }

    }

    /**
     * Delete all slots.
     *
     * Not used anymore, but here in case needed
     */
    protected void deleteAllSlots(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SLT_TAB, null);

        if (c.moveToFirst()) {  //Go to first if possible
            do {
                int id = c.getInt(0); //get slot ID
                String whereClause = SLT_TAB_ID + "=?";
                String[] whereArg = {String.valueOf(id)};
                db.delete(SLT_TAB, whereClause, whereArg);

            } while (c.moveToNext()); // loop while cursor not at end of list
            c.close();
        }
    }

    /**
     * Get a slot from String code
     *
     * @param code string code for slot
     * @return Slot
     */
    public Slot getSlot(String code){
        code = code.toUpperCase();
        int slotID = -1;
        switch (code) {
            case "SUA":
                slotID = 1;
                break;
            case "MM":
                slotID = 2;
                break;
            case "ME":
                slotID = 3;
                break;
            case "TUM":
                slotID = 4;
                break;
            case "TUE":
                slotID = 5;
                break;
            case "WM":
                slotID = 6;
                break;
            case "WE":
                slotID = 7;
                break;
            case "THM":
                slotID = 8;
                break;
            case "THE":
                slotID = 9;
                break;
            case "FM":
                slotID = 10;
                break;
            case "FE":
                slotID = 11;
                break;
            case "SAA":
                slotID = 12;
                break;
        }
        return getSlot(slotID);
    }

    /**
     * Get Slot information by id
     *
     * @param id of Slot
     * @return Slot
     */
    public Slot getSlot(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SLT_TAB +
                " WHERE "+ SLT_TAB_ID + "=" + id + " LIMIT 1", null);
        Slot slot = new Slot();
        if (c.moveToFirst()) {  //Go to first if possible
            slot = slotDeserializer(c);

        }
        return slot;
    }

    /**
     * Get a list of all slots
     *
     * @return List of all slots
     */
    public ArrayList<Slot> getSlotList(){
        SQLiteDatabase db = this.getReadableDatabase();
        //Get from database
        Cursor c = db.rawQuery("SELECT * FROM " + SLT_TAB, null);
        ArrayList<Slot> slots = new ArrayList<>();

        if (c.moveToFirst()) {  //Go to first if possible
            do {
                Slot s = slotDeserializer(c);

                slots.add(s);

            } while (c.moveToNext()); // loop while cursor not at end of list
        }

        return slots;
    }

    /**
     * Deserialize DB return string into Slot record
     *
     * @param c DB query cursor at location
     * @return Slot
     */
    private Slot slotDeserializer(Cursor c){
        return slotDeserializer(c, 0);
    }

    /**
     * Deserialize DB return string into Slot record
     *
     * @param c DB query cursor at location
     * @return Slot
     */
    private Slot slotDeserializer(Cursor c, int indexStart){
        Slot s = null;
        if (!c.isNull(indexStart)) {
            s = new Slot();
            s.setId(c.getInt(indexStart)); //get slot ID
            s.setDayOfWeek(c.getString(1 + indexStart));
            s.setClosingSlot(c.getInt(2 + indexStart) > 0);
            s.setOpeningSlot(c.getInt(3 + indexStart) > 0);
            LocalTime st = LocalTime.parse(c.getString(4 + indexStart));
            LocalTime et = LocalTime.parse(c.getString(5 + indexStart));
            s.setStartTime(st);
            s.setEndTime(et);
        }

        return s;
    }

    //#############################################
    //######## \/ AVAILABILITY HANDLERS \/ ########                                   //Availability
    //#############################################

    /**
     * Add Employee availabilities.
     *
     * @param slotID Array of all Slots that Employee is available
     * @param employeeID Employee id to add availabilities for
     * @return if added
     */
    public boolean addAvailRecords(int[] slotID, int employeeID){
        SQLiteDatabase db = this.getWritableDatabase();
        return addEmpAvabs(db, slotID, employeeID);
    }

    /**
     * Optimizer method to add employee availabilities.
     *
     * @param db SQLite DB object
     * @param slotID Array of all Slots that Employee is available
     * @param employeeID Employee id to add availabilities for
     * @return if added
     */
    private boolean addEmpAvabs(SQLiteDatabase db, int[] slotID, int employeeID){
        boolean added = true;
        // Insert records, assumes 12 slots
        for (int i = 0; i < slotID.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(AVB_TAB_EMP_ID, employeeID);
            cv.put(AVB_TAB_SLT_ID, slotID[i]);
            long rowInserted = db.insert(AVB_TAB, null, cv);
            if (rowInserted < 0){
                added = false;
            }
        }

        return added;
    }

    /**
     * Replace Employee availability.
     *
     * @param slotID Array of all Slots that Employee is available
     * @param employeeID Employee id to replace availabilities for
     * @return if replaced
     */
    public boolean replaceAvailability(int[] slotID, int employeeID){
        SQLiteDatabase db = this.getWritableDatabase();
        deleteEmpAvabRecord(db, employeeID);
        return addEmpAvabs(db, slotID, employeeID);
    }

    /**
     * Delete employee availabilities.
     *
     * @param empID Employee ID to delete availabilities
     * @return if deleted
     */
    public boolean deleteAvailability(int empID){
        SQLiteDatabase db = this.getWritableDatabase();
        return deleteEmpAvabRecord(db, empID);
    }

    /**
     * Optimizer method to delete employee availabilities
     *
     * @param db SQLite DB object
     * @param empID Employee ID to delete
     * @return if rows were deleted
     */
    private boolean deleteEmpAvabRecord(SQLiteDatabase db, int empID){
        boolean avbDeleted = false;

        int linesAffected = db.delete(AVB_TAB,
                AVB_TAB_EMP_ID + '=' + empID, null );
        if (linesAffected > 0){
            avbDeleted = true;
        }
        return avbDeleted;
    }

    /**
     * Get Slots available for an Employee ID
     *
     * @param empID Employee ID to get from availabilities table
     * @return List of Slot IDs
     */
    public int[] getEmployeeAvailabilities(int empID){
        return getAvailabilityIDs(empID, true);
    }

    /**
     * Get Employees available for a Slot ID
     *
     * @param slotID Slot ID to get from availabilities table
     * @return List of Employee IDs
     */
    public int[] getSlotAvailability(int slotID) {
        return getAvailabilityIDs(slotID, false);
    }

    /**
     * Reuse method to get availability ids.
     *
     * @param id ID to query
     * @param isEmployee if ID an employee ID
     * @return List of IDs of specified type
     */
    private int[] getAvailabilityIDs(int id, boolean isEmployee){
        SQLiteDatabase db = this.getReadableDatabase();

        String column;
        int colNum;

        //Check if we're getting employee or slot id
        if (isEmployee){
            column = AVB_TAB_EMP_ID;
            colNum = 1;
        } else {
            column = AVB_TAB_SLT_ID;
            colNum = 0;
        }

        // Get records from database
        Cursor c = db.rawQuery("SELECT * FROM " + AVB_TAB +
                " WHERE " + column + "=" + id, null);

        ArrayList<Integer> ids = new ArrayList<>();

        // add ids to arraylist
        if (c.moveToFirst()) {  //Go to first if possible
            do {
                ids.add(c.getInt(colNum)); //get slot ID

            } while (c.moveToNext()); // loop while cursor not at end of list
            c.close();
        }

        return MainActivity.intList2Array(ids);
    }

    //#############################################
    //######## \/ SHIFT HANDLERS \/ ###############                                         //Shifts
    //#############################################

    /**
     * Add or replace an employee based on if there the shift ID is -1 or not
     *
     * @param shift Shift to add/replace
     * @return Row number of added/replaced - Should be the same as ID
     */
    public long addOrReplaceShift(Shift shift){
        SQLiteDatabase db = this.getWritableDatabase();
        long rowNum;
        if (shift.getId() == -1){
            rowNum = addShiftRecord(db, shift);
        } else {
            rowNum = replaceShiftRecord(db, shift);
        }

        db.close();
        return rowNum;
    }

    /**
     * Add a shift to DB
     *
     * @param shift Shift to add
     * @return Row number add - Should be the same as ID
     */
    public long addShift(Shift shift){
        SQLiteDatabase db = this.getWritableDatabase();
        long rowNum = addShiftRecord(db, shift);
        db.close();
        return rowNum;
    }

    /**
     * Bulk add shifts to DB
     *
     * @param shifts list to add to DB
     * @return Number of rows added
     */
    public int addShifts(ArrayList<Shift> shifts){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAdded = 0;
        for (Shift shift:shifts) {
            long rowNum = addShiftRecord(db, shift);
            if (rowNum > 0){
                rowsAdded++;
            }
        }
        db.close();
        return rowsAdded;
    }

    /**
     * Optimizer method to add Shift to DB
     *
     * @param db SQLDatabase object (Writable)
     * @param shift Shift to add
     * @return Row number added - Should be the same as ID
     */
    private long addShiftRecord(SQLiteDatabase db, Shift shift){
        ContentValues cv = shiftContentBuilder(shift);
        Log.i("INFO", "Added shift: " + cv);
        //Log.i("INFO", "Added shift: " + cv.toString(), new Exception());
        return db.insert(SHF_TAB, null, cv);
    }

    /**
     * Replace a shift in DB.
     *
     * @param shift Shift to replace
     * @return Row number replaced - Should be the same as ID
     */
    public long replaceShift(Shift shift){
        SQLiteDatabase db = this.getWritableDatabase();
        long rowNum = replaceShiftRecord(db, shift);
        db.close();
        return rowNum;
    }

    /**
     * Bulk replace shifts
     *
     * @param shifts list
     * @return number of rows affected
     */
    public int replaceShifts(ArrayList<Shift> shifts){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAdded = 0;
        for (Shift shift:shifts) {
            long rowNum = replaceShiftRecord(db, shift);
            if (rowNum > 0){
                rowsAdded++;
            }
        }
        db.close();

        return rowsAdded;
    }

    /**
     * Optimizer method to replace a Shift record
     *
     * @param db SQLDatabase object (Writable)
     * @param shift Shift to replace
     * @return Row number replaced - Should be the same as ID
     */
    private long replaceShiftRecord(SQLiteDatabase db, Shift shift){
        ContentValues cv = shiftContentBuilder(shift);
        cv.put(SHF_TAB_ID, shift.getId());
        Log.i("INFO", "Replaced shift: " + cv);
        //Log.i("INFO", "Replaced shift: " + cv.toString(), new Exception());
        return db.replace(SHF_TAB, null, cv);
    }

    /**
     * Builds ContentValues based on Shift
     * Does not include ID. used o pass to DB methods
     *
     * @param shift Shift to convert
     * @return ContentValues object with column names and row data
     */
    private ContentValues shiftContentBuilder(Shift shift){
        ContentValues cv = new ContentValues();
        if (shift.getEmployee1() != -1) {
            cv.put(SHF_TAB_EMP_ID1, shift.getEmployee1());
        }
        if (shift.getEmployee2() != -1) {
            cv.put(SHF_TAB_EMP_ID2, shift.getEmployee2());
        }
        if (shift.getEmployee3() != -1) {
            cv.put(SHF_TAB_EMP_ID3, shift.getEmployee3());
        }
        cv.put(SHF_TAB_SLT_ID, shift.getSlot());
        cv.put(SHF_TAB_BUSY, shift.isBusy());
        cv.put(SHF_TAB_READY, shift.isReady());
        cv.put(SHF_TAB_DATE, shift.getDate().toString());
        return cv;
    }

    /**
     * Delete a Shift
     *
     * @param shift Shift to delete
     * @return if Shift deleted
     */
    public boolean deleteShift(Shift shift){
        return deleteShift(shift.getId());
    }

    /**
     * Deletes a Shift by ID
     *
     * @param id Shift ID to delete
     * @return if record deleted
     */
    public boolean deleteShift(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete shifts
        boolean shiftDeleted = false;
        int linesAffected = db.delete(SHF_TAB,
                SHF_TAB_ID + '=' + id, null );
        if (linesAffected > 0){
            shiftDeleted = true;
        }
        return shiftDeleted;
    }

    /**
     * Build string for getting a whole shift including sot and employee objects
     *
     * @return SQL to join slot and employee tables
     */
    private String fullShiftQuery(){
        String cols =
                SHF_TAB+"."+SHF_TAB_ID + ", " +              //SHF 0
                        SHF_TAB+"."+SHF_TAB_BUSY + ", " +
                        SHF_TAB+"."+SHF_TAB_READY + ", " +
                        SHF_TAB+"."+SHF_TAB_DATE + ", " +
                        "e1." + EMP_TAB_ID + ", " +                  //EMP1 4
                        "e1." + EMP_TAB_NAME + ", " +
                        "e1." + EMP_TAB_CLOSE + ", " +
                        "e1." + EMP_TAB_OPEN + ", " +
                        "e1." + EMP_TAB_EMPLOYED + ", " +
                        "e2." + EMP_TAB_ID + ", " +                  //EMP2 9
                        "e2." + EMP_TAB_NAME + ", " +
                        "e2." + EMP_TAB_CLOSE + ", " +
                        "e2." + EMP_TAB_OPEN + ", " +
                        "e2." + EMP_TAB_EMPLOYED + ", " +
                        "e3." + EMP_TAB_ID + ", " +                  //EMP3 14
                        "e3." + EMP_TAB_NAME + ", " +
                        "e3." + EMP_TAB_CLOSE + ", " +
                        "e3." + EMP_TAB_OPEN + ", " +
                        "e3." + EMP_TAB_EMPLOYED + ", " +
                        SLT_TAB+"."+SLT_TAB_ID + ", " +              //SLT 19
                        SLT_TAB+"."+SLT_TAB_DOW + ", " +
                        SLT_TAB+"."+SLT_TAB_CLOSE + ", " +
                        SLT_TAB+"."+SLT_TAB_OPEN + ", " +
                        SLT_TAB+"."+SLT_TAB_START_TIME + ", " +
                        SLT_TAB+"."+SLT_TAB_END_TIME;               //End 24

        String query =  "SELECT " + cols + " FROM " + SHF_TAB +
                "\nLEFT JOIN " + EMP_TAB + " AS e1 ON " + SHF_TAB_EMP_ID1+"=e1."+EMP_TAB_ID +
                "\nLEFT JOIN " + EMP_TAB + " AS e2 ON " + SHF_TAB_EMP_ID2+"=e2."+EMP_TAB_ID +
                "\nLEFT JOIN " + EMP_TAB + " AS e3 ON " + SHF_TAB_EMP_ID3+"=e3."+EMP_TAB_ID +
                "\nLEFT JOIN " + SLT_TAB + " ON " + SHF_TAB_SLT_ID + "=" + SLT_TAB+"."+SLT_TAB_ID;

        return query;
    }

    /**
     * Get a full shift with slot and employee objects
     *
     * @param id Employee id to retrieve
     * @return full shift
     */
    public Shift getFullShift(int id){
        // SQL query
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery( fullShiftQuery() +
                " WHERE " + SHF_TAB_ID + '=' + id + " LIMIT 1", null);
        c.moveToFirst();
        return fullShiftDeserializer(c);
    }

    /**
     * Get all shifts between two dates including the relevant employee and slot objects
     *
     * @param from Date to start
     * @param to Date to finish
     * @return list of shifts with slot and emplooyee objects
     */
    public ArrayList<Shift> getFullShiftList(DateTime from, DateTime to){
        ArrayList<Shift> shifts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery( fullShiftQuery() +
        "\nWHERE " + SHF_TAB + "." + SHF_TAB_DATE + " BETWEEN \"" + from + "\" AND \"" + to + '\"' +
        "\nORDER BY " + SHF_TAB + "." +  SHF_TAB_DATE + " ASC" + ';', null);
        if (c.moveToFirst()) {  //Go to first if possible
            do {
                shifts.add(fullShiftDeserializer(c));
            } while (c.moveToNext()); // loop while cursor not at end of list
        }
        c.close();
        return shifts;
    }

    /**
     * Get shift information from shift
     *
     * @param shift Shift to get DB information from
     * @return Updated Shift from DB
     */
    public Shift getShift(Shift shift){
        return getShift(shift.getId());
    }

    /**
     * Get Shift information by ID
     *
     * @param id of Shift to get from DB
     * @return Shift
     */
    public Shift getShift(int id){
        // SQL query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SHF_TAB +
                " WHERE " + SHF_TAB_ID + '=' + id + " LIMIT 1", null);
        // Result into shift
        c.moveToFirst();
        Shift shift = shiftDeserializer(c);
        c.close();
        return shift;
    }

    /**
     * Gets list of Ghifts for the current year
     *
     * @return list of Shifts
     */
    public ArrayList<Shift> getShiftList(){
        // Jan. 1 this year
        DateTime start = new DateTime(CURRENT_YEAR, 1,1,0,0,0,0);
        // Dec. 31 this year
        DateTime end = new DateTime(CURRENT_YEAR, 12,31,23,59,59,0);
        return getShiftList(start, end);
    }

    /**
     * Get a list of Shifts for this year
     *
     * @param from Date range start
     * @param to Date range end
     * @return List of Shifts
     */
    public ArrayList<Shift> getShiftList(DateTime from, DateTime to){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Shift> shifts = new ArrayList<>();

        //SQL query
        Cursor c = db.rawQuery("SELECT * FROM " + SHF_TAB +
                " WHERE "+ SHF_TAB_DATE + " BETWEEN \"" + from + "\" AND \"" + to + '\"' +
                " ORDER BY " + SHF_TAB_DATE + " ASC", null);

        if (c.moveToFirst()) {  //Go to first if possible
            do {
                shifts.add(shiftDeserializer(c));
            } while (c.moveToNext()); // loop while cursor not at end of list
        }
        c.close();
        return shifts;
    }

    /**
     * Turn DB record back into shift object
     *
     * @param c Cursor from DB query
     * @return Shift from cursor
     */
    private Shift shiftDeserializer(Cursor c){
        Shift s = new Shift();
        s.setId(c.getInt(0));
        // Check that Employee 1 is not null and add, otherwise set to -1
        if ( c.getString(1) != null) {
            s.setEmployee1(c.getInt(1));
        } else {
            s.setEmployee1(-1);
        }
        //Employee 2
        if ( c.getString(2) != null) {
            s.setEmployee2(c.getInt(2));
        } else {
            s.setEmployee2(-1);
        }
        //Employee 3
        if ( c.getString(3) != null) {
            s.setEmployee3(c.getInt(3));
        } else {
            s.setEmployee3(-1);
        }

        s.setSlot(c.getInt(4));

        s.setBusy(c.getInt(5) > 0);
        s.setReady(c.getInt(6) > 0);

        DateTime shiftDate = new DateTime(c.getString(7));
        s.setDate(shiftDate);

        return s;
    }

    /**
     * Deserialize a full shift
     *
     * @param c DB Cursor containing relevant data
     * @return Shift object from information at cursor
     */
    private Shift fullShiftDeserializer(Cursor c){
        //System.out.println(DatabaseUtils.dumpCurrentRowToString(c));
        Shift s = new Shift();
        s.setId(c.getInt(0));
        s.setBusy(c.getInt(1) > 0);
        s.setReady(c.getInt(2) > 0);

        DateTime shiftDate = new DateTime(c.getString(3));
        s.setDate(shiftDate);

        Employee emp1 = employeeDeserializer(c, 4, true);
        Employee emp2 = employeeDeserializer(c, 9, true);
        Employee emp3 = employeeDeserializer(c, 14, true);
        Slot slot = slotDeserializer(c, 19);

        s.setEmployeeObj1(emp1);
        s.setEmployeeObj2(emp2);
        s.setEmployeeObj3(emp3);
        s.setSlotObj(slot);

        return s;
    }

}