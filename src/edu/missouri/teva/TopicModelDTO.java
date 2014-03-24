/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.missouri.teva;

import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.CommunityModel.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    
    public CommunityModelBuilder internalModel() {
        return new CommunityModelBuilder(internalModel);
    }

    public static class CommunityModelBuilder {
        private final CommunityModel internal;
        private Date[][] windowBoundaries;

        public CommunityModelBuilder(CommunityModel model) {
            this.internal = model;
        }
        
        public CommunityModelBuilder withWindowsFromFactory(TevaFactory factory) {
            this.windowBoundaries = factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries();
            return this; 
        }

        
        public CommunityModel build() {
            
            if(windowBoundaries == null) {
                TEvA.log("GENERATING WINDOW BOUNDARIES BECAUSE NONE WERE GIVEN");
                windowBoundaries = new Date[internal.getCommunities().size()][2];
                for(int i = 0;i < internal.getCommunities().size(); i++) {
 
                    windowBoundaries[i][0] = new Date(i*1000*60);
                    windowBoundaries[i][1] = new Date(i*1000*60 + 1);
                }
            }
            
            CommunityModel output = new CommunityModel(internal.getParameters(), windowBoundaries, "");
            for(Community community: internal.getCommunities()) {
                output.addCommunity(community);
            }
            
            for(Map.Entry<Integer, Set<Connection>> entry: internal.spawners.entrySet()) {
                int bin = entry.getKey();
                for(Connection connection: entry.getValue()) {
                   output.addConnection(bin, connection.weight, CommunityModel.ConnectionType.SPAWNS, connection.source, connection.target);
                }
               
            }
            
            for(Map.Entry<Integer, Set<Connection>> entry: internal.consumers.entrySet()) {
                int bin = entry.getKey();
                for(Connection connection: entry.getValue()) {
                   output.addConnection(bin, connection.weight, CommunityModel.ConnectionType.CONSUMES, connection.source, connection.target);
                }
               
            }
            
            for(Map.Entry<Integer, Set<Connection>> entry: internal.informs.entrySet()) {
                int bin = entry.getKey();
                for(Connection connection: entry.getValue()) {
                   output.addConnection(bin, connection.weight, CommunityModel.ConnectionType.INFORMS, connection.source, connection.target);
                }
               
            }
            
            
            
            return output;
        }
    }

}
