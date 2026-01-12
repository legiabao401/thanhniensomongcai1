package mongcai1.thanhniensomongcai1.repository;

import mongcai1.thanhniensomongcai1.model.Location;
import mongcai1.thanhniensomongcai1.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * Find all active locations
     */
    List<Location> findByIsActiveTrue();
    
    /**
     * Find locations by category
     */
    List<Location> findByCategoryAndIsActiveTrue(Category category);
    
    /**
     * Find locations by category ID
     */
    List<Location> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    /**
     * Find locations by category ID with pagination
     */
    Page<Location> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    /**
     * Find all active locations with pagination
     */
    Page<Location> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Search locations by name or address (case insensitive)
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.address) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY l.name")
    List<Location> searchByNameOrAddress(@Param("query") String query);
    
    /**
     * Search locations by name or address with pagination
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.address) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY l.name")
    Page<Location> searchByNameOrAddress(@Param("query") String query, Pageable pageable);
    
    /**
     * Find locations by category and search query
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND l.category.id = :categoryId AND " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.address) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY l.name")
    List<Location> findByCategoryAndSearch(@Param("categoryId") Long categoryId, @Param("query") String query);
    
    /**
     * Find locations within a geographic area (bounding box)
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND " +
           "l.latitude BETWEEN :minLat AND :maxLat AND " +
           "l.longitude BETWEEN :minLng AND :maxLng " +
           "ORDER BY l.name")
    List<Location> findWithinBounds(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                   @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);
    
    /**
     * Count active locations by category
     */
    long countByCategoryIdAndIsActiveTrue(Long categoryId);
    
    /**
     * Find locations with phone numbers
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND l.phone IS NOT NULL AND l.phone != '' ORDER BY l.name")
    List<Location> findLocationsWithPhone();
    
    /**
     * Find locations with coordinates
     */
    @Query("SELECT l FROM Location l WHERE l.isActive = true AND l.latitude IS NOT NULL AND l.longitude IS NOT NULL ORDER BY l.name")
    List<Location> findLocationsWithCoordinates();
}