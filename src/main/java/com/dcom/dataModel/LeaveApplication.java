package com.dcom.dataModel;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class LeaveApplication implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int leaveApplicationId;
    private int userId;
    private Date date;
    private int numberOfDays;
    private String type;
    private String status;

    public static final String paidLeave = "Paid";
    public static final String unPaidLeave = "Unpaid";

    public LeaveApplication() {
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }
    public int getLeaveApplicationId(){return leaveApplicationId;}

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
