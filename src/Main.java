import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Main {

//    public static ArrayList<Person> persons = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        populateDatabase(conn);

        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    boolean isZero = true;
                    Session s = request.session();
                    Integer offset = 0;
                    HashMap map = new HashMap<String, ArrayList<Person>>();
                    String offsetStr = request.queryParams("offset");

                    if(offsetStr != null && ! offsetStr.equals("") && Integer.valueOf(offsetStr) >= 0){
                        offset = Integer.valueOf(offsetStr);
                    }

                    ArrayList<Person> offsetPersons = selectPeople(conn, offset);

                    s.attribute("offset", offset);

                    if(offset != 0){
                        isZero = false;
                    }

                    map.put("isZero", isZero);
                    map.put("offset", offset);
                    map.put("people", offsetPersons);

                    return new ModelAndView(map, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                (request, response) -> {
                    HashMap m = new HashMap();
                    Integer id = Integer.valueOf(request.queryParams("id"));
                    Person p = selectPerson(conn, id);
                    m.put("person", p);
                    return new ModelAndView(m, "person.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/next",
                (request, response) -> {
                    Session s = request.session();
                    Integer offset = s.attribute("offset");
                    response.redirect("/?offset=" + (offset + 20));
                    return "";
                }
        );

        Spark.post(
                "/previous",
                (request, response) -> {
                    Session s = request.session();
                    Integer offset = s.attribute("offset");

                    if(offset >= 20) response.redirect("/?offset=" + (offset - 20));
                    else response.redirect("/?offset=" + offset);

                    return "";
                }
        );
    }


    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS people");
        stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip VARCHAR)");
    }

    public static void insertPerson(Connection conn, String firstName, String lastName, String email,
                                    String country, String ip) throws SQLException{

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ip);
        stmt.execute();
    }

    public static Person selectPerson(Connection conn, Integer id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet set = stmt.executeQuery();

        set.next();

        return new Person(1, set.getString("first_name"), set.getString("last_name"), set.getString("email"),
                set.getString("country"), set.getString("ip"));
    }

    public static ArrayList<Person> selectPeople(Connection conn, Integer offset) throws SQLException, FileNotFoundException {

        populateDatabase(conn);
        ArrayList<Person> persons = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        stmt.setString(1, String.valueOf(offset));
        ResultSet set = stmt.executeQuery();

        while(set.next()){
            persons.add(new Person(set.getInt("id"), set.getString("first_name"), set.getString("last_name"),
                    set.getString("email"), set.getString("country"), set.getString("ip")));
        }

        return persons;
    }

    static ArrayList<Person> populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        ArrayList<Person> persons = new ArrayList<>();
        File f = new File("src/people.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine();

        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            insertPerson(conn, p.firstName, p.lastName, p.email, p.country, p.ipAddress);
        }
        for (Person p : persons ) {
            insertPerson(conn, p.firstName, p.lastName, p.email, p.country, p.ipAddress);
        }

        return persons;
    }

}
