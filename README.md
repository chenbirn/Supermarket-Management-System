# Supermarket Management System

A Java-based system for managing suppliers, inventory, and orders in a supermarket environment.  
The project was developed as part of a Software Analysis and Design course.

## 📌 Overview
This system simulates the backend logic of a supermarket, including supplier management, product inventory, and order processing.  
It follows a layered architecture and emphasizes clean design principles and separation of concerns.

## 🏗️ Architecture
The system is built using a multi-layered architecture:

- **Presentation Layer** – Handles user interaction (menus, CLI)
- **Service Layer** – Business logic and application flow
- **Domain Layer** – Core entities and logic (Order, Supplier, Product, etc.)
- **Data Access Layer (DAO)** – Handles database operations
- **DTO Layer** – Transfers data between layers

## ✨ Features
- Manage suppliers and their agreements
- Handle different types of orders (regular, periodic, shortage)
- Inventory and product management
- Discount handling (by quantity / store discounts)
- Separation between business logic and data access
- SQLite database integration

## 🛠️ Technologies
- Java
- SQLite
- DAO / DTO design patterns
- Object-Oriented Programming (OOP)
