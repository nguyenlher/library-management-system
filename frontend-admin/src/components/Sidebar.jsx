// File: src/components/Sidebar.jsx
import React from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/Dashboard.css';

const Sidebar = ({ isOpen, toggleSidebar, onLogout }) => {
  const location = useLocation();
  const menuItems = [
    { name: "Tổng quan", path: "/" },
    { name: "Người dùng", path: "/users" },
    { name: "Sách", path: "/books" },
    { name: "Tác giả", path: "/authors" },
    { name: "Nhà xuất bản", path: "/publishers" },
    { name: "Mượn/Trả", path: "/borrows" },
    { name: "Phí phạt", path: "/fines" },
  ];

  const handleLogout = () => {
    if (window.confirm('Bạn có chắc chắn muốn đăng xuất?')) {
      if (onLogout) {
        onLogout();
      }
    }
  };

  return (
    <div className={`sidebar ${isOpen ? "open" : "closed"}`}>
      <div className="sidebar-header">
        <div className="logo-area">
          <div className="logo-box" onClick={toggleSidebar} style={{ cursor: 'pointer' }}>Q</div>
          {isOpen && <span className="logo-text">Quản trị viên</span>}
        </div>
        {isOpen && (
          <button className="toggle-btn" onClick={toggleSidebar}>
            {'<'}
          </button>
        )}
      </div>

      <ul className="sidebar-menu">
        {menuItems.map((item, index) => (
          <li key={index} className={`menu-item ${location.pathname === item.path ? 'active' : ''}`}>
            <a href={item.path}>
              <span className="text-icon">{item.name.charAt(0)}</span>
              {isOpen && <span className="label">{item.name}</span>}
            </a>
          </li>
        ))}
      </ul>

      <div className="sidebar-footer">
        <div className="menu-item logout">
          <a onClick={handleLogout} style={{ cursor: 'pointer' }}>
            <span className="text-icon">X</span>
            {isOpen && <span className="label">Đăng xuất</span>}
          </a>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;