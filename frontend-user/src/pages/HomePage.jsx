import React from 'react';
import '../styles/HomePage.css';

// --- Dữ liệu mẫu (Giả lập từ API) ---
const booksData = [
  {
    id: 1,
    title: "Đắc Nhân Tâm",
    author: "Dale Carnegie",
    price: "120.000đ",
    rating: 4.8,
    image: "https://placehold.co/300x400/333/white?text=Dac+Nhan+Tam"
  },
  {
    id: 2,
    title: "Nhà Giả Kim",
    author: "Paulo Coelho",
    price: "85.000đ",
    rating: 4.9,
    image: "https://placehold.co/300x400/444/white?text=Nha+Gia+Kim"
  },
  {
    id: 3,
    title: "Tuổi Trẻ Đáng Giá Bao Nhiêu",
    author: "Rosie Nguyễn",
    price: "90.000đ",
    rating: 4.5,
    image: "https://placehold.co/300x400/555/white?text=Tuoi+Tre"
  },
  {
    id: 4,
    title: "Tắt Đèn",
    author: "Ngô Tất Tố",
    price: "65.000đ",
    rating: 4.7,
    image: "https://placehold.co/300x400/666/white?text=Tat+Den"
  }
];

// --- Các Component con ---

const Header = () => (
  <header className="header">
    <div className="logo">Book<span>Shelf</span>.</div>
    <ul className="nav-links">
      <li><a href="/" className="active">Trang chủ</a></li>
      <li><a href="/">Thể loại</a></li>
      <li><a href="/">Tác giả</a></li>
      <li><a href="/">Về chúng tôi</a></li>
    </ul>
    <div className="header-icons">
      <a href="/"><i className="fas fa-search"></i></a>
      <a href="/"><i className="fas fa-shopping-cart"></i></a>
      <a href="/" className="btn-login">Đăng nhập</a>
    </div>
  </header>
);

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

const BookCard = ({ book }) => (
  <div className="book-card">
    <img src={book.image} className="book-img" alt={book.title} />
    <h3 className="book-title">{book.title}</h3>
    <p className="book-author">{book.author}</p>
    <div className="book-footer">
      <div className="rating"><i className="fas fa-star"></i> {book.rating}</div>
      <div className="price">{book.price}</div>
    </div>
  </div>
);

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

const Footer = () => (
  <footer>
    <p>&copy; 2024 My Book Shelf. Design Concept inspired by Figma Community.</p>
  </footer>
);

// --- Component Chính (Main) ---

const HomePage = () => {
  return (
    <div className="homepage-container">
      <Header />
      <Hero />
      
      <section className="trending">
        <div className="section-header">
          <h2>Sách Bán Chạy</h2>
          <a href="/" className="view-all">Xem tất cả <i className="fas fa-arrow-right"></i></a>
        </div>

        <div className="book-grid">
          {booksData.map((book) => (
            <BookCard key={book.id} book={book} />
          ))}
        </div>
      </section>

      <Newsletter />
      <Footer />
    </div>
  );
};

export default HomePage;