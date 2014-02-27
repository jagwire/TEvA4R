/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ryan
 */
public class Edge implements edu.mit.cci.sna.Edge {

    private Node[] endpoints = new Node[2];
    private boolean directed = false;
    private float weight = 0.0f;

    private Map<String, Object> properties;

    public static Edge fromString(String csvData) {
        String[] tokens = csvData.split(",");
        if (tokens.length > 2) {

            return new Edge(Node.fromString(tokens[0]), Node.fromString(tokens[1]), Float.valueOf(tokens[2]));
        }
        return new Edge(Node.fromString(tokens[0]), Node.fromString(tokens[1]), 0.0f);
    }

    public Edge(Node source, Node destination, float weight) {
        properties = new HashMap<>();
        endpoints[0] = source;
        endpoints[1] = destination;
        this.weight = weight;
    }

    @Override
    public edu.mit.cci.sna.Node[] getEndpoints() {
        return endpoints;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    ;
    
    @Override
    public float getWeight() {
        return weight;
    }

    ;
    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public void setProperty(String property, Object value) {
        this.properties.put(property, value);
    }

    @Override
    public Object getProperty(String property) {
        return this.properties.get(property);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
