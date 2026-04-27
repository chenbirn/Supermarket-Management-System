package presentationLayer;

import DTO.SupplierDTO;
import domainLayer.Supplier;
import enums.PaymentType;
import enums.SupplierStatus;
import serviceLayer.SupplierService;

import java.util.EnumSet;
import java.util.Scanner;

/**
 * SupplyServiceMenu handles the user interaction
 * for supplier_id operations like adding, editing, and removing suppliers.
 */
public class SupplyServiceMenu {
    private final SupplierService supplierManager;
    private final Scanner scanner = new Scanner(System.in);

    public SupplyServiceMenu(SupplierService supplierManager) {
        this.supplierManager = supplierManager;
    }

    /**
     * Displays the main supplier_id menu and handles user choices.
     */
    public void menu() {
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Add a new supplier to system");
            System.out.println("2. Edit supplier details");
            System.out.println("3. Remove supplier from the system");
            System.out.println("4. Show all suppliers in system");
            System.out.println("5. Go back to main menu");

            int userChoice = getIntInRange(1, 5);

            if (userChoice == 1) {
                choice1();
            } else if (userChoice == 2) {
                choice2();
            } else if (userChoice == 3) {
                choice3();
            } else if (userChoice == 4) {
                choice4();
            } else {
                System.out.println("Back to main menu...");
                return;
            }
        }
    }

    /**
     * Adds a new supplier_id by asking the user for details.
     * Shows errors if input is wrong.
     */
    private void choice1() {
        String name = getValidName("Enter supplier name (letters and spaces only): ");

        System.out.println("Enter supplier address: ");
        String address = scanner.nextLine();

        int id = positiveIntHelper("Enter supplier ID (positive number): ");
        try {
            if (supplierManager.findSupplierById(id) != null) {
                System.out.println("Supplier already exists. Back to menu..");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int bankAccount = positiveIntHelper("Enter bank account number (positive number): ");
        EnumSet<PaymentType> paymentTypes = getPaymentTypes();

        // Enter first contact
        String contactName;
        String contactPhone;

        while (true) {
            contactName = getValidName("Enter first contact name (letters and spaces only): ");

            System.out.println("Enter first contact phone number: ");
            contactPhone = scanner.nextLine();

            try {
                supplierManager.createContact(contactName, contactPhone);
                break;
            } catch (Exception e) {
                System.out.println("Invalid contact details: " + e.getMessage() + "\nPlease enter name and phone again.");
            }
        }

        // Create the supplier_id with one contact in the list
        try {
            SupplierDTO dto = new SupplierDTO(id, name, address, paymentTypes, bankAccount, SupplierStatus.ACTIVE, contactName, contactPhone, null, null);
            String creationRes = supplierManager.createNewSupplier(dto);
            System.out.println(creationRes);
        } catch (Exception e) {
            System.out.println("\nError creating supplier: " + e.getMessage());
            return;
        }

        // Ask if user wants to add second contact
        System.out.println("Do you want to add a second contact? (y/n)");
        String ans = scanner.next().toLowerCase();
        scanner.nextLine(); // Clear buffer

        if (ans.equals("y")) {
            while (true) {
                String secondName = getValidName("Enter second contact name (letters and spaces only): ");

                System.out.println("Enter second contact phone number: ");
                String secondNum = scanner.nextLine();

                try {
                    String addContactRes = supplierManager.addContactToSupplier(id,
                            supplierManager.createContact(secondName, secondNum));
                    System.out.println(addContactRes);
                    break;  // Success
                } catch (Exception e) {
                    System.out.println("Invalid second contact details: " + e.getMessage() + "\nPlease enter name and phone again.");
                }
            }
        }
        System.out.println("All supplier details saved successfully! Back to supplier menu...\n");
    }

    /**
     * Allows editing existing supplier_id details (name, address, payment, bank account, contacts).
     */
    private void choice2() {
        int id = positiveIntHelper("Enter supplier ID to edit: ");
        try {
            if (!supplierManager.supplierExists(id)) {
                System.out.println("\nSupplier not found. Returning to menu...");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            Supplier s = supplierManager.findSupplierById(id);
            if (s.getSupplierStatus() != SupplierStatus.ACTIVE) {
                System.out.println("\nSupplier is not active. Back to menu...\n");
                return;
            }
            System.out.println("You selected the following supplier:");
            System.out.println(s);
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            System.out.println("What would you like to edit?");
            System.out.println("1. Change supplier name");
            System.out.println("2. Change supplier address");
            System.out.println("3. Change supplier payment types");
            System.out.println("4. Change supplier bank account");
            System.out.println("5. Add new contact");
            System.out.println("6. Remove a contact");
            System.out.println("7. Back to supplier menu");

            int choice = getIntInRange(1, 7);

            try {
                if (choice == 1) {
                    String newName = getValidName("Enter new supplier name (letters and spaces only):");

                    if (confirmAction("Are you sure you want to update the supplier name?")) {
                        System.out.println(supplierManager.changeSupplierName(id, newName) + "\n");
                    } else {
                        System.out.println("\nName change cancelled.\n");
                    }
                } else if (choice == 2) {
                    System.out.println("Enter new address: ");
                    String newAddress = scanner.nextLine();

                    if (confirmAction("Are you sure you want to update the supplier address?")) {
                        System.out.println(supplierManager.changeSupplierAddress(id, newAddress) + "\n");
                    } else {
                        System.out.println("\nAddress change cancelled.\n");
                    }
                } else if (choice == 3) {
                    EnumSet<PaymentType> newPayment = getPaymentTypes();
                    if (confirmAction("Are you sure you want to update the supplier payment types?")) {
                        System.out.println(supplierManager.changeSupplierPaymentTypes(id, newPayment) + "\n");
                    } else {
                        System.out.println("\nPayment types change cancelled.\n");
                    }
                } else if (choice == 4) {
                    int newBankA = positiveIntHelper("Enter new bank account number: ");
                    if (confirmAction("Are you sure you want to update the supplier bank account?")) {
                        System.out.println(supplierManager.changeSupplierBankAccount(id, newBankA) + "\n");
                    } else {
                        System.out.println("\nBank account change cancelled.\n");
                    }
                } else if (choice == 5) {
                    if (supplierManager.getNumOfContacts(id) >= 2) {
                        System.out.println("Supplier already has two contacts. Cannot add more contacts." + "\n");
                        continue; // Back to editing menu
                    }
                    while (true) {
                        String name = getValidName("Enter contact name (letters and spaces only): ");
                        System.out.println("Enter contact phone: ");
                        String phone = scanner.nextLine();

                        try {
                            System.out.println(supplierManager.addContactToSupplier(id,
                                    supplierManager.createContact(name, phone)) + "\n");
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid contact details: " + e.getMessage() + " Please enter name and phone again.");
                        }
                    }
                } else if (choice == 6) {
                    if (supplierManager.getNumOfContacts(id) == 1) {
                        System.out.println("\nSupplier has only one contact. Cannot remove.");
                        continue;  // Cannot remove the last contact
                    }
                    System.out.println("--- Current Contacts ---");
                    supplierManager.printSupplierContacts(id);

                    System.out.println("\nEnter the number of the contact to remove: ");
                    int contactChoice = getIntInRange(1, supplierManager.getNumOfContacts(id));

                    try {
                        supplierManager.removeContactFromSupplier(id,
                                supplierManager.getContactByPosition(id, contactChoice));
                        System.out.println("Contact removed successfully!\n");
                    } catch (Exception e) {
                        System.out.println("\nError during contact removal: " + e.getMessage());
                    }

                } else if (choice == 7) {
                    System.out.println("Returning to supplier menu...\n");
                    return;
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
    }

    /**
     * Removes a supplier_id from the system (or sets it to INACTIVE if needed).
     */
    private void choice3() {
        try {
            if (supplierManager.getAllSuppliers().isEmpty()) {
                System.out.println("There are no suppliers in the system. Cannot remove.\nBack to menu...\n");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        try {
            System.out.println("Please choose supplier: ");
            for (Supplier sup : supplierManager.getAllSuppliers()) {
                System.out.println(sup);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Enter supplier ID number: ");
        int id;
        while (true) {
            try {
                id = scanner.nextInt();
                scanner.nextLine();
                if (supplierManager.findSupplierById(id) == null) {
                    System.out.println("Supplier not found. Please enter a valid supplier ID:");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter digits only:");
                scanner.nextLine();
            }
        }

        try {
            Supplier supplier = supplierManager.findSupplierById(id);
            if (supplier == null) {
                System.out.println("Supplier not found.");
                return;
            }
            System.out.println("\n" + supplier);

            if (!confirmAction("Are you sure you want to delete this supplier?")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            boolean success = supplierManager.removeSupplier(id);
            if (success) {
                System.out.println("Supplier removed successfully.\n");
            } else {
                System.out.println("\nSupplier could not be fully removed due to active agreements" +
                        " or orders." + " Status changed to INACTIVE.");
            }
        } catch (Exception e) {
            System.out.println("\nError during supplier removal: " + e.getMessage());
        }
    }

    //Helper Functions//

    /**
     * Asks the user for a positive integer (repeats until valid input).
     */
    private int positiveIntHelper(String userMessage) {
        int number = -1;
        while (number <= 0) {
            System.out.println(userMessage + "(up to 9 digits)");
            String input = scanner.nextLine().trim();

            if (!input.matches("\\d+")) {
                System.out.println("Invalid input. Please enter digits only.");
                continue;
            }

            if (input.length() > 9) {
                System.out.println("Number is too long. Please enter a number with up to 9 digits.");
                continue;
            }

            try {
                number = Integer.parseInt(input);
                if (number <= 0) {
                    System.out.println("Please enter a positive number greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Number is too large. Please enter a number with up to 9 digits.");
            }
        }
        return number;
    }

    /**
     * Gets an integer input in a specific range.
     */
    private int getIntInRange(int min, int max) {
        int num = -1;
        while (num < min || num > max) {
            try {
                num = scanner.nextInt();
                scanner.nextLine();
                if (num < min || num > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ":");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter digits only.");
                scanner.nextLine();
            }
        }
        return num;
    }

    /**
     * Lets the user select multiple payment types.
     */
    private EnumSet<PaymentType> getPaymentTypes() {
        EnumSet<PaymentType> paymentTypes = EnumSet.noneOf(PaymentType.class);
        System.out.println("Select payment type (Enter 5 when you're done): ");

        while (true) {
            int idx = 1;
            for (PaymentType pt : PaymentType.values()) {
                System.out.println(idx + ". " + pt);
                idx++;
            }
            System.out.println("5. No more payment types");

            int choice = getIntInRange(1, 5);

            if (choice == 5) {
                if (paymentTypes.isEmpty()) {
                    System.out.println("You must select at least one payment type before finishing. " +
                            "Please choose one.");
                    continue;
                } else {
                    System.out.println("Payment types selected successfully!");
                    break;
                }
            } else {
                PaymentType payTSelected = PaymentType.values()[choice - 1];
                if (paymentTypes.contains(payTSelected)) {
                    System.out.println("You already selected " + payTSelected +
                            ". Please choose a different one.");
                } else {
                    paymentTypes.add(payTSelected);
                    System.out.println(payTSelected + " added to your selection.");
                }
            }
        }
        return paymentTypes;
    }

    /**
     * Helper function to get valid name (letters only)
     * Keeps asking until a valid name is entered.
     */
    private String getValidName(String message) {
        String name;
        while (true) {
            System.out.println(message);
            name = scanner.next();
            scanner.nextLine();

            if (name.matches("[a-zA-Z ]+")) {
                if (name.trim().isEmpty()) {
                    System.out.println("Invalid input. Name cannot be empty. Please try again.");
                    continue;
                }
                return name;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    /**
     * Asks the user to confirm an action (y/n).
     * Returns true if the user confirms ('y'), false if the user denies ('n').
     * Keeps asking until the user enters a valid input ('y' or 'n').
     */
    private boolean confirmAction(String message) {
        while (true) {
            System.out.println(message + " (y/n)");
            String ans = scanner.next().trim().toLowerCase();
            scanner.nextLine();

            if (ans.equals("y")) {
                return true;
            } else if (ans.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    private void choice4() {
        try {
           for (Supplier supplier: supplierManager.getAllSuppliers())
               System.out.println(supplier);
           System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}