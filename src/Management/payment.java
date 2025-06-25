/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management;

import config.DBconnector;
import config.utils;
import java.util.Scanner;

/**
 *
 * @author mikel
 */
public class payment {
     Scanner sc = new Scanner(System.in);
    DBconnector dbc = new DBconnector();
    
    public void paymentCRUD() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                     ===  PAYMENT MANAGEMENT MENU  ===                                  |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Add Payment                                                                                        |\n" +
                                "|  [2] View All Payment History                                                                           |\n" +
                                "|  [3] Update Payment                                                                                     |\n" +
                                "|  [4] Delete Payment                                                                                     |\n" +
                                "|  [5] Back to Main Menu                                                                                  |\n" +
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
                    System.out.println("Returning to Main Menu...");
                    break;

            }
            
        }while(!action.equals("5"));
    }
}
