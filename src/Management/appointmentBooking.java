
package Management;

import config.DBconnector;
import config.utils;
import java.util.Scanner;

public class appointmentBooking {
    Scanner sc = new Scanner(System.in); 
    DBconnector dbc = new DBconnector();
    customerManagement cm = new customerManagement();
    serviceManagement sm = new serviceManagement();
    
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
            
        }while(action.equals("5"));
    }
    
    public void addAppoinment(){
        cm.viewCustomers();
        
        // Ask for category ID
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
        
        
        
    }
}
