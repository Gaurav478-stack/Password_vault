# SecurePass Vault - Java Backend Service

## 🎯 Overview

This Java backend service provides advanced password management features using **Object-Oriented Programming (OOP)** concepts. It complements the JavaScript frontend with additional security analysis, audit logging, and vault management capabilities.

## 📚 OOP Concepts Demonstrated

### 1. **Encapsulation**
- **Classes with Private Fields**: `Password`, `AuditLog`, `PasswordStrengthResult`
- **Getters/Setters**: Controlled access to object data
- **Data Hiding**: Internal implementation details hidden from users

### 2. **Abstraction**
- **Interfaces**: `IPasswordAnalyzer`, `IAuditService`
- **Abstract Contracts**: Define behavior without implementation details
- **Separation of Concerns**: Interface vs Implementation

### 3. **Inheritance**
- **Interface Implementation**: Classes implementing service interfaces
- **Code Reusability**: Shared behavior through inheritance hierarchy

### 4. **Polymorphism**
- **Method Overriding**: Different implementations of interface methods
- **Interface Types**: Work with abstractions rather than concrete classes

### 5. **Composition**
- **VaultManager**: Composed of `IPasswordAnalyzer` and `IAuditService`
- **Has-A Relationships**: Objects contain other objects

### 6. **Design Patterns**
- **Builder Pattern**: `AuditLog.Builder` for complex object creation
- **Dependency Injection**: Constructor injection in `VaultManager`
- **Facade Pattern**: `VaultManager` simplifies complex subsystems
- **Strategy Pattern**: Different analyzer implementations can be swapped

## 🏗️ Project Structure

```
java-backend/
└── src/main/java/com/securepass/
    ├── model/
    │   ├── Password.java                    # Password entity (Encapsulation)
    │   ├── AuditLog.java                    # Audit log entry (Builder Pattern)
    │   └── PasswordStrengthResult.java      # DTO for analysis results
    ├── service/
    │   ├── IPasswordAnalyzer.java           # Password service interface (Abstraction)
    │   ├── IAuditService.java               # Audit service interface (Abstraction)
    │   └── impl/
    │       ├── AdvancedPasswordAnalyzer.java # Implementation (Inheritance)
    │       └── InMemoryAuditService.java    # Implementation (Inheritance)
    ├── manager/
    │   └── VaultManager.java                # Main orchestrator (Composition, Facade)
    └── SecurePassApplication.java           # Entry point with demo
```

## 🚀 Features

### 1. Password Strength Analysis
- **Comprehensive Scoring**: 0-100 score based on multiple factors
- **Entropy Calculation**: Measures password randomness
- **Pattern Detection**: Finds sequential and repeated characters
- **Common Password Check**: Validates against known weak passwords
- **Detailed Feedback**: Actionable suggestions for improvement

### 2. Password Generation
- **Secure Random**: Uses `SecureRandom` for cryptographic strength
- **Customizable**: Length and character set options
- **Guaranteed Variety**: Ensures all character types included
- **Shuffle Algorithm**: Fisher-Yates for unpredictability

### 3. Vault Management
- **CRUD Operations**: Add, retrieve, update, delete passwords
- **Search Functionality**: Find by service name or username
- **Category Filtering**: Organize passwords by category
- **Access Tracking**: Records password access count and timestamp

### 4. Security Scanning
- **Weak Password Detection**: Identifies passwords scoring below threshold
- **Duplicate Detection**: Finds reused passwords across services
- **Automated Reports**: Generates comprehensive security assessments

### 5. Audit Logging
- **Complete Activity Tracking**: Logs all vault operations
- **Timestamp Recording**: Precise activity timestamps
- **Success/Failure Tracking**: Monitors security events
- **Report Generation**: Creates detailed audit reports
- **Filtering Capabilities**: Query by user, action, date range

### 6. Statistics & Analytics
- **Usage Metrics**: Total passwords, access counts
- **Category Breakdown**: Distribution across categories
- **Trend Analysis**: Activity patterns over time

## 🔧 How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- No external dependencies required (uses Java standard library)

### Compilation

```bash
# Navigate to java-backend directory
cd java-backend

# Compile all Java files
javac -d bin src/main/java/com/securepass/**/*.java src/main/java/com/securepass/*.java
```

### Execution

```bash
# Run the demo application
java -cp bin com.securepass.SecurePassApplication
```

## 📊 Demo Output

The application demonstrates:

1. **Password Strength Analysis**
   - Tests various passwords
   - Shows scoring and feedback
   - Displays entropy calculations

2. **Strong Password Generation**
   - Generates cryptographically secure passwords
   - Validates generated passwords
   - Shows strength scores

3. **Vault Operations**
   - Adds sample passwords
   - Demonstrates search functionality
   - Shows category filtering

4. **Security Analysis**
   - Scans for weak passwords
   - Detects duplicate passwords
   - Provides recommendations

5. **Vault Statistics**
   - Total password count
   - Access statistics
   - Category breakdown

6. **Audit Report**
   - Complete activity log
   - Success rate analysis
   - Recent activity summary

## 💡 Usage Examples

### Analyzing Password Strength

```java
IPasswordAnalyzer analyzer = new AdvancedPasswordAnalyzer();
PasswordStrengthResult result = analyzer.analyzeStrength("MyP@ssw0rd!");

System.out.println("Score: " + result.getScore());
System.out.println("Level: " + result.getLevel());
System.out.println("Entropy: " + result.getEntropy() + " bits");
```

### Adding Password to Vault

```java
VaultManager vault = new VaultManager("user123", analyzer, auditService);
Password password = new Password(
    UUID.randomUUID().toString(),
    "Gmail",
    "user@gmail.com",
    "encrypted_password",
    "email"
);
vault.addPassword(password);
```

### Generating Strong Password

```java
String strongPassword = analyzer.generateStrongPassword(16, true);
System.out.println("Generated: " + strongPassword);
```

### Security Scan

```java
List<Password> weakPasswords = vault.findWeakPasswords();
Map<String, List<Password>> duplicates = vault.findDuplicatePasswords();
```

### Audit Logging

```java
IAuditService auditService = new InMemoryAuditService();
AuditLog log = new AuditLog.Builder()
    .action("LOGIN")
    .userId("user123")
    .success(true)
    .build();
auditService.logEvent(log);
```

## 🎓 Learning Objectives

This codebase teaches:

1. **Encapsulation**: How to protect data with private fields and public methods
2. **Abstraction**: Using interfaces to define contracts
3. **Inheritance**: Implementing interfaces and extending classes
4. **Polymorphism**: Multiple implementations of same interface
5. **Composition**: Building complex objects from simpler ones
6. **Design Patterns**: Practical application of Builder, Facade, Strategy patterns
7. **Dependency Injection**: Loose coupling through constructor injection
8. **Immutability**: Creating thread-safe immutable objects
9. **Stream API**: Modern Java collection processing
10. **Best Practices**: Clean code, SOLID principles, documentation

## 🔐 Security Features

- **Secure Random Generation**: Uses `SecureRandom` for cryptographic strength
- **No Password Storage**: Demonstrates design without storing plaintext
- **Audit Trail**: Complete logging of all activities
- **Input Validation**: Prevents null and invalid data
- **Exception Handling**: Proper error management

## 🚀 Future Enhancements

Potential extensions:

1. **REST API**: Add Spring Boot REST endpoints
2. **Database Integration**: Replace in-memory storage with PostgreSQL/MongoDB
3. **JWT Authentication**: Secure API access
4. **Password Breach API**: Integrate with HaveIBeenPwned
5. **Multi-user Support**: Add user management
6. **Export/Import**: Vault backup and restore
7. **2FA Support**: Two-factor authentication
8. **Password Expiry**: Time-based password rotation
9. **Sharing**: Secure password sharing between users
10. **Mobile App**: Android/iOS clients

## 📝 Class Descriptions

### Models

- **Password**: Represents a stored password with metadata
- **AuditLog**: Immutable log entry for audit trail
- **PasswordStrengthResult**: Analysis result DTO

### Services

- **IPasswordAnalyzer**: Interface for password operations
- **AdvancedPasswordAnalyzer**: Implementation with advanced algorithms
- **IAuditService**: Interface for audit logging
- **InMemoryAuditService**: Implementation using in-memory storage

### Managers

- **VaultManager**: Main orchestrator combining all services
- **VaultStatistics**: Inner class for statistics

## 🤝 Integration with Frontend

This Java backend can be integrated with the JavaScript frontend by:

1. **REST API**: Expose services as REST endpoints
2. **JSON Communication**: Serialize/deserialize data
3. **CORS Configuration**: Allow cross-origin requests
4. **JWT Tokens**: Secure authentication
5. **WebSocket**: Real-time updates

Example integration:
```
Frontend (JavaScript) → REST API (Java Spring Boot) → Services (Java)
```

## 📖 Additional Resources

- [Java OOP Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Design Patterns](https://refactoring.guru/design-patterns)
- [SOLID Principles](https://www.digitalocean.com/community/conceptual_articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)
- [Java Best Practices](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

## 📄 License

MIT License - Free to use and modify

---

**Made with ❤️ for learning OOP concepts**
