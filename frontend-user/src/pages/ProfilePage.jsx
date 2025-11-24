import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/ProfilePage.css';

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');
  const accessToken = localStorage.getItem('accessToken');
  const userEmail = localStorage.getItem('currentUserEmail');

  useEffect(() => {
    if (!userId || !accessToken) {
      navigate('/login');
      return;
    }
    fetchProfile();
  }, [userId, accessToken, navigate]);

  const fetchProfile = async () => {
    try {
      const response = await axios.get(`http://localhost:8081/users/${userId}/profile`, {
        headers: { Authorization: `Bearer ${accessToken}` }
      });
      setProfile(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Failed to fetch profile:', err);
      setError('Không thể tải thông tin cá nhân. Vui lòng thử lại.');
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    // Delay 3 giây trước khi thực hiện cập nhật
    setTimeout(async () => {
      try {
        const updateData = {
          name: profile.name,
          phone: profile.phone,
          address: profile.address
        };

        await axios.put(`http://localhost:8081/users/profile/${profile.id}`, updateData, {
          headers: { Authorization: `Bearer ${accessToken}` }
        });

        setSuccess('Cập nhật thông tin thành công!');
        localStorage.setItem('userName', profile.name);
        localStorage.setItem('userProfile', JSON.stringify(profile));
      } catch (err) {
        console.error('Failed to update profile:', err);
        setError('Cập nhật thất bại. Vui lòng thử lại.');
      } finally {
        setSaving(false);
      }
    }, 3000); // 3000ms = 3 giây
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({ ...prev, [name]: value }));
  };

  const formatPrice = (price) => {
    if (!price) return '0đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  if (loading) {
    return (
      <>
        <Header />
        <div className="profile-container">
          <div className="loading-spinner">
            <i className="fas fa-spinner fa-spin"></i> Đang tải...
          </div>
        </div>
        <Footer />
      </>
    );
  }

  if (!profile) {
    return (
      <>
        <Header />
        <div className="profile-container">
          <div className="error-message">Không tìm thấy thông tin cá nhân.</div>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <Header />
      <div className="profile-container">
        <h1>Thông tin cá nhân</h1>

        <div className="profile-content">
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}

          <div className="profile-card">
            <div className="profile-header">
              <div className="avatar-section">
                <div className="avatar">
                  <i className="fas fa-user"></i>
                </div>
                <div className="user-info">
                  <h2>{profile.name || 'Chưa cập nhật'}</h2>
                  <p>{userEmail}</p>
                </div>
              </div>
            </div>

            <form onSubmit={handleSave} className="profile-form">
              <div className="form-section">
                <h3>Thông tin cơ bản</h3>

                <div className="form-group">
                  <label htmlFor="email">Email</label>
                  <div className="input-wrapper">
                    <input
                      type="email"
                      id="email"
                      value={userEmail || ''}
                      readOnly
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="name">Họ tên *</label>
                  <div className="input-wrapper">
                    <input
                      type="text"
                      id="name"
                      name="name"
                      placeholder="Nhập họ tên"
                      value={profile.name || ''}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="phone">Số điện thoại</label>
                  <div className="input-wrapper">
                    <input
                      type="tel"
                      id="phone"
                      name="phone"
                      placeholder="Nhập số điện thoại"
                      value={profile.phone || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="address">Địa chỉ</label>
                  <div className="input-wrapper">
                    <input
                      type="text"
                      id="address"
                      name="address"
                      placeholder="Nhập địa chỉ"
                      value={profile.address || ''}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h3>Thông tin tài khoản</h3>

                <div className="info-grid">
                  <div className="info-item">
                    <span className="label">Tổng phạt</span>
                    <span className="value fine-amount">{formatPrice(profile.totalFine || 0)}</span>
                  </div>

                  <div className="info-item">
                    <span className="label">Trạng thái mượn</span>
                    <span className={`value ${profile.borrowLock ? 'status-locked' : 'status-active'}`}>
                      {profile.borrowLock ? 'Đã khóa' : 'Hoạt động'}
                    </span>
                  </div>
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" className="save-button" disabled={saving}>
                  {saving ? (
                    <>
                      <i className="fas fa-spinner fa-spin"></i> Đang lưu...
                    </>
                  ) : (
                    <>
                      <i className="fas fa-save"></i> Lưu thay đổi
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default ProfilePage;