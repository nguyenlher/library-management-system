import React, { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Header = () => {
  const [userName, setUserName] = useState(() => localStorage.getItem('userName'));
  const [categories, setCategories] = useState([]);
  const [categoryError, setCategoryError] = useState(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const dropdownRef = useRef(null);
  const userMenuRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleStorage = () => setUserName(localStorage.getItem('userName'));
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  useEffect(() => {
    let cancelled = false;
    setCategoryError(null);
    axios.get('http://localhost:8082/books/categories')
      .then((response) => {
        if (!cancelled) {
          setCategories(response.data || []);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setCategoryError('Không thể tải danh mục');
        }
      });
    return () => { cancelled = true; };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userId');
    setUserName(null);
    setIsUserMenuOpen(false);
    navigate('/login');
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setIsUserMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <header className="header">
      <div className="logo">Book<span>Shelf</span></div>
      <ul className="nav-links">
        <li><Link to="/" className="active">Trang chủ</Link></li>
        <li
          className="dropdown"
          ref={dropdownRef}
          onMouseEnter={() => setIsDropdownOpen(true)}
          onMouseLeave={() => setIsDropdownOpen(false)}
        >
          <button
            type="button"
            className="dropdown-toggle"
            onClick={() => setIsDropdownOpen((prev) => !prev)}
            aria-expanded={isDropdownOpen}
          >
            Thể loại <i className="fas fa-chevron-down" aria-hidden="true" />
          </button>
          <div className={`dropdown-menu ${isDropdownOpen ? 'show' : ''}`}>
            {categoryError ? (
              <span className="dropdown-error">{categoryError}</span>
            ) : categories.length > 0 ? (
              categories.map((category) => (
                <Link key={category.id} to={`/books?category=${category.id}`}>
                  {category.name}
                </Link>
              ))
            ) : (
              <span className="dropdown-empty">Đang tải...</span>
            )}
          </div>
        </li>
        <li><Link to="/about">Về chúng tôi</Link></li>
      </ul>
      <div className="header-icons">
        {userName ? (
          <div
            className="user-menu"
            ref={userMenuRef}
            onMouseEnter={() => setIsUserMenuOpen(true)}
            onMouseLeave={() => setIsUserMenuOpen(false)}
          >
            <button
              type="button"
              className="btn-login"
              onClick={() => setIsUserMenuOpen((prev) => !prev)}
              aria-expanded={isUserMenuOpen}
            >
              Xin chào, {userName} <i className="fas fa-chevron-down" aria-hidden="true" />
            </button>
            {isUserMenuOpen && (
              <div className="user-menu-items">
                <Link to="/profile">Thông tin cá nhân</Link>
                <Link to="/borrowed">Lịch sử mượn</Link>
                <Link to="/fines">Lịch sử phạt</Link>
                <button type="button" onClick={handleLogout}>Đăng xuất</button>
              </div>
            )}
          </div>
        ) : (
          <Link to="/login" className="btn-login">Đăng nhập</Link>
        )}
      </div>
    </header>
  );
};

export default Header;