import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/PaymentResult.css';

const PaymentFineSuccess = () => {
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
    const processedPayments = JSON.parse(localStorage.getItem('processedFinePayments') || '[]');
    if (processedPayments.includes(paymentId)) {
      console.log('Fine payment already processed:', paymentId);
      setLoading(false);
      return;
    }

    const amount = searchParams.get('amount');
    const transactionNo = searchParams.get('transactionNo');

    console.log('PaymentFineSuccess - paymentId:', paymentId);
    console.log('PaymentFineSuccess - amount:', amount);
    console.log('PaymentFineSuccess - transactionNo:', transactionNo);

    // Mark this payment as being processed
    processedPayments.push(paymentId);
    localStorage.setItem('processedFinePayments', JSON.stringify(processedPayments));

    // Get payment details to get referenceId (fineId) and userId
    axios.get(`http://localhost:8084/payments/${paymentId}`)
      .then(paymentResponse => {
        console.log('Payment details:', paymentResponse.data);
        const payment = paymentResponse.data;

        // Update fine status to PAID
        console.log('Paying fine:', payment.referenceId);

        return axios.put(`http://localhost:8086/fines/${payment.referenceId}/pay`)
          .then(fineResponse => {
            console.log('Fine paid successfully:', fineResponse.data);
          });
      })
      .then(() => {
        console.log('Fine payment processed successfully');
        setLoading(false);
      })
      .catch(err => {
        console.error('Error details:', err);
        console.error('Error response:', err.response?.data);
        setError('Thanh toán thành công nhưng không thể cập nhật trạng thái phạt. Vui lòng liên hệ quản trị viên.');
        setLoading(false);
      });
  }, []);

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
            <p>Đang cập nhật trạng thái phạt...</p>
          </div>
        ) : error ? (
          <div className="result-card error-card">
            <div className="result-icon">
              <i className="fas fa-exclamation-circle"></i>
            </div>
            <h1>Lỗi xử lý</h1>
            <p className="result-message">{error}</p>
            <div className="result-actions">
              <button onClick={handleGoToFines} className="btn btn-primary">
                Xem lịch sử phạt
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
              Bạn đã thanh toán thành công khoản phạt.
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
              <button onClick={handleGoToFines} className="btn btn-primary">
                Xem lịch sử phạt
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

export default PaymentFineSuccess;