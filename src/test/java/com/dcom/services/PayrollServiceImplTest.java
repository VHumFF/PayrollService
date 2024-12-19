package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.*;
import com.dcom.utils.JWTUtil;
import com.dcom.rmi.PayrollService;
import com.dcom.serviceLocator.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceImplTest {

    private PayrollService payrollService;

    @Mock
    private DataRetrievalInterface dbService;

    @Mock
    private UserSessionInfo userSessionInfo;

    @BeforeEach
    void setUp() throws RemoteException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize the payrollService instance
        payrollService = new PayrollServiceImpl();

        // Mock ServiceLocator to return the mocked dbService
        try (MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(dbService);
        }
    }

    @Test
    void testGetPayrollList_HR() throws RemoteException {
        String token = "valid-token";
        List<Payroll> payrollList = Arrays.asList(
                new Payroll(1, 1000.0, true, 100.0, "A", 200.0, Date.valueOf("2024-11-01"))
        );

        // Mocking JWT validation and user type
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mocking the static method for JWT validation
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);
            when(userSessionInfo.getUserType()).thenReturn("HR");

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            // Mock the DB service method
            when(mockDbService.retrievePayrollList()).thenReturn(payrollList);

            // Call the method under test
            List<Payroll> result = payrollService.getPayrollList(token);

            // Assert results
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1000.0, result.get(0).getTotalPaid());
            verify(mockDbService).retrievePayrollList();  // Verify DB call
        }
    }


    @Test
    void testGetPayrollList_UserWithoutPermission() throws RemoteException {
        String token = "valid-token";

        // Mock JWT validation and user type as 'Employee'
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class)) {
            // Mocking the static method
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);
            when(userSessionInfo.getUserType()).thenReturn("Employee");

            // Call the method under test
            List<Payroll> result = payrollService.getPayrollList(token);

            // Assert no result and verify DB service is not called
            assertNull(result);
            verify(dbService, never()).retrievePayrollList();
        }
    }

    @Test
    void testGetPayrollListForUser() throws RemoteException {
        String token = "valid-token";
        Map<Integer, String> expectedMap = new HashMap<>();
        expectedMap.put(1, "11-2024");

        // Mock JWT validation and user ID
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mocking the static method
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);
            when(userSessionInfo.getUserId()).thenReturn(1);

            // Mock DB service to return payroll for user
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);
            when(mockDbService.retrievePayrollByUserId(1)).thenReturn(Arrays.asList(
                    new Payroll(1, 1, 1000.0, true, 100.0, "A", 200.0, Date.valueOf("2024-11-01"))
            ));

            // Call the method under test
            Map<Integer, String> result = payrollService.getPayrollListForUser(token);

            // Assert results
            assertNotNull(result);
            assertEquals(expectedMap, result);
        }

    }

    @Test
    void testGeneratePayroll() throws RemoteException {
        String token = "valid-token";
        int payrollId = 1;
        int employeeUserId = 1;
        Employee employee = new Employee(1, "John", 1000.0, 20, 10);
        Payroll payroll = new Payroll(1, payrollId, 1000.0, false, 100.0, "A", 200.0, Date.valueOf("2024-11-01"));

        // Mock JWT validation and user ID
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {

            // Mocking the static method
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Ensure userSessionInfo mocks the necessary methods
            when(userSessionInfo.getUserId()).thenReturn(1);
            when(userSessionInfo.getUserType()).thenReturn("HR");

            // Mock DB service to return employee and payroll
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);
            when(mockDbService.retrieveEmployee(employeeUserId)).thenReturn(employee);
            when(mockDbService.retrievePayrollByPayRollId(payrollId)).thenReturn(payroll);

            // Call the method under test
            PayrollTemplate payrollTemplate = payrollService.generatePayroll(token, payrollId, employeeUserId);

            // Assert results
            assertNotNull(payrollTemplate);
            assertEquals(employee.getName(), payrollTemplate.getEmployeeName());
            assertEquals(1000.0, payrollTemplate.getBaseSalary());
            assertEquals(100.0, payrollTemplate.getTax());
        }


    }

    @Test
    void testProcessPayroll() throws RemoteException {
        String token = "valid-token";
        int payrollId = 1;
        Payroll payroll = new Payroll(1, payrollId, 1000.0, false, 100.0, "A", 200.0, Date.valueOf("2024-11-01"));

        // Mock JWT validation, user type and DB service
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            // Mocking the static method
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);
            when(userSessionInfo.getUserType()).thenReturn("HR");

            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);

            when(mockDbService.retrievePayrollByPayRollId(payrollId)).thenReturn(payroll);
            when(mockDbService.updatePayroll(payroll)).thenReturn(true);

            // Call the method under test
            boolean result = payrollService.processPayroll(token, payrollId);

            // Assert results
            assertTrue(result);
            assertTrue(payroll.isPaid());}

    }

    @Test
    void testProcessPayroll_AlreadyPaid() throws RemoteException {
        String token = "valid-token";
        int payrollId = 1;
        Payroll payroll = new Payroll(1, payrollId, 1000.0, true, 100.0, "A", 200.0, Date.valueOf("2024-11-01"));

        // Mock JWT validation, user type and DB service
        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            // Mocking the static method
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);
            when(userSessionInfo.getUserType()).thenReturn("HR");

            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(ServiceLocator::getDbService).thenReturn(mockDbService);
            when(mockDbService.retrievePayrollByPayRollId(payrollId)).thenReturn(payroll);

            // Call the method under test
            boolean result = payrollService.processPayroll(token, payrollId);

            // Assert results (already paid, should return true with no changes)
            assertTrue(result);
            assertTrue(payroll.isPaid());}

    }
}
