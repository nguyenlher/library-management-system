import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/PaymentResult.css';

const PaymentBorrowSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const paymentId = searchParams.get('paymentId');

    if (!paymentId) {
      setError('Không tìm thấy thông tin thanh toán');
      setLoading(false);
      return;
    }

    // Check if this payment has already been processed
    const processedPayments = JSON.parse(localStorage.getItem('processedPayments') || '[]');
    if (processedPayments.includes(paymentId)) {
      console.log('Payment already processed:', paymentId);
      setLoading(false);
      return;
    }

    const amount = searchParams.get('amount');
    const transactionNo = searchParams.get('transactionNo');

    console.log('PaymentBorrowSuccess - paymentId:', paymentId);
    console.log('PaymentBorrowSuccess - amount:', amount);
    console.log('PaymentBorrowSuccess - transactionNo:', transactionNo);

    // Mark this payment as being processed
    processedPayments.push(paymentId);
    localStorage.setItem('processedPayments', JSON.stringify(processedPayments));

    // Get payment details to get referenceId (bookId) and userId
    axios.get(`http://localhost:8084/payments/${paymentId}`)
      .then(paymentResponse => {
        console.log('Payment details:', paymentResponse.data);
        const payment = paymentResponse.data;

        // Create borrow record
        const borrowData = {
          userId: payment.userId,
          bookId: payment.referenceId,
          borrowDate: new Date().toISOString(),
          dueDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString()
        };

        console.log('Creating borrow with data:', borrowData);

        return axios.post('http://localhost:8086/borrows', borrowData)
          .then(borrowResponse => {
            console.log('Borrow created successfully:', borrowResponse.data);
            // Decrease available copies of the book
            return axios.get(`http://localhost:8082/books/${payment.referenceId}`)
              .then(bookResponse => {
                const book = bookResponse.data;
                const updatedBook = {
                  ...book,
                  availableCopies: book.availableCopies - 1
                };
                return axios.put(`http://localhost:8082/books/${payment.referenceId}`, updatedBook);
              });
          });
      })
      .then(() => {
        console.log('Book availableCopies updated successfully');
        setLoading(false);
      })
      .catch(err => {
        console.error('Error details:', err);
        console.error('Error response:', err.response?.data);
        setError('Thanh toán thành công nhưng không thể tạo phiếu mượn. Vui lòng liên hệ quản trị viên.');
        setLoading(false);
      });
  }, []);

  const handleGoToHistory = () => {
    navigate('/borrowed');
  };

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <>
      <Header />
      <div className="payment-result-container">
        {loading ? (
          <div className="loading-section">
            <i className="fas fa-spinner fa-spin"></i>
            <p>Đang xử lý phiếu mượn sách...</p>
          </div>
        ) : error ? (
          <div className="result-card error-card">
            <div className="result-icon">
              <i className="fas fa-exclamation-circle"></i>
            </div>
            <h1>Lỗi xử lý</h1>
            <p className="result-message">{error}</p>
            <div className="result-actions">
              <button onClick={handleGoToHistory} className="btn btn-primary">
                Xem lịch sử mượn
              </button>
              <button onClick={handleGoHome} className="btn btn-secondary">
                Về trang chủ
              </button>
            </div>
          </div>
        ) : (
          <div className="result-card success-card">
            <div className="result-icon">
              <i className="fas fa-check-circle"></i>
            </div>
            <h1>Thanh toán thành công!</h1>
            <p className="result-message">
              Bạn đã thanh toán thành công phí mượn sách.
            </p>

            <div className="payment-details">
              <div className="detail-item">
                <span className="label">Mã giao dịch:</span>
                <span className="value">{searchParams.get('transactionNo')}</span>
              </div>
              <div className="detail-item">
                <span className="label">Mã thanh toán:</span>
                <span className="value">{searchParams.get('paymentId')}</span>
              </div>
              <div className="detail-item">
                <span className="label">Số tiền:</span>
                <span className="value">
                  {new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                  }).format(parseInt(searchParams.get('amount')) / 100)}
                </span>
              </div>
            </div>

            <div className="result-actions">
              <button onClick={handleGoToHistory} className="btn btn-primary">
                Xem lịch sử mượn
              </button>
              <button onClick={handleGoHome} className="btn btn-secondary">
                Về trang chủ
              </button>
            </div>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default PaymentBorrowSuccess;