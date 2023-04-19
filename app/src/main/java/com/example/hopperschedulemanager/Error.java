package com.example.hopperschedulemanager;

public class Error {
    public String error;
    public int errorNum;
    public boolean inError;

    public Error(String error, int errorNum, boolean errorState) {
        this.error = error;
        this.errorNum = errorNum;
        this.inError = errorState;
    }

    @Override
    public String toString(){
        return error;
    }


}
