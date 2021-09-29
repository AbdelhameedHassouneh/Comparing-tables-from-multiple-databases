import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ComparisionClass {
    public static void main(String[] args) {

        try {

            Class.forName("org.h2.Driver");
            Scanner input=new Scanner(System.in);
            System.out.println("Please enter the name of the database1");
            String db1NameToConnect=input.next();
            DataBaseC database1=grapData(db1NameToConnect);
            System.out.println("*************************************************");
            System.out.println("Please enter the name of the database2");
            String db2NameToConnect=input.next();
            DataBaseC database2=grapData(db2NameToConnect);

            compareDatabases(database1,database2);



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }



    public static DataBaseC grapData(String db1NameToConnect){
        DataBaseC database=new DataBaseC();
        Connection conn=createConnection("jdbc:h2:~/"+db1NameToConnect,"root","toor");
        try {

            DatabaseMetaData dbMeta=conn.getMetaData();
            ResultSet resultSet = dbMeta.getTables(null, null, null, new String[]{"TABLE"});


            int size=0;

            while(resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");

                Table table=new Table();
                table.tableName=tableName;
                ResultSet columns = dbMeta.getColumns(null,null, tableName, null);
                ResultSet primaryKeys = dbMeta.getPrimaryKeys(null, null, tableName);
                primaryKeys.next();

                table.primaryKey=primaryKeys.getString("COLUMN_NAME");


                while(columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");


                    String datatype = columns.getString("TYPE_NAME");
                    int cSize =columns.getInt("COLUMN_SIZE");



                    Column c=new Column();
                    c.title=columnName;
                    c.dataType=datatype;
                    c.size=cSize;

                    table.columns.put(c.title,c);
                }
                size++;
                database.tables.put(table.tableName,table);
            }
            database.dataBaseName=db1NameToConnect;
            database.numberOfTables=size;

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


    public static void compareDatabases(DataBaseC db1,DataBaseC db2){
        Scanner input=new Scanner(System.in);

        System.out.println("DataBase 1 Name :"+db1.dataBaseName);
        System.out.println("Database 1 #Of Tables: "+db1.numberOfTables);

        System.out.println("DataBase 2 Name :"+db2.dataBaseName);
        System.out.println("Database 2 #Of Tables: "+db2.numberOfTables);
        System.out.println("**************Going through tables:*************");
        for (Map.Entry<String, Table> entry : db2.tables.entrySet()) {

            Table table = (Table) entry.getValue();
            System.out.println("from database 2");
            System.out.println(table.tableName);
            Table tableindb1=db1.tables.get(table.tableName);

            if(tableindb1==null){

                System.out.println("Note this table doesn't exist in db1");
                System.out.println("Primary key of this table in db2 is: "+table.primaryKey);
                for (Column column : table.columns.values()) {
                    System.out.println(column.title+" "+ column.dataType+" "+column.size);

                }
            }else {
                System.out.println("Note this table exists in db1");
                System.out.println("Primary key of this table in db2 is: "+table.primaryKey+" and in db1 is: "+tableindb1.primaryKey);


                for (Column column : table.columns.values()) {
                    Column c=tableindb1.columns.get(column.title);
                    if(c==null){

                        System.out.println(column.title+" "+ column.dataType+" "+column.size+" doesn't exist in db1");



                    }else{
                        if(column.title.equals(c.title)&&column.dataType.equals(c.dataType)&&column.size==c.size){

                            System.out.println(column.title+" "+ column.dataType+" "+column.size+": exists and the same");

                        }else if(column.title.equals(c.title)&&(!(column.dataType.equals(c.dataType))||!(column.size==c.size))){
                            System.out.println(column.title+" "+ column.dataType+" "+column.size+"---"+c.title+" "+c.dataType+" "+c.size+" modified in db2");

                        }
                    }
                }
            }
            input.next();
        }
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
}
