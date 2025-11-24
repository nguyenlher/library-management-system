import React, { useState, useEffect } from 'react';
import { FaBook, FaEdit, FaTrash, FaSearch, FaTimes, FaEye, FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Dashboard.css';

const BookManagement = () => {
  const navigate = useNavigate();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);
  const [showModal, setShowModal] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [justAdded, setJustAdded] = useState(false);
  const [authorSearch, setAuthorSearch] = useState('');
  const [publisherSearch, setPublisherSearch] = useState('');
  const [authors, setAuthors] = useState([]);
  const [publishers, setPublishers] = useState([]);
  const [authorSearchTerm, setAuthorSearchTerm] = useState('');
  const [publisherSearchTerm, setPublisherSearchTerm] = useState('');
  const [selectedAuthors, setSelectedAuthors] = useState([]);

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
    fetchBooks();
    fetchAuthors();
    fetchPublishers();
  }, []);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8082/books');
      const data = await response.json();
      setBooks(data);
    } catch (error) {
      console.error('Error fetching books:', error);
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

  // Tính toán sách hiển thị theo trang
  const filteredBooks = books.filter(book =>
    book.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    book.isbn?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredBooks.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentBooks = filteredBooks.slice(startIndex, endIndex);

  // Reset về trang 1 khi tìm kiếm
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  // Reset về trang 1 khi dữ liệu thay đổi, trừ khi vừa thêm sách
  useEffect(() => {
    if (!justAdded) {
      setCurrentPage(1);
    }
  }, [books]);

  // Chuyển đến trang cuối khi vừa thêm sách
  useEffect(() => {
    if (justAdded && books.length > 0) {
      const newTotalPages = Math.ceil(books.length / itemsPerPage);
      setCurrentPage(newTotalPages);
      setJustAdded(false);
    }
  }, [books, justAdded, itemsPerPage]);

  const handleViewDetails = (bookId) => {
    navigate(`/books/${bookId}`);
  };

  const handleAdd = () => {
    setEditingBook(null);
    setSelectedFile(null);
    setSelectedAuthors([]);
    setFormData({
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
    setShowModal(true);
  };

  const handleEdit = (book) => {
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

  const handleDelete = async (bookId) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa sách này?')) {
      try {
        await fetch(`http://localhost:8082/books/${bookId}`, {
          method: 'DELETE',
        });
        fetchBooks(); // Refresh danh sách
      } catch (error) {
        console.error('Error deleting book:', error);
      }
    }
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

      if (editingBook) {
        // Update book
        await fetch(`http://localhost:8082/books/${editingBook.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(bookData),
        });
      } else {
        // Add new book
        await fetch('http://localhost:8082/books', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(bookData),
        });
        setJustAdded(true);
      }
      setShowModal(false);
      setSelectedFile(null);
      fetchBooks(); // Refresh list
    } catch (error) {
      console.error('Error saving book:', error);
      alert('Có lỗi xảy ra khi lưu sách');
    }
  };

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý Sách</h2>
        <button className="btn-primary" onClick={handleAdd}>
          <FaBook /> Thêm sách
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm kiếm theo tiêu đề, ISBN..."
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
                  <th>Ảnh</th>
                  <th>Tiêu đề</th>
                  <th>Tác giả</th>
                  <th>Tổng số bản</th>
                  <th>Hiện có</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentBooks.length > 0 ? (
                  currentBooks.map((book, index) => (
                    <tr key={book.id}>
                      <td>{startIndex + index + 1}</td>
                      <td>
                        {book.coverImageUrl ? (
                          <img 
                            src={book.coverImageUrl} 
                            alt={book.title} 
                            style={{ width: '50px', height: '70px', objectFit: 'cover' }}
                          />
                        ) : (
                          <div style={{ 
                            width: '50px', 
                            height: '70px', 
                            backgroundColor: '#f0f0f0',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            border: '1px solid #ddd'
                          }}>
                            <FaBook />
                          </div>
                        )}
                      </td>
                      <td>{book.title}</td>
                      <td>{book.authors ? book.authors.map(a => a.name).join(', ') : 'N/A'}</td>
                      <td>{book.totalCopies}</td>
                      <td>{book.availableCopies}</td>
                      <td>
                        <div className="action-buttons">
                          <button className="btn-icon view" title="Xem chi tiết" onClick={() => handleViewDetails(book.id)}>
                            <FaEye />
                          </button>
                          <button className="btn-icon edit" title="Chỉnh sửa" onClick={() => handleEdit(book)}>
                            <FaEdit />
                          </button>
                          <button
                            className="btn-icon delete"
                            title="Xóa"
                            onClick={() => handleDelete(book.id)}
                          >
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy sách nào
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
            Hiển thị {startIndex + 1}-{Math.min(endIndex, filteredBooks.length)} / {filteredBooks.length} sách (Trang {currentPage}/{totalPages})
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>{editingBook ? 'Chỉnh sửa sách' : 'Thêm sách'}</h3>
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
                    placeholder="Ví dụ: 2022"
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
                  <label>Số bản hiện có:</label>
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
                  {editingBook ? 'Cập nhật' : 'Thêm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default BookManagement;