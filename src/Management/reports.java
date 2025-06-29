package Management;

import config.DBconnector;
import config.utils;
import java.util.Scanner;

public class reports {
    Scanner sc = new Scanner(System.in);
    DBconnector dbc = new DBconnector();
    customerManagement cm = new customerManagement();
    
    public void report() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                     ===  REPORT HISTORY SECTION  ===                                   |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Generate Specific Customer History                                                                 |\n" +
                                "|  [2] Generate Specific Staff History                                                                    |\n" +
                                "|  [3] Generate All Appointment History                                                                     |\n" +
                                "|  [4] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");
            
            action = utils.getValidatedChoice(1, 4);
            
            switch(action){
                case "1":
                    generateCustomerHistory();
                    break;
                    
                case "2":
                    generateStaffHistory();
                    break;
                    
                case "3":
                    generateAllAppointmentHistory();
                    break;
                    
                case "4":
                    System.out.println("Returning to Main Menu...");
                    break;
                
            }
            
        }while(!action.equals("4"));
    }
     public void generateCustomerHistory() {
        System.out.println("\n🔍 GENERATE SPECIFIC CUSTOMER HISTORY");

        cm.viewCustomers();

        int customerId;
        while (true) {
            System.out.print("Enter Customer ID to view history: ");
            String input = utils.sc.nextLine().trim();
            if (input.matches("\\d+")) {
                customerId = Integer.parseInt(input);
                String checkSQL = "SELECT c_id FROM customers_tbl WHERE c_id = ?";
                if (dbc.getSingleValue(checkSQL, customerId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Customer ID not found, try again.");
                }
            } else {
                System.out.println("❌ Invalid numeric ID.");
            }
        }

        System.out.printf("\n📅 APPOINTMENTS FOR CUSTOMER ID %d:\n", customerId);

        String appSQL = 
            "SELECT a.a_id AS appointment_id, " +
            "       s.service_name AS service, " +
            "       st.s_fname AS staff, " +
            "       a.date AS date, " +
            "       a.time AS time, " +
            "       a.total AS total, " +
            "       a.status AS status " +
            "FROM appointments_tbl a " +
            "JOIN services_tbl s ON a.service_id = s.service_id " +
            "JOIN staff_tbl st ON a.staff_id = st.s_id " +
            "WHERE a.customer_id = ? " +
            "ORDER BY a.date ASC";

        String[] appHeaders = {"ID", "Service", "Staff", "Date", "Time", "Total", "Status"};
        String[] appFields  = {"appointment_id", "service", "staff", "date", "time", "total", "status"};

        dbc.viewRecordsStaff(appSQL, appHeaders, appFields, customerId);

        System.out.printf("\n💰 PAYMENTS FOR CUSTOMER ID %d:\n", customerId);

        String paySQL = 
            "SELECT p.payment_id AS payment_id, " +
            "       a.date AS appointment_date, " +
            "       p.amount AS amount, " +
            "       p.balance AS balance, " +
            "       p.payment_status AS status, " +
            "       p.payment_date AS payment_date " +
            "FROM payments_tbl p " +
            "JOIN appointments_tbl a ON p.appointment_id = a.a_id " +
            "WHERE a.customer_id = ? " +
            "ORDER BY p.payment_date DESC";

        String[] payHeaders = {"Payment ID", "Appointment Date", "Amount Paid", "Balance", "Status", "Payment Date"};
        String[] payFields  = {"payment_id", "appointment_date", "amount", "balance", "status", "payment_date"};

        dbc.viewRecordsStaff(paySQL, payHeaders, payFields, customerId);

        System.out.println("\n✅ Customer history generated successfully.");
    }
     
    public void generateStaffHistory() {
        System.out.println("\n🔍 GENERATE SPECIFIC STAFF HISTORY");

        String staffListSQL = 
            "SELECT s_id, s_fname, position, specialization FROM staff_tbl ORDER BY s_fname";
        String[] headers = {"ID", "Name", "Position", "Specialization"};
        String[] fields  = {"s_id", "s_fname", "position", "specialization"};

        dbc.viewRecords(staffListSQL, headers, fields);

        int staffId;
        while (true) {
            System.out.print("Enter Staff ID to view history: ");
            String input = utils.sc.nextLine().trim();
            if (input.matches("\\d+")) {
                staffId = Integer.parseInt(input);
                String checkSQL = "SELECT s_id FROM staff_tbl WHERE s_id = ?";
                if (dbc.getSingleValue(checkSQL, staffId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Staff ID not found, try again.");
                }
            } else {
                System.out.println("❌ Invalid numeric ID.");
            }
        }

        System.out.printf("\n📅 APPOINTMENTS HANDLED BY STAFF ID %d:\n", staffId);

        String appSQL = 
            "SELECT a.a_id AS appointment_id, " +
            "       c.c_fname AS customer, " +
            "       s.service_name AS service, " +
            "       a.date AS date, " +
            "       a.time AS time, " +
            "       a.total AS total, " +
            "       a.status AS status " +
            "FROM appointments_tbl a " +
            "JOIN customers_tbl c ON a.customer_id = c.c_id " +
            "JOIN services_tbl s ON a.service_id = s.service_id " +
            "WHERE a.staff_id = ? " +
            "ORDER BY a.date ASC";

        String[] appHeaders = {"ID", "Customer", "Service", "Date", "Time", "Total", "Status"};
        String[] appFields  = {"appointment_id", "customer", "service", "date", "time", "total", "status"};

        dbc.viewRecordsStaff(appSQL, appHeaders, appFields, staffId);

        String summarySQL = 
            "SELECT COUNT(*) AS total_appointments, SUM(total) AS total_revenue " +
            "FROM appointments_tbl " +
            "WHERE staff_id = ?";

        double totalRevenue = dbc.getSingleValue("SELECT SUM(total) FROM appointments_tbl WHERE staff_id = ?", staffId);
        int totalAppointments = (int) dbc.getSingleValue("SELECT COUNT(*) FROM appointments_tbl WHERE staff_id = ?", staffId);

        System.out.printf("\n✅ Staff ID %d has served %d appointments, generating a total revenue of ₱%.2f\n", staffId, totalAppointments, totalRevenue);
    }

    public void generateAllAppointmentHistory() {
        System.out.println("\n📅 ALL APPOINTMENT HISTORY WITH PAYMENT DETAILS:");

        String sqlQuery = 
            "SELECT a.a_id AS appointment_id, " +
            "       c.c_fname AS customer, " +
            "       s.service_name AS service, " +
            "       st.s_fname AS staff, " +
            "       a.date AS date, " +
            "       a.time AS time, " +
            "       a.total AS total, " +
            "       IFNULL(latest.amount_paid, 0) AS amount_paid, " +
            "       IFNULL(latest.balance, a.total) AS balance, " +
            "       a.status AS status " +
            "FROM appointments_tbl a " +
            "JOIN customers_tbl c ON a.customer_id = c.c_id " +
            "JOIN services_tbl s ON a.service_id = s.service_id " +
            "JOIN staff_tbl st ON a.staff_id = st.s_id " +
            "LEFT JOIN ( " +
            "   SELECT p1.appointment_id, " +
            "          SUM(p1.amount) AS amount_paid, " +
            "          MIN(p1.balance) AS balance " +
            "   FROM payments_tbl p1 " +
            "   GROUP BY p1.appointment_id " +
            ") latest ON a.a_id = latest.appointment_id " +
            "ORDER BY a.date ASC, a.time ASC";

        String[] headers = {"ID", "Customer", "Service", "Staff", "Date", "Time", "Total", "Amount Paid", "Balance", "Status"};
        String[] fields  = {"appointment_id", "customer", "service", "staff", "date", "time", "total", "amount_paid", "balance", "status"};

        dbc.viewRecords(sqlQuery, headers, fields);
    }


}
