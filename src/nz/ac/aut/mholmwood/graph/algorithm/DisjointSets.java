/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author michael
 * @param <E>
 */
public class DisjointSets<E> {
    
    HashMap<E, TreeNode<E>> nodeMap;
    
    public DisjointSets(){
        nodeMap = new HashMap<>();
    }
    
    /**
     * Add a new set to this DisjointSets. Returns true if the element
     * is added.
     * @param element
     * @return 
     */
    public boolean makeSet(final E element){
        TreeNode<E> node = new TreeNode<>();
        node.element = element;
        node.parent = null;
        
        boolean added = false;
        
        if(added = (!nodeMap.containsKey(element))){
            nodeMap.put(element, node);
        }
        
        return added;
    }
    
    /**
     * Adds all of the elements of the given collection.
     * @param collection
     * @return 
     */
    public boolean addAll(Collection<E> collection){
        boolean allAdded = true;
        
        //List to add successfully added elements.
        List<E> added = new ArrayList<>();
        
        for(final E e : collection){
           if(!makeSet(e)){
               allAdded = false;
               break;
           }
           else {
               added.add(e);
           }
           
           //Remove any elements successfully added if addAll was not successful.
           if(!allAdded) {
               for(E a : added) {
                   nodeMap.remove(a);
               }
           }
        }
    
        return allAdded;
    }
    
    /**
     * Remove a set that contains the given element. To be completed.
     * 
     * @param element
     * @return 
     */    
    public boolean removeSet(final E element) {
        return false;
    }
        
    public boolean union(E elementOne, E elementTwo){
        TreeNode<E> nodeOne = nodeMap.get(elementOne);
        TreeNode<E> nodeTwo = nodeMap.get(elementTwo);
        
        boolean complete = false;
        
        if(nodeOne != null || nodeTwo != null){
            TreeNode<E> rootOne = DisjointSets.this.findRoot(nodeOne);
            TreeNode<E> rootTwo = DisjointSets.this.findRoot(nodeTwo);
    
            rootOne.parent = rootTwo;
        }
        
        return complete;
    }
    
    /**
     * Find the root for the given element.
     * @param element
     * @return 
     */
    public E findRoot(E element){
        return DisjointSets.this.findRoot(nodeMap.get(element)).element;
    }
    
    private TreeNode<E> findRoot(TreeNode<E> node){
        TreeNode<E> checkNode = node;
        
        while(checkNode.parent != null){
            checkNode = checkNode.parent;
        }
        
        return checkNode;
    }
    
    class TreeNode<E>{
        E element;
        TreeNode<E> parent = null;
        
        @Override
        public boolean equals(Object o){
            return ((TreeNode<E>)o).element.equals(element);
        }
    }
}
