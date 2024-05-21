import java.util.List;

public interface EmployeeDAO {
    List<Employee> findBySsn(String ssn);
    void deleteByNameAndSurname(String name, String surname);
    void save(Employee employee);
}