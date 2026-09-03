
## Project Overview

# Online Grocery Store

## Project Overview

This project is a backend application for an online grocery store. It allows users to browse available products and view information about them, such as category, product type, price, and vendor. Products are supplied by different vendors and managed through inventory batches with individual quantities, purchase prices, expiration dates, and statuses.

The application also provides user account management, shopping cart functionality, order processing, and payment management. Users can create an account, manage their information, add products to their cart, place orders, and keep track of their orders and payments.

## Business Concept

The application is designed around the idea that the same product type can be offered by different vendors at different selling prices and with different stock quantities.

For example, the product type can be:

**Milk 1L**

while the actual product offers can be:

* Milk 1L — Vendor: Latti — Price: 12.00
* Milk 1L — Vendor: MilkyWay — Price: 11.00

Each product offer can contain multiple inventory batches. A batch represents a specific quantity of products received from the vendor and contains information such as purchase price and expiration date.

This separation allows the application to manage product information, vendor offers, and physical inventory independently.

## Main Features

* User account creation and management
* Product catalog management
* Product categorization
* Product type management
* Vendor management
* Inventory batch management
* Product stock management
* Shopping cart management
* Order creation and management
* Payment management
* Product filtering by category, product type, and vendor
* Inventory and expiration date management

## Business Logic

### Product Type

`ProductType` represents the general type of a product available in the store.

For example:

> Milk 1L

A product type belongs to a category and can have multiple product offers.

### Product

`Product` represents a specific offer of a product from a particular vendor.

A product is connected to:

* one `ProductType`
* one `Vendor`
* one selling price
* one active status
* its creation date

Different vendors can therefore provide the same product type at different prices.

### Vendor

`Vendor` represents a supplier that provides products to the store.

One vendor can provide multiple products.

### Batch

`Batch` represents a specific inventory batch received from a vendor.

A batch contains:

* quantity
* purchase price
* expiration date
* active status
* creation date

The purchase price represents the price paid by the store to the vendor, while the product price represents the selling price offered to customers.

### Orders and Order Items

An `Order` represents a customer's purchase.

An order contains one or more `OrderItem` records. Each order item stores the purchased product, quantity, and the product price at the moment of purchase.

Storing the price in `OrderItem` preserves the order history even if the product's current selling price changes later.

### Payments

Each order can have a corresponding `Payment` containing information about:

* payment amount
* payment method
* payment status
* payment creation date

## Entity Relationships

The main entity relationships are:

```text
Category 1 ───── N ProductType

ProductType 1 ───── N Product

Vendor 1 ───── N Product

Product 1 ───── N Batch

User 1 ───── N Address

User 1 ───── 1 Cart

Cart 1 ───── N CartItem

Product 1 ───── N CartItem

User 1 ───── N Order

Order 1 ───── N OrderItem

Product 1 ───── N OrderItem

Order 1 ───── 1 Payment
```

### Main Product Structure

```text
Category
    │
    ▼
ProductType
    │
    ▼
Product ◄──── Vendor
    │
    ▼
Batch
```

The `ProductType` describes what the product is, while `Product` represents a concrete offer from a specific vendor and `Batch` represents the physical inventory received from that vendor.

## Application Flow

The basic purchasing flow is:

```text
User
  │
  ▼
Browse Products
  │
  ▼
Select Product
  │
  ▼
Add to Cart
  │
  ▼
Create Order
  │
  ▼
Payment
  │
  ▼
Order Confirmation
```

Inventory is associated with product batches and is used to determine whether the requested quantity is available.

## Project Architecture

The application follows a layered architecture:

```text
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
Database
```

### Controller

Responsible for handling HTTP requests and returning responses to clients.

### Service

Contains the application's business logic and coordinates operations between controllers and repositories.

### Repository

Responsible for communication with the database and persistence operations.

### Entity

Represents the application's domain objects and their relationships with database tables.

### DTO

Used to transfer data between different layers of the application without exposing entities directly.

## Technologies

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* Lombok

## Database

The application uses PostgreSQL as the relational database.

Entity persistence and database mapping are handled using JPA and Hibernate.

The main database entities include:

* User
* Address
* Category
* ProductType
* Product
* Vendor
* Batch
* Cart
* CartItem
* Order
* OrderItem
* Payment

## Project Status

The project is currently under development. Core domain entities and their relationships are being implemented first, followed by repositories, services, controllers, validation, exception handling, and testing.

## Future Improvements

Planned improvements include:

* REST API implementation
* Input validation
* Global exception handling
* Authentication and authorization
* Advanced product filtering
* Inventory management improvements
* Payment processing integration
* Unit and integration testing
* API documentation
* Docker support

```

Asta este **foarte suficient pentru README-ul de acum**. Nu trebuie să mai stăm să-l lustruim în seara asta. 😄

Și am avut grijă la un lucru: **nu am pretins că ai implementat deja ceea ce încă urmează să construiești**. Am separat ce reprezintă arhitectura/business-ul proiectului de ceea ce este încă „under development”.

Acum poți să-l pui în `README.md`, iar următorul pas poate fi liniștit **GitHub-ul**. 😎
```


________________________________________________________________


 🟢 USER REGISTRATION
 ↓
 🟢 LOGIN
 ↓
 🟢 VIEW PRODUCTS
 ↓
 🟡 produs disponibil?
 /          \
 NU            DA
 ↓              ↓
 🔴 STOP       🟢 ADD TO CART 
 ↓
 🟡 quantity valid?
 /         \
 NU           DA
 ↓             ↓
 🔴 STOP      🟢 CART
 ↓
 🟢 CHECKOUT
 ↓
 🟡 verifică stock
 /          \
 NU            DA
 ↓              ↓
 🔴 STOP        🟢 ORDER
 ↓
 🟢 PAYMENT
 /        \
 FAILED      SUCCESS
 ↓            ↓
 🔴 STOP    🟢 scade stock
 ↓
 🟢 ORDER CONFIRMED
 ↓
 🟢 DELIVERY

users
------------------------
id
first_name
last_name
email
password
phone
role
created_at

addresses
------------------------
id
user_id FK → users.id
city
street
house_number
apartment
postal_code

User 1 ─────── N Address

categories
------------------------
id
name
description

Dairy
Meat
Fruit
Vegetables
Drinks
Bakery

products
------------------------
id
category_id FK
name
description
price
active
created_at

Category 1 ─────── N Product

inventory_batches
------------------------
id
product_id FK
quantity
expiration_date
created_at

Product 1 ─────── N InventoryBatch

carts
------------------------
id
user_id FK
created_at

cart_items
------------------------
id
cart_id FK
product_id FK
quantity

Cart 1 ─────── N CartItem

Product 1 ──── N CartItem

Cart
│
├── Milk x2
├── Bread x1
└── Apples x4

orders
------------------------
id
user_id FK
address_id FK
status
total_price
created_at

NEW
PENDING_PAYMENT
CONFIRMED
PROCESSING
SHIPPED
DELIVERED
CANCELLED

User 1 ─────── N Order

order_items
------------------------
id
order_id FK
product_id FK
quantity
price

astăzi:
Milk = 25 lei

user cumpără → order #100

peste o lună:
Milk = 30 lei

payments
------------------------
id
order_id FK
amount
payment_method
status
created_at

CARD
CASH

PENDING
SUCCESS
FAILED
REFUNDED


                       USERS
                      /     \
                     /       \
                ADDRESSES    CART
                                │
                                ▼
                           CART_ITEMS
                                │
                                ▼
PRODUCTS ◄──────────────────────┘
│
├────────► CATEGORIES
│
└────────► INVENTORY_BATCHES


USERS
│
▼
ORDERS
│
├────────► ORDER_ITEMS ──────► PRODUCTS
│
├────────► PAYMENTS
│
└────────► ADDRESSES

Ordinea în care aș crea tabelele în Flyway

Aici contează foreign keys.

1. users
2. categories
3. products
4. addresses
5. inventory_batches
6. carts
7. cart_items
8. orders
9. order_items
10. payments