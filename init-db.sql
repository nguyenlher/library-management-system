-- Create databases for each service
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS book_db;
CREATE DATABASE IF NOT EXISTS borrow_db;
CREATE DATABASE IF NOT EXISTS payment_db;
CREATE DATABASE IF NOT EXISTS notification_db;

-- Grant all privileges on these databases to root user
GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON user_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON book_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON borrow_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON payment_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'root'@'%';

FLUSH PRIVILEGES;
