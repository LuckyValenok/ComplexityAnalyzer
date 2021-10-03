package net.luckyvalenok.analyzer;

import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {
    public static final int LEVELS = 5;
    private final SkipNode<T> head = new SkipNode<>(null);
    private final Random rand = new Random();
    
    public void insert(T data) {
        SkipNode<T> SkipNode = new SkipNode<>(data);
        for (int i = 0; i < LEVELS; i++) {
            if (rand.nextInt((int) Math.pow(2, i)) == 0) {
                insert(SkipNode, i);
            }
        }
    }
    
    public boolean delete(T target) {
        System.out.println("Deleting " + target.toString());
        SkipNode<T> victim = search(target, false);
        if (victim == null)
            return false;
        victim.data = null;
        
        for (int i = 0; i < LEVELS; i++) {
            head.refreshAfterDelete(i);
        }
        
        System.out.println();
        return true;
    }
    
    public SkipNode<T> search(T data) {
        return search(data, true);
    }
    
    public void print() {
        for (int i = 0; i < LEVELS; i++) {
            head.print(i);
        }
        
        System.out.println();
    }
    
    private void insert(SkipNode<T> SkipNode, int level) {
        head.insert(SkipNode, level);
    }
    
    private SkipNode<T> search(T data, boolean print) {
        SkipNode<T> result = null;
        for (int i = LEVELS - 1; i >= 0; i--) {
            if ((result = head.search(data, i, print)) != null) {
                if (print) {
                    System.out.println("Found " + data.toString() + " at level " + i + ", so stopped\n");
                }
                
                break;
            }
        }
        
        return result;
    }
    
}
