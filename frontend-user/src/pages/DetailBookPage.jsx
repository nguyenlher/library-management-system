import React, { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import '../styles/DetailBookPage.css';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import axios from 'axios';

const DetailBookPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [borrowCount, setBorrowCount] = useState(null);
  const [relatedBooks, setRelatedBooks] = useState([]);

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

  useEffect(() => {
    if (!book?.id) {
      setBorrowCount(null);
      return;
    }

    let cancelled = false;
    axios
      .get('http://localhost:8086/borrows/count', { params: { bookId: book.id } })
      .then((response) => {
        if (!cancelled) {
          setBorrowCount(response.data?.count ?? 0);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setBorrowCount(0);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [book]);

  useEffect(() => {
    if (!book?.categories?.length) {
      setRelatedBooks([]);
      return;
    }

    const categoryIds = book.categories.map((category) => category.id);
    let cancelled = false;
    axios
      .get('http://localhost:8082/books')
      .then((response) => {
        if (cancelled) return;
        const candidates = (response.data || [])
          .filter((candidate) => candidate.id !== book.id)
          .filter((candidate) =>
            candidate.categories?.some((category) => categoryIds.includes(category.id)),
          )
          .slice(0, 4);
        setRelatedBooks(candidates);
      })
      .catch(() => {
        if (!cancelled) {
          setRelatedBooks([]);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [book]);

  const authorLabel = book?.authors?.length
    ? book.authors.map((author) => author.name).join(', ')
    : book?.author || 'Tác giả chưa rõ';

  const publisherLabel = book?.publisherName || book?.publisher || 'Chưa có thông tin';
  const publishYearLabel = book?.publishYear || book?.publicationYear || 'Chưa rõ';
  const isbnLabel = book?.isbn || 'Chưa có';
  const borrowFeeLabel = book?.borrowFee != null
    ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Number(book.borrowFee))
    : 'Chưa có thông tin';
  const availableLabel = book?.availableCopies != null
    ? `${book.availableCopies} sách`
    : 'Chưa có thông tin';
  const availableCount = book?.availableCopies ?? 0;
  const isOutOfStock = availableCount === 0;

  return (
    <div className="detail-page">
      <Header />
      {loading ? (
        <section className="detail-hero">
          <p className="status-text">Đang tải thông tin sách...</p>
        </section>
      ) : error ? (
        <section className="detail-hero">
          <p className="status-text status-text--error">{error}</p>
        </section>
      ) : (
        book && (
          <>
            <section className="detail-hero">
              <div className="detail-hero__image">
                <img src={book.coverImageUrl || book.image || 'https://placehold.co/500x650/F65D4E/white?text=Book'} alt={book.title} />
              </div>
              <div className="detail-hero__content">
                <p className="detail-hero__tag">{book.categories?.[0]?.name || 'Sách nổi bật'}</p>
                <h1>{book.title}</h1>
                <p className="detail-author"><strong>Tác giả:</strong> {authorLabel}</p>
                <div className="detail-publishing">
                  <span><strong>Nhà xuất bản:</strong> {publisherLabel}</span>
                </div>
                <div className="detail-publishing">
                  <span><strong>Năm xuất bản:</strong> {publishYearLabel}</span>
                </div>
                <div className="detail-publishing">
                  <span><strong>ISBN:</strong> {isbnLabel}</span>
                </div>
                {/* <div className="detail-meta">
                  <span><i className="fas fa-star"></i> {book.rating || '—'}</span>
                  <span><i className="fas fa-eye"></i> {book.totalView || book.views || '—'} lượt xem</span>
                  <span><i className="fas fa-book-open"></i> {book.pages || book.pageCount || '—'} trang</span>
                </div> */}
                <div className="detail-publishing">
                  {borrowCount !== null && (
                    <span><strong>Lượt mượn:</strong> {borrowCount} lượt</span>
                  )}
                </div>
                <div className="detail-publishing">
                  {availableLabel !== null && (
                    <span><strong>Số lượng hiện có:</strong> {availableLabel}</span>
                  )}
                  {isOutOfStock && (
                    <span className="status-text status-text--error">Đã hết sách</span>
                  )}
                </div>
                <div className="detail-fee">
                  <span><strong>Phí mượn sách:</strong> {borrowFeeLabel} / ngày</span>
                </div>
                <div className="detail-actions">
                  <button
                    type="button"
                    className="btn-primary"
                    onClick={async () => {
                      const userId = localStorage.getItem('userId'); // Assuming userId is stored
                      if (!userId) {
                        alert('Vui lòng đăng nhập để mượn sách.');
                        return;
                      }
                      try {
                        const response = await axios.get(`http://localhost:8081/users/${userId}/profile`);
                        if (response.data.borrowLock) {
                          alert('Bạn đã bị chặn mượn sách. Yêu cầu hoàn tất tất cả khoản phạt để có thể thực hiện mượn sách.');
                          return;
                        }
                        navigate(`/checkout/${book.id}`);
                      } catch (error) {
                        console.error('Error checking borrow lock:', error);
                        alert('Có lỗi xảy ra. Vui lòng thử lại.');
                      }
                    }}
                    disabled={isOutOfStock}
                  >
                    Đăng ký mượn sách
                  </button>
                </div>
              </div>
            </section>

            <section className="detail-info">
              <div className="detail-info__block">
                <h2>Tóm tắt</h2>
                <p>{book.summary || 'Chưa có tóm tắt cho đầu sách này.'}</p>
              </div>
            </section>

            <div style={{ marginLeft: '80px' }} className="section-header">
              <h2>Sách cùng thể loại</h2>
            </div>

            <section className="detail-tabs">
              <div className="detail-tab">
                {relatedBooks.length ? (
                  <div className="related-grid">
                    {relatedBooks.map((related) => (
                      <Link key={related.id} to={`/books/${related.id}`} className="related-card">
                        <img
                          src={related.coverImageUrl || related.image || 'https://placehold.co/300x400/F65D4E/white?text=Book'}
                          alt={related.title}
                        />
                        <div className="related-card__body">
                          <h4>{related.title}</h4>
                          <p className="related-author">
                            {related.authors?.map((author) => author.name).join(', ') || related.author || 'Tác giả chưa rõ'}
                          </p>
                        </div>
                      </Link>
                    ))}
                  </div>
                ) : (
                  <p>Không tìm thấy sách nào cùng thể loại.</p>
                )}
              </div>
            </section>
          </>
        )
      )}
      <Footer />
    </div>
  );
};

export default DetailBookPage;
