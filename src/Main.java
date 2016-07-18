
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;




public class Main {


    static ArrayList<Person> persons = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("people.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] columns = line.split(",");
            Person person = new Person(Integer.valueOf(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
            persons.add(person);



        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    HashMap map = new HashMap<String, ArrayList<Person>>();


                    return new ModelAndView(map, "index.html");
        }

                    );




        Spark.get(
                "/person",
                (request, response) -> {

                },
                new MustacheTemplateEngine()
        );



        Spark.post(
                "/next",
                (request, response) -> {


                    return "";
                }
        );

        Spark.post(
                "/previous",
                (request, response) -> {





                    return "";
                }
        );



    }

        }
    }

