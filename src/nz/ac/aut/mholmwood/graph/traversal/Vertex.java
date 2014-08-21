/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import nz.ac.aut.mholmwood.graph.algorithm.Algorithm;

/**
 *
 * @author Michael Holmwood
 * @version 0.1 03/10/2013
 */
public class Vertex<E extends Comparable> implements Comparable<Vertex<E>>{
    
    //The element this vertex holds.
    private E element;
    //The set of edes for this vertex.
    Set<Edge<E>> edges;
    //The pre-calculated time for this vertex, used in DFS.
    private int pre;
    //The post-calculated time for this vertex, used in DFS.
    private int post;
    //Specifies if this vertex has already been visited.
    private boolean visited;
    //The distance from the start node, used in BFS
    protected int dist;
    //The algorithm used for processing this vertex.
    private Algorithm algorithm;
    //The id of the group of vertices that this vertex belongs to.
    private int id;
    //Set this vertex to weighted mode.
    public boolean weighted;
 
    /**
     * Constructor for Vertex, takes the element.
     * @param element 
     */
    public Vertex(E element){
        Tools.assertNotNull(element);
        this.element = element;
        edges = new HashSet<>();
        visited = false;
        pre = -1;
        post = -1;
        dist = -1;
        algorithm = Algorithm.NIL;
        id = -1;
    }
    
    
    /**
     * Get the element that this vertex holds.
     * @return - E the element.
     */
    public E getElement(){
        return element;
    }
    
    /**
     * Set the id of vertices this vertex belongs to.
     * @param id 
     */
    public void setId(int id){
        this.id = id;
    }
    
    /**
     * Set the weighted mode of this vertex.
     * @param set 
     */
    public void setWeighted(boolean set){
        this.weighted = set;
    }
    
    /**
     * Get the id of the group of vertices this Vertex belongs to.
     * @return id
     */
    public int getId(){
        return id;
    }
    
    /**
     * Copy the vertex, without the edges.
     * @return 
     */
    public Vertex<E> copy(){
        Vertex<E> vertex = new Vertex<>(element);
        vertex.algorithm = algorithm;
        vertex.dist = dist;
        vertex.post = post;
        vertex.pre = pre;
        
        return vertex;
    } 
    /**
     * Set the pre-calculated time for this Vertex.
     * @param pre 
     */
    public void setPre(int pre){
        this.pre = pre;
    }
    
    /**
     * Set the post-calculated time for this vertex.
     * @param post 
     */
    public void setPost(int post){
        this.post = post;
    }
    
    /**
     * Get the pre-calc for this vertex.
     * @return 
     */
    public int getPre(){
        return pre;
    }
    
    /**
     * Get the post calc for this vertex.
     * @return 
     */
    public int getPost(){
        return post;
    }
    
    /**
     * Set the distance for this vertex.
     * @param dist 
     */
    public void setDist(int dist){
        this.dist = dist;
    }
    
    /**
     * Get the distance for this vertex.
     * @return 
     */
    public int getDist(){
        return dist;
    }
    
    /**
     * Get all of the adjacent vertices for this vertex.
     * @return 
     */
    public Set<Vertex<E>> getAdjecentVertices(){
        Set<Vertex<E>> adjacent = new TreeSet<>();
        
        for(Edge<E> e : edges){
            adjacent.add(e.getVTwo());
        }
        
        return adjacent;
    }
    /**
     * Add an edge to this vertex. If the edge already exists, false
     * will be returned.
     * 
     * @param edge - The edge to add.
     * @return - False if the edge already exists.
     */
    public boolean addEdge(Edge<E> edge){
        return edges.add(edge);
    }
    
    /**
     * Remove the specified edge from this vertex;
     * 
     * @param toRemove - The edge to remove.
     * @return - True if the edge was removed.
     */
    public boolean removeEdge(Edge<E> toRemove){
        return edges.remove(toRemove);
    }

    /**
     * @see java.lang.Object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.element);
        return hash;
    }
    
    /**
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object o){
        
        boolean equals = o instanceof Vertex && ((Vertex)o).element.equals(element);
        
        return equals;
    }

    /**
     * Test to see if the given vertex is adjacent to this one.
     * 
     * @param test
     * @return 
     */
    public boolean testAdjacent(Vertex<E> test){
        boolean adjacent = false;
        for(Edge<E> edge : edges){
            if(edge.getVTwo().equals(test)){
                adjacent = true;
                break;
            }
        }
        
        return adjacent;
    }
    /**
     * @see java.lang.Comparable
     */
    @Override
    public int compareTo(Vertex<E> o) {
        int retVal;
        
        if(weighted){
            retVal = compareWeighted(o);
        }
        else{
            retVal = element.compareTo(o.element);
        }
            
        return retVal;
    }
    
    private int compareWeighted(Vertex<E> o){
        int retVal;
        
        if(dist == -1 && o.dist != -1){
            retVal = -1;
        }
        else if(dist != -1 && o.dist == -1){
            retVal = 1;
        }
        else{
            retVal = dist - o.dist;
        }
        
        return retVal;
    }
    /**
     * Get the edges for this vertex.
     * @return 
     */
    public Set<Edge<E>> getEdges(){
        return edges;
    }
    
    /**
     * Get the edge that links this vertex to the specified vertex, if it
     * exists.
     * 
     * @param v
     * @return 
     */
    public Edge<E> getEdgeFor(Vertex<E> v){
        Edge<E> edge = null;
        
        for(Edge<E> e : edges){
            if(e.getVTwo().equals(v)){
                edge = e;
                break;
            }
        }
        
        return edge;
    }
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        
        builder.append(Tools.LINE).append("Element: ").append(element.toString());
        
        for(Edge e : edges){
            builder.append(e.toString());
        }
        
        builder.append(Tools.LINE);
        return builder.toString();
    }
}
