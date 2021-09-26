import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Driver2 {
    public static void main(String[] args) {
        try {
            Class.forName(Driver.JDBC_DRIVER);
            Connection conn=Driver.createConnection("jdbc:h2:~/newdb2","root","toor");
            ArrayList<Person>list=Driver.createStatementAndExcute2(conn,"select * \n" +
                    "from (\n" +
                    "      select *\n" +
                    "      from OTHER_TABLE\n" +
                    "      except\n" +
                    "      select *\n" +
                    "      from PERSONS\n" +
                    "     ) as T\n" +
                    "union all\n" +
                    "select * \n" +
                    "from (\n" +
                    "      select *\n" +
                    "      from PERSONS\n" +
                    "      except\n" +
                    "      select *\n" +
                    "      from OTHER_TABLE\n" +
                    "     ) as T");



                    Driver.close(conn);

                    for (Person p : list){
                        System.out.println(p.toString());
                    }




        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
