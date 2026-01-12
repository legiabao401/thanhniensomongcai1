package mongcai1.thanhniensomongcai1.repository;

import mongcai1.thanhniensomongcai1.model.Post;
import mongcai1.thanhniensomongcai1.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * Find all posts ordered by published date (newest first)
     */
    Page<Post> findAllByOrderByPublishedAtDesc(Pageable pageable);
    
    /**
     * Find featured posts ordered by published date
     */
    List<Post> findByIsFeaturedTrueOrderByPublishedAtDesc();
    
    /**
     * Find featured posts with pagination
     */
    Page<Post> findByIsFeaturedTrueOrderByPublishedAtDesc(Pageable pageable);
    
    /**
     * Find urgent posts ordered by published date
     */
    List<Post> findByIsUrgentTrueOrderByPublishedAtDesc();
    
    /**
     * Find posts by category
     */
    Page<Post> findByCategoryOrderByPublishedAtDesc(Category category, Pageable pageable);
    
    /**
     * Find posts by category ID
     */
    Page<Post> findByCategoryIdOrderByPublishedAtDesc(Long categoryId, Pageable pageable);
    
    /**
     * Search posts by title or content (case insensitive)
     */
    @Query("SELECT p FROM Post p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.publishedAt DESC")
    Page<Post> searchByTitleOrContent(@Param("query") String query, Pageable pageable);
    
    /**
     * Find posts published after a specific date
     */
    Page<Post> findByPublishedAtAfterOrderByPublishedAtDesc(LocalDateTime date, Pageable pageable);
    
    /**
     * Find posts published between dates
     */
    Page<Post> findByPublishedAtBetweenOrderByPublishedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find recent posts (last 30 days)
     */
    @Query("SELECT p FROM Post p WHERE p.publishedAt >= :thirtyDaysAgo ORDER BY p.publishedAt DESC")
    List<Post> findRecentPosts(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    /**
     * Find top viewed posts
     */
    Page<Post> findAllByOrderByViewCountDescPublishedAtDesc(Pageable pageable);
    
    /**
     * Find posts by author
     */
    Page<Post> findByAuthorOrderByPublishedAtDesc(String author, Pageable pageable);
    
    /**
     * Count posts by category
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * Count featured posts
     */
    long countByIsFeaturedTrue();
    
    /**
     * Count urgent posts
     */
    long countByIsUrgentTrue();
    
    /**
     * Increment view count for a post
     */
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
    
    /**
     * Find posts with high view count (popular posts)
     */
    @Query("SELECT p FROM Post p WHERE p.viewCount > :minViews ORDER BY p.viewCount DESC, p.publishedAt DESC")
    List<Post> findPopularPosts(@Param("minViews") Integer minViews);
    
    /**
     * Find latest posts by category (limit 5)
     */
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId ORDER BY p.publishedAt DESC")
    List<Post> findLatestPostsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
}