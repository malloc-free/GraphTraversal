/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author michael
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
        
        for(final E e : collection){
           if(!makeSet(e)){
               allAdded = false;
           }
        }
        
        return allAdded;
    }
    
    public boolean union(E elementOne, E elementTwo){
        TreeNode<E> nodeOne = nodeMap.get(elementOne);
        TreeNode<E> nodeTwo = nodeMap.get(elementTwo);
        
        boolean complete = false;
        
        if(nodeOne != null || nodeTwo != null){
            TreeNode<E> rootOne = findRoot(nodeOne);
            TreeNode<E> rootTwo = findRoot(nodeTwo);
    
            rootOne.parent = rootTwo;
        }
        
        return complete;
    }
    
    /**
     * Find the parent for the given element.
     * @param element
     * @return 
     */
    public E find(E element){
        return findRoot(nodeMap.get(element)).element;
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
