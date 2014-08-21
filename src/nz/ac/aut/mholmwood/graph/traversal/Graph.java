/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author michael
 */
public class Graph<E extends Comparable> 
        implements Iterable<Entry<Vertex<E>, Set<Edge<E>>>>, Cloneable{
    
    //The set of verticies for this graph.
    private Set<Vertex<E>> verticies;
    private Map<Vertex<E>, Set<Edge<E>>> edgeMap;
    private Map<E, Vertex<E>> vertexMap;
    private Cycle cycle;
    
    public enum Cycle{
        TRUE, FALSE, UNDETERMINED;
    }
    public Graph(){
        verticies = new HashSet<>();
        edgeMap = new TreeMap<>();
        vertexMap = new TreeMap<>();
        cycle = Cycle.UNDETERMINED;
    }
    
    public void setCycle(Cycle cycle){
        this.cycle = cycle;
    }
    
    public Cycle hasCycle(){
        return cycle;
    }
    
    /**
     * Add all of the vertices from the specified graph to this one.
     * @param graph 
     */
    public void addAll(Graph<E> graph){
        for(Vertex<E> v : graph.getVertexSet()){
            addVertex(v);
        }
    }
    
    public boolean addVertex(Vertex<E> vertex){
        Tools.assertNotNull(vertex);
        
        boolean added;
        
        if((added = verticies.add(vertex))){
            edgeMap.put(vertex, vertex.getEdges());
            vertexMap.put(vertex.getElement(), vertex);
        }
        
        return added;
    }
    
    /**
     * Remove the specified vertex from this graph.
     * @param vertex
     * @return 
     */
    public boolean removeVertex(Vertex<E> vertex){
        boolean retVal;
        
        if((retVal = verticies.remove(vertex))){
            edgeMap.remove(vertex);
            vertexMap.remove(vertex.getElement());
        }
        
        return retVal;
    }
    
    public void resetPValues(){
        for(Vertex<E> v : verticies){
            v.setPre(-1);
            v.setPost(-1);
            v.setDist(-1);
           
            for(Edge<E> e : v.getEdges()){
                e.setTraversed(false);
            }
        }
    }
    /**
     * Get the vertices for this graph.
     * @return - Set of vertices.
     */
    public Set<Vertex<E>> getVertexSet(){
        return verticies;
    }
    
    /**
     * Get the specified vertex from the given element. Returns null
     * if the vertex does not exist.
     * @param e
     * @return 
     */
    public Vertex<E> getVertex(E e){
        return vertexMap.get(e);
    }
    
    /**
     * Get all the edges for this graph;
     * @return - A list of the edges for this graph.
     */
    public List<Edge<E>> getAllEdges(){
        List<Edge<E>> edges = new LinkedList<>();
        
        for(Set<Edge<E>> s : edgeMap.values()){
            edges.addAll(s);
        }
        
        return edges;
    }
    
    /**
     * Check to see if this Graph contains the specified vertex.
     * @param vertex
     * @return 
     */
    public boolean contains(Vertex<E> vertex){
        return verticies.contains(vertex);
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        
        for(Vertex v : verticies) {
            builder.append(Tools.D_LINE).append("Vertex: ")
                    .append(v.toString()).append(Tools.D_LINE);
            
        }
        
        int size = 0;
        
        for(Set<Edge<E>> e : edgeMap.values()){
            size += e.size();
        }
        
        builder.append("Total number of edges: ").append(size);
        
        builder.append(Tools.D_LINE);
        return builder.toString();
    }

    @Override
    public Iterator<Entry<Vertex<E>, Set<Edge<E>>>> iterator() {
        return edgeMap.entrySet().iterator();
    }
    
    @Override
    public Graph<E> clone(){
        Graph<E> graph = new Graph<>();
        
        for(Set<Edge<E>> s : edgeMap.values()){
            for(Edge<E> e : s){
                Vertex<E> vOne;
                Vertex<E> vTwo;
                
                if((vOne = graph.getVertex(e.getVOne().getElement())) == null){
                    vOne = e.getVOne().copy();
                    graph.addVertex(vOne);
                }
                if((vTwo = graph.getVertex(e.getVTwo().getElement())) == null){
                    vTwo = e.getVTwo().copy();
                    graph.addVertex(vTwo);
                }
                
                Edge<E> newEdge = new Edge(vOne, vTwo);
                vOne.addEdge(newEdge);
            }
        }
        
        return graph;
    }
}
