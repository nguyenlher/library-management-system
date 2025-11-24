import React from 'react';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/AboutPage.css';

const AboutPage = () => {
  return (
    <div className="about-page">
      <Header />

      <main className="about-content">
        <section className="about-hero">
          <div className="container">
            <p className="about-intro">
              - Chào mừng bạn đến với BookShelf - <br />Hệ thống thư viện trực tuyến hàng đầu Việt Nam.
              Chúng tôi cung cấp dịch vụ mượn sách tiện lợi, nhanh chóng với kho sách đa dạng
              từ văn học, khoa học, kỹ thuật đến sách thiếu nhi. Với sứ mệnh lan tỏa tri thức
              và thúc đẩy văn hóa đọc.
            </p>
          </div>
        </section>

        <section className="about-details">
          <div className="container">
            <div className="about-grid">
              <div className="contact-info">
                <h2>Thông tin liên hệ</h2>
                <div className="contact-item">
                  <i className="fas fa-map-marker-alt"></i>
                  <div>
                    <h3>Địa chỉ</h3>
                    <p>19 Đ. Nguyễn Hữu Thọ, Phường, Quận 7, TP. Hồ Chí Minh</p>
                  </div>
                </div>
                <div className="contact-item">
                  <i className="fas fa-envelope"></i>
                  <div>
                    <h3>Email</h3>
                    <p>contact@bookshelf.vn</p>
                  </div>
                </div>
                <div className="contact-item">
                  <i className="fas fa-phone"></i>
                  <div>
                    <h3>Điện thoại</h3>
                    <p>+84 907 318 993</p>
                  </div>
                </div>
              </div>

              <div className="map-container">
                <h2>Vị trí</h2>
                <div className="map-placeholder">
                  <iframe
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3920.0330883073725!2d106.6967668746547!3d10.731931389414115!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x317528b2747a81a3%3A0x33c1813055acb613!2zxJDhuqFpIGjhu41jIFTDtG4gxJDhu6ljIFRo4bqvbmc!5e0!3m2!1svi!2s!4v1763968596080!5m2!1svi!2s"
                    width="100%"
                    height="300"
                    style={{ border: 0 }}
                    allowFullScreen=""
                    loading="lazy"
                    referrerPolicy="no-referrer-when-downgrade"
                    title="BookShelf Location"
                  ></iframe>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default AboutPage;