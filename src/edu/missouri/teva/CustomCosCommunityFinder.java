/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.missouri.teva;

import edu.mit.cci.sna.CliqueDecoratedNetwork;
import edu.mit.cci.sna.NetworkUtils;
import edu.mit.cci.teva.cpm.cos.CosCommunityFinder;
import edu.mit.cci.teva.cpm.cos.CosFileReader;
import edu.mit.cci.teva.cpm.cos.CosRunner;
import edu.mit.cci.teva.engine.CommunityFinderException;
import edu.mit.cci.teva.engine.CommunityFrame;
import edu.mit.cci.teva.engine.TevaParameters;
import edu.mit.cci.util.StreamGobbler;
import edu.mit.cci.util.U;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan
 */
public class CustomCosCommunityFinder extends CosCommunityFinder {

    private final CustomCosRunner runner;
    private final TevaParameters parameters;
    private static final boolean isDebugEnabled = false;
    private final File _outputDir;

    private static final String COS_NET_NAME = "CosNetwork";
    private final boolean _overwriteNetworks;
    
    public CustomCosCommunityFinder(TevaParameters ps) {
        super(ps);
        this.runner = new CustomCosRunner(ps);
        this.parameters = ps;
        this._outputDir = new File(ps.getWorkingDirectory());
        this._overwriteNetworks = ps.getOverwriteNetworks();
        
    }
    
    private void createNetworkFile(edu.mit.cci.sna.Network adapter, File f, boolean edgeWeights) throws IOException {
        //String filename = prefix + "." + System.currentTimeMillis() + Math.random() + ".net";

        TEvA.log("CREATING NETWORK FILE: "+f.getAbsolutePath());
        if(f == null) {
            TEvA.log("FILE IS NULL!");
        }
        
        PrintWriter output = new PrintWriter(new FileWriter(f));
        //output.println("#" + new Date());
        if(output == null) {
            TEvA.log("PRINT WRITER IS NULL!");
        }
        
        for (edu.mit.cci.sna.Edge e : adapter.getEdges()) {
            if(e == null) {
                TEvA.log("EDGE IS NULL, continuing.");
                continue;
            }
            
            if(e.getEndpoints() == null) {
                TEvA.log("EDGE ENDPOINTS ARE NULL, continuing.");
                continue;
            }
            
            
            
            if(e.getEndpoints()[0] == null) {
                TEvA.log("FIRST NODE IS NULL");
            }
            
            if(e.getEndpoints()[1] == null) {
                TEvA.log("SECOND NODE IS NULL!");
            }

            
//            TEvA.log("WRITING NETWORK LINE: "+e.getEndpoints()[0].getLabel() + " " + e.getEndpoints()[1].getLabel() + (edgeWeights ? " " + e.getWeight() : ""));
            output.println(e.getEndpoints()[0].getLabel() + " " + e.getEndpoints()[1].getLabel() + (edgeWeights ? " " + e.getWeight() : ""));
        }
        output.flush();
        output.close();
    }
    
    
    @Override
    public List<CommunityFrame> findCommunities(edu.mit.cci.sna.Network currentGraph, int cliqueSizeAtWindow, int window, String id) throws CommunityFinderException {
        File networkFile = new File(_outputDir, COS_NET_NAME+"."+id+"."+window+".net");
        File outputDir = new File(networkFile.getAbsolutePath()+"_files");
        
        /**
         * HERE WOULD BE A GOOD PLACE TO CALL Files.createFile() twice.
         */
        
        if(!outputDir.exists()) {
            TEvA.log(outputDir.getAbsolutePath()+" DOES NOT EXIST, MAKING IT NOW!");
            outputDir.mkdirs();
        }
        File outputFile = new File(networkFile.getAbsolutePath()+"_files", networkFile.getName());
        
        if(!outputFile.exists() || _overwriteNetworks) {
            try {
                createNetworkFile(currentGraph, outputFile, false);
            } catch (Exception ex) {
                TEvA.log("EXCEPTION: "+ex.getLocalizedMessage());
                throw new CommunityFinderException("Error creating network file!", ex);
            }
        }
        assert(outputFile.exists());
        assert(outputDir.exists());
        assert(networkFile.exists());
        return findCommunities(outputFile, cliqueSizeAtWindow, window);
    }

    @Override
    public List<CommunityFrame> findCommunities(File networkFile, int cliqueSizeAtWindow, int window) throws CommunityFinderException {
        try {
            runner.process(networkFile, parameters.getOverwriteAnalyses());

        } catch (IOException e) {
            throw new CommunityFinderException("Error processing network", e);
        } catch (InterruptedException e) {
            throw new CommunityFinderException("Error running cos", e);
        } catch (CommunityFinderException ex) {
            Logger.getLogger(CustomCosCommunityFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<CliqueDecoratedNetwork> networks = null;

        try {
            networks = CosFileReader.readCommunities(networkFile.getName(), cliqueSizeAtWindow, networkFile.getParentFile());
        } catch (CommunityFinderException ex) {
            TEvA.log(ex.getLocalizedMessage());

            TEvA.log("No communities identified; continuing");
            networks = Collections.emptyList();
        }

        List<CommunityFrame> result = new ArrayList<CommunityFrame>();
        for (CliqueDecoratedNetwork n : networks) {
            result.add(new CommunityFrame(window, n));
        }
        return result;
    }

    private static class CustomCosRunner extends CosRunner {

        
        private String _mCliqueExec;
        private String _cosExec;
        private String _cosParams;
        
        public CustomCosRunner(TevaParameters ps) {

            super(ps.getCosMaxCliquesExecutable(), ps.getCosExecutable(), ps.getCpmParameters(), ps.getWorkingDirectory());
            _mCliqueExec = ps.getCosMaxCliquesExecutable();
            _cosExec = ps.getCosExecutable();
            _cosParams =ps.getCpmParameters();
            
        }

        private boolean move(File source, File destination) {
            File actualDestination;
            Path sourcePath = source.toPath();
            Path destinationPath = destination.toPath();
            boolean success = false;
            try {
                TEvA.log("ATTEMPTING TO MOVE "+sourcePath.toString()+ " TO "+destinationPath.toString()+File.separator+sourcePath.toString());
                
                
                Files.move(sourcePath, new File(destinationPath+File.separator+sourcePath.toString()).toPath(), REPLACE_EXISTING);
                success = true;
                TEvA.log("MOVE SUCCESSFUL!");
//           if(destination.isFile()) {
//               actualDestination = destination;               
//           } else {
//               actualDestination = new File(destination, source.getName());
//           }
//            source.mkdirs();
//            actualDestination.mkdirs();
//           try {
//                actualDestination.createNewFile();
//                if(!source.exists()) {
//                    source.createNewFile();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(CustomCosCommunityFinder.class.getName()).log(Level.SEVERE, null, ex);
//            } finally {
//               return source.renameTo(actualDestination);
//           }
            } catch (IOException ex) {
                Logger.getLogger(CustomCosCommunityFinder.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                return success;
            }
            
            
        }
        
        @Override
        public boolean process(File inputFile, boolean overwrite) throws IOException, InterruptedException, CommunityFinderException {
            File outputDir = inputFile.getParentFile();
            TEvA.log("OUTPUT DIRECTORY:  "+outputDir.getAbsolutePath());
            if (!new File(inputFile.getAbsolutePath() + ".mcliques").exists() || overwrite) {
                if (!generateMaxCliques(inputFile)) {
                    throw new CommunityFinderException("Problem generating max cliques");
                }

            // move files
                boolean b = move(new File(inputFile.getName() + ".map"), outputDir);
                b &= move(new File(inputFile.getName() + ".mcliques"), outputDir);
                if (!b) {
                    TEvA.log("Error moving files");
                    throw new CommunityFinderException("Could not move files to output dir: " + outputDir.getAbsolutePath());
                }
            }
            if (!new File(outputDir, "k_num_communities.txt").exists() || overwrite) {
                if (!generateCommunityAnalysis(inputFile, outputDir)) {
                    throw new CommunityFinderException("Problem extracting communities");
                }

                File[] files = new File(".").listFiles(new FilenameFilter() {
                    public boolean accept(File file, String s) {
                        return s.endsWith("_communities.txt");
                    }
                });
                boolean b = true;
                for (File f : files) {
                    b &= move(f, outputDir);
                }
                if (!b) {
                    throw new CommunityFinderException("Error moving community files");
                }
            }
            return true;
        }

        private boolean generateMaxCliques(File inputFile) throws IOException, InterruptedException {
            TEvA.log("Generating maximal cliques for " + inputFile.getAbsolutePath());
            long current = System.currentTimeMillis();
            String cmd = buildMCliqueCommandLine(inputFile);
            TEvA.log("Run " + cmd);
            Process p = Runtime.getRuntime().exec(cmd);

            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = p.waitFor();
            TEvA.log("ExitValue: " + exitVal);

            if (isDebugEnabled) {
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s = null;
                while ((s = r.readLine()) != null) {
                    System.err.println(s);
                }
            }

            TEvA.log("Finished in " + (System.currentTimeMillis() - current) / 1000l + " seconds.");

            return true;
        }

        private boolean generateCommunityAnalysis(File inputFile, File outputdir) throws IOException, InterruptedException {
            TEvA.log("Generating communities for " + inputFile.getName());
            long current = System.currentTimeMillis();

            File mcliques = new File(outputdir, inputFile.getName() + ".mcliques");
            String cmd = buildCosCommandLine(mcliques);
            TEvA.log("Execute: " + cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = p.waitFor();
            TEvA.log("ExitValue: " + exitVal);

            if (isDebugEnabled) {
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s = null;
                while ((s = r.readLine()) != null) {
                    System.err.println(s);
                }
            }

            TEvA.log("Finished in " + (System.currentTimeMillis() - current) / 1000l + " seconds.");

            return true;
        }

        public String buildMCliqueCommandLine(File inputFile) {
            return String.format("%s %s", _mCliqueExec, inputFile.getAbsolutePath());

        }

        public String buildCosCommandLine(File inputFile) {
            return String.format("%s %s %s", _cosExec, _cosParams, inputFile.getAbsolutePath());

        }
        

        
    }
}
