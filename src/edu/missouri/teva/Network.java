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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Ryan
 */
public class Network implements edu.mit.cci.sna.Network {

    private Set<edu.mit.cci.sna.Node> nodes;
    private List<edu.mit.cci.sna.Edge> edges;

    public Network() {
        nodes = new HashSet<>();
        edges = new ArrayList<>();
    }

    public Network(Set<Node> nodes, List<Edge> edges) {
        super();
        nodes.addAll(nodes);
        edges.addAll(edges);
    }

    public boolean isDirected() {
        return false;
    }

    public static Network[] networksFromCSV(String csvData) {

        List<Network> _networks = new ArrayList<Network>();

        String[] networks = csvData.split("=");
        for (String network : networks) {
            Network n = _fromCSV(network);
            _networks.add(n);
        }


        return _networks.toArray(new Network[]{});
    }

    private static Network _fromCSV(String network) throws NumberFormatException {
        Set<Node> nodes = new HashSet<Node>();
        List<Edge> edges = new ArrayList<Edge>();
        String[] csvRows = network.split("\n");
        for (int i = 0; i < csvRows.length; i++) {
            System.out.println("PRCESSING ROW: " + csvRows[i]);
            String[] columns = csvRows[i].split(",");

            Node node1 = Node.fromString(columns[0]);
            Node node2 = Node.fromString(columns[1]);

            nodes.add(node1);
            nodes.add(node2);
            float weight = 0;
            if (columns.length == 3) {
                if (columns[2].equals("weight")) {
                    continue;
                }
                weight = Float.valueOf(columns[2]);
            }

            edges.add(new Edge(node1, node2, weight));
        }
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
