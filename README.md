# Library Management System

A microservices library management system built with Spring Boot and React.

## Project Structure

```
library-management-system/
├── auth-service/
├── user-service/
├── book-service/
├── borrow-service/
├── payment-service/
├── notification-service/
├── frontend-user/
├── frontend-admin/
├── docker-compose.yml
├── run-all-services.ps1
├── .gitignore
└── README.md
```

## System Architecture

```
Library Management System
├── Backend Services (Spring Boot 3.5.7 + Java 21)
│   ├── auth-service (Port 8083) - User authentication
│   ├── user-service (Port 8081) - User profile management
│   ├── book-service (Port 8082) - Book catalog management
│   ├── borrow-service (Port 8086) - Book borrowing/returning
│   ├── payment-service (Port 8084) - Payment processing
│   └── notification-service (Port 8085) - Notification services
└── Frontend Applications (React)
    ├── frontend-user (Port 3000) - User interface
    └── frontend-admin (Port 3001) - Admin interface

```

## Installation and Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd library-management-system
```

### 2. Run with Docker
```bash
docker-compose up --build
```

**URLs after startup:**
- Frontend User: http://localhost:3000
- Frontend Admin: http://localhost:3001
- Auth Service: http://localhost:8083
- User Service: http://localhost:8081
- Book Service: http://localhost:8082
- Borrow Service: http://localhost:8086
- Payment Service: http://localhost:8084
- Notification Service: http://localhost:8085

### 3. Local Development Setup

#### Build All Services
```bash
./gradlew build
```

#### Run Services
```bash
.\run-all-services.ps1

# Or run each service manually
cd auth-service && ./gradlew bootRun &
cd user-service && ./gradlew bootRun &
cd book-service && ./gradlew bootRun &
cd borrow-service && ./gradlew bootRun &
cd payment-service && ./gradlew bootRun &
cd notification-service && ./gradlew bootRun &
```

#### Run Frontend
```bash
# Frontend User
cd frontend-user
npm install
npm start
# Access: http://localhost:3000

# Frontend Admin
cd frontend-admin
npm install
npm start
# Access: http://localhost:3001
```

## Database

Each service uses a separate database:
- `auth_db` - Authentication data
- `user_db` - User profiles
- `book_db` - Books and categories
- `borrow_db` - Borrowing transactions
- `payment_db` - Payment records
- `notification_db` - Notifications

**Database schemas are automatically created** when services start for the first time.

## Configuration

### Environment Variables
Create `.env` file in root directory:
```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_ROOT_PASSWORD=
JWT_SECRET=your-secret-key
```

### Application Properties
Each service has `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/service_db?createDatabaseIfNotExist=true
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: update
```

## Troubleshooting

### Services Won't Start
```bash
# Check for port conflicts
netstat -ano | findstr :8081

# Check logs
cd service-name && ./gradlew bootRun --info
```

### Frontend Won't Load
```bash
# Clear cache and reinstall
cd frontend-user
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
npm start
```

### Gradle Build Failed
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### Missing gradle-wrapper.jar
If you encounter the error "Unable to access jarfile ...gradle-wrapper.jar":

```bash
# Download the wrapper jar (for Gradle 8.14.3)
Invoke-WebRequest -Uri "https://github.com/gradle/gradle/blob/v8.14.3/gradle/wrapper/gradle-wrapper.jar?raw=true" -OutFile "gradle\wrapper\gradle-wrapper.jar"

# Then run build
./gradlew build
```

## Contact

- Project Link: https://github.com/nguyenlher/library-management-system
- Email: nguyenlher@gmail.com

---