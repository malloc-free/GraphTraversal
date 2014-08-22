/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import nz.ac.aut.mholmwood.graph.traversal.Edge;
import nz.ac.aut.mholmwood.graph.traversal.Graph;
import nz.ac.aut.mholmwood.graph.traversal.Graph.Cycle;
import nz.ac.aut.mholmwood.graph.traversal.SCComponent;
import nz.ac.aut.mholmwood.graph.traversal.Vertex;

/**
 *
 * @author michael
 */
public enum Algorithm {
    /**
     * Perform basic DFS
     */
    DFS(){
        @Override
        public void performAlgorithm(Vertex<?> start,
                Graph<?> g, Object... args){
            calc = -1;
            id = 0;
            start.setId(id);
            performDFS(start);
            start.setPost(++calc);
            
            //If a callback is set, do this next.
            if(call != null){
                call.call(start);
            }
            
            //Iterate through the remaining vertices in the graph.
            for(Vertex v : g.getVertexSet()){
                v.setId(++id);
                if(v.getPre() == -1){
                    //Callback function again.
                    if(call != null){
                        call.call(v);
                    }
                    performDFS(v);
                    v.setPost(++calc);
                    
                }          
            }
            
            //Set if a cycle was detected in this graph.
            g.setCycle((cycleDetected) ? Cycle.TRUE : Cycle.FALSE);
        }
        
        /**
         * Get labels for vertices that have had DFS performed on them.
         */
        @Override
        public String getLabel(Vertex<?> v){
            return v.getPre() + "/" + v.getPost();
        }
    },
    
    /**
     * Perform DFS with cycle detection.
     */
    DFS_CYCLE(){
        @Override
        public void performAlgorithm(Vertex<?> start, Graph<?> g, Object... args){
            if(g.hasCycle() == Cycle.UNDETERMINED)
                DFS.performAlgorithm(start, g, args);
        }
        
        /**
         * Get labels for vertices that have had a DFS cycle performed on them.
         */
        @Override
        public String getLabel(Vertex<?> v){
            return DFS.getLabel(v);
        }
    },
    
    /**
     * Linearize the given graph, from the given starting point.
     */
    DFS_LINEARIZE(){
        @Override
        public void performAlgorithm(Vertex<?> start, Graph<?> g, Object... args){
            stack = new ArrayDeque<>();
            
            //Set the callback to load finished nodes onto a stack.
            call = new CallBack() {

                @Override
                public void call(Vertex<?> vertex) {
                    stack.push(vertex);
                }
            };
            
            //Perform DFS, using the callback function.
            DFS.performAlgorithm(start, g, args);
            //If there is a cycle detected, report.
            if(g.hasCycle() == Cycle.TRUE){
                throw new IllegalArgumentException("Graph contains a cycle, cannot"
                        + " lineraize!");
            }
            
            //args[0] is the list to be loaded with vertices.
            List<Vertex<?>> list = (List<Vertex<?>>)args[0];
            
            //Unload them as they are popped.
            while(!stack.isEmpty()){
                list.add(stack.pop());
            }
        }
        
        @Override
        public String getLabel(Vertex<?> v){
            return DFS.getLabel(v);
        }
    },
    
    /**
     * Perform a breadth-first search of the designated graph.
     */
    BFS(){
        @Override
        public void performAlgorithm(Vertex<?> start,
                Graph<?> g, Object... args){
            queue = new ArrayDeque<>();
            queue.add(start);
            start.setDist(0);
            performBFS();
            
        }
        
        @Override
        public String getLabel(Vertex<?> v){
            int dist = v.getDist();
            String append;
            if(dist == -1){
               
                append = "dist = " + '\u221E';
            }
            else{
                append = "dist = " + dist;
            }
                
            return append;
        }
    },
    
    /**
     * Create a list of Strongly connected components in the given graph.
     */
    KSA(){
        @Override
        public void performAlgorithm(Vertex<?> start, 
                Graph<?> g, Object... args){
            stack = new ArrayDeque<>();
            
            call = new CallBack() {

                @Override
                public void call(Vertex<?> vertex) {
                    stack.push(vertex);
                }
            };
            
            if(args != null && args.length != 1 &&
                    !(args[0] instanceof List)){
                List l = (List)args[0];
            }
            
            List<SCComponent> list = 
                    (List<SCComponent>)args[0];
            
            performKSA(start, g, list);
              
        }
        
        @Override
        public String getLabel(Vertex<?> v){
            return Integer.toString(v.getId());
        }
    },
    
    NIL(){
        @Override
        public void performAlgorithm(Vertex<?> start,
                Graph<?> g, Object... args){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLabel(Vertex<?> v){
            throw new UnsupportedOperationException();
        }
    };
    
    //Used for pre/post calculations
    private static int calc = -1;
    //Used for BFS.
    private static Queue<Vertex<?>> queue;
    //Used in DFS if other processing is required.
    private static CallBack call = null;
    //Used when finsding strongly connected components.
    private static Deque<Vertex<?>> stack;
    //Used to specifiy the current SCC.
    private static SCComponent sccComponent;
    //Used in DFS to signify the current tree id, used to detect cycles.
    private static int id;
    //Set to true if a cycle has been detected in the current graph.
    private static boolean cycleDetected;
    
    //Abstract method for getting vertex labels.
    public abstract String getLabel(Vertex<?> v);
    //Abstract method for performing the given algorithm.
    public abstract void performAlgorithm(Vertex<?> start,
             Graph<?> g, Object... args);
    
    public static void reset(){
        calc = -1;
        queue = null;
        call = null;
        stack = null;
        sccComponent = null;
        id = 0;
        cycleDetected = false;
    }
    
    /**
     * Perform DFS from the given vertex.
     * @param start 
     */
    private static void performDFS(Vertex<?> start){
        start.setPre(++calc);
        
        for(Edge<?> e : start.getEdges()){
            Vertex<?> v = e.getVTwo();
            
            if(v.getPre() == -1){
                e.setTraversed(true);
                performDFS(v);
                v.setPost(++calc);
                
                //If there is additional processing required, call the 
                //Callback function.
                if(call != null){
                    call.call(v);
                }
            }
            else if(v.getId() == start.getId() && v.getPre() < start.getPre() 
                    && v.getPost() == -1){
                e.setBackEdge(true);
                cycleDetected = true;
            }
            
        }
    }
    
    /**
     * Perform BFS using a queue.
     */
    private static void performBFS(){
        while(!queue.isEmpty()){
            
            Vertex<?> v = queue.poll();
            
            for(Edge<?> e : v.getEdges()){
                Vertex<?> vTwo = e.getVTwo();
                if(vTwo.getDist() == -1){
                    e.setTraversed(true);
                    vTwo.setDist(v.getDist() + 1);
                    queue.add(vTwo);
                }
            }
        }
    }
    
    /**
     * Search for SCC's
     * @param vertex
     * @param graph
     * @param list 
     */
    private static void performKSA(Vertex<?> vertex,
            Graph<?> graph, List<SCComponent> list){
        DFS.performAlgorithm(vertex, graph);
        graph.resetPValues();
        final Graph<?> gPrime = new Graph<>();
        gPrime.addAll((Graph)graph);
        
        //Swap the directions of all of the edges.
        for(Edge<?> e : gPrime.getAllEdges()){
            e.swapDirection();
        }
        
        //As vertices are finished, we want to add them to the SCC and remove
        //them from the stack and graph.
        call = new CallBack() {

            @Override
            public void call(Vertex<?> vertex) {
                sccComponent.addVertex(vertex);
                stack.remove(vertex);
                gPrime.removeVertex((Vertex)vertex);
            }
        };
        
        while(!stack.isEmpty()){
            calc = -1;
            sccComponent = new SCComponent();
            list.add(sccComponent);
            Vertex<?> v = stack.pop();
            //A little bit of a cheat, removeVertex (at this time) does
            //not destroy links between vertices, just removes them from 
            //the graph. resetPValues will not clear the pre and post
            //calc values, and so these 'removed' vertices will not be
            //traversed in subsequent runs of DFS. Effectively the 
            //same thing.
            gPrime.removeVertex((Vertex)v);
            sccComponent.addVertex(v);
            gPrime.resetPValues();
            performDFS(v);
        }
        
        //Swap all edge directions back.
        for(Edge e : graph.getAllEdges()){
            e.swapDirection();
        }
    }
    
    /**
     * Interface, used by some algorithms that perform DFS.
     */
    private interface CallBack{
        public void call(Vertex<?> vertex);
    }
}
