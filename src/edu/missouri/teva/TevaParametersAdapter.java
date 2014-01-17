/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.missouri.teva;

import edu.mit.cci.teva.engine.TevaParameters;
import java.io.IOException;

/**
 *
 * @author Ryan
 */
public class TevaParametersAdapter {

    private TevaParameters parameters;

    public TevaParametersAdapter(String csvKeyValuePairs) throws IOException {
        parameters = new TevaParameters();

        //first split the string into the pairs by splitting about the commas.
        String[] keyValuePairs = csvKeyValuePairs.split(",");

        //next, iterate over the pairs
        for (String pair : keyValuePairs) {
            //separate the key from the value, about the =. They key will be
            //at the first index, the value will be at the second index.
            String[] tokens = pair.split("=");

            String key = tokens[0];
            String value = tokens[1];

            //store the mapping in a TevaParameters object
            parameters.setProperty(key, value);
        }
    }

    public TevaParameters getParameters() {
        return parameters;
    }
}
