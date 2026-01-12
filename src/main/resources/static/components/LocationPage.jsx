import React, { useState, useEffect } from 'react';
import { 
    Search, 
    MapPin, 
    Building2,
    Heart,
    GraduationCap,
    ShoppingBag,
    Phone,
    Mail,
    Clock,
    ExternalLink,
    Star,
    Filter,
    Grid,
    List,
    Navigation,
    Eye,
    ChevronLeft
} from 'lucide-react';
import { locationAPI, categoryAPI, apiUtils } from '../api/apiClient.js';

const LocationPage = () => {
    const [locations, setLocations] = useState([]);
    const [categories, setCategories] = useState([]);
    const [filteredLocations, setFilteredLocations] = useState([]);
    const [activeCategory, setActiveCategory] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showFilters, setShowFilters] = useState(false);

    // Load data on component mount
    useEffect(() => {
        loadLocationData();
    }, []);

    // Filter locations when category or search term changes
    useEffect(() => {
        filterLocations();
    }, [locations, activeCategory, searchTerm]);

    const loadLocationData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Load locations and categories in parallel
            const [locationsData, categoriesData] = await Promise.all([
                locationAPI.getAllSimple(),
                categoryAPI.getLocationCategories(true)
            ]);

            setLocations(locationsData);
            setCategories([
                { id: 'all', name: 'Tất cả', icon: 'Building2' },
                ...categoriesData
            ]);

        } catch (err) {
            console.error('Error loading location data:', err);
            setError(apiUtils.handleError(err));
        } finally {
            setLoading(false);
        }
    };

    const filterLocations = () => {
        let filtered = locations;

        // Filter by category
        if (activeCategory !== 'all') {
            filtered = filtered.filter(location => 
                location.category && location.category.id.toString() === activeCategory.toString()
            );
        }

        // Filter by search term
        if (searchTerm.trim()) {
            const searchLower = searchTerm.toLowerCase().trim();
            filtered = filtered.filter(location =>
                location.name.toLowerCase().includes(searchLower) ||
                location.address.toLowerCase().includes(searchLower) ||
                (location.description && location.description.toLowerCase().includes(searchLower))
            );
        }

        setFilteredLocations(filtered);
    };

    const handleSearch = (e) => {
        e.preventDefault();
        // Search is handled by useEffect
    };

    const handleCategoryChange = (categoryId) => {
        setActiveCategory(categoryId);
        setShowFilters(false);
    };

    const handleGetDirections = (location) => {
        if (location.latitude && location.longitude) {
            // Open Google Maps with directions
            const url = `https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}`;
            window.open(url, '_blank');
        } else {
            // Search by address
            const encodedAddress = encodeURIComponent(location.address);
            const url = `https://www.google.com/maps/search/?api=1&query=${encodedAddress}`;
            window.open(url, '_blank');
        }
    };

    const handleViewOnMap = (location) => {
        if (location.latitude && location.longitude) {
            // Open Google Maps at location
            const url = `https://www.google.com/maps/@${location.latitude},${location.longitude},17z`;
            window.open(url, '_blank');
        } else {
            // Search by address
            const encodedAddress = encodeURIComponent(location.address);
            const url = `https://www.google.com/maps/search/?api=1&query=${encodedAddress}`;
            window.open(url, '_blank');
        }
    };

    const getIconComponent = (iconName) => {
        const icons = {
            Building2,
            Heart,
            GraduationCap,
            ShoppingBag,
            MapPin
        };
        return icons[iconName] || Building2;
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-white">
                {/* Navigation */}
                <nav className="sticky top-0 z-50 bg-[#0056B3] shadow-lg">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="flex justify-between items-center h-16">
                            <div className="flex items-center">
                                <Star className="h-8 w-8 text-[#FFFF00] mr-3" />
                                <span className="text-white text-xl font-bold">Móng Cái 1</span>
                            </div>
                            
                            <div className="hidden md:block">
                                <div className="ml-10 flex items-baseline space-x-4">
                                    <a href="#home" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
                                        Trang chủ
                                    </a>
                                    <a href="#locations" className="text-white bg-blue-700 px-3 py-2 rounded-md text-sm font-medium">
                                        Địa điểm
                                    </a>
                                    <a href="#news" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
                                        Tin tức
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </nav>

                <div className="flex items-center justify-center py-20">
                    <div className="text-center">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#0056B3] mx-auto mb-4"></div>
                        <p className="text-gray-600">Đang tải danh sách địa điểm...</p>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-white flex items-center justify-center">
                <div className="text-center">
                    <div className="text-red-500 text-6xl mb-4">⚠️</div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-2">Lỗi tải dữ liệu</h2>
                    <p className="text-gray-600 mb-4">{error}</p>
                    <button 
                        onClick={loadLocationData}
                        className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition duration-200"
                    >
                        Thử lại
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white">
            {/* Navigation */}
            <nav className="sticky top-0 z-50 bg-[#0056B3] shadow-lg">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <Star className="h-8 w-8 text-[#FFFF00] mr-3" />
                            <span className="text-white text-xl font-bold">Móng Cái 1</span>
                        </div>
                        
                        <div className="hidden md:block">
                            <div className="ml-10 flex items-baseline space-x-4">
                                <a href="#home" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
                                    Trang chủ
                                </a>
                                <a href="#locations" className="text-white bg-blue-700 px-3 py-2 rounded-md text-sm font-medium">
                                    Địa điểm
                                </a>
                                <a href="#news" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
                                    Tin tức
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Page Header */}
            <section className="bg-gradient-to-r from-blue-50 to-blue-100 py-12">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center mb-6">
                        <button 
                            onClick={() => window.history.back()}
                            className="flex items-center text-[#0056B3] hover:text-blue-700 mr-4"
                        >
                            <ChevronLeft className="h-5 w-5 mr-1" />
                            Quay lại
                        </button>
                        <h1 className="text-3xl md:text-4xl font-bold text-[#1A1A1A]">
                            Danh bạ địa điểm
                        </h1>
                    </div>
                    <p className="text-lg text-gray-600 mb-8">
                        Tìm kiếm các địa điểm quan trọng trong khu vực Móng Cái 1
                    </p>

                    {/* Search and Filters */}
                    <div className="bg-white rounded-lg shadow-md p-6">
                        {/* Search Bar */}
                        <form onSubmit={handleSearch} className="mb-4">
                            <div className="flex">
                                <div className="relative flex-1">
                                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                                    <input
                                        type="text"
                                        placeholder="Tìm kiếm theo tên địa điểm hoặc địa chỉ..."
                                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-[#0056B3] focus:border-transparent"
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                    />
                                </div>
                                <button 
                                    type="submit"
                                    className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-3 rounded-r-lg transition duration-200"
                                >
                                    Tìm kiếm
                                </button>
                            </div>
                        </form>

                        {/* Filter Controls */}
                        <div className="flex flex-wrap items-center justify-between gap-4">
                            {/* Category Filters */}
                            <div className="flex flex-wrap gap-2">
                                {categories.map((category) => {
                                    const IconComponent = getIconComponent(category.icon);
                                    return (
                                        <button
                                            key={category.id}
                                            onClick={() => handleCategoryChange(category.id)}
                                            className={`flex items-center px-4 py-2 rounded-full text-sm font-medium transition duration-200 ${
                                                activeCategory.toString() === category.id.toString()
                                                    ? 'bg-[#0056B3] text-white'
                                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                            }`}
                                        >
                                            <IconComponent className="h-4 w-4 mr-2" />
                                            {category.name}
                                        </button>
                                    );
                                })}
                            </div>

                            {/* View Mode Toggle */}
                            <div className="flex items-center space-x-2">
                                <span className="text-sm text-gray-600">Hiển thị:</span>
                                <div className="flex border border-gray-300 rounded-lg overflow-hidden">
                                    <button
                                        onClick={() => setViewMode('grid')}
                                        className={`p-2 ${viewMode === 'grid' ? 'bg-[#0056B3] text-white' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
                                    >
                                        <Grid className="h-4 w-4" />
                                    </button>
                                    <button
                                        onClick={() => setViewMode('list')}
                                        className={`p-2 ${viewMode === 'list' ? 'bg-[#0056B3] text-white' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
                                    >
                                        <List className="h-4 w-4" />
                                    </button>
                                </div>
                            </div>
                        </div>

                        {/* Results Count */}
                        <div className="mt-4 text-sm text-gray-600">
                            Tìm thấy <span className="font-semibold">{filteredLocations.length}</span> địa điểm
                            {searchTerm && ` cho "${searchTerm}"`}
                            {activeCategory !== 'all' && ` trong danh mục "${categories.find(c => c.id.toString() === activeCategory.toString())?.name}"`}
                        </div>
                    </div>
                </div>
            </section>

            {/* Locations Grid/List */}
            <section className="py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    {filteredLocations.length > 0 ? (
                        <div className={viewMode === 'grid' ? 
                            'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6' : 
                            'space-y-6'
                        }>
                            {filteredLocations.map((location) => {
                                const IconComponent = getIconComponent(location.category?.icon);
                                
                                if (viewMode === 'list') {
                                    return (
                                        <div
                                            key={location.id}
                                            className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200 border border-gray-200"
                                        >
                                            <div className="flex">
                                                <div className="w-48 h-32 bg-gray-200 flex items-center justify-center flex-shrink-0">
                                                    <IconComponent className="h-12 w-12 text-gray-400" />
                                                </div>
                                                <div className="flex-1 p-6">
                                                    <div className="flex items-start justify-between mb-2">
                                                        <h3 className="font-semibold text-[#1A1A1A] text-lg">
                                                            {location.name}
                                                        </h3>
                                                        <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
                                                            {location.category?.name}
                                                        </span>
                                                    </div>
                                                    
                                                    <div className="space-y-2 mb-4">
                                                        <p className="text-sm text-gray-600 flex items-start">
                                                            <MapPin className="h-4 w-4 text-[#DA251D] mr-2 mt-0.5 flex-shrink-0" />
                                                            {location.address}
                                                        </p>
                                                        
                                                        {location.phone && (
                                                            <p className="text-sm text-gray-600 flex items-center">
                                                                <Phone className="h-4 w-4 text-[#0056B3] mr-2" />
                                                                {location.phone}
                                                            </p>
                                                        )}
                                                        
                                                        {location.email && (
                                                            <p className="text-sm text-gray-600 flex items-center">
                                                                <Mail className="h-4 w-4 text-[#0056B3] mr-2" />
                                                                {location.email}
                                                            </p>
                                                        )}
                                                        
                                                        {location.openingHours && (
                                                            <p className="text-sm text-gray-600 flex items-start">
                                                                <Clock className="h-4 w-4 text-[#0056B3] mr-2 mt-0.5 flex-shrink-0" />
                                                                {location.openingHours}
                                                            </p>
                                                        )}
                                                    </div>
                                                    
                                                    <div className="flex space-x-2">
                                                        <button 
                                                            onClick={() => handleViewOnMap(location)}
                                                            className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2 px-4 rounded-md text-sm font-medium transition duration-200 flex items-center justify-center"
                                                        >
                                                            <Eye className="h-4 w-4 mr-1" />
                                                            Xem bản đồ
                                                        </button>
                                                        <button 
                                                            onClick={() => handleGetDirections(location)}
                                                            className="flex-1 bg-[#0056B3] hover:bg-blue-700 text-white py-2 px-4 rounded-md text-sm font-medium transition duration-200 flex items-center justify-center"
                                                        >
                                                            <Navigation className="h-4 w-4 mr-1" />
                                                            Chỉ đường
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                }

                                // Grid view
                                return (
                                    <div
                                        key={location.id}
                                        className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200"
                                    >
                                        <div className="h-48 bg-gray-200 flex items-center justify-center relative">
                                            <IconComponent className="h-16 w-16 text-gray-400" />
                                            <div className="absolute top-2 right-2 bg-white bg-opacity-90 px-2 py-1 rounded text-xs font-medium text-gray-700">
                                                {location.category?.name}
                                            </div>
                                        </div>
                                        <div className="p-4">
                                            <h3 className="font-semibold text-[#1A1A1A] mb-2 line-clamp-2">
                                                {location.name}
                                            </h3>
                                            
                                            <div className="space-y-2 mb-4">
                                                <p className="text-sm text-gray-600 flex items-start">
                                                    <MapPin className="h-4 w-4 text-[#DA251D] mr-1 mt-0.5 flex-shrink-0" />
                                                    <span className="line-clamp-2">{location.address}</span>
                                                </p>
                                                
                                                {location.phone && (
                                                    <p className="text-sm text-gray-600 flex items-center">
                                                        <Phone className="h-4 w-4 text-[#0056B3] mr-1" />
                                                        {location.phone}
                                                    </p>
                                                )}
                                            </div>
                                            
                                            <div className="space-y-2">
                                                <button 
                                                    onClick={() => handleViewOnMap(location)}
                                                    className="w-full bg-gray-100 hover:bg-gray-200 text-gray-700 py-2 px-4 rounded-md text-sm font-medium transition duration-200 flex items-center justify-center"
                                                >
                                                    <Eye className="h-4 w-4 mr-1" />
                                                    Xem bản đồ
                                                </button>
                                                <button 
                                                    onClick={() => handleGetDirections(location)}
                                                    className="w-full bg-[#0056B3] hover:bg-blue-700 text-white py-2 px-4 rounded-md text-sm font-medium transition duration-200 flex items-center justify-center"
                                                >
                                                    <Navigation className="h-4 w-4 mr-1" />
                                                    Chỉ đường
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    ) : (
                        <div className="text-center py-12">
                            <div className="text-gray-400 text-6xl mb-4">📍</div>
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">
                                Không tìm thấy địa điểm
                            </h3>
                            <p className="text-gray-600 mb-4">
                                {searchTerm ? 
                                    `Không có địa điểm nào phù hợp với "${searchTerm}"` :
                                    'Không có địa điểm nào trong danh mục này'
                                }
                            </p>
                            <button 
                                onClick={() => {
                                    setSearchTerm('');
                                    setActiveCategory('all');
                                }}
                                className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition duration-200"
                            >
                                Xem tất cả địa điểm
                            </button>
                        </div>
                    )}
                </div>
            </section>
        </div>
    );
};

export default LocationPage;