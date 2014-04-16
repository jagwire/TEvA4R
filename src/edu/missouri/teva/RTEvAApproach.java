package edu.missouri.teva;

import edu.mit.cci.teva.DefaultTevaFactory;
import edu.mit.cci.teva.cpm.cos.CosCommunityFinder;
import edu.mit.cci.teva.engine.CommunityFinder;
import edu.mit.cci.teva.engine.CommunityMembershipStrategy;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.teva.model.Conversation;
import edu.mit.cci.teva.model.DiscussionThread;
import edu.mit.cci.teva.model.Post;
import edu.mit.cci.teva.util.ExhaustiveAssignment;
import edu.mit.cci.teva.util.WindowablePostAdapter;
import edu.mit.cci.text.preprocessing.DictionaryMunger;
import edu.mit.cci.text.preprocessing.Munger;
import edu.mit.cci.text.preprocessing.StopwordMunger;
import edu.mit.cci.text.preprocessing.Tokenizer;
import edu.mit.cci.text.windowing.WindowStrategy;
import edu.mit.cci.text.windowing.Windowable;
import edu.mit.cci.text.windowing.WindowingUtils;
import edu.mit.cci.util.U;
import edu.mit.cci.util.U.Adapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Ryan
 */
public class RTEvAApproach extends DefaultTevaFactory {

    protected Date[][] windows = null;

    public RTEvAApproach(TevaParameters params, Conversation conversation) {
        super(params, conversation);
    }

    public Tokenizer<String> getTokenizer() {
        return new PretokenizedTokenizer();
    }

    
    @Override
    public List<List<Windowable>> getConversationData() throws IOException {
        Tokenizer<String> tokenizer = getTokenizer();
        List<List<Windowable>> data = new ArrayList<>();
        for(DiscussionThread thread: conversation.getAllThreads()) {
            List<Windowable> threadData = new ArrayList<>();
            for(Post p: thread.getPosts()) {
                threadData.add(new WindowablePostAdapter(p, tokenizer));
            }
            data.add(threadData);
                    
        }
        return data;
    }
    
    @Override
    public WindowStrategy.Factory<Windowable> getTopicWindowingFactory() {
        return getTrafficSlicedFactory();
    }

    @Override
    public Munger[] getMungers() throws IOException {
        List<Munger> mungers = new ArrayList<>();
        String dictionary = params.getReplacementDictionary();
        if (dictionary != null && !dictionary.isEmpty()) {
            if (dictionary.startsWith("/") || dictionary.startsWith(".")) {
                mungers.add(DictionaryMunger.read(IOUtils.toInputStream(dictionary)));
                //log "Loaded replacement list from file"

            } else {
                mungers.add(DictionaryMunger.read(getClass().getResourceAsStream("/" + dictionary)));
                //log "replacement list from resource"
            }
        }
        String stopwords = params.getStopwordList();
        if (stopwords != null && !stopwords.isEmpty()) {
            if (stopwords.startsWith("/") || stopwords.startsWith(".")) {
                mungers.add(StopwordMunger.readAndAdd((IOUtils.toInputStream(stopwords))));
                //log "loaded stopword list from file:"
            } else {
                mungers.add(StopwordMunger.readAndAdd(getClass().getResourceAsStream("/" + stopwords)));
            }
        }

        return mungers.toArray(new Munger[mungers.size()]);
    }

    @Override
    public CommunityMembershipStrategy getMembershipMatchingStrategy() {
        return new ExhaustiveAssignment();
    }

    @Override
    public CommunityFinder getFinder() {
        return new CosCommunityFinder(params);
    }

    public WindowStrategy.Factory<Windowable> getTrafficSlicedFactory() {
        return new WindowStrategy.Factory<Windowable>() {

            @Override
            public WindowStrategy<Windowable> getStrategy() {
                if (windows == null) {
                    windows = WindowingUtils.analyzeMultipleThreadsBySize(conversation, getTokenizer(), (int) params.getWindowSize(), (int) params.getWindowDelta());
                }
                return new LookBehindWindowStrategy((int) params.getWindowSize(), true, windows);
            }
        };
    }



}
