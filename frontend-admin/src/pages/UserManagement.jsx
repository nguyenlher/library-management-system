import React, { useState, useEffect } from 'react';
import { FaUserPlus, FaEdit, FaTrash, FaSearch, FaTimes } from 'react-icons/fa';
import '../styles/Dashboard.css';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [formData, setFormData] = useState({
    email: '',
    fullName: '',
    phoneNumber: '',
    address: '',
    password: '',
    borrowLock: false,
    status: 'ACTIVE'
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      // Fetch users from auth-service
      const userResponse = await fetch('http://localhost:8083/users');
      const usersData = await userResponse.json();
      const regularUsers = usersData.filter(user => user.role === 'USER');

      // Fetch profiles from user-service
      const profileResponse = await fetch('http://localhost:8081/users');
      const profilesData = await profileResponse.json();

      // Create map of userId to profile
      const profileMap = {};
      profilesData.forEach(profile => {
        profileMap[profile.userId] = profile;
      });

      // Merge user data with profile data
      const usersWithProfiles = regularUsers.map(user => ({
        ...user,
        fullName: profileMap[user.id]?.name || 'N/A',
        phoneNumber: profileMap[user.id]?.phone || 'N/A',
        address: profileMap[user.id]?.address || 'N/A',
        borrowLock: profileMap[user.id]?.borrowLock || false
      }));

      setUsers(usersWithProfiles);
    } catch (error) {
      console.error('Error fetching users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = () => {
    setEditingUser(null);
    setFormData({
      email: '',
      fullName: '',
      phoneNumber: '',
      address: '',
      password: 'password123',
      borrowLock: false,
      status: 'ACTIVE'
    });
    setShowModal(true);
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    setFormData({
      email: user.email,
      fullName: user.fullName,
      phoneNumber: user.phoneNumber,
      address: user.address,
      password: '',
      borrowLock: user.borrowLock,
      status: user.status || 'ACTIVE'
    });
    setShowModal(true);
  };

  const handleDelete = async (userId) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
      try {
        // Delete profile first
        await fetch(`http://localhost:8081/users/${userId}/profile`, {
          method: 'DELETE',
        });
        // Then delete user
        await fetch(`http://localhost:8083/users/${userId}`, {
          method: 'DELETE',
        });
        fetchUsers(); // Refresh danh sách
      } catch (error) {
        console.error('Error deleting user:', error);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingUser) {
        // Update user status in auth-service
        const userUpdateResponse = await fetch(`http://localhost:8083/users/${editingUser.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: formData.email,
            status: formData.status,
            role: 'USER'
          }),
        });
        if (!userUpdateResponse.ok) throw new Error('Failed to update user');

        // Update profile
        const profileResponse = await fetch(`http://localhost:8081/users/profile/${editingUser.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            name: formData.fullName,
            phone: formData.phoneNumber,
            address: formData.address,
            borrowLock: formData.borrowLock,
            totalFine: 0
          }),
        });
        if (!profileResponse.ok) throw new Error('Failed to update profile');
      } else {
        // Add new user
        // First create user in auth-service
        const userResponse = await fetch('http://localhost:8083/users', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: formData.email,
            password: formData.password,
            role: 'USER'
          }),
        });
        if (!userResponse.ok) throw new Error('Failed to create user');
        const newUser = await userResponse.json();

        // Then create profile in user-service
        const profileResponse = await fetch('http://localhost:8081/users/profile', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            userId: newUser.id,
            name: formData.fullName,
            phone: formData.phoneNumber,
            address: formData.address,
            borrowLock: false,
            totalFine: 0
          }),
        });
        if (!profileResponse.ok) throw new Error('Failed to create profile');
      }
      setShowModal(false);
      fetchUsers(); // Refresh list
    } catch (error) {
      console.error('Error saving user:', error);
      alert('Có lỗi xảy ra khi lưu người dùng');
    }
  };

  const getStatusDisplay = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'Hoạt động';
      case 'INACTIVE':
        return 'Ngưng hoạt động';
      default:
        return status || 'Hoạt động';
    }
  };

  const filteredUsers = users.filter(user =>
    user.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.fullName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="dashboard-content">
      <div className="page-header">
        <h2 className="page-title">Quản lý Người dùng</h2>
        <button className="btn-primary" onClick={handleAdd}>
          <FaUserPlus /> Thêm người dùng
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <div className="search-box-table">
            <FaSearch />
            <input
              type="text"
              placeholder="Tìm kiếm theo tên, email..."
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
                  <th>ID</th>
                  <th>Họ tên</th>
                  <th>Email</th>
                  <th>Số điện thoại</th>
                  <th>Địa chỉ</th>
                  <th>Khóa Mượn</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.length > 0 ? (
                  filteredUsers.map((user) => (
                    <tr key={user.id}>
                      <td>{user.id}</td>
                      <td>{user.fullName || 'N/A'}</td>
                      <td>{user.email}</td>
                      <td>{user.phoneNumber || 'N/A'}</td>
                      <td>{user.address || 'N/A'}</td>
                      <td>{user.borrowLock ? 'Có' : 'Không'}</td>
                      <td>{getStatusDisplay(user.status)}</td>
                      <td>
                        <div className="action-buttons">
                          <button className="btn-icon edit" title="Chỉnh sửa" onClick={() => handleEdit(user)}>
                            <FaEdit />
                          </button>
                          <button
                            className="btn-icon delete"
                            title="Xóa"
                            onClick={() => handleDelete(user.id)}
                          >
                            <FaTrash />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="8" style={{ textAlign: 'center', padding: '30px' }}>
                      Không tìm thấy người dùng nào
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          )}
        </div>

        <div className="card-footer">
          <div className="pagination-info">
            Hiển thị {filteredUsers.length} / {users.length} người dùng
          </div>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>{editingUser ? 'Chỉnh sửa người dùng' : 'Thêm người dùng'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <FaTimes />
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Email:</label>
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    required
                    disabled={!!editingUser}
                  />
                </div>
                <div className="form-group">
                  <label>Họ tên:</label>
                  <input
                    type="text"
                    value={formData.fullName}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Số điện thoại:</label>
                  <input
                    type="text"
                    value={formData.phoneNumber}
                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <label>Địa chỉ:</label>
                  <input
                    type="text"
                    value={formData.address}
                    onChange={(e) => setFormData({...formData, address: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <div className="checkbox-group">
                    <label htmlFor="borrowLock">Khóa Mượn:</label>
                    <input
                      type="checkbox"
                      id="borrowLock"
                      checked={formData.borrowLock}
                      onChange={(e) => setFormData({...formData, borrowLock: e.target.checked})}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label>Trạng thái:</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value})}
                  >
                    <option value="ACTIVE">Hoạt động</option>
                    <option value="INACTIVE">Ngưng hoạt động</option>
                  </select>
                </div>
                {!editingUser && (
                  <div className="form-group">
                    <label>Mật khẩu:</label>
                    <input
                      type="password"
                      value={formData.password}
                      onChange={(e) => setFormData({...formData, password: e.target.value})}
                      required
                    />
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>
                  Hủy
                </button>
                <button type="submit" className="btn-primary">
                  {editingUser ? 'Cập nhật' : 'Thêm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserManagement;
