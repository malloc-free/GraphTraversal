/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

/**
 *
 * @author michael
 */
public class Tools {
    public static final String LINE =   "\n-----------------------------------\n";
    public static final String D_LINE = "\n===================================\n";
    public static final String S_LINE = "\n***********************************\n";
    
    static void assertNotNull(Object... objects) throws IllegalArgumentException{
        for(Object o : objects){
            if(o == null){
                throw new IllegalArgumentException("Argument is null");
            }
        }
    }
    
    /**
     * Add the runnable to the event queue;
     * @param run 
     */
    public static void evQueue(Runnable run){
        java.awt.EventQueue.invokeLater(run);
    }
}
