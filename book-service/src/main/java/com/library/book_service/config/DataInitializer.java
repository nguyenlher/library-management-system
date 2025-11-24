package com.library.book_service.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.book_service.entity.Author;
import com.library.book_service.entity.Book;
import com.library.book_service.entity.Category;
import com.library.book_service.entity.Publisher;
import com.library.book_service.repository.AuthorRepository;
import com.library.book_service.repository.BookRepository;
import com.library.book_service.repository.CategoryRepository;
import com.library.book_service.repository.PublisherRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public DataInitializer(PublisherRepository publisherRepository,
                          AuthorRepository authorRepository,
                          CategoryRepository categoryRepository,
                          BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
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
        Publisher publisherHoi = new Publisher("Hội Nhà Văn");
        Publisher publisherKimDong = new Publisher("Kim Đồng");
        Publisher publisherVanHoc = new Publisher("Văn học");
        Publisher publisherTre = new Publisher("Trẻ");
        Publisher publisherChinhTri = new Publisher("Chính trị Quốc gia");
        Publisher publisherTheGioi = new Publisher("Thế Giới");
        Publisher publisherKHTT = new Publisher("Khoa học & Kỹ thuật");
        Publisher publisherTTTT = new Publisher("Thông tin & Truyền thông");
        Publisher publisherLaoDong = new Publisher("Lao Động");
        Publisher publisherHongDuc = new Publisher("Hồng Đức");
        Publisher publisherTongHop = new Publisher("Tổng hợp");
        Publisher publisherScribner = new Publisher("Scribner");
        publisherRepository.saveAll(Arrays.asList(publisherHoi, publisherKimDong, publisherVanHoc,
            publisherTre, publisherChinhTri, publisherTheGioi, publisherKHTT, publisherTTTT,
            publisherLaoDong, publisherHongDuc, publisherTongHop, publisherScribner));

        Author namCao = new Author("Nam Cao");
        Author nguyenNgoc = new Author("Nguyên Ngọc");
        Author baoNinh = new Author("Bảo Ninh");
        Author nguyenNhatAnh = new Author("Nguyễn Nhật Ánh");
        Author toHoai = new Author("Tô Hoài");
        Author williamDuiker = new Author("William J. Duiker");
        Author nguyenHuyThiep = new Author("Nguyễn Huy Thiệp");
        Author nguyenNgocTu = new Author("Nguyễn Ngọc Tư");
        Author fScott = new Author("F. Scott Fitzgerald");
        Author georgeOrwell = new Author("George Orwell");
        Author harperLee = new Author("Harper Lee");
        Author marquez = new Author("Gabriel García Márquez");
        Author dostoevsky = new Author("Fyodor Dostoevsky");
        Author janeAusten = new Author("Jane Austen");
        Author saintExupery = new Author("Antoine de Saint-Exupéry");
        Author rowling = new Author("J.K. Rowling");
        Author coelho = new Author("Paulo Coelho");
        Author murakami = new Author("Haruki Murakami");
        Author yuval = new Author("Yuval Noah Harari");
        Author carlSagan = new Author("Carl Sagan");
        Author stephenHawking = new Author("Stephen Hawking");
        Author richardFeynman = new Author("Richard Feynman");
        Author stuartRussell = new Author("Stuart Russell");
        Author peterNorvig = new Author("Peter Norvig");
        Author danielKahneman = new Author("Daniel Kahneman");
        Author ericMatthes = new Author("Eric Matthes");
        Author daleCarnegie = new Author("Dale Carnegie");
        Author adamGrant = new Author("Adam Grant");
        Author stephenCovey = new Author("Stephen R. Covey");
        Author jamesClear = new Author("James Clear");
        Author georgeClason = new Author("George S. Clason");
        Author ichiroKishimi = new Author("Ichiro Kishimi & Fumitake Koga");
        Author carolDweck = new Author("Carol S. Dweck");
        Author thichNhatHanh = new Author("Thích Nhất Hạnh");
        Author hectorGarcia = new Author("Héctor García & Francesc Miralles");
        Author roaldDahl = new Author("Roald Dahl");
        Author rjPalacio = new Author("R.J. Palacio");
        Author ebWhite = new Author("E.B. White");
        Author hansAndersen = new Author("Hans Christian Andersen");
        Author grimBrothers = new Author("Anh em nhà Grimm");
        Author ernestHemingway = new Author("Ernest Hemingway");
        Author naokiHigashida = new Author("Naoki Higashida");
        Author jeffKinney = new Author("Jeff Kinney");
        Author robertMartin = new Author("Robert C. Martin");
        authorRepository.saveAll(Arrays.asList(namCao, nguyenNgoc, baoNinh, nguyenNhatAnh,
            toHoai, williamDuiker, nguyenHuyThiep, nguyenNgocTu, fScott, georgeOrwell,
            harperLee, marquez, dostoevsky, janeAusten, saintExupery, rowling, coelho,
            murakami, yuval, carlSagan, stephenHawking, richardFeynman, stuartRussell,
            peterNorvig, danielKahneman, ericMatthes, daleCarnegie, adamGrant, stephenCovey,
            jamesClear, georgeClason, ichiroKishimi, carolDweck, thichNhatHanh, hectorGarcia,
            roaldDahl, rjPalacio, ebWhite, hansAndersen, grimBrothers, ernestHemingway,
            naokiHigashida, jeffKinney, robertMartin));

        Category viet = new Category("Văn học Việt Nam");
        Category foreign = new Category("Tiểu thuyết nước ngoài");
        Category science = new Category("Khoa học – Công nghệ");
        Category selfHelp = new Category("Tâm lý – Kỹ năng sống");
        Category children = new Category("Thiếu nhi");
        categoryRepository.saveAll(Arrays.asList(viet, foreign, science, selfHelp, children));

        // Create Vietnamese literature books
        Book book1 = new Book();
        book1.setTitle("Sống Mòn");
        book1.setIsbn("9786041178243");
        book1.setSummary("Tiểu thuyết hiện thực phê phán sâu sắc về đời sống lao động và thân phận người bình dân giữa nhịp sống thành thị");
        book1.setPublisherId(publisherHoi.getId());
        book1.setPublishYear(2020);
        book1.setEdition("2023");
        book1.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915887/books/S%E1%BB%91ng_m%C3%B2n_ad3sl4.png");
        book1.setBorrowFee(BigDecimal.valueOf(1000));
        book1.setTotalCopies(6);
        book1.setAvailableCopies(6);
        book1.setAuthors(new HashSet<>(Arrays.asList(namCao)));
        book1.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Đất nước đứng lên");
        book2.setIsbn("9786041123458");
        book2.setSummary("Hồi ký chiến trường Tây Nguyên kể lại sức sống của dân bản địa và tinh thần quật cường của quân dân Việt Nam");
        book2.setPublisherId(publisherKimDong.getId());
        book2.setPublishYear(2018);
        book2.setEdition("2022");
        book2.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915888/books/%C4%90%E1%BA%A5t_n%C6%B0%E1%BB%9Bc_%C4%91%E1%BB%A9ng_l%C3%AAn_kjfeba.jpg");
        book2.setBorrowFee(BigDecimal.valueOf(2000));
        book2.setTotalCopies(7);
        book2.setAvailableCopies(7);
        book2.setAuthors(new HashSet<>(Arrays.asList(nguyenNgoc)));
        book2.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("Nỗi buồn chiến tranh");
        book3.setIsbn("9786041156782");
        book3.setSummary("Câu chuyện nhân bản mở ra nỗi đau chiến tranh qua góc nhìn một người lính trở về và ký ức của những thân phận bị thương");
        book3.setPublisherId(publisherVanHoc.getId());
        book3.setPublishYear(2021);
        book3.setEdition("2024");
        book3.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915886/books/N%E1%BB%97i_bu%E1%BB%93n_chi%E1%BA%BFn_tranh_bwyflr.jpg");
        book3.setBorrowFee(BigDecimal.valueOf(2000));
        book3.setTotalCopies(6);
        book3.setAvailableCopies(6);
        book3.setAuthors(new HashSet<>(Arrays.asList(baoNinh)));
        book3.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setTitle("Cho tôi xin một vé đi tuổi thơ");
        book4.setIsbn("9786041178908");
        book4.setSummary("Ký ức sáng trong tuổi thơ 8x-9x với những chi tiết giản dị và cảm xúc ấm áp nơi phố thị Việt Nam");
        book4.setPublisherId(publisherTre.getId());
        book4.setPublishYear(2008);
        book4.setEdition("2025");
        book4.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915885/books/Cho_t%C3%B4i_xin_m%E1%BB%99t_v%C3%A9_%C4%91i_tu%E1%BB%95i_th%C6%A1_r3b8vx.jpg");
        book4.setBorrowFee(BigDecimal.valueOf(1000));
        book4.setTotalCopies(8);
        book4.setAvailableCopies(8);
        book4.setAuthors(new HashSet<>(Arrays.asList(nguyenNhatAnh)));
        book4.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book4);

        Book book5 = new Book();
        book5.setTitle("Tôi thấy hoa vàng trên cỏ xanh");
        book5.setIsbn("9786041134561");
        book5.setSummary("Miêu tả miền quê Việt Nam qua hành trình tìm lại sự thanh thản của một đứa trẻ mê đắm sắc vàng của hoa cỏ");
        book5.setPublisherId(publisherTre.getId());
        book5.setPublishYear(2010);
        book5.setEdition("2024");
        book5.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915885/books/T%C3%B4i_th%E1%BA%A5y_hoa_v%C3%A0ng_tr%C3%AAn_c%E1%BB%8F_xanh_yjc9oz.jpg");
        book5.setBorrowFee(BigDecimal.valueOf(1000));
        book5.setTotalCopies(7);
        book5.setAvailableCopies(7);
        book5.setAuthors(new HashSet<>(Arrays.asList(nguyenNhatAnh)));
        book5.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book5);

        Book book6 = new Book();
        book6.setTitle("Chuyện cũ Hà Nội");
        book6.setIsbn("9786041189013");
        book6.setSummary("Tái hiện Hà Nội trước đổi mới với lời văn thơ mộng và nỗi nhớ về những ngõ nhỏ, quán cóc và con người thân quen");
        book6.setPublisherId(publisherVanHoc.getId());
        book6.setPublishYear(2019);
        book6.setEdition("-");
        book6.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915885/books/Chuy%E1%BB%87n_c%C5%A9_H%C3%A0_N%E1%BB%99i_eevdp3.jpg");
        book6.setBorrowFee(BigDecimal.valueOf(1000));
        book6.setTotalCopies(5);
        book6.setAvailableCopies(5);
        book6.setAuthors(new HashSet<>(Arrays.asList(toHoai)));
        book6.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book6);

        Book book7 = new Book();
        book7.setTitle("Dế Mèn phiêu lưu ký");
        book7.setIsbn("9786041112345");
        book7.setSummary("Phiêu lưu tưởng tượng tinh nghịch của Dế Mèn, khắc họa tinh thần khám phá và bản lĩnh của trẻ em Việt Nam");
        book7.setPublisherId(publisherKimDong.getId());
        book7.setPublishYear(2022);
        book7.setEdition("2025");
        book7.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915885/books/D%E1%BA%BF_M%C3%A8n_phi%C3%AAu_l%C6%B0u_k%C3%BD_mchhee.jpg");
        book7.setBorrowFee(BigDecimal.valueOf(1000));
        book7.setTotalCopies(9);
        book7.setAvailableCopies(9);
        book7.setAuthors(new HashSet<>(Arrays.asList(toHoai)));
        book7.setCategories(new HashSet<>(Arrays.asList(viet, children)));
        bookRepository.save(book7);

        Book book8 = new Book();
        book8.setTitle("Hồ Chí Minh – Một cuộc đời");
        book8.setIsbn("9786041167896");
        book8.setSummary("Tiểu sử chi tiết về Chủ tịch Hồ Chí Minh, hệ thống tư liệu và câu chuyện đời thường của Người được kể chân thực");
        book8.setPublisherId(publisherChinhTri.getId());
        book8.setPublishYear(2018);
        book8.setEdition("2023");
        book8.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915881/books/H%E1%BB%93_Ch%C3%AD_Minh_M%E1%BB%99t_cu%E1%BB%99c_%C4%91%E1%BB%9Di_aoity0.jpg");
        book8.setBorrowFee(BigDecimal.valueOf(2000));
        book8.setTotalCopies(6);
        book8.setAvailableCopies(6);
        book8.setAuthors(new HashSet<>(Arrays.asList(williamDuiker)));
        book8.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book8);

        Book book9 = new Book();
        book9.setTitle("Miền hoang tưởng");
        book9.setIsbn("9786041198764");
        book9.setSummary("Tập truyện ngắn hiện đại ghi lại tiếng nói nội tâm, sự trăn trở về hồn quê và số phận con người sau chiến tranh");
        book9.setPublisherId(publisherHoi.getId());
        book9.setPublishYear(2021);
        book9.setEdition("-");
        book9.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915881/books/Mi%E1%BB%81n_hoang_t%C6%B0%E1%BB%9Fng_mgruac.png");
        book9.setBorrowFee(BigDecimal.valueOf(1000));
        book9.setTotalCopies(5);
        book9.setAvailableCopies(5);
        book9.setAuthors(new HashSet<>(Arrays.asList(nguyenHuyThiep)));
        book9.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book9);

        Book book10 = new Book();
        book10.setTitle("Cánh đồng bất tận");
        book10.setIsbn("9786041145679");
        book10.setSummary("Bức tranh miền Tây sông nước với nhân vật đa chiều, những phút giây xúc động và mẫu số chung của tình người");
        book10.setPublisherId(publisherTre.getId());
        book10.setPublishYear(2015);
        book10.setEdition("2024");
        book10.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915880/books/C%C3%A1nh_%C4%91%E1%BB%93ng_b%E1%BA%A5t_t%E1%BA%ADn_ddwolj.jpg");
        book10.setBorrowFee(BigDecimal.valueOf(1000));
        book10.setTotalCopies(6);
        book10.setAvailableCopies(6);
        book10.setAuthors(new HashSet<>(Arrays.asList(nguyenNgocTu)));
        book10.setCategories(new HashSet<>(Arrays.asList(viet)));
        bookRepository.save(book10);

        // Create foreign literature books
        Book book11 = new Book();
        book11.setTitle("The Great Gatsby");
        book11.setIsbn("9780743273565");
        book11.setSummary("Tiểu thuyết Mỹ hiện thực phơi bày tham vọng, sự suy tàn của giấc mơ Mỹ và sự cô đơn của người giàu sang");
        book11.setPublisherId(publisherScribner.getId());
        book11.setPublishYear(2022);
        book11.setEdition("2024");
        book11.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915881/books/The_Great_Gatsby_pdjtxs.png");
        book11.setBorrowFee(BigDecimal.valueOf(1000));
        book11.setTotalCopies(5);
        book11.setAvailableCopies(5);
        book11.setAuthors(new HashSet<>(Arrays.asList(fScott)));
        book11.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book11);

        Book book12 = new Book();
        book12.setTitle("1984");
        book12.setIsbn("9780451524933");
        book12.setSummary("Dystopia lạnh lùng về xã hội bị giám sát tối đa, nơi mỗi suy nghĩ bị theo dõi và quyền cá nhân bị xóa bỏ");
        book12.setPublisherId(publisherHoi.getId());
        book12.setPublishYear(2020);
        book12.setEdition("2025");
        book12.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915880/books/1984_b4ui9s.jpg");
        book12.setBorrowFee(BigDecimal.valueOf(1000));
        book12.setTotalCopies(6);
        book12.setAvailableCopies(6);
        book12.setAuthors(new HashSet<>(Arrays.asList(georgeOrwell)));
        book12.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book12);

        Book book13 = new Book();
        book13.setTitle("To Kill a Mockingbird");
        book13.setIsbn("9780061120087");
        book13.setSummary("Qua lăng kính đứa trẻ, cuốn sách khám phá công lý và lòng nhân đạo trong xã hội Mỹ thời kỳ phân biệt chủng tộc");
        book13.setPublisherId(publisherKimDong.getId());
        book13.setPublishYear(2021);
        book13.setEdition("-");
        book13.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915880/books/To_Kill_a_Mockingbird_ruayqk.jpg");
        book13.setBorrowFee(BigDecimal.valueOf(1000));
        book13.setTotalCopies(7);
        book13.setAvailableCopies(7);
        book13.setAuthors(new HashSet<>(Arrays.asList(harperLee)));
        book13.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book13);

        Book book14 = new Book();
        book14.setTitle("One Hundred Years of Solitude");
        book14.setIsbn("9780061120091");
        book14.setSummary("Đại tác phẩm hiện thực huyền ảo của Márquez xoay quanh gia đình Buendía và vòng lặp lịch sử kỳ ảo tại Macondo");
        book14.setPublisherId(publisherVanHoc.getId());
        book14.setPublishYear(2023);
        book14.setEdition("-");
        book14.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915879/books/One_Hundred_Years_of_Solitude_zw6xbw.jpg");
        book14.setBorrowFee(BigDecimal.valueOf(2000));
        book14.setTotalCopies(5);
        book14.setAvailableCopies(5);
        book14.setAuthors(new HashSet<>(Arrays.asList(marquez)));
        book14.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book14);

        Book book15 = new Book();
        book15.setTitle("Crime and Punishment");
        book15.setIsbn("9780143058144");
        book15.setSummary("Sự đấu tranh nội tâm của nhân vật chính giữa tội lỗi, trừng phạt và hy vọng được giải thoát tâm linh");
        book15.setPublisherId(publisherHoi.getId());
        book15.setPublishYear(2019);
        book15.setEdition("2024");
        book15.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915880/books/Crime_and_Punishment_edyopf.jpg");
        book15.setBorrowFee(BigDecimal.valueOf(2000));
        book15.setTotalCopies(6);
        book15.setAvailableCopies(6);
        book15.setAuthors(new HashSet<>(Arrays.asList(dostoevsky)));
        book15.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book15);

        Book book16 = new Book();
        book16.setTitle("Pride and Prejudice");
        book16.setIsbn("9780141439518");
        book16.setSummary("Hài hước và sắc bén về tình yêu, tiền tài và định kiến xã hội Anh thời hậu Napoléon");
        book16.setPublisherId(publisherTre.getId());
        book16.setPublishYear(2020);
        book16.setEdition("2025");
        book16.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915879/books/Pride_and_Prejudice_jgj94c.jpg");
        book16.setBorrowFee(BigDecimal.valueOf(1000));
        book16.setTotalCopies(5);
        book16.setAvailableCopies(5);
        book16.setAuthors(new HashSet<>(Arrays.asList(janeAusten)));
        book16.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book16);

        Book book17 = new Book();
        book17.setTitle("The Little Prince");
        book17.setIsbn("9780156017890");
        book17.setSummary("Bản tình ca triết lý của Saint-Exupéry với những bài học về tình bạn, trách nhiệm và con tim của người lớn");
        book17.setPublisherId(publisherKimDong.getId());
        book17.setPublishYear(2022);
        book17.setEdition("2025");
        book17.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915878/books/The_Little_Prince_itx6s8.jpg");
        book17.setBorrowFee(BigDecimal.valueOf(1000));
        book17.setTotalCopies(7);
        book17.setAvailableCopies(7);
        book17.setAuthors(new HashSet<>(Arrays.asList(saintExupery)));
        book17.setCategories(new HashSet<>(Arrays.asList(foreign, children)));
        bookRepository.save(book17);

        Book book18 = new Book();
        book18.setTitle("Harry Potter and the Philosopher's Stone");
        book18.setIsbn("9780439708180");
        book18.setSummary("Khởi đầu của trường phái phù thủy, đầy phép màu, tình bạn và những thử thách đầu đời");
        book18.setPublisherId(publisherTre.getId());
        book18.setPublishYear(2021);
        book18.setEdition("2025");
        book18.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915609/books/Harry_Potter_and_the_Philosopher_s_Stone_boukex.jpg");
        book18.setBorrowFee(BigDecimal.valueOf(1000));
        book18.setTotalCopies(8);
        book18.setAvailableCopies(8);
        book18.setAuthors(new HashSet<>(Arrays.asList(rowling)));
        book18.setCategories(new HashSet<>(Arrays.asList(foreign, children)));
        bookRepository.save(book18);

        Book book19 = new Book();
        book19.setTitle("The Alchemist");
        book19.setIsbn("9780062315007");
        book19.setSummary("Hành trình thiêng liêng đi tìm bản chất giấc mơ cá nhân và sự thực của vận mệnh được kể giản dị nhưng sâu sắc");
        book19.setPublisherId(publisherVanHoc.getId());
        book19.setPublishYear(2020);
        book19.setEdition("2024");
        book19.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915574/books/The_Alchemist_lfovi7.jpg");
        book19.setBorrowFee(BigDecimal.valueOf(2000));
        book19.setTotalCopies(4);
        book19.setAvailableCopies(4);
        book19.setAuthors(new HashSet<>(Arrays.asList(coelho)));
        book19.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book19);

        Book book20 = new Book();
        book20.setTitle("Norwegian Wood");
        book20.setIsbn("9780375704024");
        book20.setSummary("Murakami phác họa tuổi trẻ bất an, tình yêu và nỗi cô đơn sâu thẳm trong Tokyo hiện đại");
        book20.setPublisherId(publisherHoi.getId());
        book20.setPublishYear(2022);
        book20.setEdition("-");
        book20.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915571/books/Norwegian_Wood_saapbt.jpg");
        book20.setBorrowFee(BigDecimal.valueOf(2000));
        book20.setTotalCopies(5);
        book20.setAvailableCopies(5);
        book20.setAuthors(new HashSet<>(Arrays.asList(murakami)));
        book20.setCategories(new HashSet<>(Arrays.asList(foreign)));
        bookRepository.save(book20);

        // Science & technology books
        Book book21 = new Book();
        book21.setTitle("Sapiens: Lược sử loài người");
        book21.setIsbn("9786043149457");
        book21.setSummary("Lược sử nhân loại từ thời nguyên thủy đến hiện đại qua góc nhìn vĩ mô, kết hợp khảo cứu và triết lý");
        book21.setPublisherId(publisherTheGioi.getId());
        book21.setPublishYear(2019);
        book21.setEdition("2025");
        book21.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915570/books/Sapiens_L%C6%B0%E1%BB%A3c_s%E1%BB%AD_lo%C3%A0i_ng%C6%B0%E1%BB%9Di_u9gswd.jpg");
        book21.setBorrowFee(BigDecimal.valueOf(2000));
        book21.setTotalCopies(6);
        book21.setAvailableCopies(6);
        book21.setAuthors(new HashSet<>(Arrays.asList(yuval)));
        book21.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book21);

        Book book22 = new Book();
        book22.setTitle("Vũ trụ");
        book22.setIsbn("9786043145679");
        book22.setSummary("Lời tự sự mê hoặc giúp độc giả hiểu các khái niệm vũ trụ học và sự kỳ vĩ của không gian");
        book22.setPublisherId(publisherTheGioi.getId());
        book22.setPublishYear(2020);
        book22.setEdition("2024");
        book22.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915572/books/V%C5%A9_tr%E1%BB%A5_doliwc.png");
        book22.setBorrowFee(BigDecimal.valueOf(1000));
        book22.setTotalCopies(5);
        book22.setAvailableCopies(5);
        book22.setAuthors(new HashSet<>(Arrays.asList(carlSagan)));
        book22.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book22);

        Book book23 = new Book();
        book23.setTitle("Lược sử thời gian");
        book23.setIsbn("9786043141239");
        book23.setSummary("Hành trình từ Big Bang đến lỗ đen, giải thích vật lý cao cấp bằng ngôn ngữ gần gũi và hình ảnh sinh động");
        book23.setPublisherId(publisherTre.getId());
        book23.setPublishYear(2021);
        book23.setEdition("2025");
        book23.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915570/books/L%C6%B0%E1%BB%A3c_s%E1%BB%AD_th%E1%BB%9Di_gian_coh7ha.jpg");
        book23.setBorrowFee(BigDecimal.valueOf(1000));
        book23.setTotalCopies(4);
        book23.setAvailableCopies(4);
        book23.setAuthors(new HashSet<>(Arrays.asList(stephenHawking)));
        book23.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book23);

        Book book24 = new Book();
        book24.setTitle("Người đẹp say ngủ");
        book24.setIsbn("9786043147897");
        book24.setSummary("Chia sẻ phóng khoáng về vật lý và những câu chuyện đam mê khám phá nhân loại");
        book24.setPublisherId(publisherTheGioi.getId());
        book24.setPublishYear(2022);
        book24.setEdition("-");
        book24.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915570/books/Ng%C6%B0%E1%BB%9Di_%C4%91%E1%BA%B9p_say_ng%E1%BB%A7_cmmsi0.png");
        book24.setBorrowFee(BigDecimal.valueOf(2000));
        book24.setTotalCopies(5);
        book24.setAvailableCopies(5);
        book24.setAuthors(new HashSet<>(Arrays.asList(richardFeynman)));
        book24.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book24);

        Book book25 = new Book();
        book25.setTitle("Homo Deus: Lược sử tương lai");
        book25.setIsbn("9786043145672");
        book25.setSummary("Nhìn về tương lai loài người trong kỷ nguyên công nghệ, kết hợp dự đoán và phân tích xã hội");
        book25.setPublisherId(publisherTheGioi.getId());
        book25.setPublishYear(2020);
        book25.setEdition("2024");
        book25.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915570/books/Homo_Deus_L%C6%B0%E1%BB%A3c_s%E1%BB%AD_t%C6%B0%C6%A1ng_lai_wtssmk.jpg");
        book25.setBorrowFee(BigDecimal.valueOf(1000));
        book25.setTotalCopies(5);
        book25.setAvailableCopies(5);
        book25.setAuthors(new HashSet<>(Arrays.asList(yuval)));
        book25.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book25);

        Book book26 = new Book();
        book26.setTitle("Artificial Intelligence: A Modern Approach");
        book26.setIsbn("9780134610990");
        book26.setSummary("Giáo trình AI mang tính toàn diện, đề cập thuật toán, học máy và cân nhắc đạo đức cho kỷ nguyên số");
        book26.setPublisherId(publisherKHTT.getId());
        book26.setPublishYear(2023);
        book26.setEdition("-");
        book26.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915569/books/Artificial_Intelligence_A_Modern_Approach_utcvyb.jpg");
        book26.setBorrowFee(BigDecimal.valueOf(2000));
        book26.setTotalCopies(4);
        book26.setAvailableCopies(4);
        book26.setAuthors(new HashSet<>(Arrays.asList(stuartRussell, peterNorvig)));
        book26.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book26);

        Book book27 = new Book();
        book27.setTitle("Clean Code");
        book27.setIsbn("9780132350886");
        book27.setSummary("Hướng dẫn xây dựng phần mềm bền vững với chuẩn mực kỹ thuật, tái cấu trúc và thử nghiệm tự động");
        book27.setPublisherId(publisherTTTT.getId());
        book27.setPublishYear(2021);
        book27.setEdition("2024");
        book27.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915569/books/Clean_Code_lihviv.jpg");
        book27.setBorrowFee(BigDecimal.valueOf(2000));
        book27.setTotalCopies(5);
        book27.setAvailableCopies(5);
        book27.setAuthors(new HashSet<>(Arrays.asList(robertMartin)));
        book27.setCategories(new HashSet<>(Arrays.asList(science)));
        bookRepository.save(book27);

        Book book28 = new Book();
        book28.setTitle("Atomic Habits");
        book28.setIsbn("9780735211292");
        book28.setSummary("Hệ thống thói quen nhỏ nhưng nhất quán, giúp thay đổi cuộc sống và công việc một cách thực tế");
        book28.setPublisherId(publisherTongHop.getId());
        book28.setPublishYear(2018);
        book28.setEdition("2024");
        book28.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915570/books/Atomic_Habits_gw1x6g.png");
        book28.setBorrowFee(BigDecimal.valueOf(1000));
        book28.setTotalCopies(6);
        book28.setAvailableCopies(6);
        book28.setAuthors(new HashSet<>(Arrays.asList(jamesClear)));
        book28.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book28);

        Book book29 = new Book();
        book29.setTitle("The 7 Habits of Highly Effective People");
        book29.setIsbn("9780743269513");
        book29.setSummary("7 thói quen cốt lõi đem lại hiệu suất và giá trị nhân cách bền vững cho cá nhân và tổ chức");
        book29.setPublisherId(publisherVanHoc.getId());
        book29.setPublishYear(2019);
        book29.setEdition("2025");
        book29.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915569/books/The_7_Habits_of_Highly_Effective_People_zjemc9.png");
        book29.setBorrowFee(BigDecimal.valueOf(1000));
        book29.setTotalCopies(5);
        book29.setAvailableCopies(5);
        book29.setAuthors(new HashSet<>(Arrays.asList(stephenCovey)));
        book29.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book29);

        Book book30 = new Book();
        book30.setTitle("Thinking, Fast and Slow");
        book30.setIsbn("9780143127550");
        book30.setSummary("So sánh tư duy nhanh trực giác và tư duy chậm phản biện để hiểu cách chúng ảnh hưởng quyết định hàng ngày");
        book30.setPublisherId(publisherTheGioi.getId());
        book30.setPublishYear(2017);
        book30.setEdition("2023");
        book30.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915569/books/Thinking_Fast_and_Slow_hacjjv.png");
        book30.setBorrowFee(BigDecimal.valueOf(1000));
        book30.setTotalCopies(4);
        book30.setAvailableCopies(4);
        book30.setAuthors(new HashSet<>(Arrays.asList(danielKahneman)));
        book30.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book30);

        Book book31 = new Book();
        book31.setTitle("Mindset: The New Psychology of Success");
        book31.setIsbn("9780345472328");
        book31.setSummary("Phân tích khác biệt giữa tư duy cố định và tư duy phát triển, giúp chuyển hóa thái độ học tập và thành công");
        book31.setPublisherId(publisherChinhTri.getId());
        book31.setPublishYear(2016);
        book31.setEdition("2024");
        book31.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915568/books/Mindset_The_New_Psychology_of_Success_bvcigv.jpg");
        book31.setBorrowFee(BigDecimal.valueOf(1000));
        book31.setTotalCopies(5);
        book31.setAvailableCopies(5);
        book31.setAuthors(new HashSet<>(Arrays.asList(carolDweck)));
        book31.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book31);

        Book book32 = new Book();
        book32.setTitle("The Courage to Be Disliked");
        book32.setIsbn("9781594635273");
        book32.setSummary("Triết lý Adler giúp người đọc tự do khỏi sự đánh giá của người khác và sống chủ động hơn");
        book32.setPublisherId(publisherHongDuc.getId());
        book32.setPublishYear(2018);
        book32.setEdition("2022");
        book32.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915568/books/The_Courage_to_Be_Disliked_hpyctv.png");
        book32.setBorrowFee(BigDecimal.valueOf(1000));
        book32.setTotalCopies(4);
        book32.setAvailableCopies(4);
        book32.setAuthors(new HashSet<>(Arrays.asList(ichiroKishimi)));
        book32.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book32);

        Book book33 = new Book();
        book33.setTitle("How to Win Friends and Influence People");
        book33.setIsbn("9780671027032");
        book33.setSummary("Kinh điển của Carnegie về kỹ năng giao tiếp, xây dựng quan hệ và tạo ảnh hưởng tích cực trong mọi tình huống");
        book33.setPublisherId(publisherLaoDong.getId());
        book33.setPublishYear(2020);
        book33.setEdition("2023");
        book33.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915567/books/How_to_Win_Friends_and_Influence_People_mh56h6.jpg");
        book33.setBorrowFee(BigDecimal.valueOf(1000));
        book33.setTotalCopies(6);
        book33.setAvailableCopies(6);
        book33.setAuthors(new HashSet<>(Arrays.asList(daleCarnegie)));
        book33.setCategories(new HashSet<>(Arrays.asList(selfHelp)));
        bookRepository.save(book33);

        Book book34 = new Book();
        book34.setTitle("Charlie and the Chocolate Factory");
        book34.setIsbn("9780142410318");
        book34.setSummary("Cuộc phiêu lưu kỳ ảo của Charlie trong nhà máy chocolate, hòa quyện giữa sự kỳ diệu và bài học đạo đức");
        book34.setPublisherId(publisherTre.getId());
        book34.setPublishYear(2015);
        book34.setEdition("2024");
        book34.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915569/books/Charlie_and_the_Chocolate_Factory_cpvrjb.png");
        book34.setBorrowFee(BigDecimal.valueOf(1000));
        book34.setTotalCopies(8);
        book34.setAvailableCopies(8);
        book34.setAuthors(new HashSet<>(Arrays.asList(roaldDahl)));
        book34.setCategories(new HashSet<>(Arrays.asList(children, foreign)));
        bookRepository.save(book34);

        Book book35 = new Book();
        book35.setTitle("Wonder");
        book35.setIsbn("9780375869020");
        book35.setSummary("Chuyện cảm động về Auggie – một cậu bé mặt khác thường – và hành trình dấn thân giúp người khác hiểu mình");
        book35.setPublisherId(publisherScribner.getId());
        book35.setPublishYear(2018);
        book35.setEdition("2023");
        book35.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915568/books/Wonder_dexmim.jpg");
        book35.setBorrowFee(BigDecimal.valueOf(1000));
        book35.setTotalCopies(7);
        book35.setAvailableCopies(7);
        book35.setAuthors(new HashSet<>(Arrays.asList(rjPalacio)));
        book35.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book35);

        Book book36 = new Book();
        book36.setTitle("Charlotte's Web");
        book36.setIsbn("9780064400558");
        book36.setSummary("Tình bạn giữa cô bé Fern và chú heo Wilbur, bài học về sự hy sinh và trách nhiệm với sinh vật bé nhỏ");
        book36.setPublisherId(publisherHongDuc.getId());
        book36.setPublishYear(2017);
        book36.setEdition("2024");
        book36.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915567/books/Charlotte_s_Web_vl1zrk.jpg");
        book36.setBorrowFee(BigDecimal.valueOf(1000));
        book36.setTotalCopies(6);
        book36.setAvailableCopies(6);
        book36.setAuthors(new HashSet<>(Arrays.asList(ebWhite)));
        book36.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book36);

        Book book37 = new Book();
        book37.setTitle("Fairy Tales");
        book37.setIsbn("9780141326120");
        book37.setSummary("Tuyển tập Andersen với hình ảnh giàu tính tưởng tượng, mang đến bài học luân lý sâu sắc và cảm xúc dịu dàng");
        book37.setPublisherId(publisherKimDong.getId());
        book37.setPublishYear(2019);
        book37.setEdition("2024");
        book37.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915568/books/Fairy_Tales_tyev0h.png");
        book37.setBorrowFee(BigDecimal.valueOf(1000));
        book37.setTotalCopies(5);
        book37.setAvailableCopies(5);
        book37.setAuthors(new HashSet<>(Arrays.asList(hansAndersen)));
        book37.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book37);

        Book book38 = new Book();
        book38.setTitle("Grimm's Fairy Tales");
        book38.setIsbn("9780141375560");
        book38.setSummary("Biên niên sử Grimm đầy đen tối, kỳ ảo và giàu bài học cho trẻ em tuổi lớn hơn");
        book38.setPublisherId(publisherKimDong.getId());
        book38.setPublishYear(2016);
        book38.setEdition("2023");
        book38.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915568/books/Grimm_s_Fairy_Tales_bv7him.jpg");
        book38.setBorrowFee(BigDecimal.valueOf(1000));
        book38.setTotalCopies(6);
        book38.setAvailableCopies(6);
        book38.setAuthors(new HashSet<>(Arrays.asList(grimBrothers)));
        book38.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book38);

        Book book39 = new Book();
        book39.setTitle("The Reason I Jump");
        book39.setIsbn("9780553419156");
        book39.setSummary("Nhật ký xúc động giúp độc giả thấu hiểu thế giới nội tâm của một cậu bé tự kỷ nhạy cảm và thông minh");
        book39.setPublisherId(publisherLaoDong.getId());
        book39.setPublishYear(2019);
        book39.setEdition("2024");
        book39.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915567/books/The_Reason_I_Jump_ornel7.jpg");
        book39.setBorrowFee(BigDecimal.valueOf(1000));
        book39.setTotalCopies(5);
        book39.setAvailableCopies(5);
        book39.setAuthors(new HashSet<>(Arrays.asList(naokiHigashida)));
        book39.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book39);

        Book book40 = new Book();
        book40.setTitle("Diary of a Wimpy Kid");
        book40.setIsbn("9781419703688");
        book40.setSummary("Nhật ký hài hước và châm biếm về Greg, phản ánh áp lực học đường, gia đình và tình bạn của một cậu bé tuổi teen");
        book40.setPublisherId(publisherTre.getId());
        book40.setPublishYear(2020);
        book40.setEdition("2024");
        book40.setCoverImageUrl("https://res.cloudinary.com/dehn8lwxv/image/upload/v1763915567/books/Diary_of_a_Wimpy_Kid_nil0f2.jpg");
        book40.setBorrowFee(BigDecimal.valueOf(1000));
        book40.setTotalCopies(7);
        book40.setAvailableCopies(7);
        book40.setAuthors(new HashSet<>(Arrays.asList(jeffKinney)));
        book40.setCategories(new HashSet<>(Arrays.asList(children)));
        bookRepository.save(book40);
    }
}