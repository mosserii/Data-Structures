

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */

/*Shir Naiberg, id: 318251592, userName: shirnaiberg
  Zohar Mosseri , id:322712860 , userName: zoharmosseri */
public class FibonacciHeap {
    private HeapNode min = null;
    private HeapNode first = null;
    private int size = 0;
    private int numOfTrees = 0;
    private int numOfMark = 0;
    public static int totalLinks = 0;
    public static int numOfCut = 0;


    public FibonacciHeap() {//constructor for an empty heap
    }

    /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() {
        return this.size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
   public HeapNode insert(int key) {
       HeapNode new_node = new HeapNode(key);//creating the node to be inserted
       if (this.isEmpty()) {//empty heap so it will be the only node
           this.first = new_node;
           this.min = this.first;
           new_node.setPrev(new_node);
           new_node.setNext(new_node);
       }
       else {//heap is not empty
           HeapNode temp = this.first;
           this.first = new_node;
           this.first.setNext(temp);
           this.first.setPrev(temp.getPrev());
           temp.setPrev(this.first);
           this.first.getPrev().setNext(this.first);
           if (this.first.getKey() < this.min.getKey())//checking if x < heap's current min
               this.min = this.first;
       }
       this.size++;
       this.numOfTrees++;
       return new_node;
   }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
   public void deleteMin() {
       if (this.size == 0)
           return;//not exist
       if (this.size == 1) {//one node heap so it will become an empty heap
           this.min = null;
           this.first = null;
           this.size = 0;
           this.numOfTrees = 0;
           this.numOfMark = 0;
           return;
       }

       ///////////////////////////////////////////////
       HeapNode y = this.min;
       if (y.getChild() != null) {//min has children

           HeapNode yChild = y.getChild();
           for (int p = 0; p < y.getRank(); p++) {//min children will become roots for now so need to unmark them
               if (yChild.isMark()){//unmark roots
                   yChild.setMark(false);
                   this.numOfMark--;
               }
               yChild = yChild.getNext();
           }

           if (numOfTrees == 1) //min is the only root and has children
               this.first = y.getChild();

           else if (numOfTrees >= 2){//min has siblings & children
               HeapNode xLeft =  y.getPrev();
               HeapNode xRight =  y.getNext();
               if (y == this.first)
                   this.first = xRight;

               //promoting min children to be his siblings' siblings
               xLeft.setNext(y.getChild());
               y.getChild().getPrev().setNext(xRight);
               xRight.setPrev(y.getChild().getPrev());
               y.getChild().setPrev(xLeft);
           }
           this.numOfTrees += min.getRank()-1;//(number of children of min) - (min's tree)
       }
       else {//min has no children, but has siblings
           if (y == this.first)
               this.first = y.getNext();
           y.getPrev().setNext(y.getNext());//bypass min
           y.getNext().setPrev(y.getPrev());
           this.min = y.getNext();
           this.numOfTrees--;
       }
       ///////////////////////////////////////////////
       this.size--;
       toBuckets();//consolidating all the trees
   }





    private void toBuckets() {
        int logn = 1 + (int) Math.ceil((Math.log(this.size)/Math.log(2)));
        HeapNode[] buckets = new HeapNode[logn];//index i in buckets represents a tree of rank i

        HeapNode y = this.first;
        int n = this.numOfTrees;
        buckets[y.getRank()] = y;//adding first tree before the loop
        if (this.size != 1)
            y = y.getNext();

        for (int p = 0; p < n; p++){
            if (n == 1)//already added before the loop
                break;
            int i = y.getRank();
            HeapNode copyOfyNext = y.getNext();
            while (buckets[i] != null){//bucket i is not empty //link y with what's inside and go to next bucket
                y = linkTrees(buckets[i], y, i);
                buckets[i] = null;//free up space of bucket i
                i++;
            }
            buckets[i] = y;

            y = copyOfyNext;//the next that was before of the consolidating
            if (copyOfyNext == this.first)
                break;
        }
        /////end of putting into buckets and consolidating///////////


        HeapNode[] finalTrees = new HeapNode[logn];//array with all the trees, sorted by tree.rank
        int r = 0;
        for (int i = 0; i < logn; i++){
            if (buckets[i] != null)
                finalTrees[r++] = buckets[i];
        }
        this.numOfTrees = r;
        this.min  = finalTrees[r-1];//as a default, might change inside the loop
        this.first = finalTrees[0];
        //handling last tree before the loop
        finalTrees[0].setPrev(finalTrees[r-1]);
        finalTrees[r-1].setNext(finalTrees[0]);
        finalTrees[r-1].setParent(null);
        if (finalTrees[r-1].mark){
            finalTrees[r-1].setMark(false);
            this.numOfMark--;
        }

        for (int i = 0; i < r-1; i++) {//last tree (r-1) is handled before the loop
            if (finalTrees[i].getKey() < this.min.getKey())//finding new min
                this.min = finalTrees[i];
            if (finalTrees[i].isMark()){//unmark roots
                finalTrees[i].setMark(false);
                this.numOfMark--;
        }
            finalTrees[i].setParent(null);//roots have no parent
            finalTrees[i].setNext(finalTrees[i+1]);//connecting them as: left is a lower rank tree than right
            finalTrees[i+1].setPrev(finalTrees[i]);
        }
    }

    private HeapNode linkTrees(HeapNode x1, HeapNode x2, int rank) {
        if (x1.getKey() < x2.getKey()) //who'll be hanged on who? x2 hanged on x1.
            return linkTreesHelper(x1, x2, rank);
        else//x1 hanged on x2
            return linkTreesHelper(x2, x1, rank);//notice the order of arguments
    }

    private HeapNode linkTreesHelper(HeapNode x1, HeapNode x2, int rank) {//we hang x2 on x1
        //x1 is the new root, no siblings
        x1.setNext(x1);
        x1.setPrev(x1);
        x1.setParent(null);
        //x2 is the new child of x1, so he has new siblings (the children of x1)
        HeapNode x1Child = x1.getChild();


        if (rank == 0){//both roots point at themselves as next and prev
            x2.setNext(x2);
            x2.setPrev(x2);
        }
        else if (rank == 1) {//x2 child will point at himself
            x1Child.setNext(x2);
            x1Child.setPrev(x2);
            x2.setPrev(x1Child);
            x2.setNext(x1Child);
        }
        else if (rank > 1) {//need to connect x1 children with x2
            x2.setNext(x1Child);
            x1Child.getPrev().setNext(x2);
            x2.setPrev(x1Child.getPrev());
            x1Child.setPrev(x2);
        }

        x1.setChild(x2);
        x2.setParent(x1);
        x1.setRank(x1.getRank()+1);//added child -> rank +=1;
        numOfTrees--;
        totalLinks++;
        return x1;
    }

    /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) {
        if (heap2 == null || heap2.isEmpty()) //no need to do anything heap2 is empty
            return;

        //heap2 is not empty//
        this.numOfTrees = this.numOfTrees + heap2.numOfTrees; //all the trees from heap2 will be added to this heap
        this.numOfMark += heap2.numOfMark; // all marked nodes from heap2 will be added to this heap
        if (this.isEmpty()){ // if this heap is empty then this heap will become lime heap2
            this.min = heap2.min;
            this.first = heap2.first;
            this.size = heap2.size;
        }
        else { //this heap isn't empty as well
            if (this.min.getKey() > heap2.min.getKey()) //choose new min
                this.min = heap2.min;
            this.first.getPrev().setNext(heap2.first); //add heap2 trees after this heap's  trees
            heap2.first.getPrev().setNext(this.first);
            this.first.setPrev(heap2.first.getPrev());
            heap2.first.setPrev(this.first.getPrev());
            this.size = this.size + heap2.size; //add size
        }

    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep() {
        if (this.isEmpty()) //no need to do anything
            return new int[0];
    	int[] arr = new int[1 + (int) Math.ceil((Math.log(this.size)/Math.log(2)))]; //the largest tree possible is with rank: log(size) (base 2)
        HeapNode x = this.first;
        arr[x.getRank()]++; // add the first tree
        x = x.getNext();
        while (x != this.first){ // go to next tree until we are at first tree again
            arr[x.getRank()]++; //add it to the right place by rank
            x = x.getNext();
        }
        int count = arr.length-1;
        for (int i = arr.length-1; i>-1; i--){ //check the real length of the array. if there are zeros at the end then we can slice the array
            if (arr[i] == 0)
                count--;
            else
                break;
        }
        if (count < arr.length) {
            int[] newArr = new int[count + 1]; //build a new array with the right size
            for (int j = 0; j < count + 1; j++) {
                newArr[j] = arr[j];
            }
            return newArr;
        }
        return arr;
    }

	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */

   public void delete(HeapNode x){
       if (this.min == x) { //if x is already the min in this heap no need to decrease key
           this.deleteMin();
           return;
       }
       int delta = -this.min.getKey() + x.getKey()+2;
       this.decreaseKey(x, delta);//decrease by infinity
       this.deleteMin(); //delete x
   }


    private void meldAndCut(HeapNode node){
        numOfCut++; //we are doing a cut
        this.numOfTrees++; // meld will add another tree ti the heap
        if (node.getNext() == node){ //if node doesn't have brothers then his parent won't have child after cut
            node.getParent().setChild(null);
        }
        else { //node has brothers
            if (node.getParent().getChild() == node){ //if nodeParent child was node then change it to the next child
                node.getParent().setChild(node.getNext());
            }
            node.getPrev().setNext(node.getNext()); //cut node from brothers circle
            node.getNext().setPrev(node.getPrev());
        }
        node.getParent().setRank(node.getParent().getRank()-1); //rank of noeParent will be decrease
        node.setParent(null); // node will become root
        HeapNode temp = this.first;
        this.first = node; // add node to the beginning of the heap
        node.setNext(temp);
        node.setPrev(temp.getPrev());
        temp.setPrev(this.first);
        this.first.getPrev().setNext(this.first);
        if (node.mark){ //node can't be marked because he is a root now
            node.setMark(false);
            this.numOfMark--;
        }
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {

        x.setKey(x.getKey()-delta); //decrease key
        if (x.getParent() != null) {//x is not root
            if (x.getParent().getKey() > x.getKey()){ //if x can't be child of his parent anymore because he os to small
                HeapNode y = x.getParent();
                this.meldAndCut(x); //cut x
                while (y != null){ //go up
                    if (y.getParent() != null) { //if y parent is not a root
                        if (!y.isMark()) { // if y is not marked then just mark it and finish
                            this.numOfMark++;
                            y.setMark(true);
                            break;
                        } else { //id y is already marked then we need to cut again
                            HeapNode temp = y;
                            y = y.getParent();
                            this.meldAndCut(temp);
                        }
                    }
                    else
                        break;
                }
            }
        }
        if (x.getKey() < this.min.getKey()) //check if x is now the minimum
            this.min = x;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.numOfTrees + 2*this.numOfMark; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {
    	return totalLinks; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() {
    	return numOfCut; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k) {
        if (k <= 0 || H == null || H.isEmpty())
            return new int[0];

        int[] minArray = new int[k];
        FibonacciHeap heapK = new FibonacciHeap(); //temp heap to keep the possible nodes that can be k minimum
        HeapNode x = H.min;
        minArray[0] = x.getKey(); //add the minimum in heap H
        HeapNode y = heapK.insert(x.getKey());
        y.setPointer(x); //keep pointer to the origin position of x in H
        for (int i = 1; i < k; i++) {
            if (heapK.min.getPointer().getChild() != null) { //go to the child of the current minimum
                x = heapK.min.getPointer().getChild();
                y = heapK.insert(x.getKey()); //add child
                y.setPointer(x);
                HeapNode temp = x;
                while (x.getNext() != temp) { //if child has brothers go to all of them
                    x = x.getNext();
                    y = heapK.insert(x.getKey()); //add to temp heap
                    y.setPointer(x); // keep pointer to the origin position of x in H
                }
            }
            heapK.deleteMin(); // delete min from temp heap- the min is already in the minAraay
            minArray[i] = heapK.min.getKey(); // add new minimum to array
        }
        return minArray;
    }

    public HeapNode getFirst() {
        return this.first;
    }


    /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        private boolean mark;
        private int rank = 0;
        private HeapNode child = null;
        private HeapNode next=null;
        private HeapNode prev = null;
        private HeapNode parent = null;
        private HeapNode pointer = null;

        public HeapNode(int key){
            this.key = key;
        }

       public HeapNode(int key, HeapNode next, HeapNode prev) {
           this.key = key;
           this.next = next;
           this.prev = prev;
       }
       public void setKey(int key){
            this.key = key;
       }
       public int getKey() {
    		return this.key;
    	}

       public boolean isMark() {
           return mark;
       }

       public void setMark(boolean mark) {
           this.mark = mark;
       }

       public int getRank() {
           return rank;
       }

       public void setRank(int rank) {
           this.rank = rank;
       }

       public HeapNode getChild() {
           return child;
       }

       public void setChild(HeapNode child) {
           this.child = child;
       }

       public HeapNode getNext() {
           return next;
       }

       public void setNext(HeapNode next) {
           this.next = next;
       }

       public HeapNode getPrev() {
           return prev;
       }

       public void setPrev(HeapNode prev) {
           this.prev = prev;
       }

       public HeapNode getParent() {
           return parent;
       }

       public void setParent(HeapNode parent) {
           this.parent = parent;
       }

        public HeapNode getPointer() {
            return pointer;
        }
        public void setPointer(HeapNode pointer) {
            this.pointer = pointer;
        }
    }


}
