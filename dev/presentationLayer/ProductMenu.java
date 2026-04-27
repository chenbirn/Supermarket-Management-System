package presentationLayer;

import DTO.ProductDto;
import dataAccessLayer.ItemDaoSQLite;
import dataAccessLayer.ProductDaoSQLite;
import dataAccessLayer.StoreDiscountDaoSQLite;
import domainLayer.*;
import enums.PackagingOption;
import enums.UnitType;
import serviceLayer.ProductService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


// menu to control the store inventory
public class ProductMenu {
    private Scanner scanner;
    private ProductService service;

    // constructor that receives a controller
    public ProductMenu(ProductService service) {
        this.scanner = new Scanner(System.in);
        this.service = service;
    }

    public ProductMenu(Scanner scanner) {
        this.scanner = scanner;

    }

    // function that runs the menu
    public void run() throws SQLException {
        boolean running = true;

        // main menu starts here
        while (running) {
            // start of the menu //
            System.out.println("=== Main Menu ===");
            System.out.println("1. Report Menu");
            System.out.println("2. Product Menu");
            System.out.println("3. Go Out");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            // options in the menu
            switch (choice) {
                case 1 -> ReportMenu();
                case 2 -> this.productmenu();
                case 3 -> {
                    running = false;
                    System.out.println("Bye bye!");
                }
                default -> System.out.println("Invalid option\n");
            }
        }
    }

    //  option 1 - report menu
    private void ReportMenu() throws SQLException {
        System.out.println("--- Report Menu ---");
        System.out.println("1. Inventory Report");
        System.out.println("2. Order Report");
        System.out.println("3. Defective Report");
        System.out.println("4. Go Back");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            // options in the menu
            case 1 -> {
                System.out.println("Enter category names (use commas if you enter more than one):");
                //scanner.nextLine();
                String input = scanner.nextLine().trim();
                service.InventoryReport(input);
            }
            case 2 -> service.orderReport();
            case 3 -> service.DefectiveReport();
            case 4 -> {
            }
            default -> System.out.println("Invalid option.");
        }
        System.out.println();
    }

    // option 2 - product menu
    private void productmenu() throws SQLException {
        System.out.println("--- Product Menu ---");
        System.out.println("1. Crate Discount");
        System.out.println("2. Add new Product to the Market");
        System.out.println("3. Set information about exist product");
        System.out.println("4. Show product ");
        System.out.println("5. Go Back");
        System.out.print("Choose an option: ");

        String choiceStr = scanner.nextLine();
        int choice = Integer.parseInt(choiceStr);
        switch (choice) {
            case 1 -> applyDiscount();
            case 2 -> addProduct();
            case 3 -> editProductMenu();
            case 4 -> ShowProduct();
            case 5 -> {
            }
            default -> System.out.println("Invalid option");
        }
        System.out.println();
    }
    // option 4 - show product
    private void ShowProduct() throws SQLException {
        System.out.print("Enter product ID: ");

        String ID = scanner.nextLine();
        int id = Integer.parseInt(ID);
        //scanner.nextLine();
        service.printProductDetails(id);
    }
    // option 2 - add product
    private void addProduct() throws SQLException {

        System.out.println("Which category add this product?");
        String name = scanner.nextLine();
        System.out.println("Which  sub category add this product?");
        String sub = scanner.nextLine();

        System.out.println("Enter Unit Type (KG / LITERS / UNITS):");
        String input = scanner.nextLine().trim().toUpperCase();

        UnitType unitType = UnitType.valueOf(input);

        double ml;
        if (unitType == UnitType.LITERS) {
            System.out.println("Enter size (in ml):");
            ml = scanner.nextDouble();
            scanner.nextLine(); // ניקוי ה־newline
        } else {
            ml = 0;
        }

        System.out.println("Enter weight (in kg):");
        double weight = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Which size category add this product?");
        String size = scanner.nextLine();


        System.out.println("Enter product ID:");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter product name:");
        String productName = scanner.nextLine();

        System.out.println("Enter manufacturer name:");
        String manufacturer = scanner.nextLine();

        System.out.println("Enter sale price:");
        double salePrice = scanner.nextDouble();
        scanner.nextLine();


        System.out.println("Enter minimum quantity:");
        int minQty = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Frequency (in weeks) for periodic order :");
        int frequency = scanner.nextInt();
        scanner.nextLine();


        System.out.println("Enter Packaging Option (single units / box only / both options):");
        String packagingOption_ = scanner.nextLine().trim().toLowerCase();

        PackagingOption packagingOption;

        if (packagingOption_.equals("single units")) {
            packagingOption = PackagingOption.SINGLE_UNITS;
        } else if (packagingOption_.equals("box only")) {
            packagingOption = PackagingOption.BOX_ONLY;
        } else if (packagingOption_.equals("both options")) {
            packagingOption = PackagingOption.BOTH;
        } else {
            throw new IllegalArgumentException("Invalid packaging option: " + packagingOption_);
        }

        int boxUnit;
        if (packagingOption == PackagingOption.BOX_ONLY || packagingOption == PackagingOption.BOTH) {
            System.out.println("Enter how many units come in a box:");
            boxUnit = scanner.nextInt();
            scanner.nextLine(); // ניקוי newline
        } else {
            boxUnit = 1;
        }
        // creating the product
        ProductDto dto = new ProductDto(id, manufacturer, productName, salePrice, salePrice, weight, ml, minQty, -1, frequency, null, null, packagingOption, unitType, boxUnit, name, sub, size); //לכניס את הפרטים מהמשתמש וליצור DTO
        service.addProduct(dto);

    }

    //  option 3 - mini menu to edit a product
    private void editProductMenu() throws SQLException {
        System.out.println("--- Edit Product ---");
        System.out.print("Enter product ID: ");
        String ID = scanner.nextLine();
        int id = Integer.parseInt(ID);

        System.out.println("1. Change Price");
        System.out.println("2. Add new items from the supplier_id");
        System.out.println("3. Report an item is defective");
        System.out.println("4. Removing items from a product");
        System.out.println("5. Move items from warehouse to shelf");
        System.out.println("6. change the weight of product");
        System.out.println("7. change quentity of box unit");
        System.out.println("8. change packaging Option");
        System.out.println("9. Go Back");

        System.out.print("Choose an option: ");
        String choiceStr = scanner.nextLine();

        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice < 1 || choice > 9) {
                System.out.println("Invalid option. Please choose a number between 1 and 8.");
                return;
            }
            switch (choice) {
                // changing the price
                case 1 -> {
                    System.out.print("Enter the new price: ");
                    double price = Double.parseDouble(scanner.nextLine());
                    if (service.updateProductPrice(id, price)) {
                        System.out.println("Price updated successfully!");
                    } else {
                        System.out.println("Product not found.");
                    }
                }
                // adding items
                case 2 -> {
                    System.out.print("How much items you want to add? : ");
                    int count = Integer.parseInt(scanner.nextLine());

                    System.out.print("Enter EXP date (yyyy-MM-dd): ");
                    LocalDate expDate = LocalDate.parse(scanner.nextLine());

                    System.out.print("Enter Buy Price for the items: ");
                    double buyPrice = Double.parseDouble(scanner.nextLine());

                    if (service.addItemsToProduct(id, count, buyPrice, expDate)) {
                        System.out.println(count + " items added successfully!");
                    }
                    else {
                        System.out.println("There is no supplier for this product.");
                    }
                // reporting item as defective
                }
                case 3 -> {
                    System.out.print("What is the item Id :");
                    int itemId = Integer.parseInt(scanner.nextLine());
                    if (service.reportItemAsDefective(id, itemId)) {
                        System.out.println("Item reported as defective successfully!");
                    } else {
                        System.out.println("Product or item not found.");
                    }
                }
                // removing items from product
                case 4 -> {
                    System.out.print("How many items do you want to remove? ");
                    int removeCount = Integer.parseInt(scanner.nextLine());

                    if (service.removeOldestItemsFromProduct(id, removeCount)) {
                        System.out.println("Items removed successfully!");
                    } else {
                        System.out.println("Not enough items to remove.");
                    }
                }
                // moving items from warehouse to shelf
                case 5 -> {
                    System.out.print("How many items do you want to move from warehouse to shelf? ");
                    int moveCount = Integer.parseInt(scanner.nextLine());
                    if (service.moveItemsFromWarehouseToShelf(id, moveCount)) {
                        System.out.println("Items moved successfully!");
                    } else {
                        System.out.println("Not enough items in warehouse.");
                    }
                }
                // changing the weight of a product
                case 6 -> {
                    System.out.print("Enter new weight: ");
                    double weight = scanner.nextDouble();

                    boolean success = service.updateProductWeight(id, weight);
                    if (success) {
                        System.out.println("Product weight updated successfully.");
                    } else {
                        System.out.println("Product not found.");
                    }
                }
                //changing the quantity of a box unit
                case 7 -> {
                    if (service.findProductById(id).getPackagingOption() != PackagingOption.SINGLE_UNITS) {
                        System.out.print("Enter new box units value: ");
                        int newBoxUnits = scanner.nextInt();

                        boolean updated = service.updateProductBoxUnits(id, newBoxUnits);
                        if (updated) {
                            System.out.println("Box units updated successfully.");
                        } else {
                            System.out.println("Product not found.");
                        }
                    } else {///////////////////////////////////////////////////////////////
                        System.out.println("the Product comes in SINGLE UNITS");
                    }
                }
                // changing the packaging option
                case 8 -> {
                    System.out.print("Enter new Packaging Option (single units / box only / both options): ");
                    String packagingOption_ = scanner.nextLine().trim().toLowerCase();
                    service.updatePackageType(id, packagingOption_);
                }
                // return
                case 9 -> System.out.println("Returning to product menu.");
                default -> System.out.println("Invalid option");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number only.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    // option 1 - create new discount
    public void applyDiscount() throws SQLException {
        System.out.print("Do you want to apply discount on a 'category' or a 'product'? ");
        String choice = scanner.nextLine().trim().toLowerCase();
        //scanner.nextLine();

        System.out.print("Enter discount percentage (e.g. 10 for 10%): ");
        double discountPercent = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter discount start date (yyyy-MM-dd): ");
        String startStr = scanner.nextLine();
        System.out.print("Enter discount end date (yyyy-MM-dd): ");
        String endStr = scanner.nextLine();

        System.out.print("Enter discount ID: ");
        String Discount = scanner.nextLine();
        int DiscountID = Integer.parseInt(Discount);

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr);
            endDate = LocalDate.parse(endStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }


        if (discountPercent <= 0 || discountPercent >= 100 || endDate.isBefore(startDate)) {
            System.out.println("Invalid discount value. Must be between 0 and 100.");
            return;
        }


        switch (choice) {
            case "category" -> {
                System.out.print("Enter main Category name or null if the discount is for sub/size category: ");
                String mainCategory = scanner.nextLine().trim();
                if (mainCategory.equals("null")) {
                    mainCategory = null;
                }

                System.out.print("Enter sub Category name or null if the discount is for size category:: ");
                String subCategory = scanner.nextLine().trim();
                if (subCategory.equals("null")) {
                    subCategory = null;
                }

                System.out.print("Enter size Category name or null if the discount is for all sizes: ");
                String sizeCategory = scanner.nextLine().trim();
                if (sizeCategory.equals("null")) {
                    sizeCategory = null;
                }

                boolean success = service.applyDiscountToCategory(DiscountID, mainCategory, subCategory, sizeCategory, discountPercent, startDate, endDate);
                if (success) {
                    System.out.println("Discount added to  " + mainCategory + " -> " + subCategory + " -> " + sizeCategory);
                }
            }
            case "product" -> {
                System.out.print("Enter product ID: ");
                int productId = scanner.nextInt();
                scanner.nextLine();

                boolean success = service.applyDiscountToProduct(DiscountID, productId, discountPercent, startDate, endDate);

                if (success) {
                    System.out.println("Discount added to product ID: " + productId);
                }
            }
            default -> System.out.println(choice + "Invalid choice. Please enter 'category' or 'product'.");
        }
    }
}




