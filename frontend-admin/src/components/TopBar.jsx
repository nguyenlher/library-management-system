import React from 'react';
import { FaBars, FaSearch, FaBell, FaEnvelope } from 'react-icons/fa';
import '../styles/Dashboard.css';

const TopBar = ({ toggleSidebar }) => {
  return (
    <div className="topbar">
      <div className="topbar-left">
        <button className="mobile-toggle" onClick={toggleSidebar}>
          <FaBars />
        </button>
        <div className="search-box">
          <FaSearch />
          <input type="text" placeholder="Tìm kiếm..." />
        </div>
      </div>
      <div className="topbar-right">
        <div className="icon-btn"><FaBell /><span className="badge">3</span></div>
        <div className="icon-btn"><FaEnvelope /><span className="badge">5</span></div>
        <div className="user-profile">
          <div className="avatar-placeholder">AD</div>
          <div className="user-info">
            <span className="name">Admin User</span>
            <span className="role">Super Admin</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TopBar;
