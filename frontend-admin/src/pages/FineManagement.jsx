import React, { useState, useEffect } from 'react';
import { FaBook, FaUser, FaMoneyBillWave, FaSearch, FaCheck, FaChevronLeft, FaChevronRight, FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import '../styles/Dashboard.css';

const FineManagement = () => {
  const [fines, setFines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(8);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingFine, setEditingFine] = useState(null);
  const [formData, setFormData] = useState({ borrowId: '', userId: '', amount: '', reason: 'LATE' });

  useEffect(() => {
    fetchFines();
  }, []);

  const fetchFines = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8086/fines');
      if (!response.ok) {
        console.error('Fines endpoint returned', response.status);
        setFines([]);
        return;
      }
      const data = await response.json();

      const usersRes = await fetch('http://localhost:8081/users');
      const users = usersRes.ok ? await usersRes.json() : [];

      const userMap = {};
      users.forEach(u => { if (u.userId != null) userMap[u.userId] = u.name; });

      const enriched = Array.isArray(data) ? data.map(fine => ({
        ...fine,
        userName: userMap[fine.userId] || 'N/A'
      })) : [];

      setFines(enriched);
    } catch (error) {
      console.error('Error fetching fines:', error);
    } finally {
      setLoading(false);
    }
  };

    const filteredFines = fines.filter(fine => (
      fine.userName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (fine.reason?.toLowerCase().includes(searchTerm.toLowerCase()))
    ));

    const formatPaidAmount = (fine) => {
      const paidValue = fine.paid ? fine.amount : 0;
      return paidValue ? `${paidValue.toLocaleString('vi-VN')} ₫` : '0 ₫';
    };

  const totalPages = Math.ceil(filteredFines.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentFines = filteredFines.slice(startIndex, startIndex + itemsPerPage);

  const formatDate = (value) => {
    if (!value) return 'N/A';
    return new Date(value).toLocaleDateString('vi-VN');
  };

  const handlePayFine = async (fineId) => {
    if (!window.confirm('Xác nhận đã thanh toán phí phạt?')) return;
    try {
      await fetch(`http://localhost:8086/fines/${fineId}/pay`, { method: 'PUT' });
      fetchFines();
    } catch (error) {
      console.error('Error paying fine:', error);
    }
  };

  const handleAddFine = async () => {
    try {
      const response = await fetch('http://localhost:8086/fines', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          borrowId: parseInt(formData.borrowId),
          userId: parseInt(formData.userId),
          amount: parseFloat(formData.amount),
          reason: formData.reason
        })
      });
      if (response.ok) {
        setShowAddModal(false);
        setFormData({ borrowId: '', userId: '', amount: '', reason: 'LATE' });
        fetchFines();
      }
    } catch (error) {
      console.error('Error adding fine:', error);
    }
  };

  const handleEditFine = (fine) => {
    setEditingFine(fine);
    setFormData({
      borrowId: fine.borrowId,
      userId: fine.userId,
      amount: fine.amount,
      reason: fine.reason
    });
    setShowAddModal(true);
  };

  const handleUpdateFine = async () => {
    try {
      const response = await fetch(`http://localhost:8086/fines/${editingFine.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          amount: parseFloat(formData.amount),
          reason: formData.reason
        })
      });
      if (response.ok) {
        setShowAddModal(false);
        setEditingFine(null);
        setFormData({ borrowId: '', userId: '', amount: '', reason: 'LATE' });
        fetchFines();
      }
    } catch (error) {
      console.error('Error updating fine:', error);
    }
  };

  const handleDeleteFine = async (fineId) => {
    if (!window.confirm('Xác nhận xóa phí phạt?')) return;
    try {
      await fetch(`http://localhost:8086/fines/${fineId}`, { method: 'DELETE' });
      fetchFines();
    } catch (error) {
      console.error('Error deleting fine:', error);
    }
  };

  const handleSubmit = () => {
    if (editingFine) {
      handleUpdateFine();
    } else {
      handleAddFine();
    }
  };

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý phí phạt</h2>
        <button className="btn-primary" onClick={() => { setEditingFine(null); setFormData({ borrowId: '', userId: '', amount: '', reason: 'LATE' }); setShowAddModal(true); }}>
          <FaPlus /> Thêm phạt
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm theo sách, người mượn, lý do..."
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
                  <th>Người mượn</th>
                  <th>Số tiền</th>
                  <th>Lý do</th>
                  <th>Tiền đã trả (paid)</th>
                  <th>Ngày tạo</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {currentFines.length > 0 ? (
                  currentFines.map((fine, index) => (
                    <tr key={fine.id}>
                      <td>{startIndex + index + 1}</td>
                      <td>{fine.userName || 'N/A'}</td>
                      <td>{fine.amount ? `${fine.amount.toLocaleString('vi-VN')} ₫` : 'N/A'}</td>
                      <td>{fine.reason || 'N/A'}</td>
                      <td>{formatPaidAmount(fine)}</td>
                      <td>{formatDate(fine.createdAt)}</td>
                      <td className={`status ${fine.paid ? 'completed' : 'pending'}`}>
                        {fine.paid ? 'Đã thanh toán' : 'Chưa thanh toán'}
                      </td>
                      <td>
                        <div className="action-buttons">
                          {!fine.paid && (
                            <button className="btn-icon edit" title="Đánh dấu đã thanh toán" onClick={() => handlePayFine(fine.id)}>
                              <FaCheck />
                            </button>
                          )}
                          <button className="btn-icon edit" title="Sửa" onClick={() => handleEditFine(fine)}>
                            <FaEdit />
                          </button>
                          <button className="btn-icon delete" title="Xóa" onClick={() => handleDeleteFine(fine.id)}>
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="8" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy phí phạt nào
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
            Hiển thị {startIndex + 1}-{Math.min(startIndex + currentFines.length, filteredFines.length)} / {filteredFines.length} phí phạt (Trang {currentPage}/{totalPages})
          </div>
        </div>
      </div>

      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editingFine ? 'Sửa phí phạt' : 'Thêm phí phạt'}</h3>
            <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
              {!editingFine && (
                <>
                  <div className="form-group">
                    <label>Borrow ID:</label>
                    <input type="number" value={formData.borrowId} onChange={(e) => setFormData({ ...formData, borrowId: e.target.value })} required />
                  </div>
                  <div className="form-group">
                    <label>User ID:</label>
                    <input type="number" value={formData.userId} onChange={(e) => setFormData({ ...formData, userId: e.target.value })} required />
                  </div>
                </>
              )}
              <div className="form-group">
                <label>Số tiền:</label>
                <input type="number" step="0.01" value={formData.amount} onChange={(e) => setFormData({ ...formData, amount: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Lý do:</label>
                <select value={formData.reason} onChange={(e) => setFormData({ ...formData, reason: e.target.value })}>
                  <option value="LATE">Trễ hạn</option>
                  <option value="LOST">Mất sách</option>
                  <option value="DAMAGE">Hư hỏng</option>
                </select>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowAddModal(false)}>Hủy</button>
                <button type="submit" className="btn-primary">{editingFine ? 'Cập nhật' : 'Thêm'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default FineManagement;