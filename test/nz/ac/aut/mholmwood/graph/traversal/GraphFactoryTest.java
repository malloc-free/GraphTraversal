/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.traversal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class GraphFactoryTest {
    
    public GraphFactoryTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of readFile method, of class GraphFactory.
     */
    @Test
    public void testReadFile() {
        Graph<Integer> g = GraphFactory.readFile("trial.txt");
        
        System.out.println(g.toString());
    }
}