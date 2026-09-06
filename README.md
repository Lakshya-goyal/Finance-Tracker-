# Simple Beginner Full-Stack Finance Tracker

A clean, functional, and full-stack **Personal Finance Tracker** built with **Angular**, **Spring Boot**, **MySQL**, and **Google Gemini AI**.

---

## 1. About the Project

This project is designed as a clear, easy-to-understand full-stack web application for tracking personal income, expenses, categories, and monthly budgets. It demonstrates core software engineering fundamentals across the entire stack:
- **Angular**: Components, Routing, Reactive Forms, Route Guards, Services, `HttpClient`, `localStorage`.
- **Spring Boot**: REST Controllers, Services, JPA Repositories, Entities, Data Validation, CORS.
- **MySQL**: Relational tables, Foreign keys, CRUD operations.

---

## 2. Features

- **User Registration & Login**: Simple database-backed authentication.
- **Dashboard**:
  - 4 quick summary cards: Total Balance, Total Income, Total Expenses, This Month Expenses.
  - Recent Transactions table.
  - Expenses by Category breakdown with visual progress indicators.
- **✨ AI Bill & Receipt Scanner (Google Gemini AI)**:
  - Upload receipt/bill photos (PNG, JPG, JPEG, WEBP) or paste statement text/SMS alerts.
  - Automatically extracts Merchant/Title, Total Amount, Date, and Description.
  - Dynamically categorizes the expense into database-matched categories (Food, Bills, Transport, etc.).
  - **Editable Review Form**: Review and modify extracted details before submitting to the database.
  - Auto-fills into Transactions and updates Dashboard and Budgets in real-time.
- **Transactions Management**:
  - Add new Income or Expense.
  - Quick access to AI Scanner from Navbar and Add Transaction form.
  - View all transactions with color-coded badges (`INCOME` in green, `EXPENSE` in red).
  - Search transactions by title or description.
  - Filter transactions by Type (Income/Expense) and Category.
  - Edit and Delete transactions.
- **Category Management**:
  - Default seeded categories (Salary, Freelance, Food, Transport, Shopping, Bills, Entertainment, Education, Healthcare, Other).
  - Add custom categories with Income or Expense classification.
  - Edit and Delete categories.
- **Budget Tracking**:
  - Set monthly category spending budgets.
  - Visual HTML/CSS progress bars showing percentage spent, remaining balance, and over-budget warnings.
  - Edit and Delete budgets.
- **Client-Side Route Protection**:
  - Angular Route Guard prevents unauthorized access to protected pages when not logged in.


---

## 3. Tech Stack

### Frontend
- **Framework**: Angular 18 (Standalone Components)
- **Language**: TypeScript
- **State/Routing**: Angular Router & Route Guards
- **HTTP Client**: Angular `HttpClient`
- **Forms**: Angular Reactive Forms (`FormGroup`, `FormControl`, `Validators`)
- **Styling**: Clean, minimalist custom CSS (no heavy UI frameworks)

### Backend
- **Framework**: Spring Boot 3.3.2
- **Language**: Java 21
- **Persistence**: Spring Data JPA & Hibernate ORM
- **Web**: Spring Web (REST Controllers)
- **Validation**: Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@DecimalMin`)
- **Build Tool**: Apache Maven

### Database
- **Engine**: MySQL (`finance_tracker` database)

---

## 4. Architecture

The application follows the classic, clean 3-tier full-stack architecture:

```text
┌────────────────────────────────────────────────────────┐
│                   Angular Frontend                     │
│  (Components ──> Services ──> HttpClient Observables)  │
└───────────────────────────┬────────────────────────────┘
                            │ HTTP (JSON / REST)
                            ▼
┌────────────────────────────────────────────────────────┐
│                  Spring Boot Backend                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │ REST Controllers (@RestController)               │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ Service Layer (@Service) - Business Logic        │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ Data Access Layer (Spring Data JPA Repositories) │  │
│  └──────────────────────────────────────────────────┘  │
└───────────────────────────┬────────────────────────────┘
                            │ JDBC / SQL
                            ▼
┌────────────────────────────────────────────────────────┐
│                     MySQL Database                     │
│      (users, categories, transactions, budgets)        │
└────────────────────────────────────────────────────────┘
```

### End-to-End Flow Example: Adding a Transaction
```text
User fills Angular Form
        ↓
TransactionFormComponent (Validates inputs)
        ↓
TransactionService (Prepares JSON request)
        ↓
HttpClient POST http://localhost:8080/api/transactions
        ↓
TransactionController (@PostMapping createTransaction)
        ↓
TransactionService (Finds Category and User entities)
        ↓
TransactionRepository (save())
        ↓
MySQL Database (INSERT INTO transactions ...)
        ↓
Returns 201 Created with TransactionResponse
        ↓
Angular navigates back to /transactions with updated data
```

---

## 5. Database Schema

Database Name: `finance_tracker`

```text
users
├── id (BIGINT, PK, AUTO_INCREMENT)
├── name (VARCHAR(100))
├── email (VARCHAR(150), UNIQUE)
├── password (VARCHAR(255))
└── created_at (TIMESTAMP)

categories
├── id (BIGINT, PK, AUTO_INCREMENT)
├── name (VARCHAR(100))
└── type (VARCHAR(20)) -- 'INCOME' or 'EXPENSE'

transactions
├── id (BIGINT, PK, AUTO_INCREMENT)
├── title (VARCHAR(200))
├── amount (DECIMAL(12,2))
├── type (VARCHAR(20)) -- 'INCOME' or 'EXPENSE'
├── description (TEXT)
├── transaction_date (DATE)
├── category_id (BIGINT, FK -> categories.id)
├── user_id (BIGINT, FK -> users.id)
└── created_at (TIMESTAMP)

budgets
├── id (BIGINT, PK, AUTO_INCREMENT)
├── category_id (BIGINT, FK -> categories.id)
├── user_id (BIGINT, FK -> users.id)
├── amount (DECIMAL(12,2))
├── budget_month (INT)
├── budget_year (INT)
└── created_at (TIMESTAMP)
```

---

## 6. REST API Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Log in and retrieve user info |

### Categories (`/api/categories`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/categories` | Get all categories (optional `?type=EXPENSE`) |
| `POST` | `/api/categories` | Create a new category |
| `PUT` | `/api/categories/{id}` | Update category name/type |
| `DELETE` | `/api/categories/{id}` | Delete a category |

### Transactions (`/api/transactions`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/transactions/user/{userId}` | Get all transactions for a user |
| `GET` | `/api/transactions/{id}` | Get transaction details by ID |
| `POST` | `/api/transactions` | Create a new transaction |
| `PUT` | `/api/transactions/{id}` | Update an existing transaction |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction |

### Budgets (`/api/budgets`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/budgets/user/{userId}` | Get all monthly budgets and calculated spent amounts |
| `POST` | `/api/budgets` | Set or update a monthly budget |
| `PUT` | `/api/budgets/{id}` | Update a budget |
| `DELETE` | `/api/budgets/{id}` | Delete a budget |

### Dashboard (`/api/dashboard`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard/{userId}` | Get summary totals, balance, recent transactions, and category expenses |

---

## 7. Project Structure

```text
FinanceTracker/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/financetracker/
│       │   │   ├── FinanceTrackerApplication.java
│       │   │   ├── config/
│       │   │   │   ├── CorsConfig.java
│       │   │   │   └── DataInitializer.java
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── BudgetController.java
│       │   │   │   ├── CategoryController.java
│       │   │   │   ├── DashboardController.java
│       │   │   │   └── TransactionController.java
│       │   │   ├── dto/
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── BudgetRequest.java
│       │   │   │   ├── BudgetResponse.java
│       │   │   │   ├── CategoryExpenseDto.java
│       │   │   │   ├── CategoryRequest.java
│       │   │   │   ├── CategoryResponse.java
│       │   │   │   ├── DashboardResponse.java
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── TransactionRequest.java
│       │   │   │   ├── TransactionResponse.java
│       │   │   │   └── UserDto.java
│       │   │   ├── entity/
│       │   │   │   ├── Budget.java
│       │   │   │   ├── Category.java
│       │   │   │   ├── CategoryType.java
│       │   │   │   ├── Transaction.java
│       │   │   │   ├── TransactionType.java
│       │   │   │   └── User.java
│       │   │   ├── repository/
│       │   │   │   ├── BudgetRepository.java
│       │   │   │   ├── CategoryRepository.java
│       │   │   │   ├── TransactionRepository.java
│       │   │   │   └── UserRepository.java
│       │   │   └── service/
│       │   │       ├── BudgetService.java
│       │   │       ├── CategoryService.java
│       │   │       ├── DashboardService.java
│       │   │       ├── TransactionService.java
│       │   │       └── UserService.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── mysql-schema.sql
│       └── test/
│           ├── java/com/financetracker/
│           │   ├── FinanceTrackerApplicationTests.java
│           │   ├── FullFlowServiceTest.java
│           │   └── UserServiceTest.java
│           └── resources/
│               ├── application-test.properties
│               └── mockito-extensions/
│                   └── org.mockito.plugins.MockMaker
│
├── frontend/
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   └── src/
│       ├── index.html
│       ├── main.ts
│       ├── styles.css
│       └── app/
│           ├── app.component.ts/.html/.css
│           ├── app.routes.ts
│           ├── app.config.ts
│           ├── navbar/
│           ├── login/
│           ├── register/
│           ├── dashboard/
│           ├── transactions/
│           │   ├── transaction-list/
│           │   └── transaction-form/
│           ├── categories/
│           ├── budgets/
│           ├── guards/
│           │   └── auth.guard.ts
│           ├── models/
│           │   ├── budget.model.ts
│           │   ├── category.model.ts
│           │   ├── dashboard.model.ts
│           │   ├── transaction.model.ts
│           │   └── user.model.ts
│           └── services/
│               ├── auth.service.ts
│               ├── budget.service.ts
│               ├── category.service.ts
│               ├── dashboard.service.ts
│               └── transaction.service.ts
│
├── docs/
│   └── interview-questions.md
└── README.md
```

---

## 8. How to Run

### Step 1: Database Setup
1. Start your local MySQL server.
2. Open MySQL CLI or MySQL Workbench and create the database (or let Spring Boot create it automatically):
   ```sql
   CREATE DATABASE finance_tracker;
   ```
3. Update database credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Step 2: Start Backend
In a terminal:
```bash
cd backend
mvn clean spring-boot:run
```
The Spring Boot backend will start on **`http://localhost:8080`** and automatically seed default categories on initial startup.

To run automated backend tests:
```bash
mvn clean test
```

### Step 3: Start Frontend
In a separate terminal:
```bash
cd frontend
npm install
npm start
```
The Angular application will launch on **`http://localhost:4200`**.

---

## 9. Interview Preparation Guide

A dedicated interview preparation guide is available in [`docs/interview-questions.md`](docs/interview-questions.md), covering:
- What is Angular and why it was chosen.
- How Components, Services, Reactive Forms, and Route Guards work.
- Spring Boot architecture (`Controller` -> `Service` -> `Repository` -> `MySQL`).
- How Dependency Injection and JPA/Hibernate work.
- Step-by-step walkthroughs of user login, dashboard balance calculation, and adding transactions.

---

## 10. Important Security Disclaimer

> [!NOTE]
> This project uses a simple database-backed login flow for educational purposes. 
> A production application should use secure password hashing (such as BCrypt with Spring Security) and a proper authentication and authorization mechanism (such as session cookies or secure tokens).

---

## 11. Future Improvements

- Add BCrypt password hashing (`BCryptPasswordEncoder`).
- Export transaction reports to CSV or PDF.
- Add date range filtering on the Transactions page.
- Add visual pie charts or bar charts using Chart.js.
- Recurring transaction schedules.
