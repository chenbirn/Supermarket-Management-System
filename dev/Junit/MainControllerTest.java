import DTO.*;
import dataAccessLayer.*;
import domainLayer.*;
import enums.*;
import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeAll;
import serviceLayer.AgreementService;
import serviceLayer.OrderService;
import serviceLayer.ProductService;
import serviceLayer.SupplierService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;

//import static org.junit.Assert.*;

public class MainControllerTest {

    private static MainController mainController;
    private static ProductService productService;
    private static OrderService orderService;
    private static AgreementService agreementService;
    private static SupplierService supplierService;
    private static ProductDto productDto1;
    private static ProductDto productDto2;
    private static ProductDto productDto3;
    private static OrderProductDTO orderProduct1;
    private static OrderDTO periodicOrder;
    private static OrderDaoSQLite orderDao;
    private static OrderProductDAOSQLite orderProductDao;
    private static DiscountByQuantityDao discountByQuantityDao;
    private static AgreementProductDaoSQLite agreementProductDao;
    private static ItemDaoSQLite itemDao;
    private static StoreDiscountDaoSQLite storeDiscountDao;
    private static ProductDaoSQLite productDao;
    private static AgreementDaoSQLite agreementDao;
    private static SupplierDaoSQLite supplierDao;
    private static SupplierDTO supplier1;
    private static SupplierDTO supplier2;
    private static AgreementDTO agreement1;
    private static AgreementDTO agreement2;
    private static AgreementProductDTO agreementProduct1;
    private static AgreementProductDTO agreementProduct2;
    private static DiscountByQuantityDTO discount1;
    private static ItemDTO item1;
    private static ItemDTO item2;
    private static CategoryRepository categoryRepo;
    private static ProductRepository productRepo;
    private static AgreementRepository agreementRepo;
    private static SupplierRepository supplierRepo;
    private static OrderRepository orderRepo;


    @BeforeAll
    public static void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory_test.db");
        conn.setAutoCommit(true);

        // Create tables if not exist
        DatabaseInitializer.createTablesIfNeeded(conn);

        // Create DAOs
        storeDiscountDao = new StoreDiscountDaoSQLite(conn);
        itemDao = new ItemDaoSQLite(conn);
        orderDao = new OrderDaoSQLite(conn);
        orderProductDao = new OrderProductDAOSQLite(conn);
        supplierDao = new SupplierDaoSQLite(conn);
        agreementDao = new AgreementDaoSQLite(conn);
        agreementProductDao = new AgreementProductDaoSQLite(conn);
        discountByQuantityDao = new DiscountByQuantityDaoSQLite(conn);
        productDao = new ProductDaoSQLite(conn);

        // Create repositories
        categoryRepo = new CategoryRepository(storeDiscountDao);
        productRepo = new ProductRepository(categoryRepo, productDao, itemDao, storeDiscountDao);
        supplierRepo = new SupplierRepository(supplierDao);
        agreementRepo = new AgreementRepository(
                agreementDao, agreementProductDao, discountByQuantityDao, supplierRepo, productRepo);
        orderRepo = new OrderRepository(supplierRepo, productRepo, orderDao, supplierDao, orderProductDao, agreementProductDao);
        // Create controllers
        Controller inventoryController = new Controller(categoryRepo, productRepo);
        SupplierController supplierController = new SupplierController(supplierRepo, orderRepo, agreementRepo);
        mainController = new MainController(inventoryController, supplierController);

        // Create services
        orderService = new OrderService(mainController);
        supplierService = new SupplierService(mainController);
        agreementService = new AgreementService(mainController);
        productService = new ProductService(mainController);

        productDto1 = new ProductDto(111, "tnuva", "milk", 20, 20, 3,
                2, 2, 1, 1, new ArrayList<>(), new ArrayList<>(), PackagingOption.BOX_ONLY,
                UnitType.LITERS, 21, "Dairy", "Milk", "2");
        productDto2 = new ProductDto(112, "Angel", "Sliced Bread", 15, 15, 1,
                1, 3, 5, 10, new ArrayList<>(), new ArrayList<>(), PackagingOption.BOTH,
                UnitType.UNITS, 22, "Bakery", "Bread", "1");
        productDto3 = new ProductDto(113, "Elite", "Milk Chocolate", 8.5, 8.5, 0.1,
                0.1, 30, -1, 5, new ArrayList<>(), new ArrayList<>(), PackagingOption.SINGLE_UNITS,
                UnitType.KG, 23, "Snacks", "Chocolate", "100");

        supplier1 = new SupplierDTO(
                201,
                "Tnuva",
                "1 Dairy Street, Tel Aviv",
                EnumSet.of(PaymentType.CASH, PaymentType.BANK_TRANSFER),
                123456,
                SupplierStatus.ACTIVE,
                "Dana Levi",
                "050-1234567",
                "Itay Cohen",
                "052-9876543"
        );

        supplier2 = new SupplierDTO(
                202,
                "Strauss",
                "22 Chocolate Ave, Haifa",
                EnumSet.of(PaymentType.CREDIT_CARD),
                654321,
                SupplierStatus.ACTIVE,
                "Maya Levi",
                "054-1112222",
                "Shai Regev",
                "053-3334444"
        );

        agreement1 = new AgreementDTO(
                301,
                201,
                List.of(DeliveryDays.SUNDAY, DeliveryDays.WEDNESDAY),
                DeliveryMethod.SELF_PICKUP,
                AgreementStatus.ACTIVE
        );

        agreement2 = new AgreementDTO(
                302,
                202,
                List.of(DeliveryDays.MONDAY, DeliveryDays.THURSDAY),
                DeliveryMethod.DELIVERY,
                AgreementStatus.ACTIVE
        );

        agreementProduct1 = new AgreementProductDTO(
                "201-111",
                111,
                301,
                16.0
        );

        //cheaper supplier for product 111
        agreementProduct2 = new AgreementProductDTO(
                "202-111",
                111,
                302,
                18.0
        );

        discount1 = new DiscountByQuantityDTO(
                "202-111",
                0.10,
                10,
                DiscountMethod.PERCENTAGE
        );


        AgreementProductDTO agreementProduct3 = new AgreementProductDTO(
                "202-112",
                112,
                302,
                20.0
        );

        item1 = new ItemDTO(
                1000,
                17.8,
                LocalDate.of(2025, 7, 1),
                DefectiveStatus.GoodCondition,
                location.Warehouse,
                111
        );

        item2 = new ItemDTO(
                1002,
                13.0,
                LocalDate.of(2025, 6, 10),
                DefectiveStatus.Defective,
                location.Warehouse,
                111
        );

        periodicOrder = new OrderDTO(
                5001,
                LocalDate.now().minusDays(7),
                "050-1234567",
                144.0,
                OrderStatus.IN_PROCESS,
                201,
                LocalDate.of(2025, 6, 5),
                1,
                OrderType.PERIODIC
        );

        orderProduct1 = new OrderProductDTO(
                10,
                16.0,
                true,
                0.10,
                144.0,
                5001,
                "201-111"
        );

    }

    @Test
    public void CreateProduct() {
        try {
            productService.addProduct(productDto1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(productDao.findById(111), productDto1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void CreateSupplier() {
        try {
            supplierService.createNewSupplier(supplier1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(supplierDao.findById(201), supplier1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            supplierService.createNewSupplier(supplier2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void SaveAgreement() {
        try {
            agreementDao.save(agreement1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(agreementDao.findAgreementById(301), agreement1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            agreementProductDao.save(agreementProduct1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(agreementProductDao.findAllByAgreement(301).getFirst(), agreementProduct1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            agreementDao.save(agreement2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            agreementProductDao.save(agreementProduct2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void CreateItem() {
        try {
            itemDao.save(item1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(itemDao.findById(1000), item1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            itemDao.save(item2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void SaveOrder() {
        try {
            orderDao.save(periodicOrder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(orderDao.findById(5001), periodicOrder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            orderProductDao.save(orderProduct1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assertEquals(orderProductDao.findByOrderId(5001).getFirst(), orderProduct1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void PeriodicOrderReCreation() throws Exception {
        CreateProduct();
        CreateSupplier();
        CreateItem();
        SaveAgreement();
        SaveOrder();
        productRepo.loadAllFromDB();
        supplierRepo.loadAllFromDB();
        orderRepo.loadAllFromDB();
        agreementRepo.loadAllFromDB();
        List<PeriodicOrder> orders = mainController.checkAllPeriodicOrders();
        assertFalse(orders.isEmpty(), "Expected at least one periodic order, but list was empty");
    }

    @Test
    public void ShortageOrderCreation() {
        try {
            CreateProduct();
            CreateSupplier();
            CreateItem();
            SaveAgreement();
            SaveOrder();
            productRepo.loadAllFromDB();
            supplierRepo.loadAllFromDB();
            orderRepo.loadAllFromDB();
            agreementRepo.loadAllFromDB();
            mainController.checkLowStockAlert();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void DefectiveItems() {
        try {
            CreateProduct();
            CreateSupplier();
            CreateItem();
            SaveAgreement();
            SaveOrder();
            productRepo.loadAllFromDB();
            supplierRepo.loadAllFromDB();
            orderRepo.loadAllFromDB();
            agreementRepo.loadAllFromDB();
            mainController.DefectiveReport();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void ProductDtoToEntity() {
        assertInstanceOf(Product.class, ProductMapper.toEntity(productDto2));
    }

    @Test
    public void SupplierDtoToEntity() {
        assertInstanceOf(Supplier.class, SupplierMapper.toEntity(supplier2));
    }
}
