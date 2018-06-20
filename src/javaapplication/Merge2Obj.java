package javaapplication;

// Must have the default constructor
import java.lang.reflect.Field;
public class Merge2Obj {

    private String name;
    private String lasName;

    // Must have default constructor, otherwise, it won't work
    public Merge2Obj() {
        super();
    }

    public Merge2Obj(String name, String lasName) {
        super();
        this.name = name;
        this.lasName = lasName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLasName() {
        return lasName;
    }

    public void setLasName(String lasName) {
        this.lasName = lasName;
    }

    public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }

     public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Merge2Obj obj1 = new Merge2Obj("ABC", null);
        Merge2Obj obj2 = new Merge2Obj("PQR", "LMN");

        Merge2Obj obj3 = mergeObjects(obj1, obj2);

        System.out.println(obj3.name);
        System.out.println(obj3.lasName);
    }

}