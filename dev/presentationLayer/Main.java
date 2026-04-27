package presentationLayer;

import dataAccessLayer.*;
import domainLayer.*;
import enums.*;
import serviceLayer.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            boolean useSampleData = false;
            String dbPath = "";

            System.out.println("Welcome to the System Init!");
            System.out.println("Select data load option:");
            System.out.println("1. Load sample data");
            System.out.println("2. Run with empty database");
            System.out.println("3. Exit");

            int choice = 0;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 3) {
                        System.out.println("Invalid option. Please choose number between 1-3.");
                    } else {
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            if (choice == 3) {
                System.out.println("Bye Bye!");
                return;
            }

            useSampleData = (choice == 1);
            dbPath = useSampleData ? "inventory.db" : "inventory_test.db";

            // Connect to database
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            conn.setAutoCommit(true);

            // Create tables if not exist
            DatabaseInitializer.createTablesIfNeeded(conn);
            System.out.println("Database connected and tables created!");

            // Clear all data if running empty
            if (!useSampleData) {
                DatabaseInitializer.clearAllData(conn);
            }

            // Insert sample data if selected
            if (useSampleData) {
                DatabaseInitializer.insertSampleData(conn);
                System.out.println("Sample data inserted successfully.");
            }

            // Create DAOs
            StoreDiscountDaoSQLite storeDiscountDao = new StoreDiscountDaoSQLite(conn);
            ProductDaoSQLite productDao = new ProductDaoSQLite(conn);
            ItemDaoSQLite itemDao = new ItemDaoSQLite(conn);
            OrderDaoSQLite orderDao = new OrderDaoSQLite(conn);
            OrderProductDAOSQLite orderProductDao = new OrderProductDAOSQLite(conn);
            SupplierDaoSQLite supplierDao = new SupplierDaoSQLite(conn);
            AgreementDaoSQLite agreementDao = new AgreementDaoSQLite(conn);
            AgreementProductDaoSQLite agreementProductDao = new AgreementProductDaoSQLite(conn);
            DiscountByQuantityDao discountByQuantityDao = new DiscountByQuantityDaoSQLite(conn);

            // Create repositories
            ICategoryRepository categoryRepo = new CategoryRepository(storeDiscountDao);
            IProductRepository productRepo = new ProductRepository(categoryRepo, productDao, itemDao, storeDiscountDao);
            ISupplierRepository supplierRepo = new SupplierRepository(supplierDao);
            IAgreementRepository agreementRepo = new AgreementRepository(
                    agreementDao, agreementProductDao, discountByQuantityDao, supplierRepo, productRepo);
            IOrderRepository orderRepo = new OrderRepository(
                    supplierRepo, productRepo, orderDao, supplierDao, orderProductDao, agreementProductDao);

            // Create controllers
            Controller inventoryController = new Controller(categoryRepo, productRepo);
            SupplierController supplierController = new SupplierController(supplierRepo, orderRepo, agreementRepo);
            MainController mainController = new MainController(inventoryController, supplierController);

            // Create services
            OrderService orderService = new OrderService(mainController);
            SupplierService supplierService = new SupplierService(mainController);
            AgreementService agreementService = new AgreementService(mainController);
            ProductService productService = new ProductService(mainController);

            // Create menus
            ProductMenu productMenu = new ProductMenu(productService);
            OrderServiceMenu orderMenu = new OrderServiceMenu(orderService, supplierService);
            SupplyServiceMenu supplyMenu = new SupplyServiceMenu(supplierService);
            AgreementServiceMenu agreementMenu = new AgreementServiceMenu(agreementService, supplierService, productService);

            // Start the main system menu
            SupplierInventoryManager manager = new SupplierInventoryManager(
                    mainController,
                    productMenu,
                    orderMenu,
                    supplyMenu,
                    agreementMenu);

            manager.menu();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
