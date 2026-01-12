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
  Plus,
  X,
  Filter,
  ChevronDown,
  Star
} from 'lucide-react';

const MongCaiPortal = () => {
  const [activeCategory, setActiveCategory] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [isAdminModalOpen, setIsAdminModalOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // Mock data for locations
  const locations = [
    {
      id: 1,
      title: "UBND Phường Móng Cái 1",
      address: "123 Đường Trần Phú, Phường Móng Cái 1, TP Móng Cái",
      category: "hanh_chinh",
      image: "/api/placeholder/300/200"
    },
    {
      id: 2,
      title: "Trạm Y tế Phường Móng Cái 1",
      address: "45 Đường Lê Lợi, Phường Móng Cái 1, TP Móng Cái",
      category: "y_te",
      image: "/api/placeholder/300/200"
    },
    {
      id: 3,
      title: "Trường Tiểu học Móng Cái 1",
      address: "67 Đường Nguyễn Trãi, Phường Móng Cái 1, TP Móng Cái",
      category: "giao_duc",
      image: "/api/placeholder/300/200"
    },
    {
      id: 4,
      title: "Chợ Móng Cái",
      address: "89 Đường Hùng Vương, Phường Móng Cái 1, TP Móng Cái",
      category: "thuong_mai",
      image: "/api/placeholder/300/200"
    }
  ];

  // Mock data for news
  const newsItems = [
    {
      id: 1,
      title: "Thông báo sáp nhập khu vực 2026",
      description: "Chi tiết về kế hoạch sáp nhập và tái cơ cấu hành chính của khu vực Móng Cái 1 trong năm 2026",
      date: "08/01/2026",
      urgent: true,
      image: "/api/placeholder/300/200"
    },
    {
      id: 2,
      title: "Lịch tiêm chủng mở rộng",
      description: "Chương trình tiêm chủng miễn phí cho trẻ em và người cao tuổi tại các trạm y tế trong khu vực",
      date: "07/01/2026",
      urgent: false,
      image: "/api/placeholder/300/200"
    },
    {
      id: 3,
      title: "Hoạt động Đoàn thanh niên phường",
      description: "Các hoạt động tình nguyện và phát triển cộng đồng của Đoàn thanh niên phường Móng Cái 1",
      date: "05/01/2026",
      urgent: false,
      image: "/api/placeholder/300/200"
    }
  ];

  const categories = [
    { id: 'all', name: 'Tất cả', icon: Building2 },
    { id: 'hanh_chinh', name: 'Hành chính', icon: Building2 },
    { id: 'y_te', name: 'Y tế', icon: Heart },
    { id: 'giao_duc', name: 'Giáo dục', icon: GraduationCap },
    { id: 'thuong_mai', name: 'Thương mại/Chợ', icon: ShoppingBag }
  ];

  const quickAccessItems = [
    { 
      title: 'Hành chính', 
      icon: Building2, 
      description: 'Dịch vụ công, thủ tục hành chính',
      color: 'bg-blue-50 border-blue-200 hover:bg-blue-100'
    },
    { 
      title: 'Y tế', 
      icon: Heart, 
      description: 'Bệnh viện, phòng khám, dược phẩm',
      color: 'bg-red-50 border-red-200 hover:bg-red-100'
    },
    { 
      title: 'Giáo dục', 
      icon: GraduationCap, 
      description: 'Trường học, trung tâm đào tạo',
      color: 'bg-green-50 border-green-200 hover:bg-green-100'
    },
    { 
      title: 'Thương mại/Chợ', 
      icon: ShoppingBag, 
      description: 'Chợ, siêu thị, dịch vụ thương mại',
      color: 'bg-yellow-50 border-yellow-200 hover:bg-yellow-100'
    }
  ];

  const filteredLocations = locations.filter(location => {
    const matchesCategory = activeCategory === 'all' || location.category === activeCategory;
    const matchesSearch = location.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         location.address.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  // Close mobile menu when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      setIsMobileMenuOpen(false);
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

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
            
            {/* Desktop Navigation */}
            <div className="hidden md:block">
              <div className="ml-10 flex items-baseline space-x-4">
                <a href="#home" className="text-white hover:bg-blue-700 px-3 py-2 rounded-md text-sm font-medium transition duration-200">
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

            {/* Mobile menu button */}
            <div className="md:hidden">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setIsMobileMenuOpen(!isMobileMenuOpen);
                }}
                className="text-white hover:text-gray-300 focus:outline-none focus:text-gray-300"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Navigation Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden bg-[#0056B3] border-t border-blue-600">
            <div className="px-2 pt-2 pb-3 space-y-1">
              <a href="#home" className="text-white block px-3 py-2 rounded-md text-base font-medium hover:bg-blue-700">
                Trang chủ
              </a>
              <a href="#locations" className="text-white block px-3 py-2 rounded-md text-base font-medium hover:bg-blue-700">
                Địa điểm
              </a>
              <a href="#news" className="text-white block px-3 py-2 rounded-md text-base font-medium hover:bg-blue-700">
                Tin tức
              </a>
            </div>
          </div>
        )}
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
          <div className="max-w-2xl mx-auto relative">
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
              <button className="bg-[#0056B3] hover:bg-blue-700 text-white px-6 py-3 rounded-r-lg transition duration-200">
                Tìm kiếm
              </button>
            </div>
          </div>
        </div>
      </section>

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

      {/* Location Directory */}
      <section id="locations" className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-[#1A1A1A] mb-4">
              Danh bạ địa điểm
            </h2>
            <p className="text-lg text-gray-600">
              Tìm kiếm các địa điểm quan trọng trong khu vực
            </p>
          </div>

          {/* Category Filter */}
          <div className="flex flex-wrap justify-center mb-8 gap-2">
            {categories.map((category) => {
              const IconComponent = category.icon;
              return (
                <button
                  key={category.id}
                  onClick={() => setActiveCategory(category.id)}
                  className={`flex items-center px-4 py-2 rounded-full text-sm font-medium transition duration-200 ${
                    activeCategory === category.id
                      ? 'bg-[#0056B3] text-white'
                      : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
                  }`}
                >
                  <IconComponent className="h-4 w-4 mr-2" />
                  {category.name}
                </button>
              );
            })}
          </div>

          {/* Location Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredLocations.map((location) => (
              <div
                key={location.id}
                className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200"
              >
                <div className="h-48 bg-gray-200 flex items-center justify-center">
                  <Building2 className="h-16 w-16 text-gray-400" />
                </div>
                <div className="p-4">
                  <h3 className="font-semibold text-[#1A1A1A] mb-2 line-clamp-2">
                    {location.title}
                  </h3>
                  <p className="text-sm text-gray-600 mb-4 flex items-start">
                    <MapPin className="h-4 w-4 text-[#DA251D] mr-1 mt-0.5 flex-shrink-0" />
                    {location.address}
                  </p>
                  <button className="w-full bg-[#0056B3] hover:bg-blue-700 text-white py-2 px-4 rounded-md text-sm font-medium transition duration-200">
                    Xem chỉ đường
                  </button>
                </div>
              </div>
            ))}
          </div>

          {filteredLocations.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-500 text-lg">Không tìm thấy địa điểm phù hợp</p>
            </div>
          )}
        </div>
      </section>

      {/* News Section */}
      <section id="news" className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-12">
            <div>
              <h2 className="text-3xl font-bold text-[#1A1A1A] mb-4">
                Tin tức & Thông báo
              </h2>
              <p className="text-lg text-gray-600">
                Cập nhật thông tin mới nhất từ khu vực
              </p>
            </div>
            
            {/* Admin Add Post Button */}
            <button
              onClick={() => setIsAdminModalOpen(true)}
              className="flex items-center bg-[#0056B3] hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition duration-200"
            >
              <Plus className="h-5 w-5 mr-2" />
              Thêm bài viết
            </button>
          </div>

          {/* News Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {newsItems.map((news) => (
              <article
                key={news.id}
                className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition duration-200 cursor-pointer"
              >
                <div className="h-48 bg-gray-200 flex items-center justify-center relative">
                  <Calendar className="h-16 w-16 text-gray-400" />
                  {news.urgent && (
                    <div className="absolute top-2 left-2 bg-[#DA251D] text-white px-2 py-1 rounded text-xs font-bold">
                      KHẨN CẤP
                    </div>
                  )}
                </div>
                <div className="p-6">
                  <div className="flex items-center mb-2">
                    <Calendar className="h-4 w-4 text-[#DA251D] mr-2" />
                    <span className="text-sm font-medium text-[#DA251D]">
                      {news.date}
                    </span>
                  </div>
                  <h3 className="font-bold text-[#1A1A1A] mb-3 text-lg line-clamp-2">
                    {news.title}
                  </h3>
                  <p className="text-gray-600 text-sm line-clamp-3 mb-4">
                    {news.description}
                  </p>
                  <button className="text-[#0056B3] hover:text-blue-700 font-medium text-sm flex items-center">
                    Đọc thêm
                    <ExternalLink className="h-4 w-4 ml-1" />
                  </button>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>

      {/* Admin Modal */}
      {isAdminModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center p-6 border-b">
              <h3 className="text-xl font-bold text-[#1A1A1A]">
                Thêm bài viết mới
              </h3>
              <button
                onClick={() => setIsAdminModalOpen(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-[#1A1A1A] mb-2">
                  Tiêu đề bài viết
                </label>
                <input
                  type="text"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#0056B3]"
                  placeholder="Nhập tiêu đề..."
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-[#1A1A1A] mb-2">
                  Mô tả ngắn
                </label>
                <textarea
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#0056B3]"
                  placeholder="Nhập mô tả..."
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-[#1A1A1A] mb-2">
                  Nội dung chi tiết
                </label>
                <textarea
                  rows={6}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#0056B3]"
                  placeholder="Nhập nội dung..."
                />
              </div>
              <div className="flex items-center space-x-2">
                <input
                  type="checkbox"
                  id="urgent"
                  className="rounded focus:ring-[#0056B3]"
                />
                <label htmlFor="urgent" className="text-sm text-[#1A1A1A]">
                  Đánh dấu là thông báo khẩn cấp
                </label>
              </div>
              <div className="flex justify-end space-x-4 pt-4">
                <button
                  onClick={() => setIsAdminModalOpen(false)}
                  className="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Hủy
                </button>
                <button className="px-4 py-2 bg-[#0056B3] hover:bg-blue-700 text-white rounded-md">
                  Đăng bài
                </button>
              </div>
            </div>
          </div>
        </div>
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

export default MongCaiPortal;