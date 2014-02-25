/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ryan
 */
public class Node implements edu.mit.cci.sna.Node {
    private String label;
    private String id;
    private Map<String, Object> properties;

    public Node() {
        properties = new HashMap<String, Object>();
    }

    public static Node fromString(String data) {
        Node synthetic = new Node();
        synthetic.setLabel(data);
        synthetic.setId(data);
        return null;
    }

    public void setLabel(String data) {
        this.label = label;
    }

    public void setId(String data) {
        this.id = id;
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
