package dataAccessLayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTablesIfNeeded(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY,
                    manufacturer TEXT,
                    name TEXT,
                    real_price REAL,
                    sale_price REAL,
                    weight REAL,
                    size_ml REAL,
                    min_quantity INTEGER,
                    curr_quantity INTEGER,
                    packaging_option TEXT CHECK(packaging_option IN ('SINGLE_UNITS', 'BOX_ONLY', 'BOTH')),
                    unit_type TEXT CHECK(unit_type IN ('KG', 'LITERS', 'UNITS')),
                    box_units INTEGER,
                    product_order_status TEXT CHECK(product_order_status IN ('ORDERED', 'NOT_ORDERED')),
                    frequency INTEGER,
                    main_category_name TEXT,
                    sub_category_name TEXT,
                    size_category_name TEXT
                );

                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY,
                    buy_price REAL,
                    expiration_date TEXT,
                    status TEXT,
                    location TEXT,
                    product_id INTEGER,
                    FOREIGN KEY(product_id) REFERENCES products(id)
                );

                CREATE TABLE IF NOT EXISTS store_discounts (
                    id INTEGER PRIMARY KEY,
                    product_id INTEGER,
                    main_category_name TEXT,
                    sub_category_name TEXT,
                    size_category_name TEXT,
                    percent_discount REAL,
                    category_or_product TEXT CHECK(category_or_product IN ('product', 'category')),
                    start_date TEXT,
                    end_date TEXT,
                    FOREIGN KEY(product_id) REFERENCES products(id)
                );

                CREATE TABLE IF NOT EXISTS Agreements (
                    agreement_id INTEGER PRIMARY KEY,
                    supplier_id INTEGER NOT NULL,
                    delivery_method TEXT NOT NULL CHECK (delivery_method IN ('DELIVERY', 'SELF_PICKUP')),
                    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id)
                );
                
                CREATE TABLE IF NOT EXISTS Agreement_DeliveryDays (
                    agreement_id INTEGER,
                    day TEXT NOT NULL CHECK (day IN ('SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'BY_ORDER')),
                    PRIMARY KEY (agreement_id, day),
                    FOREIGN KEY(agreement_id) REFERENCES Agreements(agreement_id)
                );
                
                CREATE TABLE IF NOT EXISTS AgreementProducts (
                    supplyItem_id TEXT PRIMARY KEY,
                    agreement_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY (agreement_id) REFERENCES Agreements(agreement_id),
                    FOREIGN KEY(product_id) REFERENCES products(id)
                );
                
                CREATE TABLE IF NOT EXISTS DiscountByQuantity (
                    supplyItem_id TEXT PRIMARY KEY,
                    discount REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    Dtype TEXT NOT NULL CHECK (Dtype IN ('PERCENTAGE', 'AMOUNT')),
                    FOREIGN KEY (supplyItem_id) REFERENCES AgreementProducts(supplyItem_id)
                );
                
                CREATE TABLE IF NOT EXISTS suppliers (
                    supplier_id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    address TEXT NOT NULL,
                    payment_type TEXT NOT NULL,
                    bank_account INTEGER,
                    status TEXT NOT NULL CHECK(status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')),
                    contact_name1 TEXT NOT NULL,
                    contact_phone1 TEXT NOT NULL,
                    contact_name2 TEXT,
                    contact_phone2 TEXT
                );
                
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INTEGER PRIMARY KEY,
                    order_date DATE NOT NULL,
                    delivery_date DATE,
                    contact_num TEXT NOT NULL,
                    total_price REAL NOT NULL,
                    status TEXT NOT NULL,
                    supplier_id INTEGER NOT NULL,
                    order_type TEXT NOT NULL CHECK(order_type IN ('MANUAL', 'PERIODIC', 'SHORTAGE')),
                    frequency INTEGER,
                    FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
                );
                
                CREATE TABLE IF NOT EXISTS order_products (
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    use_discount BOOLEAN NOT NULL,
                    discount REAL NOT NULL,
                    final_price REAL NOT NULL,
                    order_id INTEGER NOT NULL,
                    supplier_product_id TEXT NOT NULL,
                
                    PRIMARY KEY (order_id, supplier_product_id),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id),
                    FOREIGN KEY (supplier_product_id) REFERENCES AgreementProducts(supplyItem_id)
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tables created (if not exist).");
        }
    }

    public static void insertSampleData(Connection conn) {
        String[] insertions = {
                """
        INSERT OR IGNORE INTO suppliers (
            supplier_id, name, address, payment_type, bank_account, status,
            contact_name1, contact_phone1, contact_name2, contact_phone2
        )
        VALUES (
            1, 'Supplier One', '123 Main St', 'CREDIT_CARD', 12345678, 'ACTIVE',
            'Alice', '0501234567', 'Bob', '0507654321'
        );
        """,
                """
        INSERT OR IGNORE INTO suppliers (
            supplier_id, name, address, payment_type, bank_account, status,
            contact_name1, contact_phone1, contact_name2, contact_phone2
        )
        VALUES (
            2, 'Supplier Two', '456 Market Ave', 'BANK_TRANSFER', 87654321, 'ACTIVE',
            'Charlie', '0509876543', 'Dana', '0503456789'
        );
        """,
                """
        INSERT OR IGNORE INTO suppliers (
            supplier_id, name, address, payment_type, bank_account, status,
            contact_name1, contact_phone1, contact_name2, contact_phone2
        )
        VALUES (
            3, 'Supplier Three', '789 Industrial Rd', 'CASH', 11223344, 'ACTIVE',
            'Eve', '0501122334', 'Frank', '0504433221'
        );
        """,

                """
        INSERT OR IGNORE INTO products (id, manufacturer, name, real_price, sale_price, weight, size_ml, min_quantity, curr_quantity,
                                                                  packaging_option, unit_type, box_units, product_order_status, frequency,
                                                                  main_category_name, sub_category_name, size_category_name)
        VALUES (1, 'BrandA', 'Shampoo', 20.0, 18.0, 0.5, 500, 15, 15, 'BOX_ONLY', 'LITERS', 12, 'ORDERED', 1,
                'Hygiene', 'Hair', '1');
        """,
                """
        INSERT OR IGNORE INTO products (id, manufacturer, name, real_price, sale_price, weight, size_ml, min_quantity, curr_quantity,
                                       packaging_option, unit_type, box_units, product_order_status, frequency,
                                       main_category_name, sub_category_name, size_category_name)
        VALUES (2, 'BrandB', 'Soap', 10.0, 9.0, 0.3, 250, 5, 12, 'SINGLE_UNITS', 'UNITS', 6, 'ORDERED', 2,
                'Hygiene', 'Body', '2');
        """,
                """
        INSERT OR IGNORE INTO products (id, manufacturer, name, real_price, sale_price, weight, size_ml, min_quantity, curr_quantity,
                                       packaging_option, unit_type, box_units, product_order_status, frequency,
                                       main_category_name, sub_category_name, size_category_name)
        VALUES (3, 'BrandC', 'Toothpaste', 15.0, 13.0, 0.2, 150, 8, 20, 'BOX_ONLY', 'UNITS', 10, 'ORDERED', 9,
                'Hygiene', 'Teeth', '1');
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (100, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (101, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (102, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (103, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (104, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (105, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (106, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (107, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (108, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (109, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (110, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (111, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (112, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (113, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (114, 18.0, '2025-12-31', 'GoodCondition', 'Shelf', 1);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (115, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (116, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (117, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (118, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (119, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (120, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (121, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (122, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (123, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (124, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (125, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (126, 9.0, '2025-12-31', 'GoodCondition', 'Shelf', 2);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (127, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (128, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (129, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (130, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (131, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (132, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (133, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (134, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (135, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (136, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (137, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (138, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (139, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (140, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (141, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (142, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (143, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (144, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (145, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO items (id, buy_price, expiration_date, status, location, product_id)
        VALUES (146, 13.0, '2025-12-31', 'GoodCondition', 'Shelf', 3);
        """,
                """
        INSERT OR IGNORE INTO store_discounts (id, product_id, main_category_name, sub_category_name, size_category_name,
                                     percent_discount, category_or_product, start_date, end_date)
        VALUES (1, 1, 'Hygiene', 'Hair', '1', 10.0, 'product', '2025-01-01', '2025-12-31');
        """,
                """
        INSERT OR IGNORE INTO Agreements (agreement_id, supplier_id, delivery_method)
        VALUES (1, 1, 'DELIVERY');
      
        """,
                """
        INSERT OR IGNORE INTO Agreements (agreement_id, supplier_id, delivery_method)
        VALUES (2, 2, 'DELIVERY');
        
        """,
                """
        INSERT OR IGNORE INTO Agreement_DeliveryDays (agreement_id, day)
        VALUES (1, 'SUNDAY');
        """,
                """
        INSERT OR IGNORE INTO AgreementProducts (supplyItem_id, agreement_id, product_id, price)
        VALUES ('1-1', 1, 1, 16.0);
        """,
                """
        INSERT OR IGNORE INTO AgreementProducts (supplyItem_id, agreement_id, product_id, price)
        VALUES ('2-1', 2, 1, 10.0);
        """,
                """
        INSERT OR IGNORE INTO DiscountByQuantity (supplyItem_id, discount, quantity, Dtype)
        VALUES ('1-1', 5.0, 10, 'PERCENTAGE');
        """,
                """
        INSERT OR IGNORE INTO orders (order_id, order_date, delivery_date, contact_num, total_price, status, supplier_id, order_type, frequency)
        VALUES (1, '2025-06-02', '2025-06-03', '0501234567', 160.0, 'IN_PROCESS', 1, 'PERIODIC', 1);
        """,
                """
        INSERT OR IGNORE INTO order_products (quantity, price, use_discount, discount, final_price, order_id, supplier_product_id)
        VALUES (10, 16.0, 1, 5.0, 152.0, 1, '1-1');
        """
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : insertions) {
                stmt.executeUpdate(sql);
            }
            System.out.println("Sample data inserted successfully into all tables.");
        } catch (SQLException e) {
            System.out.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Clears all data from the database tables.
    public static void clearAllData(Connection conn) {
        try {
            String[] tables = {
                    "order_products",
                    "orders",
                    "AgreementProducts",
                    "Agreement_DeliveryDays",
                    "Agreements",
                    "DiscountByQuantity",
                    "suppliers",
                    "items",
                    "products",
                    "store_discounts"
            };

            for (String table : tables) {
                if (tableExists(conn, table)) {
                    String sql = "DELETE FROM " + table + ";";
                    conn.createStatement().executeUpdate(sql);
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to clear data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Checks if a given table exists in the database.
    private static boolean tableExists(Connection conn, String tableName) {
        try (var rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }



}
