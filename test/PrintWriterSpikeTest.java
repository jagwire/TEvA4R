/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ryan
 */
public class PrintWriterSpikeTest {

    public PrintWriterSpikeTest() {
    }

    @Test
    public void shouldMakeFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("ERROR.txt");
            throw new NullPointerException();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrintWriterSpikeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
            npe.printStackTrace(writer);
        } finally {
            assertTrue(new File("ERROR.txt").exists());
        }
    }

    @Test
    public void shouldAlsoMakeFile() {
        File file = new File("ERROR2.txt");
        PrintStream ps = null;
        try {
            ps = new PrintStream(file);
            throw new NullPointerException();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrintWriterSpikeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
            npe.printStackTrace(ps);
        }
    }

    @Test
    public void shouldWriteToFile() {
        File file = new File("C:\\Development\\TEVA-LOG.txt");
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write("SOMETHING INTERESTING");
            writer.flush();
            writer.println();

            writer.write("SOMETHING INTERESTING");
            writer.flush();
            writer.println();

            writer.write("SOMETHING INTERESTING");
            writer.flush();
            writer.println();

            writer.write("SOMETHING INTERESTING");
            writer.flush();
            writer.println();
            //writer.close() is ESSENTIAL
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrintWriterSpikeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
