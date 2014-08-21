/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import nz.ac.aut.mholmwood.graph.traversal.Edge;
import nz.ac.aut.mholmwood.graph.traversal.Graph;
import nz.ac.aut.mholmwood.graph.traversal.Vertex;

/**
 *
 * @author michael
 */
public enum AlgorithmWgt {
    
    /**
     * Perform dijkstra's algorithm on the supplied vertex.
     */
    DIJ(){
        @Override
        public void performAlgorithm(Vertex<?> start,
            Graph<?> g, Object... args){
            dijkstura(start, g);
        }
        
        @Override
        public String getLabel(Vertex<?> vertex){
            return "min dist = " + Integer.toString(vertex.getDist());
        }
    },
    
    /**
     * Perform kruskal's algorithm on the supplied graph.
     */
    MST(){
        @Override
        public void performAlgorithm(Vertex<?> start,
            Graph<?> g, Object... args){
            Set<Edge<?>> set = 
                    (Set<Edge<?>>)args[0];
            kruskal(g, set);
        }
        
        @Override
        public String getLabel(Vertex<?> vertex){
            return "";
        }
    },
    
    /**
     * Perform floyd-warshall on the graph, and find the minimum walk for the
     * given vertices. 
     * ************Note: This is not currently implemented in the GUI.**********
     */
    FW(){
        @Override
        public void performAlgorithm(Vertex<?> start,
            Graph<?> g, Object... args){
            
            Vertex<?> finish = (Vertex<?>)args[0];
            List<Vertex<?>> list = (List<Vertex<?>>)args[1];
            List<Vertex<?>> vList = new ArrayList<>((Set<Vertex<?>>)g.getVertexSet());
            
            //Need another comparator, comparing elements of a vertex.
            Collections.sort((List<Comparable>)(List<?>)vList,
                    new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    Comparable one = ((Vertex)o1).getElement();
                    Comparable two = ((Vertex)o2).getElement();
                    
                    return one.compareTo(two);
                }
            });
            
            //Initalize the weighted matrix
            int[][] weights = initalize(g, vList);
            //Perform FW
            int[][] next = floydWarshall(weights);
            //Get the index values of the two vertices
            int iOne = vList.indexOf(start);
            int iTwo = vList.indexOf(finish);
            //Find the path between the two vertices.
            findPath(list, vList, iOne, iTwo, next);
        }
        
        @Override
        public String getLabel(Vertex<?> vertex){
            return "";
        }
    };
    
    //The value of infinity, used for the matirx of vertex weights.
    private static int INFINITY = Integer.MAX_VALUE;
    
    /**
     * The abstract method used to perform the algorithm.
     * @param start
     * @param g
     * @param args 
     */
    public abstract void performAlgorithm(Vertex<?> start,
            Graph<?> g, Object... args);
    /**
     * The abstract method used to fetch a label for the algorithm.
     * @param vertex
     * @return 
     */
    public abstract String getLabel(Vertex<?> vertex);
    
    /**
     * Perform Kruskal's algorithm on the selected graph.
     * @param start
     * @param g 
     */
    private static void kruskal(Graph<?> g, Set<Edge<?>> edges){
        DisjointSets<Vertex<?>> disjoint = new DisjointSets<>();
        Set<Vertex<?>> vSet = (Set<Vertex<?>>)g.getVertexSet();
        
        //Add all vertices to the disjoint set.
        disjoint.addAll(vSet);
     
        //Create a list of all edges.
        List<Edge<?>> list = new ArrayList<>();
        list.addAll(g.getAllEdges());
        
        //Comparator required so we compare weights of edges.
        Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((Edge)o1).getWeight() - ((Edge)o2).getWeight();
            } 
        });
        
        //Add edges to the disjoint set.
        for(Edge<?> e : list){
            Vertex<?> rootOne = disjoint.find(e.getVOne());
            Vertex<?> rootTwo = disjoint.find(e.getVTwo());
            
            if(!(rootOne.equals(rootTwo))){
                edges.add(e);
                e.setTraversed(true);
                disjoint.union(rootOne, rootTwo);
            }
        }
        
    }
    
    /**
     * Perform Dijkstura's algorithm on the given graph.
     * @param start
     * @param g 
     */
    private static void dijkstura(Vertex<?> start, 
            Graph<?> g){
        
        //Setup the priority queue
        PriorityQueue<Vertex<?>> queue = new PriorityQueue<>();
        
        //Create the queue and initalize.
        Set<Vertex<?>> set = new HashSet<>();
        queue.add(start);
        start.setDist(0);
        
        //Start worknig the queue.
        while(!queue.isEmpty()){
            Vertex<?> v = queue.poll();
            set.add(v);
            
            for(Edge<?> e : v.getEdges()){
                Vertex<?> vCurr = e.getVTwo();
                if(!set.contains(vCurr)){
                    int dist = e.getWeight() + v.getDist();
                    
                    if(vCurr.getDist() == -1 || dist < vCurr.getDist()){
                        vCurr.setDist(dist);
                    }
                    
                    queue.add(vCurr);
                }
            }
        }
    }
    
    /**
     * Intialize a adjacency matrix for the vertices weights.
     * @param g
     * @param vList
     * @return 
     */
    private static int[][] initalize(Graph<?> g, List<Vertex<?>> vList){
        int[][] f = new int[vList.size()][vList.size()];
        
        for(int[] i : f){
            Arrays.fill(i, INFINITY);
        }
        
        //Initialize f
        for(int x = 0; x < vList.size(); x++){
            Vertex vOne = vList.get(x);
            
            for(int y = 0; y < vList.size(); y++){
                if(x != y){
                    Vertex vTwo = vList.get(y);
                    Edge e;
                    if((e = vOne.getEdgeFor(vTwo)) != null){
                        f[x][y] = e.getWeight();
                        f[y][x] = e.getWeight();
                    }
                }
                else{
                    f[x][y] = 0;
                }
            }
        }
        
        return f;
        
    }
    
    /**
     * Perform the Floyd-Warshall algorithm on the given matrix.
     * @param f
     * @return 
     */
    private static int[][] floydWarshall(int[][] f){
        int[][] next = new int[f.length][f.length];
        
        for(int[] i : next){
            Arrays.fill(i, 0, next.length, -1);
        }
        
        for(int k = 0; k < f.length; k++){
            for(int i = 0; i < f.length; i++){
                for(int j = 0; j < f.length; j++){
                    //When values are infinity, then we need to treat this
                    //special case.
                    boolean infinity = (f[i][k] == INFINITY) || (f[k][j] == INFINITY);
                    if(!infinity && (f[i][k] + f[k][j]) < f[i][j]){
                        f[i][j] = f[i][k] + f[k][j];
                        //Add k to the list of intermediate nodes.
                        next[i][j] = k;
                    }
                }
            }
        }
        
        return next;
    }
    
    /**
     * Find the shortest path using the given vertices as the start and end
     * points.
     * @param list
     * @param vList
     * @param start
     * @param finish
     * @param next 
     */
    private static void findPath(List<Vertex<?>> list, List<Vertex<?>> vList, 
            int start, int finish,
            int[][] next){
       
        List<Integer> indexList = new ArrayList<>();

        indexList.addAll(path(start, finish, next));
        indexList.add(0, start);
        indexList.add(finish);
        
        for(int i : indexList){
            list.add(vList.get(i));
        }
    }
    
    /**
     * Recursively find the path between two points, including all intermediate
     * points.
     * @param start
     * @param finish
     * @param next
     * @return 
     */
    private static List<Integer> path(int start, int finish,
            int[][] next){
        
        int intermediate = next[start][finish];
        
        List<Integer> indexList = new ArrayList<>();
        
        if(intermediate != -1){
            indexList.addAll(0, path(start, intermediate, next));
            indexList.add(intermediate);
            indexList.addAll(path(intermediate, finish, next));
        }
        
        return indexList;
    }
}
