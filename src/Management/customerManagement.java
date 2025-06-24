
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class customerManagement {
    Scanner sc = new Scanner(System.in);
    DBconnector dbc = new DBconnector();
    
    public void customerCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                 👤  CUSTOMER MANAGEMENT MENU                                             |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add New Customer                                                                                   |\n" +
                                "|  [2] View All Customers                                                                                 |\n" +
                                "|  [3] Search Customer                                                                                    |\n" +
                                "|  [4] Update Customer Info                                                                               |\n" +
                                "|  [5] Delete Customer                                                                                    |\n" +
                                "|  [6] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");
            
            action = utils.getValidatedChoice(1, 6);
            
            switch(action){
                case "1":
                    addCustomer();
                    break;
                
                case "2":
                    viewCustomers();
                    break;
                    
                case "3":
                    searchCustomer();
                    break;
                    
                case "4":
                    viewCustomers();
                    updateCustomer();
                    break;
                case "5": 
                    viewCustomers();
                    deleteCustomer();
                    break;
                    
                case "6": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("6"));
    }
    
    public void addCustomer() {
        System.out.println("\n===============================");
        System.out.println("      ADD NEW CUSTOMER");
        System.out.println("===============================");

        System.out.print("Enter Customer Name: ");
        String name = utils.getNonEmptyInput("", sc);

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

        String gender = "";
        while (true) {
            System.out.println("Select Gender:");
            System.out.println("[1] Male");
            System.out.println("[2] Female");
            System.out.print("Enter Choice (1 or 2): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                gender = "Male";
                break;
            } else if (choice.equals("2")) {
                gender = "Female";
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
        
        System.out.print("Enter Address: ");
        String address = utils.getNonEmptyInput("", sc);
        
        String sql = "INSERT INTO customers_tbl (c_fname, c_email, c_phone, c_gender, c_address, created_at, updated_at) VALUES (?,?,?,?,?,?,?)";
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
       
        dbc.addRecord(sql, name, email, pnumber, gender, address, now, now);
        System.out.println("\nCustomer Added Successfully!!!");
    }
    
    public void viewCustomers() {
        
        System.out.println("\n🧾 CUSTOMER LIST: ");
        
        String sqlQuery = "SELECT c_id, c_fname, c_email, c_phone, c_gender, c_address FROM customers_tbl";
        String[] columnHeaders = {"ID", "Name", "Email", "Phone Number", "Gender", "Address"};
        String[] columnNames = {"c_id", "c_fname", "c_email", "c_phone", "c_gender", "c_address"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);
    }
    
    public void updateCustomer() {
        System.out.println("\n===============================");
        System.out.println("         UPDATE CUSTOMER");
        System.out.println("===============================");

        // Ask for Customer ID
        System.out.print("\nEnter Customer ID to update: ");
        String idInput = sc.nextLine().trim();

        if (!idInput.matches("\\d+")) {
            System.out.println("❌ Invalid ID format.");
            return;
        }

        int id = Integer.parseInt(idInput);

        // ✅ Check if the customer exists
        String checkSql = "SELECT c_id FROM customers_tbl WHERE c_id = ?";
        int existingId = (int) dbc.getSingleValue(checkSql, id);

        if (existingId == 0) {
            System.out.println("❌ Customer with ID " + id + " does not exist.");
            return;
        }

        // Continue with update inputs
        System.out.println("\n🔁 Enter new values (leave blank to keep existing):");

        System.out.print("New Name: ");
        String name = sc.nextLine().trim();

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

        System.out.print("New Address: ");
        String address = sc.nextLine().trim();

        String gender = "";
        while (true) {
            System.out.println("Select Gender:");
            System.out.println("[1] Male");
            System.out.println("[2] Female");
            System.out.println("[3] Keep Existing");
            System.out.print("Enter Choice (1-3): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                gender = "Male"; break;
            } else if (choice.equals("2")) {
                gender = "Female"; break;
            } else if (choice.equals("3")) {
                gender = ""; break;
            } else {
                System.out.println("❌ Invalid choice.");
            }
        }

        // ✅ Build SQL only with changed fields
        StringBuilder sql = new StringBuilder("UPDATE customers_tbl SET ");
        List<Object> values = new ArrayList<>();

        if (!name.isEmpty()) {
            sql.append("c_fname = ?, ");
            values.add(name);
        }
        if (!email.isEmpty()) {
            sql.append("c_email = ?, ");
            values.add(email);
        }
        if (!pnumber.isEmpty()) {
            sql.append("c_phone = ?, ");
            values.add(pnumber);
        }
        if (!gender.isEmpty()) {
            sql.append("c_gender = ?, ");
            values.add(gender);
        }
        if (!address.isEmpty()) {
            sql.append("c_address = ?, ");
            values.add(address);
        }

        if (values.isEmpty()) {
            System.out.println("⚠️ No fields to update. Operation cancelled.");
            return;
        }

        // Add updated_at and id
        sql.append("updated_at = ? WHERE c_id = ?");
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        values.add(id);

        // ✅ Call reusable update method
        dbc.updateRecord(sql.toString(), values.toArray());
        System.out.println("✅ Customer updated successfully.");
    }


    public void deleteCustomer() {
        System.out.println("\n===============================");
        System.out.println("         DELETE CUSTOMER");
        System.out.println("===============================");
        int customerId;
        System.out.print("\nEnter Customer ID to Delete: ");

        while (true) {
            
            if (sc.hasNextInt()) {
                customerId = sc.nextInt();
                sc.nextLine(); 

                String sql = "SELECT c_id FROM customers_tbl WHERE c_id = ?";
                if (dbc.getSingleValue(sql, customerId) != 0) {
                    break; 
                } else {
                    System.out.print("Customer with ID " + customerId + " does not exist!"
                            + " Please try again : ");
                }
            } else {
                System.out.print("Invalid input! Please enter a numeric Product ID :");
                sc.next(); 
            }
        }
        System.out.println("");
        String qry = "DELETE FROM customers_tbl WHERE c_id = ?";
        dbc.deleteRecord(qry, customerId);
        System.out.println("Customer Deleted successfully!!!");
    }
    
    public void searchCustomer() {
        System.out.println("\n===============================");
        System.out.println("        SEARCH CUSTOMER");
        System.out.println("===============================");

        System.out.print("Search by [1] ID or [2] Name: ");
        String choice = sc.nextLine().trim();

        String sql;
        String[] headers = { "ID", "Name", "Email", "Phone", "Gender", "Address" };
        String[] columns = { "c_id", "c_fname", "c_email", "c_phone", "c_gender", "c_address" };

        if (choice.equals("1")) {
            // Search by ID
            System.out.print("Enter Customer ID: ");
            String idInput = sc.nextLine().trim();

            if (!idInput.matches("\\d+")) {
                System.out.println("❌ Invalid ID format.");
                return;
            }

            sql = "SELECT c_id, c_fname, c_email, c_phone, c_gender, c_address FROM customers_tbl WHERE c_id = " + idInput;
            dbc.viewRecords(sql, headers, columns);

        } else if (choice.equals("2")) {
            // Search by Name (partial match)
            System.out.print("Enter Customer Name: ");
            String nameInput = sc.nextLine().trim();

            if (nameInput.isEmpty()) {
                System.out.println("❌ Name cannot be empty.");
                return;
            }

            sql = "SELECT c_id, c_fname, c_email, c_phone, c_gender, c_address FROM customers_tbl WHERE c_fname LIKE '%" + nameInput + "%'";
            dbc.viewRecords(sql, headers, columns);

        } else {
            System.out.println("❌ Invalid choice.");
        }
    }

}
