import React, { useState, useEffect } from 'react';
import { 
    Search, 
    MapPin, 
    Calendar,
    Building2,
    Heart,
    GraduationCap,
    ShoppingBag,
    Phone,
    Mail,
    ExternalLink,
    Star,
    ChevronRight,
    Clock,
    Eye
} from 'lucide-react';
import { postAPI, locationAPI, categoryAPI, apiUtils } from '../api/apiClient.js';

const HomePage = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [featuredPosts, setFeaturedPosts] = useState([]);
    const [urgentPosts, setUrgentPosts] = useState([]);
    const [locationCategories, setLocationCategories] = useState([]);
    const [recentLocations, setRecentLocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Quick access categories with icons and colors
    const quickAccessItems = [
        { 
            title: 'Hành chính', 
            icon: Building2, 
            description: 'Dịch vụ công, thủ tục hành chính',
            color: 'bg-blue-50 border-blue-200 hover:bg-blue-100',
            categoryId: 1
        },
        { 
            title: 'Y tế', 
            icon: Heart, 
            description: 'Bệnh viện, phòng khám, dược phẩm',
            color: 'bg-red-50 border-red-200 hover:bg-red-100',
            categoryId: 2
        },
        { 
            title: 'Giáo dục', 
            icon: GraduationCap, 
            description: 'Trường học, trung tâm đào tạo',
            color: 'bg-green-50 border-green-200 hover:bg-green-100',
            categoryId: 3
        },
        { 
            title: 'Thương mại/Chợ', 
            icon: ShoppingBag, 
            description: 'Chợ, siêu thị, dịch vụ thương mại',
            color: 'bg-yellow-50 border-yellow-200 hover:bg-yellow-100',
            categoryId: 4
        }
    ];

    // Load data on component mount
    useEffect(() => {
        loadHomePageData();
    }, []);

    const loadHomePageData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Load data in parallel
            const [
                featuredPostsData,
                urgentPostsData,
                locationCategoriesData,
                recentLocationsData
            ] = await Promise.all([
                postAPI.getFeatured(3),
                postAPI.getUrgent(),
                categoryAPI.getLocationCategories(true),
                locationAPI.getAllSimple({ size: 4 })
            ]);

            setFeaturedPosts(featuredPostsData);
            setUrgentPosts(urgentPostsData.slice(0, 2)); // Limit urgent posts
            setLocationCategories(locationCategoriesData);
            setRecentLocations(Array.isArray(recentLocationsData) ? recentLocationsData.slice(0, 4) : []);

        } catch (err) {
            console.error('Error loading homepage data:', err);
            setError(apiUtils.handleError(err));
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchTerm.trim()) {
            // Navigate to search results (you can implement routing here)
            console.log('Searching for:', searchTerm);
            // For now, we'll just log the search term
            // In a real app, you'd navigate to a search results page
        }
    };

    const handleQuickAccessClick = (categoryId) => {
        // Navigate to locations page filtered by category
        console.log('Navigate to locations with category:', categoryId);
        // In a real app, you'd use React Router to navigate
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-white flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#0056B3] mx-auto mb-4"></div>
                    <p className="text-gray-600">Đang tải dữ liệu...</p>
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
                        onClick={loadHomePageData}
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
                                <a href="#home" className="text-white bg-blue-700 px-3 py-2 rounded-md text-sm font-medium">
                                    Trang chủ
                                </a>
                                <a href="#locations" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
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

            {/* Hero Section */}
            <section id="home" className="bg-gradient-to-r from-blue-50 to-blue-100 py-16">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
                    <h1 className="text-4xl md:text-5xl font-bold text-[#1A1A1A] mb-4">
                        Thanh niên số Móng Cái 1
                    </h1>
                    <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
                        Nơi kết nối thông tin và dịch vụ cho cộng đồng khu vực Móng Cái 1
                    </p>
                    
                    {/* Search Bar */}
                    <form onSubmit={handleSearch} className="max-w-2xl mx-auto relative">
                        <div className="flex">
                            <div className="relative flex-1">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                                <input
                                    type="text"
                                    placeholder="Tìm kiếm địa điểm, tin tức..."
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
                </div>
            </section>

            {/* Urgent News Alert */}
            {urgentPosts.length > 0 && (
                <section className="bg-[#DA251D] text-white py-3">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="flex items-center">
                            <span className="bg-[#FFFF00] text-[#DA251D] px-2 py-1 rounded text-xs font-bold mr-3">
                                KHẨN CẤP
                            </span>
                            <div className="flex-1 overflow-hidden">
                                <p className="truncate">
                                    {urgentPosts[0].title}
                                </p>
                            </div>
                            <ChevronRight className="h-5 w-5 ml-2" />
                        </div>
                    </div>
                </section>
            )}

            {/* Quick Access Categories */}
            <section className="py-16 bg-white">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <h2 className="text-3xl font-bold text-[#1A1A1A] text-center mb-12">
                        Dịch vụ nhanh
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                        {quickAccessItems.map((item, index) => {
                            const IconComponent = item.icon;
                            return (
                                <div
                                    key={index}
                                    onClick={() => handleQuickAccessClick(item.categoryId)}
                                    className={`${item.color} border-2 rounded-lg p-6 text-center cursor-pointer transform hover:scale-105 transition duration-200 shadow-sm hover:shadow-md`}
                                >
                                    <IconComponent className="h-12 w-12 mx-auto mb-4 text-[#0056B3]" />
                                    <h3 className="font-semibold text-[#1A1A1A] mb-2">{item.title}</h3>
                                    <p className="text-sm text-gray-600">{item.description}</p>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </section>

            {/* Featured News Section */}
            <section className="py-16 bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center mb-12">
                        <div>
                            <h2 className="text-3xl font-bold text-[#1A1A1A] mb-4">
                                Tin tức nổi bật
                            </h2>
                            <p className="text-lg text-gray-600">
                                Cập nhật thông tin mới nhất từ khu vực
                            </p>
                        </div>
                        <a 
                            href="#news" 
                            className="flex items-center text-[#0056B3] hover:text-blue-700 font-medium"
                        >
                            Xem tất cả
                            <ChevronRight className="h-5 w-5 ml-1" />
                        </a>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {featuredPosts.map((post) => (
                            <article
                                key={post.id}
                                className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200 cursor-pointer"
                            >
                                <div className="h-48 bg-gray-200 flex items-center justify-center relative">
                                    <Calendar className="h-16 w-16 text-gray-400" />
                                    {post.isUrgent && (
                                        <div className="absolute top-2 left-2 bg-[#DA251D] text-white px-2 py-1 rounded text-xs font-bold">
                                            KHẨN CẤP
                                        </div>
                                    )}
                                </div>
                                <div className="p-6">
                                    <div className="flex items-center mb-2 text-sm text-gray-500">
                                        <Calendar className="h-4 w-4 text-[#DA251D] mr-2" />
                                        <span className="font-medium text-[#DA251D]">
                                            {formatDate(post.publishedAt)}
                                        </span>
                                        <div className="flex items-center ml-auto">
                                            <Eye className="h-4 w-4 mr-1" />
                                            <span>{post.viewCount}</span>
                                        </div>
                                    </div>
                                    <h3 className="font-bold text-[#1A1A1A] mb-3 text-lg line-clamp-2">
                                        {post.title}
                                    </h3>
                                    <p className="text-gray-600 text-sm line-clamp-3 mb-4">
                                        {post.summary}
                                    </p>
                                    <div className="flex items-center justify-between">
                                        <span className="text-xs text-gray-500">
                                            {post.author}
                                        </span>
                                        <button className="text-[#0056B3] hover:text-blue-700 font-medium text-sm flex items-center">
                                            Đọc thêm
                                            <ExternalLink className="h-4 w-4 ml-1" />
                                        </button>
                                    </div>
                                </div>
                            </article>
                        ))}
                    </div>

                    {featuredPosts.length === 0 && (
                        <div className="text-center py-12">
                            <p className="text-gray-500 text-lg">Chưa có tin tức nổi bật</p>
                        </div>
                    )}
                </div>
            </section>

            {/* Recent Locations Section */}
            {recentLocations.length > 0 && (
                <section className="py-16 bg-white">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="flex justify-between items-center mb-12">
                            <div>
                                <h2 className="text-3xl font-bold text-[#1A1A1A] mb-4">
                                    Địa điểm quan trọng
                                </h2>
                                <p className="text-lg text-gray-600">
                                    Các địa điểm dịch vụ công trong khu vực
                                </p>
                            </div>
                            <a 
                                href="#locations" 
                                className="flex items-center text-[#0056B3] hover:text-blue-700 font-medium"
                            >
                                Xem tất cả
                                <ChevronRight className="h-5 w-5 ml-1" />
                            </a>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                            {recentLocations.map((location) => (
                                <div
                                    key={location.id}
                                    className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200 cursor-pointer"
                                >
                                    <div className="h-32 bg-gray-200 flex items-center justify-center">
                                        <Building2 className="h-8 w-8 text-gray-400" />
                                    </div>
                                    <div className="p-4">
                                        <h3 className="font-semibold text-[#1A1A1A] mb-2 line-clamp-2 text-sm">
                                            {location.name}
                                        </h3>
                                        <p className="text-xs text-gray-600 mb-3 flex items-start">
                                            <MapPin className="h-3 w-3 text-[#DA251D] mr-1 mt-0.5 flex-shrink-0" />
                                            <span className="line-clamp-2">{location.address}</span>
                                        </p>
                                        {location.phone && (
                                            <p className="text-xs text-gray-600 mb-3 flex items-center">
                                                <Phone className="h-3 w-3 text-[#0056B3] mr-1" />
                                                {location.phone}
                                            </p>
                                        )}
                                        <button className="w-full bg-[#0056B3] hover:bg-blue-700 text-white py-2 px-3 rounded-md text-xs font-medium transition duration-200">
                                            Xem chi tiết
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            )}

            {/* Footer */}
            <footer className="bg-gray-800 text-white py-12">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <div>
                            <div className="flex items-center mb-4">
                                <Star className="h-8 w-8 text-[#FFFF00] mr-3" />
                                <span className="text-xl font-bold">Móng Cái 1</span>
                            </div>
                            <p className="text-gray-300 text-sm leading-relaxed">
                                Cổng thông tin chính thức của khu vực Móng Cái 1, 
                                cung cấp thông tin dịch vụ và tin tức cho cộng đồng.
                            </p>
                        </div>
                        
                        <div>
                            <h4 className="font-bold mb-4">Thông tin liên hệ</h4>
                            <div className="space-y-2 text-sm">
                                <div className="flex items-center">
                                    <MapPin className="h-4 w-4 mr-2 text-[#DA251D]" />
                                    <span className="text-gray-300">
                                        123 Đường Trần Phú, Phường Móng Cái 1, TP Móng Cái, Quảng Ninh
                                    </span>
                                </div>
                                <div className="flex items-center">
                                    <Phone className="h-4 w-4 mr-2 text-[#DA251D]" />
                                    <span className="text-gray-300">(033) 123-4567</span>
                                </div>
                                <div className="flex items-center">
                                    <Mail className="h-4 w-4 mr-2 text-[#DA251D]" />
                                    <span className="text-gray-300">info@mongcai1.gov.vn</span>
                                </div>
                            </div>
                        </div>
                        
                        <div>
                            <h4 className="font-bold mb-4">Liên kết chính thức</h4>
                            <div className="space-y-2 text-sm">
                                <a
                                    href="#"
                                    className="flex items-center text-gray-300 hover:text-white transition duration-200"
                                >
                                    <ExternalLink className="h-4 w-4 mr-2" />
                                    Cổng thông tin Quảng Ninh
                                </a>
                                <a
                                    href="#"
                                    className="flex items-center text-gray-300 hover:text-white transition duration-200"
                                >
                                    <ExternalLink className="h-4 w-4 mr-2" />
                                    UBND TP Móng Cái
                                </a>
                                <a
                                    href="#"
                                    className="flex items-center text-gray-300 hover:text-white transition duration-200"
                                >
                                    <ExternalLink className="h-4 w-4 mr-2" />
                                    Cổng dịch vụ công quốc gia
                                </a>
                            </div>
                        </div>
                    </div>
                    
                    <div className="border-t border-gray-700 mt-8 pt-8 text-center">
                        <p className="text-gray-400 text-sm">
                            © 2026 Thanh niên số Móng Cái 1. Tất cả quyền được bảo lưu.
                        </p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default HomePage;