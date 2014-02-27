/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ryan
 */
public class FileIOSpikeTest {

    public FileIOSpikeTest() {
    }

    @Test
    public void shouldWriteToFile() {
        try {
            Charset charset = Charset.defaultCharset();

            String s = "message2222";
            File f = new File("C:\\Development\\woah.txt");
            BufferedWriter writer = Files.newBufferedWriter(f.toPath(), charset);
            writer.write(s, 0, s.length());
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileIOSpikeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void shouldShowMultipleLines() {
        try {
            Charset charset = Charset.defaultCharset();

            String s1 = "message2222\n";
            File f = new File("C:\\Development\\woah2.txt");
            BufferedWriter writer = Files.newBufferedWriter(f.toPath(), charset);

            writer.write(s1, 0, s1.length());
            writer.flush();
            writer.newLine();

            writer.write(s1, 0, s1.length());
            writer.flush();
            writer.newLine();

            writer.write(s1, 0, s1.length());
            writer.flush();
            writer.newLine();

            writer.write(s1, 0, s1.length());
            writer.flush();
            writer.newLine();
        } catch (IOException ex) {
            Logger.getLogger(FileIOSpikeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
