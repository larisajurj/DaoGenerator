import java.util.List;

public interface EmployeeDAO {
    List<Employee> findBySsn(String ssn);
    List<Employee> findByNameAndSurname(String name, String surname);
    List<Employee> findBySalaryAndAge(double salary, int age);
    void deleteByNameAndSurname(String name, String surname);
    void save(Employee employee);
}