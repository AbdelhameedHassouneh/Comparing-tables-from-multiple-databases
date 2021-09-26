
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Driver {
    static final String JDBC_DRIVER = "org.h2.Driver";


    public static void main(String[] args) {


        try {
            Class.forName(JDBC_DRIVER);
            Connection conn=createConnection("jdbc:h2:~/newdb1","root","toor");


            HashMap <Integer,Person>map1=createStatementAndExcute(conn,"select * from persons;");

            for (Map.Entry<Integer, Person> p:map1.entrySet()){
                System.out.println(p.toString());

            }

            close(conn);

            System.out.println("table2");
            Connection conn2=createConnection("jdbc:h2:~/newdb2","root","toor");


            HashMap <Integer,Person>map2=createStatementAndExcute(conn2,"select * from persons;");

            for (Map.Entry<Integer, Person> p:map2.entrySet()){
                System.out.println(p.toString());

            }



            close(conn2);


            System.out.println("different rows ");


            for (Integer i:map2.keySet()) {
                Person person=map2.get(i);
                if(map1.get(i)==null||!person.equal(map1.get(i))){
                    System.out.println(person.toString());
                }
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        System.out.println("The end");
    }

    public static void close(Connection conn) {

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            if(conn!=null) conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        }
    }

    public static Connection createConnection(String db_url,String user,String pass){

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db_url,user,pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;



    }

    public static HashMap<Integer,Person> createStatementAndExcute(Connection conn, String query){
        //STEP 3: Execute a query
        HashMap<Integer,Person>map =new HashMap<>();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                map.put(rs.getInt("PersonID"),new Person(rs.getInt("PersonID"),rs.getString("FirstName"),rs.getString("LastName")));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static ArrayList<Person> createStatementAndExcute2(Connection conn, String query){
        //STEP 3: Execute a query
        ArrayList<Person>list =new ArrayList<>();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                list.add(new Person(rs.getInt("PersonID"),rs.getString("FirstName"),rs.getString("LastName")));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



}