import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/CheckoutPage.css';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import axios from 'axios';

const CheckoutPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentStep, setCurrentStep] = useState(1);
  const [paymentMethod, setPaymentMethod] = useState('');
  const [paymentUrl, setPaymentUrl] = useState('');
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [paymentError, setPaymentError] = useState(null);

  const baseBorrowDate = new Date().toISOString().split('T')[0];
  const baseReturnDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
  const [currentUserName] = useState(() => localStorage.getItem('userName') || '');
  const [borrowForm, setBorrowForm] = useState({
    name: currentUserName,
    borrowDate: baseBorrowDate,
    returnDate: baseReturnDate,
  });

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    axios
      .get(`http://localhost:8082/books/${id}`)
      .then((response) => {
        if (!cancelled) {
          setBook(response.data);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setError('Không tìm thấy sách này.');
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [id]);

  const borrowDays = Math.ceil(
    (new Date(borrowForm.returnDate) - new Date(borrowForm.borrowDate)) / (1000 * 60 * 60 * 24)
  );
  const totalAmount = (book?.borrowFee || 0) * borrowDays;
  const totalAmountFormatted = new Intl.NumberFormat('vi-VN', { 
    style: 'currency', 
    currency: 'VND' 
  }).format(totalAmount);

  const handleStep1Next = (event) => {
    event.preventDefault();
    setCurrentStep(2);
  };

  const handlePaymentMethodChange = async (method) => {
    setPaymentMethod(method);
    setPaymentError(null);
    
    if (method === 'vnpay') {
      setPaymentLoading(true);
      try {
        const userId = localStorage.getItem('userId') || 1;
        
        const response = await axios.post('http://localhost:8084/payments/vnpay', {
          userId: Number(userId),
          amount: totalAmount,
          referenceId: book?.id,
          orderInfo: `Thanh toan phi muon sach: ${book?.title}`,
          type: 'borrow'
        });
        
        setPaymentUrl(response.data.paymentUrl);
      } catch (error) {
        setPaymentError('Không thể tạo liên kết thanh toán. Vui lòng thử lại.');
        setPaymentMethod('');
      } finally {
        setPaymentLoading(false);
      }
    }
  };

  const handleConfirmPayment = () => {
    if (!paymentMethod) {
      setPaymentError('Vui lòng chọn phương thức thanh toán');
      return;
    }
    setCurrentStep(3);
  };

  const handleGoBack = () => {
    if (currentStep === 2) {
      setPaymentMethod('');
      setPaymentUrl('');
      setPaymentError(null);
    }
    setCurrentStep((prev) => Math.max(1, prev - 1));
  };

  return (
    <div className="checkout-page">
      <Header />
      <div className="checkout-container">
        {loading ? (
          <p className="status-text">Đang tải thông tin sách...</p>
        ) : error ? (
          <p className="status-text status-text--error">{error}</p>
        ) : (
          book && (
            <>
              <div className="checkout-header">
                <h1>Đăng ký mượn sách</h1>
                <div className="checkout-steps">
                  {[1, 2, 3].map((step) => (
                    <div
                      key={step}
                      className={`checkout-step ${step <= currentStep ? 'active' : ''} ${step < currentStep ? 'completed' : ''}`}
                    >
                      <div className="step-number">{step}</div>
                      <div className="step-label">
                        {step === 1 ? 'Thông tin' : step === 2 ? 'Thanh toán' : 'Hoàn tất'}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="checkout-content">
                <div className="checkout-main">
                  {currentStep === 1 && (
                    <div className="checkout-section">
                      <h2>Thông tin mượn sách</h2>
                      <form className="checkout-form" onSubmit={handleStep1Next}>
                        <div className="form-group">
                          <label>Tên người mượn</label>
                          <input
                            type="text"
                            value={borrowForm.name || currentUserName || ''}
                            readOnly
                            disabled
                          />
                        </div>
                        <div className="form-group">
                          <label>Sách mượn</label>
                          <input type="text" value={book?.title || ''} readOnly disabled />
                        </div>
                        <div className="form-row">
                          <div className="form-group">
                            <label>Ngày mượn</label>
                            <input
                              type="date"
                              value={borrowForm.borrowDate}
                              required
                              onChange={(e) => setBorrowForm({ ...borrowForm, borrowDate: e.target.value })}
                            />
                          </div>
                          <div className="form-group">
                            <label>Ngày trả</label>
                            <input
                              type="date"
                              value={borrowForm.returnDate}
                              required
                              onChange={(e) => setBorrowForm({ ...borrowForm, returnDate: e.target.value })}
                            />
                          </div>
                        </div>
                        <div className="form-actions">
                          <button type="button" className="btn-outline" onClick={() => navigate(-1)}>
                            Hủy
                          </button>
                          <button type="submit" className="btn-primary">
                            Tiếp tục
                          </button>
                        </div>
                      </form>
                    </div>
                  )}

                  {currentStep === 2 && (
                    <div className="checkout-section">
                      <h2>Phương thức thanh toán</h2>
                      <div className="payment-methods">
                        <label className={`payment-method ${paymentMethod === 'cash' ? 'selected' : ''}`}>
                          <input
                            type="radio"
                            name="paymentMethod"
                            value="cash"
                            checked={paymentMethod === 'cash'}
                            onChange={() => handlePaymentMethodChange('cash')}
                          />
                          <div className="method-content">
                            <i className="fas fa-money-bill-wave"></i>
                            <span>Tiền mặt</span>
                            <p>Thanh toán khi nhận sách</p>
                          </div>
                        </label>
                        <label className={`payment-method ${paymentMethod === 'vnpay' ? 'selected' : ''}`}>
                          <input
                            type="radio"
                            name="paymentMethod"
                            value="vnpay"
                            checked={paymentMethod === 'vnpay'}
                            onChange={() => handlePaymentMethodChange('vnpay')}
                          />
                          <div className="method-content">
                            <i className="fas fa-credit-card"></i>
                            <span>VNPay</span>
                            <p>Thanh toán qua cổng VNPay</p>
                          </div>
                        </label>
                      </div>

                      {paymentError && (
                        <p className="payment-error">{paymentError}</p>
                      )}
                      
                      {paymentLoading && (
                        <p className="payment-loading">Đang tạo liên kết thanh toán...</p>
                      )}
                      
                      {paymentMethod === 'vnpay' && paymentUrl && (
                        <div className="vnpay-section">
                          <p>Nhấn nút bên dưới để mở cổng thanh toán VNPay:</p>
                          <button
                            type="button"
                            className="btn-vnpay"
                            onClick={() => window.open(paymentUrl, '_blank')}
                          >
                            <i className="fas fa-external-link-alt"></i> Mở trang thanh toán VNPay
                          </button>
                          <p className="payment-note">Sau khi thanh toán thành công, vui lòng quay lại và nhấn "Đã thanh toán"</p>
                        </div>
                      )}

                      <div className="form-actions">
                        <button type="button" className="btn-outline" onClick={handleGoBack}>
                          Quay lại
                        </button>
                        <button 
                          type="button" 
                          className="btn-primary" 
                          onClick={handleConfirmPayment}
                          disabled={!paymentMethod || paymentLoading}
                        >
                          {paymentMethod === 'cash' ? 'Xác nhận' : 'Đã thanh toán'}
                        </button>
                      </div>
                    </div>
                  )}

                  {currentStep === 3 && (
                    <div className="checkout-section checkout-success">
                      <div className="success-icon">
                        <i className="fas fa-check-circle"></i>
                      </div>
                      <h2>Đăng ký thành công!</h2>
                      <p>
                        {paymentMethod === 'cash'
                          ? 'Đã đăng ký mượn sách. Vui lòng thanh toán tiền mặt khi nhận sách.'
                          : 'Thanh toán thành công. Ứng dụng sẽ xử lý yêu cầu của bạn.'}
                      </p>
                      <div className="form-actions">
                        <button type="button" className="btn-primary" onClick={() => navigate(`/books/${id}`)}>
                          Quay lại chi tiết sách
                        </button>
                      </div>
                    </div>
                  )}
                </div>

                <div className="checkout-sidebar">
                  <div className="order-summary">
                    <h3>Thông tin đơn hàng</h3>
                    <div className="book-info">
                      <img 
                        src={book.coverImageUrl || 'https://placehold.co/100x140/F65D4E/white?text=Book'} 
                        alt={book.title}
                      />
                      <div className="book-details">
                        <h4>{book.title}</h4>
                        <p className="book-author">
                          {book.authors?.map((a) => a.name).join(', ') || 'Tác giả chưa rõ'}
                        </p>
                      </div>
                    </div>
                    <div className="summary-row">
                      <span>Phí mượn/ngày:</span>
                      <span>
                        {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(book.borrowFee || 0)}
                      </span>
                    </div>
                    <div className="summary-row">
                      <span>Số ngày mượn:</span>
                      <span>{borrowDays} ngày</span>
                    </div>
                    <div className="summary-divider"></div>
                    <div className="summary-row summary-total">
                      <span>Tổng cộng:</span>
                      <span>{totalAmountFormatted}</span>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )
        )}
      </div>
      <Footer />
    </div>
  );
};

export default CheckoutPage;
