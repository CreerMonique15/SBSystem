package Management;

import config.utils;

public class reports {
    public void report() {
        
        String action;
        do{
            System.out.println( "___________________________________________________________________________________________________________\n" +
                                "|                                                                                                         |\n" +
                                "|                                     ===  REPORT HISTORY SECTION  ===                                   |\n" +
                                "|_________________________________________________________________________________________________________|\n" +
                                "|                                                                                                         |\n" +
                                "|  [1] Generate Specific Customer History                                                                 |\n" +
                                "|  [2] Generate Specific Staff History                                                                    |\n" +
                                "|  [3] Gerate All Appointment History                                                                     |\n" +
                                "|  [4] Back to Main Menu                                                                                  |\n" +
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
                    System.out.println("Returning to Main Menu...");
                    break;
                
            }
            
        }while(!action.equals("4"));
    }
}
