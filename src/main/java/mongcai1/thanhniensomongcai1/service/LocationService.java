package mongcai1.thanhniensomongcai1.service;

import mongcai1.thanhniensomongcai1.model.Location;
import mongcai1.thanhniensomongcai1.model.Category;
import mongcai1.thanhniensomongcai1.repository.LocationRepository;
import mongcai1.thanhniensomongcai1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all active locations
     */
    public List<Location> getAllActiveLocations() {
        return locationRepository.findByIsActiveTrue();
    }
    
    /**
     * Get all active locations with pagination
     */
    public Page<Location> getAllActiveLocations(Pageable pageable) {
        return locationRepository.findByIsActiveTrue(pageable);
    }
    
    /**
     * Get location by ID
     */
    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }
    
    /**
     * Get locations by category ID
     */
    public List<Location> getLocationsByCategory(Long categoryId) {
        return locationRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    /**
     * Get locations by category ID with pagination
     */
    public Page<Location> getLocationsByCategory(Long categoryId, Pageable pageable) {
        return locationRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }
    
    /**
     * Search locations by name or address
     */
    public List<Location> searchLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveLocations();
        }
        return locationRepository.searchByNameOrAddress(query.trim());
    }
    
    /**
     * Search locations with pagination
     */
    public Page<Location> searchLocations(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveLocations(pageable);
        }
        return locationRepository.searchByNameOrAddress(query.trim(), pageable);
    }
    
    /**
     * Search locations by category and query
     */
    public List<Location> searchLocationsByCategory(Long categoryId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getLocationsByCategory(categoryId);
        }
        return locationRepository.findByCategoryAndSearch(categoryId, query.trim());
    }
    
    /**
     * Get locations within geographic bounds
     */
    public List<Location> getLocationsWithinBounds(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return locationRepository.findWithinBounds(minLat, maxLat, minLng, maxLng);
    }
    
    /**
     * Get locations with coordinates
     */
    public List<Location> getLocationsWithCoordinates() {
        return locationRepository.findLocationsWithCoordinates();
    }
    
    /**
     * Get locations with phone numbers
     */
    public List<Location> getLocationsWithPhone() {
        return locationRepository.findLocationsWithPhone();
    }
    
    /**
     * Create new location
     */
    public Location createLocation(Location location) {
        // Validate category exists and is of type LOCATION
        if (location.getCategory() != null) {
            Optional<Category> category = categoryRepository.findById(location.getCategory().getId());
            if (category.isPresent() && category.get().getType().toString().equals("LOCATION")) {
                location.setCategory(category.get());
            } else {
                throw new IllegalArgumentException("Danh mục không hợp lệ hoặc không phải danh mục địa điểm");
            }
        }
        
        // Set default active status
        if (location.getIsActive() == null) {
            location.setIsActive(true);
        }
        
        return locationRepository.save(location);
    }
    
    /**
     * Update existing location
     */
    public Location updateLocation(Long id, Location locationDetails) {
        Optional<Location> existingLocation = locationRepository.findById(id);
        
        if (existingLocation.isPresent()) {
            Location location = existingLocation.get();
            
            // Update fields
            location.setName(locationDetails.getName());
            location.setAddress(locationDetails.getAddress());
            location.setDescription(locationDetails.getDescription());
            location.setImageUrl(locationDetails.getImageUrl());
            location.setLatitude(locationDetails.getLatitude());
            location.setLongitude(locationDetails.getLongitude());
            location.setPhone(locationDetails.getPhone());
            location.setEmail(locationDetails.getEmail());
            location.setWebsite(locationDetails.getWebsite());
            location.setOpeningHours(locationDetails.getOpeningHours());
            
            // Update category if provided
            if (locationDetails.getCategory() != null) {
                Optional<Category> category = categoryRepository.findById(locationDetails.getCategory().getId());
                if (category.isPresent() && category.get().getType().toString().equals("LOCATION")) {
                    location.setCategory(category.get());
                } else {
                    throw new IllegalArgumentException("Danh mục không hợp lệ hoặc không phải danh mục địa điểm");
                }
            }
            
            return locationRepository.save(location);
        } else {
            throw new RuntimeException("Không tìm thấy địa điểm với ID: " + id);
        }
    }
    
    /**
     * Delete location (soft delete by setting isActive to false)
     */
    public void deleteLocation(Long id) {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent()) {
            Location loc = location.get();
            loc.setIsActive(false);
            locationRepository.save(loc);
        } else {
            throw new RuntimeException("Không tìm thấy địa điểm với ID: " + id);
        }
    }
    
    /**
     * Permanently delete location
     */
    public void permanentlyDeleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy địa điểm với ID: " + id);
        }
    }
    
    /**
     * Get count of locations by category
     */
    public long getLocationCountByCategory(Long categoryId) {
        return locationRepository.countByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    /**
     * Check if location exists
     */
    public boolean locationExists(Long id) {
        return locationRepository.existsById(id);
    }
}