import java.util.List;

public interface BookDAO {
    List<Book> findByYearAndAuthor(int year, String author);
}
