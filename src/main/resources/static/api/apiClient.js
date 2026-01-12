// API Client for Móng Cái 1 Regional Portal
// Base configuration and utility functions for API calls

const API_BASE_URL = 'http://localhost:8080/api';

// API Client class
class ApiClient {
    constructor(baseURL = API_BASE_URL) {
        this.baseURL = baseURL;
        this.defaultHeaders = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
    }

    // Generic request method
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: { ...this.defaultHeaders, ...options.headers },
            ...options
        };

        try {
            const response = await fetch(url, config);
            
            // Handle different response types
            const contentType = response.headers.get('content-type');
            let data;
            
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                throw new Error(data.message || data || `HTTP error! status: ${response.status}`);
            }

            return data;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // GET request
    async get(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        
        return this.request(url, {
            method: 'GET'
        });
    }

    // POST request
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // PUT request
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    // DELETE request
    async delete(endpoint) {
        return this.request(endpoint, {
            method: 'DELETE'
        });
    }
}

// Create API client instance
const apiClient = new ApiClient();

// Location API functions
export const locationAPI = {
    // Get all locations with optional filters
    getAll: (params = {}) => apiClient.get('/locations', params),
    
    // Get locations without pagination (for maps, dropdowns)
    getAllSimple: (params = {}) => apiClient.get('/locations/simple', params),
    
    // Get location by ID
    getById: (id) => apiClient.get(`/locations/${id}`),
    
    // Get locations with coordinates
    getWithCoordinates: () => apiClient.get('/locations/with-coordinates'),
    
    // Get locations with phone numbers
    getWithPhone: () => apiClient.get('/locations/with-phone'),
    
    // Get locations within geographic bounds
    getWithinBounds: (minLat, maxLat, minLng, maxLng) => 
        apiClient.get('/locations/bounds', { minLat, maxLat, minLng, maxLng }),
    
    // Get location count by category
    getCountByCategory: (categoryId) => apiClient.get(`/locations/category/${categoryId}/count`),
    
    // Create new location (Admin)
    create: (location) => apiClient.post('/locations', location),
    
    // Update location (Admin)
    update: (id, location) => apiClient.put(`/locations/${id}`, location),
    
    // Delete location (Admin)
    delete: (id) => apiClient.delete(`/locations/${id}`),
    
    // Permanently delete location (Admin)
    permanentDelete: (id) => apiClient.delete(`/locations/${id}/permanent`)
};

// Post API functions
export const postAPI = {
    // Get all posts with optional filters
    getAll: (params = {}) => apiClient.get('/posts', params),
    
    // Get post by ID (increments view count)
    getById: (id) => apiClient.get(`/posts/${id}`),
    
    // Get post by ID without incrementing view count (Admin)
    getByIdPreview: (id) => apiClient.get(`/posts/${id}/preview`),
    
    // Get featured posts
    getFeatured: (limit = 3) => apiClient.get('/posts/featured', { limit }),
    
    // Get urgent posts
    getUrgent: () => apiClient.get('/posts/urgent'),
    
    // Get recent posts (last 30 days)
    getRecent: () => apiClient.get('/posts/recent'),
    
    // Get popular posts
    getPopular: (minViews = 10) => apiClient.get('/posts/popular', { minViews }),
    
    // Get top viewed posts
    getTopViewed: (params = {}) => apiClient.get('/posts/top-viewed', params),
    
    // Get latest posts by category
    getLatestByCategory: (categoryId, limit = 5) => 
        apiClient.get(`/posts/category/${categoryId}/latest`, { limit }),
    
    // Get post statistics
    getStats: () => apiClient.get('/posts/stats'),
    
    // Create new post (Admin)
    create: (post) => apiClient.post('/posts', post),
    
    // Update post (Admin)
    update: (id, post) => apiClient.put(`/posts/${id}`, post),
    
    // Delete post (Admin)
    delete: (id) => apiClient.delete(`/posts/${id}`)
};

// Category API functions
export const categoryAPI = {
    // Get all categories
    getAll: (params = {}) => apiClient.get('/categories', params),
    
    // Get category by ID
    getById: (id) => apiClient.get(`/categories/${id}`),
    
    // Get location categories
    getLocationCategories: (withData = false) => 
        apiClient.get('/categories/locations', { withData }),
    
    // Get post categories
    getPostCategories: (withData = false) => 
        apiClient.get('/categories/posts', { withData }),
    
    // Check if category exists
    checkExists: (name, type) => apiClient.get('/categories/check', { name, type }),
    
    // Create new category (Admin)
    create: (category) => apiClient.post('/categories', category),
    
    // Update category (Admin)
    update: (id, category) => apiClient.put(`/categories/${id}`, category),
    
    // Delete category (Admin)
    delete: (id) => apiClient.delete(`/categories/${id}`),
    
    // Force delete category (Admin)
    forceDelete: (id) => apiClient.delete(`/categories/${id}/force`)
};

// Utility functions for common API patterns
export const apiUtils = {
    // Handle API errors with user-friendly messages
    handleError: (error) => {
        console.error('API Error:', error);
        
        if (error.message.includes('Failed to fetch')) {
            return 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.';
        } else if (error.message.includes('404')) {
            return 'Không tìm thấy dữ liệu yêu cầu.';
        } else if (error.message.includes('500')) {
            return 'Lỗi máy chủ. Vui lòng thử lại sau.';
        } else {
            return error.message || 'Đã xảy ra lỗi không xác định.';
        }
    },

    // Format date for API calls
    formatDate: (date) => {
        if (date instanceof Date) {
            return date.toISOString();
        }
        return date;
    },

    // Build pagination params
    buildPaginationParams: (page = 0, size = 10, sortBy = 'id', sortDir = 'asc') => ({
        page,
        size,
        sortBy,
        sortDir
    }),

    // Build search params
    buildSearchParams: (query, categoryId = null) => {
        const params = {};
        if (query && query.trim()) {
            params.search = query.trim();
        }
        if (categoryId) {
            params.categoryId = categoryId;
        }
        return params;
    }
};

// Export default API client
export default apiClient;