
package Management;

import config.DBconnector;
import config.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class categoryManagement {
    
    Scanner sc = new Scanner(System.in); 
    DBconnector dbc = new DBconnector();
    
     public void categoryCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                   ===  CATEGORY MANAGEMENT MENU  ===                                    |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add New Category                                                                                   |\n" +
                                "|  [2] View All Categories                                                                                |\n" +
                                "|  [3] Update Category                                                                                    |\n" +
                                "|  [4] Delete Category                                                                                    |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");

            action = utils.getValidatedChoice(1, 5);
            
            switch(action){
                case "1":
                    addCategory();
                    break;
                
                case "2":
                    viewCategories();
                    break;
                    
                case "3":
                    viewCategories();
                    updateCategory();
                    break;
                    
                case "4":
                    viewCategories();
                    deleteCategory();
                    break;
                    
                case "5": // Back to the main Menu
                    System.out.println("Returning to Main Menu...");
                    break;
                    
            }
            
        }while(!action.equals("5"));
    }

    public void addCategory() {
        System.out.print("Enter Category Name: ");
        String name = utils.getNonEmptyInput("", sc);
        
        System.out.print("Enter Description: ");
        String des = utils.getNonEmptyInput("", sc);
        
        String sql = "INSERT INTO categories_tbl (name, description) VALUES (?,?)";
        dbc.addRecord(sql, name, des);
        
        System.out.println("\nCategory Added Successfully!!!");
    }
    
    public void viewCategories() {
        System.out.println("\n🧾 CATEGORY LIST: ");
        
        String sqlQuery = "SELECT category_id, name, description FROM categories_tbl";
        String[] columnHeaders = {"ID", "Name", "Description"};
        String[] columnNames = {"category_id", "name", "description"};

        dbc.viewRecords(sqlQuery, columnHeaders, columnNames);
    }
    public void updateCategory() {
        System.out.println("\n===============================");
        System.out.println("        UPDATE CATEGORY");
        System.out.println("===============================");

        System.out.print("Enter Category ID to update: ");
        String idInput = sc.nextLine().trim();

        if (!idInput.matches("\\d+")) {
            System.out.println("❌ Invalid ID format.");
            return;
        }

        int catId = Integer.parseInt(idInput);

        // Check if category exists
        String checkSql = "SELECT category_id FROM categories_tbl WHERE category_id = ?";
        int exists = (int) dbc.getSingleValue(checkSql, catId);
        if (exists == 0) {
            System.out.println("❌ Category with ID " + catId + " does not exist.");
            return;
        }

        System.out.println("\n🔁 Enter new values (leave blank to keep existing):");

        System.out.print("New Category Name     : ");
        String name = sc.nextLine().trim();

        System.out.print("New Description       : ");
        String desc = sc.nextLine().trim();

        if (name.isEmpty() && desc.isEmpty()) {
            System.out.println("⚠️ No fields to update. Operation cancelled.");
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE categories_tbl SET ");
        List<Object> values = new ArrayList<>();

        if (!name.isEmpty()) {
            sql.append("name = ?, ");
            values.add(name);
        }
        if (!desc.isEmpty()) {
            sql.append("description = ?, ");
            values.add(desc);
        }

        // Remove the trailing comma
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE category_id = ?");
        values.add(catId);

        dbc.updateRecord(sql.toString(), values.toArray());
        System.out.println("\n✅ Category updated successfully.");
    }

    
    public void deleteCategory() {
        System.out.println("\n===============================");
        System.out.println("         DELETE CATEGORY");
        System.out.println("===============================");
        int categoryId;
        System.out.print("\nEnter Category ID to Delete: ");

        while (true) {
            
            if (sc.hasNextInt()) {
                categoryId = sc.nextInt();
                sc.nextLine(); 

                String sql = "SELECT category_id FROM categories_tbl WHERE category_id = ?";
                if (dbc.getSingleValue(sql, categoryId) != 0) {
                    break; 
                } else {
                    System.out.print("Staff with ID " + categoryId + " does not exist!"
                            + " Please try again : ");
                }
            } else {
                System.out.print("Invalid input! Please enter a numeric Category ID :");
                sc.next(); 
            }
        }
        System.out.println("");
        String qry = "DELETE FROM categories_tbl WHERE category_id = ?";
        dbc.deleteRecord(qry, categoryId);
        System.out.println("Category Deleted successfully!!!");
    }
}
