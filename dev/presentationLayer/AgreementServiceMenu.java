package presentationLayer;

import domainLayer.Agreement;
import domainLayer.AgreementProduct;
import enums.*;
import domainLayer.Supplier;
import serviceLayer.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Presentation layer for managing supplier_id agreements through a user interface.
 * Handles creating, editing, and viewing agreements by interacting with the AgreementService.
 */
public class AgreementServiceMenu {
    private final AgreementService agreementManager;
    private final SupplierService supplierManager;
    private final ProductService productService;

    public AgreementServiceMenu(AgreementService agreementManager, SupplierService supplierManager, ProductService productService) {
        this.agreementManager = agreementManager;
        this.supplierManager = supplierManager;
        this.productService = productService;
    }

    /**
     * Displays the main agreement management menu.
     * Allows the user to select actions related to supplier_id agreements.
     */
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Create new agreement");
            System.out.println("2. See all items in agreement");
            System.out.println("3. Edit an agreement");
            System.out.println("4. Show all agreements");
            System.out.println("5. Go back to main menu");
            int user_choice;
            while (true) {
                try {
                    user_choice = scanner.nextInt();
                    if (user_choice < 1 || user_choice > 5) {
                        System.out.println("Invalid input. Please Enter a number between 1 and 4.");
                        scanner.nextLine();
                    } else
                        break;
                } catch (Exception e) {
                    System.out.println("Invalid input. Please Enter a number between 1 and 4.");
                    scanner.nextLine();
                }
            }
            if (user_choice == 1) {
                choice1();
            }
            if (user_choice == 2) {
                choice2();
            }
            if (user_choice == 3) {
                choice3();
            }
            if (user_choice == 4) {
                choice4();
            }
            if (user_choice == 5) {
                System.out.println("back to main menu...\n");
                return;
            }
        }
    }

    /**
     * Handles the creation of a new agreement based on user input.
     * Collects delivery days, delivery method, and supplier_id ID from the user.
     */
    private void choice1() {
        Scanner scanner = new Scanner(System.in);
        try {
            if (supplierManager.getAllSuppliers().isEmpty() || supplierManager.activeSuppliers().isEmpty()) {
                System.out.println("There are no suppliers in system. Creating an agreement is impossible.\nBack to menu...");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Please choose supplier:");
        try {
            for (Supplier sup : supplierManager.activeSuppliers()) {
                System.out.println(sup.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Enter supplier id number: ");
        int sup;
        while (true) {
            try {
                sup = scanner.nextInt();
                if (supplierManager.findSupplierById(sup) == null)
                    System.out.println("Supplier not exist. Please enter supplier id number: ");
                else if (!supplierManager.activeSuppliers().contains(supplierManager.findSupplierById(sup))) {
                    System.out.println("Supplier inactive. Please enter supplier id number: ");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please choose supplier: ");
                scanner.nextLine();
            }
        }
        DeliveryMethod deliveryMethod = deliveryMethod();
        List<DeliveryDays> deliveryDays = deliveryDays();
        int newAgreementId = 0;
        try {
            newAgreementId = agreementManager.CreateAgreement(deliveryDays, deliveryMethod, sup);
            System.out.println("Agreement created successfully. Agreement's id: " + newAgreementId);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        String addItem = "y";
        System.out.println("Add item to Agreement:");
        while (addItem.equals("y")) {
            try {
                addItem(newAgreementId, sup);
                while (true) {
                    System.out.println("Add another item to agreement?(y/n): ");
                    addItem = scanner.next();
                    if (!(addItem.equals("y") || addItem.equals("n")))
                        System.out.println("Please enter 'y' or 'n' only.");
                    else
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Displays all items included in a specific agreement.
     * Collects the agreement ID from the user.
     */
    private void choice2() {
        int ag_id = enterAgreementId();
        if (ag_id != 0) {
            try {
                Collection<AgreementProduct> agreementProducts = agreementManager.ItemsInAgreement(ag_id);
                if (agreementProducts.isEmpty()) {
                    System.out.println("There are no items in this agreement. Back to menu...");
                    return;
                }
                for (AgreementProduct agreementProduct : agreementProducts)
                    System.out.println(agreementProduct);
            } catch (Exception e) {
                System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu.\n");

            }
        }
    }

    /**
     * Handles editing an existing agreement.
     * Allows the user to choose between several edit options:
     * - Add a new item
     * - Remove an item
     * - Add a quantity agreement to an item
     * - Edit an existing quantity agreement
     * - Change delivery days
     * - Change delivery method
     * - Change agreement status
     * - Return to the main menu
     * Prompts the user for an agreement ID first and then provides the editing options.
     */
    private void choice3() {
        int ag_id = enterAgreementId();
        if (ag_id == 0) {
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Please choose edit option:
                1. Add item
                2. Remove item
                3. Add quantity agreement to item
                4. Edit quantity agreement of item
                5. Change delivery days
                6. Change delivery method
                7. Change agreement's status
                8. Back to menu""");
        int opt;
        while (true) {
            try {
                opt = scanner.nextInt();
                if (opt < 1 || opt > 8) {
                    System.out.println("Invalid input. Please enter a number between 1 and 8.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 8.");
                scanner.nextLine();
            }
        }
        if (opt == 1) {
            opt1(ag_id);
        }
        if (opt == 2) {
            opt2(ag_id);
        }
        if (opt == 3) {
            opt3(ag_id);
        }
        if (opt == 4) {
            opt4(ag_id);
        }
        if (opt == 5) {
            opt5(ag_id);
        }
        if (opt == 6) {
            opt6(ag_id);
        }
        if (opt == 7) {
            opt7(ag_id);
        }
        if (opt == 8) {
            System.out.println("Back to menu..");
        }
    }

    /**
     * Adds a new item to the agreement.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt1(int agreement_id) {
        try {
            addItem(agreement_id, agreementManager.findSupplierIdByAgreementId(agreement_id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes an item from the agreement after user confirmation.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt2(int agreement_id) {
        Scanner scanner = new Scanner(System.in);
        int sys_id;
        while (true) {
            System.out.println("Please enter item's system id: ");
            try {
                sys_id = scanner.nextInt();
                boolean check;
                try {
                    check = agreementManager.checkItem(agreement_id, sys_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu.\n");
                    return;
                }
                if (!check) {
                    System.out.println("Item is not in agreement. Please enter a valid item id.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid item id.");
                scanner.nextLine();
            }
        }
        System.out.println("Are you sure you want to remove this item from agreement? (y/n): ");
        String answer;
        while (true) {
            answer = scanner.next();
            if (!(answer.equals("y") || answer.equals("n"))) {
                System.out.println("Invalid input. Please enter y/n.");
            } else
                break;
        }
        if (answer.equals("y")) {
            System.out.println(agreementManager.removeProduct(agreement_id, sys_id));
        } else
            System.out.println("Item was not removed.");
    }

    /**
     * Adds a quantity-based discount to an item in the agreement.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt3(int agreement_id) {
        int item_id = ValidItem(agreement_id);
        if (item_id != 0)
            addQuantityAgreement(agreement_id, item_id);
    }

    /**
     * Edits an existing quantity-based discount for an item in the agreement.
     * Allows changing discount value, quantity threshold, or discount method.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt4(int agreement_id) {
        Scanner scanner = new Scanner(System.in);
        int item_id = ValidItem(agreement_id);
        if (item_id == 0)
            return;
        System.out.println("Please choose edit option:\n1. Change discount\n2. Change quantity\n3. Change discount method\n4. Back to menu");
        int opt;
        while (true) {
            try {
                opt = scanner.nextInt();
                if (opt < 1 || opt > 4) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
                scanner.nextLine();
            }
        }
        if (opt == 1) {
            double discount;
            System.out.println("Please enter discount: ");
            while (true) {
                try {
                    discount = scanner.nextDouble();
                    if (discount <= 0) {
                        System.out.println("Invalid input. Please enter positive number.");
                        scanner.nextLine();
                    } else
                        break;
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter positive number.");
                    scanner.nextLine();
                }
            }
            System.out.println(agreementManager.editDiscountInQA(agreement_id, item_id, discount));
        }

        if (opt == 2) {
            int quantity;
            System.out.println("Please enter quantity: ");
            while (true) {
                try {
                    quantity = scanner.nextInt();
                    if (quantity <= 0) {
                        System.out.println("Invalid input. Please enter positive number.");
                        scanner.nextLine();
                    } else
                        break;
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter positive number.");
                    scanner.nextLine();
                }
            }
            System.out.println(agreementManager.editQuantityInQA(agreement_id, item_id, quantity));
        }
        if (opt == 3) {
            String discountMethodInput;
            DiscountMethod discountMethod;
            System.out.println("Please enter discount method (percentage/ amount): ");
            while (true) {
                try {
                    discountMethodInput = scanner.next();
                    if (discountMethodInput.equals("percentage")) {
                        discountMethod = DiscountMethod.PERCENTAGE;
                        break;
                    } else if (discountMethodInput.equals("amount")) {
                        discountMethod = DiscountMethod.AMOUNT;
                        break;
                    } else
                        System.out.println("Invalid input. Please enter percentage/ amount).");

                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter percentage/ amount).");
                }
            }
            System.out.println(agreementManager.editDiscountMethodInQA(agreement_id, item_id, discountMethod));
        }
        if (opt == 4) {
            System.out.println("Back to menu..");
        }
    }

    /**
     * Changes the delivery days assigned to an agreement.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt5(int agreement_id) {
        Scanner scanner = new Scanner(System.in);
        List<DeliveryDays> deliveryDays = deliveryDays();
        System.out.println(agreementManager.editDeliveryDays(agreement_id, deliveryDays));
    }

    /**
     * Changes the delivery method assigned to an agreement.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt6(int agreement_id) {
        DeliveryMethod deliveryMethod = deliveryMethod();
        System.out.println(agreementManager.editDeliveryMethod(agreement_id, deliveryMethod));
    }

    /**
     * Changes the status of an agreement (active/inactive) based on user confirmation.
     *
     * @param agreement_id Agreement ID.
     */
    private void opt7(int agreement_id) {
        Scanner scanner = new Scanner(System.in);
        AgreementStatus agreementStatus;
        try {
            agreementStatus = agreementManager.getCurrentStatus(agreement_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu.\n");
            return;
        }
        if (agreementStatus.equals(AgreementStatus.ACTIVE)) {
            System.out.println("Agreement's current status is: active. would you like to make agreement inactive (y/n)?");
        }
        if (agreementStatus.equals(AgreementStatus.INACTIVE)) {
            System.out.println("Agreement's current status is: inactive. would you like to make agreement active (y/n)?");
        }
        String answer;
        while (true) {
            try {
                answer = scanner.next();
                if (answer.equals("y")) {
                    if (agreementStatus.equals(AgreementStatus.ACTIVE)) {
                        System.out.println(agreementManager.changeStatus(agreement_id, AgreementStatus.INACTIVE));
                    } else {
                        System.out.println(agreementManager.changeStatus(agreement_id, AgreementStatus.ACTIVE));
                    }
                    return;
                } else if (answer.equals("n")) {
                    System.out.println("Status not changed. Back to menu...");
                } else
                    System.out.println("Invalid input. Please enter y/n.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter y/n.");
            }
        }
    }

    //wrapper functions//

    /**
     * Prompts the user to select a delivery method (self pickup or delivery).
     *
     * @return Selected DeliveryMethod.
     */
    private DeliveryMethod deliveryMethod() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the delivery method?\n1. self pickup\n2. delivery");
        while (true) {
            try {
                int dMethod = scanner.nextInt();
                if (dMethod == 1) {
                    return DeliveryMethod.SELF_PICKUP;
                } else if (dMethod == 2) {
                    return DeliveryMethod.DELIVERY;
                } else {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter 1 or 2.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Prompts the user to select delivery days for an agreement.
     *
     * @return List of selected DeliveryDays.
     */
    private List<DeliveryDays> deliveryDays() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What are the delivery days? Please enter all options and 0 when finish.");
        int counter = 1;
        for (DeliveryDays day : DeliveryDays.values()) {
            System.out.println(counter + ". " + day);
            counter++;
        }
        int deliveryD;
        List<DeliveryDays> deliveryDays = new ArrayList<>();
        while (true) {
            try {
                deliveryD = scanner.nextInt();
                if (deliveryD < 0 || deliveryD > 7) {
                    System.out.println("Invalid input. Please enter numbers between 1-7 and 0 when finish.");
                    scanner.nextLine();
                } else if (deliveryD == 0 && deliveryDays.isEmpty()) {
                    System.out.println("Must choose at least one day.");
                    scanner.nextLine();
                } else if (deliveryD == 1) {
                    if (!deliveryDays.contains(DeliveryDays.SUNDAY))
                        deliveryDays.add(DeliveryDays.SUNDAY);
                } else if (deliveryD == 2) {
                    if (!deliveryDays.contains(DeliveryDays.MONDAY))
                        deliveryDays.add(DeliveryDays.MONDAY);
                } else if (deliveryD == 3) {
                    if (!deliveryDays.contains(DeliveryDays.TUESDAY))
                        deliveryDays.add(DeliveryDays.TUESDAY);
                } else if (deliveryD == 4) {
                    if (!deliveryDays.contains(DeliveryDays.WEDNESDAY))
                        deliveryDays.add(DeliveryDays.WEDNESDAY);
                } else if (deliveryD == 5) {
                    if (!deliveryDays.contains(DeliveryDays.THURSDAY))
                        deliveryDays.add(DeliveryDays.THURSDAY);
                } else if (deliveryD == 6) {
                    if (!deliveryDays.contains(DeliveryDays.FRIDAY))
                        deliveryDays.add(DeliveryDays.FRIDAY);
                } else if (deliveryD == 7) {
                    if (!deliveryDays.contains(DeliveryDays.BY_ORDER))
                        deliveryDays.add(DeliveryDays.BY_ORDER);
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter numbers between 1-7 and 0 when finish.");
                scanner.nextLine();
            }
        }
        return deliveryDays;
    }

    /**
     * Adds a new item to an agreement based on user input.
     * May also add a quantity agreement if the user chooses.
     */
    private void addItem(int agreement_id, int supplier_id){
        Scanner scanner = new Scanner(System.in);
        int item_id;
        while (true) {
            System.out.println("Please enter Item's system-id: ");
            try {
                item_id = scanner.nextInt();
                if (productService.findProductById(item_id) == null) {
                    System.out.println("Item does not exist in system. enter Item's system-id: ");
                    scanner.nextLine();
                } else if (agreementManager.checkItem(agreement_id, item_id)) {
                    System.out.println("Item is already in agreement. back to menu...");
                    return;
                } else if (supplierManager.supplierProvidesProduct(supplier_id, item_id)) {
                    System.out.println("Item is already in one of supplier's agreements. Can't add item. back to menu...");
                    return;
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }
        double price;
        while (true) {
            System.out.println("Please enter Price: ");
            try {
                price = scanner.nextDouble();
                if (price < 0) {
                    System.out.println("Invalid input. Please enter a positive number.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }
        System.out.println(agreementManager.addItem(agreement_id, price, item_id));
        System.out.println("Is there a quantity agreement? (y/n): ");
        String answer;
        while (true) {
            answer = scanner.next();
            if (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Invalid input. Please enter y/n.");
            } else
                break;
        }
        if (answer.equals("y")) {
            addQuantityAgreement(agreement_id, item_id);
        }
    }

    /**
     * Adds a quantity-based discount agreement to an item.
     */
    private void addQuantityAgreement(int agreement_id, int item_id) {
        Scanner scanner = new Scanner(System.in);
        DiscountMethod discountMethod = null;
        System.out.println("Please enter discount method:\n1. percentage\n2. amount");
        int method = 0;
        while (true) {
            try {
                method = scanner.nextInt();
                if (method < 1 || method > 2) {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter 1 or 2.");
                scanner.nextLine();
            }
        }
        if (method == 1)
            discountMethod = DiscountMethod.PERCENTAGE;
        if (method == 2)
            discountMethod = DiscountMethod.AMOUNT;
        int quantity;
        while (true) {
            System.out.println("Please quantity for discount: ");
            try {
                quantity = scanner.nextInt();
                if (quantity < 1) {
                    System.out.println("Invalid input. Please enter a positive integer.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer.");
                scanner.nextLine();
            }
        }
        double discount;
        while (true) {
            System.out.println("Please enter discount: ");
            try {
                discount = scanner.nextDouble();
                if (discount < 0) {
                    System.out.println("Invalid input. Please enter a positive number.");
                    scanner.nextLine();
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive number.");
                scanner.nextLine();
            }
        }
        System.out.println(agreementManager.addQuantityAgreement(agreement_id, discount, quantity, discountMethod, item_id));
    }

    /**
     * Prompts the user to enter and validate an item ID within a given agreement.
     *
     * @return Validated system item ID, or 0 if a problem occurred.
     */
    private int ValidItem(int agreement_id) {
        Scanner scanner = new Scanner(System.in);
        int item_id = 0;
        System.out.println("Please enter item's system id: ");
        while (true) {
            boolean check;
            try {
                item_id = scanner.nextInt();
                if (productService.findProductById(item_id) == null) {
                    System.out.println("Item does not exist in system. Back to menu...");
                    return 0;
                }
                try {
                    check = agreementManager.checkItem(agreement_id, item_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    return 0;
                }
                if (!check) {
                    System.out.println("Item is not in agreement. Back to menu...");
                    break;
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid item id.");
                scanner.nextLine();
            }
        }
        return item_id;
    }

    /**
     * Prompts the user to enter and validate an agreement ID.
     *
     * @return Validated agreement ID, or 0 if a problem occurred.
     */
    private int enterAgreementId() {
        Scanner scanner = new Scanner(System.in);
        int ag_id = 0;
        while (true) {
            System.out.println("Please enter agreement id: ");
            try {
                ag_id = scanner.nextInt();
                Agreement agreement = null;
                try {
                    agreement = agreementManager.findAgreement(ag_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    ag_id = 0;
                    break;
                }
                if (agreement == null) {
                    System.out.println("There is no agreement by this id. Back to menu...");
                    ag_id = 0;
                    break;
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid id: ");
                scanner.nextLine();
            }
        }
        return ag_id;
    }

    private void choice4() {
        try {
            if (agreementManager.getAllAgreements().isEmpty()) {
                System.out.println("There are no agreements in the system.\n");
                return;
            }
            System.out.println("All agreements:");
            for (Agreement ag : agreementManager.getAllAgreements()) {
                System.out.println(ag);
            }
        } catch (Exception e) {
            System.out.println("A problem occurred while fetching agreements. " + e.getMessage());
        }
    }
}

