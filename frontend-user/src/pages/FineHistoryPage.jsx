import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/FineHistoryPage.css';

const FineHistoryPage = () => {
  const [fines, setFines] = useState([]);
  const [filteredFines, setFilteredFines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('newest');
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      navigate('/login');
      return;
    }

    let cancelled = false;
    setLoading(true);
    setError(null);

    axios
      .get(`http://localhost:8086/fines/user/${userId}`)
      .then(async (response) => {
        if (cancelled) return;

        const fineData = response.data || [];
        
        // Fetch book details for each fine
        const finesWithBooks = await Promise.all(
          fineData.map(async (fine) => {
            try {
              const bookResponse = await axios.get(`http://localhost:8082/books/${fine.bookId}`);
              return {
                ...fine,
                book: bookResponse.data
              };
            } catch (err) {
              return {
                ...fine,
                book: null
              };
            }
          })
        );

        if (!cancelled) {
          setFines(finesWithBooks);
          setFilteredFines(finesWithBooks);
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError('Không thể tải lịch sử phạt');
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [navigate]);

  // Filter and sort fines
  useEffect(() => {
    let result = [...fines];

    // Apply search filter
    if (searchTerm) {
      result = result.filter(fine =>
        fine.book?.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        fine.book?.author?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Apply status filter
    if (statusFilter !== 'ALL') {
      result = result.filter(fine => fine.status === statusFilter);
    }

    // Apply sorting
    result.sort((a, b) => {
      switch (sortBy) {
        case 'newest':
          return new Date(b.createdDate) - new Date(a.createdDate);
        case 'oldest':
          return new Date(a.createdDate) - new Date(b.createdDate);
        case 'amount':
          return b.amount - a.amount;
        case 'title':
          return (a.book?.title || '').localeCompare(b.book?.title || '');
        default:
          return 0;
      }
    });

    setFilteredFines(result);
  }, [fines, searchTerm, statusFilter, sortBy]);

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  };

  const formatPrice = (price) => {
    if (!price) return '0đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'UNPAID':
        return 'Chưa thanh toán';
      case 'PAID':
        return 'Đã thanh toán';
      case 'CANCELLED':
        return 'Đã hủy';
      default:
        return status;
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'UNPAID':
        return 'status-unpaid';
      case 'PAID':
        return 'status-paid';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return '';
    }
  };

  const handlePayFine = async (fine) => {
    if (fine.status !== 'UNPAID') return;

    try {
      const response = await axios.post('http://localhost:8084/payments/vnpay', {
        userId: parseInt(localStorage.getItem('userId')),
        amount: fine.amount,
        referenceId: fine.id,
        orderInfo: `Thanh toán phạt cho sách: ${fine.book?.title || 'N/A'}`,
        type: 'fine'
      });

      if (response.data && response.data.paymentUrl) {
        window.open(response.data.paymentUrl, '_blank');
      } else {
        alert('Không thể tạo liên kết thanh toán');
      }
    } catch (err) {
      alert('Lỗi khi tạo thanh toán: ' + err.message);
    }
  };

  return (
    <>
      <Header />
      <div className="fine-history-container">
        <h1>Lịch sử phạt</h1>

        {/* Search and Filter Controls */}
        <div className="controls-section">
          <div className="search-box">
            <i className="fas fa-search"></i>
            <input
              type="text"
              placeholder="Tìm kiếm theo tên sách, tác giả..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="filters">
            <div className="filter-group">
              <label>Trạng thái:</label>
              <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                <option value="ALL">Tất cả</option>
                <option value="UNPAID">Chưa thanh toán</option>
                <option value="PAID">Đã thanh toán</option>
                <option value="CANCELLED">Đã hủy</option>
              </select>
            </div>

            <div className="filter-group">
              <label>Sắp xếp:</label>
              <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="title">Tên sách (A-Z)</option>
                <option value="newest">Mới nhất</option>
                <option value="oldest">Cũ nhất</option>
                <option value="amount">Số tiền (Cao-Thấp)</option>
              </select>
            </div>
          </div>
        </div>

        {/* Results count */}
        {!loading && !error && (
          <div className="results-count">
            Tìm thấy <strong>{filteredFines.length}</strong> kết quả
          </div>
        )}

        {loading && (
          <div className="loading-spinner">
            <i className="fas fa-spinner fa-spin"></i> Đang tải...
          </div>
        )}

        {error && <div className="error-message">{error}</div>}

        {!loading && !error && filteredFines.length === 0 && fines.length === 0 && (
          <div className="empty-message">
            <i className="fas fa-exclamation-triangle"></i>
            <p>Bạn chưa có khoản phạt nào</p>
          </div>
        )}

        {!loading && !error && filteredFines.length === 0 && fines.length > 0 && (
          <div className="empty-message">
            <i className="fas fa-search"></i>
            <p>Không tìm thấy kết quả phù hợp</p>
          </div>
        )}

        {!loading && !error && filteredFines.length > 0 && (
          <div className="fine-list">
            {filteredFines.map((fine) => (
              <div key={fine.id} className="fine-item">
                <div className="book-image">
                  {fine.book?.coverImageUrl ? (
                    <img src={fine.book.coverImageUrl} alt={fine.book.title} />
                  ) : (
                    <div className="no-image">
                      <i className="fas fa-book"></i>
                    </div>
                  )}
                </div>

                <div className="fine-details">
                  <h3 className="book-title">
                    {fine.book?.title || 'Không tìm thấy thông tin sách'}
                  </h3>
                  
                  <div className="fine-info">
                    <div className="info-item">
                      <span className="label">Ngày phạt:</span>
                      <span className="value">{formatDate(fine.createdDate)}</span>
                    </div>
                    
                    <div className="info-item">
                      <span className="label">Lý do:</span>
                      <span className="value">{fine.reason}</span>
                    </div>
                    
                    <div className="info-item">
                      <span className="label">Số tiền:</span>
                      <span className="value fine-amount">{formatPrice(fine.amount)}</span>
                    </div>

                    <div className="info-item">
                      <span className="label">Trạng thái:</span>
                      <span className={`value ${getStatusClass(fine.status)}`}>{getStatusText(fine.status)}</span>
                    </div>
                  </div>

                  {fine.status === 'UNPAID' && (
                    <button 
                      className="pay-button"
                      onClick={() => handlePayFine(fine)}
                    >
                      Thanh toán qua VNPay
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default FineHistoryPage;