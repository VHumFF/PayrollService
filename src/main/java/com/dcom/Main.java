package com.dcom;

import com.dcom.rmi.PayrollService;
import com.dcom.services.PayrollServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            PayrollService payrollService = new PayrollServiceImpl();
            Registry registry = LocateRegistry.createRegistry(8082);
            registry.rebind("payrollService", payrollService);
            System.out.println("Payroll Service is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}