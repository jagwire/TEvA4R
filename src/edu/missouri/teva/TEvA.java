package edu.missouri.teva;

import edu.mit.cci.adapters.csv.CsvBasedConversation;
import edu.mit.cci.sna.Edge;
import edu.mit.cci.sna.Network;
import edu.mit.cci.sna.jung.DirectedJungNetwork;
import edu.mit.cci.sna.jung.JungUtils;
import edu.mit.cci.teva.DefaultTevaFactory;
import edu.mit.cci.teva.MemoryBasedRunner;
import edu.mit.cci.teva.TevaFactory;
import edu.mit.cci.teva.cpm.cfinder.CFinderCommunityFinder;
import edu.mit.cci.teva.cpm.cos.CosCommunityFinder;
import edu.mit.cci.teva.engine.BasicStepStrategy;
import edu.mit.cci.teva.engine.Community;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.CommunityModel;
import edu.mit.cci.teva.engine.CommunityModel.Connection;
import edu.mit.cci.teva.engine.ConversationChunk;
import edu.mit.cci.teva.engine.EvolutionEngine;
import edu.mit.cci.teva.engine.FastMergeStrategy;
import edu.mit.cci.teva.engine.NetworkProvider;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.teva.engine.TopicMembershipEngine;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.util.TevaUtils;
import edu.mit.cci.text.windowing.BinningStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.wordij.CorpusToNetworkGenerator;
import edu.mit.cci.util.U;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ryan
 */
public class TEvA {

//    private Conversation conversation;
    private NetworkProvider provider;
    private TevaFactory factory;

    private static final String LOG_NAME = "/Users/Ryan/Development/TEVA-LOG.txt";
    private static PrintWriter printWriter;

    static {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    printWriter.close();
                }
            }));
            printWriter = new PrintWriter(new File(LOG_NAME));
            printWriter.write("=== TEVA LOG ===");
            printWriter.flush();
            printWriter.println();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TEvA.class.getName()).log(Level.FATAL, null, ex);
        }
    }

    public TEvA() {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        for (Logger logger : loggers) {
            logger.setLevel(Level.OFF);

        }
        PropertyConfigurator.configure("/Users/Ryan/Development/RTEvA/log4j.properties");
        try {
            System.setOut(new PrintStream("MOCK_OUT.txt"));
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(TEvA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public void defaults(String propertiesInCSV) {
        String[] entries = propertiesInCSV.split(",");
        Map<String, String> defaults = new HashMap<String, String>();
        for (String entry : entries) {
            String[] tokens = entry.split("=");
            defaults.put(tokens[0], tokens[1]);
        }

    }

    public Object lexicalize(String stopwordsFilename, String replacementsFilename) {

        return null;
    }

    private Conversation getConversationFromCSV(String csvData) {
        Conversation conversation = null;
        try {
            InputStream stream = IOUtils.toInputStream(csvData);
            conversation = new CsvBasedConversation("Corpus-Name", stream);
        } catch(Exception e) { 
            e.printStackTrace();
        } finally {
            return conversation;
        }
    }
    
    public String[] networks(String myData, String csvKeyValuePairs) throws IOException, ParseException, CommunityFinderException, JAXBException {
        //create input stream from string
        InputStream stream = IOUtils.toInputStream(myData);

        //create conversation 
        Conversation conversation = new CsvBasedConversation("Corpus-Name", stream);

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
        List<String> output = new ArrayList<String>();
        output.add(csvEdgeListHeader);

        for (int i = 0; i < provider.getNumberWindows(); i++) {
            List<String> csvRows = new ArrayList<String>();
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

    private EvolutionEngine EvolutionEngine(CommunityModel model, TevaParameters parameters, NetworkProvider provider) {
        return new EvolutionEngine(model,
                parameters,
                provider,
                //new CFinderCommunityFinder(parameters.getOverwriteNetworks(), parameters.getOverwriteAnalyses(), parameters),
                new CustomCosCommunityFinder(parameters),
                new BasicStepStrategy(model, parameters),
                new FastMergeStrategy(parameters.getFixedCliqueSize()));
    }

    private CommunityModel CommunityModel(TevaParameters parameters, TevaFactory factory) {
        if(factory == null) { 
            return new CommunityModel(parameters, null, "");
            
        }
        return new CommunityModel(parameters, factory.getTopicWindowingFactory().getStrategy().getWindowBoundaries(), "");
    }

    public TopicModelDTO evolve(String[] networksData, String csvKeyValuePairs) {
        try {
            final Network[] networks = edu.missouri.teva.Network.networksFromArray(networksData);
            
            NetworkProvider _provider = new NetworkProvider() {
                @Override
                public int getNumberWindows() {
                    return networks.length;
                }
                
                @Override
                public Network getNetworkAt(int i) {
                    return networks[i];
                }
            };
            
            log("PROVIDER CREATED, number of windows: " + _provider.getNumberWindows());

            TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
            TevaParameters parameters = adapter.getParameters();
            
            /**
             * Create the community model.
             *
             * We pass null here because the evolution engine doesn't actually need
             * the Date[][] array. We make note of this here because it will be more
             * important in the membership function.
             */
            CommunityModel model = CommunityModel(parameters, null);
            
            //create evolution engine
            EvolutionEngine engine = EvolutionEngine(model, parameters, _provider);
            log("ENGINE CREATED!");
            
            //run the algorithm
            engine.process();
            log("COMMUNITY PROCESSED!");
            //windows list will get translated into CSV and assigned
            //to topic field of TopicModel
            List<List<List<String>>> windows = new ArrayList<List<List<String>>>();
            for (Community window : model.getCommunities()) {
                List<List<String>> topics = new ArrayList<List<String>>();
                for (CommunityFrame topic : window.history.values()) {
                    List<String> edges = new ArrayList<String>();
                    for (Edge edge : topic.getEdges()) {
                        String edgeAsCSV = toCSV(edge);
                        edges.add(edgeAsCSV);
                    }
                    topics.add(edges);
                }
                windows.add(topics);
            }
            
            List<String> spawns = new ArrayList<String>();
            for (Set<Connection> connections : model.spawners.values()) {
                
                for (Connection connection : connections) {
                    spawns.add(toCSV(connection));
                }
            }
            
            if(spawns == null) {
                log("SPAWNS IS NULL!");
            }
            
            List<String> consumes = new ArrayList<String>();
            for (Set<Connection> connections : model.consumers.values()) {
                for (Connection connection : connections) {
                    consumes.add(toCSV(connection));
                }
            }
            
            if(consumes == null) {
                log("CONSUMES IS NULL!");
            }
            
            List<String> informs = new ArrayList<String>();
            for (Set<Connection> connections : model.informs.values()) {
                for (Connection connection : connections) {
                    informs.add(toCSV(connection));
                }
            }
            
            if(informs == null) {
                log("INFORMS IS NULL");
            }
            
            log("EVOLVE FINISHED!");
            //result should now be in CommunityModel
            return new TopicModelDTO(spawns, consumes, informs, windows, model);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return new TopicModelDTO();

    }

    public TopicModelDTO evolve(String networksData, String csvKeyValuePairs) {

        log("INSIDE EVOLVE()");
        try {            //generate NetworkProvider from csv string of edge lists.
            final Network[] networks = edu.missouri.teva.Network.networksFromCSV(networksData);
            NetworkProvider _provider = new NetworkProvider() {
                @Override
                public int getNumberWindows() {
                    return networks.length;
                }

                @Override
                public Network getNetworkAt(int i) {
                    return networks[i];
                }
            };

            log("PROVIDER CREATED, number of windows: " + _provider.getNumberWindows());

            log("INFO FOR FIRST EDGE ENTRY IN FIRST NETWORK: ");
            Network first = _provider.getNetworkAt(0);
            Edge[] first_edges = first.getEdges().toArray(new Edge[]{});
            edu.mit.cci.sna.Node one = first_edges[0].getEndpoints()[0];
            edu.mit.cci.sna.Node two = first_edges[0].getEndpoints()[1];
            log("EDGE: " + one.getLabel() + "," + two.getLabel() + "," + first_edges[0].getWeight());

            TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
            TevaParameters parameters = adapter.getParameters();

            /**
             * Create the community model.
             *
             * We pass null here because the evolution engine doesn't actually
             * need the Date[][] array. We make note of this here because it
             * will be more important in the membership function.
             */
            CommunityModel model = CommunityModel(parameters, null);

            //create evolution engine
            EvolutionEngine engine = EvolutionEngine(model, parameters, _provider);
            log("ENGINE CREATED!");

            //run the algorithm            
            engine.process();
            log("COMMUNITY PROCESSED!");
            //windows list will get translated into CSV and assigned
            //to topic field of TopicModel
            List<List<List<String>>> windows = new ArrayList<List<List<String>>>();
            for (Community window : model.getCommunities()) {
                List<List<String>> topics = new ArrayList<List<String>>();
                for (CommunityFrame topic : window.history.values()) {
                    List<String> edges = new ArrayList<String>();
                    for (Edge edge : topic.getEdges()) {
                        String edgeAsCSV = toCSV(edge);
                        edges.add(edgeAsCSV);
                    }
                    topics.add(edges);
                }
                windows.add(topics);
            }

            List<String> spawns = new ArrayList<String>();
            for (Set<Connection> connections : model.spawners.values()) {

                for (Connection connection : connections) {
                    spawns.add(toCSV(connection));
                }
            }

            List<String> consumes = new ArrayList<String>();
            for (Set<Connection> connections : model.consumers.values()) {
                for (Connection connection : connections) {
                    consumes.add(toCSV(connection));
                }
            }

            List<String> informs = new ArrayList<String>();
            for (Set<Connection> connections : model.informs.values()) {
                for (Connection connection : connections) {
                    informs.add(toCSV(connection));
                }
            }

            log("EVOLVE FINISHED!");
            //result should now be in CommunityModel
            return new TopicModelDTO(spawns, consumes, informs, windows, model);

        } catch (Exception ex) {
            ex.printStackTrace();
            log("EXCEPTION: " + ex.getLocalizedMessage());
            ex.printStackTrace(printWriter);
        }

        return new TopicModelDTO();
    }
    
    public String[] membership(String csvData, String csvKeyValuePairs, TopicModelDTO dto) {
        try {
            Conversation conversation = getConversationFromCSV(csvData);
            
            //new teva parameters
            TevaParametersAdapter adapter = new TevaParametersAdapter(csvKeyValuePairs);
            TevaParameters parameters = adapter.getParameters();
            
            //new factory based on RTEvAApproach
            
                                  
            TevaFactory approach = new RTEvAApproach(parameters, conversation);       
            CommunityModel model =  dto.internalModel().withWindowsFromFactory(approach).build();
            TopicMembershipEngine engine = approach.getMembershipEngine(model, conversation);
            
            //run membership
            engine.process();
            
            List<String> rows = new ArrayList<String>();
            log("PROCESSING: "+model.getCommunities().size()+" COMMUNITIES");
            for(Community community: model.getCommunities()) {
                String topicId = community.getId();
                log("PROCESSING ASSIGNMENTS: "+community.getAssignments().size());
                for(ConversationChunk chunk: community.getAssignments()) {
                    log("PROCESSING MESSAGES: "+chunk.messages.size());
                    for(Windowable post: chunk.messages) {
                       
                        String row = (topicId + ","+post.getId()+"\n");
                        log("ADDING ROW: "+row);
                        rows.add(row);
                    }
                }
            }
            
            return rows.toArray(new String[] { });
            
        } catch (Exception ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(TEvA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            log(ex.getLocalizedMessage());
        }
        return null;
    }
 
    public void topic_graph(TopicModelDTO dto, String filename) {
        
        try {
            CommunityModel synthetic =dto.internalModel().build();
            DirectedJungNetwork network = TevaUtils.createCommunityGraph(synthetic, true, true, true);
            TevaUtils.addDrainageScoresForCommunityGraph(network);
            JungUtils.writeGraphML(network, U.mapify("Size", 0, "Messages",0,"Window", 0, "CommunityId", "", "Centrality", 0), filename + ".graphml");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            log(ex.getLocalizedMessage());
        }
        
    }
    
    private String toCSV(Edge edge) {
        edu.mit.cci.sna.Node[] ends = edge.getEndpoints();
        float weight = edge.getWeight();
        return StringUtils.join(new Object[]{ends[0], ends[1], weight}, ",");
    }

    private String toCSV(Connection connection) {
        String source = connection.source.id;
        String target = connection.target.id;
        float weight = connection.weight;

        return StringUtils.join(new Object[]{source, target, weight}, ",");

    }

    public synchronized static void log(String message) {

        if (!new File(LOG_NAME).exists()) {
            try {
                printWriter = new PrintWriter(new File(LOG_NAME));
                printWriter.write("=== TEVA LOG ===");
                printWriter.flush();
                printWriter.println();
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(TEvA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        printWriter.append(message + "\n");
        printWriter.flush();
        printWriter.println();

    }
}
