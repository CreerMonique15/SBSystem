
package config;

import java.util.Scanner;
import java.util.regex.Pattern;

public class utils {
    
    private static Scanner sc = new Scanner(System.in);
    
    // Reusable method to get a validated choice as a String within a given numeric range
    public static String getValidatedChoice(int min, int max) {
        String input;

        while (true) {
            System.out.print(" Enter Action: ");
            input = sc.nextLine();
            System.out.println("|_________________________________________________________________________________________________________|");

            if (input.matches("\\d+")) {
                int numericChoice = Integer.parseInt(input);
                if (numericChoice >= min && numericChoice <= max) {
                    return input; // Return the original string input
                }
            }

            System.out.println("Invalid input! Please enter a number between " + min + " and " + max + ".\n");
        }
    }
    
    // Email validation using regex
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, email);
    }

    // Phone validation: 09XXXXXXXXX or +639XXXXXXXXX
    public static boolean isValidPhone(String phone) {
        String regex = "^(09\\d{9}|\\+639\\d{9})$";
        return Pattern.matches(regex, phone);
    }
    
    // Non Empty input
    public static String getNonEmptyInput(String prompt, Scanner sc) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("❌ This field cannot be empty. Please try again.");
            System.out.print("🔁 Enter again: ");
        }
    }
    
    public static double getValidDoubleInput(String prompt, Scanner sc) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("❌ Value must not be negative. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Please enter a valid decimal value.");
            }

            System.out.print("🔁 Enter again: ");
        }
    }


}
