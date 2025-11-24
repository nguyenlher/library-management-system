// File: src/components/Dashboard.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/Dashboard.css';
import { FaUserFriends, FaBox, FaBookOpen, FaExclamationTriangle } from 'react-icons/fa';

// Chỉ giữ lại phần Nội dung (Content)
const Dashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalBooks: 0,
    totalBorrows: 0,
    totalFines: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        setError(null);

        const [usersRes, booksRes, borrowsRes, finesRes] = await Promise.allSettled([
          axios.get('http://localhost:8083/users?role=USER'), // User service
          axios.get('http://localhost:8082/books'), // Book service
          axios.get('http://localhost:8086/borrows'), // Borrow service
          axios.get('http://localhost:8086/fines') // Fine service
        ]);

        // Xử lý response
        const totalUsers = usersRes.status === 'fulfilled' 
          ? usersRes.value.data.length 
          : 0;
        
        const totalBooks = booksRes.status === 'fulfilled' 
          ? booksRes.value.data.length 
          : 0;
        
        const totalBorrows = borrowsRes.status === 'fulfilled' 
          ? borrowsRes.value.data.length 
          : 0;
        
        const totalFines = finesRes.status === 'fulfilled' 
          ? finesRes.value.data.length 
          : 0;

        setStats({
          totalUsers,
          totalBooks,
          totalBorrows,
          totalFines
        });

      } catch (err) {
        console.error('Error fetching dashboard stats:', err);
        setError('Không thể tải dữ liệu thống kê');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const formatNumber = (num) => {
    return new Intl.NumberFormat('vi-VN').format(num);
  };

  return (
    <div className="dashboard-content">
      <h2 className="page-title">Tổng quan hệ thống</h2>

      {error && <div className="error-message">{error}</div>}

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card purple">
          <div className="stat-icon"><FaUserFriends /></div>
          <div className="stat-info">
            <h3>Tổng Người Dùng</h3>
            <p>{loading ? '...' : formatNumber(stats.totalUsers)}</p>
            <span className="trend up">Người dùng</span>
          </div>
        </div>
        <div className="stat-card blue">
          <div className="stat-icon"><FaBox /></div>
          <div className="stat-info">
            <h3>Tổng Sách</h3>
            <p>{loading ? '...' : formatNumber(stats.totalBooks)}</p>
            <span className="trend up">Sách trong thư viện</span>
          </div>
        </div>
        <div className="stat-card orange">
          <div className="stat-icon"><FaBookOpen /></div>
          <div className="stat-info">
            <h3>Tổng Mượn</h3>
            <p>{loading ? '...' : formatNumber(stats.totalBorrows)}</p>
            <span className="trend up">Lượt mượn sách</span>
          </div>
        </div>
        <div className="stat-card red">
          <div className="stat-icon"><FaExclamationTriangle /></div>
          <div className="stat-info">
            <h3>Tổng Phạt</h3>
            <p>{loading ? '...' : formatNumber(stats.totalFines)}</p>
            <span className="trend down">Lượt vi phạm</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;