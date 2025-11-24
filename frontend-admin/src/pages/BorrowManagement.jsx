import React, { useState, useEffect } from 'react';
import { FaBook, FaUser, FaCalendarPlus, FaCheck, FaTrash, FaSearch, FaChevronLeft, FaChevronRight, FaPlus, FaEdit } from 'react-icons/fa';
import '../styles/Dashboard.css';

const BorrowManagement = () => {
  const [borrows, setBorrows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingBorrow, setEditingBorrow] = useState(null);
  const [users, setUsers] = useState([]);
  const [books, setBooks] = useState([]);
  const [formData, setFormData] = useState({
    userId: '',
    bookId: '',
    borrowDate: new Date().toISOString().split('T')[0],
    dueDate: '',
    status: 'BORROWED'
  });
  const [bookSearchTerm, setBookSearchTerm] = useState('');

  useEffect(() => {
    fetchBorrows();
    fetchUsersAndBooks();
  }, []);

  const fetchUsersAndBooks = async () => {
    try {
      const [usersRes, booksRes] = await Promise.all([
        fetch('http://localhost:8081/users'),
        fetch('http://localhost:8082/books')
      ]);

      if (usersRes.ok) {
        const usersData = await usersRes.json();
        setUsers(usersData);
      }

      if (booksRes.ok) {
        const booksData = await booksRes.json();
        setBooks(booksData);
      }
    } catch (error) {
      console.error('Error fetching users/books:', error);
    }
  };

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

  const filteredBooks = books.filter(book =>
    book.title.toLowerCase().includes(bookSearchTerm.toLowerCase())
  );

  const getStatusDisplay = (status) => {
    switch (status) {
      case 'BORROWED':
        return 'Đang mượn';
      case 'RETURNED':
        return 'Đã trả';
      case 'LATE_RETURNED':
        return 'Trễ hạn';
      case 'LOST':
        return 'Thất lạc';
      default:
        return status || 'Đang mượn';
    }
  };

  const formatDate = (value) => {
    if (!value) return 'N/A';
    return new Date(value).toLocaleDateString('vi-VN');
  };

  const handleReturn = async (borrowId, currentStatus) => {
    if (!window.confirm('Xác nhận đã trả sách này?')) return;
    try {
      if (currentStatus === 'BORROWED') {
        // Trường hợp 1: BORROWED -> gọi /return để chuyển thành RETURNED
        await fetch(`http://localhost:8086/borrows/${borrowId}/return`, { method: 'PUT' });
      } else if (currentStatus === 'LATE_RETURNED') {
        // Trường hợp 2: LATE_RETURNED -> chỉ cập nhật ngày trả, giữ nguyên trạng thái
        const returnDate = new Date().toISOString().split('T')[0];
        await fetch(`http://localhost:8086/borrows/${borrowId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            returnDate: returnDate,
            status: 'LATE_RETURNED' // Giữ nguyên trạng thái
          })
        });
      }
      // Trường hợp 3: LOST -> không hiển thị nút (đã xử lý ở JSX)
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

  const handleAddBorrow = async () => {
    try {
      const response = await fetch('http://localhost:8086/borrows', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      if (response.ok) {
        setShowAddModal(false);
        setFormData({
          userId: '',
          bookId: '',
          borrowDate: new Date().toISOString().split('T')[0],
          dueDate: '',
          status: 'BORROWED'
        });
        setBookSearchTerm('');
        fetchBorrows();
      } else {
        console.error('Failed to add borrow');
      }
    } catch (error) {
      console.error('Error adding borrow:', error);
    }
  };

  const handleEditBorrow = async () => {
    try {
      const response = await fetch(`http://localhost:8086/borrows/${editingBorrow.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      if (response.ok) {
        setEditingBorrow(null);
        setFormData({
          userId: '',
          bookId: '',
          borrowDate: new Date().toISOString().split('T')[0],
          dueDate: '',
          status: 'BORROWED'
        });
        fetchBorrows();
      } else {
        console.error('Failed to edit borrow');
      }
    } catch (error) {
      console.error('Error editing borrow:', error);
    }
  };

  const openEditModal = (borrow) => {
    setEditingBorrow(borrow);
    setFormData({
      userId: borrow.userId,
      bookId: borrow.bookId,
      borrowDate: borrow.borrowDate ? new Date(borrow.borrowDate).toISOString().split('T')[0] : '',
      dueDate: borrow.dueDate ? new Date(borrow.dueDate).toISOString().split('T')[0] : '',
      status: borrow.status || 'BORROWED'
    });
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
          <button className="btn-primary" onClick={() => setShowAddModal(true)}>
            <FaPlus /> Thêm mượn sách
          </button>
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
                  <th>Hạn trả</th>
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
                        {getStatusDisplay(borrow.status)}
                      </td>
                      <td>
                        <div className="action-buttons">
                          {borrow.status !== 'RETURNED' && borrow.status !== 'LOST' && (
                            <button className="btn-icon edit" title="Đánh dấu trả" onClick={() => handleReturn(borrow.id, borrow.status)}>
                              <FaCheck />
                            </button>
                          )}
                          
                          <button className="btn-icon edit" title="Chỉnh sửa" onClick={() => openEditModal(borrow)}>
                            <FaEdit />
                          </button>

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

      {/* Add Borrow Modal */}
      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Thêm bản ghi mượn sách</h3>
              <button className="modal-close" onClick={() => { setShowAddModal(false); setBookSearchTerm(''); }}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Người mượn:</label>
                <select
                  value={formData.userId}
                  onChange={(e) => setFormData({...formData, userId: e.target.value})}
                  required
                >
                  <option value="">Chọn người mượn</option>
                  {users.map(user => (
                    <option key={user.userId} value={user.userId}>{user.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Sách:</label>
                <input
                  type="text"
                  placeholder="Tìm kiếm sách..."
                  value={bookSearchTerm}
                  onChange={(e) => setBookSearchTerm(e.target.value)}
                  className="search-input"
                  style={{ marginBottom: '10px' }}
                />
                <select
                  value={formData.bookId}
                  onChange={(e) => setFormData({...formData, bookId: e.target.value})}
                  required
                  size={Math.min(filteredBooks.length + 1, 6)}
                  style={{ width: '100%', maxHeight: '200px' }}
                >
                  <option value="">Chọn sách</option>
                  {filteredBooks.map(book => (
                    <option key={book.id} value={book.id}>{book.title}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Ngày mượn:</label>
                <input
                  type="date"
                  value={formData.borrowDate}
                  onChange={(e) => setFormData({...formData, borrowDate: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Hạn trả:</label>
                <input
                  type="date"
                  value={formData.dueDate}
                  onChange={(e) => setFormData({...formData, dueDate: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Trạng thái:</label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({...formData, status: e.target.value})}
                  required
                >
                  <option value="BORROWED">Đang mượn</option>
                  <option value="RETURNED">Đã trả</option>
                  <option value="LATE_RETURNED">Trễ hạn</option>
                  <option value="LOST">Thất lạc</option>
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => { setShowAddModal(false); setBookSearchTerm(''); }}>Hủy</button>
              <button className="btn-primary" onClick={handleAddBorrow}>Thêm</button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Borrow Modal */}
      {editingBorrow && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Chỉnh sửa bản ghi mượn sách</h3>
              <button className="modal-close" onClick={() => setEditingBorrow(null)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Người mượn:</label>
                <select
                  value={formData.userId}
                  disabled
                  style={{ backgroundColor: '#f5f5f5', cursor: 'not-allowed' }}
                >
                  <option value="">Chọn người mượn</option>
                  {users.map(user => (
                    <option key={user.userId} value={user.userId}>{user.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Sách:</label>
                <select
                  value={formData.bookId}
                  disabled
                  style={{ backgroundColor: '#f5f5f5', cursor: 'not-allowed' }}
                >
                  <option value="">Chọn sách</option>
                  {books.map(book => (
                    <option key={book.id} value={book.id}>{book.title}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Ngày mượn:</label>
                <input
                  type="date"
                  value={formData.borrowDate}
                  disabled
                  style={{ backgroundColor: '#f5f5f5', cursor: 'not-allowed' }}
                />
              </div>
              <div className="form-group">
                <label>Hạn trả:</label>
                <input
                  type="date"
                  value={formData.dueDate}
                  onChange={(e) => setFormData({...formData, dueDate: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Trạng thái:</label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({...formData, status: e.target.value})}
                  required
                >
                  <option value="BORROWED">Đang mượn</option>
                  <option value="RETURNED">Đã trả</option>
                  <option value="LATE_RETURNED">Trễ hạn</option>
                  <option value="LOST">Thất lạc</option>
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setEditingBorrow(null)}>Hủy</button>
              <button className="btn-primary" onClick={handleEditBorrow}>Lưu</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default BorrowManagement;