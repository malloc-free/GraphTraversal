/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import java.util.Objects;

/**
 *
 * @author Michael Holmwood
 * @version 0.1 03/10/2013
 */
public class Edge<E extends Comparable> implements Comparable<Edge<E>>{
    
    //The first vertex of this edge. 
    private Vertex<E> vOne;
    //The second vertex of this edge.
    private Vertex<E> vTwo;
    //The weight for this edge.
    private int weight;
    //Specifies if this edge has already been traversed.
    private boolean traversed;
    //Signifies if this is a back edge.
    private boolean backEdge;
    //An opposing edge, required by some algorithms.
    private Edge<E> opposing;
    
    private Edge(){};
    
    /**
     * Constructor that creates an edge with a weight of zero.
     * @param vOne
     * @param vTwo 
     */
    public Edge(Vertex<E> vOne, Vertex<E> vTwo){
        Tools.assertNotNull(vOne, vTwo);
        this.vOne = vOne;
        this.vTwo = vTwo;
        weight = 0;
        traversed = false;
        backEdge = false;
    }
    
    /**
     * Constructor that creates an edge with a weight value.
     * @param vOne
     * @param vTwo
     * @param weight 
     */
    public Edge(Vertex<E> vOne, Vertex<E> vTwo, int weight){
        this(vOne, vTwo);
        this.weight = weight;
    }
    
    /**
     * Set if this edge is a back edge or not.
     * @param backEdge 
     */
    public void setBackEdge(boolean backEdge){
        this.backEdge = backEdge;
    }
    
    /**
     * Returns if this edge is a back edge or not.
     * @return 
     */
    public boolean isBackEdge(){
        return backEdge;
    }
    /**
     * Set this if the vertex has been traversed.
     * @param traversed 
     */
    public void setTraversed(boolean traversed){
        this.traversed = traversed;
    }
    
    /**
     * Use this to test if the vertex has been traversed.
     * @return 
     */
    public boolean isTraversed(){
        return traversed;
    }
    
    /**
     * Get the first vertex of this edge.
     * @return - the vertex vOne.
     */
    public Vertex<E> getVOne(){
        return vOne;
    }
    
    /**
     * Get the weight for this edge.
     * @return 
     */
    public int getWeight(){
        return weight;
    }
    
    /**
     * Return the second vertex for this edge.
     * @return - the vertex vTwo
     */
    public Vertex<E> getVTwo(){
        return vTwo;
    }
    
    /**
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object o){
        
        boolean equals = false;
        
        if(o instanceof Edge){
            Edge e = (Edge)o;
            equals = (e.vOne == vOne) && (e.vTwo == vTwo);
        }
        
        return equals;
    }

    /**
     * @see java.lang.Object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.vOne);
        hash = 17 * hash + Objects.hashCode(this.vTwo);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        
        builder.append(Tools.S_LINE).append("vOne: ").append(vOne.getElement().toString());
        builder.append("\nvTwo: ").append(vTwo.getElement()).append(Tools.S_LINE);
        
        return builder.toString();
    }

    @Override
    public int compareTo(Edge<E> e) {
        return vTwo.compareTo(e.vTwo);
    }
    
    /**
     * Swap the direction this edge is pointing.
     */
    public void swapDirection(){
        vOne.removeEdge(this);
        vTwo.addEdge(this);
        Vertex<E> vTemp = vOne;
        vOne = vTwo;
        vTwo = vTemp;
       
        System.out.println("bla");
    }
    
    /**
     * Generates an opposing edge.
     */
    public void generateOpposingEdge(){
        opposing = new Edge<>();
        opposing.vOne = vTwo;
        opposing.vTwo = vOne;
        opposing.weight = weight;
        vTwo.addEdge(opposing);
    }
    
    /**
     * Removes the opposing edge.
     */
    public void removeOpposingEdge(){
        vTwo.removeEdge(opposing);
        opposing = null;
    }
    
    /**
     * Tests if this edge has an opposing edge.
     * @return 
     */
    public boolean hasOpposingEdge(){
        return (opposing != null) ? true : false;
    }
}
