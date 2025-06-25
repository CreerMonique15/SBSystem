
package Management;

import config.DBconnector;
import config.utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                    
                    break;
                    
                case "4":
                    
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

        String sqlQuery = "SELECT s.service_name AS service_name, " +
                          "       s.description AS description, " +
                          "       c.name AS category, " +
                          "       s.price AS price, " +
                          "       st.s_fname AS staff " +
                          "FROM services_tbl s " +
                          "JOIN categories_tbl c ON s.category_id = c.category_id " +
                          "JOIN staff_tbl st ON s.assigned_staff = st.s_id";

        String[] columnHeaders = {"Service Name", "Description", "Category", "Price", "Assigned Staff"};
        String[] columnNames = {"service_name", "description", "category", "price", "staff"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);
    }

    
}
