/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import edu.mit.cci.text.preprocessing.Tokenizer;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ryan
 */
public class PretokenizedTokenizer implements Tokenizer<String> {
    private int limit = -1;

    public PretokenizedTokenizer() {
    }

    public List<String> tokenize(String text) {
        //def result= (text - ~/[[\s,]\d+[\s,]/).tokenize(", ");
        String[] tokens = text.split("[\\s,]\\d+[\\s,]");
        return Arrays.asList(tokens);
    }
    
}
