import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;

public class BookTest {

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/library_catalog_test", null, null);
  }

  @After
  public void tearDown() {
    try (Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM books *;";
      con.createQuery(sql).executeUpdate();
    }
  }

  @Test
  public void Book_instantiatesCorrectly_true() {
    Book book = new Book("Title", "Author");
    assertTrue(book instanceof Book);
  }

  @Test
  public void getters_returnsCorrectly_true() {
    Book book = new Book("Title", "Author");
    assertEquals("Title", book.getTitle());
    assertEquals("Author", book.getAuthor());
    assertEquals(false, book.getIsCheckedOut());
  }

  @Test
  public void save_savesToDB_true() {
    Book book = new Book("Title", "Author");
    book.save();
    assertTrue(Book.all().get(0).equals(book));
  }

  @Test
  public void find_returnsById_book2() {
    Book book1 = new Book("Title", "Author");
    book1.save();
    Book book2 = new Book("Titlez", "Authorz");
    book2.save();
    assertEquals(book2, Book.find(book2.getId()));
  }

  @Test
  public void updateIsCheckedOut_changeCheckedOutStatus_true() {
    Book book = new Book("Title", "Author");
    book.save();
    book.updateIsCheckedOut();
    assertEquals(true, Book.find(book.getId()).getIsCheckedOut());
  }

  @Test
  public void deleteBook_deletesBook_true() {
    Book book = new Book("Title", "Author");
    book.save();
    book.deleteBook();
    assertEquals(null, Book.find(book.getId()));
  }

  @Test
  public void findByTitle_returnsBookWithMatchingTitle_true() {
    Book book = new Book("Title", "Author");
    book.save();
    Book book2 = new Book("Fresh", "Me");
    book2.save();
    assertEquals(book2, Book.findByTitle(book2.getTitle()));
  }

  @Test
  public void findByAuthor_returnsBookByAuthor_true() {
    Book book = new Book("Title", "Author");
    book.save();
    Book book2 = new Book("Fresh", "Me");
    book2.save();
    assertEquals(book2, Book.findByAuthor(book2.getAuthor()));
  }

  @Test
  public void getCheckOutDate_returnsDate_true() {
    Book book = new Book("Title", "Jerry");
    book.save();
    Timestamp savedDate = Book.find(book.getId()).getCheckedOutDate();
    Timestamp rightNow = new Timestamp(new Date().getTime());
    assertEquals(DateFormat.getDateTimeInstance().format(rightNow), DateFormat.getDateTimeInstance().format(savedDate));
  }

  @Test
  public void isDue_checksIfBookIsDue_false() {
    Book book = new Book("Title", "Author");
    book.save();
    assertFalse(Book.find(book.getId()).isDue());
  }

}
