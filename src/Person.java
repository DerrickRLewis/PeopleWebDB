
public class Person implements Comparable<Person> {

    public Person(Integer id, String firstName, String lastName, String email, String country, String ipAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.ipAddress = ipAddress;
    }

    Integer id;
    String firstName;
    String lastName;
    String email;
    String country;
    String ipAddress;

    @Override
    public int compareTo(Person o) {
        if(! this.lastName.equals(o.lastName)) return this.lastName.compareTo( o.lastName );
        else return this.firstName.compareTo( o.firstName );
    }
}
