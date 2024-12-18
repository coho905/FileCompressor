import java.util.*;
import java.io.*;

/**
 * Generic binary tree, storing data of a parametric data in each node
 */

public class BinaryTree<E> {
    protected BinaryTree<E> left, right;	// children; can be null
    E data;

    /**
     * Constructs leaf node -- left and right are null
     */
    public BinaryTree(E data) {
        this.data = data; this.left = null; this.right = null;
    }


    public BinaryTree<E> copyToDepth(int d){
        BinaryTree<E> copy = new BinaryTree<E>(this.data);
        if( d==0){
            return copy;
        }

       if (hasLeft()){
           copy.left = this.left.copyToDepth(d-1);
       }
       if (hasRight()) {
           copy.right = this.right.copyToDepth(d-1);
       }

        return copy;
    }
    /**
     * Constructs inner node
     */
    public BinaryTree(E data, BinaryTree<E> left, BinaryTree<E> right) {
        this.data = data; this.left = left; this.right = right;
    }

    public void traverse() {
        System.out.println(data);
        if (hasLeft()) left.traverse();
        if (hasRight()) right.traverse();
    }


    /**
     * Is it an inner node?
     */
    public boolean isInner() {
        return left != null || right != null;
    }

    /**
     * Is it a leaf node?
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * Does it have a left child?
     */
    public boolean hasLeft() {
        return left != null;
    }


    /**
     * @author Colin and Gabe
     * ALlOWS US TO UPDATE THE CHILD
     */
    public void setLeft(BinaryTree<E> leftSet) {
        left = leftSet;
    }

    public void setRight(BinaryTree<E> rightSet) {
        right = rightSet;
    }


    /**
     * Does it have a right child?
     */
    public boolean hasRight() {
        return right != null;
    }

    public BinaryTree<E> getLeft() {
        return left;
    }

    public BinaryTree<E> getRight() {
        return right;
    }

    public E getData() {
        return data;
    }

    /**
     * Number of nodes (inner and leaf) in tree
     */
    public int size() {
        int num = 1;
        if (hasLeft()) num += left.size();
        if (hasRight()) num += right.size();
        return num;
    }

    /**
     * Longest length to a leaf node from here
     */
    public int height() {
        if (isLeaf()) return 0;
        int h = 0;
        if (hasLeft()) h = Math.max(h, left.height());
        if (hasRight()) h = Math.max(h, right.height());
        return h+1;						// inner: one higher than highest child
    }

    /**
     * Same structure and data?
     */
    public boolean equalsTree(BinaryTree<E> t2) {
        if (hasLeft() != t2.hasLeft() || hasRight() != t2.hasRight()) return false;
        if (!data.equals(t2.data)) return false;
        if (hasLeft() && !left.equalsTree(t2.left)) return false;
        if (hasRight() && !right.equalsTree(t2.right)) return false;
        return true;
    }

    /**
     * Leaves, in order from left to right
     */
    public ArrayList<E> fringe() {
        ArrayList<E> f = new ArrayList<E>();
        addToFringe(f);
        return f;
    }

    /**
     * Helper for fringe, adding fringe data to the list
     */
    private void addToFringe(ArrayList<E> fringe) {
        if (isLeaf()) {
            fringe.add(data);
        }
        else {
            if (hasLeft()) left.addToFringe(fringe);
            if (hasRight()) right.addToFringe(fringe);
        }
    }

    /**
     * Returns a string representation of the tree
     */
    public String toString() {
        return toStringHelper("");
    }

    /**
     * Recursively constructs a String representation of the tree from this node,
     * starting with the given indentation and indenting further going down the tree
     */
    public String toStringHelper(String indent) {
        String res = indent + data + "\n";
        if (hasLeft()) res += left.toStringHelper(indent+"  ");
        if (hasRight()) res += right.toStringHelper(indent+"  ");
        return res;
    }

    /**
     * Very simplistic binary tree parser based on Newick representation
     * Assumes that each node is given a label; that becomes the data
     * Any distance information (following the colon) is stripped
     * <tree> = "(" <tree> "," <tree> ")" <label> [":"<dist>]
     *        | <label> [":"<dist>]
     * No effort at all to handle malformed trees or those not following these strict requirements
     */
    public static BinaryTree<String> parseNewick(String s) {
        BinaryTree<String> t = parseNewick(new StringTokenizer(s, "(,)", true));
        // Get rid of the semicolon
        t.data = t.data.substring(0,t.data.length()-1);
        return t;
    }

    /**
     * Does the real work of parsing, now given a tokenizer for the string
     */
    public static BinaryTree<String> parseNewick(StringTokenizer st) {
        String token = st.nextToken();
        if (token.equals("(")) {
            // Inner node
            BinaryTree<String> left = parseNewick(st);
            String comma = st.nextToken();
            BinaryTree<String> right = parseNewick(st);
            String close = st.nextToken();
            String label = st.nextToken();
            String[] pieces = label.split(":");
            return new BinaryTree<String>(pieces[0], left, right);
        }
        else {
            // Leaf
            String[] pieces = token.split(":");
            return new BinaryTree<String>(pieces[0]);
        }
    }

    /**
     * Slurps the entire file into a single String, and returns it
     */
    public static String readIntoString(String filename) throws IOException {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = in.readLine()) != null) buff.append(line);
        in.close();
        return buff.toString();
    }

    /**
     * Some tree testing
     */
    public static void main(String[] args) throws IOException {
        //manually build a tree
        BinaryTree<String> root = new BinaryTree<String>("G");
        root.left = new BinaryTree<String>("B");
        root.right = new BinaryTree<String>("F");
        BinaryTree<String>temp = root.left;
        temp.left = new BinaryTree<String>("A");
        temp.right = new BinaryTree<String>("C");
        temp = root.right;
        temp.left = new BinaryTree<String>("D");
        temp.right = new BinaryTree<String>("E");

        System.out.println(root);

        BinaryTree copy = root.copyToDepth(0);
        System.out.println(copy);
        System.out.println(root);

    }
}
