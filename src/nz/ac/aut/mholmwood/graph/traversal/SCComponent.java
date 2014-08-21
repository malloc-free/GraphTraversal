/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author michael
 */
public class SCComponent implements Iterable<Vertex>{
    
    private Set<Vertex> verticies;
    private static int currentId = -1;
    private int id;
    
    public SCComponent(){
        verticies = new TreeSet<>();
        id = ++currentId;
    }
    
    public void resetId(){
        currentId = 0;
    }
    /**
     * Add the vertex, and change the id to that of this SCComponent
     * @param vertex
     * @return 
     */
    public boolean addVertex(Vertex vertex){
        vertex.setId(currentId);
        return verticies.add(vertex);
    }
    
    /**
     * Iterate through the vertices in this SCC.
     * @return 
     */
    @Override
    public Iterator<Vertex> iterator(){
        return verticies.iterator();
    }
}
