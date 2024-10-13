package com.dcom.dataModel;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Employee implements Serializable{
    private static final long serialVersionUID = 1L; // This helps during deserialization

    
    private int userId;
    private String name;
    private double salary;
    private int totalDaysOfWork;
    private int availablePaidLeave;

    // Constructor
    public Employee(int userId, String name, double salary, int totalDaysOfWork, int availablePaidLeave) {
        this.userId = userId;
        this.name = name;
        this.salary = salary;
        this.totalDaysOfWork = totalDaysOfWork;
        this.availablePaidLeave = availablePaidLeave;
    }

    public Employee(int userId) {
        this(userId, "unknown", 0, 20, 10); // Default values for name, salary, etc.
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public int getTotalDaysOfWork() {
        return totalDaysOfWork;
    }

    public int getAvailablePaidLeave() {
        return availablePaidLeave;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalDaysOfWork(int totalDaysOfWork) {
        this.totalDaysOfWork = totalDaysOfWork;
    }

    public void setAvailablePaidLeave(int availablePaidLeave) {
        this.availablePaidLeave = availablePaidLeave;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }


    public double getDailyWage() {
        if (totalDaysOfWork == 0) {
            return 0;
        }
        return salary / totalDaysOfWork;
    }
    
}
