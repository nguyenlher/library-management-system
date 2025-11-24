import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/HomePage.css';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import { Link } from 'react-router-dom';

const Hero = () => (
  <section className="hero">
    <div className="hero-content">
      <h1>Tìm kiếm cuốn sách<br />yêu thích tiếp theo của bạn</h1>
      <p>Khám phá kho tàng tri thức với hơn 10,000 đầu sách. Từ văn học kinh điển đến sách kỹ năng hiện đại.</p>
      
      <div className="search-box">
        <input type="text" placeholder="Nhập tên sách, tác giả..." />
        <button>Tìm kiếm</button>
      </div>
    </div>
    <div className="hero-image">
      <img src="https://placehold.co/600x400/F65D4E/white?text=Hero+Book+Image" alt="Book Cover" />
    </div>
  </section>
);

const BookCard = ({ book }) => {
  const authorLabel = book.authors?.length
    ? book.authors.map((author) => author.name).join(', ')
    : book.author || 'Tác giả chưa rõ';


  return (
    <Link to={`/books/${book.id}`} className="book-card">
      <img src={book.coverImageUrl || book.image} className="book-img" alt={book.title} />
      <h3 className="book-title">{book.title}</h3>
      <p className="book-author">{authorLabel}</p>
      <div className="book-footer" />
    </Link>
  );
};

const Newsletter = () => (
  <section className="newsletter">
    <h2>Đăng ký nhận tin</h2>
    <p>Nhận thông báo về sách mới và ưu đãi đặc biệt hàng tuần.</p>
    <div className="input-group">
      <input type="email" placeholder="Địa chỉ email của bạn" />
      <button>Đăng ký</button>
    </div>
  </section>
);

// --- Component Chính (Main) ---

const HomePage = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const loadBooks = async () => {
      try {
        const response = await axios.get('http://localhost:8082/books');
        if (!cancelled) {
          setBooks(response.data || []);
          setError(null);
        }
      } catch (fetchError) {
        if (!cancelled) {
          setError('Không thể tải sách từ book-service. Vui lòng thử lại sau.');
          setBooks([]);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    loadBooks();
    return () => { cancelled = true; };
  }, []);
  return (
    <div className="homepage-container">
      <Header />
      <Hero />
      
      <section className="trending">
        <div className="section-header">
          <h2>Sách Hot</h2>
          <Link to="/books" className="view-all">Xem tất cả <i className="fas fa-arrow-right"></i></Link>
        </div>

        <div className="book-grid">
          {loading ? (
            <p>Đang tải sách...</p>
          ) : books.length ? (
            books.slice(0, 5).map((book) => (
              <BookCard key={book.id} book={book} />
            ))
          ) : (
            <p>Không có sách để hiển thị.</p>
          )}
        </div>
        {error && <p className="error-text">{error}</p>}
      </section>

      <Newsletter />
      <Footer />
    </div>
  );
};

export default HomePage;