/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan
 */
public class Network implements edu.mit.cci.sna.Network {

    public static Network[] networksFromArray(String[] networksData) {
        
        List<Network> _networks = new ArrayList<Network>();
        for(String csvString: networksData) {
            Network _network = new Network();
            String[] csvRows = csvString.split("\n");
            for(String csvRow: csvRows) {
                //TEvA.log(csvRow);
               String[] tokens = csvRow.split(",");
               if(tokens == null) {
                   TEvA.log("TOKENS ARE NULL!");
               }
               
               if(tokens[0] == null) {
                   TEvA.log("TOKENS 1 IS NULL!");
               }
               
               if(tokens[1] == null) {
                   TEvA.log("TOKENS 2 IS NULL!");
               }
               
               Node src = Node.fromString(tokens[0]);
               Node dst = Node.fromString(tokens[1]);
               _network.add(src);
               _network.add(dst);
               
               Edge e = new Edge(src, dst, Float.valueOf(tokens[2]).floatValue());
               _network.add(e);
            }
            _networks.add(_network);
        }
        return _networks.toArray(new Network[] { });
    }

    private Set<edu.mit.cci.sna.Node> nodes;
    private List<edu.mit.cci.sna.Edge> edges;

    public Network() {
        this.nodes = new HashSet<edu.mit.cci.sna.Node>();
        this.edges = new ArrayList<edu.mit.cci.sna.Edge>();
    }

    public Network(Set<edu.mit.cci.sna.Node> nodes, List<edu.mit.cci.sna.Edge> edges) {
        super();
        this.nodes = new HashSet<edu.mit.cci.sna.Node>();
        this.edges = new ArrayList<edu.mit.cci.sna.Edge>();
        if (nodes != null) {
            this.nodes.addAll(nodes);
        }

        if (edges != null) {
            this.edges.addAll(edges);
        }
    }

    public boolean isDirected() {
        return false;
    }

    public static Network[] networksFromCSV(String csvData) {

        List<Network> _networks = new ArrayList<Network>();

        String[] networks = csvData.split("=");
        for (String network : networks) {
            TEvA.log("PROCESSING NETWORK!");
            Network n = _fromCSV(network);
            if (n == null) {
                continue;
            }
            _networks.add(n);
        }

        return _networks.toArray(new Network[]{});
    }

    private static Network _fromCSV(String network) throws NumberFormatException {
        Set<edu.mit.cci.sna.Node> nodes = new HashSet<edu.mit.cci.sna.Node>();
        List<edu.mit.cci.sna.Edge> edges = new ArrayList<edu.mit.cci.sna.Edge>();
        // System.out.println("GENERATING NETWORK FROM CSV!");
        String[] csvRows = network.split("\n");
        //System.out.println("SPLIT CSV INTO " + csvRows.length + " ROWS!");

        try {
            for (int i = 0; i < csvRows.length; i++) {
                
                String[] columns = csvRows[i].split(",");

                Node node1 = Node.fromString(columns[0]);
                Node node2 = Node.fromString(columns[1]);

                nodes.add(node1);
                nodes.add(node2);
                float weight = 0;
                if (columns.length == 3) {
                    if (columns[2].equals("weight")) {
                        return null;
                    }

                    weight = Float.valueOf(columns[2]);
                }

                edges.add(new Edge(node1, node2, weight));
            }
        } catch (Exception e) {
            TEvA.log("CREATING DUMB NETWORK!");
            TEvA.log(e.getLocalizedMessage());
            return new Network();
        }

        TEvA.log("CREATING FULL NETWORK WITH " + edges.size() + " EDGES!");
        Network n = new Network(nodes, edges);
        return n;
    }

    @Override
    public Collection<edu.mit.cci.sna.Node> getNodes() {
        return nodes;
    }

    @Override
    public Collection<edu.mit.cci.sna.Edge> getEdges() {
        return edges;
    }

    @Override
    public void remove(edu.mit.cci.sna.Edge edge) {
        edges.remove(edge);
    }

    @Override
    public void remove(edu.mit.cci.sna.Node node) {
        nodes.remove(node);
    }

    @Override
    public void add(edu.mit.cci.sna.Node node) {
        nodes.add(node);
    }

    @Override
    public void add(edu.mit.cci.sna.Edge edge) {
        edges.add(edge);
    }
}
