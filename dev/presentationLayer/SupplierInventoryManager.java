package presentationLayer;

import domainLayer.MainController;
import domainLayer.PeriodicOrder;
import serviceLayer.ProductService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SupplierInventoryManager {
    private final MainController mainController;
    private final ProductMenu productMenu;
    private final OrderServiceMenu orderMenu;
    private final SupplyServiceMenu supplyMenu;
    private final AgreementServiceMenu agreementMenu;

    public SupplierInventoryManager(MainController mainController,
                                    ProductMenu productMenu,
                                    OrderServiceMenu orderMenu,
                                    SupplyServiceMenu supplyMenu,
                                    AgreementServiceMenu agreementMenu) {
        this.mainController = mainController;
        this.productMenu = productMenu;
        this.orderMenu = orderMenu;
        this.supplyMenu = supplyMenu;
        this.agreementMenu = agreementMenu;
    }


    public void menu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int user_choice;
        //show all periodic orders that were created
        updatePeriodic();
        //check all low stock products
        mainController.checkLowStockAlert();
        mainController.updateProductPricesBasedOnCategoryDiscounts();
        while (true) {
            System.out.println("Welcome to Suppliers + Inventory Management System!\nWhat would you like to do?:");
            System.out.println("1. Manage orders");
            System.out.println("2. Manage products");
            System.out.println("3. Manage supplier cards");
            System.out.println("4. Manage agreements");
            System.out.println("5. Exit system");
            try {
                user_choice = scanner.nextInt();
                while (user_choice < 1 || user_choice > 5) {
                    System.out.println("Please Enter a number between 1 and 5.");
                    user_choice = scanner.nextInt();
                }
                if (user_choice == 1) {
                    if (getPassword() == 1)
                        orderMenu.menu();
                    else
                        System.out.println("Wrong password. back to main menu...");
                }
                if (user_choice == 2) {
                    if (getPassword() == 2)
                        productMenu.run();
                    else
                        System.out.println("Wrong password. back to main menu...");
                }
                if (user_choice == 3) {
                    if (getPassword() == 3)
                        supplyMenu.menu();
                    else
                        System.out.println("Wrong password. back to main menu...");
                }
                if (user_choice == 4) {
                    if (getPassword() == 4)
                        agreementMenu.menu();
                    else
                        System.out.println("Wrong password. back to main menu...");
                }
                if (user_choice == 5) {
                    System.out.println("bye bye");
                    break;
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                e.printStackTrace(); // אם את רוצה דיבאגינג אמיתי//////////////////////////////////////////////////////////////////////////
                System.out.println("Invalid Input. Please Enter a number between 1 and 5.");
                scanner.nextLine();
            }
        }
    }

    //wrapper function - get password from user, returns option number that matches password
    private int getPassword() {
        Scanner scanner = new Scanner(System.in);
        int password = 0;
        System.out.println("Please enter 4 digits password: ");
        while (true) {
            try {
                password = scanner.nextInt();
                //check input's length
                if (Integer.toString(password).length() != 4)
                    System.out.println("Invalid input. Please enter 4 digits password.");
                else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter 4 digits password.");
                scanner.nextLine();
            }
        }
        if (password == 1234)
            return 1;
        if (password == 5678)
            return 2;
        if (password == 4321)
            return 3;
        if (password == 8765)
            return 4;
        return 0;
    }

    public void updatePeriodic() {
        try {
            List<PeriodicOrder> orders = mainController.checkAllPeriodicOrders();
            if (!orders.isEmpty()) {
                System.out.println("The following periodic orders were created:");
                for (PeriodicOrder order : orders)
                    System.out.println(order);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


