package com.dcom.dataModel;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Payroll implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private int payrollId;
    private double totalPaid;
    private boolean paid; // True if paid, false if not
    private double tax;
    private String salaryClass; // Example: "A", "B", etc.
    private double epf; // Changed from percentage to epf
    private Date date;

    // Constructor for creating a new payroll entry
    public Payroll(int userId, double totalPaid, boolean paid, double tax, String salaryClass, double epf, Date date) {
        this.userId = userId;
        this.totalPaid = totalPaid;
        this.paid = paid;
        this.tax = tax;
        this.salaryClass = salaryClass;
        this.epf = epf;
        this.date = date;
    }

    // Constructor for retrieving an existing payroll entry
    public Payroll(int userId, int payrollId, double totalPaid, boolean paid, double tax, String salaryClass, double epf, Date date) {
        this.userId = userId;
        this.payrollId = payrollId;
        this.totalPaid = totalPaid;
        this.paid = paid;
        this.tax = tax;
        this.salaryClass = salaryClass;
        this.epf = epf;
        this.date = date;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public int getPayrollId() {
        return payrollId;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getSalaryClass() {
        return salaryClass;
    }

    public void setSalaryClass(String salaryClass) {
        this.salaryClass = salaryClass;
    }

    public double getEpf() { // Renamed from getPercentage to getEpf
        return epf;
    }

    public void setEpf(double epf) { // Renamed from setPercentage to setEpf
        this.epf = epf;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
