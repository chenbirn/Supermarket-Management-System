package presentationLayer;

import domainLayer.*;
import enums.DeliveryDays;
import enums.OrderStatus;
import enums.SupplierStatus;
import serviceLayer.*;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Presentation layer for managing supplier_id orders through a user interface.
 * Handles creating, editing, and viewing orders by interacting with the OrderService.
 */
public class OrderServiceMenu {
    private final OrderService orderManager;
    private final SupplierService supplierManager ;

    public OrderServiceMenu(OrderService orderManager, SupplierService supplierManager) {
        this.orderManager = orderManager;
        this.supplierManager = supplierManager;
    }

    /**
     * Displays the main order management menu.
     * Allows the user to select actions related to supplier_id orders.
     */
    public void menu() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("What would you like to do?");
            System.out.println("1. Create a new order");
            System.out.println("2. Search for an order");
            System.out.println("3. Edit an order");
            System.out.println("4. Show all orders in system");
            System.out.println("5. Go back to main menu");
            try {
                int user_choice = scanner.nextInt();
                if (user_choice < 1 || user_choice > 5) {
                    System.out.println("Please Enter a number between 1 and 5.\n");
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
                    System.out.println("back to main menu...");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.\n");
                scanner.nextLine();
            }
        }
    }

    /**
     * Handles creating a new supplier_id order.
     * Allows the user to select a supplier_id, enter contact details, and add items to the order.
     */
    private void choice1() {
        Scanner scanner = new Scanner(System.in);
        try {
            if (supplierManager.getAllSuppliers().isEmpty() || supplierManager.activeSuppliers().isEmpty()) {
                System.out.println("There are no active suppliers in system. Creating an order is impossible.\nBack to menu...");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            System.out.println("Please choose supplier:");
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
                    System.out.println("Supplier not exist. Please enter supplier number: ");
                else if (!supplierManager.activeSuppliers().contains(supplierManager.findSupplierById(sup)))
                    System.out.println("\nSupplier inactive. Please enter supplier number: ");
                else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please choose supplier: ");
                scanner.nextLine();
            }
        }
        try {
            if (!supplierManager.hasActiveAgreements(sup)) {
                System.out.println("Supplier has no active signed agreements. Order cannot be made.\nBack to menu...");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        LocalDate deliveryDateFormat = null;
        try {
            if (supplierManager.getDeliveryDays(sup).size() == 1 && supplierManager.getDeliveryDays(sup).contains(DeliveryDays.BY_ORDER)) {
                System.out.println("Supplier deliver only by order. Please enter delivery date: (dd/mm/yyyy format)");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String deliveryDate = scanner.nextLine();
                deliveryDateFormat = LocalDate.parse(deliveryDate, formatter);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String contactNum;
        System.out.println("contact phone number: ");
        while (true) {
            try {
                contactNum = scanner.next();
                if ((!contactNum.matches("\\d+")) || contactNum.length() != 10)
                    System.out.println("Contact phone number should be 10 digits only. Please enter a valid phone number.");
                else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
        int newOrderId = 0;
        try {
            newOrderId = orderManager.createNewOrder(contactNum, sup);
            System.out.println("Order created successfully.");
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        //if supplier's delivery day is only by order, set manually delivery day to order
        if (deliveryDateFormat != null) {
            try {
                orderManager.setOrderDeliveryDate(newOrderId, deliveryDateFormat);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Add items to order:");
        String moreItems = "y";
        while (moreItems.equals("y")) {
            int item_id;
            while (true) {
                System.out.println("item's system-id: ");
                try {
                    item_id = scanner.nextInt();
                    if (!supplierManager.supplierProvidesProduct(sup, item_id)) {
                        System.out.println("Item is not provided by order's supplier. Please enter another item.");
                    } else if (orderManager.checkItem(newOrderId, item_id)) {
                        System.out.println("Item is already in order. Back to menu...");
                        return;
                    } else
                        break;
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    scanner.nextLine();
                }
            }
            int quantity;
            while (true) {
                System.out.println("quantity: ");
                try {
                    quantity = scanner.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                }
            }
            boolean dis = false;
            try {
                if (orderManager.checkQAgreement(sup, item_id)) {
                    System.out.println("add discount by quantity agreement? (y/n): ");
                    String discount = scanner.next();
                    //make sure it is only y or n
                    while (true) {
                        if (!(discount.equals("y") || discount.equals("n"))) {
                            System.out.println("please enter y or n.");
                            discount = scanner.next();
                        } else break;
                    }
                    if (discount.equals("y"))
                        dis = true;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println(orderManager.addItem(newOrderId, item_id, quantity, dis));
            try {
                if (supplierManager.suppliedItemsAmount(sup) == orderManager.productsInOrderAmount(newOrderId)) {
                    System.out.println("No more items to add. Back to menu..");
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("add another item? (y/n):");
            while (true) {
                try {
                    moreItems = scanner.next();
                    if (!moreItems.equals("y") && !moreItems.equals("n"))
                        System.out.println("Invalid input. Please enter y/n.");
                    else
                        break;
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter y/n.");
                }
            }
        }
        System.out.println("Back to menu...");
        return;
    }

    /**
     * Handles searching for supplier orders based on various parameters.
     * Allows the user to search by order ID, supplier ID, item ID, item supplier ID, or date range.
     */
    private void choice2() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("by which parameter would you like to search?:\n1. Order id\n2. supplier's id\n" +
                "3. Item's system-id\n4. item's supplier-id\n5. dates\n6. Back to menu");
        int param;
        while (true) {
            try {
                param = scanner.nextInt();
                if (param < 1 || param > 6) {
                    System.out.println("Please enter a number between 1 and 6.");
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid Input. Please enter a number between 1 and 6.");
                scanner.nextLine();
            }
        }
        if (param == 1) {
            param1();
        }
        if (param == 2) {
            param2();
        }
        if (param == 3) {
            param3();
        }
        if (param == 4) {
            param4();
        }
        if (param == 5) {
            param5();
        }
        if (param == 6) {
            System.out.println("Back to menu..");
        }
    }

    /**
     * Searches for an order by order ID and displays the result.
     */
    private void param1() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter order's id: ");
        int o_id;
        while (true) {
            try {
                o_id = scanner.nextInt();
                Order order;
                try {
                    order = orderManager.showOrderById(o_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    return;
                }
                if (order == null)
                    System.out.println("No order by this id.");
                else
                    System.out.println(order);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter order's id:  ");
            }
        }
    }

    /**
     * Searches for orders by supplier ID and displays the results.
     */
    private void param2() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter supplier's id: ");
        int s_id;
        while (true) {
            try {
                s_id = scanner.nextInt();
                Collection<Order> orders;
                try {
                    orders = orderManager.findOrdersBySupplier(s_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    return;
                }
                if (orders.isEmpty())
                    System.out.println("No orders with this supplier.");
                else
                    for (Order order : orders)
                        System.out.println(order);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter supplier's id:  ");
            }
        }
    }

    /**
     * Searches for orders containing a specific system item ID and displays the results.
     */
    private void param3() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter item's system-id: ");
        int IS_id;
        while (true) {
            try {
                IS_id = scanner.nextInt();
                Collection<Order> orders;
                try {
                    orders = orderManager.findOrdersByItemSysId(IS_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    return;
                }
                if (orders.isEmpty())
                    System.out.println("No orders with this item.");
                else
                    for (Order order : orders)
                        System.out.println(order);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter item's system-id:  ");
            }
        }
    }

    /**
     * Searches for orders containing items supplied by a specific supplier and displays the results.
     */
    private void param4() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter item's supplier-id: ");
        String iSup_id;
        while (true) {
            try {
                iSup_id = scanner.next();
                Collection<Order> orders;
                try {
                    orders = orderManager.findOrdersByItemSupId(iSup_id);
                } catch (Exception e) {
                    System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                    return;
                }
                if (orders.isEmpty())
                    System.out.println("No orders with this item.");
                else
                    for (Order order : orders)
                        System.out.println(order);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter item's supplier-id:  ");
            }
        }
    }

    /**
     * Searches for orders placed between two given dates and displays the results.
     */
    private void param5() {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                System.out.println("Please enter Initial date in (dd/mm/yyyy) format: ");
                String startInput = scanner.nextLine();
                LocalDate startDate = LocalDate.parse(startInput, formatter);

                System.out.print("Please enter final date in (dd/mm/yyyy) format: ");
                String endInput = scanner.nextLine();
                LocalDate endDate = LocalDate.parse(endInput, formatter);

                if (endDate.isBefore(startDate)) {
                    System.out.println("final date must be after initial date");
                } else {
                    Collection<Order> orders;
                    try {
                        orders = orderManager.findOrdersByDates(startDate, endDate);
                    } catch (Exception e) {
                        System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
                        return;
                    }
                    if (orders.isEmpty())
                        System.out.println("No orders by these dates.");
                    else
                        for (Order order : orders)
                            System.out.println(order);
                    break;
                }
            } catch (Exception e) {
                System.out.println("invalid input.");
            }
        }
    }

    /**
     * Handles editing an existing order.
     * Allows the user to add items, change the supplier, change the order status, or remove items.
     */
    private void choice3() {
        Scanner scanner = new Scanner(System.in);
        int o_id;
        while (true) {
            System.out.println("Please enter order's id: ");
            try {
                o_id = scanner.nextInt();
                if (orderManager.showOrderById(o_id) == null) {
                    System.out.println("There is no order by this id in the system.\nBack to menu..");
                    return;
                } else
                    break;
            } catch (Exception e) {
                System.out.println("invalid input.");
            }
        }
        OrderStatus curr_status;
        try {
            curr_status = orderManager.currentStatus(o_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        if (curr_status.equals(OrderStatus.DONE)) {
            System.out.println("Order's status is 'done', therefore cannot be changed. \nBack to menu...\n");
            return;
        }
        try {
            if (orderManager.getDeliveryDate(o_id).equals(LocalDate.now())) {
                System.out.println("Order delivery day is due today. Order cannot be edited.\nBack to menu...\n");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Please choose edit option:\n1. Add item\n2. Change supplier\n3. Change status\n4.Delete item");
        int opt;
        while (true) {
            try {
                opt = scanner.nextInt();
                if (opt < 1 || opt > 4) {
                    System.out.println("Please enter a number between 1 and 4.");
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid Input. Please enter a number between 1 and 4.");
            }
        }
        //add item
        if (opt == 1) {
            try {
                opt1(o_id, orderManager.supplierIdByOrder(o_id));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        //change supplier
        if (opt == 2) {
            opt2(o_id);
        }
        //change status
        if (opt == 3) {
            opt3(o_id);
        }
        //delete item
        if (opt == 4) {
            opt4(o_id);
        }
    }

    /**
     * Adds a new item to an existing order, or updates the quantity if the item already exists.
     *
     * @param order_id Order ID.
     */
    private void opt1(int order_id, int supplier_id) {
        try {
            if (supplierManager.suppliedItemsAmount(supplier_id) == orderManager.productsInOrderAmount(order_id)) {
                System.out.println("No items to add. Back to menu..");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        int item_id;
        while (true) {
            System.out.println("Please enter item's system-id: ");
            try {
                item_id = scanner.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Invalid Input.");
            }
        }
        try {
            if (!supplierManager.supplierProvidesProduct(supplier_id, item_id)) {
                System.out.println("Supplier does not supply this item. Back to menu...");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int quantity;
        //search if item is already in order
        boolean check;
        try {
            check = orderManager.checkItem(order_id, item_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        if (check) {
            System.out.println("Item is already ordered. would you like to change quantity? (y/n)");
            while (true) {
                String answer = scanner.next();
                if (answer.equals("y")) {
                    System.out.println("Enter new quantity: ");
                    while (true) {
                        try {
                            quantity = scanner.nextInt();
                            if (quantity < 1) {
                                System.out.println("Invalid input. Please enter a positive integer");
                            } else
                                break;
                        } catch (Exception e) {
                            System.out.println("Invalid input. Please enter a positive integer");
                        }
                    }
                    System.out.println(orderManager.changeItemQuantity(order_id, item_id, quantity));
                    return;
                } else if (answer.equals("n")) {
                    return;
                } else
                    System.out.println("Invalid input. Please enter (y/n)\n");
            }
        }
        System.out.println("Enter quantity: ");
        while (true) {
            try {
                quantity = scanner.nextInt();
                if (quantity < 1) {
                    System.out.println("Invalid input. Please enter a positive integer");
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer");
            }
        }
        System.out.println("\nuse discount? (y/n): ");
        while (true) {
            String discount = scanner.next();
            if (discount.equals("y")) {
                System.out.println(orderManager.addItem(order_id, item_id, quantity, true));
                break;
            } else if (discount.equals("n")) {
                System.out.println(orderManager.addItem(order_id, item_id, quantity, false));
                break;
            } else
                System.out.println("Invalid input. Please enter y/n.");
        }
    }

    /**
     * Changes the supplier of an existing order.
     * Warns the user if items cannot be supplied by the new supplier.
     *
     * @param order_id Order ID.
     */
    private void opt2(int order_id) {
        Scanner scanner = new Scanner(System.in);
        int s_id;
        while (true) {
            System.out.println("Please enter supplier's id: ");
            try {
                s_id = scanner.nextInt();
                if (!supplierManager.supplierExists(s_id)) {
                    System.out.println("Supplier does not exist in system. Back to menu...");
                    return;
                } else if (supplierManager.getSupplierStatus(s_id).equals(SupplierStatus.INACTIVE)) {
                    System.out.println("Supplier is not active. Back to menu...");
                    return;
                } else
                    break;
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
        Collection<OrderProduct> notSupplied;
        try {
            notSupplied = orderManager.canNotSupply(order_id, s_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        if (!notSupplied.isEmpty()) {
            System.out.println("The following items in order cannot be supplied be given supplier:");
            for (OrderProduct item : notSupplied)
                System.out.println(item);
            System.out.println("Are you sure you want to change supplier? items that are not supplied will be deleted. (y/n)");
            String answer = scanner.next();
            if (answer.equals("n")) {
                System.out.println("Supplier not changed");
                return;
            }
        }
        System.out.println(orderManager.changeSupplier(order_id, s_id));
    }

    /**
     * Changes the status of an existing order.
     * Offers the user valid status transitions based on the current order status.
     *
     * @param order_id Order ID.
     */
    private void opt3(int order_id) {
        Scanner scanner = new Scanner(System.in);
        OrderStatus currStatus;
        try {
            currStatus = orderManager.currentStatus(order_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        System.out.println("Order's current status is: " + currStatus + ".\nPlease choose new status:");
        int counter = 1;
        List<OrderStatus> options = new ArrayList<>();
        for (OrderStatus status : OrderStatus.values()) {
            if (!status.equals(currStatus)) {
                System.out.println(counter + ". " + status);
                options.add(status);
                counter++;
            }
        }
        System.out.println("3. do not change status");
        while (true) {
            try {
                int newS;
                newS = scanner.nextInt();
                if (newS < 1 || newS > 3) {
                    System.out.println("Please enter a number between 1 and 3.");
                } else {
                    if (newS == 1) {
                        System.out.println(orderManager.changeStatus(order_id, options.get(0)));
                        return;
                    }
                    if (newS == 2) {
                        System.out.println(orderManager.changeStatus(order_id, options.get(1)));
                        return;
                    }
                    if (newS == 3) {
                        System.out.println("Status not changed. Back to menu..");
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid Input. Please enter a number between 1 and 3.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Removes an item from an existing order after user confirmation.
     *
     * @param order_id Order ID.
     */
    private void opt4(int order_id) {
        Scanner scanner = new Scanner(System.in);
        int s_id;
        while (true) {
            System.out.println("Please enter Item's System-id: ");
            try {
                s_id = scanner.nextInt();
                break;

            } catch (Exception e) {
                System.out.println("Invalid input. ");
            }
        }
        boolean check;
        try {
            check = orderManager.checkItem(order_id, s_id);
        } catch (Exception e) {
            System.out.println("A problem occurred. " + e.getMessage() + "\nBack to menu...\n");
            return;
        }
        if (!check) {
            System.out.println("Item is not in order.");
            return;
        }
        System.out.println("Are you sure you want to delete this item? (y/n): ");
        String answer = scanner.next();
        if (answer.equals("y")) {
            System.out.println(orderManager.removeItem(order_id, s_id));
        } else {
            System.out.println("Item was not removed");
        }
    }

    private void choice4() {
        try {
            for (Order order : orderManager.getAllOrders())
                System.out.println(order);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
