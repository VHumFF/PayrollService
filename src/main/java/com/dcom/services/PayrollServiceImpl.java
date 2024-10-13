package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.*;
import com.dcom.rmi.PayrollService;
import com.dcom.serviceLocator.ServiceLocator;
import com.dcom.utils.JWTUtil;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.LocalDate;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayrollServiceImpl extends UnicastRemoteObject implements PayrollService {

    public PayrollServiceImpl() throws RemoteException {
        super();
    }

    public List<Payroll> getPayrollList(String token){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return null;
        }
        if(!userSessionInfo.getUserType().equals("HR")){
            System.out.println("User do not have permission to perform this action");
            return null;
        }

        DataRetrievalInterface dbService = ServiceLocator.getDbService();
        List<Payroll> payrollList;
        try{
            payrollList = dbService.retrievePayrollList();
            return payrollList;
        }catch (Exception e){
            System.out.println("Error while retrieving payroll list:"+ e.getMessage());
            return null;
        }
    }

    public Map<Integer, String> getPayrollListForUser(String token){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return null;
        }

        DataRetrievalInterface dbService = ServiceLocator.getDbService();
        Map<Integer, String> payrollMap = new HashMap<>();
        try{
            List<Payroll> payrollList = dbService.retrievePayrollByUserId(userSessionInfo.getUserId());
            for(Payroll payroll: payrollList){
                LocalDate payMonth = payroll.getDate().toLocalDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
                String payMonthStr = dateFormat.format(payMonth);

                payrollMap.put(payroll.getPayrollId(), payMonthStr);
            }

            return payrollMap;
        }catch (Exception e){
            System.out.println("Error while retrieving payroll list:"+ e.getMessage());
            return null;
        }
    }

    public PayrollTemplate generatePayroll(String token, int payrollId) {
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return null;
        }

        DataRetrievalInterface dbService = ServiceLocator.getDbService();

        final Employee[] employeeHolder = new Employee[1]; // Array to hold employee result
        final Payroll[] payrollHolder = new Payroll[1]; // Array to hold payroll result
        Thread employeeThread = new Thread(() -> {
            try {
                employeeHolder[0] = retrieveEmployee(dbService, userSessionInfo.getUserId());
            } catch (RemoteException e) {
                System.out.println("Error retrieving employee: " + e.getMessage());
            }
        });
        Thread payrollThread = new Thread(() -> {
            try {
                payrollHolder[0] = retrievePayroll(dbService, payrollId, userSessionInfo.getUserId());
            } catch (RemoteException e) {
                System.out.println("Error retrieving payroll: " + e.getMessage());
            }
        });

        employeeThread.start();
        payrollThread.start();

        try {
            employeeThread.join();
            payrollThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
            return null;
        }

        Employee employee = employeeHolder[0];
        Payroll payroll = payrollHolder[0];

        if (employee == null || payroll == null) {
            return null;
        }

        return createPayrollTemplate(dbService, employee, payroll, userSessionInfo.getUserId());
    }

    private Employee retrieveEmployee(DataRetrievalInterface dbService, int userId) throws RemoteException {
        return dbService.retrieveEmployee(userId);
    }

    private Payroll retrievePayroll(DataRetrievalInterface dbService, int payrollId, int userId) throws RemoteException {
        Payroll payroll = dbService.retrievePayrollByPayRollId(payrollId);
        if (payroll == null || payroll.getUserId() != userId) {
            System.out.println("User does not have permission to view this payroll.");
            return null;
        }
        return payroll;
    }

    public boolean processPayroll(String token, int payrollId){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if (userSessionInfo == null) {
            System.out.println("Invalid or expired token");
            return false;
        }

        if(!userSessionInfo.getUserType().equals("HR")){
            System.out.println("User do not have permission to perform this action");
            return false;
        }

        DataRetrievalInterface dbService = ServiceLocator.getDbService();
        try{
            Payroll payroll = dbService.retrievePayrollByPayRollId(payrollId);
            if(payroll.isPaid()){
                System.out.println("This payroll has already been processed");
                return true;
            }else{
                payroll.setPaid(true);
                return dbService.updatePayroll(payroll);
            }
        }catch (Exception e){
            System.out.println("Error occurred while retrieving payroll:" + e.getMessage());
            return false;
        }
    }


    private PayrollTemplate createPayrollTemplate(DataRetrievalInterface dbService, Employee employee, Payroll payroll, int userId) {
        LocalDate localPayMonth = payroll.getDate().toLocalDate();
        YearMonth payrollMonth = YearMonth.of(localPayMonth.getYear(), localPayMonth.getMonthValue());
        Date startDate = Date.valueOf(payrollMonth.atDay(1));
        Date endDate = Date.valueOf(payrollMonth.atEndOfMonth());

        String startDateStr = formatDate(startDate);
        String endDateStr = formatDate(endDate);
        List<LeaveApplication> leaveApplicationList;
        try{
            leaveApplicationList = dbService.retrieveLeaveApplicationByUserIdAndStartDateAndEndDateAndLeaveType(
                    userId, startDate, endDate, LeaveApplication.unPaidLeave);
        }catch (Exception e){
            System.out.println("Error while retrieving leave application list: " + e.getMessage());
            return null;
        }


        PayrollTemplate payrollTemplate = new PayrollTemplate();
        payrollTemplate.setEmployeeName(employee.getName());
        payrollTemplate.setPayPeriod(startDateStr + " - " + endDateStr);
        payrollTemplate.setBaseSalary(employee.getSalary());
        payrollTemplate.setPaid(payroll.isPaid());

        // Calculate total earnings (update logic if bonus applies)
        payrollTemplate.setTotalEarnings(employee.getSalary());

        payrollTemplate.setTax(payroll.getTax());
        payrollTemplate.setEpf(payroll.getEpf());

        double dailyWage = employee.getDailyWage();
        double leaveDeduction = calculateLeaveDeduction(leaveApplicationList, dailyWage);
        payrollTemplate.setLeaveWithoutPayDeduction(leaveDeduction);

        payrollTemplate.setTotalDeductions(payrollTemplate.getTax() + payrollTemplate.getEpf() + payrollTemplate.getLeaveWithoutPayDeduction());
        payrollTemplate.setNetPay(payrollTemplate.getTotalEarnings() - payrollTemplate.getTotalDeductions());

        return payrollTemplate;
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    private double calculateLeaveDeduction(List<LeaveApplication> leaveApplicationList, double dailyWage) {
        return (leaveApplicationList != null && !leaveApplicationList.isEmpty())
                ? dailyWage * leaveApplicationList.size()
                : 0;
    }

}
































