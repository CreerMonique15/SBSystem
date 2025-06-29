
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author mikel
 */
public class payment {
    Scanner sc = new Scanner(System.in);
    DBconnector dbc = new DBconnector();
    appointmentBooking ab = new appointmentBooking();
    
    public void paymentCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                     ===  PAYMENT MANAGEMENT MENU  ===                                  |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add Payment                                                                                        |\n" +
                                "|  [2] View All Payment History                                                                           |\n" +
                                "|  [3] Update Payment                                                                                     |\n" +
                                "|  [4] Delete Payment                                                                                     |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");
            
            action = utils.getValidatedChoice(1, 5);
            
            switch(action){
                case "1":
                    addPayment();
                    break;
                   
                case "2":
                    viewAllPayments();
                    break;
                    
                case "3":
                    break;
                    
                case "4":
                    break;
                    
                case "5": 
                    System.out.println("Returning to Main Menu...");
                    break;

            }
            
        }while(!action.equals("5"));
    }     
        
    public void addPayment() {

        System.out.println("\n📅 APPOINTMENT LIST:");

        String sqlQuery = 
            "SELECT a.a_id AS a_id, " +
            "       c.c_fname AS customer, " +
            "       s.service_name AS service, " +
            "       cat.name AS category, " +
            "       st.s_fname AS staff, " +
            "       a.date AS date, " +
            "       a.time AS time, " +
            "       a.total AS total, " +
            "       IFNULL(p.balance, a.total) AS balance, " +
            "       a.status AS status " +
            "FROM appointments_tbl a " +
            "JOIN customers_tbl c ON a.customer_id = c.c_id " +
            "JOIN services_tbl s ON a.service_id = s.service_id " +
            "JOIN categories_tbl cat ON s.category_id = cat.category_id " +
            "JOIN staff_tbl st ON a.staff_id = st.s_id " +
            "LEFT JOIN ( " +
            "   SELECT p1.appointment_id, p1.balance " +
            "   FROM payments_tbl p1 " +
            "   INNER JOIN ( " +
            "       SELECT appointment_id, MAX(payment_date) AS latest " +
            "       FROM payments_tbl GROUP BY appointment_id " +
            "   ) p2 ON p1.appointment_id = p2.appointment_id AND p1.payment_date = p2.latest " +
            ") p ON a.a_id = p.appointment_id " +
            "ORDER BY a.date ASC, a.time ASC";

        String[] headers = {"ID", "Customer", "Service", "Category", "Staff", "Date", "Time", "Total", "Balance", "Status"};
        String[] fields  = {"a_id", "customer", "service", "category", "staff", "date", "time", "total", "balance", "status"};

        dbc.viewRecords(sqlQuery, headers, fields);

        int appId;
        while (true) {
            System.out.print("Select Appointment ID     : ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                appId = Integer.parseInt(input);
                String checkSQL = "SELECT a_id FROM appointments_tbl WHERE a_id = ?";
                if (dbc.getSingleValue(checkSQL, appId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Appointment ID not found. Try again.");
                }
            } else {
                System.out.println("❌ Please enter a valid numeric ID.");
            }
        }

        String totalSQL = "SELECT total FROM appointments_tbl WHERE a_id = ?";
        double totalAmount = dbc.getSingleValue(totalSQL, appId);

        String paidSQL = "SELECT SUM(CAST(amount AS DOUBLE)) FROM payments_tbl WHERE appointment_id = ?";
        double totalPaid = dbc.getSingleValue(paidSQL, appId);

        double remaining = totalAmount - totalPaid;
        if (remaining <= 0) {
            System.out.println("✅ This appointment is already fully paid.");
            return;
        }

        System.out.printf("\n💵 Payment Method: Cash only\n");
        System.out.printf("Appointment total: %.2f\n", totalAmount);
        System.out.printf("Amount already paid: %.2f\n", totalPaid);
        System.out.printf("Remaining balance: %.2f\n", remaining);

        double cashPaid = 0;
        while (true) {
            System.out.print("Enter amount to pay now: ");
            String input = sc.nextLine().trim();
            try {
                cashPaid = Double.parseDouble(input);
                if (cashPaid <= 0) {
                    System.out.println("❌ Cannot be zero or negative.");
                } else if (cashPaid > remaining) {
                    System.out.println("❌ Cannot exceed the remaining balance.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }

        double newBalance = remaining - cashPaid;

        // status logic
        String payStatus = newBalance == 0 ? "Completed" : "Partial";

        System.out.printf("New balance will be: %.2f\n", newBalance);
        System.out.printf("Confirm payment of %.2f with remaining balance %.2f? (Y/N): ", cashPaid, newBalance);
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("❌ Payment cancelled.");
            return;
        }

        // Insert to payments_tbl
        String insertSQL = "INSERT INTO payments_tbl (appointment_id, amount, balance, payment_status, payment_date) VALUES (?, ?, ?, ?, ?)";
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        boolean success = dbc.addRecord(
            insertSQL,
            appId,
            String.valueOf(cashPaid),
            newBalance,
            payStatus,
            nowStr
        );

        if (success) {
            // If fully paid, update appointment
            if (newBalance == 0) {
                String updateSQL = "UPDATE appointments_tbl SET status = 'Paid' WHERE a_id = ?";
                dbc.updateRecord(updateSQL, appId);
            }
            System.out.println("✅ Payment recorded successfully!");
        } else {
            System.out.println("❌ Failed to record payment. Please try again.");
        }
    }
    
    public void viewAllPayments() {
        System.out.println("\n💵 PAYMENT HISTORY:");

        String sqlQuery = 
            "SELECT p.payment_id AS payment_id, " +
            "       c.c_fname AS customer, " +
            "       s.service_name AS service, " +
            "       a.date AS appointment_date, " +
            "       p.amount AS amount_paid, " +
            "       p.balance AS balance, " +
            "       p.payment_status AS status, " +
            "       p.payment_date AS payment_date " +
            "FROM payments_tbl p " +
            "JOIN appointments_tbl a ON p.appointment_id = a.a_id " +
            "JOIN customers_tbl c ON a.customer_id = c.c_id " +
            "JOIN services_tbl s ON a.service_id = s.service_id " +
            "ORDER BY p.payment_date DESC";

        String[] headers = {"ID", "Customer", "Service", "Appointment Date", "Amount Paid", "Balance", "Status", "Payment Date"};
        String[] fields  = {"payment_id", "customer", "service", "appointment_date", "amount_paid", "balance", "status", "payment_date"};

        dbc.viewRecords(sqlQuery, headers, fields);
    }


}
