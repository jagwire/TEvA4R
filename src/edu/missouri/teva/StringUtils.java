/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.missouri.teva;

import java.util.List;

/**
 *
 * @author Ryan
 */
public class StringUtils {

    public static String listToCSV(List<String> values) {
        return join(values.toArray(null), ",");
//        StringBuilder builder = new StringBuilder();
//        builder.append(values.get(0));
//        for (int i = 1; i < values.size(); i++) {
//            builder.append(",").append(values.get(i));
//        }
//
//        return builder.toString();
    }

    public static String join(Object[] elements, String joinElement) {
        StringBuilder builder = new StringBuilder();
        builder.append(elements[0].toString());
        for (int i = 1; i < elements.length; i++) {
            builder.append(joinElement).append(elements[i].toString());

        }
        return builder.toString();
    }
}
