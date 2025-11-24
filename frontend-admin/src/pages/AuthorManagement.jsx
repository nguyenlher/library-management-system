import React, { useState, useEffect } from 'react';
import { FaUser, FaEdit, FaTrash, FaSearch, FaTimes, FaEye, FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import '../styles/Dashboard.css';

const AuthorManagement = () => {
  const navigate = useNavigate();
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);
  const [showModal, setShowModal] = useState(false);
  const [editingAuthor, setEditingAuthor] = useState(null);
  const [justAdded, setJustAdded] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    biography: '',
    birthDate: '',
    deathDate: ''
  });

  useEffect(() => {
    fetchAuthors();
  }, []);

  const fetchAuthors = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8082/authors');
      const data = await response.json();
      setAuthors(data);
    } catch (error) {
      console.error('Error fetching authors:', error);
    } finally {
      setLoading(false);
    }
  };

  // Tính toán tác giả hiển thị theo trang
  const filteredAuthors = authors.filter(author =>
    author.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    author.biography?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredAuthors.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentAuthors = filteredAuthors.slice(startIndex, endIndex);

  // Reset về trang 1 khi tìm kiếm
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  // Reset về trang 1 khi dữ liệu thay đổi, trừ khi vừa thêm tác giả
  useEffect(() => {
    if (!justAdded) {
      setCurrentPage(1);
    }
  }, [authors]);

  // Chuyển đến trang cuối khi vừa thêm tác giả
  useEffect(() => {
    if (justAdded && authors.length > 0) {
      const newTotalPages = Math.ceil(authors.length / itemsPerPage);
      setCurrentPage(newTotalPages);
      setJustAdded(false);
    }
  }, [authors, justAdded, itemsPerPage]);

  const handleViewDetails = (authorId) => {
    navigate(`/authors/${authorId}`);
  };

  const handleAdd = () => {
    setEditingAuthor(null);
    setFormData({
      name: '',
      biography: '',
      birthDate: '',
      deathDate: ''
    });
    setShowModal(true);
  };

  const handleEdit = (author) => {
    setEditingAuthor(author);
    setFormData({
      name: author.name || '',
      biography: author.biography || '',
      birthDate: author.birthDate || '',
      deathDate: author.deathDate || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (authorId) => {
    try {
      // Kiểm tra xem tác giả có còn ràng buộc với sách không
      const booksResponse = await fetch('http://localhost:8082/books');
      const books = await booksResponse.json();
      const authorBooks = books.filter(book => book.authorId === authorId);

      if (authorBooks.length > 0) {
        alert(`Không thể xóa tác giả này vì còn ${authorBooks.length} cuốn sách đang sử dụng. Vui lòng xóa hoặc thay đổi tác giả của các cuốn sách này trước.`);
        return;
      }

      if (window.confirm('Bạn có chắc chắn muốn xóa tác giả này?')) {
        await fetch(`http://localhost:8082/authors/${authorId}`, {
          method: 'DELETE',
        });
        fetchAuthors(); // Refresh danh sách
      }
    } catch (error) {
      console.error('Error deleting author:', error);
      alert('Có lỗi xảy ra khi xóa tác giả');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const authorData = {
        name: formData.name,
        biography: formData.biography,
        birthDate: formData.birthDate ? new Date(formData.birthDate).toISOString().split('T')[0] : null,
        deathDate: formData.deathDate ? new Date(formData.deathDate).toISOString().split('T')[0] : null
      };

      if (editingAuthor) {
        // Update author
        await fetch(`http://localhost:8082/authors/${editingAuthor.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(authorData),
        });
      } else {
        // Add new author
        await fetch('http://localhost:8082/authors', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(authorData),
        });
        setJustAdded(true);
      }
      setShowModal(false);
      fetchAuthors(); // Refresh list
    } catch (error) {
      console.error('Error saving author:', error);
      alert('Có lỗi xảy ra khi lưu tác giả');
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('vi-VN');
  };

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý Tác giả</h2>
        <button className="btn-primary" onClick={handleAdd}>
          <FaUser /> Thêm tác giả
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm kiếm theo tên, tiểu sử..."
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
                  <th>Tên tác giả</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentAuthors.length > 0 ? (
                  currentAuthors.map((author, index) => (
                    <tr key={author.id}>
                      <td>{startIndex + index + 1}</td>
                      <td>{author.name}</td>
                      <td>
                        <div className="action-buttons">
                          <button className="btn-icon view" title="Xem chi tiết" onClick={() => handleViewDetails(author.id)}>
                            <FaEye />
                          </button>
                          <button className="btn-icon edit" title="Chỉnh sửa" onClick={() => handleEdit(author)}>
                            <FaEdit />
                          </button>
                          <button
                            className="btn-icon delete"
                            title="Xóa"
                            onClick={() => handleDelete(author.id)}
                          >
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy tác giả nào
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          )}
        </div>

        {/* Phân trang */}
        {totalPages > 1 && (
          <div className="pagination">
            <button 
              className="btn-pagination" 
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
            >
              <FaChevronLeft /> Trước
            </button>
            
            <div className="pagination-numbers">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                <button
                  key={page}
                  className={`btn-pagination-number ${currentPage === page ? 'active' : ''}`}
                  onClick={() => setCurrentPage(page)}
                >
                  {page}
                </button>
              ))}
            </div>
            
            <button 
              className="btn-pagination" 
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
            >
              Sau <FaChevronRight />
            </button>
          </div>
        )}

        <div className="card-footer">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-{Math.min(endIndex, filteredAuthors.length)} / {filteredAuthors.length} tác giả (Trang {currentPage}/{totalPages})
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>{editingAuthor ? 'Chỉnh sửa tác giả' : 'Thêm tác giả'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <FaTimes />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Tên tác giả:</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Tiểu sử:</label>
                  <textarea
                    value={formData.biography}
                    onChange={(e) => setFormData({...formData, biography: e.target.value})}
                    rows="4"
                  />
                </div>
                <div className="form-group">
                  <label>Ngày sinh:</label>
                  <input
                    type="date"
                    value={formData.birthDate}
                    onChange={(e) => setFormData({...formData, birthDate: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <label>Ngày mất:</label>
                  <input
                    type="date"
                    value={formData.deathDate}
                    onChange={(e) => setFormData({...formData, deathDate: e.target.value})}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>
                  Hủy
                </button>
                <button type="submit" className="btn-primary">
                  {editingAuthor ? 'Cập nhật' : 'Thêm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuthorManagement;