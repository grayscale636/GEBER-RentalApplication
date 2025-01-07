Sure, here is a sample 

README.md

 file for your project:

```markdown
# GEBER Rental Application

## Project Description

GEBER Rental Application is a web-based application designed to manage vehicle rentals. The application allows administrators to manage vehicles and rentals, and provides a user-friendly interface for customers to rent vehicles. The application is built using Spring Boot, Thymeleaf, and other modern web technologies.

## Project Details

- **Technologies Used:**
  - Java
  - Spring Boot
  - Thymeleaf
  - Spring Data JPA
  - H2 Database (for development)
  - Bootstrap (for UI)

- **Features:**
  - Admin login and authentication
  - Vehicle management (add, update, delete vehicles)
  - Rental management (add, update, delete rentals)
  - Pagination and sorting for vehicle listings
  - Alerts and notifications for admin actions

## How to Clone and Run the Project

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven
- Git

### Steps to Clone and Run

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/geber-rental-application.git
   cd geber-rental-application
   ```

2. **Build the project using Maven:**

   ```bash
   mvn clean install
   ```

3. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**

   Open your web browser and go to `http://localhost:8080`.

### Default Admin Credentials

- **Username:** admin
- **Password:** admin

### Additional Information

- The application uses an in-memory H2 database for development purposes. You can access the H2 console at `http://localhost:8080/h2-console` with the following credentials:
  - **JDBC URL:** `jdbc:h2:mem:testdb`
  - **Username:** `sa`
  - **Password:** (leave blank)

- To change the database configuration, update the `application.properties` file located in the 

resources

 directory.

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
```

Replace `https://github.com/yourusername/geber-rental-application.git` with the actual URL of your repository. This `README.md` file provides a comprehensive overview of the project, including its description, details, and instructions on how to clone and run it.
Replace `https://github.com/yourusername/geber-rental-application.git` with the actual URL of your repository. This `README.md` file provides a comprehensive overview of the project, including its description, details, and instructions on how to clone and run it.