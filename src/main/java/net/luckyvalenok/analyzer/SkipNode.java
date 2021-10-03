package net.luckyvalenok.analyzer;

public class SkipNode<N extends Comparable<? super N>> {
    
    N data;
    @SuppressWarnings("unchecked")
    SkipNode<N>[] next = (SkipNode<N>[]) new SkipNode[SkipList.LEVELS];
    
    SkipNode(N data) {
        this.data = data;
    }
    
    void refreshAfterDelete(int level) {
        SkipNode<N> current = this.getNext(level);
        while (current != null && current.getNext(level) != null) {
            if (current.getNext(level).data == null) {
                SkipNode<N> successor = current.getNext(level).getNext(level);
                current.setNext(successor, level);
                return;
            }
            
            current = current.getNext(level);
        }
    }
    
    void setNext(SkipNode<N> next, int level) {
        this.next[level] = next;
    }
    
    SkipNode<N> getNext(int level) {
        return this.next[level];
    }
    
    SkipNode<N> search(N data, int level, boolean print) {
        if (print) {
            System.out.print("Searching for: " + data + " at ");
            print(level);
        }
        
        SkipNode<N> result = null;
        SkipNode<N> current = this.getNext(level);
        while (current != null && current.data.compareTo(data) < 1) {
            if (current.data.equals(data)) {
                result = current;
                break;
            }
            
            current = current.getNext(level);
        }
        
        return result;
    }
    
    void insert(SkipNode<N> SkipNode, int level) {
        SkipNode<N> current = this.getNext(level);
        if (current == null) {
            this.setNext(SkipNode, level);
            return;
        }
        
        if (SkipNode.data.compareTo(current.data) < 1) {
            this.setNext(SkipNode, level);
            SkipNode.setNext(current, level);
            return;
        }
        
        while (current.getNext(level) != null && current.data.compareTo(SkipNode.data) < 1 &&
            current.getNext(level).data.compareTo(SkipNode.data) < 1) {
            
            current = current.getNext(level);
        }
        
        SkipNode<N> successor = current.getNext(level);
        current.setNext(SkipNode, level);
        SkipNode.setNext(successor, level);
    }
    
    void print(int level) {
        System.out.print("level " + level + ": [");
        int length = 0;
        SkipNode<N> current = this.getNext(level);
        while (current != null) {
            length++;
            System.out.print(current.data.toString() + " ");
            current = current.getNext(level);
        }
        
        System.out.println("], length: " + length);
    }
    
}