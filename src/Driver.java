import java.sql.*;
import java.util.ArrayList;

public class Driver {
    static final String JDBC_DRIVER = "org.h2.Driver";


    public static void main(String[] args) {


        try {
            Class.forName(JDBC_DRIVER);
            Connection conn=createConnection("jdbc:h2:~/newdb1","root","toor");


            ArrayList <Person>listFromTable1=createStatementAndExcute(conn,"select * from persons;");

            for (Person p:listFromTable1){
                System.out.println(p.toString());

            }

            close(conn);

            System.out.println("table2");
            Connection conn2=createConnection("jdbc:h2:~/newdb2","root","toor");


            ArrayList <Person>listFromTable2=createStatementAndExcute(conn2,"select * from persons;");

            for (Person p:listFromTable2){
                System.out.println(p.toString());
            }



            close(conn2);


            System.out.println("different rows ");






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
        //STEP 2: Open a connection
        System.out.println("Connecting to database...");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db_url,user,pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;



    }

    public static ArrayList<Person> createStatementAndExcute(Connection conn, String query){
        //STEP 3: Execute a query
        System.out.println("Creating table in given database...");
        ArrayList<Person>personsList=new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                personsList.add(new Person(rs.getInt("PersonID"),rs.getString("FirstName"),rs.getString("LastName")));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Created table in given database...");
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personsList;


    }

}
