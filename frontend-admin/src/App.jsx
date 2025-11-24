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
import './App.css';
import './styles/Dashboard.css';

function App() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  return (
    <Router>
      <div className="admin-container">
        <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
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
    </Router>
  );
}

export default App;