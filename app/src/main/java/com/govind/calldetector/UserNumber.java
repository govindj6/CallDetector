package com.govind.calldetector;

import java.io.Serializable;

public class UserNumber implements Serializable {

    private String number;
    private long registerTime;

    public UserNumber(String number) {
        this.number = number;
        this.registerTime = System.currentTimeMillis() + 30000;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > registerTime;
    }
}
