public class Column {
    String dataType;
    String title;
    int size ;

    boolean visited=false;


    public Column() {
    }

    public Column(String dataType, String title,int size) {
        this.dataType = dataType;
        this.title = title;
        this.size=size;


    }
}
