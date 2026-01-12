package mongcai1.thanhniensomongcai1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 300, message = "Tiêu đề không được vượt quá 300 ký tự")
    @Column(nullable = false, length = 300)
    private String title;
    
    @NotBlank(message = "Nội dung không được để trống")
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Size(max = 500, message = "URL hình ảnh không được vượt quá 500 ký tự")
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    @Column(length = 100)
    private String author = "UBND Phường Móng Cái 1";
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt = LocalDateTime.now();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục không được để trống")
    private Category category;
    
    // Constructors
    public Post() {}
    
    public Post(String title, String content, String summary, Category category) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public Boolean getIsUrgent() {
        return isUrgent;
    }
    
    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    // Helper method to increment view count
    public void incrementViewCount() {
        this.viewCount = this.viewCount + 1;
    }
    
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isFeatured=" + isFeatured +
                ", isUrgent=" + isUrgent +
                ", viewCount=" + viewCount +
                ", publishedAt=" + publishedAt +
                '}';
    }
}