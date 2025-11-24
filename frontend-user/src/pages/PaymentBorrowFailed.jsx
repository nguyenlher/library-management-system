import React from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/PaymentResult.css';

const PaymentBorrowFailed = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const errorCode = searchParams.get('code') || '99';

  const getErrorMessage = (code) => {
    switch (code) {
      case '07':
        return 'Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).';
      case '09':
        return 'Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.';
      case '10':
        return 'Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.';
      case '11':
        return 'Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.';
      case '12':
        return 'Thẻ/Tài khoản của khách hàng bị khóa.';
      case '13':
        return 'Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.';
      case '24':
        return 'Khách hàng hủy giao dịch.';
      case '51':
        return 'Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.';
      case '65':
        return 'Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.';
      case '75':
        return 'Ngân hàng thanh toán đang bảo trì.';
      case '79':
        return 'KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.';
      case '97':
        return 'Chữ ký không hợp lệ.';
      default:
        return 'Đã có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau.';
    }
  };

  const handleTryAgain = () => {
    navigate(-1);
  };

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <>
      <Header />
      <div className="payment-result-container">
        <div className="result-card error-card">
          <div className="result-icon">
            <i className="fas fa-times-circle"></i>
          </div>
          <h1>Thanh toán thất bại</h1>
          <p className="result-message">{getErrorMessage(errorCode)}</p>

          <div className="payment-details">
            <div className="detail-item">
              <span className="label">Mã lỗi:</span>
              <span className="value">{errorCode}</span>
            </div>
          </div>

          <div className="result-actions">
            <button onClick={handleTryAgain} className="btn btn-primary">
              Thử lại
            </button>
            <button onClick={handleGoHome} className="btn btn-secondary">
              Về trang chủ
            </button>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default PaymentBorrowFailed;