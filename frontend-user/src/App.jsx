import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import './App.css';
import Home from './pages/HomePage';
import Login from './pages/LoginPage';
import Register from './pages/RegisterPage';
import DetailBook from './pages/DetailBookPage';
import BookPage from './pages/BookPage';
import Checkout from './pages/CheckoutPage';
import BorrowHistory from './pages/BorrowHistoryPage';
import FineHistory from './pages/FineHistoryPage';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentFailed from './pages/PaymentFailed';
import PaymentBorrowSuccess from './pages/PaymentBorrowSuccess';
import PaymentBorrowFailed from './pages/PaymentBorrowFailed';
import PaymentFineSuccess from './pages/PaymentFineSuccess';
import PaymentFineFailed from './pages/PaymentFineFailed';
import AboutPage from './pages/AboutPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />

        <Route path="/home" element={<Home />} />

        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/books" element={<BookPage />} />
        <Route path="/books/:id" element={<DetailBook />} />
        <Route path="/checkout/:id" element={<Checkout />} />
        <Route path="/borrowed" element={<BorrowHistory />} />
        <Route path="/fines" element={<FineHistory />} />
        <Route path="/payment/success" element={<PaymentSuccess />} />
        <Route path="/payment/failed" element={<PaymentFailed />} />
        <Route path="/payment/borrow/success" element={<PaymentBorrowSuccess />} />
        <Route path="/payment/borrow/failed" element={<PaymentBorrowFailed />} />
        <Route path="/payment/fine/success" element={<PaymentFineSuccess />} />
        <Route path="/payment/fine/failed" element={<PaymentFineFailed />} />
        <Route path="/about" element={<AboutPage />} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;