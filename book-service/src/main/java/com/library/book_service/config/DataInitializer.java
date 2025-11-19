package com.library.book_service.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.book_service.entity.Author;
import com.library.book_service.entity.Book;
import com.library.book_service.entity.BookAuthor;
import com.library.book_service.entity.BookCategory;
import com.library.book_service.entity.Category;
import com.library.book_service.entity.Publisher;
import com.library.book_service.repository.AuthorRepository;
import com.library.book_service.repository.BookAuthorRepository;
import com.library.book_service.repository.BookCategoryRepository;
import com.library.book_service.repository.BookRepository;
import com.library.book_service.repository.CategoryRepository;
import com.library.book_service.repository.PublisherRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookCategoryRepository bookCategoryRepository;

    public DataInitializer(PublisherRepository publisherRepository,
                          AuthorRepository authorRepository,
                          CategoryRepository categoryRepository,
                          BookRepository bookRepository,
                          BookAuthorRepository bookAuthorRepository,
                          BookCategoryRepository bookCategoryRepository) {
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0) {
            createSampleData();
            System.out.println("Sample book data initialized successfully!");
        } else {
            System.out.println("Book data already exists, skipping initialization.");
        }
    }

    private void createSampleData() {
        // Create publishers
        Publisher publisher1 = new Publisher("Penguin Random House");
        Publisher publisher2 = new Publisher("HarperCollins");
        Publisher publisher3 = new Publisher("Simon & Schuster");
        publisherRepository.saveAll(Arrays.asList(publisher1, publisher2, publisher3));

        // Create authors
        Author author1 = new Author("J.K. Rowling");
        Author author2 = new Author("George R.R. Martin");
        Author author3 = new Author("Agatha Christie");
        Author author4 = new Author("Stephen King");
        Author author5 = new Author("Haruki Murakami");
        authorRepository.saveAll(Arrays.asList(author1, author2, author3, author4, author5));

        // Create categories
        Category category1 = new Category("Fiction");
        Category category2 = new Category("Fantasy");
        Category category3 = new Category("Mystery");
        Category category4 = new Category("Horror");
        Category category5 = new Category("Literary Fiction");
        categoryRepository.saveAll(Arrays.asList(category1, category2, category3, category4, category5));

        // Create books
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Philosopher's Stone");
        book1.setIsbn("9780747532699");
        book1.setSummary("The first book in the Harry Potter series.");
        book1.setPublisherId(publisher1.getId());
        book1.setPublishYear(1997);
        book1.setEdition("1st Edition");
        book1.setCoverImageUrl("https://example.com/harry-potter-1.jpg");
        book1.setBorrowFee(BigDecimal.valueOf(2.50));
        book1.setTotalCopies(10);
        book1.setAvailableCopies(8);
        book1.setAuthors(new HashSet<>(Arrays.asList(author1)));
        book1.setCategories(new HashSet<>(Arrays.asList(category1, category2)));
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("A Game of Thrones");
        book2.setIsbn("9780553103540");
        book2.setSummary("The first book in the A Song of Ice and Fire series.");
        book2.setPublisherId(publisher2.getId());
        book2.setPublishYear(1996);
        book2.setEdition("1st Edition");
        book2.setCoverImageUrl("https://example.com/game-of-thrones.jpg");
        book2.setBorrowFee(BigDecimal.valueOf(3.00));
        book2.setTotalCopies(5);
        book2.setAvailableCopies(3);
        book2.setAuthors(new HashSet<>(Arrays.asList(author2)));
        book2.setCategories(new HashSet<>(Arrays.asList(category1, category2)));
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("Murder on the Orient Express");
        book3.setIsbn("9780062693662");
        book3.setSummary("A classic mystery novel by Agatha Christie.");
        book3.setPublisherId(publisher3.getId());
        book3.setPublishYear(1934);
        book3.setEdition("1st Edition");
        book3.setCoverImageUrl("https://example.com/orient-express.jpg");
        book3.setBorrowFee(BigDecimal.valueOf(2.00));
        book3.setTotalCopies(7);
        book3.setAvailableCopies(7);
        book3.setAuthors(new HashSet<>(Arrays.asList(author3)));
        book3.setCategories(new HashSet<>(Arrays.asList(category1, category3)));
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setTitle("The Shining");
        book4.setIsbn("9780307743657");
        book4.setSummary("A horror novel about a haunted hotel.");
        book4.setPublisherId(publisher1.getId());
        book4.setPublishYear(1977);
        book4.setEdition("1st Edition");
        book4.setCoverImageUrl("https://example.com/the-shining.jpg");
        book4.setBorrowFee(BigDecimal.valueOf(2.75));
        book4.setTotalCopies(6);
        book4.setAvailableCopies(4);
        book4.setAuthors(new HashSet<>(Arrays.asList(author4)));
        book4.setCategories(new HashSet<>(Arrays.asList(category1, category4)));
        bookRepository.save(book4);

        Book book5 = new Book();
        book5.setTitle("Kafka on the Shore");
        book5.setIsbn("9781400079278");
        book5.setSummary("A surreal novel by Haruki Murakami.");
        book5.setPublisherId(publisher2.getId());
        book5.setPublishYear(2002);
        book5.setEdition("1st Edition");
        book5.setCoverImageUrl("https://example.com/kafka-on-the-shore.jpg");
        book5.setBorrowFee(BigDecimal.valueOf(3.25));
        book5.setTotalCopies(4);
        book5.setAvailableCopies(4);
        book5.setAuthors(new HashSet<>(Arrays.asList(author5)));
        book5.setCategories(new HashSet<>(Arrays.asList(category1, category5)));
        bookRepository.save(book5);

        // Create book-author relationships
        bookAuthorRepository.save(new BookAuthor(book1.getId(), author1.getId()));
        bookAuthorRepository.save(new BookAuthor(book2.getId(), author2.getId()));
        bookAuthorRepository.save(new BookAuthor(book3.getId(), author3.getId()));
        bookAuthorRepository.save(new BookAuthor(book4.getId(), author4.getId()));
        bookAuthorRepository.save(new BookAuthor(book5.getId(), author5.getId()));

        // Create book-category relationships
        bookCategoryRepository.save(new BookCategory(book1.getId(), category1.getId()));
        bookCategoryRepository.save(new BookCategory(book1.getId(), category2.getId()));
        bookCategoryRepository.save(new BookCategory(book2.getId(), category1.getId()));
        bookCategoryRepository.save(new BookCategory(book2.getId(), category2.getId()));
        bookCategoryRepository.save(new BookCategory(book3.getId(), category1.getId()));
        bookCategoryRepository.save(new BookCategory(book3.getId(), category3.getId()));
        bookCategoryRepository.save(new BookCategory(book4.getId(), category1.getId()));
        bookCategoryRepository.save(new BookCategory(book4.getId(), category4.getId()));
        bookCategoryRepository.save(new BookCategory(book5.getId(), category1.getId()));
        bookCategoryRepository.save(new BookCategory(book5.getId(), category5.getId()));
    }
}