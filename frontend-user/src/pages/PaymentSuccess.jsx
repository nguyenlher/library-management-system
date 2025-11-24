import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/PaymentResult.css';

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isFinePayment, setIsFinePayment] = useState(false);

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

    console.log('PaymentSuccess - paymentId:', paymentId);
    console.log('PaymentSuccess - amount:', amount);
    console.log('PaymentSuccess - transactionNo:', transactionNo);

    // Mark this payment as being processed
    processedPayments.push(paymentId);
    localStorage.setItem('processedPayments', JSON.stringify(processedPayments));

    // Get payment details to get referenceId (bookId or fineId) and userId
    axios.get(`http://localhost:8084/payments/${paymentId}`)
      .then(paymentResponse => {
        console.log('Payment details:', paymentResponse.data);
        const payment = paymentResponse.data;
        
        // Check if this is a fine payment or borrow payment
        const finePayment = payment.type === 'fine';
        setIsFinePayment(finePayment);
        
        if (finePayment) {
          // Update fine status to PAID
          console.log('Paying fine:', payment.referenceId);
          
          return axios.put(`http://localhost:8086/fines/${payment.referenceId}/pay`)
            .then(fineResponse => {
              console.log('Fine paid successfully:', fineResponse.data);
            });
        } else {
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
        }
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

  const handleGoToFines = () => {
    navigate('/fines');
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
            <p>{isFinePayment ? 'Đang cập nhật trạng thái phạt...' : 'Đang xử lý phiếu mượn sách...'}</p>
          </div>
        ) : error ? (
          <div className="result-card error-card">
            <div className="result-icon">
              <i className="fas fa-exclamation-circle"></i>
            </div>
            <h1>Lỗi xử lý</h1>
            <p className="result-message">{error}</p>
            <div className="result-actions">
              <button onClick={isFinePayment ? handleGoToFines : handleGoToHistory} className="btn btn-primary">
                {isFinePayment ? 'Xem lịch sử phạt' : 'Xem lịch sử mượn'}
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
              {isFinePayment 
                ? 'Bạn đã thanh toán thành công khoản phạt.'
                : 'Bạn đã thanh toán thành công phí mượn sách.'
              }
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
              <button onClick={isFinePayment ? handleGoToFines : handleGoToHistory} className="btn btn-primary">
                {isFinePayment ? 'Xem lịch sử phạt' : 'Xem lịch sử mượn'}
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

export default PaymentSuccess;
