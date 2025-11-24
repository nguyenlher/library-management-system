import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/LoginPage.css';

const LoginPage = ({ onLogin }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const apiBaseUrl = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8083';
      const response = await axios.post(`${apiBaseUrl}/auth/login`, {
        email,
        password
      });
      console.log('Admin Login response:', response.data); // Debug log

      const { accessToken, refreshToken, userId, email: userEmail } = response.data;

      // Lưu token và userId
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userId', userId);
      localStorage.setItem('currentUserEmail', userEmail);

      // Decode JWT token để lấy role
      const decodeJWT = (token) => {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          return payload;
        } catch (error) {
          console.error('Error decoding JWT:', error);
          return {};
        }
      };

      const tokenPayload = decodeJWT(accessToken);
      const userRole = tokenPayload.role || 'USER';
      console.log('Decoded JWT payload:', tokenPayload); // Debug log
      console.log('Role from JWT:', userRole); // Debug log

      // Kiểm tra quyền admin
      if (userRole !== 'ADMIN' && userRole !== 'LIBRARIAN') {
        alert('Bạn không có quyền truy cập trang quản trị!');
        // Clear tokens
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('currentUserEmail');
        return;
      }

      // Lấy thông tin profile (tùy chọn)
      try {
        const userServiceUrl = process.env.REACT_APP_USER_SERVICE_URL || 'http://localhost:8081';
        const profileResponse = await axios.get(`${userServiceUrl}/users/${userId}/profile`, {
          headers: { Authorization: `Bearer ${accessToken}` }
        });
        const profile = profileResponse.data;
        console.log('Profile response:', profile); // Debug log

        if (profile?.name) {
          localStorage.setItem('userName', profile.name);
        }
        localStorage.setItem('userProfile', JSON.stringify(profile));
      } catch (profileError) {
        console.warn('Unable to load profile from user-service', profileError);
      }

      // Lưu role và điều hướng đến dashboard
      localStorage.setItem('userRole', userRole);
      console.log('Redirecting to admin dashboard'); // Debug log
      
      // Call onLogin callback to update authentication state
      if (onLogin) {
        onLogin();
      }
      
      navigate('/dashboard');
    } catch (error) {
      console.error('Admin login failed:', error);
      console.log('Error response:', error.response);
      let errorMessage = 'Lỗi không xác định';
      if (error.message === 'Network Error') {
        errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra auth-service có đang chạy.';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      alert('Đăng nhập thất bại: ' + errorMessage);
    }
  };

  return (
    <div className="login-container">

      {/* PHẦN 1: HÌNH ẢNH MINH HỌA (BÊN TRÁI) */}
      <div className="login-banner">
        <div className="banner-content">
          <div className="brand-logo">Admin <span>Panel</span>
          </div>
          <p className="quote">
            "Quản lý thư viện hiệu quả và chuyên nghiệp."
            <br/><span>- Library Management System -</span>
          </p>
        </div>
        {/* Ảnh nền mờ hoặc hình minh họa */}
        <img
          src="https://placehold.co/800x1000/2B2B2B/white?text=Admin+Dashboard"
          alt="Admin Login Banner"
          className="banner-image"
        />
      </div>

      {/* PHẦN 2: FORM ĐĂNG NHẬP (BÊN PHẢI) */}
      <div className="login-form-wrapper">
        <div className="form-box">
          <div className="form-header">
            <h2>Đăng nhập Admin</h2>
            <p>Vui lòng nhập thông tin để truy cập hệ thống quản trị.</p>
          </div>

          <form onSubmit={handleSubmit}>

            {/* Input Email */}
            <div className="input-group">
              <label htmlFor="email">Email</label>
              <div className="input-field">
                <i className="fas fa-envelope"></i>
                <input
                  type="email"
                  id="email"
                  placeholder="admin@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            {/* Input Password */}
            <div className="input-group">
              <label htmlFor="password">Mật khẩu</label>
              <div className="input-field">
                <i className="fas fa-lock"></i>
                <input
                  type="password"
                  id="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            {/* Remember Me & Forgot Password */}
            <div className="form-actions">
              <label className="remember-me">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                />
                Ghi nhớ đăng nhập
              </label>
              <a href="#" className="forgot-password">Quên mật khẩu?</a>
            </div>

            {/* Login Button */}
            <button type="submit" className="btn-submit">Đăng Nhập</button>
          </form>

          {/* Back to User Portal Link */}
          <div className="form-footer">
            <p>Quay lại trang người dùng? <a href={process.env.REACT_APP_USER_PORTAL_URL || "http://localhost:3000"} target="_blank" rel="noopener noreferrer">User Portal</a></p>
          </div>
        </div>
      </div>

    </div>
  );
};

export default LoginPage;