
/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {

	private IAVLNode root;
	protected IAVLNode min = null;
	protected IAVLNode max = null;


	public AVLTree(IAVLNode root){
		this.root = root;
	}

	public AVLTree() {
		this.root = new AVLNode();//a virtual tree from a virtual node
	}

	public boolean empty() {
		return (!this.root.isRealNode());
	}

	private void rotateLeft(IAVLNode x){
		IAVLNode y = x.getRight();
		x.setRight(y.getLeft());
		x.getRight().setParent(x);
		y.setLeft(x);
		y.setParent(x.getParent());
		x.setParent(y);
		x.setSize();
		y.setSize();
		if (y.getParent() != null) {
			IAVLNode z = y.getParent();
			if (z.getRight() == x)
				z.setRight(y);
			else
				z.setLeft(y);
		} else
			this.root = y;
	}
	public void rotateRight(IAVLNode x) {
		IAVLNode y = x.getLeft();
		x.setLeft(y.getRight());
		x.getLeft().setParent(x);
		y.setRight(x);
		y.setParent(x.getParent());
		x.setParent(y);
		x.setSize();
		y.setSize();
		if (y.getParent() != null) {
			IAVLNode z = y.getParent();
			if (z.getRight() == x)
				z.setRight(y);
			else
				z.setLeft(y);
		} else
			this.root = y;
	}

	private void promote(IAVLNode x) {
		x.setHeight(x.getHeight()+1);
	}

	private void demote(IAVLNode x) {
		x.setHeight(x.getHeight()-1);
	}



	/**
	 * public String search(int k)
	 *
	 * Returns the info of an item with key k if it exists in the tree.
	 * otherwise, returns null.
	 */
	public String search(int k) {
		IAVLNode kNode = this.searchNode(k);
		if (kNode == null)
			return null;
		return kNode.getValue();
	}
	private IAVLNode searchNode(int k){
		if (this.empty()){//tree is empty
			return null;
		}
		IAVLNode x = this.root;
		while (x.isRealNode()){//not a virtual node
			if(x.getKey() == k)// found
				return x;
			else if(x.getKey()>k)
				x = x.getLeft();
			else
				x = x.getRight();
		}
		return null;
	}

	/**
	 * if k is a key of a node in the tree, return the node.
	 * else return the place the node with key k should be at
	 *
	 */

	private IAVLNode treePosition(int k){
		IAVLNode x = this.root;
		IAVLNode y = null;// todo check: why not a virtual node instead??
		while (x.isRealNode()) {
			y = x;
			int xKey = x.getKey();
			if (k == xKey)
				return x;
			else if (k < xKey)
				x = x.getLeft();
			else
				x = x.getRight();
		}
		return y;
	}


	/**
	 * public int insert(int k, String i)
	 *
	 * Inserts an item with key k and info i to the AVL tree.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {

		if (this.empty()) { //if tree is empty then insert node to root
			this.root = new AVLNode(k, i);
			this.root.setSize();
			this.root.setHeight(0);
			this.root.getRight().setParent(this.root);
			this.root.getLeft().setParent(this.root);
			this.min = root;
			this.max = root;
			return 0;
		}

		int numOfOperations = 0;
		AVLNode newNode = new AVLNode(k,i);
		newNode.setSize();
		newNode.setHeight(0);//height is set to 0
		AVLNode y = (AVLNode) treePosition(k);//look for a plce to insert newNode
		newNode.getLeft().setParent(newNode); //newNodeLeft and newNodeRight are virtual nodes, make sure they are connectef to newNode
		newNode.getRight().setParent(newNode);
		int yKey = y.getKey();
		if (k == yKey)
			return -1; //Already in the tree
		if (k < yKey)
			y.setLeft(newNode); // insert newNode to the left
		else
			y.setRight(newNode); // insert newNode to the right

		newNode.setParent(y);


		//min,max
		if (newNode.getKey() < this.min.getKey())
			this.min = newNode;
		if (newNode.getKey() > this.max.getKey())
			this.max = newNode;

		///////////////////end of regular insertion! now rebalanced //////////////////


		while (y != null){ //go up to the root
			y.setSize();
			int balanceR = y.getHeight() - y.getRight().getHeight(); //difference of height between y and his right side
			int balanceL = y.getHeight()- y.getLeft().getHeight(); // difference of height between y and his left side

			if ((balanceL == 1 && balanceR == 1) || (balanceL == 1 && balanceR ==2) || (balanceL == 2 && balanceR == 1)) {//no need to do anything
				y = (AVLNode) y.getParent();
				break; // tree is balanced
			}

			else if ((balanceL == 0 && balanceR == 1)||(balanceL == 1 && balanceR == 0)){//case 1
				promote(y);
				numOfOperations++;
			}
			else{
				numOfOperations += reBalanceInsert(y, balanceR, balanceL);
				y = (AVLNode) y.getParent();
				break; //after reBalanceInsert func the tree is balanced
			}
			y = (AVLNode) y.getParent();

		}
		while(y != null){ // update size all the way up
			y.setSize();
			y = (AVLNode) y.getParent();
		}

		return numOfOperations;
	}



	public int reBalanceInsert(IAVLNode x, int balanceR,int balanceL ){
		if (balanceL == 0 && balanceR== 2) {
			IAVLNode xLeft = x.getLeft();
			int balanceL2 = xLeft.getHeight()- xLeft.getLeft().getHeight();
			int balanceR2 = xLeft.getHeight() - xLeft.getRight().getHeight();
			if (balanceL2 == 1 && balanceR2 ==2) { //case 2
				rotateRight(x);
				demote(x);
				return 2; // 2 operations
			}
			else {// case 3
				rotateLeft(xLeft);
				rotateRight(x);
				demote(xLeft); demote(x);
				promote(xLeft.getParent());
				return 5; // 5 operations
			}
		}

		else if (balanceL == 2 && balanceR== 0){// other side
			IAVLNode xRight = x.getRight();
			int balanceL2 = xRight.getHeight()- xRight.getLeft().getHeight();
			int balanceR2 = xRight.getHeight() - xRight.getRight().getHeight();
			if (balanceL2 == 2 && balanceR2 ==1) {//case 2
				rotateLeft(x);
				demote(x);
				return 2;// 2 operations
			}
			else {// case 3
				rotateRight(xRight);
				rotateLeft(x);
				demote(xRight); demote(x);
				promote(xRight.getParent());
				return 5; // 5 operations
			}
		}
		return 0;
	}


	/**
	 * public int delete(int k)
	 *
	 * Deletes an item with key k from the binary tree, if it is there.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		int numOfOperations = 0;
		IAVLNode nodeToDelete =  this.treePosition(k);//todo check

		if (nodeToDelete == null || nodeToDelete.getKey() != k)
			return -1;//node was not found

		IAVLNode parentOfPhysicalDeletedNode = deleteHelper(nodeToDelete);//the first node we want to rebalance


		while (parentOfPhysicalDeletedNode != null){
			numOfOperations += reBalanceDelete(parentOfPhysicalDeletedNode);
			// todo check - important :
			//todo //parentOfPhysicalDeletedNode.setHeight(1 +Math.max(parentOfPhysicalDeletedNode.getLeft().getHeight(), parentOfPhysicalDeletedNode.getRight().getHeight()));
			parentOfPhysicalDeletedNode.setSize();//todo check if that's the right place for it
			parentOfPhysicalDeletedNode = parentOfPhysicalDeletedNode.getParent();//going up to the Root
		}

		//min,max todo check if not before the while loop (after the deletion is happening)
		if (this.empty()){//empty/1 node tree
			this.min = root;
			this.max = root;
		}
		else{//TODO OOOOOOOOO change
			if (this.min == nodeToDelete) {
				this.min = nodeToDelete.getSuccessor();
			}
			if (this.max == nodeToDelete) {
				this.max = nodeToDelete.getPredecessor();
			}
		}

		return numOfOperations;
	}


	private IAVLNode deleteHelper(IAVLNode nodeToDelete){
		IAVLNode nodeToDeleteParent = nodeToDelete.getParent();
		IAVLNode firstToRebalance = new AVLNode();

		if ((!nodeToDelete.getLeft().isRealNode()) || (!nodeToDelete.getRight().isRealNode())){//unary node / a Leaf
			IAVLNode newSon = new AVLNode();

			if (nodeToDelete.getLeft().isRealNode()) {//left unary node
				newSon = nodeToDelete.getLeft();
				newSon.setSize();
			}
			else if (nodeToDelete.getRight().isRealNode()) {// right unary node
				newSon = nodeToDelete.getRight();
				newSon.setSize();//
			}
			// else (a Leaf) :  newSon will be a virtual node


			if (nodeToDelete.equals(this.getRoot())){
				this.root = newSon;
				this.root.setParent(null);
			}
			else {
				if (newSon.isRealNode())
					newSon.setParent(nodeToDeleteParent);


				if (nodeToDeleteParent.getRight().equals(nodeToDelete)) {//nodeToDelete is a rightSon
					nodeToDeleteParent.setRight(newSon);//we bypass nodeToDelete
					newSon.setParent(nodeToDeleteParent);
				}
				else { //nodeToDelete is a rightSon
					nodeToDeleteParent.setLeft(newSon);//we bypass nodeToDelete
					newSon.setParent(nodeToDeleteParent);
				}
					nodeToDeleteParent.setSize();
			}
			firstToRebalance = nodeToDeleteParent;
		}
		else if ((nodeToDelete.getLeft().isRealNode()) && (nodeToDelete.getRight().isRealNode())){// a node with 2 sons
			IAVLNode successor = nodeToDelete.getSuccessor();
			IAVLNode successorParent = successor.getParent();


			if (successorParent.getRight().equals(successor)) //successor is a rightSon, bypass it
				successorParent.setRight(successor.getRight());


			else  //successor is not a direct son of nodeToDelete
				successorParent.setLeft(successor.getRight());

			successor.getRight().setParent(successorParent);//because successor will be gone soon


			successor.setRight(nodeToDelete.getRight());//connecting successor with nodeToDelete rightSon
			nodeToDelete.getRight().setParent(successor);

			successor.setLeft(nodeToDelete.getLeft());//connecting successor with nodeToDelete leftSon
			nodeToDelete.getLeft().setParent(successor);


			successor.setParent(nodeToDelete.getParent());//connecting successor with nodeToDeleteParent
			if (nodeToDeleteParent != null){//not the root
				if (nodeToDeleteParent.getLeft() == nodeToDelete)//nodeToDelete is a leftSon
					nodeToDeleteParent.setLeft(successor);
				else //nodeToDelete is a rightSon
					nodeToDeleteParent.setRight(successor);
				nodeToDeleteParent.setSize();
			}

			successor.setHeight(1 + Math.max(successor.getLeft().getHeight(), successor.getRight().getHeight()));
			successor.setSize();
			successorParent.setSize();
			nodeToDelete.setSize();

			if (this.getRoot() == nodeToDelete){//if deleted the root with 2 sons
				this.root = successor;
				this.root.setParent(null);
				this.root.setSize();
			}

			if (successorParent == nodeToDelete)
				firstToRebalance = successor;

			else
				firstToRebalance = successorParent;

			firstToRebalance.setSize();
		}
		return firstToRebalance;
	}

	public int reBalanceDelete(IAVLNode x){
		int balanceL = x.getHeight() - x.getLeft().getHeight();
		int balanceR = x.getHeight() - x.getRight().getHeight();

		if (balanceL == 2 && balanceR == 2) {//case 1
			demote(x);
			return 1;
		}

		if (balanceL == 3 && balanceR == 1){//cases 2,3,4
			IAVLNode xRight = x.getRight();
			int balanceL2 = xRight.getHeight()- xRight.getLeft().getHeight();
			int balanceR2 = xRight.getHeight() - xRight.getRight().getHeight();

			if (balanceL2 == 1 &&balanceR2 == 2){//case 4, xRight is (1,2)
				demote(xRight);
				demote(x); demote(x);//demote Z twice (x2)
				promote(xRight.getLeft());
				rotateRight(xRight);
				rotateLeft(x);
				return 5;
			}
			else if (balanceL2 == 1 && balanceR2 == 1) {//case 2 (xRight is  (1,1))
				demote(x);
				promote(xRight);
				rotateLeft(x);
				return 3;
			}
			else{//case 3 (xRight is  (2,1))
				demote(x);demote(x);//x2!
				rotateLeft(x);
				return 2;
			}
		}

		else if (balanceL == 1 && balanceR == 3){//other side, cases 2',3',4'
			IAVLNode xLeft = x.getLeft();
			int balanceL2 = xLeft.getHeight()- xLeft.getLeft().getHeight();
			int balanceR2 = xLeft.getHeight() - xLeft.getRight().getHeight();
			if (balanceL2 == 2 && balanceR2 == 1){//case 4 // todo check of right and left
				demote(xLeft);
				demote(x); demote(x);//demote Z twice (x2)
				promote(xLeft.getRight());
				rotateLeft(xLeft);
				rotateRight(x);
				return 5;
			}
			else if (balanceL2 == 1 && balanceR2 == 1) {// case 2,xLeft is (1,1)
				promote(xLeft);
				demote(x);
				rotateRight(x);
				return 3;
			}
			else {// case 3,xLeft is (1,2)
				demote(x);demote(x);//x2!
				rotateRight(x);
				return 2;
			}
		}
		return 0;
	}
	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty.
	 */

	public String min() {
		if (this.min == null)
			return null;
		return this.min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty.
	 */
	public String max() {
		if (this.max == null)
			return null;
		return this.max.getValue();
	}

	private IAVLNode[] toArray(){//array of IAVLNodes
		if (this.empty())
			return new IAVLNode[0];
		IAVLNode[] inOrder = new IAVLNode[((AVLNode)root).getSize()]; // sorted array
		IAVLNode[] temp = new IAVLNode[((AVLNode)root).getSize()];// will use this array as a Stack, size- number of inOrder in tree
		int i = 0; // inOrder index
		int j = 1; // temp index
		IAVLNode x = this.root;
		temp[0] = this.root;
		while (i<inOrder.length && x.isRealNode()){
			while (x.getLeft().isRealNode()){ // if possible, go left
				x = x.getLeft();
				temp[j] = x;
				j++;
			}
			//no more left
			inOrder[i] = x;// add the most left node that is not in the inOrder array yet
			i++;
			temp[j-1] = null;//remove left from temp
			j = j-1;

			if (i==inOrder.length) // check if all the inOrder are in the inOrder array
				break;
			x = x.getRight(); // try to go right
			while (!x.isRealNode() && j!=0){ // if there is no right node to x then go to his parent
				x = temp[j - 1]; // get parent
				inOrder[i] = x; // add parent
				i++;
				temp[j - 1] = null;// remove parent from temp
				j--;
				x = x.getRight(); // try again to go right
			}
			temp[j] = x;//add right to temp array and start again the proses
			j++;
		}
		return inOrder;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray() {
		if (this.empty()) // check if empty tree
			return new int[0];
		IAVLNode[] nodes = this.toArray(); //use IAVLNodes array, this array is sorted
		int[] keys = new int[nodes.length];
		for (int i = 0; i<nodes.length; i++){//get each key for sorted array
			keys[i] = nodes[i].getKey();
		}
		return keys;
	}


	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */

	public String[] infoToArray() {
		if (this.empty())
			return new String[0];
		IAVLNode[] nodes = this.toArray();//use IAVLNodes array, this array is sorted
		String[] values = new String[nodes.length];
		for (int i = 0; i<nodes.length; i++){//get each values for sorted array
			values[i] = nodes[i].getValue();
		}
		return values;
		}


	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 */
	public int size() {
		return ((AVLNode)this.root).getSize(); // to be replaced by student code
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public AVLTree[] split(int x)
	 *
	 * splits the tree into 2 trees according to the key x.
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 *
	 * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	 * postcondition: none
	 */
	public AVLTree[] split(int x) {
		IAVLNode xNode = this.searchNode(x); // look for node in tree with key x
		if (xNode ==null){ //if x is not in the tree then add it
			this.insert(x, null);
			xNode = this.searchNode(x);
		}
		AVLTree l = new AVLTree(xNode.getLeft()); // the tree with nodes < x
		AVLTree r = new AVLTree(xNode.getRight());// tree with nodes > x

		while(xNode.getParent() != null){// go all the way up
			xNode = xNode.getParent();
			if(xNode.getKey() < x){ // if node < x then add it to the left tree
				AVLTree addL = new AVLTree(xNode.getLeft());
				addL.root.setParent(null);
				IAVLNode addNode = new AVLNode(xNode.getKey(),xNode.getValue());
				l.join(addNode,addL); // join the trees (it rotates the join tree to be AVLTREE)
			}
			else { // is node > x add it with his right child to the right tree
				AVLTree addR = new AVLTree(xNode.getRight());
				addR.root.setParent(null);
				IAVLNode addNode = new AVLNode(xNode.getKey(),xNode.getValue());
				r.join(addNode,addR); // join the trees (it rotates the join tree to be AVLTREE)
			}
		}
		l.root.setParent(null); //disconnect root from previous parent
		r.root.setParent(null);
		l.min = l.searchMin(); // search new min and max per tree
		l.max = l.searchMax();
		r.min = r.searchMin();
		r.max = r.searchMax();
		AVLTree[] twoSplits = {l,r};
		return twoSplits;
	}

	private IAVLNode searchMin() { // search min for split
		if(this.empty())
			return null;
		IAVLNode x = this.root;
		while (x.getLeft().isRealNode()){//go all the way left
			x = x.getLeft();
		}
		return x;
	}

	private IAVLNode searchMax() { // search max for split
		if(this.empty())
			return null;
		IAVLNode x = this.root;
		while (x.getRight().isRealNode()){//go all the way right
			x = x.getRight();
		}
		return x;
	}

	/**
	 * public int join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree.
	 * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	 *
	 * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
	 * postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t){
		if (t == null){ //if other tree is null then create new empty tree
			t = new AVLTree();
		}
		AVLTree t2;
		AVLTree t1;
		//check which tree is the small and which is the bigger
		if (this.root.getKey() > x.getKey() || (t.root.getKey() < x.getKey() && this.root.getKey() == -1)){ //this is the bigger
			t2 = this;
			t1 = t;
		}
		else { // t is the bigger
			t1 = this;
			t2 = t;
		}
		// update min and max
		// if one of the trees is empty then update the min/max to be x
		if (t1.min == null){
			t1.min = x;

		}
		if (t2.max == null){
			t2.max = x;
		}
		t1.max = t2.max;
		t2.min = t1.min;

		//keys(t1) < x < keys(t2)
		int t2Height = t2.root.getHeight();
		int t1Height = t1.root.getHeight();
		IAVLNode c;
		if (t2Height == t1Height){ //trees have  the same height
			x.setRight(t2.root);
			x.setLeft(t1.root);
			t1.root.setParent(x);
			t2.root.setParent(x);
			t1.root = x;
			t2.root = x;
			((AVLNode)x).setHeight();
			((AVLNode)x).setSize();
			return 1;
		}
		int count = 0;
		if (t2Height > t1Height){//t2 tree is higher
			IAVLNode b =  t2.root;
			while (b.getHeight() > t1Height && b.isRealNode()) { //find node with height like t.root
					b = b.getLeft();//our b node
					count++;
			}
			c = b.getParent();
			x.setRight(b);
			c.setLeft(x);
			b.setParent(x);
			x.setParent(c);
			x.setLeft(t1.root);
			t1.root.setParent(x);
			((AVLNode)x).setHeight();
			((AVLNode)x).setSize();
			t1.root = t2.root;
		}
		else {//t1 tree is higher
			IAVLNode b = t1.root;
			while (b.getHeight() > t2Height && b.isRealNode()) { //find node with height like this.root
					b = b.getRight();//our b node
					count++;
			}
			c = b.getParent();
			x.setLeft(b);
			c.setRight(x);
			b.setParent(x);
			x.setParent(c);
			x.setRight(t2.root);
			t2.root.setParent(x);
			t2.root = t1.root;
			((AVLNode) x).setHeight();
			x.setSize();
		}
		//finished the connection between the trees, now rebalanced the join tree
		if (c.getHeight() == x.getHeight()+1) { //case 1 - difference in height is 1, no need to rebalanced
			while (c != null){ // update size
				c.setSize();
				c = c.getParent();
			}
		}
		else { //else, rebalanced and update size
			while (c != null) {
				c.setSize();
				int balanceR = c.getHeight() - c.getRight().getHeight();//edge of y and his leftSon
				int balanceL = c.getHeight() - c.getLeft().getHeight();
				if ((balanceL == 1 && balanceR == 1) || (balanceL == 1 && balanceR == 2) || (balanceL == 2 && balanceR == 1)) {//no need to do anything
					//no need to do rebalanced
					c = c.getParent();
					continue;
				}
				else if ((balanceL == 0 && balanceR == 1) || (balanceL == 1 && balanceR == 0)) {
					promote(c);
				}
				else
					reBalanceJoin(c, balanceR, balanceL);
				c = c.getParent();
			}
			t.root = this.root;// make sure after all th changes the root is still the same

		}
		return count+1;
	}




	public void reBalanceJoin(IAVLNode x ,int balanceR, int balanceL ){
		if (balanceL == 0 && balanceR== 2) {
			IAVLNode xLeft = x.getLeft();
			int balanceL2 = xLeft.getHeight()- xLeft.getLeft().getHeight();
			int balanceR2 = xLeft.getHeight() - xLeft.getRight().getHeight();
			if (balanceL2 == 1 && balanceR2 ==2) { //case 2
				rotateRight(x);
				demote(x);
			}
			else if (balanceL2 == 2 && balanceR2 ==1){// case 3
				rotateLeft(xLeft);
				rotateRight(x);
				demote(xLeft); demote(x);
				promote(xLeft.getParent());
			}
			else if (balanceL2 == 1 && balanceR2 ==1){ // special case join
				rotateRight(x);
				promote(xLeft);
			}
		}

		else if (balanceL == 2 && balanceR== 0){// other side
			IAVLNode xRight = x.getRight();
			int balanceL2 = xRight.getHeight()- xRight.getLeft().getHeight();
			int balanceR2 = xRight.getHeight() - xRight.getRight().getHeight();
			if (balanceL2 == 2 && balanceR2 ==1) {//case 2
				rotateLeft(x);
				demote(x);
			}
			else if (balanceL2 == 1 && balanceR2 ==2){// case 3 =
				rotateRight(xRight);
				rotateLeft(x);
				demote(xRight); demote(x);
				promote(xRight.getParent());
			}
			else if (balanceL2 == 1 && balanceR2 ==1){ // special case join
				rotateLeft(x);
				promote(xRight);
			}
		}
	}


	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
		public void setHeight(int height); // Sets the height of the node.
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public void setSize();
		public int getSize();
		//public IAVLNode getSuccessorOfRightChild();
		public IAVLNode getSuccessor();
		public IAVLNode getPredecessor();


	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in another file.
	 *
	 * This class can and MUST be modified (It must implement IAVLNode).
	 */
	public class AVLNode implements IAVLNode {
		private int key = -1;
		private String value = null;
		private int height = -1;
		private AVLNode left;
		private AVLNode right;
		private AVLNode parent = null;//todo check about that
		private int size = 0;

		public AVLNode(int key, String value) {
			this.key = key;
			this.value = value;
			this.left = new AVLNode();
			this.right = new AVLNode();
			this.parent =  null;
			this.height = 0;
		}

		public AVLNode() {//virtual node constructor
			this.left = null;
			this.right = null;
		}

		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}

		public void setLeft(IAVLNode node) {
			this.left = (AVLNode) node;
		}

		public IAVLNode getLeft() {
			return this.left;
		}

		public void setRight(IAVLNode node) {
			this.right = (AVLNode) node;
		}

		public IAVLNode getRight() {
			return this.right;
		}

		public void setParent(IAVLNode node) {
			this.parent = (AVLNode) node;
		}

		public IAVLNode getParent() {
			return this.parent;
		}

		public boolean isRealNode() {
			if (this.key == -1)
				return false;
			return true;
		}

		public void setHeight(int height) {
			this.height = height;
		}
		public void setHeight(){ // TODO  if used????
			int maxH = Math.max(this.left.getHeight(), this.right.getHeight());
			this.height = maxH +1;
		}
		public int getHeight() {
			return this.height;
		}
		public int getSize() {
			return this.size;
		}
		public void setSize() {
				this.size = (this.getLeft().getSize() + this.getRight().getSize()) + 1;
		}


		public IAVLNode getSuccessor() {
			IAVLNode x = this;
			if (this.getRight().isRealNode()){
				x = this.getRight();
				while (x.getLeft().isRealNode())//go all the way left
					x = x.getLeft();
				return x;
			}

			else{//does not have a rightSon
				IAVLNode xParent = x.getParent();
				while (xParent != null && x == xParent.getRight()) {//todo check of while-do loop
					x = xParent;
					xParent = xParent.getParent();
				}
				return xParent;
			}
		}



		public IAVLNode getPredecessor() {
			IAVLNode x = this;
			if (this.getLeft().isRealNode()){
				x = this.getLeft();
				while (x.getRight().isRealNode())//go all the way left
					x = x.getRight();
				return x;
			}

			else{//does not have a leftSon
				IAVLNode xParent = x.getParent();
				while (xParent != null && x == xParent.getLeft()) {//todo check of while-do loop
					x = xParent;
					xParent = xParent.getParent();
				}
				return xParent;
			}
		}

	}
}


  
