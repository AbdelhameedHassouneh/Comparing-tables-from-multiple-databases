import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Table {
    String tableName;
    int numberOfColumns;
    HashMap<String,String []> data;
    HashMap<String,Column> columns;
    String primaryKey;



    public Table() {
        this.data = new HashMap<>();
        this.columns=new HashMap<>();
    }

    public Table(String tableName, int numberOfColumns) {
        this.tableName = tableName;
        this.numberOfColumns = numberOfColumns;
        this.data = new HashMap<>();
        this.columns=new HashMap<>();
    }
}