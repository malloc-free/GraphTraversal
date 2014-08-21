/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import nz.ac.aut.knights.visual.graph.api.GraphControl;
import nz.ac.aut.knights.visual.graph.api.GraphControl.ControlType;
import nz.ac.aut.knights.visual.graph.api.GraphControlListener;
import nz.ac.aut.knights.visual.graph.api.GraphMessage;
import nz.ac.aut.knights.visual.graph.api.GraphMessage.EnumKeyValue;
import nz.ac.aut.knights.visual.graph.api.GraphModelCommands;
import nz.ac.aut.knights.visual.graph.gui.GraphFrame;
import nz.ac.aut.knights.visual.graph.gui.VisualGraph;
import nz.ac.aut.mholmwood.graph.traversal.*;
import static nz.ac.aut.knights.visual.graph.api.GraphViewCommands.*;
import static nz.ac.aut.knights.visual.graph.api.GraphModelCommands.*;
import static nz.ac.aut.knights.visual.graph.gui.VisualGraph.*;
import nz.ac.aut.mholmwood.graph.algorithm.Algorithm;
import nz.ac.aut.mholmwood.graph.algorithm.AlgorithmWgt;

/**
 *
 * @author michael
 */
public class Main extends VisualGraph{
    private static Graph<Integer> graph;
    private static GraphControl control;
    private static String startName;
    private static String finishName;
    
    public static void main(String[] args){
        final GraphFrame frame = new GraphFrame();
        control = GraphControl.getControl(ControlType.SYNC, true);
        control.addGraphListenerMap(frame.getCommands());
        setUp();
        
        Tools.evQueue(new Runnable() {

            @Override
            public void run() {
                frame.createAndShow();
            }
        });
    }
    
    public static void setUp(){
        control.addGraphListener("DFS", new GraphControlListener(){

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithm(vName, Algorithm.DFS);
            }
        });
        
        control.addGraphListener("BFS", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithm(vName, Algorithm.BFS);
            }
        });
        
        control.addGraphListener("SCA", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithm(vName, Algorithm.KSA);
            }
        });
        
        control.addGraphListener("DIJ", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithmWgt(vName, AlgorithmWgt.DIJ);
            }
        });
        
        control.addGraphListener("MST", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithmWgt(vName, AlgorithmWgt.MST);
            }
        });
        
        control.addGraphListener(OPEN_FILE.toString(), new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String fileType = (String)message.get(OPEN_FILE_TYPE.toString());
                String fileName = "Random";
                if(!fileType.equals(RANDOM.toString())){
                    fileName = (String)message.get(OPEN_FILE_NAME.toString());
                }
                
                GraphModelCommands model = GraphModelCommands.valueOf(fileType);
                openFile(model, fileName);
            }
        });
        
        control.addGraphListener(CLEAR_ALL_MODEL.toString(), new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                graph = null;
                
                sendMessage(CLEAR_ALL_VIEW);
            }
        });
        
        control.addGraphListener("DFS_CYCLE", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithm(vName, Algorithm.DFS_CYCLE);
            }
        });
        
        control.addGraphListener("DFS_LINEARIZE", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                String vName = (String)message.get(VERTEX_NAME.toString());
                runAlgorithm(vName, Algorithm.DFS_LINEARIZE);
            }
        });
        
        control.addGraphListener("FIND_PATH", new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                sendMessage(GET_SELECTION, new EnumKeyValue(){{
                    keyEnum = EXCLUSION_LIST; object = new ArrayList<>();
                }},
                        
                    new EnumKeyValue(){{
                        keyEnum = MESSAGE; object = "Select start node";
                    }},
                    new EnumKeyValue(){{
                        keyEnum = RETURN_COMMAND; object = FIND_PATH_START.toString();
                    }}
                );
            
            }
            
        });
        
        control.addGraphListener(FIND_PATH_START.toString(), new GraphControlListener() {
            
            @Override
            public void alertControlListener(GraphMessage message) {
                startName = ((List<String>)message.get(M_VERTEX_LIST.toString())).get(0);
                final List<String> exclusion = new ArrayList<>();
                exclusion.add(startName);
                sendMessage(GET_SELECTION, new EnumKeyValue(){{
                    keyEnum = EXCLUSION_LIST; object = exclusion;
                }},
                    new EnumKeyValue(){{
                        keyEnum = MESSAGE; object = "Select end node";
                }},
                    
                    new EnumKeyValue(){{
                        keyEnum = RETURN_COMMAND; object = FIND_PATH_FINISH.toString();
                    }});
            }
        });
        
        control.addGraphListener(FIND_PATH_FINISH.toString(), new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                finishName = ((List<String>)message.get(M_VERTEX_LIST.toString())).get(0);
                final List<String> exclusion = new ArrayList<>();
                exclusion.add(startName);
                exclusion.add(finishName);
                sendMessage(GET_SELECTION, new EnumKeyValue(){{
                    keyEnum = EXCLUSION_LIST; object = exclusion;
                }},
                    new EnumKeyValue(){{
                        keyEnum = MESSAGE; object = "Select intermediate nodes";
                }},
                    new EnumKeyValue(){{
                        keyEnum = RETURN_COMMAND; object = FIND_EXECUTE.toString();
                }});
            }
        });
        
        control.addGraphListener(FIND_EXECUTE.toString(), new GraphControlListener() {

            @Override
            public void alertControlListener(GraphMessage message) {
                List<String> vertexList = (List<String>)message.get(M_VERTEX_LIST.toString());
                findPath(vertexList);
            }
        });
    }
    
    public static void openFile(GraphModelCommands type, String fileName){
        Algorithm.reset();
        switch(type){
            case DIRECTED : graph = GraphFactory.readFile(fileName); break;
            case UNDIRECTED : graph = GraphFactory.readWgtFile(fileName); break;
            case RANDOM : graph = GraphFactory.generateRandomGraph(0, 20, 0, 5);
                break;
            default : throw new IllegalArgumentException("No such option");
        }
        
        sendMessage(CLEAR_ALL_VIEW);
        displayGraph(type);
    }
    
    /**
     * Send commands to display the selected graph.
     * @param type 
     */
    public static void displayGraph(GraphModelCommands type){
        
        for(Vertex<Integer> v : graph.getVertexSet()){
            final String name = v.getElement().toString();
            addVertex(name);
            final String label = v.getPre() + "/" + v.getPost();
            
            sendMessage(VERTEX_MODIFY_LABEL, new EnumKeyValue(){{
                    keyEnum = VERTEX_NAME; object = name;
                }},
                new EnumKeyValue(){{
                    keyEnum = VERTEX_LABEL; object = label;
                }});
            
            sendMessage(VERTEX_MODIFY_FILL, new EnumKeyValue(){{
                    keyEnum = VERTEX_NAME; object = name;
                }},     
                new EnumKeyValue(){{
                    keyEnum = VERTEX_FILL; object = true;
                }}
            );
        }
       
        
        for(final Edge e : graph.getAllEdges()){
            addEdge(e.getVOne().getElement().toString(), 
                    e.getVTwo().getElement().toString());
            
            sendMessage(EDGE_MODIFY_TEXT, new EnumKeyValue(){{
                    keyEnum = EDGE_TEXT; object = Integer.toString(e.getWeight());
                }},
                new EnumKeyValue(){{
                    keyEnum = EDGE_VERTEX_ONE; object = e.getVOne().getElement().toString();
                }},
                new EnumKeyValue(){{
                    keyEnum = EDGE_VERTEX_TWO; object = e.getVTwo().getElement().toString();
                }}
            );
            
            if(type == UNDIRECTED){
                sendMessage(EDGE_MODIFY_DIR, new EnumKeyValue(){{
                        keyEnum = EDGE_DIRECTION; object = false;
                    }},

                    new EnumKeyValue(){{
                        keyEnum = EDGE_VERTEX_ONE; object = e.getVOne().getElement().toString();
                    }},

                    new EnumKeyValue(){{
                        keyEnum = EDGE_VERTEX_TWO; object = e.getVTwo().getElement().toString();
                    }});
            }
         
        }
        
        if(type == DIRECTED){
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "DFS");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "BFS");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "SCA");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "DFS_CYCLE");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "DFS_LINEARIZE");
        }
        else{
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "DIJ");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "MST");
            sendMessage(SET_MODEL_COMMAND, R_CLICK_MENU, "FIND_PATH");
        }
    }
    
    public static void runAlgorithmWgt(String vertexName, AlgorithmWgt algorithm){
        graph.resetPValues();
        int s = Integer.parseInt(vertexName); 
         Vertex<Integer> start = graph.getVertex(s);
       
         if(algorithm == AlgorithmWgt.DIJ){
            for(Edge<Integer> e : graph.getAllEdges()){
                e.generateOpposingEdge();
            } 
            
            algorithm.performAlgorithm(start, graph);//, start, list);
            
            for(Edge<Integer> e : graph.getAllEdges()){
                e.removeOpposingEdge();
            }
         }
         else if(algorithm == AlgorithmWgt.MST){
             Set<Edge<Integer>> set = new HashSet<>();
             algorithm.performAlgorithm(start, graph, set);
         }
         
         for(Vertex<Integer> v : graph.getVertexSet()){
            final String name = v.getElement().toString();
            final String label = algorithm.getLabel(v);
            
            sendMessage(VERTEX_MODIFY_LABEL, new EnumKeyValue(){{
                    keyEnum = VERTEX_NAME; object = name;
                }},
                new EnumKeyValue(){{
                    keyEnum = VERTEX_LABEL; object = label;
                }});
            
            for(Edge<Integer> e : v.getEdges()){
                Color color = (e.isTraversed()) ? Color.red : Color.black;
                
                modifyEdgeColor(color, e.getVOne().getElement().toString(),
                        e.getVTwo().getElement().toString());
                
            }
        }
        
    }
    
    public static void runAlgorithm(String vertexName, Algorithm algorithm){
        graph.resetPValues();
        int s = Integer.parseInt(vertexName);
        Vertex<Integer> start = graph.getVertex(s);
        graph.resetPValues();
        
        if(algorithm == Algorithm.KSA){
           
            List<SCComponent> list = new ArrayList<>();
            algorithm.performAlgorithm(start, graph, list);
            Queue<Color> colors = generateColors(list.size());
            for(SCComponent scc : list){
                Color color = colors.poll();
                for(Vertex v : scc){
                    modifyColor(color, v.getElement().toString());
                }
            }
            
            update(algorithm);
        }
        else if(algorithm == Algorithm.DFS_CYCLE){
            Algorithm.DFS_CYCLE.performAlgorithm(start, graph);
            update(algorithm);
            
            for(Edge<Integer> e : graph.getAllEdges()){
               
                if(e.isBackEdge()){
                modifyEdgeColor(Color.GREEN, e.getVOne().getElement().toString(),
                        e.getVTwo().getElement().toString());
                }
            }
        }
        else if(algorithm == Algorithm.DFS_LINEARIZE){
            String message;
            
            try{
                List<Vertex<Integer>> list = new ArrayList<>();
                Algorithm.DFS_LINEARIZE.performAlgorithm(start, graph, list);
                update(algorithm);

                StringBuilder builder = new StringBuilder();

                for(Vertex<Integer> v : list){
                    builder.append(v.getElement().toString()).append(",");
                }
                
                message = builder.toString();
            }
            catch(IllegalArgumentException ex){
                message = "Cycle detected!";
            }
            
            final String finMessage = message;
            
            sendMessage(DISPLAY_MESSAGE,
                new EnumKeyValue(){{
                    keyEnum = MESSAGE; object = finMessage;
                }},
                new EnumKeyValue(){{
                    keyEnum = TITLE; object = "Linearization of Graph";
                }});
            
        }
        else{
            algorithm.performAlgorithm(start, graph);
            update(algorithm);
        }
        
        
    }
    
    /**
     * Update the visualization of the graph.
     * 
     * @param algorithm 
     */
    private static void update(Algorithm algorithm){
        for(Vertex<Integer> v : graph.getVertexSet()){
            final String name = v.getElement().toString();
            final String label = algorithm.getLabel(v);
            
            sendMessage(VERTEX_MODIFY_LABEL, new EnumKeyValue(){{
                    keyEnum = VERTEX_NAME; object = name;
                }},
                new EnumKeyValue(){{
                    keyEnum = VERTEX_LABEL; object = label;
                }});
            
            for(Edge<Integer> e : v.getEdges()){
                Color color = (e.isTraversed()) ? Color.red : Color.black;
                
                modifyEdgeColor(color, e.getVOne().getElement().toString(),
                        e.getVTwo().getElement().toString());
                
            }
        }
    }
    
    private static void findPath(List<String> vStrs){
        int eStart = Integer.parseInt(startName);
        int eFinish = Integer.parseInt(finishName);
        
        List<Vertex<Integer>> vList = new ArrayList<>();
        vList.add(graph.getVertex(eStart));
        vList.add(graph.getVertex(eFinish));
        
        for(String s : vStrs){
            
        }
    }
    
    /**
     * Generate the specified number of colors.
     * @param num
     * @return 
     */
    public static Queue<Color> generateColors(int num){
        Queue<Color> vals = new LinkedList<>();
        vals.add(Color.BLUE.brighter().brighter());
        vals.add(Color.CYAN.brighter().brighter());
        vals.add(Color.GREEN.brighter().brighter());
        vals.add(Color.GRAY.brighter().brighter());
        vals.add(Color.ORANGE.brighter().brighter());
        vals.add(Color.MAGENTA.brighter().brighter());
        vals.add(Color.RED.brighter().brighter());
        
        Queue<Color> colors = new LinkedList<>();
        
        while(num > 0){
            Color c = vals.poll().darker();
            colors.add(c);
            vals.add(c);
            num--;
        }
        
        return colors;
    }
}
