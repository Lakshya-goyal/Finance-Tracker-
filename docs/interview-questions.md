# Full-Stack Finance Tracker — Interview Questions & Answers

This document contains beginner-friendly interview questions and clear explanations based directly on the code in this project.

---

## 1. Frontend & Angular Fundamentals

### Q1: What is Angular?
**Answer:**
Angular is a TypeScript-based open-source frontend web framework developed by Google. It is used for building single-page applications (SPAs) where user interactions feel fast and dynamic because pages update without requiring full browser reloads.

### Q2: Why did you use Angular for this project?
**Answer:**
We used Angular because:
1. **Component-based architecture**: Breaks down the UI into clean, reusable pieces (e.g., `NavbarComponent`, `DashboardComponent`, `TransactionListComponent`).
2. **Strong typing with TypeScript**: Catches errors during development before running in the browser.
3. **Built-in tools**: Provides powerful built-in routing (`RouterModule`), form management (`ReactiveFormsModule`), and HTTP communication (`HttpClient`) without requiring third-party libraries.

### Q3: What is a Component in Angular?
**Answer:**
A component is the fundamental building block of an Angular user interface. It consists of:
- **TypeScript Class (`.ts`)**: Contains data properties and business logic (e.g. `TransactionFormComponent`).
- **HTML Template (`.html`)**: Defines the visual UI structure.
- **CSS Stylesheet (`.css`)**: Encapsulates styling for that specific component.

### Q4: What is an Angular Service?
**Answer:**
An Angular Service is a TypeScript class with the `@Injectable()` decorator designed to hold reusable logic and manage data across multiple components. In our project, services handle all communication with the backend REST API (e.g., `AuthService`, `TransactionService`, `CategoryService`, `BudgetService`, `DashboardService`).

### Q5: Why did you create `TransactionService`?
**Answer:**
Instead of writing `HttpClient` API calls directly inside UI components like `TransactionListComponent` and `TransactionFormComponent`, we centralize all transaction HTTP requests (`GET`, `POST`, `PUT`, `DELETE`) inside `TransactionService`. This keeps components focused only on presentation and makes API calls easy to maintain, test, and reuse.

### Q6: What is `HttpClient` in Angular?
**Answer:**
`HttpClient` is Angular's built-in service (`provideHttpClient()`) for making HTTP requests (such as `GET`, `POST`, `PUT`, and `DELETE`) to backend REST APIs. It returns RxJS `Observable`s, which let components handle asynchronous responses or errors cleanly using `.subscribe()`.

### Q7: How does Angular communicate with Spring Boot?
**Answer:**
1. The user performs an action in an Angular component (e.g. clicks "Save Transaction").
2. The component calls a method in an Angular Service (e.g. `transactionService.createTransaction(data)`).
3. The service uses `HttpClient` to send an HTTP request (`POST http://localhost:8080/api/transactions`) with JSON data.
4. Spring Boot receives the request in `TransactionController`, processes it, and responds with a JSON payload and HTTP status code (`200 OK` or `201 Created`).
5. Angular's `Observable` resolves the response data back to the component to update the UI.

### Q8: What is Dependency Injection (DI)?
**Answer:**
Dependency Injection is a design pattern where a framework automatically provides required dependencies (such as services or repositories) to a class rather than the class creating them manually with `new`.
- **In Angular**: `constructor(private transactionService: TransactionService) {}`
- **In Spring Boot**: `public TransactionController(TransactionService transactionService) { this.transactionService = transactionService; }`

---

## 2. Backend & Spring Boot Fundamentals

### Q9: What is Spring Boot?
**Answer:**
Spring Boot is an extension of the Java Spring framework that simplifies creating stand-alone, production-ready applications. It provides auto-configuration, starter dependencies (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`), and an embedded server (Tomcat) so we can run the backend with a single command without complex XML setup.

### Q10: What is a REST API?
**Answer:**
A REST (Representational State Transfer) API is an architectural style for web services. It uses standard HTTP methods to perform operations on resources:
- `GET /api/transactions/user/1`: Retrieve transactions
- `POST /api/transactions`: Create a new transaction
- `PUT /api/transactions/1`: Update an existing transaction
- `DELETE /api/transactions/1`: Delete a transaction

### Q11: What is a Controller in Spring Boot?
**Answer:**
A Controller is a class annotated with `@RestController` that handles incoming HTTP requests from clients (like Angular), maps them to endpoints using `@GetMapping`, `@PostMapping`, etc., validates input data with `@Valid`, calls the appropriate Service method, and returns `ResponseEntity` containing data and HTTP status codes.

### Q12: What is a Service in Spring Boot?
**Answer:**
A Service is a class annotated with `@Service` that holds the core business logic of the application. For example, `DashboardService` calculates total income, total expenses, net balance, and percentage of category expenses. Controllers should not contain business logic; they delegate to Services.

### Q13: What is a Repository in Spring Boot?
**Answer:**
A Repository is an interface that extends Spring Data JPA's `JpaRepository`. It acts as the data access layer (DAO) for interacting with the database. Spring automatically generates SQL implementations for standard CRUD operations (`save`, `findById`, `findAll`, `deleteById`) as well as custom derived queries like `findByEmail` or `findByUserId`.

---

## 3. Database & JPA Fundamentals

### Q14: What is JPA?
**Answer:**
JPA (Jakarta Persistence API) is a standard Java specification for Object-Relational Mapping (ORM). It allows developers to map Java classes (Entities) to database tables and map class fields to table columns without writing raw SQL queries.

### Q15: What is Hibernate?
**Answer:**
Hibernate is the actual ORM library that implements the JPA specification. While JPA defines the interfaces and annotations (`@Entity`, `@Table`, `@ManyToOne`), Hibernate does the heavy lifting of generating SQL queries, executing them against MySQL, and converting database rows into Java objects.

### Q16: What is an Entity in JPA?
**Answer:**
An Entity is a Java class annotated with `@Entity` that represents a table in the relational database. In our project, we have four entities:
1. `User` -> `users` table
2. `Category` -> `categories` table
3. `Transaction` -> `transactions` table
4. `Budget` -> `budgets` table

### Q17: What is `@ManyToOne`?
**Answer:**
`@ManyToOne` is a JPA annotation representing a relationship where many rows in one table reference a single row in another table.
- Example: Many `Transaction` records belong to one `User` (`@ManyToOne private User user;`).
- Example: Many `Transaction` records belong to one `Category` (`@ManyToOne private Category category;`).

### Q18: Why did you use MySQL?
**Answer:**
MySQL is a proven, reliable, open-source relational database management system (RDBMS). It enforces data integrity using primary keys, unique constraints (e.g. unique user emails), and foreign keys across users, categories, transactions, and budgets.

---

## 4. End-to-End Application Flows

### Q19: How does Login work in this project?
**Answer:**
1. **User Input**: User enters email and password into Angular's `LoginComponent` reactive form.
2. **HTTP Request**: `AuthService.login()` sends `POST /api/auth/login` to Spring Boot.
3. **Controller**: `AuthController.login()` receives the `LoginRequest`.
4. **Service**: `UserService.login()` looks up the user by email using `UserRepository.findByEmail()`.
5. **Validation**: Compares the entered password with the stored password.
6. **Response**: Returns an `AuthResponse` containing `UserDto` (`id`, `name`, `email`).
7. **Storage**: Angular stores the user details in `localStorage` under key `currentUser`.
8. **Navigation**: Angular router redirects the user to `/dashboard`.

### Q20: How does the Dashboard calculate balance?
**Answer:**
1. Angular sends `GET /api/dashboard/{userId}` to Spring Boot.
2. `DashboardService` fetches all transactions for that user using `TransactionRepository.findByUserIdOrderByTransactionDateDesc(userId)`.
3. It iterates over the transactions:
   - Sums all `INCOME` transactions -> `totalIncome`
   - Sums all `EXPENSE` transactions -> `totalExpenses`
   - Calculates `balance = totalIncome - totalExpenses`
   - Groups expenses by category and calculates percentages for the progress bars
   - Filters expenses in the current month for `thisMonthExpenses`
4. Returns the calculated `DashboardResponse` to Angular for rendering in summary cards.

### Q21: How does adding a Transaction work end-to-end?
**Answer:**
```text
[1] User fills Angular Form (Title, Amount, Type, Category, Date, Description)
       ↓
[2] TransactionFormComponent runs Reactive Form validation (required, min amount > 0)
       ↓
[3] TransactionService sends HTTP POST to http://localhost:8080/api/transactions
       ↓
[4] TransactionController receives @Valid @RequestBody TransactionRequest
       ↓
[5] TransactionService fetches Category entity and User entity
       ↓
[6] Transaction entity is populated and passed to TransactionRepository.save()
       ↓
[7] Hibernate generates SQL INSERT INTO transactions ... and executes it on MySQL
       ↓
[8] Spring Boot returns 201 Created with TransactionResponse
       ↓
[9] Angular receives response and navigates to /transactions with updated list
```

### Q22: How does client-side Route Protection work?
**Answer:**
In Angular, we created an `authGuard` function (`CanActivateFn`). When a user attempts to navigate to protected routes like `/dashboard`, `/transactions`, or `/budgets`:
1. `authGuard` checks `authService.isLoggedIn()`.
2. If `localStorage` contains a logged-in user, the guard returns `true` (navigation allowed).
3. If not, it redirects the user to `/login` and returns `false`.

---

## 5. Summary Cheat-Sheet for Interviews

| Concept | Purpose in this Project |
| :--- | :--- |
| **Angular Standalone Component** | Self-contained UI view with HTML, TypeScript, and CSS |
| **Reactive Forms** | Handles user inputs with synchronous validators (`Validators.required`, `Validators.email`, `Validators.min`) |
| **Route Guard** | Prevents unauthenticated users from opening protected views |
| **Spring Boot Controller** | Defines REST API endpoints and handles HTTP requests |
| **Spring Boot Service** | Houses core business logic and calculations |
| **Spring Data JPA Repository** | Executes database queries without boilerplate SQL |
| **JPA Entities** | Java classes mapped directly to MySQL tables (`users`, `categories`, `transactions`, `budgets`) |
| **MySQL Foreign Keys** | Ensures transactions and budgets are linked to valid users and categories |
