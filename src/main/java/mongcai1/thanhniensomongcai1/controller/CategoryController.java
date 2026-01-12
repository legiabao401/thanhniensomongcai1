package mongcai1.thanhniensomongcai1.controller;

import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.model.CategoryType;
import mongcai1.thanhniensomongcai1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * GET /api/categories - Get all categories
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        
        try {
            List<Category> categories;
            
            if (search != null && !search.trim().isEmpty()) {
                // Search categories by name
                categories = categoryService.searchCategoriesByName(search);
            } else if (type != null) {
                // Filter by type
                try {
                    CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
                    categories = categoryService.getCategoriesByType(categoryType);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Loại danh mục không hợp lệ. Sử dụng: LOCATION hoặc POST");
                }
            } else {
                // Get all categories
                categories = categoryService.getAllCategories();
            }
            
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh sách danh mục: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/categories/{id} - Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            
            if (category.isPresent()) {
                return ResponseEntity.ok(category.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy danh mục với ID: " + id);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải thông tin danh mục: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/categories/locations - Get location categories
     */
    @GetMapping("/locations")
    public ResponseEntity<?> getLocationCategories(
            @RequestParam(defaultValue = "false") boolean withData) {
        
        try {
            List<Category> categories;
            
            if (withData) {
                // Only get categories that have locations
                categories = categoryService.getLocationCategoriesWithLocations();
            } else {
                // Get all location categories
                categories = categoryService.getLocationCategories();
            }
            
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh mục địa điểm: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/categories/posts - Get post categories
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getPostCategories(
            @RequestParam(defaultValue = "false") boolean withData) {
        
        try {
            List<Category> categories;
            
            if (withData) {
                // Only get categories that have posts
                categories = categoryService.getPostCategoriesWithPosts();
            } else {
                // Get all post categories
                categories = categoryService.getPostCategories();
            }
            
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh mục bài viết: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/categories - Create new category (Admin only)
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo danh mục mới: " + e.getMessage());
        }
    }
    
    /**
     * PUT /api/categories/{id} - Update category (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/categories/{id} - Delete category (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Đã xóa danh mục thành công");
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa danh mục: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/categories/{id}/force - Force delete category and all associated data (Admin only)
     */
    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> forceDeleteCategory(@PathVariable Long id) {
        try {
            categoryService.forceDeleteCategory(id);
            return ResponseEntity.ok("Đã xóa vĩnh viễn danh mục và tất cả dữ liệu liên quan");
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa vĩnh viễn danh mục: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/categories/check - Check if category exists by name and type
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkCategoryExists(
            @RequestParam String name,
            @RequestParam String type) {
        
        try {
            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
            boolean exists = categoryService.categoryExistsByNameAndType(name, categoryType);
            
            return ResponseEntity.ok(new CategoryExistsResponse(exists));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Loại danh mục không hợp lệ. Sử dụng: LOCATION hoặc POST");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kiểm tra danh mục: " + e.getMessage());
        }
    }
    
    // Inner class for category exists response
    public static class CategoryExistsResponse {
        private boolean exists;
        
        public CategoryExistsResponse(boolean exists) {
            this.exists = exists;
        }
        
        public boolean isExists() {
            return exists;
        }
    }
}