# Quick Start Guide - Java Backend

## 🚀 Get Started in 3 Steps

### Step 1: Verify Java Installation
```powershell
java -version
```
You should see Java version 8 or higher. If not, download from [oracle.com/java](https://www.oracle.com/java/technologies/downloads/)

### Step 2: Run the Application
Navigate to the `java-backend` folder and run:
```powershell
cd java-backend
run.bat
```

### Step 3: Watch the Demo
The application will automatically demonstrate all features!

## 📺 What You'll See

The demo showcases 6 main features:

### 1️⃣ Password Strength Analysis
Tests various passwords and shows:
- Strength score (0-100)
- Security level (WEAK to VERY_STRONG)
- Entropy in bits
- Actionable feedback

### 2️⃣ Strong Password Generation
Generates cryptographically secure passwords:
- 16+ characters
- Mixed case, numbers, special characters
- High entropy scores
- Validated automatically

### 3️⃣ Vault Operations
Demonstrates core functionality:
- Adding passwords to vault
- Searching by service name
- Filtering by category
- Access tracking

### 4️⃣ Security Analysis
Identifies vulnerabilities:
- ⚠️ Weak passwords (score < 50)
- 🔄 Duplicate passwords across services
- 📊 Security recommendations

### 5️⃣ Vault Statistics
Shows usage metrics:
- Total password count
- Total access count
- Category breakdown
- Distribution charts

### 6️⃣ Security Audit Report
Complete activity log with:
- All vault operations
- Success/failure tracking
- Action breakdown
- Recent activity summary

## 🎓 Code Structure

```
src/main/java/com/securepass/
├── model/                      # Data models
│   ├── Password.java           # Password entity
│   ├── AuditLog.java          # Audit log (Builder pattern)
│   └── PasswordStrengthResult.java
│
├── service/                    # Service layer (Abstraction)
│   ├── IPasswordAnalyzer.java  # Interface
│   ├── IAuditService.java      # Interface
│   └── impl/                   # Implementations
│       ├── AdvancedPasswordAnalyzer.java
│       └── InMemoryAuditService.java
│
├── manager/                    # Business logic
│   └── VaultManager.java       # Main orchestrator (Facade)
│
└── SecurePassApplication.java  # Entry point with demo
```

## 🎯 Key OOP Concepts

### Encapsulation
```java
private String encryptedPassword;  // Private field

public String getEncryptedPassword() {  // Public getter
    return encryptedPassword;
}
```

### Abstraction (Interface)
```java
public interface IPasswordAnalyzer {
    PasswordStrengthResult analyzeStrength(String password);
    String generateStrongPassword(int length, boolean includeSpecialChars);
}
```

### Inheritance (Implementation)
```java
public class AdvancedPasswordAnalyzer implements IPasswordAnalyzer {
    @Override
    public PasswordStrengthResult analyzeStrength(String password) {
        // Implementation here
    }
}
```

### Composition
```java
public class VaultManager {
    private final IPasswordAnalyzer passwordAnalyzer;  // Has-A relationship
    private final IAuditService auditService;
}
```

### Builder Pattern
```java
AuditLog log = new AuditLog.Builder()
    .action("LOGIN")
    .userId("user123")
    .success(true)
    .build();
```

## 💡 Try It Yourself

After running the demo, uncomment the interactive menu in `SecurePassApplication.java`:

```java
// runDemo();  // Comment out
runInteractiveMenu();  // Uncomment this
```

Then recompile and run to use the interactive menu!

## 🔧 Manual Commands

If you prefer manual compilation:

```powershell
# Compile
javac -d bin src/main/java/com/securepass/**/*.java src/main/java/com/securepass/*.java

# Run
java -cp bin com.securepass.SecurePassApplication
```

## 📚 Learn More

- Full documentation: `README.md`
- Code examples: All classes have detailed comments
- OOP concepts: Check class headers for explanations

## 🎉 Next Steps

1. ✅ Run the demo
2. 📖 Read the code with comments
3. 🔧 Try the interactive menu
4. 🚀 Extend with your own features!

**Happy Learning! 🎓**
