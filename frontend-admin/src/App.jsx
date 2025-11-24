import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import Dashboard from './pages/Dashboard';
import UserManagement from './pages/UserManagement';
import BookManagement from './pages/BookManagement';
import AuthorManagement from './pages/AuthorManagement';
import PublisherManagement from './pages/PublisherManagement';
import BorrowManagement from './pages/BorrowManagement';
import FineManagement from './pages/FineManagement';
import DetailBook from './pages/DetailBook';
import LoginPage from './pages/LoginPage';
import './App.css';
import './styles/Dashboard.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(() => {
    // Check if user is already logged in
    const token = localStorage.getItem('accessToken');
    const role = localStorage.getItem('userRole');
    return token && (role === 'ADMIN' || role === 'LIBRARIAN');
  });

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleLogin = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('currentUserEmail');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userName');
    localStorage.removeItem('userProfile');
    setIsAuthenticated(false);
  };

  return (
    <Router>
      {!isAuthenticated ? (
        <Routes>
          <Route path="/*" element={<LoginPage onLogin={handleLogin} />} />
        </Routes>
      ) : (
        <div className="admin-container">
          <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} onLogout={handleLogout} />
          <div className="main-wrapper">
            <TopBar toggleSidebar={toggleSidebar} />
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/users" element={<UserManagement />} />
              <Route path="/books" element={<BookManagement />} />
              <Route path="/authors" element={<AuthorManagement />} />
              <Route path="/publishers" element={<PublisherManagement />} />
              <Route path="/borrows" element={<BorrowManagement />} />
              <Route path="/fines" element={<FineManagement />} />
              <Route path="/books/:id" element={<DetailBook />} />
              <Route path="/" element={<Dashboard />} />
            </Routes>
          </div>
        </div>
      )}
    </Router>
  );
}

export default App;