import React, { useState, useEffect } from 'react';
import { FaBuilding, FaEdit, FaTrash, FaSearch, FaTimes, FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import '../styles/Dashboard.css';

const PublisherManagement = () => {
  const [publishers, setPublishers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);
  const [showModal, setShowModal] = useState(false);
  const [editingPublisher, setEditingPublisher] = useState(null);
  const [justAdded, setJustAdded] = useState(false);
  const [formData, setFormData] = useState({
    name: ''
  });

  useEffect(() => {
    fetchPublishers();
  }, []);

  const fetchPublishers = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8082/publishers');
      const data = await response.json();
      setPublishers(data);
    } catch (error) {
      console.error('Error fetching publishers:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredPublishers = publishers.filter(pub =>
    pub.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredPublishers.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentPublishers = filteredPublishers.slice(startIndex, endIndex);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  useEffect(() => {
    if (!justAdded) {
      setCurrentPage(1);
    }
  }, [publishers]);

  useEffect(() => {
    if (justAdded && publishers.length > 0) {
      const newTotalPages = Math.ceil(publishers.length / itemsPerPage);
      setCurrentPage(newTotalPages);
      setJustAdded(false);
    }
  }, [publishers, justAdded, itemsPerPage]);

  const handleAdd = () => {
    setEditingPublisher(null);
    setFormData({ name: '' });
    setShowModal(true);
  };

  const handleEdit = (pub) => {
    setEditingPublisher(pub);
    setFormData({
      name: pub.name || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (publisherId) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa nhà xuất bản này?')) {
      try {
        await fetch(`http://localhost:8082/publishers/${publisherId}`, {
          method: 'DELETE',
        });
        fetchPublishers();
      } catch (error) {
        console.error('Error deleting publisher:', error);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const publisherData = {
        name: formData.name
      };

      if (editingPublisher) {
        await fetch(`http://localhost:8082/publishers/${editingPublisher.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(publisherData),
        });
      } else {
        await fetch('http://localhost:8082/publishers', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(publisherData),
        });
        setJustAdded(true);
      }

      setShowModal(false);
      fetchPublishers();
    } catch (error) {
      console.error('Error saving publisher:', error);
      alert('Có lỗi xảy ra khi lưu nhà xuất bản');
    }
  };

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý Nhà xuất bản</h2>
        <button className="btn-primary" onClick={handleAdd}>
          <FaBuilding /> Thêm NXB
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm kiếm theo tên..."
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
                  <th>Tên NXB</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentPublishers.length > 0 ? (
                  currentPublishers.map((pub, index) => (
                    <tr key={pub.id}>
                      <td>{startIndex + index + 1}</td>
                      <td>{pub.name}</td>
                      <td>
                        <div className="action-buttons">
                          <button className="btn-icon edit" title="Chỉnh sửa" onClick={() => handleEdit(pub)}>
                            <FaEdit />
                          </button>
                          <button className="btn-icon delete" title="Xóa" onClick={() => handleDelete(pub.id)}>
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy nhà xuất bản nào
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
            Hiển thị {startIndex + 1}-{Math.min(endIndex, filteredPublishers.length)} / {filteredPublishers.length} nhà xuất bản (Trang {currentPage}/{totalPages})
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>{editingPublisher ? 'Chỉnh sửa NXB' : 'Thêm NXB'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <FaTimes />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Tên NXB:</label>
                  <input type="text" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} required />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>Hủy</button>
                <button type="submit" className="btn-primary">{editingPublisher ? 'Cập nhật' : 'Thêm'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default PublisherManagement;
