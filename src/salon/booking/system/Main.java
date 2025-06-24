
package salon.booking.system;

import Management.appointmentBooking;
import Management.categoryManagement;
import Management.customerManagement;
import Management.serviceManagement;
import Management.staffManagement;
import config.utils;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        String action;
        
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                ✂️  WELCOME TO SALON BOOKING SYSTEM ✂️                                  |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Customer Management                                                                                |\n" +
                                "|  [2] Staff Management                                                                                   |\n" +
                                "|  [3] Service Management                                                                                 |\n" +
                                "|  [4] Category Management                                                                                 |\n" +
                                "|  [5] Appointment Booking                                                                                |\n" +
                                "|  [6] Exit                                                                                               |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");
            
            action = utils.getValidatedChoice(1, 6  );

            switch(action){
                case "1":
                    customerManagement cm = new customerManagement();
                    cm.customerCRUD(); 
                    break;
                
                case "2":
                    staffManagement sm = new staffManagement();
                    sm.staffCRUD();
                    break;
                    
                case "3":
                    serviceManagement sem = new serviceManagement();
                    sem.serviceCRUD();
                    break;
                    
                case "4":
                    categoryManagement cam = new categoryManagement();
                    cam.categoryCRUD();
                    break;
                case "5":
                    appointmentBooking ab = new appointmentBooking();
                    ab.appoinmentCRUD();
                    break;
                    
                case "6": // Exit Option
                    System.out.println("Exiting the application...");
                    break;
                    
            }
            
        }while( !action.equals("6"));
        
        sc.close();
        
    }
    
}
