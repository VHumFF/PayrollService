/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.dcom;

import com.dcom.dataModel.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

public interface DataRetrievalInterface extends Remote {
    Employee retrieveEmployee(int employeeId) throws RemoteException;

    Payroll retrievePayrollByPayRollId(int payrollId) throws RemoteException;
    List<Payroll> retrievePayrollByUserId(int userId) throws RemoteException;

    List<LeaveApplication> retrieveLeaveApplicationByUserIdAndStartDateAndEndDateAndLeaveType(int userId, Date startDate, Date endDate, String type) throws RemoteException;
    boolean updatePayroll(Payroll payroll) throws RemoteException;
    List<Payroll> retrievePayrollList() throws RemoteException;
}

