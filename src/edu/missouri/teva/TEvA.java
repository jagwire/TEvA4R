/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.missouri.teva;

import edu.mit.cci.adapters.csv.CsvBasedConversation;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.teva.DefaultTevaFactory;
import edu.mit.cci.teva.MemoryBasedRunner;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.EvolutionEngine;
import edu.mit.cci.teva.engine.NetworkProvider;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Ryan
 */
public class TEvA {
    private METHOD method;
    private int cliquesize;
    private double min_link;
    private boolean expire_old;
    private MEMBERSHIP_METHOD membership_method;
    private double min_match;

    private Conversation conversation;

    public static enum METHOD {
        EXPONENTIAL,
        LINEAR;
    }

    public static enum MEMBERSHIP_METHOD {

        SIMILARITY;
    }
    private int tuples;
    private int indirection;
    private int window;
    private int delta;

    private NetworkProvider provider;
    private TevaFactory factory;

    public void defaults(String propertiesInCSV) {
        String[] entries = propertiesInCSV.split(",");
        Map<String, String> defaults = new HashMap<>();
        for (String entry : entries) {
            String[] tokens = entry.split("=");
            defaults.put(tokens[0], tokens[1]);
        }

    }

    public Object lexicalize(String stopwordsFilename, String replacementsFilename) {

        return null;
    }

    public String[] networks(String myData, String csvKeyValuePairs) throws IOException, ParseException, CommunityFinderException, JAXBException {
        //create input stream from string
        InputStream stream = IOUtils.toInputStream(myData);

        //create conversation 
        conversation = new CsvBasedConversation("Corpus-Name", stream);

        TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
        TevaParameters parameters = adapter.getParameters();

        factory = new DefaultTevaFactory(parameters, conversation);

        //start network generation piece
        BinningStrategy<Windowable> binningStrategy = factory.getTopicBinningStrategy(factory.getConversationData(), factory.getTopicWindowingFactory());
        CorpusToNetworkGenerator<Windowable> networkGenerator = new CorpusToNetworkGenerator<Windowable>(binningStrategy, factory.getNetworkCalculator());
        final List<Network> result = networkGenerator.analyzeToMemory();

        //while we're at it, let's create a network provider for safe keeping
         provider = new NetworkProvider() {
            public int getNumberWindows() {
                return result.size();
            }

            public Network getNetworkAt(int i) {
                return result.get(i);
            }
        };
        //output result somehow.
        String csvEdgeListHeader = "node 1,node 2,weight\n";
        List<String> output = new ArrayList<>();
        output.add(csvEdgeListHeader);

        for (int i = 0; i < provider.getNumberWindows(); i++) {
            List<String> csvRows = new ArrayList<>();
            Network network = provider.getNetworkAt(i);
            //generate edge list in the form of a long csv string
            for (Edge edge : network.getEdges()) {

                //generate a csv row ala: node,node,weight
                String first = edge.getEndpoints()[0].getId();
                String second = edge.getEndpoints()[1].getId();
                String weight = String.valueOf(edge.getWeight());
                csvRows.add(first + "," + second + "," + weight);
            }

            //take all of the items in csvRows and combine them with newlines
            //to create one big string of the form:
            //node,node,weight\nnode,node,weight\nnode,node,weight
            String returnElement = StringUtils.join(csvRows.toArray(new String[]{}), "\n");

            output.add(returnElement);
        }
        
        return output.toArray(new String[]{});
    }

    private EvolutionEngine EvolutionEngine(CommunityModel model, TevaParameters parameters, NetworkProvider provider, TevaFactory factory) {
        return new EvolutionEngine(model, parameters, provider, factory.getFinder(), factory.getStepper(model), factory.getMerger());
    }

    private CommunityModel CommunityModel(TevaParameters parameters, TevaFactory factory, Conversation conversation) {
        return new CommunityModel(parameters, factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries(), conversation.getName());
    }

    public String[] evolve(String networks, String csvKeyValuePairs) {
        try {
            if (conversation == null || factory == null) {
                throw new RuntimeException("TRIED TO RUN EVOLVE WITHOUT RUNNING NETWORKS FIRST!");
            }

            TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
            TevaParameters parameters = adapter.getParameters();

            //create community model
            CommunityModel model = CommunityModel(parameters, factory, conversation);

            //create evolution engine
            EvolutionEngine engine = EvolutionEngine(model, parameters, provider, factory);

            //run the algorithm            
            engine.process();

            return new String[]{};

        } catch (CommunityFinderException ex) {
            Logger.getLogger(TEvA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TEvA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new String[]{};

    }
    /*
     public Assignments membership(CSVData myData, TopicModel model) {
        return null;
    }

    public static void topic_graph(TopicModel model, String graphmlFile) {
    }

    public static void message_graph(Object myData, TopicModel model, Assignments assignments, String graphmlFilename) {
    }
     */
}
