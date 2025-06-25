
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class staffManagement {
    Scanner sc = new Scanner(System.in);
    DBconnector dbc = new DBconnector();
    
    public void staffCRUD() {    
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                 🧑‍💼  STAFF MANAGEMENT MENU                                               |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add New Staff                                                                                      |\n" +
                                "|  [2] View All Staff                                                                                     |\n" +
                                "|  [3] Update Staff Info                                                                                  |\n" +
                                "|  [4] Delete Staff                                                                                       |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");

            action = utils.getValidatedChoice(1, 6);
            
            switch(action){
                case "1":
                    addStaff();
                    break;
                
                case "2":
                    viewStaff();
                    break;
                    
                case "3":
                    viewStaff();
                    updateStaff();
                    break;
                    
                case "4":
                    viewStaff();
                    deleteStaff();
                    break;

                case "5": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("5"));
    }
    
    public void addStaff() {
        System.out.println("\n===============================");
        System.out.println("      ADD NEW STAFF");
        System.out.println("===============================");

        System.out.print("Enter Staff Name: ");
        String fname = utils.getNonEmptyInput("", sc);

        // --- Email with Validation ---
        String email;
        while (true) {
            System.out.print("Enter Email Address   : ");
            email = sc.nextLine().trim();
            if (utils.isValidEmail(email)) {
                break;
            } else {
                System.out.println("❌ Invalid email format. Please try again.");
            }
        }

        // --- Phone Number with Validation ---
        String pnumber;
        while (true) {
            System.out.print("Enter Phone Number    : ");
            pnumber = sc.nextLine().trim();
            if (utils.isValidPhone(pnumber)) {
                break;
            } else {
                System.out.println("❌ Invalid phone number. Use format 09XXXXXXXXX or +639XXXXXXXXX.");
            }
        }

        System.out.print("Enter Position: ");
        String position = utils.getNonEmptyInput("", sc);

        System.out.print("Enter Specialization: ");
        String specialization = utils.getNonEmptyInput("", sc);

        System.out.print("Enter Experience: ");
        String experience = utils.getNonEmptyInput("", sc);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "INSERT INTO staff_tbl (s_fname, s_email, s_phone, position, specialization, experience, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'Available', ?, ?)";

        dbc.addRecord(sql, fname, email, pnumber, position, specialization, experience, now, now);

        System.out.println("\n✅ Staff Added Successfully!");
    }
    
    public void viewStaff() {
        
        System.out.println("\n🧾 STAFF LIST: ");
        
        String sqlQuery = "SELECT s_id, s_fname, s_email, s_phone, position, specialization, experience, status FROM staff_tbl";
        String[] columnHeaders = {"ID", "Name", "Email", "Phone Number", "Position", "Specialization", "Experience", "Status"};
        String[] columnNames = {"s_id", "s_fname", "s_email", "s_phone", "position", "specialization", "experience", "status"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);
    }
    
    public void updateStaff() {
        System.out.println("\n===============================");
        System.out.println("        UPDATE STAFF");
        System.out.println("===============================");

        System.out.print("Enter Staff ID to update: ");
        String idInput = sc.nextLine().trim();

        if (!idInput.matches("\\d+")) {
            System.out.println("❌ Invalid ID format.");
            return;
        }

        int staffId = Integer.parseInt(idInput);

        // Check if staff exists
        String checkSql = "SELECT s_id FROM staff_tbl WHERE s_id = ?";
        int exists = (int) dbc.getSingleValue(checkSql, staffId);
        if (exists == 0) {
            System.out.println("❌ Staff with ID " + staffId + " does not exist.");
            return;
        }

        // Start collecting new values
        System.out.println("\n🔁 Enter new values (leave blank to keep existing):");

        System.out.print("New Name: ");
        String fname = sc.nextLine().trim();

        String email;
        while (true) {
            System.out.print("New Email: ");
            email = sc.nextLine().trim();
            if (email.isEmpty() || utils.isValidEmail(email)) break;
            System.out.println("❌ Invalid email format.");
        }

        String pnumber;
        while (true) {
            System.out.print("New Phone: ");
            pnumber = sc.nextLine().trim();
            if (pnumber.isEmpty() || utils.isValidPhone(pnumber)) break;
            System.out.println("❌ Invalid phone number format.");
        }

        System.out.print("New Position: ");
        String position = sc.nextLine().trim();

        System.out.print("New Specialization: ");
        String specialization = sc.nextLine().trim();

        System.out.print("New Experience: ");
        String experience = sc.nextLine().trim();

        // Build dynamic SQL
        StringBuilder sql = new StringBuilder("UPDATE staff_tbl SET ");
        List<Object> values = new ArrayList<>();

        if (!fname.isEmpty()) {
            sql.append("s_fname = ?, ");
            values.add(fname);
        }
        if (!email.isEmpty()) {
            sql.append("s_email = ?, ");
            values.add(email);
        }
        if (!pnumber.isEmpty()) {
            sql.append("s_phone = ?, ");
            values.add(pnumber);
        }
        if (!position.isEmpty()) {
            sql.append("position = ?, ");
            values.add(position);
        }
        if (!specialization.isEmpty()) {
            sql.append("specialization = ?, ");
            values.add(specialization);
        }
        if (!experience.isEmpty()) {
            sql.append("experience = ?, ");
            values.add(experience);
        }

        if (values.isEmpty()) {
            System.out.println("⚠️ No fields to update. Operation cancelled.");
            return;
        }

        sql.append("updated_at = ? WHERE s_id = ?");
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        values.add(staffId);

        dbc.updateRecord(sql.toString(), values.toArray());
        System.out.println("\n✅ Staff updated successfully.");
    }
    
     public void deleteStaff() {
        System.out.println("\n===============================");
        System.out.println("         DELETE STAFF");
        System.out.println("===============================");
        int staffId;
        System.out.print("\nEnter Staff ID to Delete: ");

        while (true) {
            
            if (sc.hasNextInt()) {
                staffId = sc.nextInt();
                sc.nextLine(); 

                String sql = "SELECT s_id FROM staff_tbl WHERE s_id = ?";
                if (dbc.getSingleValue(sql, staffId) != 0) {
                    break; 
                } else {
                    System.out.print("Staff with ID " + staffId + " does not exist!"
                            + " Please try again : ");
                }
            } else {
                System.out.print("Invalid input! Please enter a numeric Staff ID :");
                sc.next(); 
            }
        }
        System.out.println("");
        String qry = "DELETE FROM staff_tbl WHERE s_id = ?";
        dbc.deleteRecord(qry, staffId);
        System.out.println("Staff Deleted successfully!!!");
    }


}
