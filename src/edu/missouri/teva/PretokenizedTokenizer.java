/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import edu.mit.cci.text.preprocessing.Tokenizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Ryan
 */
public class PretokenizedTokenizer implements Tokenizer<String> {
    private int limit = -1;

    public PretokenizedTokenizer() {
    }

    public List<String> tokenize(String text) {
 
        //remove first entry of matching: 
        text = text.replaceFirst("[\\s,]\\d+[\\s,]", "");
        String[] result = text.split(", ");
        String[] arrayOutput = null;
        if(limit > -1) {
            arrayOutput = Arrays.copyOfRange(result, 0, Math.min(limit, result.length));
        } else {
            arrayOutput = result;
        }
        
        return Arrays.asList(arrayOutput);
    }    
}
