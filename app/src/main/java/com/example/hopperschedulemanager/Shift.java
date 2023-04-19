package com.example.hopperschedulemanager;

import androidx.annotation.NonNull;

import java.io.Serializable;

import hirondelle.date4j.DateTime;


/**Shift contains employees working and date of shift
 *
 */
public class Shift implements Serializable, Comparable {

    private int id;
    private int employeeID1 = -1, employeeID2 = -1, employeeID3 = -1;
    private Employee employeeObj1 = null;
    private Employee employeeObj2 = null;
    private Employee employeeObj3 = null;
    private int slot;
    private Slot slotObj = null;
    private boolean busy = false;
    private boolean ready = false;
    private DateTime date;


    /**
     * Create a Shift object without a specified ID
     */
    public Shift() {
        this(-1);
    }

    /**
     * Create Shift containing it's relevant.
     *
     * @param id An integer to identify shift.
     */
    public Shift(int id) {
        this.id = id;

    }

    /**
     * Returns the Id of the shift
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the Id of the employee in the first shift slot
     * @return
     */
    public int getEmployee1() {
        return employeeID1;
    }

    /**
     * Returns the Id of the employee in the second shift slot
     * @return
     */
    public int getEmployee2() { return employeeID2; }

    /**
     * Returns the Id of the employee in the busy shift slot
     * @return
     */
    public int getEmployee3() { return employeeID3; }

    /**
     * Returns the the employee in the first shift slot
     * @return
     */
    public Employee getEmployeeObj1() {
        return employeeObj1;
    }

    /**
     *Returns the the employee in the second shift slot
     * @return
     */
    public Employee getEmployeeObj2() {
        return employeeObj2;
    }

    /**
     * Returns the the employee in the busy shift slot
     * @return
     */
    public Employee getEmployeeObj3() {
        return employeeObj3;
    }

    /**
     * Gets the number of employees scheduled to the shift assuming they were added in order.
     * @return
     */
    public int getNumberOfEmployees(){
        if(employeeID1 == -1){
            return 0;
        }
        else if(employeeID2 == -1){
            return 1;
        }
        else if(employeeID3 == -1){
            return 2;
        }
        return 3;
    }

    /**
     * Returns employee Id in specified position in the shift:
     *      Ex. pos 2 is equivalent to getEmployee2()
     * @param pos
     * @return
     */
    public int getEmployeeIdByPosition(int pos){
        if (pos == 1){
            return employeeID1;
        }
        else if (pos == 2){
            return employeeID2;
        }
        else if (pos == 3){
            return employeeID3;
        }
        return -1;
    }

    /**
     * Returns the date the shift takes place at.
     * @return
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Returns Slot id the shift belongs to.
     *      Ex. 1 = Sunday, 2 = Monday morning, etc.
     * @return
     */
    public int getSlot() {
        return slot;
    }

    public Slot getSlotObj() {
        return slotObj;
    }

    /**
     * Returns true if Shift is busy
     * @return
     */
    public boolean isBusy(){
        return busy;
    }

    /**
     * Returns true if Shift has no missing requirements
     * @return
     */
    public boolean isReady(){
        return ready;
    }


    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmployee1(int employeeID1) {
        if(employeeID1 != this.employeeID2 && employeeID1 != this.employeeID3) {
            this.employeeID1 = employeeID1;
        }
        else if(employeeID1 == this.employeeID2){
            this.employeeID2 = this.employeeID1;
            this.employeeID1 = employeeID1;
        }
        else{
            this.employeeID3 = this.employeeID1;
            this.employeeID1 = employeeID1;
        }
    }

    public void setEmployee2(int employeeID2) {
        if(employeeID2 != this.employeeID3) {
            this.employeeID2 = employeeID2;
        }
        else{
            this.employeeID3 = this.employeeID2;
            this.employeeID2 = employeeID2;
        }
    }

    public void setEmployee3(int employeeID3) {
        this.employeeID3 = employeeID3;
    }


    /**
     * Add employee to shift
     * @param id int id of employee
     */
    public void addEmployee(int id){
        if(employeeID1 == -1){
            this.employeeID1 = id;
        }

        else if(employeeID2 == -1){
            this.employeeID2 = id;
        }

        else if(employeeID3 == -1 && this.busy){
            this.employeeID3 = id;
        }
    }

    public void setEmployeeObj1(Employee employeeObj1) {
        if (employeeObj1 != null) {
            this.employeeObj1 = employeeObj1;
            this.setEmployee1(employeeObj1.getId());
        } else {
            employeeID1 = -1;
        }
    }

    public void setEmployeeObj2(Employee employeeObj2) {
        if (employeeObj2 != null) {
            this.employeeObj2 = employeeObj2;
            this.setEmployee2(employeeObj2.getId());
        } else {
            employeeID2 = -1;
        }
    }

    public void setEmployeeObj3(Employee employeeObj3) {
        if (employeeObj3 != null) {
            this.employeeObj3 = employeeObj3;
            this.setEmployee3(employeeObj3.getId());
        }
        else {
            employeeID3 = -1;
        }
    }

    /**
     * Remove an employee from shift by providing its id
     * @param id
     * @return if an employee was removed or not
     */
    public boolean removeEmployee(int id){
        boolean changed = false;
        if(employeeID1 == id){
            this.employeeID1 = employeeID2;
            this.employeeID2 = employeeID3;
            this.employeeID3 = -1;
            changed = true;
        }

        else if(employeeID2 == id){
            this.employeeID2 = employeeID3;
            this.employeeID3 = -1;
            changed = true;
        }

        else if(employeeID3 == id){
            this.employeeID3 = -1;
            changed = true;
        }

        return changed;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setSlotObj(Slot slotObj) {
        if (slotObj != null) {
            this.slotObj = slotObj;
            slot = slotObj.getId();
        } else {
            slot = -1;
        }
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Checks if there is 2 employees in shift.
     * @return true if it does
     */
    public boolean isFull(){
        if((this.employeeID1!=-1)&&(this.employeeID2!=-1)&&(!busy)){
            return true;
        }
        else if((this.employeeID1!=-1)&&(this.employeeID2!=-1)&&(this.employeeID3!=-1)){
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        if (this.employeeID1 == -1) return true;
        return false;
    }

    /**
     * Check if a shift has an error.
     *
     * If check is a soft check, it will end on first error and error num will be 1, 0 if no error
     * If check is a hard check, it will check all and collect with a specific error number
     * Error number has format 1XXX
     *
     * X - 1 if shift is missing an employee for any reason, 0 if sufficient.
     * .
     * X - 1 if shift requires an opener but does not have one, 0 if satisfied.
     * .
     * X - 1 if shift requires a closer but does not have one, 0 if satisfied.
     *
     * @param db Current DBHandler being used.
     * @param hard if this is a hard check (True)
     * @return Error (errorNum = 0 if no error)
     */
    private Error errorChecker(DBHandler db, boolean hard){
        StringBuilder str = new StringBuilder();
        int cause = 1000;

        // Check if shift is missing an employee, trip error if true
        boolean error = !this.isFull();
        if (error) {
            ready = false;
            if (!hard){
                return new Error("Shift has error.", 1, true);
            }
            str.append("Shift does not have enough employees.\n");
            cause += 100;

        }

        Employee[] emps = {null, null, null};

        //Get employees from database
        if (employeeObj1 != null){
            emps[0] = employeeObj1;
        }
        if (employeeObj2 != null){
            emps[1] = employeeObj2;
        }
        if (employeeObj3 != null){
            emps[2] = employeeObj3;
        }
        // Get slot from database
        if (slotObj == null){
            return new Error("Shift must be full shift with employee and slot objects.", -1, true);
        }

        // Check if there is an opener and closer for the shift
        boolean hasOpener = false;
        boolean hasCloser = false;

        for (Employee e:emps) {
            if (e == null){
                continue;
            }
            if (!hasOpener) {
                hasOpener = e.canOpen();
            }
            if (!hasCloser){
                hasCloser = e.canClose();
            }
        }

        // If the shift requires an opener and doesn't have once, trip error
        if (!hasOpener && slotObj.isOpeningSlot()){
            ready = false;
            if (!hard){
                return new Error("Shift has error.", 1, true);
            }
            error = true;
            str.append("Shift is missing an Employee trained to open.\n");
            cause += 10;
        }

        // If the shift requires a closer and doesn't have once, trip error
        if (!hasCloser && slotObj.isClosingSlot()){
            ready = false;
            if (!hard){
                return new Error("Shift has error.", 1, true);
            }
            error = true;
            str.append("Shift is missing an Employee trained to close.\n");
            cause += 1;
        }

        if(!error){
            ready = true;
        }
        return new Error(str.toString(), cause, error);
    }

    /**
     * Hard Shift check
     *
     * Reports all errors.
     *
     * @param db
     * @return
     */
    public Error checkHard(DBHandler db){
        return errorChecker(db, true);
    }

    /**
     * Soft Shift Check
     *
     * Ends on first error encountered.
     *
     * @param db
     * @return
     */
    public Error checkSoft(DBHandler db){
        return errorChecker(db, false);
    }

    public void copyFrom(Shift origin){
        if(this.slot == origin.getSlot()) {
            this.setBusy(origin.isBusy());
            this.ready = origin.isReady();
            this.employeeID1 = origin.getEmployee1();
            this.employeeID2 = origin.getEmployee2();
            this.employeeID3 = origin.getEmployee3();
        }
    }

    @Override
    public int compareTo(Object compareShift) {
        int compareSlot = ((Shift) compareShift).getSlot();
        /* For Ascending order*/
        return this.slot - compareSlot;
    }

    @NonNull
    @Override
    protected Shift clone() {
        Shift clone = new Shift();
        clone.setId(this.id);
        clone.setEmployee1(this.employeeID1);
        clone.setEmployee2(this.employeeID2);
        clone.setEmployee3(this.employeeID3);
        clone.setSlot(this.slot);
        clone.setBusy(this.isBusy());
        clone.setReady(this.isReady());
        clone.setDate(this.date);

        return clone;
    }
}