/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.mholmwood.graph.algorithm;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import nz.ac.aut.mholmwood.graph.traversal.Edge;
import nz.ac.aut.mholmwood.graph.traversal.Graph;
import nz.ac.aut.mholmwood.graph.traversal.GraphFactory;
import nz.ac.aut.mholmwood.graph.traversal.Vertex;

/**
 *
 * @author michael
 */
public class PathFinder extends Thread{
        
        private CalcGroup group;
        private Path p;
        private String label;
       
        @Override
        public void run() {
            try {
                if(group.keeperSet.compareAndSet(false, true)){
                    group.keep = this;
                    update();
                }
                else
                    calc();
            } catch (InterruptedException ex) {
            }
        }
        
        public static class CalcGroup{
            private AtomicBoolean keeperSet;
            private AtomicBoolean stop;
            private AtomicInteger atomInt;
            private AtomicInteger atomFin;
            private AtomicInteger atomComplete;
            private List<Path> complete;
            private PathFinder[] calcs;
            private PriorityBlockingQueue<Path> queue;
            private PathFinder keep;
            
            public CalcGroup(){
                keeperSet = new AtomicBoolean(false);
                atomInt = new AtomicInteger(0);
                atomFin = new AtomicInteger(0);
                complete = new LinkedList<>();
                stop = new AtomicBoolean(false);
                queue = new PriorityBlockingQueue<>();
            }
            
            public void addToQueue(Path path){
                queue.add(path);
            }
            
            public void setThreads(int threads){
                calcs = new PathFinder[threads];
                atomComplete = new AtomicInteger(threads - 1);
                for(int x = 0; x < calcs.length; x++){
                    PathFinder calc = new PathFinder();
                    calc.label = Integer.toString(x);
                    calcs[x] = calc;
                    calc.group = this;
                }
            }
            
            public void start(){
                for(PathFinder c : calcs){
                    c.start();
                }
            }
            
            public void joinTo() throws InterruptedException{
                while(keep == null);
                keep.join();
            }
         
            public int getNumCalculations(){
                return atomInt.get();
            }
            
            public int getQueueSize(){
                return queue.size();
            }
            
            public int getNumFinishedPaths(){
                return atomFin.get();
            }
            
            public int getNumFoundPaths(){
                return complete.size();
            }
            
            public List<Path> getCompleteList(){
                return complete;
            }
        }
        
        private void update() throws InterruptedException{
            group.keep = this;
            
            do{
                sleep(1000);
                System.out.println("Num calcs = " + group.atomInt.toString());
                System.out.println("Num complete = " + group.atomFin.toString());
                System.out.println("Num threads running = " + group.atomComplete.toString());
                System.out.println("Num found = " + group.complete.size());
            }
            while(!interrupted());
            System.out.println(label + " : bailing!");
        }
        
        private void calc() throws InterruptedException{
             p = group.queue.take();
            
             do{
                Vertex<?> sqOne = p.getCurrentVertex();
                Path<?> clone = p.clone();
                boolean added = false;
                
                for(Edge<?> e : sqOne.getEdges()){
                    Vertex<?> sqTwo = e.getVTwo();
                    if(!p.contains(sqTwo)){
                        group.atomInt.incrementAndGet();
                        if(!added){
                            p.addVertex(sqTwo);
                            p.addToDistance(e.getWeight());
                            added = true;
                        }
                        else{
                            Path<?> temp = clone.clone();
                            temp.addVertex((Vertex)sqTwo);
                            temp.addToDistance(e.getWeight());
                            group.queue.add(temp);
                        }
                    }
                }
                
                if(!added){
                    group.complete.add(p);
                    group.atomFin.incrementAndGet();
                    Path<?> temp = group.queue.poll(5l, TimeUnit.SECONDS);
                    
                    if(temp == null){
                        System.out.println("Nothing to do!");
                        break;
                    }
                    else
                        p = temp;
                } 
            }
             
            while(!group.stop.get());
            
             if(group.atomComplete.decrementAndGet() == 0){
                 group.keep.interrupt();
             }
        }
        
        public static class Path<E extends Comparable> implements Comparable<Path<E>>{
            Deque<Vertex<E>> visited;
            int totalDistance;
            
            Path(){
                visited = new LinkedList<>();
                totalDistance = 0;
            }
            
            Vertex<E> getCurrentVertex(){
                return visited.peekLast();
            }
            
            @Override
            public Path<E> clone(){
                Path<E> clone = new Path<>();
                
                for(Vertex<E> v : visited){
                    clone.visited.add(v);
                }
                
                clone.totalDistance = totalDistance;
                
                return clone;
            }
            
            public void addToDistance(int distance){
                totalDistance += distance;
            }
            
            public boolean containsAll(Collection<E> vertices){
                return visited.containsAll(vertices);
            }
            
            boolean addVertex(Vertex<E> vertex){
                boolean contains = false;
                
                if(!visited.contains(vertex)){
                    visited.add(vertex);
                    contains = true;
                }
                return contains;
            }
            
            boolean contains(Vertex<E> vertex){
                return visited.contains(vertex);
            }
            
            int getLevel(){
                return visited.size();
            }

            @Override
            public int compareTo(Path<E> o) {
                return getLevel() - o.getLevel();
            }
        }
        
        public static void main(String... args) throws InterruptedException{
            Graph<Integer> graph = GraphFactory.readWgtFile("matrix.txt");
            
            for(Edge<Integer> e : graph.getAllEdges()){
                e.generateOpposingEdge();
            }
            
            Vertex<Integer> v = graph.getVertex(0);
            
            CalcGroup group = new CalcGroup();
            
            group.setThreads(4);
            Path start = new Path();
            start.addVertex(v);
            group.addToQueue(start);
            group.start();
            group.joinTo();
            
            for(Edge<Integer> e : graph.getAllEdges()){
                e.removeOpposingEdge();
            }
            
            for(Path<Integer> p : group.getCompleteList()){
                System.out.println("-------------------- Path --------------------");
                System.out.println("Total distance = " + p.totalDistance);
                for(Vertex<Integer> vert : p.visited){
                    System.out.println(vert);
                }
                
            }
        }
    }
