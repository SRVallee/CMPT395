package com.example.hopperschedulemanager;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


/**Employee, contains it's own id, name, open/close capabilities and avaliability.
 * Contains the toolbar and navigation page
 */
public class Employee implements Serializable {

    private int id;
    private String name;
    private boolean open;
    private boolean close;
    private String[] emails = {null,null};
    private String[] phoneNumbers = {null,null};
    private boolean employed;
    private boolean[] availabilities = new boolean[12];

    /**
     * Create an Employee object without a specified ID
     *
     */
    public Employee(){
        this(-1);
    }

    /**
     * Create Employee containing it's info.
     * @param id An integer to identify individual employee.
     */
    public Employee(int id) {
        name = "Unnamed";
        this.id = id;
        open = false;
        close = false;
        employed = true;
        
    }

    /**
     * Convert boolean array to Slot ID array.
     *
     * @param boolArr array of booleans, length of 12
     * @return Slot ID array
     */
    static public int[] convertToAvailabilityArray(boolean[] boolArr){
        ArrayList<Integer> slotIDs = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (boolArr[i]) {
                slotIDs.add(i+1);
            }
        }

        return MainActivity.intList2Array(slotIDs);
    }

    /**
     * Convert Slot IDs into bool array
     *
     * @param intArr array of Slot IDs
     * @return boolean array
     */
    static public boolean[] convertToBooleanArray(int[] intArr){
        boolean[] boolArr = new boolean[12];
        for (int i = 0; i < intArr.length; i++) {
            boolArr[intArr[i]-1] = true;
        }
        return boolArr;
    }

    //getters
    /**
     * 
     * @return id integer
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @return Employee's name
     */
    public String getName() {
        Log.d("name: ", name);
        return name;
    }

    /**
     *
     * @return Array of employee emails
     */
    public String[] getEmails() { return emails; }

    /**
     *
     * @return Array of employee phone numbers
     */
    public String[] getPhoneNumbers() { return phoneNumbers; }


    /**
     *
     * @return boolean of if employee can close
     */
    public boolean canClose() {
        return close;
    }

    /**
     *
     * @return boolean of if employee can open
     */
    public boolean canOpen() {
        return open;
    }

    /**
     *
     * @return boolean on whether employee is currently employed
     */
    public boolean isEmployed() {
        return employed;
    }

    /**
     *
     * @return Array of availabilities
     */
    public boolean[] getBoolAvailabilities() {
        return availabilities;
    }

    public int[] getSlotAvailabilities(){
        return convertToAvailabilityArray(availabilities);
    }

    //setters

    /**
     * Set new Employee id
     * @param id id integer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Change name of employee
     * @param name String containing name of employee
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set whether the employee can open
     * @param open True if the employee is able to open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Set whether the employee can close
     * @param close True if the employee is able to close
     */
    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * Rehire or lay off an employee
     * @param employed is the contact being employed?
     */
    public void setEmployment(boolean employed) {
        this.employed = employed;
    }

    /**
     * Set email addresses.
     *
     * Must have 1 email. If there is no second email address, set param email2 should be null.
     * @param email Primary email address
     * @param email2 Secondary email address (optional = null)
     */
    public void setEmails(String email, String email2){
        this.emails[0] = email;
        this.emails[1] = email2;
        for (int i = 0; i < 2; i++) {
            if (emails[i] == ""){
                emails[i] = null;
            }
        }
    }

    /**
     * Set Phone numbers.
     *
     * Must have 1 phone number. If there is no second phone number, set param phone2 should be
     * null.
     * @param phone Primary phone number
     * @param phone2 Secondary Phone number (optional = null)
     */
    public void setPhoneNumbers(String phone, String phone2){
        this.phoneNumbers[0] = phone;
        this.phoneNumbers[1] = phone2;
        for (int i = 0; i < 2; i++) {
            if (phoneNumbers[i] == ""){
                phoneNumbers[i] = null;
            }
        }
    }

    /**
     * Set employed status
     *
     * @param employed if employee is employed
     */
    public void setEmployed(boolean employed) { this.employed = employed; }

    /**
     * Set employee availabilities
     *
     * @param availabilities boolean array of availabilities
     */
    public void setAvailabilities(boolean[] availabilities) {
        this.availabilities = availabilities;
    }

    /**
     * Override toString to show the name of employee when printing
     * @return string representation of object
     */
    @Override
    public String toString() {
        if(this.open && close){
            return this.name + "  (o,c)";
        }
        else if(this.open){
            return this.name + "  (o)";
        }
        else if(this.close){
            return this.name + "  (c)";
        }
        return this.name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}