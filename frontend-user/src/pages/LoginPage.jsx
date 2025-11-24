import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/LoginPage.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8083/auth/login', {
        email,
        password
      });
        const { accessToken, refreshToken, userId, email: userEmail } = response.data;
      // Lưu token và userId
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userId', userId);
        localStorage.setItem('currentUserEmail', userEmail);

      // Lấy thông tin người dùng từ user-service
      try {
        const profileResponse = await axios.get(`http://localhost:8081/users/${userId}/profile`, {
          headers: { Authorization: `Bearer ${accessToken}` }
        });
        const profile = profileResponse.data;
        if (profile?.name) {
          localStorage.setItem('userName', profile.name);
        }
          localStorage.setItem('userProfile', JSON.stringify(profileResponse.data));
      } catch (profileError) {
        console.warn('Unable to load profile from user-service', profileError);
          alert('Đăng nhập thành công nhưng không thể tải profile người dùng.');
      }

      // Chuyển hướng đến HomePage
      navigate('/home');
    } catch (error) {
      console.error('Login failed:', error);
      console.log('Error response:', error.response);
      let errorMessage = 'Lỗi không xác định';
      if (error.message === 'Network Error') {
        errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra auth-service có đang chạy trên port 8083.';
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
          <div className="brand-logo">Your <span>Book</span> Shelf
          </div>
          <p className="quote">
            "Sách là giấc mơ bạn cầm trên tay."
            <br/><span>- Neil Gaiman -</span>
          </p>
        </div>
        {/* Ảnh nền mờ hoặc hình minh họa */}
        <img 
          src="https://placehold.co/800x1000/F65D4E/white?text=Digital+Public+Library" 
          alt="Login Banner" 
          className="banner-image"
        />
      </div>

      {/* PHẦN 2: FORM ĐĂNG NHẬP (BÊN PHẢI) */}
      <div className="login-form-wrapper">
        <div className="form-box">
          <div className="form-header">
            <h2>Chào mừng trở lại!</h2>
            <p>Vui lòng nhập thông tin để đăng nhập.</p>
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
                  placeholder="name@example.com" 
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
                <span>Ghi nhớ tôi</span>
              </label>
              <a href="#" className="forgot-password">Quên mật khẩu?</a>
            </div>

            {/* Login Button */}
            <button type="submit" className="btn-submit">Đăng Nhập</button>
          </form>

          {/* Social Login */}
          <div className="social-login">
            <p>Hoặc đăng nhập với</p>
            <div className="social-icons">
              <button className="btn-social google">
                <i className="fab fa-google"></i> Google
              </button>
              <button className="btn-social facebook">
                <i className="fab fa-facebook-f"></i> Facebook
              </button>
            </div>
          </div>

          {/* Register Link */}
          <div className="form-footer">
            <p>Chưa có tài khoản? <a href="#">Đăng ký ngay</a></p>
          </div>
        </div>
      </div>

    </div>
  );
};

export default LoginPage;