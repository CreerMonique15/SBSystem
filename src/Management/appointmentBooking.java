
package Management;

import config.utils;
import java.util.Scanner;

public class appointmentBooking {
    public void appoinmentCRUD() {
        Scanner sc = new Scanner(System.in);     
        
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
}
