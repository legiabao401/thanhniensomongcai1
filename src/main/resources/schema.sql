-- Móng Cái 1 Regional Portal Database Schema
-- MySQL Database Schema for Spring Boot Application

-- Create database
CREATE DATABASE IF NOT EXISTS mongcai1_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mongcai1_portal;

-- Categories table for both locations and posts
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type ENUM('LOCATION', 'POST') NOT NULL,
    description TEXT,
    icon VARCHAR(50), -- For storing icon names
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_name_type (name, type)
);

-- Locations table
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address TEXT NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    category_id BIGINT NOT NULL,
    latitude DECIMAL(10, 8), -- For GPS coordinates
    longitude DECIMAL(11, 8), -- For GPS coordinates
    phone VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(200),
    opening_hours TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_category (category_id),
    INDEX idx_active (is_active)
);

-- Posts table for news and announcements
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    content LONGTEXT NOT NULL, -- HTML/Rich text content
    summary TEXT, -- Short description for cards
    thumbnail_url VARCHAR(500),
    category_id BIGINT NOT NULL,
    author VARCHAR(100) DEFAULT 'UBND Phường Móng Cái 1',
    is_featured BOOLEAN DEFAULT FALSE,
    is_urgent BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_category (category_id),
    INDEX idx_featured (is_featured),
    INDEX idx_urgent (is_urgent),
    INDEX idx_published (published_at),
    FULLTEXT(title, content, summary)
);

-- Insert initial categories for locations
INSERT INTO categories (name, type, description, icon) VALUES
('Hành chính', 'LOCATION', 'Các cơ quan hành chính nhà nước', 'Building2'),
('Y tế', 'LOCATION', 'Bệnh viện, trạm y tế, phòng khám', 'Heart'),
('Giáo dục', 'LOCATION', 'Trường học, trung tâm đào tạo', 'GraduationCap'),
('Thương mại', 'LOCATION', 'Chợ, siêu thị, dịch vụ thương mại', 'ShoppingBag'),
('Dịch vụ công cộng', 'LOCATION', 'Bưu điện, ngân hàng, dịch vụ khác', 'MapPin');

-- Insert initial categories for posts
INSERT INTO categories (name, type, description, icon) VALUES
('Thông báo chính thức', 'POST', 'Thông báo từ UBND và các cơ quan', 'FileText'),
('Hoạt động cộng đồng', 'POST', 'Các hoạt động của Đoàn thanh niên và cộng đồng', 'Users'),
('Y tế - Sức khỏe', 'POST', 'Thông tin về y tế và chăm sóc sức khỏe', 'Heart'),
('Giáo dục', 'POST', 'Thông tin giáo dục và đào tạo', 'BookOpen');

-- Insert sample locations
INSERT INTO locations (name, address, description, category_id, latitude, longitude, phone, email, opening_hours) VALUES
('UBND Phường Móng Cái 1', '123 Đường Trần Phú, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh', 'Ủy ban nhân dân phường Móng Cái 1, nơi giải quyết các thủ tục hành chính cho người dân', 1, 21.5285, 106.7317, '033-123-4567', 'ubnd@mongcai1.gov.vn', 'Thứ 2 - Thứ 6: 7:30-17:00, Thứ 7: 7:30-11:00'),
('Trạm Y tế Phường Móng Cái 1', '45 Đường Lê Lợi, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh', 'Trạm y tế phường cung cấp dịch vụ chăm sóc sức khỏe ban đầu cho người dân', 2, 21.5275, 106.7325, '033-123-4568', 'ytphuong@mongcai1.gov.vn', 'Thứ 2 - Chủ nhật: 6:00-22:00'),
('Trường Tiểu học Móng Cái 1', '67 Đường Nguyễn Trãi, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh', 'Trường tiểu học công lập chất lượng cao phục vụ giáo dục trẻ em trong khu vực', 3, 21.5295, 106.7335, '033-123-4569', 'tieuhoc@mongcai1.edu.vn', 'Thứ 2 - Thứ 6: 7:00-17:00'),
('Chợ Móng Cái', '89 Đường Hùng Vương, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh', 'Chợ truyền thống lớn nhất khu vực với đủ loại hàng hóa tươi sống và thiết yếu', 4, 21.5305, 106.7345, '033-123-4570', 'cho@mongcai1.gov.vn', 'Hàng ngày: 5:00-19:00'),
('Bưu điện Móng Cái 1', '12 Đường Hai Bà Trưng, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh', 'Bưu điện phường cung cấp dịch vụ bưu chính viễn thông', 5, 21.5265, 106.7355, '033-123-4571', 'buudien@mongcai1.gov.vn', 'Thứ 2 - Thứ 6: 7:30-17:30');

-- Insert sample posts
INSERT INTO posts (title, content, summary, category_id, is_featured, is_urgent, author) VALUES
('Thông báo về kế hoạch sáp nhập khu vực năm 2026', 
 '<h2>Thông báo chính thức</h2><p>UBND phường Móng Cái 1 thông báo về kế hoạch sáp nhập và tái cơ cấu hành chính theo Nghị quyết của UBND tỉnh Quảng Ninh...</p><h3>Nội dung chính:</h3><ul><li>Thời gian thực hiện: Quý I/2026</li><li>Phạm vi ảnh hưởng: Toàn bộ khu vực phường</li><li>Các thủ tục cần thiết cho người dân</li></ul>',
 'Chi tiết về kế hoạch sáp nhập và tái cơ cấu hành chính của khu vực Móng Cái 1 trong năm 2026', 
 1, TRUE, TRUE, 'UBND Phường Móng Cái 1'),

('Lịch tiêm chủng mở rộng tháng 1/2026',
 '<h2>Chương trình tiêm chủng miễn phí</h2><p>Trạm Y tế phường Móng Cái 1 thông báo lịch tiêm chủng mở rộng cho trẻ em và người cao tuổi...</p><h3>Đối tượng:</h3><ul><li>Trẻ em từ 6 tháng đến 5 tuổi</li><li>Người cao tuổi trên 65 tuổi</li><li>Phụ nữ mang thai</li></ul><h3>Thời gian:</h3><p>Từ ngày 15/01 đến 30/01/2026, các ngày thứ 2, 4, 6 hàng tuần.</p>',
 'Chương trình tiêm chủng miễn phí cho trẻ em và người cao tuổi tại các trạm y tế trong khu vực',
 3, TRUE, FALSE, 'Trạm Y tế Phường Móng Cái 1'),

('Hoạt động tình nguyện của Đoàn Thanh niên tháng 1/2026',
 '<h2>Các hoạt động trong tháng</h2><p>Đoàn Thanh niên phường Móng Cái 1 tổ chức nhiều hoạt động ý nghĩa...</p><h3>Chương trình:</h3><ul><li>Ngày 12/01: Tặng quà cho gia đình khó khăn</li><li>Ngày 19/01: Dọn dẹp vệ sinh môi trường</li><li>Ngày 26/01: Tư vấn pháp luật miễn phí</li></ul>',
 'Các hoạt động tình nguyện và phát triển cộng đồng của Đoàn thanh niên phường Móng Cái 1',
 2, FALSE, FALSE, 'Đoàn Thanh niên Phường'),

('Khai giảng năm học mới 2026 tại các trường trong khu vực',
 '<h2>Lễ khai giảng năm học 2025-2026</h2><p>Các trường học trong phường Móng Cái 1 chuẩn bị tổ chức lễ khai giảng năm học mới...</p><h3>Thời gian:</h3><p>Ngày 05/02/2026 (thứ 5) tại tất cả các trường</p><h3>Chuẩn bị:</h3><ul><li>Phụ huynh cần chuẩn bị đồng phục mới</li><li>Hoàn thành thủ tục nhập học</li><li>Tham gia họp phụ huynh đầu năm</li></ul>',
 'Thông tin về lễ khai giảng và các hoạt động giáo dục năm học mới tại khu vực',
 4, TRUE, FALSE, 'Phòng Giáo dục & Đào tạo');

-- Users table for admin authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    role ENUM('SUPER_ADMIN', 'ADMIN', 'EDITOR') NOT NULL DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, full_name, email, role, is_active) VALUES
('admin', 'admin123', 'Quản trị viên hệ thống', 'admin@mongcai1.gov.vn', 'SUPER_ADMIN', TRUE),
('editor', 'editor123', 'Biên tập viên', 'editor@mongcai1.gov.vn', 'EDITOR', TRUE);

-- Create indexes for better performance
CREATE INDEX idx_locations_name ON locations(name);
CREATE INDEX idx_posts_title ON posts(title);
CREATE INDEX idx_posts_published_featured ON posts(published_at, is_featured);

-- Grant permissions (adjust username as needed)
-- GRANT ALL PRIVILEGES ON mongcai1_portal.* TO 'mongcai1_user'@'localhost' IDENTIFIED BY 'mongcai1_password';
-- FLUSH PRIVILEGES;