# Script to run all library management system services locally in PowerShell
# Make sure MySQL is running on localhost:3306 with empty root password
# And databases are created: auth_db, user_db, book_db, borrow_db, payment_db, notification_db

Write-Host "Starting all Library Management System services..." -ForegroundColor Green

# Function to run a service
function Run-Service {
    param([string]$serviceDir, [int]$port)
    Write-Host "Starting $serviceDir on port $port..." -ForegroundColor Yellow
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd $serviceDir && gradlew bootRun" -NoNewWindow
}

# Run all backend services in background
Run-Service "auth-service" 8083
Run-Service "user-service" 8081
Run-Service "book-service" 8082
Run-Service "borrow-service" 8086
Run-Service "payment-service" 8084
Run-Service "notification-service" 8085

Write-Host ""
Write-Host "All backend services are starting..." -ForegroundColor Green
Write-Host "Frontend URLs:" -ForegroundColor Cyan
Write-Host "  - User Frontend: http://localhost:3000 (if running separately)" -ForegroundColor White
Write-Host "  - Admin Frontend: http://localhost:3001 (if running separately)" -ForegroundColor White
Write-Host ""
Write-Host "API Endpoints:" -ForegroundColor Cyan
Write-Host "  - Auth Service: http://localhost:8083" -ForegroundColor White
Write-Host "  - User Service: http://localhost:8081" -ForegroundColor White
Write-Host "  - Book Service: http://localhost:8082" -ForegroundColor White
Write-Host "  - Borrow Service: http://localhost:8086" -ForegroundColor White
Write-Host "  - Payment Service: http://localhost:8084" -ForegroundColor White
Write-Host "  - Notification Service: http://localhost:8085" -ForegroundColor White
Write-Host ""
Write-Host "To stop all services: Use Task Manager or close command windows" -ForegroundColor Red
Write-Host ""
Write-Host "Check service logs in each command window for startup status..." -ForegroundColor Yellow

Read-Host "Press Enter to exit"