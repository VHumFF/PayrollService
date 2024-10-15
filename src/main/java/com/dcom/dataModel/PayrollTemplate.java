package com.dcom.dataModel;

import java.io.Serializable;

public class PayrollTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeName;
    private String payPeriod;
    private double baseSalary;
    private double totalEarnings;
    private double tax;
    private double epf;
    private double leaveWithoutPayDeduction;
    private double totalDeductions;
    private double netPay;
    private Boolean paid;


    public PayrollTemplate(){

    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setPaid(Boolean paid){
        this.paid = paid;
    }

    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setEpf(double epf) {
        this.epf = epf;
    }

    public void setTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public void setLeaveWithoutPayDeduction(double leaveWithoutPayDeduction) {
        this.leaveWithoutPayDeduction = leaveWithoutPayDeduction;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public double getTax() {
        return tax;
    }

    public double getEpf() {
        return epf;
    }

    public double getLeaveWithoutPayDeduction() {
        return leaveWithoutPayDeduction;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }
    public Boolean isPaid(){
        return paid;
    }

}
