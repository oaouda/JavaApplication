/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author sam
 */
public class Person {
    private final String firstName;
    private final String lastName;
    private final Address address;

    public Person(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person{" + "firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + '}';
    }

    
    
    public static Object mergePersons(Object obj1, Object obj2) throws Exception {
        Field[] allFields = obj1.getClass().getDeclaredFields();
        for (Field field : allFields) {

            if (!field.isAccessible() && Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            if (field.get(obj1) == null && field.get(obj2) != null) {
                field.set(obj1, field.get(obj2));
            }
        }
        return obj1;
    }
    
    public static void main(String [] args) throws Exception {
        Address address = new Address("vista bella", "simi valley", "ca");
        Person p1 = new Person("John", null, address);
        Person p2 = new Person(null, "Snow", null);
        Person merged = (Person) mergePersons(p1, p2);
        System.out.println("merged person:" + merged);
    }
    
    
}
