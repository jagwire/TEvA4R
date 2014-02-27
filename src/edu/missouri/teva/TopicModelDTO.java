/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ryan
 */
public class TopicModelDTO {

    private List<String> spawns;
    private List<String> consumes;
    private List<String> informs;

    private List<List<List<String>>> windows;

    public TopicModelDTO() {
        this.spawns = new ArrayList<>();
        this.consumes = new ArrayList<>();
        this.informs = new ArrayList<>();
        this.windows = new ArrayList<>();
    }

    public TopicModelDTO(List<String> spawns, List<String> consumes, List<String> informs, List<List<List<String>>> windows) {
        super();

        this.spawns.addAll(spawns);
        this.consumes.addAll(consumes);
        this.informs.addAll(informs);
        this.windows.addAll(windows);
        // System.out.println("DTO CREATED!");
    }

    public String[] getSpawns() {
        return spawns.toArray(new String[]{});
    }

    public String[] getConsumes() {
        return consumes.toArray(new String[]{});
    }

    public String[] getInforms() {
        return informs.toArray(new String[]{});
    }

    public List<List<List<String>>> getWindows() {
        return windows;
    }

}
