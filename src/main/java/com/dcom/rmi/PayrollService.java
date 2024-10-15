package com.dcom.rmi;

import com.dcom.dataModel.Payroll;
import com.dcom.dataModel.PayrollTemplate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface PayrollService extends Remote {
    Map<Integer, String> getPayrollListForUser(String token) throws RemoteException;
    PayrollTemplate generatePayroll(String token, int payrollId, int employeeUserId) throws RemoteException;
    boolean processPayroll(String token, int payrollId) throws RemoteException;
    List<Payroll> getPayrollList(String token) throws RemoteException;
}
