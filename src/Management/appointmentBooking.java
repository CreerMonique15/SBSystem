
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class appointmentBooking {
    Scanner sc = new Scanner(System.in); 
    DBconnector dbc = new DBconnector();
    customerManagement cm = new customerManagement();
    serviceManagement sm = new serviceManagement();
    staffManagement stm = new staffManagement();
    
    public void appoinmentCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                             📅  APPOINTMENT BOOKING MENU                                                |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Book New Appointment                                                                               |\n" +
                                "|  [2] View All Appointments                                                                              |\n" +
                                "|  [3] Search Appointment                                                                                 |\n" +
                                "|  [4] Update Appointment                                                                                 |\n" +
                                "|  [5] Cancel Appointment                                                                                 |\n" +
                                "|  [6] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");


            action = utils.getValidatedChoice(1, 6);
            
            switch(action){
                case "1":
                    addAppointment();
                    break;
                
                case "2":
                    
                    break;
                    
                case "3":
                    
                    break;
                    
                case "4":
                    
                    break;
                case "5": 

                    break;
                    
                case "6": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("6"));
    }
    
    public void addAppointment() {
        cm.viewCustomers();

        // Step 1: Select Customer
        int cusId;
        while (true) {
            System.out.print("Select Customer ID     : ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                cusId = Integer.parseInt(input);
                String checkSQL = "SELECT c_id FROM customers_tbl WHERE c_id = ?";
                if (dbc.getSingleValue(checkSQL, cusId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Customer ID not found. Try again.");
                }
            } else {
                System.out.println("❌ Please enter a valid numeric ID.");
            }
        }

        System.out.println("\n🧾 SERVICE LIST: ");

        String sqlQuery = "SELECT s.service_id AS service_id, " +
                          "       s.service_name AS service_name, " +
                          "       s.description AS description, " +
                          "       c.name AS category, " +
                          "       s.price AS price " +
                          "FROM services_tbl s " +
                          "JOIN categories_tbl c ON s.category_id = c.category_id";


        String[] columnHeaders = {"ID", "Service Name", "Description", "Category", "Price"};
        String[] columnNames = {"service_id", "service_name", "description", "category", "price"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);

        // Step 2: Select Service
        int serId;
        while (true) {
            System.out.print("Select Service ID      : ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                serId = Integer.parseInt(input);
                String checkSQL = "SELECT service_id FROM services_tbl WHERE service_id = ?";
                if (dbc.getSingleValue(checkSQL, serId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Service ID not found. Try again.");
                }
            } else {
                System.out.println("❌ Please enter a valid numeric ID.");
            }
        }
        
        String staffSql = "SELECT st.s_id, " +
             "       st.s_fname, " +
             "       st.s_email, " +
             "       st.s_phone, " + 
             "       st.position, " + 
             "       st.specialization, " + 
             "       st.experience " +
             "FROM services_tbl s " +
             "JOIN staff_tbl st ON s.assigned_staff = st.s_id " +
             "WHERE s.service_id = ?";
        
        String[] staffColumnHeaders = {"ID", "Name", "Email", "Phone Number", "Position", "Specialization", "Experience"};
        String[] staffColumnNames = {"s_id", "s_fname", "s_email", "s_phone", "position", "specialization", "experience"};

        dbc.viewRecordsStaff(staffSql, staffColumnHeaders, staffColumnNames, serId);
        
        // Step 3: Select Staff
        int staffId;
        while (true) {
            System.out.print("Select Staff ID        : ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                staffId = Integer.parseInt(input);
                String checkSQL = "SELECT s_id FROM staff_tbl WHERE s_id = ?";
                if (dbc.getSingleValue(checkSQL, staffId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Staff ID not found. Try again.");
                }
            } else {
                System.out.println("❌ Please enter a valid numeric ID.");
            }
        }

        // Step 4: Enter Appointment Date and Time
        System.out.print("Enter Appointment Date (YYYY-MM-DD) : ");
        String date = sc.nextLine().trim();

        System.out.print("Enter Appointment Time (HH:MM)      : ");
        String time = sc.nextLine().trim();

        // Check if staff is already booked 
        String checkAvailability = "SELECT COUNT(*) FROM appointments_tbl WHERE staff_id = ? AND date = ? AND time = ?";
        int conflict = dbc.getTripleValue(checkAvailability, staffId, date, time);
        if (conflict > 0) {
            System.out.println("❌ Staff is already booked at that time. Try a different time.");
            return;
        }
        
        System.out.println("");

        // Step 5: Insert into Appointments Table
        String insertSQL = "INSERT INTO appointments_tbl (customer_id, service_id, staff_id, date, time, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, 'Pending',? ,?)";
        
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        boolean success = dbc.addRecord(insertSQL, cusId, serId, staffId, date, time, now, now);

        if (success) {
            System.out.println("\n✅ Appointment successfully booked!");
        } else {
            System.out.println("\n❌ Failed to book appointment. Please try again.");
        }
    }

}
