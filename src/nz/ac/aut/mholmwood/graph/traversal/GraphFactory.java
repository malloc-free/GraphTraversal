/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author michael
 */
public class GraphFactory {
    /**
     * Reads the supplied file and generates the Graph.
     * 
     * @param fileName - The name of the file to read.
     * @throws FileNotFoundException 
     */
    public static Graph<Integer> readFile(String fileName){
        
        
        Graph<Integer> graph = new Graph<>();
       
        try(BufferedReader reader = getFileReader(fileName)){
            String line;
            int element = 0;
            
            while((line = reader.readLine()) != null){
                
                Vertex<Integer> temp;
                
                if((temp = graph.getVertex(element)) == null){
                    temp = new Vertex<>(element);
                    graph.addVertex(temp);
                }
                
                StringTokenizer token = new StringTokenizer(line);
                
                while(token.hasMoreTokens()){
                    Vertex tempTwo;
                    
                    int adjecent = Integer.parseInt(token.nextToken());
                    
                    if((tempTwo = graph.getVertex(adjecent)) == null){
                        tempTwo = new Vertex<>(adjecent);
                        graph.addVertex(tempTwo);    
                    }
                    
                    Edge edge = new Edge(temp, tempTwo);
                    temp.addEdge(edge);
                }
                
                element++;
            }
           
        } catch (IOException ex) {
            System.err.println("The following error has occured: " +
                    ex.getMessage());
            System.exit(-1);
        }
       
        return graph;
        
    }
    
    /**
     * Get a buffered reader for the given file.
     * @param fileName
     * @return
     * @throws IOException 
     */
    private static BufferedReader getFileReader(String fileName) throws IOException{
        Tools.assertNotNull(fileName);
        
    
        
        Path path = Paths.get(fileName);
        Charset set = Charset.defaultCharset();
        
        return Files.newBufferedReader(path, set);
    }
    
    /**
     * Read a weighted file.
     * @param fileName
     * @return 
     */
    public static Graph<Integer> readWgtFile(String fileName){
        Graph<Integer> graph = new Graph<>();
        try(BufferedReader reader = getFileReader(fileName)){
            String line;
            int element = 0;
            
            while((line = reader.readLine()) != null){
                
                Vertex<Integer> temp;
                
                if((temp = graph.getVertex(element)) == null){
                    temp = new Vertex<>(element);
                    graph.addVertex(temp);
                    temp.setWeighted(true);
                }
                
                StringTokenizer token = new StringTokenizer(line);
                int currentValue = 0;
                
                while(token.hasMoreTokens()){
                    
                    String toke = token.nextToken();
                    
                    if(!toke.equals("#")){
                        Vertex tempTwo;
                    
                        if((tempTwo = graph.getVertex(currentValue)) == null){
                            tempTwo = new Vertex<>(currentValue);
                            graph.addVertex(tempTwo);
                            tempTwo.setWeighted(true);
                        }
                        
                        int weight = Integer.valueOf(toke);
                        
                        if(!tempTwo.testAdjacent(temp)){
                            Edge edge = new Edge(temp, tempTwo, weight);
                            temp.addEdge(edge);
                            //Edge edgeTwo = new Edge(tempTwo, temp, weight);
                            //tempTwo.addEdge(edgeTwo);
                        }
                    }
                    
                    currentValue++;
                }
                
                element++;
            }
           
        } catch (IOException ex) {
            System.err.println("The following error has occured: " +
                    ex.getMessage());
            System.exit(-1);
        }
       
        return graph;
        
    }
    
    public static Graph<Integer> generateRandomGraph(int minVal, int maxVal, int minEdge,
            int maxEdge){
        Graph<Integer> ranGraph = new Graph<>();
        Random random = new SecureRandom();
       
        for(int x = minVal; x < maxVal; x++){
            Vertex<Integer> v;
            
            if((v = ranGraph.getVertex(x)) == null){
                v = new Vertex<>(x);
                ranGraph.addVertex(v);
            }
            
            int numEdges = random.nextInt(maxEdge - minEdge) + minEdge;
            
            for(int y = 0; y < numEdges; y++){
                int randEnd = random.nextInt(maxVal - minVal) + minVal;
                
                Vertex<Integer> randV;
                if((randV = ranGraph.getVertex(randEnd)) == null){
                    randV = new Vertex<>(randEnd);
                    ranGraph.addVertex(randV);
                }
                
                Edge<Integer> e = new Edge<>(v, randV);
                v.addEdge(e);
            }
        }
     
        return ranGraph;
        
    }
    
}
