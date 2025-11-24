import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Link, useLocation } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import '../styles/BookPage.css';

const BookPage = () => {
  const location = useLocation();
  const [categories, setCategories] = useState([]);
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategoryIds, setSelectedCategoryIds] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('title');

  useEffect(() => {
    let cancelled = false;
    axios
      .get('http://localhost:8082/books/categories')
      .then((response) => {
        if (!cancelled) {
          setCategories(response.data || []);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setError('Không thể tải danh mục.');
        }
      });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    let cancelled = false;
    setError(null);
    axios
      .get('http://localhost:8082/books')
      .then((response) => {
        if (!cancelled) {
          setBooks(response.data || []);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setError('Không thể tải sách.');
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const categoryFromHeader = params.get('category');
    const searchFromUrl = params.get('search');
    
    if (categoryFromHeader) {
      const id = Number(categoryFromHeader);
      if (!Number.isNaN(id)) {
        setSelectedCategoryIds([id]);
        return;
      }
    }
    
    if (searchFromUrl) {
      setSearchTerm(searchFromUrl);
    }
    
    setSelectedCategoryIds([]);
  }, [location.search]);

  const filteredBooks = useMemo(() => {
    let result = books;

    // Filter by category
    if (selectedCategoryIds.length) {
      result = result.filter((book) =>
        book.categories?.some((category) => selectedCategoryIds.includes(category.id)),
      );
    }

    // Filter by search term
    if (searchTerm) {
      result = result.filter((book) =>
        book.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        book.authors?.some((author) => 
          author.name?.toLowerCase().includes(searchTerm.toLowerCase())
        ) ||
        book.publisher?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Sort books
    result = [...result].sort((a, b) => {
      switch (sortBy) {
        case 'title':
          return (a.title || '').localeCompare(b.title || '');
        case 'author':
          const authorA = a.authors?.[0]?.name || '';
          const authorB = b.authors?.[0]?.name || '';
          return authorA.localeCompare(authorB);
        case 'year-new':
          return (b.publicationYear || 0) - (a.publicationYear || 0);
        case 'year-old':
          return (a.publicationYear || 0) - (b.publicationYear || 0);
        case 'available':
          return (b.availableCopies || 0) - (a.availableCopies || 0);
        default:
          return 0;
      }
    });

    return result;
  }, [books, selectedCategoryIds, searchTerm, sortBy]);

  const toggleCategory = (categoryId) => {
    setSelectedCategoryIds((prev) =>
      prev.includes(categoryId)
        ? prev.filter((id) => id !== categoryId)
        : [...prev, categoryId],
    );
  };

  const selectedLabel = selectedCategoryIds.length
    ? categories
        .filter((category) => selectedCategoryIds.includes(category.id))
        .map((category) => category.name)
        .join(', ')
    : 'Tất cả thể loại';

  return (
    <div className="category-page">
      <Header />
      <section className="category-hero">
        <div>
          <p className="category-hero__tag">Danh mục lựa chọn</p>
          <h1>{selectedLabel}</h1>
          <p>Chọn tối đa nhiều thể loại để thu hẹp tìm kiếm. Bỏ chọn để quay về tất cả sách.</p>
        </div>
        <div className="category-hero__actions">
          <button type="button" onClick={() => setSelectedCategoryIds([])}>Hiển thị tất cả</button>
        </div>
      </section>

      <section className="category-filters">
        {categories.map((category) => (
          <button
            key={category.id}
            type="button"
            className={selectedCategoryIds.includes(category.id) ? 'active' : ''}
            onClick={() => toggleCategory(category.id)}
          >
            {category.name}
          </button>
        ))}
      </section>

      <section className="search-sort-controls">
        <div className="search-box">
          <i className="fas fa-search"></i>
          <input
            type="text"
            placeholder="Tìm kiếm theo tên sách, tác giả, nhà xuất bản..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          {searchTerm && (
            <button 
              type="button" 
              className="clear-search"
              onClick={() => setSearchTerm('')}
            >
              <i className="fas fa-times"></i>
            </button>
          )}
        </div>
      </section>

      <section className="category-books">
        <div className="section-header">
          <h2>{selectedCategoryIds.length ? 'Sách phù hợp' : 'Tất cả sách'}</h2>
          <div className="header-controls">
            <span>{filteredBooks.length} đầu sách</span>
            <div className="sort-control">
              <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="title">Tên sách (A-Z)</option>
                <option value="author">Tác giả (A-Z)</option>
                <option value="year-new">Năm xuất bản (Mới nhất)</option>
                <option value="year-old">Năm xuất bản (Cũ nhất)</option>
                <option value="available">Số lượng còn lại</option>
              </select>
            </div>
          </div>
        </div>
        {loading ? (
          <p className="status-text">Đang tải sách...</p>
        ) : error ? (
          <p className="status-text status-text--error">{error}</p>
        ) : (
          <div className="book-grid">
            {filteredBooks.map((book) => (
              <Link key={book.id} to={`/books/${book.id}`} className="book-card">
                <img src={book.coverImageUrl || book.image} alt={book.title} />
                <div className="book-card__body">
                  <h3>{book.title}</h3>
                  <p>{book.authors?.map((author) => author.name).join(', ') || 'Tác giả chưa rõ'}</p>
                </div>
              </Link>
            ))}
            {!filteredBooks.length && <p className="status-text">Không có sách phù hợp.</p>}
          </div>
        )}
      </section>
      <Footer />
    </div>
  );
};

export default BookPage;
