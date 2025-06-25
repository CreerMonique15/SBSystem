
package salon.booking.system;

import Management.appointmentBooking;
import Management.categoryManagement;
import Management.customerManagement;
import Management.payment;
import Management.reports;
import Management.serviceManagement;
import Management.staffManagement;
import config.utils;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        String action;
        
        do{
            System.out.println( "|=========================================================================================================|\n" +
                                "|                                                                                                         |\n" +
                                "|                                  ✂ WELCOME TO SALON BOOKING SYSTEM ✂                                  |\n" +
                                "|                                                                                                         |\n" +
                                "|=========================================================================================================|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Customer Management                                                                                |\n" +
                                "|  [2] Staff Management                                                                                   |\n" +
                                "|  [3] Service Management                                                                                 |\n" +
                                "|  [4] Category Management                                                                                |\n" +
                                "|  [5] Appointment Booking                                                                                |\n" +
                                "|  [6] Payment                                                                                            |\n" +
                                "|  [7] Report                                                                                             |\n" +
                                "|  [8] Exit                                                                                               |\n" +
                                "|                                                                                                         |\n" +
                                "|_________________________________________________________________________________________________________|");
            
            action = utils.getValidatedChoice(1, 8  );

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
                    ab.appointmentCRUD();
                    break;
                
                case "6":
                    payment pay = new payment();
                    pay.paymentCRUD();
                    break;
              
                case "7":
                    reports rep = new reports();
                    rep.report();
                    break;
                    
                case "8": // Exit Option
                    System.out.println("Exiting the application...");
                    break;
                    
            }
            
        }while( !action.equals("8"));
        
        sc.close();
        
    }
    
}
