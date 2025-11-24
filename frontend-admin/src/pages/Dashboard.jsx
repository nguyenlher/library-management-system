// File: src/components/Dashboard.jsx
import React from 'react';
import '../styles/Dashboard.css';
import { FaUserFriends, FaBox, FaChartPie } from 'react-icons/fa';

// Chỉ giữ lại phần Nội dung (Content)
const Dashboard = () => {
  return (
    <div className="dashboard-content">
      <h2 className="page-title">Tổng quan hệ thống</h2>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card purple">
          <div className="stat-icon"><FaUserFriends /></div>
          <div className="stat-info">
            <h3>Tổng User</h3>
            <p>12,345</p>
            <span className="trend up">+5.2% tuần này</span>
          </div>
        </div>
        <div className="stat-card blue">
          <div className="stat-icon"><FaBox /></div>
          <div className="stat-info">
            <h3>Sản phẩm</h3>
            <p>845</p>
            <span className="trend up">+1.5% tuần này</span>
          </div>
        </div>
        <div className="stat-card orange">
          <div className="stat-icon"><FaChartPie /></div>
          <div className="stat-info">
            <h3>Doanh thu</h3>
            <p>$45,200</p>
            <span className="trend down">-0.8% tuần này</span>
          </div>
        </div>
      </div>

      {/* Content Grid (Chart & Table) */}
      <div className="content-grid">
        <div className="card chart-section">
          <div className="card-header">
            <h3>Biểu đồ tăng trưởng</h3>
          </div>
          <div className="fake-chart">
            {[60, 40, 75, 50, 90, 65, 80].map((h, i) => (
                <div key={i} className="bar-wrapper">
                    <div className="bar" style={{height: `${h}%`}}></div>
                    <span className="bar-label">T{i+2}</span>
                </div>
            ))}
          </div>
        </div>

        <div className="card table-section">
          <div className="card-header">
            <h3>Đơn hàng gần đây</h3>
            <button className="btn-sm">Xem tất cả</button>
          </div>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Khách hàng</th>
                <th>Trạng thái</th>
                <th>Giá trị</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>#1234</td>
                <td>Nguyễn Văn A</td>
                <td><span className="status completed">Hoàn thành</span></td>
                <td>$120.00</td>
              </tr>
              <tr>
                <td>#1235</td>
                <td>Trần Thị B</td>
                <td><span className="status pending">Chờ xử lý</span></td>
                <td>$85.50</td>
              </tr>
              <tr>
                <td>#1236</td>
                <td>Lê Văn C</td>
                <td><span className="status cancel">Đã hủy</span></td>
                <td>$45.00</td>
              </tr>
               <tr>
                <td>#1237</td>
                <td>Phạm Văn D</td>
                <td><span className="status completed">Hoàn thành</span></td>
                <td>$210.00</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;