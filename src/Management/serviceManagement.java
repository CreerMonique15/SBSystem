
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class serviceManagement {
    
    Scanner sc = new Scanner(System.in);  
    DBconnector dbc = new DBconnector();
    staffManagement sm = new staffManagement();
    categoryManagement cm = new categoryManagement();
    public void serviceCRUD() {
   
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                   ===  SERVICE MANAGEMENT MENU  ===                                      |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add New Service                                                                                    |\n" +
                                "|  [2] View All Services                                                                                  |\n" +
                                "|  [3] Update Service                                                                                     |\n" +
                                "|  [4] Delete Service                                                                                     |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");

            action = utils.getValidatedChoice(1, 6);
            
            switch(action){
                case "1":
                    addService();
                    break;
                
                case "2":
                    viewService();
                    break;
                    
                case "3":
                    viewService();
                    updateService();
                    break;
                    
                case "4":
                    viewService();
                    deleteService();
                    break;
                    
                case "5": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("5"));
    }
    
    public void addService() {
        System.out.print("Enter Service Name: ");
        String sname = utils.getNonEmptyInput("", sc);
        
        System.out.print("Enter Description: ");
        String des = utils.getNonEmptyInput("", sc);
        
        cm.viewCategories();
        
        // Ask for category ID
        int catId;
        while (true) {
            System.out.print("Select Category ID     : ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                catId = Integer.parseInt(input);
                String checkSQL = "SELECT category_id FROM categories_tbl WHERE category_id = ?";
                if (dbc.getSingleValue(checkSQL, catId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Category ID not found. Try again.");
                }
            } else {
                System.out.println("❌ Please enter a valid numeric ID.");
            }
        }
        
        System.out.print("Enter Price: ");
        double price = utils.getValidDoubleInput("", sc);
        
        sm.viewStaff();

        // Ask for category ID
        int staffId;
        while (true) {
            System.out.print("Select Staff ID     : ");
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
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        String sql = "INSERT INTO services_tbl (service_name, description, category_id, price, assigned_staff, created_at, updated_at) VALUES (?,?,?,?,?,?,?)";
        dbc.addRecord(sql, sname, des, catId, price, staffId, now, now);
        
        System.out.println("\nCustomer Added Successfully!!!");
    }
    
    public void viewService() {

        System.out.println("\n🧾 SERVICE LIST: ");

        String sqlQuery = "SELECT s.service_id AS service_id, "+
                          "       s.service_name AS service_name, " +
                          "       s.description AS description, " +
                          "       c.name AS category, " +
                          "       s.price AS price, " +
                          "       st.s_fname AS staff " +
                          "FROM services_tbl s " +
                          "JOIN categories_tbl c ON s.category_id = c.category_id " +
                          "JOIN staff_tbl st ON s.assigned_staff = st.s_id";

        String[] columnHeaders = {"ID", " Service Name", "Description", "Category", "Price", "Assigned Staff"};
        String[] columnNames = {"service_id", "service_name", "description", "category", "price", "staff"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);
    }
    
    public void updateService() {
        System.out.println("\n===============================");
        System.out.println("         UPDATE SERVICE");
        System.out.println("===============================");

        // Ask for Service ID
        System.out.print("\nEnter Service ID to update: ");
        String idInput = sc.nextLine().trim();

        // Validate numeric input
        while (!idInput.matches("\\d+")) {
            System.out.println("❌ Invalid ID format. Please enter numbers only.\n");
            System.out.print("Enter Service ID again: ");
            idInput = sc.nextLine().trim();
        }

        int serviceId = Integer.parseInt(idInput);

        // Check if service exists
        while (dbc.getSingleValue("SELECT COUNT(*) FROM services_tbl WHERE service_id = ?", serviceId) == 0) {
            System.out.println("❌ Service with ID \"" + serviceId + "\" does not exist.\n");
            System.out.print("Enter Service ID again: ");
            idInput = sc.nextLine().trim();

            while (!idInput.matches("\\d+")) {
                System.out.println("❌ Invalid ID format. Please enter numbers only.\n");
                System.out.print("Enter Service ID again: ");
                idInput = sc.nextLine().trim();
            }

            serviceId = Integer.parseInt(idInput);
        }

        // Prompt for update fields
        System.out.println("\n🔁 Enter new values (leave blank to keep existing):");

        System.out.print("New Service Name: ");
        String name = sc.nextLine().trim();

        System.out.print("New Description: ");
        String desc = sc.nextLine().trim();

        // Category selection
        String categoryInput = "";
        int categoryId = 0;
        cm.viewCategories();
        while (true) {
            System.out.print("New Category ID (leave blank to keep existing): ");
            categoryInput = sc.nextLine().trim();
            if (categoryInput.isEmpty()) break;
            if (categoryInput.matches("\\d+")) {
                categoryId = Integer.parseInt(categoryInput);
                if (dbc.getSingleValue("SELECT COUNT(*) FROM categories_tbl WHERE category_id = ?", categoryId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Category ID not found.");
                }
            } else {
                System.out.println("❌ Invalid ID format.");
            }
        }

        // Price input
        double price = -1;
        System.out.print("New Price (leave blank to keep existing): ");
        String priceInput = sc.nextLine().trim();
        if (!priceInput.isEmpty()) {
            try {
                price = Double.parseDouble(priceInput);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid price format. Price will not be updated.");
            }
        }

        // Staff selection
        String staffInput = "";
        int staffId = 0;
        sm.viewStaff();
        while (true) {
            System.out.print("New Staff ID (leave blank to keep existing): ");
            staffInput = sc.nextLine().trim();
            if (staffInput.isEmpty()) break;
            if (staffInput.matches("\\d+")) {
                staffId = Integer.parseInt(staffInput);
                if (dbc.getSingleValue("SELECT COUNT(*) FROM staff_tbl WHERE s_id = ?", staffId) != 0) {
                    break;
                } else {
                    System.out.println("❌ Staff ID not found.");
                }
            } else {
                System.out.println("❌ Invalid ID format.");
            }
        }

        // Build SQL and values
        StringBuilder sql = new StringBuilder("UPDATE services_tbl SET ");
        List<Object> values = new ArrayList<>();

        if (!name.isEmpty()) {
            sql.append("service_name = ?, ");
            values.add(name);
        }
        if (!desc.isEmpty()) {
            sql.append("description = ?, ");
            values.add(desc);
        }
        if (categoryId != 0) {
            sql.append("category_id = ?, ");
            values.add(categoryId);
        }
        if (price >= 0) {
            sql.append("price = ?, ");
            values.add(price);
        }
        if (staffId != 0) {
            sql.append("assigned_staff = ?, ");
            values.add(staffId);
        }

        if (values.isEmpty()) {
            System.out.println("⚠️ No fields to update. Operation cancelled.");
            return;
        }

        sql.append("updated_at = ? WHERE service_id = ?");
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        values.add(serviceId);

        // Perform the update
        dbc.updateRecord(sql.toString(), values.toArray());
        System.out.println("✅ Service updated successfully.");
    }
    
     public void deleteService() {
        System.out.println("\n===============================");
        System.out.println("         DELETE SERVICE");
        System.out.println("===============================");
        int serviceId;
        System.out.print("\nEnter Service ID to Delete: ");

        while (true) {
            
            if (sc.hasNextInt()) {
                serviceId = sc.nextInt();
                sc.nextLine(); 

                String sql = "SELECT service_id FROM services_tbl WHERE service_id = ?";
                if (dbc.getSingleValue(sql, serviceId) != 0) {
                    break; 
                } else {
                    System.out.print("Service with ID " + serviceId + " does not exist!"
                            + " Please try again : ");
                }
            } else {
                System.out.print("Invalid input! Please enter a numeric Service ID :");
                sc.next(); 
            }
        }
        System.out.println("");
        String qry = "DELETE FROM services_tbl WHERE service_id = ?";
        dbc.deleteRecord(qry, serviceId);
        System.out.println("Service Deleted successfully!!!");
    }

}
