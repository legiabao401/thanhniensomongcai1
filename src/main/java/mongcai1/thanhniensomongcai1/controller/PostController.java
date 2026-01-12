package mongcai1.thanhniensomongcai1.controller;

import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.model.Post;
import mongcai1.thanhniensomongcai1.repository.CategoryRepository;
import mongcai1.thanhniensomongcai1.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * GET /api/posts - Get all posts with optional filtering
     */
    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean urgent,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Post> posts;
            
            if (categoryId != null) {
                // Filter by category
                posts = postService.getPostsByCategory(categoryId, pageable);
            } else if (search != null && !search.trim().isEmpty()) {
                // Search posts
                posts = postService.searchPosts(search, pageable);
            } else if (featured != null && featured) {
                // Get featured posts
                posts = postService.getFeaturedPosts(pageable);
            } else if (author != null && !author.trim().isEmpty()) {
                // Get posts by author
                posts = postService.getPostsByAuthor(author, pageable);
            } else if (startDate != null && endDate != null) {
                // Get posts between dates
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                    LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                    posts = postService.getPostsBetweenDates(start, end, pageable);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Định dạng ngày không hợp lệ. Sử dụng ISO format: yyyy-MM-ddTHH:mm:ss");
                }
            } else if (startDate != null) {
                // Get posts after date
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                    posts = postService.getPostsAfterDate(start, pageable);
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Định dạng ngày không hợp lệ. Sử dụng ISO format: yyyy-MM-ddTHH:mm:ss");
                }
            } else {
                // Get all posts
                posts = postService.getAllPosts(pageable);
            }
            
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh sách bài viết: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/{id} - Get post by ID (increments view count)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Optional<Post> post = postService.getPostById(id);
            
            if (post.isPresent()) {
                return ResponseEntity.ok(post.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bài viết với ID: " + id);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải thông tin bài viết: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/{id}/preview - Get post by ID without incrementing view count (for admin)
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<?> getPostByIdPreview(@PathVariable Long id) {
        try {
            Optional<Post> post = postService.getPostByIdNoIncrement(id);
            
            if (post.isPresent()) {
                return ResponseEntity.ok(post.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bài viết với ID: " + id);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải thông tin bài viết: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/featured - Get featured posts
     */
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedPosts(
            @RequestParam(defaultValue = "3") int limit) {
        
        try {
            List<Post> posts = postService.getLatestFeaturedPosts(limit);
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết nổi bật: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/urgent - Get urgent posts
     */
    @GetMapping("/urgent")
    public ResponseEntity<?> getUrgentPosts() {
        try {
            List<Post> posts = postService.getUrgentPosts();
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết khẩn cấp: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/recent - Get recent posts (last 30 days)
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentPosts() {
        try {
            List<Post> posts = postService.getRecentPosts();
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết gần đây: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/popular - Get popular posts (high view count)
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularPosts(
            @RequestParam(defaultValue = "10") int minViews) {
        
        try {
            List<Post> posts = postService.getPopularPosts(minViews);
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết phổ biến: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/top-viewed - Get top viewed posts
     */
    @GetMapping("/top-viewed")
    public ResponseEntity<?> getTopViewedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postService.getTopViewedPosts(pageable);
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết được xem nhiều: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/category/{categoryId}/latest - Get latest posts by category
     */
    @GetMapping("/category/{categoryId}/latest")
    public ResponseEntity<?> getLatestPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<Post> posts = postService.getLatestPostsByCategory(categoryId, limit);
            return ResponseEntity.ok(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải bài viết mới nhất theo danh mục: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/posts/stats - Get post statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getPostStats() {
        try {
            return ResponseEntity.ok(new PostStats(
                postService.getTotalPostCount(),
                postService.getFeaturedPostCount(),
                postService.getUrgentPostCount()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải thống kê bài viết: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/posts - Create new post (Admin only)
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> request) {
        try {
            // Get categoryId from request
            Long categoryId = null;
            if (request.get("categoryId") != null) {
                categoryId = Long.parseLong(request.get("categoryId").toString());
            }
            
            if (categoryId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Danh mục không được để trống"));
            }
            
            // Find category
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Danh mục không tồn tại"));
            }
            
            // Create Post object
            Post post = new Post();
            post.setTitle((String) request.get("title"));
            post.setContent((String) request.get("content"));
            post.setSummary((String) request.get("summary"));
            post.setAuthor((String) request.get("author"));
            post.setThumbnailUrl((String) request.get("thumbnailUrl"));
            post.setCategory(categoryOpt.get());
            
            // Set boolean fields
            if (request.get("isFeatured") != null) {
                post.setIsFeatured(Boolean.parseBoolean(request.get("isFeatured").toString()));
            }
            if (request.get("isUrgent") != null) {
                post.setIsUrgent(Boolean.parseBoolean(request.get("isUrgent").toString()));
            }
            
            Post createdPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi tạo bài viết mới: " + e.getMessage()));
        }
    }
    
    /**
     * PUT /api/posts/{id} - Update post (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // Find existing post
            Optional<Post> existingOpt = postService.getPostByIdNoIncrement(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy bài viết với ID: " + id));
            }
            
            Post post = existingOpt.get();
            
            // Update category if provided
            if (request.get("categoryId") != null) {
                Long categoryId = Long.parseLong(request.get("categoryId").toString());
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                if (categoryOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Danh mục không tồn tại"));
                }
                post.setCategory(categoryOpt.get());
            }
            
            // Update other fields
            if (request.get("title") != null) {
                post.setTitle((String) request.get("title"));
            }
            if (request.get("content") != null) {
                post.setContent((String) request.get("content"));
            }
            if (request.containsKey("summary")) {
                post.setSummary((String) request.get("summary"));
            }
            if (request.containsKey("author")) {
                post.setAuthor((String) request.get("author"));
            }
            if (request.containsKey("thumbnailUrl")) {
                post.setThumbnailUrl((String) request.get("thumbnailUrl"));
            }
            if (request.get("isFeatured") != null) {
                post.setIsFeatured(Boolean.parseBoolean(request.get("isFeatured").toString()));
            }
            if (request.get("isUrgent") != null) {
                post.setIsUrgent(Boolean.parseBoolean(request.get("isUrgent").toString()));
            }
            
            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi cập nhật bài viết: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/posts/{id} - Delete post (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Đã xóa bài viết thành công");
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa bài viết: " + e.getMessage());
        }
    }
    
    // Inner class for statistics response
    public static class PostStats {
        private long totalPosts;
        private long featuredPosts;
        private long urgentPosts;
        
        public PostStats(long totalPosts, long featuredPosts, long urgentPosts) {
            this.totalPosts = totalPosts;
            this.featuredPosts = featuredPosts;
            this.urgentPosts = urgentPosts;
        }
        
        // Getters
        public long getTotalPosts() { return totalPosts; }
        public long getFeaturedPosts() { return featuredPosts; }
        public long getUrgentPosts() { return urgentPosts; }
    }
}