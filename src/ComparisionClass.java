import java.io.*;
import java.sql.*;
import java.util.*;

public class ComparisionClass {
    public static void main(String[] args) {


        DataBaseC database1 = readProperityFileAndGrepData("JDBCconf1.properties");
        DataBaseC database2 = readProperityFileAndGrepData("JDBCconf2.properties");

        compareDatabases(database1, database2);
        compareDatabases(database2, database1);



    }


    public static DataBaseC grapData(String driverClass, String url, String usr, String pw) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DataBaseC database = new DataBaseC();
        Connection conn = createConnection(url+"?autoReconnect=true&useSSL=false", usr, pw);
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
            String str[]=url.split("/");
            database.dataBaseName = str[str.length-1];
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
            pw = new PrintWriter(db2.dataBaseName+".txt");

        } catch (IOException e) {

            e.printStackTrace();
        }



        pw.append("DataBase 1 Name :" + db1.dataBaseName + "\n");
        pw.append("Database 1 #Of Tables: " + db1.numberOfTables + "\n");

        pw.append("DataBase 2 Name :" + db2.dataBaseName + "\n");
        pw.append("Database 2 #Of Tables: " + db2.numberOfTables + "\n");
        pw.append("**************Going through tables:*************\n");
        for (Map.Entry<String, Table> entry : db2.tables.entrySet()) {
            //This boolean is made when the table exists in the 2 databases, but we might face a difference during the Iteration
            boolean isThereADifference=false;

            Table table = (Table) entry.getValue();


            Table tableindb1 = db1.tables.get(table.tableName);

            if (tableindb1 == null) {
                pw.append("Table Name: " + table.tableName + "\n");
                pw.append("*Note This table exists in "+db2.dataBaseName+" and it doesn't exist in "+db1.dataBaseName+"\n");
                pw.append("Primary key: " + table.primaryKey + "\n");
                for (Column column : table.columns.values()) {
                    pw.append(column.title + " " + column.dataType + " " + column.size + "\n");


                }
                isThereADifference=true;
            } else {



                if(!table.primaryKey.equalsIgnoreCase(tableindb1.primaryKey)){
                    pw.append("Table Name: " + table.tableName + "\n");
                    pw.append("*Note this table exists in "+db2.dataBaseName+"  and in "+db1.dataBaseName+"\n");
                    pw.append("Primary key of this table in db2 is: " + table.primaryKey + " and in db1 is: " + tableindb1.primaryKey + "\n");
                    isThereADifference=true;

                }


                for (Column column : table.columns.values()) {
                    Column c = tableindb1.columns.get(column.title);
                    if (c == null) {
                        if(!isThereADifference){
                            pw.append("Table Name: " + table.tableName + "\n");
                            pw.append("*Note this table exists in "+db2.dataBaseName+"  and in "+db1.dataBaseName+"\n");
                            isThereADifference=true;
                        }

                        pw.append(column.title + " " + column.dataType + " " + column.size + ": Newly added in "+db2.dataBaseName+"\n");

                    } else {
                        if (column.title.equalsIgnoreCase(c.title) && (!(column.dataType.equals(c.dataType)) || !(column.size == c.size))) {
                            if(!isThereADifference){
                                pw.append("Table Name: " + table.tableName + "\n");
                                pw.append("*Note this table exists in "+db2.dataBaseName+"  and in "+db1.dataBaseName+"\n");
                                isThereADifference=true;
                            }
                            pw.append(column.title + " " + column.dataType + " " + column.size + " : Modefied in "+db2.dataBaseName+"\n");

                        }


                    }
                }
            }

            if(isThereADifference)
            pw.append("------------------------------------------\n");

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
