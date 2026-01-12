package mongcai1.thanhniensomongcai1.service;

import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.model.CategoryType;
import mongcai1.thanhniensomongcai1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Get category by ID
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Get categories by type
     */
    public List<Category> getCategoriesByType(CategoryType type) {
        return categoryRepository.findByTypeOrderByNameAsc(type);
    }
    
    /**
     * Get location categories
     */
    public List<Category> getLocationCategories() {
        return categoryRepository.findByTypeOrderByNameAsc(CategoryType.LOCATION);
    }
    
    /**
     * Get post categories
     */
    public List<Category> getPostCategories() {
        return categoryRepository.findByTypeOrderByNameAsc(CategoryType.POST);
    }
    
    /**
     * Get location categories that have locations
     */
    public List<Category> getLocationCategoriesWithLocations() {
        return categoryRepository.findLocationCategoriesWithLocations();
    }
    
    /**
     * Get post categories that have posts
     */
    public List<Category> getPostCategoriesWithPosts() {
        return categoryRepository.findPostCategoriesWithPosts();
    }
    
    /**
     * Search categories by name
     */
    public List<Category> searchCategoriesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.searchByName(name.trim());
    }
    
    /**
     * Find category by name and type
     */
    public Optional<Category> findByNameAndType(String name, CategoryType type) {
        return categoryRepository.findByNameAndType(name, type);
    }
    
    /**
     * Create new category
     */
    public Category createCategory(Category category) {
        // Check if category with same name and type already exists
        if (categoryRepository.existsByNameAndType(category.getName(), category.getType())) {
            throw new IllegalArgumentException("Danh mục với tên '" + category.getName() + 
                                             "' và loại '" + category.getType() + "' đã tồn tại");
        }
        
        return categoryRepository.save(category);
    }
    
    /**
     * Update existing category
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        
        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            
            // Check if the new name and type combination already exists (excluding current category)
            Optional<Category> duplicateCategory = categoryRepository.findByNameAndType(
                categoryDetails.getName(), categoryDetails.getType());
            
            if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(id)) {
                throw new IllegalArgumentException("Danh mục với tên '" + categoryDetails.getName() + 
                                                 "' và loại '" + categoryDetails.getType() + "' đã tồn tại");
            }
            
            // Update fields
            category.setName(categoryDetails.getName());
            category.setType(categoryDetails.getType());
            category.setDescription(categoryDetails.getDescription());
            category.setIcon(categoryDetails.getIcon());
            
            return categoryRepository.save(category);
        } else {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }
    }
    
    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            
            // Check if category has associated locations or posts
            if ((cat.getLocations() != null && !cat.getLocations().isEmpty()) ||
                (cat.getPosts() != null && !cat.getPosts().isEmpty())) {
                throw new IllegalStateException("Không thể xóa danh mục vì vẫn còn địa điểm hoặc bài viết liên kết");
            }
            
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }
    }
    
    /**
     * Force delete category (cascade delete all associated data)
     */
    public void forceDeleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }
    }
    
    /**
     * Check if category exists
     */
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }
    
    /**
     * Check if category name and type combination exists
     */
    public boolean categoryExistsByNameAndType(String name, CategoryType type) {
        return categoryRepository.existsByNameAndType(name, type);
    }
}