import React from 'react';
import { FaBars, FaSearch, FaBell, FaEnvelope } from 'react-icons/fa';
import '../styles/Dashboard.css';

const TopBar = ({ toggleSidebar }) => {
  const userName = localStorage.getItem('userName') || 'Admin';
  const userEmail = localStorage.getItem('currentUserEmail') || '';

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
        <div className="user-profile">
          <div className="user-info">
            <div className="user-name">{userName}</div>
            <div className="user-email">{userEmail}</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TopBar;
