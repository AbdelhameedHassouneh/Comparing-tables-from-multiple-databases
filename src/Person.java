public class Person {



     int id ;
     String firstName;
     String lastName;

    public Person(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public boolean equal(Person o){
        if((this.id==o.id)&&(this.firstName.equals(o.firstName))&&(this.lastName.equals(o.lastName))){
            return true;
        }else return false;


    }
}
