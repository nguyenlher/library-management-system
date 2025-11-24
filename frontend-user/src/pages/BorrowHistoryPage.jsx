import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/BorrowHistoryPage.css';

const BorrowHistoryPage = () => {
  const [borrows, setBorrows] = useState([]);
  const [filteredBorrows, setFilteredBorrows] = useState([]);
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
      .get(`http://localhost:8086/borrows/user/${userId}`)
      .then(async (response) => {
        if (cancelled) return;

        const borrowData = response.data || [];
        
        // Fetch book details for each borrow
        const borrowsWithBooks = await Promise.all(
          borrowData.map(async (borrow) => {
            try {
              const bookResponse = await axios.get(`http://localhost:8082/books/${borrow.bookId}`);
              return {
                ...borrow,
                book: bookResponse.data
              };
            } catch (err) {
              return {
                ...borrow,
                book: null
              };
            }
          })
        );

        if (!cancelled) {
          setBorrows(borrowsWithBooks);
          setFilteredBorrows(borrowsWithBooks);
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError('Không thể tải lịch sử mượn sách');
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

  // Filter and sort borrows
  useEffect(() => {
    let result = [...borrows];

    // Apply search filter
    if (searchTerm) {
      result = result.filter(borrow =>
        borrow.book?.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        borrow.book?.author?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Apply status filter
    if (statusFilter !== 'ALL') {
      result = result.filter(borrow => borrow.status === statusFilter);
    }

    // Apply sorting
    result.sort((a, b) => {
      switch (sortBy) {
        case 'newest':
          return new Date(b.borrowDate) - new Date(a.borrowDate);
        case 'oldest':
          return new Date(a.borrowDate) - new Date(b.borrowDate);
        case 'dueDate':
          return new Date(a.dueDate) - new Date(b.dueDate);
        case 'title':
          return (a.book?.title || '').localeCompare(b.book?.title || '');
        default:
          return 0;
      }
    });

    setFilteredBorrows(result);
  }, [borrows, searchTerm, statusFilter, sortBy]);

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
      case 'BORROWED':
        return 'Đang mượn';
      case 'RETURNED':
        return 'Đã trả';
      case 'OVERDUE':
        return 'Quá hạn';
      case 'PENDING':
        return 'Chờ xử lý';
      default:
        return status;
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'BORROWED':
        return 'status-borrowed';
      case 'RETURNED':
        return 'status-returned';
      case 'OVERDUE':
        return 'status-overdue';
      case 'PENDING':
        return 'status-pending';
      default:
        return '';
    }
  };

  return (
    <>
      <Header />
      <div className="borrow-history-container">
        <h1>Lịch sử mượn sách</h1>

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
                <option value="BORROWED">Đang mượn</option>
                <option value="RETURNED">Đã trả</option>
                <option value="OVERDUE">Quá hạn</option>
                <option value="PENDING">Chờ xử lý</option>
              </select>
            </div>

            <div className="filter-group">
              <label>Sắp xếp:</label>
              <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="title">Tên sách (A-Z)</option>
                <option value="newest">Mới nhất</option>
                <option value="oldest">Cũ nhất</option>
                <option value="dueDate">Ngày hẹn trả</option>
              </select>
            </div>
          </div>
        </div>

        {/* Results count */}
        {!loading && !error && (
          <div className="results-count">
            Tìm thấy <strong>{filteredBorrows.length}</strong> kết quả
          </div>
        )}

        {loading && (
          <div className="loading-spinner">
            <i className="fas fa-spinner fa-spin"></i> Đang tải...
          </div>
        )}

        {error && <div className="error-message">{error}</div>}

        {!loading && !error && filteredBorrows.length === 0 && borrows.length === 0 && (
          <div className="empty-message">
            <i className="fas fa-book-open"></i>
            <p>Bạn chưa mượn sách nào</p>
          </div>
        )}

        {!loading && !error && filteredBorrows.length === 0 && borrows.length > 0 && (
          <div className="empty-message">
            <i className="fas fa-search"></i>
            <p>Không tìm thấy kết quả phù hợp</p>
          </div>
        )}

        {!loading && !error && filteredBorrows.length > 0 && (
          <div className="borrow-list">
            {filteredBorrows.map((borrow) => (
              <div key={borrow.id} className="borrow-item">
                <div className="book-image">
                  {borrow.book?.coverImageUrl ? (
                    <img src={borrow.book.coverImageUrl} alt={borrow.book.title} />
                  ) : (
                    <div className="no-image">
                      <i className="fas fa-book"></i>
                    </div>
                  )}
                </div>

                <div className="borrow-details">
                  <h3 className="book-title">
                    {borrow.book?.title || 'Không tìm thấy thông tin sách'}
                  </h3>
                  
                  <div className="borrow-info">
                    <div className="info-item">
                      <span className="label">Ngày mượn:</span>
                      <span className="value">{formatDate(borrow.borrowDate)}</span>
                    </div>
                    
                    <div className="info-item">
                      <span className="label">Ngày hẹn trả:</span>
                      <span className="value">{formatDate(borrow.dueDate)}</span>
                    </div>
                    
                    <div className="info-item">
                      <span className="label">Ngày trả thực tế:</span>
                      <span className="value">{formatDate(borrow.returnDate)}</span>
                    </div>

                    <div className="info-item">
                      <span className="label">{borrow.totalFine > 0 ? 'Phí phạt:' : 'Trạng thái:'}</span>
                      <span className={`value ${borrow.totalFine > 0 ? 'fine-value' : getStatusClass(borrow.status)}`}>{borrow.totalFine > 0 ? formatPrice(borrow.totalFine) : getStatusText(borrow.status)}</span>
                    </div>
                  </div>
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

export default BorrowHistoryPage;
