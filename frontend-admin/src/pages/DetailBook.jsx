import React, { useState, useEffect } from 'react';
import { FaArrowLeft, FaBook, FaCalendar, FaUser, FaTag, FaMoneyBill, FaCopy, FaEdit, FaTrash, FaTimes } from 'react-icons/fa';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Dashboard.css';

const DetailBook = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [authorSearch, setAuthorSearch] = useState('');
  const [publisherSearch, setPublisherSearch] = useState('');
  const [authors, setAuthors] = useState([]);
  const [publishers, setPublishers] = useState([]);
  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [authorSearchTerm, setAuthorSearchTerm] = useState('');
  const [publisherSearchTerm, setPublisherSearchTerm] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    isbn: '',
    summary: '',
    edition: '',
    totalCopies: 1,
    availableCopies: 1,
    borrowFee: 0,
    publishYear: new Date().getFullYear(),
    publisherId: '',
    authorIds: []
  });

  useEffect(() => {
    fetchBookDetails();
    fetchAuthors();
    fetchPublishers();
  }, [id]);

  const fetchBookDetails = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8082/books/${id}`);
      if (response.ok) {
        const data = await response.json();
        setBook(data);
      } else {
        alert('Không tìm thấy sách này');
        navigate('/books');
      }
    } catch (error) {
      console.error('Error fetching book details:', error);
      alert('Có lỗi xảy ra khi tải thông tin sách');
      navigate('/books');
    } finally {
      setLoading(false);
    }
  };

  const fetchAuthors = async () => {
    try {
      const response = await fetch('http://localhost:8082/authors');
      const data = await response.json();
      setAuthors(data);
    } catch (error) {
      console.error('Error fetching authors:', error);
    }
  };

  const fetchPublishers = async () => {
    try {
      const response = await fetch('http://localhost:8082/publishers');
      const data = await response.json();
      setPublishers(data);
    } catch (error) {
      console.error('Error fetching publishers:', error);
    }
  };

  const filteredAuthors = authors.filter(author =>
    author.name?.toLowerCase().includes(authorSearchTerm.toLowerCase())
  );

  const filteredPublishers = publishers.filter(publisher =>
    publisher.name?.toLowerCase().includes(publisherSearchTerm.toLowerCase())
  );

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatCurrency = (amount) => {
    if (!amount) return '0 VND';
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  };

  const handleEdit = () => {
    setEditingBook(book);
    setSelectedFile(null);
    setSelectedAuthors(book.authors ? book.authors.map(a => a.id) : []);
    setFormData({
      title: book.title || '',
      isbn: book.isbn || '',
      summary: book.summary || '',
      edition: book.edition || '',
      totalCopies: book.totalCopies || 1,
      availableCopies: book.availableCopies || 1,
      borrowFee: book.borrowFee || 0,
      publishYear: book.publishYear || new Date().getFullYear(),
      publisherId: book.publisherId || '',
      authorIds: book.authors ? book.authors.map(a => a.id) : []
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let coverImageUrl = editingBook ? editingBook.coverImageUrl : null;

      if (selectedFile) {
        const uploadFormData = new FormData();
        uploadFormData.append('file', selectedFile);
        uploadFormData.append('upload_preset', 'library-management-system');
        uploadFormData.append('folder', 'books');
        uploadFormData.append('api_key', '254684993199542');

        const uploadResponse = await axios.post('https://api.cloudinary.com/v1_1/dehn8lwxv/image/upload', uploadFormData);
        coverImageUrl = uploadResponse.data.secure_url;
      }

      const bookData = {
        title: formData.title,
        isbn: formData.isbn,
        summary: formData.summary,
        edition: formData.edition,
        totalCopies: parseInt(formData.totalCopies),
        availableCopies: parseInt(formData.availableCopies),
        borrowFee: parseFloat(formData.borrowFee).toFixed(2),
        publishYear: parseInt(formData.publishYear),
        coverImageUrl: coverImageUrl,
        publisherId: formData.publisherId ? parseInt(formData.publisherId) : null,
        authorIds: selectedAuthors
      };

      // Update book
      const response = await fetch(`http://localhost:8082/books/${book.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookData),
      });

      if (response.ok) {
        const updatedBook = await response.json();
        setBook(updatedBook);
        setShowModal(false);
        setSelectedFile(null);
        alert('Cập nhật sách thành công');
      } else {
        alert('Có lỗi xảy ra khi cập nhật sách');
      }
    } catch (error) {
      console.error('Error updating book:', error);
      alert('Có lỗi xảy ra khi cập nhật sách');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Bạn có chắc chắn muốn xóa sách này?')) {
      try {
        const response = await fetch(`http://localhost:8082/books/${id}`, {
          method: 'DELETE',
        });
        
        if (response.ok) {
          alert('Xóa sách thành công');
          navigate('/books');
        } else {
          alert('Có lỗi xảy ra khi xóa sách');
        }
      } catch (error) {
        console.error('Error deleting book:', error);
        alert('Có lỗi xảy ra khi xóa sách');
      }
    }
  };

  if (loading) {
    return (
      <div className="dashboard-content">
        <div className="loading">Đang tải thông tin sách...</div>
      </div>
    );
  }

  if (!book) {
    return (
      <div className="dashboard-content">
        <div className="error">Không tìm thấy thông tin sách</div>
      </div>
    );
  }

  return (
    <>
      <div className="dashboard-content">
        <div className="page-header">
          <div className="page-header-left">
            <button className="btn-secondary" onClick={() => navigate('/books')}>
              <FaArrowLeft /> Quay lại
            </button>
          </div>
          <h2 className="page-title">Chi tiết sách</h2>
          <div className="page-header-actions">
            <button className="btn-primary" onClick={handleEdit}>
              <FaEdit /> Chỉnh sửa
            </button>
            <button className="btn-danger" onClick={handleDelete}>
              <FaTrash /> Xóa
            </button>
          </div>
        </div>

        <div className="book-detail-container">
          <div className="book-detail-main">
            <div className="book-cover-section">
              {book.coverImageUrl ? (
                <img src={book.coverImageUrl} alt={book.title} className="book-cover-image" />
              ) : (
                <div className="book-cover-placeholder">
                  <FaBook size={80} />
                  <span>Không có ảnh bìa</span>
                </div>
              )}
            </div>
            <div className="book-info-section">
              <h1 className="book-title">{book.title}</h1>
              
              <div className="book-details-unified">
                {/* Thông tin cơ bản */}
                <div className="detail-group">
                  <h4 className="detail-group-title">Thông tin cơ bản</h4>
                  <div className="detail-grid">
                    <div className="detail-item">
                      <span className="detail-label">ISBN:</span>
                      <span className="detail-value">{book.isbn || 'N/A'}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Nhà xuất bản:</span>
                      <span className="detail-value">{book.publisherName || 'N/A'}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Tái bản:</span>
                      <span className="detail-value">{book.edition || 'N/A'}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Tổng số bản:</span>
                      <span className="detail-value">{book.totalCopies}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Còn lại:</span>
                      <span className="detail-value">{book.availableCopies}</span>
                    </div>
                    <div className="detail-item">
                      <span className="detail-label">Phí mượn:</span>
                      <span className="detail-value">{formatCurrency(book.borrowFee)}</span>
                    </div>
                  </div>
                </div>

                {/* Tác giả */}
                {book.authors && book.authors.length > 0 && (
                  <div className="detail-group">
                    <h4 className="detail-group-title"><FaUser /> Tác giả</h4>
                    <div className="authors-list">
                      {book.authors.map((author, index) => (
                        <span key={index} className="author-tag">
                          {author.name}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Thể loại */}
                {book.categories && book.categories.length > 0 && (
                  <div className="detail-group">
                    <h4 className="detail-group-title"><FaTag /> Thể loại</h4>
                    <div className="categories-list">
                      {book.categories.map((category, index) => (
                        <span key={index} className="category-tag">
                          {category.name}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Mô tả */}
                <div className="detail-group">
                  <h4 className="detail-group-title">Mô tả</h4>
                  <p className="book-description">{book.summary || 'Không có mô tả'}</p>
                </div>
                
              </div>
            </div>
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Chỉnh sửa sách</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <FaTimes />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Tiêu đề:</label>
                  <input
                    type="text"
                    value={formData.title}
                    onChange={(e) => setFormData({...formData, title: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>ISBN:</label>
                  <input
                    type="text"
                    value={formData.isbn}
                    onChange={(e) => setFormData({...formData, isbn: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <label>Mô tả:</label>
                  <textarea
                    value={formData.summary}
                    onChange={(e) => setFormData({...formData, summary: e.target.value})}
                    rows="3"
                  />
                </div>
                <div className="form-group">
                  <label>Tái bản:</label>
                  <input
                    type="text"
                    value={formData.edition}
                    onChange={(e) => setFormData({...formData, edition: e.target.value})}
                    placeholder="Ví dụ: Tái bản lần 2"
                  />
                </div>
                <div className="form-group">
                  <label>Hình ảnh bìa:</label>
                  {editingBook && editingBook.coverImageUrl && (
                    <div style={{ marginBottom: '10px' }}>
                      <img 
                        src={editingBook.coverImageUrl} 
                        alt="Current cover" 
                        style={{ width: '100px', height: '140px', objectFit: 'cover', border: '1px solid #ddd' }}
                      />
                      <p style={{ fontSize: '12px', color: '#666', margin: '5px 0' }}>Hình ảnh hiện tại</p>
                    </div>
                  )}
                  <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setSelectedFile(e.target.files[0])}
                  />
                  {selectedFile && (
                    <p style={{ fontSize: '12px', color: '#666', margin: '5px 0' }}>Đã chọn: {selectedFile.name}</p>
                  )}
                </div>
                <div className="form-group">
                  <label>Tổng số bản:</label>
                  <input
                    type="number"
                    value={formData.totalCopies}
                    onChange={(e) => setFormData({...formData, totalCopies: e.target.value})}
                    min="1"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Số bản còn lại:</label>
                  <input
                    type="number"
                    value={formData.availableCopies}
                    onChange={(e) => setFormData({...formData, availableCopies: e.target.value})}
                    min="0"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Phí mượn (VND):</label>
                  <input
                    type="number"
                    value={formData.borrowFee}
                    onChange={(e) => setFormData({...formData, borrowFee: e.target.value})}
                    min="0"
                    step="0.01"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Năm xuất bản:</label>
                  <input
                    type="number"
                    value={formData.publishYear}
                    onChange={(e) => setFormData({...formData, publishYear: e.target.value})}
                    min="1000"
                    max={new Date().getFullYear()}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Nhà xuất bản:</label>
                  <input
                    type="text"
                    placeholder="Tìm kiếm nhà xuất bản..."
                    value={publisherSearchTerm}
                    onChange={(e) => setPublisherSearchTerm(e.target.value)}
                    className="search-input"
                    style={{ marginBottom: '10px' }}
                  />
                  <select
                    value={formData.publisherId}
                    onChange={(e) => setFormData({...formData, publisherId: e.target.value})}
                  >
                    <option value="">Chọn nhà xuất bản</option>
                    {filteredPublishers.map(publisher => (
                      <option key={publisher.id} value={publisher.id}>
                        {publisher.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Tác giả:</label>
                  <input
                    type="text"
                    placeholder="Tìm kiếm tác giả..."
                    value={authorSearchTerm}
                    onChange={(e) => setAuthorSearchTerm(e.target.value)}
                    className="search-input"
                    style={{ marginBottom: '10px' }}
                  />
                  <div className="authors-checkbox-list">
                    {filteredAuthors.map(author => (
                      <label key={author.id} className="author-checkbox-item">
                        <input
                          type="checkbox"
                          value={author.id}
                          checked={selectedAuthors.includes(author.id)}
                          onChange={(e) => {
                            const authorId = parseInt(e.target.value);
                            if (e.target.checked) {
                              setSelectedAuthors([...selectedAuthors, authorId]);
                            } else {
                              setSelectedAuthors(selectedAuthors.filter(id => id !== authorId));
                            }
                          }}
                        />
                        <span className="checkmark"></span>
                        {author.name}
                      </label>
                    ))}
                  </div>
                  {selectedAuthors.length > 0 && (
                    <small style={{ color: '#666', fontSize: '12px', marginTop: '5px', display: 'block' }}>
                      Đã chọn {selectedAuthors.length} tác giả
                    </small>
                  )}
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>
                  Hủy
                </button>
                <button type="submit" className="btn-primary">
                  Cập nhật
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default DetailBook;