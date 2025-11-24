import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import '../styles/RegisterPage.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    name: '',
    phone: '',
    address: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    // Validation
    if (formData.password !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp.');
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError('Mật khẩu phải có ít nhất 6 ký tự.');
      setLoading(false);
      return;
    }

    try {
      // Register user in auth-service
      const registerResponse = await axios.post('http://localhost:8083/auth/register', {
        email: formData.email,
        password: formData.password,
        role: 'USER'
      });

      const user = registerResponse.data;

      // Create profile in user-service
      await axios.post('http://localhost:8081/users/profile', {
        userId: user.id,
        name: formData.name,
        phone: formData.phone,
        address: formData.address,
        borrowLock: false,
        totalFine: 0
      });

      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login');
    } catch (err) {
      console.error('Registration failed:', err);
      let errorMessage = 'Đăng ký thất bại. Vui lòng thử lại.';
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.message === 'Network Error') {
        errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra các service đang chạy.';
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      {/* PHẦN 1: HÌNH ẢNH MINH HỌA (BÊN TRÁI) */}
      <div className="register-banner">
        <div className="banner-content">
          <div className="brand-logo">Your <span>Book</span> Shelf
          </div>
          <p className="quote">
            "Tham gia cộng đồng đọc sách của chúng tôi."
            <br/><span>- Chào mừng bạn! -</span>
          </p>
        </div>
        {/* Ảnh nền mờ hoặc hình minh họa */}
        <img 
          src="https://placehold.co/800x1000/F65D4E/white?text=Join+Our+Library" 
          alt="Register Banner" 
          className="banner-image"
        />
      </div>

      {/* PHẦN 2: FORM ĐĂNG KÝ (BÊN PHẢI) */}
      <div className="register-form-wrapper">
        <div className="form-box">
          <div className="form-header">
            <h2>Tạo tài khoản mới</h2>
            <p>Vui lòng nhập thông tin để đăng ký.</p>
          </div>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit}>
            
            {/* Input Email */}
            <div className="input-group">
              <label htmlFor="email">Email</label>
              <div className="input-wrapper">
                <i className="fas fa-envelope"></i>
                <input 
                  type="email" 
                  id="email" 
                  name="email"
                  placeholder="name@example.com" 
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Input Password */}
            <div className="input-group">
              <label htmlFor="password">Mật khẩu</label>
              <div className="input-wrapper">
                <i className="fas fa-lock"></i>
                <input 
                  type="password" 
                  id="password" 
                  name="password"
                  placeholder="••••••••" 
                  value={formData.password}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Input Confirm Password */}
            <div className="input-group">
              <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
              <div className="input-wrapper">
                <i className="fas fa-lock"></i>
                <input 
                  type="password" 
                  id="confirmPassword" 
                  name="confirmPassword"
                  placeholder="••••••••" 
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Input Name */}
            <div className="input-group">
              <label htmlFor="name">Họ tên</label>
              <div className="input-field">
                <i className="fas fa-user"></i>
                <input 
                  type="text" 
                  id="name" 
                  name="name"
                  placeholder="Nguyễn Văn A" 
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Input Phone */}
            <div className="input-group">
              <label htmlFor="phone">Số điện thoại</label>
              <div className="input-field">
                <i className="fas fa-phone"></i>
                <input 
                  type="tel" 
                  id="phone" 
                  name="phone"
                  placeholder="0123456789" 
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Input Address */}
            <div className="input-group">
              <label htmlFor="address">Địa chỉ</label>
              <div className="input-field">
                <i className="fas fa-map-marker-alt"></i>
                <input 
                  type="text" 
                  id="address" 
                  name="address"
                  placeholder="123 Đường ABC, Quận XYZ" 
                  value={formData.address}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Register Button */}
            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? 'Đang đăng ký...' : 'Đăng ký'}
            </button>
          </form>

          {/* Register Link */}
          <div className="form-footer">
            <p>Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
          </div>
        </div>
      </div>

    </div>
  );
};

export default RegisterPage;