package mongcai1.thanhniensomongcai1.controller;

import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.model.Location;
import mongcai1.thanhniensomongcai1.repository.CategoryRepository;
import mongcai1.thanhniensomongcai1.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * GET /api/locations - Get all active locations with optional filtering
     */
    @GetMapping
    public ResponseEntity<?> getAllLocations(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Location> locations;
            
            if (categoryId != null && search != null && !search.trim().isEmpty()) {
                // Filter by both category and search
                List<Location> searchResults = locationService.searchLocationsByCategory(categoryId, search);
                return ResponseEntity.ok(searchResults);
            } else if (categoryId != null) {
                // Filter by category only
                locations = locationService.getLocationsByCategory(categoryId, pageable);
            } else if (search != null && !search.trim().isEmpty()) {
                // Search only
                locations = locationService.searchLocations(search, pageable);
            } else {
                // Get all locations
                locations = locationService.getAllActiveLocations(pageable);
            }
            
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh sách địa điểm: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/simple - Get all locations without pagination (for dropdowns, maps)
     */
    @GetMapping("/simple")
    public ResponseEntity<?> getAllLocationsSimple(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        
        try {
            List<Location> locations;
            
            if (categoryId != null && search != null && !search.trim().isEmpty()) {
                locations = locationService.searchLocationsByCategory(categoryId, search);
            } else if (categoryId != null) {
                locations = locationService.getLocationsByCategory(categoryId);
            } else if (search != null && !search.trim().isEmpty()) {
                locations = locationService.searchLocations(search);
            } else {
                locations = locationService.getAllActiveLocations();
            }
            
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải danh sách địa điểm: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/{id} - Get location by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        try {
            Optional<Location> location = locationService.getLocationById(id);
            
            if (location.isPresent()) {
                return ResponseEntity.ok(location.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy địa điểm với ID: " + id);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải thông tin địa điểm: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/with-coordinates - Get locations that have GPS coordinates
     */
    @GetMapping("/with-coordinates")
    public ResponseEntity<?> getLocationsWithCoordinates() {
        try {
            List<Location> locations = locationService.getLocationsWithCoordinates();
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải địa điểm có tọa độ: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/with-phone - Get locations that have phone numbers
     */
    @GetMapping("/with-phone")
    public ResponseEntity<?> getLocationsWithPhone() {
        try {
            List<Location> locations = locationService.getLocationsWithPhone();
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải địa điểm có số điện thoại: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/bounds - Get locations within geographic bounds
     */
    @GetMapping("/bounds")
    public ResponseEntity<?> getLocationsWithinBounds(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        
        try {
            List<Location> locations = locationService.getLocationsWithinBounds(minLat, maxLat, minLng, maxLng);
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải địa điểm trong vùng: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/locations/category/{categoryId}/count - Get count of locations by category
     */
    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<?> getLocationCountByCategory(@PathVariable Long categoryId) {
        try {
            long count = locationService.getLocationCountByCategory(categoryId);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đếm địa điểm theo danh mục: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/locations - Create new location (Admin only)
     */
    @PostMapping
    public ResponseEntity<?> createLocation(@RequestBody Map<String, Object> request) {
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
            
            // Create Location object
            Location location = new Location();
            location.setName((String) request.get("name"));
            location.setAddress((String) request.get("address"));
            location.setDescription((String) request.get("description"));
            location.setPhone((String) request.get("phone"));
            location.setEmail((String) request.get("email"));
            location.setWebsite((String) request.get("website"));
            location.setOpeningHours((String) request.get("openingHours"));
            location.setImageUrl((String) request.get("imageUrl"));
            location.setCategory(categoryOpt.get());
            location.setIsActive(true);
            
            // Set coordinates if provided
            if (request.get("latitude") != null && !request.get("latitude").toString().isEmpty()) {
                location.setLatitude(new BigDecimal(request.get("latitude").toString()));
            }
            if (request.get("longitude") != null && !request.get("longitude").toString().isEmpty()) {
                location.setLongitude(new BigDecimal(request.get("longitude").toString()));
            }
            
            Location createdLocation = locationService.createLocation(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi tạo địa điểm mới: " + e.getMessage()));
        }
    }
    
    /**
     * PUT /api/locations/{id} - Update location (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // Find existing location
            Optional<Location> existingOpt = locationService.getLocationById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy địa điểm với ID: " + id));
            }
            
            Location location = existingOpt.get();
            
            // Update category if provided
            if (request.get("categoryId") != null) {
                Long categoryId = Long.parseLong(request.get("categoryId").toString());
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                if (categoryOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Danh mục không tồn tại"));
                }
                location.setCategory(categoryOpt.get());
            }
            
            // Update other fields
            if (request.get("name") != null) {
                location.setName((String) request.get("name"));
            }
            if (request.get("address") != null) {
                location.setAddress((String) request.get("address"));
            }
            if (request.containsKey("description")) {
                location.setDescription((String) request.get("description"));
            }
            if (request.containsKey("phone")) {
                location.setPhone((String) request.get("phone"));
            }
            if (request.containsKey("email")) {
                location.setEmail((String) request.get("email"));
            }
            if (request.containsKey("website")) {
                location.setWebsite((String) request.get("website"));
            }
            if (request.containsKey("openingHours")) {
                location.setOpeningHours((String) request.get("openingHours"));
            }
            if (request.containsKey("imageUrl")) {
                location.setImageUrl((String) request.get("imageUrl"));
            }
            
            // Update coordinates
            if (request.get("latitude") != null && !request.get("latitude").toString().isEmpty()) {
                location.setLatitude(new BigDecimal(request.get("latitude").toString()));
            }
            if (request.get("longitude") != null && !request.get("longitude").toString().isEmpty()) {
                location.setLongitude(new BigDecimal(request.get("longitude").toString()));
            }
            
            Location updatedLocation = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updatedLocation);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi cập nhật địa điểm: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/locations/{id} - Soft delete location (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.ok("Đã xóa địa điểm thành công");
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa địa điểm: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/locations/{id}/permanent - Permanently delete location (Admin only)
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteLocation(@PathVariable Long id) {
        try {
            locationService.permanentlyDeleteLocation(id);
            return ResponseEntity.ok("Đã xóa vĩnh viễn địa điểm thành công");
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa vĩnh viễn địa điểm: " + e.getMessage());
        }
    }
}