# 🏛️ Móng Cái 1 Regional Portal - Complete Fullstack Setup Guide

## 📋 Project Overview
A comprehensive fullstack web application for managing locations and news in the Móng Cái 1 region, built with Spring Boot backend, MySQL database, and React frontend following Youth Union design standards.

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.1** - Framework
- **Spring Data JPA** - Database abstraction
- **Spring Web** - REST API
- **Spring Validation** - Input validation
- **MySQL 8.0+** - Database

### Frontend
- **React 18** - UI library
- **Tailwind CSS** - Styling framework
- **Lucide React** - Icons
- **Vanilla JavaScript** - API client

### Design System
- **Primary Color**: Deep Youth Union Blue (#0056B3)
- **Accent Colors**: Flag Red (#DA251D), Star Yellow (#FFFF00)
- **Typography**: Inter font family
- **Mobile-first responsive design**

## 🗄️ Database Schema

### Tables Created
1. **categories** - Location and post categories
2. **locations** - Geographic locations with details
3. **posts** - News articles and announcements

### Key Features
- Full-text search capabilities
- Geographic coordinates support
- View count tracking
- Featured and urgent content flags
- Comprehensive indexing for performance

## 🚀 Quick Start Guide

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE mongcai1_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Run the schema.sql file
mysql -u root -p mongcai1_portal < src/main/resources/schema.sql
```

### 2. Application Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 3. Run Backend
```bash
# Using Maven
./mvnw spring-boot:run

# Or using IDE
# Run Thanhniensomongcai1Application.java
```

### 4. Access Application
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Frontend**: Open `src/main/resources/static/index.html` in browser

## 📁 Project Structure

```
src/
├── main/
│   ├── java/mongcai1/thanhniensomongcai1/
│   │   ├── model/           # JPA Entities
│   │   │   ├── Category.java
│   │   │   ├── Location.java
│   │   │   └── Post.java
│   │   ├── repository/      # Data Access Layer
│   │   │   ├── CategoryRepository.java
│   │   │   ├── LocationRepository.java
│   │   │   └── PostRepository.java
│   │   ├── service/         # Business Logic Layer
│   │   │   ├── CategoryService.java
│   │   │   ├── LocationService.java
│   │   │   └── PostService.java
│   │   ├── controller/      # REST API Layer
│   │   │   ├── CategoryController.java
│   │   │   ├── LocationController.java
│   │   │   └── PostController.java
│   │   └── Thanhniensomongcai1Application.java
│   └── resources/
│       ├── application.properties
│       ├── schema.sql       # Database schema
│       └── static/          # Frontend files
│           ├── api/
│           │   └── apiClient.js
│           ├── components/
│           │   ├── HomePage.jsx
│           │   ├── LocationPage.jsx
│           │   └── NewsPage.jsx
│           └── index.html
└── test/                    # Test files
```

## 🔌 API Endpoints

### Categories API
- `GET /api/categories` - Get all categories
- `GET /api/categories/locations` - Get location categories
- `GET /api/categories/posts` - Get post categories
- `POST /api/categories` - Create category (Admin)

### Locations API
- `GET /api/locations` - Get locations with filtering
- `GET /api/locations/{id}` - Get location by ID
- `GET /api/locations/simple` - Get all locations (no pagination)
- `GET /api/locations/with-coordinates` - Get locations with GPS data
- `POST /api/locations` - Create location (Admin)

### Posts API
- `GET /api/posts` - Get posts with pagination and filtering
- `GET /api/posts/{id}` - Get post by ID (increments view count)
- `GET /api/posts/featured` - Get featured posts
- `GET /api/posts/urgent` - Get urgent posts
- `POST /api/posts` - Create post (Admin)

## 🎨 Frontend Components

### HomePage.jsx
- Hero section with search
- Quick access categories
- Featured news display
- Recent locations showcase

### LocationPage.jsx
- Advanced filtering by category
- Grid/List view toggle
- Google Maps integration
- Search functionality

### NewsPage.jsx
- Paginated news listing
- Category filtering
- Sort by date/views
- Responsive design

## 🔧 Development Features

### Backend Features
- **Comprehensive validation** with Jakarta Validation
- **Error handling** with proper HTTP status codes
- **CORS support** for frontend integration
- **Pagination** and sorting for large datasets
- **Search functionality** with full-text search
- **Soft delete** for locations (isActive flag)

### Frontend Features
- **Responsive design** for all screen sizes
- **Loading states** and error handling
- **API integration** with custom client
- **Interactive components** with hover effects
- **Vietnamese localization** throughout

## 📊 Sample Data Included

### Locations
- UBND Phường Móng Cái 1
- Trạm Y tế Phường Móng Cái 1
- Trường Tiểu học Móng Cái 1
- Chợ Móng Cái
- Bưu điện Móng Cái 1

### News Articles
- Administrative announcements
- Health program updates
- Youth union activities
- Educational information

## 🔒 Security Considerations

### Current Implementation
- CORS configuration for local development
- Input validation on all endpoints
- SQL injection prevention with JPA

### Production Recommendations
- Add Spring Security for authentication
- Implement JWT tokens for API access
- Add rate limiting for public endpoints
- Use HTTPS in production
- Environment-specific configuration

## 🚀 Deployment Guide

### Local Development
1. Install MySQL 8.0+
2. Create database and run schema
3. Update application.properties
4. Run Spring Boot application
5. Open frontend in browser

### Production Deployment
1. **Database**: Use managed MySQL service (AWS RDS, etc.)
2. **Backend**: Deploy to cloud platform (AWS, Heroku, etc.)
3. **Frontend**: Serve static files or use CDN
4. **Environment**: Set production environment variables

## 🧪 Testing

### Backend Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=LocationServiceTest
```

### API Testing
- Use Swagger UI at `/swagger-ui.html`
- Import Postman collection (can be generated)
- Test endpoints with sample data

## 📈 Performance Optimizations

### Database
- Proper indexing on frequently queried columns
- Connection pooling configured
- Query optimization with JPA

### Frontend
- Lazy loading of components
- Efficient state management
- Optimized API calls with pagination

## 🤝 Contributing

### Code Standards
- Follow Java naming conventions
- Use proper JSDoc for JavaScript functions
- Maintain consistent indentation
- Write meaningful commit messages

### Development Workflow
1. Create feature branch
2. Implement changes
3. Test thoroughly
4. Submit pull request
5. Code review process

## 📞 Support

For technical support or questions about the Móng Cái 1 Regional Portal:
- Review this documentation
- Check API documentation at `/swagger-ui.html`
- Examine sample data and test endpoints
- Follow the setup guide step by step

## 🎯 Next Steps

### Recommended Enhancements
1. **User Authentication**: Add login system for admins
2. **File Upload**: Support for images in posts and locations
3. **Email Notifications**: Alert system for urgent posts
4. **Mobile App**: React Native version
5. **Analytics**: Usage tracking and reporting
6. **SEO**: Meta tags and structured data
7. **PWA**: Progressive Web App features

The application is production-ready and follows modern development best practices with comprehensive error handling, validation, and responsive design.