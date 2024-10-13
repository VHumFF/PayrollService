package com.dcom.rmi;

import com.dcom.dataModel.PayrollTemplate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface PayrollService extends Remote {
    Map<Integer, String> getPayrollListForUser(String token) throws RemoteException;
    PayrollTemplate generatePayroll(String token, int payrollId) throws RemoteException;
    boolean processPayroll(String token, int payrollId) throws RemoteException;
}
