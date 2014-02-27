/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.missouri.teva.Network;
import java.util.Collections;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ryan
 */
public class SyntheticNetworkTest {

    public SyntheticNetworkTest() {
    }

    @Test
    public void edgesShouldBeNotNull() {
        Network n = new Network();

        assertNotNull(n);
        assertNotNull(n.getEdges());
        n = null;

        n = new Network(null, null);
        assertNotNull(n);
        assertNotNull(n.getEdges());

       n = new Network(Collections.EMPTY_SET, Collections.EMPTY_LIST);
        assertNotNull(n);
        assertNotNull(n.getEdges());
    }

}
