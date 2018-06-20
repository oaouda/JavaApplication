/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.*;
/**
 *
 * @author sam
 */
public class PropertiesCopier {

    /**
     * Copies all properties from sources to destination, does not copy null
     * values and any nested objects will attempted to be either cloned or
     * copied into the existing object. This is recursive. Should not cause any
     * infinite recursion.
     *
     * @param dest object to copy props into (will mutate)
     * @param sources
     * @param <T> dest
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> T copyProperties(T dest, Object... sources) throws IllegalAccessException, InvocationTargetException {
        // to keep from any chance infinite recursion lets limit each object to 1 instance at a time in the stack
        final List<Object> lookingAt = new ArrayList<>();

        BeanUtilsBean recursiveBeanUtils = new BeanUtilsBean() {

            /**
             * Check if the class name is an internal one
             *
             * @param name
             * @return
             */
            private boolean isInternal(String name) {
                return name.startsWith("java.") || name.startsWith("javax.")
                        || name.startsWith("com.sun.") || name.startsWith("javax.")
                        || name.startsWith("oracle.");
            }

            /**
             * Override to ensure that we dont end up in infinite recursion
             *
             * @param dest
             * @param orig
             * @throws IllegalAccessException
             * @throws InvocationTargetException
             */
            @Override
            public void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
                try {
                    // if we have an object in our list, that means we hit some sort of recursion, stop here.
                    if (lookingAt.stream().anyMatch(o -> o == dest)) {
                        return; // recursion detected
                    }
                    lookingAt.add(dest);
                    super.copyProperties(dest, orig);
                } finally {
                    lookingAt.remove(dest);
                }
            }

            @Override
            public void copyProperty(Object dest, String name, Object value)
                    throws IllegalAccessException, InvocationTargetException {
                // dont copy over null values
                if (value != null) {
                    // attempt to check if the value is a pojo we can clone using nested calls
                    if (!value.getClass().isPrimitive() && !value.getClass().isSynthetic() && !isInternal(value.getClass().getName())) {
                        try {
                            Object prop = super.getPropertyUtils().getProperty(dest, name);
                            // get current value, if its null then clone the value and set that to the value
                            if (prop == null) {
                                super.setProperty(dest, name, super.cloneBean(value));
                            } else {
                                // get the destination value and then recursively call
                                copyProperties(prop, value);
                            }
                        } catch (NoSuchMethodException e) {
                            return;
                        } catch (InstantiationException e) {
                            throw new RuntimeException("Nested property could not be cloned.", e);
                        }
                    } else {
                        super.copyProperty(dest, name, value);
                    }
                }
            }
        };

        for (Object source : sources) {
            recursiveBeanUtils.copyProperties(dest, source);
        }

        return dest;
    }
    
    
    public static void main(String ...args) throws IllegalAccessException, InvocationTargetException {
            Address address = new Address("vista bella", "simi valley", "ca");
        Person p1 = new Person("John", null, address);
        Person p2 = new Person(null, "Snow", null);
        Person merged = (Person) copyProperties(p1, p2);
        System.out.println("merged person:" + merged);
    }

}
