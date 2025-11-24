import React, { useState, useEffect } from 'react';
import { FaBook, FaUser, FaCalendarPlus, FaCheck, FaTrash, FaSearch, FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import '../styles/Dashboard.css';

const BorrowManagement = () => {
  const [borrows, setBorrows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);

  useEffect(() => {
    fetchBorrows();
  }, []);

  const fetchBorrows = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8086/borrows');
      const data = await response.json();

      // fetch user profiles and books to resolve names
      try {
        const [usersRes, booksRes] = await Promise.all([
          fetch('http://localhost:8081/users'),
          fetch('http://localhost:8082/books')
        ]);

        const usersData = usersRes.ok ? await usersRes.json() : [];
        const booksData = booksRes.ok ? await booksRes.json() : [];

        const userMap = {};
        usersData.forEach(u => {
          // UserProfileResponseDto has userId and name
          if (u.userId != null) userMap[u.userId] = u.name || '';
        });

        const bookMap = {};
        booksData.forEach(b => {
          if (b.id != null) bookMap[b.id] = b.title || '';
        });

        // attach resolved names to borrow objects for display
        const enriched = data.map(borrow => ({
          ...borrow,
          user: { name: userMap[borrow.userId] || 'N/A' },
          book: { title: bookMap[borrow.bookId] || 'N/A' }
        }));

        setBorrows(enriched);
      } catch (innerErr) {
        console.error('Error fetching users/books:', innerErr);
        setBorrows(data);
      }
    } catch (error) {
      console.error('Error fetching borrows:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredBorrows = borrows.filter(borrow => {
    const bookTitle = borrow.book?.title?.toLowerCase() || '';
    const userName = borrow.user?.name?.toLowerCase() || '';
    return (
      bookTitle.includes(searchTerm.toLowerCase()) ||
      userName.includes(searchTerm.toLowerCase()) ||
      borrow.status?.toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  const totalPages = Math.ceil(filteredBorrows.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentBorrows = filteredBorrows.slice(startIndex, startIndex + itemsPerPage);

  const formatDate = (value) => {
    if (!value) return 'N/A';
    return new Date(value).toLocaleDateString('vi-VN');
  };

  const handleReturn = async (borrowId) => {
    if (!window.confirm('Xác nhận đã trả sách này?')) return;
    try {
      await fetch(`http://localhost:8086/borrows/${borrowId}/return`, { method: 'PUT' });
      fetchBorrows();
    } catch (error) {
      console.error('Error marking returned:', error);
    }
  };

  const handleDelete = async (borrowId) => {
    if (!window.confirm('Bạn có muốn xóa bản ghi mượn này?')) return;
    try {
      await fetch(`http://localhost:8086/borrows/${borrowId}`, { method: 'DELETE' });
      fetchBorrows();
    } catch (error) {
      console.error('Error deleting borrow:', error);
    }
  };

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý mượn sách</h2>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm theo sách, người mượn, trạng thái..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
        <div className="table-section">
          {loading ? (
            <div className="loading">Đang tải dữ liệu...</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Sách</th>
                  <th>Người mượn</th>
                  <th>Ngày mượn</th>
                  <th>Hẹn trả</th>
                  <th>Ngày trả thực tế</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentBorrows.length > 0 ? (
                  currentBorrows.map((borrow, index) => (
                    <tr key={borrow.id}>
                      <td>{startIndex + index + 1}</td>
                      <td>{borrow.book?.title || 'N/A'}</td>
                      <td>{borrow.user?.name || 'N/A'}</td>
                      <td>{formatDate(borrow.borrowDate)}</td>
                      <td>{formatDate(borrow.dueDate)}</td>
                      <td>{formatDate(borrow.returnDate)}</td>
                      <td className={`status ${borrow.status?.toLowerCase()}`}>
                        {borrow.status || 'N/A'}
                      </td>
                      <td>
                        <div className="action-buttons">
                          {borrow.status !== 'RETURNED' && (
                            <button className="btn-icon edit" title="Đánh dấu trả" onClick={() => handleReturn(borrow.id)}>
                              <FaCheck />
                            </button>
                          )}
                          <button className="btn-icon delete" title="Xóa" onClick={() => handleDelete(borrow.id)}>
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy bản ghi mượn nào
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          )}
        </div>
        {totalPages > 1 && (
          <div className="pagination">
            <button className="btn-pagination" onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))} disabled={currentPage === 1}>
              <FaChevronLeft /> Trước
            </button>
            <div className="pagination-numbers">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                <button key={page} className={`btn-pagination-number ${currentPage === page ? 'active' : ''}`} onClick={() => setCurrentPage(page)}>
                  {page}
                </button>
              ))}
            </div>
            <button className="btn-pagination" onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))} disabled={currentPage === totalPages}>
              Sau <FaChevronRight />
            </button>
          </div>
        )}
        <div className="card-footer">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-{Math.min(startIndex + currentBorrows.length, filteredBorrows.length)} / {filteredBorrows.length} bản ghi (Trang {currentPage}/{totalPages})
          </div>
        </div>
      </div>
    </div>
  );
};

export default BorrowManagement;