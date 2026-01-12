import React, { useState, useEffect } from 'react';
import { 
    Search, 
    Calendar,
    Eye,
    Clock,
    User,
    Tag,
    ExternalLink,
    Star,
    ChevronLeft,
    ChevronRight,
    Filter,
    SortAsc,
    SortDesc
} from 'lucide-react';
import { postAPI, categoryAPI, apiUtils } from '../api/apiClient.js';

const NewsPage = () => {
    const [posts, setPosts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [activeCategory, setActiveCategory] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState('publishedAt');
    const [sortDir, setSortDir] = useState('desc');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showFilters, setShowFilters] = useState(false);

    const pageSize = 9; // Number of posts per page

    // Load data on component mount
    useEffect(() => {
        loadCategories();
    }, []);

    // Load posts when filters change
    useEffect(() => {
        loadPosts();
    }, [currentPage, activeCategory, searchTerm, sortBy, sortDir]);

    const loadCategories = async () => {
        try {
            const categoriesData = await categoryAPI.getPostCategories(true);
            setCategories([
                { id: 'all', name: 'Tất cả' },
                ...categoriesData
            ]);
        } catch (err) {
            console.error('Error loading categories:', err);
        }
    };

    const loadPosts = async () => {
        try {
            setLoading(true);
            setError(null);

            const params = {
                page: currentPage,
                size: pageSize,
                sortBy,
                sortDir
            };

            // Add filters
            if (activeCategory !== 'all') {
                params.categoryId = activeCategory;
            }

            if (searchTerm.trim()) {
                params.search = searchTerm.trim();
            }

            const response = await postAPI.getAll(params);
            
            setPosts(response.content || []);
            setTotalPages(response.totalPages || 0);
            setTotalElements(response.totalElements || 0);

        } catch (err) {
            console.error('Error loading posts:', err);
            setError(apiUtils.handleError(err));
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setCurrentPage(0); // Reset to first page
        // Posts will be loaded by useEffect
    };

    const handleCategoryChange = (categoryId) => {
        setActiveCategory(categoryId);
        setCurrentPage(0);
        setShowFilters(false);
    };

    const handleSortChange = (newSortBy) => {
        if (sortBy === newSortBy) {
            // Toggle sort direction
            setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(newSortBy);
            setSortDir(newSortBy === 'publishedAt' ? 'desc' : 'asc');
        }
        setCurrentPage(0);
    };

    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handlePostClick = (postId) => {
        // Navigate to post detail page
        console.log('Navigate to post:', postId);
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

    const formatDateTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const truncateContent = (content, maxLength = 150) => {
        if (!content) return '';
        const textContent = content.replace(/<[^>]*>/g, ''); // Remove HTML tags
        return textContent.length > maxLength ? 
            textContent.substring(0, maxLength) + '...' : 
            textContent;
    };

    // Generate pagination numbers
    const getPaginationNumbers = () => {
        const delta = 2;
        const range = [];
        const rangeWithDots = [];

        for (let i = Math.max(0, currentPage - delta); 
             i <= Math.min(totalPages - 1, currentPage + delta); 
             i++) {
            range.push(i);
        }

        if (range[0] > 0) {
            if (range[0] > 1) {
                rangeWithDots.push(0, '...');
            } else {
                rangeWithDots.push(0);
            }
        }

        rangeWithDots.push(...range);

        if (range[range.length - 1] < totalPages - 1) {
            if (range[range.length - 1] < totalPages - 2) {
                rangeWithDots.push('...', totalPages - 1);
            } else {
                rangeWithDots.push(totalPages - 1);
            }
        }

        return rangeWithDots;
    };

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
                                <a href="#locations" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
                                    Địa điểm
                                </a>
                                <a href="#news" className="text-white bg-blue-700 px-3 py-2 rounded-md text-sm font-medium">
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
                            Tin tức & Thông báo
                        </h1>
                    </div>
                    <p className="text-lg text-gray-600 mb-8">
                        Cập nhật thông tin mới nhất từ khu vực Móng Cái 1
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
                                        placeholder="Tìm kiếm tin tức theo tiêu đề hoặc nội dung..."
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
                                {categories.map((category) => (
                                    <button
                                        key={category.id}
                                        onClick={() => handleCategoryChange(category.id)}
                                        className={`flex items-center px-4 py-2 rounded-full text-sm font-medium transition duration-200 ${
                                            activeCategory.toString() === category.id.toString()
                                                ? 'bg-[#0056B3] text-white'
                                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                        }`}
                                    >
                                        <Tag className="h-4 w-4 mr-2" />
                                        {category.name}
                                    </button>
                                ))}
                            </div>

                            {/* Sort Controls */}
                            <div className="flex items-center space-x-2">
                                <span className="text-sm text-gray-600">Sắp xếp:</span>
                                <div className="flex space-x-1">
                                    <button
                                        onClick={() => handleSortChange('publishedAt')}
                                        className={`flex items-center px-3 py-1 rounded text-sm ${
                                            sortBy === 'publishedAt' 
                                                ? 'bg-[#0056B3] text-white' 
                                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                        }`}
                                    >
                                        <Calendar className="h-4 w-4 mr-1" />
                                        Ngày
                                        {sortBy === 'publishedAt' && (
                                            sortDir === 'desc' ? <SortDesc className="h-4 w-4 ml-1" /> : <SortAsc className="h-4 w-4 ml-1" />
                                        )}
                                    </button>
                                    <button
                                        onClick={() => handleSortChange('viewCount')}
                                        className={`flex items-center px-3 py-1 rounded text-sm ${
                                            sortBy === 'viewCount' 
                                                ? 'bg-[#0056B3] text-white' 
                                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                        }`}
                                    >
                                        <Eye className="h-4 w-4 mr-1" />
                                        Lượt xem
                                        {sortBy === 'viewCount' && (
                                            sortDir === 'desc' ? <SortDesc className="h-4 w-4 ml-1" /> : <SortAsc className="h-4 w-4 ml-1" />
                                        )}
                                    </button>
                                </div>
                            </div>
                        </div>

                        {/* Results Count */}
                        <div className="mt-4 text-sm text-gray-600">
                            Tìm thấy <span className="font-semibold">{totalElements}</span> bài viết
                            {searchTerm && ` cho "${searchTerm}"`}
                            {activeCategory !== 'all' && ` trong danh mục "${categories.find(c => c.id.toString() === activeCategory.toString())?.name}"`}
                        </div>
                    </div>
                </div>
            </section>

            {/* Posts Grid */}
            <section className="py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    {loading ? (
                        <div className="flex items-center justify-center py-20">
                            <div className="text-center">
                                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#0056B3] mx-auto mb-4"></div>
                                <p className="text-gray-600">Đang tải tin tức...</p>
                            </div>
                        </div>
                    ) : error ? (
                        <div className="text-center py-12">
                            <div className="text-red-500 text-6xl mb-4">⚠️</div>
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">Lỗi tải dữ liệu</h3>
                            <p className="text-gray-600 mb-4">{error}</p>
                            <button 
                                onClick={loadPosts}
                                className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition duration-200"
                            >
                                Thử lại
                            </button>
                        </div>
                    ) : posts.length > 0 ? (
                        <>
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                                {posts.map((post) => (
                                    <article
                                        key={post.id}
                                        onClick={() => handlePostClick(post.id)}
                                        className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200 cursor-pointer"
                                    >
                                        <div className="h-48 bg-gray-200 flex items-center justify-center relative">
                                            <Calendar className="h-16 w-16 text-gray-400" />
                                            {post.isUrgent && (
                                                <div className="absolute top-2 left-2 bg-[#DA251D] text-white px-2 py-1 rounded text-xs font-bold">
                                                    KHẨN CẤP
                                                </div>
                                            )}
                                            {post.isFeatured && (
                                                <div className="absolute top-2 right-2 bg-[#FFFF00] text-[#1A1A1A] px-2 py-1 rounded text-xs font-bold">
                                                    NỔI BẬT
                                                </div>
                                            )}
                                        </div>
                                        <div className="p-6">
                                            <div className="flex items-center justify-between mb-2 text-sm">
                                                <div className="flex items-center text-[#DA251D]">
                                                    <Calendar className="h-4 w-4 mr-1" />
                                                    <span className="font-medium">
                                                        {formatDate(post.publishedAt)}
                                                    </span>
                                                </div>
                                                <div className="flex items-center text-gray-500">
                                                    <Eye className="h-4 w-4 mr-1" />
                                                    <span>{post.viewCount}</span>
                                                </div>
                                            </div>
                                            
                                            <h3 className="font-bold text-[#1A1A1A] mb-3 text-lg line-clamp-2">
                                                {post.title}
                                            </h3>
                                            
                                            <p className="text-gray-600 text-sm line-clamp-3 mb-4">
                                                {post.summary || truncateContent(post.content)}
                                            </p>
                                            
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center text-xs text-gray-500">
                                                    <User className="h-3 w-3 mr-1" />
                                                    <span>{post.author}</span>
                                                </div>
                                                <div className="flex items-center text-xs text-gray-500">
                                                    <Tag className="h-3 w-3 mr-1" />
                                                    <span>{post.category?.name}</span>
                                                </div>
                                            </div>
                                            
                                            <div className="mt-4 pt-4 border-t border-gray-100">
                                                <button className="text-[#0056B3] hover:text-blue-700 font-medium text-sm flex items-center">
                                                    Đọc thêm
                                                    <ExternalLink className="h-4 w-4 ml-1" />
                                                </button>
                                            </div>
                                        </div>
                                    </article>
                                ))}
                            </div>

                            {/* Pagination */}
                            {totalPages > 1 && (
                                <div className="mt-12 flex justify-center">
                                    <nav className="flex items-center space-x-2">
                                        <button
                                            onClick={() => handlePageChange(currentPage - 1)}
                                            disabled={currentPage === 0}
                                            className={`flex items-center px-3 py-2 rounded-lg text-sm font-medium transition duration-200 ${
                                                currentPage === 0
                                                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                                                    : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
                                            }`}
                                        >
                                            <ChevronLeft className="h-4 w-4 mr-1" />
                                            Trước
                                        </button>

                                        {getPaginationNumbers().map((pageNum, index) => (
                                            pageNum === '...' ? (
                                                <span key={index} className="px-3 py-2 text-gray-500">...</span>
                                            ) : (
                                                <button
                                                    key={index}
                                                    onClick={() => handlePageChange(pageNum)}
                                                    className={`px-3 py-2 rounded-lg text-sm font-medium transition duration-200 ${
                                                        currentPage === pageNum
                                                            ? 'bg-[#0056B3] text-white'
                                                            : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
                                                    }`}
                                                >
                                                    {pageNum + 1}
                                                </button>
                                            )
                                        ))}

                                        <button
                                            onClick={() => handlePageChange(currentPage + 1)}
                                            disabled={currentPage === totalPages - 1}
                                            className={`flex items-center px-3 py-2 rounded-lg text-sm font-medium transition duration-200 ${
                                                currentPage === totalPages - 1
                                                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                                                    : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
                                            }`}
                                        >
                                            Sau
                                            <ChevronRight className="h-4 w-4 ml-1" />
                                        </button>
                                    </nav>
                                </div>
                            )}
                        </>
                    ) : (
                        <div className="text-center py-12">
                            <div className="text-gray-400 text-6xl mb-4">📰</div>
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">
                                Không tìm thấy bài viết
                            </h3>
                            <p className="text-gray-600 mb-4">
                                {searchTerm ? 
                                    `Không có bài viết nào phù hợp với "${searchTerm}"` :
                                    'Không có bài viết nào trong danh mục này'
                                }
                            </p>
                            <button 
                                onClick={() => {
                                    setSearchTerm('');
                                    setActiveCategory('all');
                                    setCurrentPage(0);
                                }}
                                className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition duration-200"
                            >
                                Xem tất cả tin tức
                            </button>
                        </div>
                    )}
                </div>
            </section>
        </div>
    );
};

export default NewsPage;