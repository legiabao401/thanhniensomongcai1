package mongcai1.thanhniensomongcai1.service;

import mongcai1.thanhniensomongcai1.model.Post;
import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.repository.PostRepository;
import mongcai1.thanhniensomongcai1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all posts with pagination
     */
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByPublishedAtDesc(pageable);
    }
    
    /**
     * Get post by ID and increment view count
     */
    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            // Increment view count
            postRepository.incrementViewCount(id);
            // Return updated post
            return postRepository.findById(id);
        }
        return post;
    }
    
    /**
     * Get post by ID without incrementing view count (for admin purposes)
     */
    public Optional<Post> getPostByIdNoIncrement(Long id) {
        return postRepository.findById(id);
    }
    
    /**
     * Get featured posts
     */
    public List<Post> getFeaturedPosts() {
        return postRepository.findByIsFeaturedTrueOrderByPublishedAtDesc();
    }
    
    /**
     * Get featured posts with pagination
     */
    public Page<Post> getFeaturedPosts(Pageable pageable) {
        return postRepository.findByIsFeaturedTrueOrderByPublishedAtDesc(pageable);
    }
    
    /**
     * Get urgent posts
     */
    public List<Post> getUrgentPosts() {
        return postRepository.findByIsUrgentTrueOrderByPublishedAtDesc();
    }
    
    /**
     * Get latest featured posts (limit 3 for homepage)
     */
    public List<Post> getLatestFeaturedPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findByIsFeaturedTrueOrderByPublishedAtDesc(pageable).getContent();
    }
    
    /**
     * Get posts by category
     */
    public Page<Post> getPostsByCategory(Long categoryId, Pageable pageable) {
        return postRepository.findByCategoryIdOrderByPublishedAtDesc(categoryId, pageable);
    }
    
    /**
     * Get latest posts by category (for widgets)
     */
    public List<Post> getLatestPostsByCategory(Long categoryId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findLatestPostsByCategory(categoryId, pageable);
    }
    
    /**
     * Search posts by title or content
     */
    public Page<Post> searchPosts(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPosts(pageable);
        }
        return postRepository.searchByTitleOrContent(query.trim(), pageable);
    }
    
    /**
     * Get recent posts (last 30 days)
     */
    public List<Post> getRecentPosts() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return postRepository.findRecentPosts(thirtyDaysAgo);
    }
    
    /**
     * Get popular posts (high view count)
     */
    public List<Post> getPopularPosts(int minViews) {
        return postRepository.findPopularPosts(minViews);
    }
    
    /**
     * Get top viewed posts
     */
    public Page<Post> getTopViewedPosts(Pageable pageable) {
        return postRepository.findAllByOrderByViewCountDescPublishedAtDesc(pageable);
    }
    
    /**
     * Get posts by author
     */
    public Page<Post> getPostsByAuthor(String author, Pageable pageable) {
        return postRepository.findByAuthorOrderByPublishedAtDesc(author, pageable);
    }
    
    /**
     * Get posts published after a specific date
     */
    public Page<Post> getPostsAfterDate(LocalDateTime date, Pageable pageable) {
        return postRepository.findByPublishedAtAfterOrderByPublishedAtDesc(date, pageable);
    }
    
    /**
     * Get posts published between dates
     */
    public Page<Post> getPostsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return postRepository.findByPublishedAtBetweenOrderByPublishedAtDesc(startDate, endDate, pageable);
    }
    
    /**
     * Create new post
     */
    public Post createPost(Post post) {
        // Validate category exists and is of type POST
        if (post.getCategory() != null) {
            Optional<Category> category = categoryRepository.findById(post.getCategory().getId());
            if (category.isPresent() && category.get().getType().toString().equals("POST")) {
                post.setCategory(category.get());
            } else {
                throw new IllegalArgumentException("Danh mục không hợp lệ hoặc không phải danh mục bài viết");
            }
        }
        
        // Set default values
        if (post.getAuthor() == null || post.getAuthor().trim().isEmpty()) {
            post.setAuthor("UBND Phường Móng Cái 1");
        }
        
        if (post.getIsFeatured() == null) {
            post.setIsFeatured(false);
        }
        
        if (post.getIsUrgent() == null) {
            post.setIsUrgent(false);
        }
        
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        return postRepository.save(post);
    }
    
    /**
     * Update existing post
     */
    public Post updatePost(Long id, Post postDetails) {
        Optional<Post> existingPost = postRepository.findById(id);
        
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            
            // Update fields
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            post.setSummary(postDetails.getSummary());
            post.setThumbnailUrl(postDetails.getThumbnailUrl());
            post.setAuthor(postDetails.getAuthor());
            post.setIsFeatured(postDetails.getIsFeatured());
            post.setIsUrgent(postDetails.getIsUrgent());
            post.setPublishedAt(postDetails.getPublishedAt());
            
            // Update category if provided
            if (postDetails.getCategory() != null) {
                Optional<Category> category = categoryRepository.findById(postDetails.getCategory().getId());
                if (category.isPresent() && category.get().getType().toString().equals("POST")) {
                    post.setCategory(category.get());
                } else {
                    throw new IllegalArgumentException("Danh mục không hợp lệ hoặc không phải danh mục bài viết");
                }
            }
            
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + id);
        }
    }
    
    /**
     * Delete post
     */
    public void deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + id);
        }
    }
    
    /**
     * Get statistics
     */
    public long getTotalPostCount() {
        return postRepository.count();
    }
    
    public long getFeaturedPostCount() {
        return postRepository.countByIsFeaturedTrue();
    }
    
    public long getUrgentPostCount() {
        return postRepository.countByIsUrgentTrue();
    }
    
    public long getPostCountByCategory(Long categoryId) {
        return postRepository.countByCategoryId(categoryId);
    }
    
    /**
     * Check if post exists
     */
    public boolean postExists(Long id) {
        return postRepository.existsById(id);
    }
}