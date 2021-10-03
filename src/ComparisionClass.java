import java.io.*;
import java.sql.*;
import java.util.*;

public class ComparisionClass {
    public static void main(String[] args) {


        DataBaseC database1 = readProperityFileAndGrepData("JDBCconf1.properties");
        DataBaseC database2 = readProperityFileAndGrepData("JDBCconf2.properties");

        compareDatabases(database1, database2);


    }


    public static DataBaseC grapData(String driverClass, String url, String usr, String pw) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DataBaseC database = new DataBaseC();
        Connection conn = createConnection(url, usr, pw);
        try {

            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet resultSet = dbMeta.getTables(null, null, null, new String[]{"TABLE"});


            int size = 0;

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");

                Table table = new Table();
                table.tableName = tableName;
                ResultSet columns = dbMeta.getColumns(null, null, tableName, null);
                ResultSet primaryKeys = dbMeta.getPrimaryKeys(null, null, tableName);
                primaryKeys.next();

                table.primaryKey = primaryKeys.getString("COLUMN_NAME");


                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");


                    String datatype = columns.getString("TYPE_NAME");
                    int cSize = columns.getInt("COLUMN_SIZE");


                    Column c = new Column();
                    c.title = columnName;
                    c.dataType = datatype;
                    c.size = cSize;

                    table.columns.put(c.title, c);
                }
                size++;
                database.tables.put(table.tableName, table);
            }
            database.dataBaseName = url.split("~/")[1];
            database.numberOfTables = size;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return database;

    }


    public static void compareDatabases(DataBaseC db1, DataBaseC db2) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter("Report.txt");
        } catch (IOException e) {

            e.printStackTrace();
        }
        System.out.println("in");


        pw.append("DataBase 1 Name :" + db1.dataBaseName + "\n");
        pw.append("Database 1 #Of Tables: " + db1.numberOfTables + "\n");

        pw.append("DataBase 2 Name :" + db2.dataBaseName + "\n");
        pw.append("Database 2 #Of Tables: " + db2.numberOfTables + "\n");
        pw.append("**************Going through tables:*************\n");
        for (Map.Entry<String, Table> entry : db2.tables.entrySet()) {

            Table table = (Table) entry.getValue();

            pw.append("Table Name: " + table.tableName + "\n");
            Table tableindb1 = db1.tables.get(table.tableName);

            if (tableindb1 == null) {

                pw.append("*Note This table exists in db2 and it doesn't exist in db1\n");
                pw.append("Primary key of this table in db2 is: " + table.primaryKey + "\n");
                for (Column column : table.columns.values()) {
                    pw.append(column.title + " " + column.dataType + " " + column.size + "\n");
                    column.visited = true;

                }
                table.visited = true;
            } else {
                pw.append("*Note this table exists in db2 and db1\n");
                pw.append("Primary key of this table in db2 is: " + table.primaryKey + " and in db1 is: " + tableindb1.primaryKey + "\n");


                for (Column column : table.columns.values()) {
                    Column c = tableindb1.columns.get(column.title);
                    if (c == null) {

                        pw.append(column.title + " " + column.dataType + " " + column.size + " exists in db2 and it doesn't exist in db1\n");
                        column.visited = true;

                    } else {
                        if (column.title.equals(c.title) && column.dataType.equals(c.dataType) && column.size == c.size) {

                            pw.append(column.title + " " + column.dataType + " " + column.size + ": exists and the same\n");


                        } else if (column.title.equals(c.title) && (!(column.dataType.equals(c.dataType)) || !(column.size == c.size))) {
                            pw.append(column.title + " " + column.dataType + " " + column.size + " in db2 --- " + c.title + " " + c.dataType + " " + c.size + " in db1 , modified in db2\n");

                        }
                        c.visited = true;
                        column.visited = true;

                    }
                }

                //-----------------------
                for (Column column : tableindb1.columns.values()) {
                    if (!column.visited)
                        pw.append(column.title + " " + column.dataType + " " + column.size + " exists in db1 and it doesn't exist in db2\n");

                }
                //--------------------------


                table.visited = true;
                tableindb1.visited = true;

            }


            pw.append("------------------------------------------\n");

        }
        for (Map.Entry<String, Table> entry : db1.tables.entrySet()) {
            Table table = (Table) entry.getValue();
            if (!table.visited) {
                pw.append("Table Name: " + table.tableName + "\n");
                pw.append("*Note This table exists in db1 and it doesn't exist in db2\n");
                pw.append("Primary key of this table in db1 is: " + table.primaryKey + "\n");

                for (Column column : table.columns.values()) {

                    pw.append(column.title + " " + column.dataType + " " + column.size + "\n");
                    column.visited = true;


                }


                table.visited = true;
                pw.append("------------------------------------------\n");
            }


        }


        pw.close();

    }

    public static void close(Connection conn) {

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static Connection createConnection(String db_url, String user, String pass) {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db_url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;


    }


    public static DataBaseC readProperityFileAndGrepData(String filePath) {
        Properties props = new Properties();
        String dbDriverClass = "";
        String dbConnUrl = "";
        String dbUserName = "";
        String dbPassword = "";

        try {
            FileReader fileReader = new FileReader(filePath);
            props.load(fileReader);
            dbDriverClass = props.getProperty("db.driver.class");

            dbConnUrl = props.getProperty("db.conn.url");

            dbUserName = props.getProperty("db.username");

            dbPassword = props.getProperty("db.password");


            System.out.println(dbDriverClass + " " + dbConnUrl);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grapData(dbDriverClass, dbConnUrl, dbUserName, dbPassword);

    }
}
