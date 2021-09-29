import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseC {
    String dataBaseName;
    int numberOfTables;
    HashMap<String,Table>tables;

    public DataBaseC() {tables=new HashMap<String,Table>();
    }

    public DataBaseC(String dataBaseName, int numberOfTables) {
        this.dataBaseName = dataBaseName;
        this.numberOfTables = numberOfTables;
        tables=new HashMap<String,Table>();


    }





}