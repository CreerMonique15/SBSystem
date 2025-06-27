
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;


public class appointmentBooking {
    Scanner sc = new Scanner(System.in); 
    DBconnector dbc = new DBconnector();
    customerManagement cm = new customerManagement();
    serviceManagement sm = new serviceManagement();
    staffManagement stm = new staffManagement();
    
    public void appointmentCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                    ===  APPOINTMENT BOOKING MENU ===                                    |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Book New Appointment                                                                               |\n" +
                                "|  [2] View All Appointments                                                                              |\n" +
                                "|  [3] Update Appointment                                                                                 |\n" +
                                "|  [4] Cancel Appointment                                                                                 |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");


            action = utils.getValidatedChoice(1, 5);
            
            switch(action){
                case "1":
                    addAppointment();
                    break;
                
                case "2":
                    viewAppointments();
                    break;
                    
                case "3":
                    viewAppointments();
                    updateAppointment();
                    break;
                case "4": 
                    viewAppointments();
                    cancelAppointment();
                    break;
                    
                case "5": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("5"));
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

       DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
       DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

       LocalDateTime now = LocalDateTime.now();
       LocalDateTime appointmentDateTime = null;
       String dateInput = "";
       String timeInput = "";
       LocalDate date = null;
       LocalTime time = null;

       while (true) {
           try {
               // Step 4: Enter Appointment Date and Time
               System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
               dateInput = sc.nextLine().trim();

               System.out.print("Enter Appointment Time (HH:MM): ");
               timeInput = sc.nextLine().trim();

               // Parse date and time
               date = LocalDate.parse(dateInput, dateFormatter);
               time = LocalTime.parse(timeInput, timeFormatter);
               appointmentDateTime = LocalDateTime.of(date, time);

               // ✅ Validate that appointment is not in the past
               if (appointmentDateTime.isBefore(now)) {
                   System.out.println("❌ Appointment must be scheduled for the present or future only.\n");
                   continue;
               }

               // ✅ Check if staff is already booked
               String checkAvailability = "SELECT COUNT(*) FROM appointments_tbl WHERE staff_id = ? AND date = ? AND time = ?";
               int conflict = dbc.getTripleValue(checkAvailability, staffId, dateInput, timeInput);

               if (conflict > 0) {
                   System.out.println("❌ Staff is already booked at that time. Try a different time.\n");
                   continue;
               }

               break;

           } catch (DateTimeParseException e) {
               System.out.println("❌ Invalid format. Please enter date as YYYY-MM-DD and time as HH:MM.\n");
           }
       }
       
       // Get the price for the selected service
        String priceSql = "SELECT price FROM services_tbl WHERE service_id = ?";
        double amount = dbc.getSingleValue(priceSql, serId);


       System.out.println("✅ Appointment slot is valid and available.");

       String insertSQL = "INSERT INTO appointments_tbl (customer_id, service_id, staff_id, date, time, total, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?, ?)";

       String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

       boolean success = dbc.addRecord(insertSQL, cusId, serId, staffId, dateInput, timeInput, amount, nowStr, nowStr);

       if (success) {
           System.out.println("\n✅ Appointment successfully booked!");
       } else {
           System.out.println("\n❌ Failed to book appointment. Please try again.");
       }
    }
    
    public void viewAppointments() {
        System.out.println("\n📅 APPOINTMENT LIST:");

        String sqlQuery = "SELECT a.a_id AS a_id, " +
                          "       c.c_fname AS customer, " +
                          "       s.service_name AS service, " +
                          "       cat.name AS category, " +
                          "       st.s_fname AS staff, " +
                          "       a.date AS date, " +
                          "       a.time AS time, " +
                          "       a.total AS total, " +
                          "       a.status AS status " +
                          "FROM appointments_tbl a " +
                          "JOIN customers_tbl c ON a.customer_id = c.c_id " +
                          "JOIN services_tbl s ON a.service_id = s.service_id " +
                          "JOIN categories_tbl cat ON s.category_id = cat.category_id " +
                          "JOIN staff_tbl st ON a.staff_id = st.s_id " +
                          "ORDER BY a.date ASC, a.time ASC";

        String[] headers = {"ID", "Customer", "Service", "Category", "Staff", "Date", "Time", "Total", "Status"};
        String[] fields  = {"a_id", "customer", "service", "category", "staff", "date", "time", "total", "status"};

        dbc.viewRecords(sqlQuery, headers, fields);
    }
    
    public void updateAppointment() {
        System.out.println("\n===============================");
        System.out.println("        UPDATE APPOINTMENT");
        System.out.println("===============================");

        // Step 1: Ask for Appointment ID
        int appointmentId;
        while (true) {
            System.out.print("\nEnter Appointment ID to Update: ");
            String input = sc.nextLine().trim();

            if (input.matches("\\d+")) {
                appointmentId = Integer.parseInt(input);
                String checkSQL = "SELECT COUNT(*) FROM appointments_tbl WHERE a_id = ? AND status = 'Pending'";
                if (dbc.getSingleValue(checkSQL, appointmentId) != 0) {
                    break;
                } else {
                    System.out.println("❌ No pending appointment with that ID. Try again.");
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a numeric ID.");
            }
        }

        // Step 2: Retrieve current appointment info
        String infoSQL = "SELECT a.customer_id, a.service_id, a.staff_id, c.c_fname AS customer, " +
                         "s.service_name, st.s_fname AS staff " +
                         "FROM appointments_tbl a " +
                         "JOIN customers_tbl c ON a.customer_id = c.c_id " +
                         "JOIN services_tbl s ON a.service_id = s.service_id " +
                         "JOIN staff_tbl st ON a.staff_id = st.s_id " +
                         "WHERE a.a_id = ?";
        Map<String, String> info = dbc.getSingleRecord(infoSQL, appointmentId);

        int customerId = Integer.parseInt(info.get("customer_id"));
        int serviceId = Integer.parseInt(info.get("service_id"));
        int staffId = Integer.parseInt(info.get("staff_id"));

        System.out.println("\n🔁 Current Info:");
        System.out.println("Customer: " + info.get("customer"));
        System.out.println("Service : " + info.get("service_name"));
        System.out.println("Staff   : " + info.get("staff"));

        // Step 3: New Date & Time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentDateTime = null;
        String dateInput = "";
        String timeInput = "";

        while (true) {
            try {
                System.out.print("Enter New Appointment Date (YYYY-MM-DD): ");
                dateInput = sc.nextLine().trim();

                System.out.print("Enter New Appointment Time (HH:MM): ");
                timeInput = sc.nextLine().trim();

                // Parse and validate
                LocalDate date = LocalDate.parse(dateInput, dateFormatter);
                LocalTime time = LocalTime.parse(timeInput, timeFormatter);
                appointmentDateTime = LocalDateTime.of(date, time);

                if (appointmentDateTime.isBefore(now)) {
                    System.out.println("❌ Cannot schedule an appointment in the past.\n");
                    continue;
                }

                // Check for conflicts
                String conflictCheck = "SELECT COUNT(*) FROM appointments_tbl " +
                                       "WHERE staff_id = ? AND date = ? AND time = ? AND status = 'Pending' AND a_id != ?";
                int conflict = dbc.getQuadValue(conflictCheck, staffId, dateInput, timeInput, appointmentId);

                if (conflict > 0) {
                    System.out.println("❌ Staff is already booked at that time. Please choose another slot.\n");
                    continue;
                }

                break;

            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid format. Use YYYY-MM-DD for date and HH:MM for time.\n");
            }
        }

        // Step 4: Perform Update
        String updateSQL = "UPDATE appointments_tbl SET date = ?, time = ?, updated_at = ? WHERE a_id = ?";
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        dbc.updateRecord(updateSQL, dateInput, timeInput, nowStr, appointmentId);
        System.out.println("\n✅ Appointment updated successfully!");
    }

    
    public void cancelAppointment() {
        System.out.println("\n===============================");
        System.out.println("        CANCEL APPOINTMENT");
        System.out.println("===============================");

        int appointmentId;
        System.out.print("\nEnter Appointment ID to Cancel: ");

        while (true) {
            if (sc.hasNextInt()) {
                appointmentId = sc.nextInt();
                sc.nextLine(); // Clear buffer

                // Check if appointment exists and is still pending
                String checkSQL = "SELECT COUNT(*) FROM appointments_tbl WHERE a_id = ? AND status = 'Pending'";
                if (dbc.getSingleValue(checkSQL, appointmentId) != 0) {
                    break;
                } else {
                    System.out.print("❌ No pending appointment found with ID " + appointmentId + ". Try again: ");
                }
            } else {
                System.out.print("❌ Invalid input! Please enter a numeric Appointment ID: ");
                sc.next(); // Clear invalid input
            }
        }

        // Update the status to 'Cancelled'
        String updateSQL = "UPDATE appointments_tbl SET status = 'Cancelled', updated_at = ? WHERE a_id = ?";
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        boolean success = dbc.updateRecord(updateSQL, now, appointmentId);

        if (success) {
            System.out.println("\n✅ Appointment cancelled successfully!");
        } else {
            System.out.println("\n❌ Failed to cancel appointment. Please try again.");
        }
    }

}
