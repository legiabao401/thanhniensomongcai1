package mongcai1.thanhniensomongcai1.repository;

import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find all categories by type
     */
    List<Category> findByType(CategoryType type);
    
    /**
     * Find category by name and type (unique constraint)
     */
    Optional<Category> findByNameAndType(String name, CategoryType type);
    
    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc();
    
    /**
     * Find categories by type ordered by name
     */
    List<Category> findByTypeOrderByNameAsc(CategoryType type);
    
    /**
     * Check if category exists by name and type
     */
    boolean existsByNameAndType(String name, CategoryType type);
    
    /**
     * Find categories for locations with count
     */
    @Query("SELECT c FROM Category c WHERE c.type = 'LOCATION' AND SIZE(c.locations) > 0 ORDER BY c.name")
    List<Category> findLocationCategoriesWithLocations();
    
    /**
     * Find categories for posts with count
     */
    @Query("SELECT c FROM Category c WHERE c.type = 'POST' AND SIZE(c.posts) > 0 ORDER BY c.name")
    List<Category> findPostCategoriesWithPosts();
    
    /**
     * Search categories by name (case insensitive)
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.name")
    List<Category> searchByName(@Param("name") String name);
}