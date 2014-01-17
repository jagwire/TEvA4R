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
        Conversation conversation = new CsvBasedConversation("Corpus-Name", stream);

        TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
        TevaParameters parameters = adapter.getParameters();

        TevaFactory factory = new DefaultTevaFactory(parameters, conversation);

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
    /*
     public TopicModel evolve(List<EdgeList> networks) {
        return null;

    }

    public Assignments membership(CSVData myData, TopicModel model) {
        return null;
    }

    public static void topic_graph(TopicModel model, String graphmlFile) {
    }

    public static void message_graph(Object myData, TopicModel model, Assignments assignments, String graphmlFilename) {
    }
     */
}
