/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import edu.mit.cci.teva.engine.CommunityModel;
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

    private CommunityModel internalModel;
    
    
    public TopicModelDTO() {
        TEvA.log("NEW DEFAULT TOPIC MODEL!");
        this.spawns = new ArrayList<String>();
        this.consumes = new ArrayList<String>();
        this.informs = new ArrayList<String>();
        this.windows = new ArrayList<List<List<String>>>();
        this.internalModel = null;
    }

    public TopicModelDTO(List<String> spawns, List<String> consumes, List<String> informs, List<List<List<String>>> windows, CommunityModel internalModel) {
        super();
        TEvA.log("NEW CUSTOM TOPIC MODEL!");
//        this.spawns.addAll(spawns);
//        this.consumes.addAll(consumes);
//        this.informs.addAll(informs);
//        this.windows.addAll(windows);
        
        this.spawns = spawns != null?spawns:new ArrayList<String>();
        this.consumes = consumes != null?consumes:new ArrayList<String>();
        this.informs = informs != null?informs:new ArrayList<String>();
        this.windows = windows != null?windows:new ArrayList<List<List<String>>>();
        this.internalModel = internalModel;
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
    
    public CommunityModel getInternalModel() {
        return internalModel;
    }

}
