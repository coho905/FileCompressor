import java.io.*;
import java.util.*;
/**
 * CS10 -- October 20, 2023
 * Problem Set 3 -- Huffman Encoding
 *
 * @author Colin Wolfe
 *
 * IMPORTANT - we made setLeft() and setRight() methods in BinaryTree.Java, this code will not work without them being implemented
 */

public class HuffmanCompression{


    // created an instance variable that keeps track of an element's 0/1 path in a String
    protected StringBuilder codeString = new StringBuilder();
    /**
     * The countFrequencies method will take a String and return a map that contains all the
     * characters in the string and the number of times each character occurs.
     *
     */
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map<Character, Long> tempMap = new HashMap<Character, Long>(); //creates empty hashmap
        BufferedReader input = new BufferedReader(new FileReader(pathName)); //creates a filereader
        int temp = input.read(); //reads first character
        while(temp!=-1){ //while there is a valid character to read
            if(tempMap.containsKey((char) temp)){ //if character already appeared
                tempMap.put((char) temp, tempMap.get((char) temp)+1); //increase frequency by 1
            }
            else{
                tempMap.put((char) temp,1L); //put it in the map for the first time
            }
            temp = input.read(); //read another character
        }

        input.close(); //close reader
        return tempMap; //return frequency map
    }

    /**
     * Construct a code tree from a map of frequency counts. Note: this code should handle the special
     * cases of empty files or files with a single character.
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return the code tree.
     */
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        if (frequencies.isEmpty()) { //if no characters appeared in file
            return null; //return null
        }
        if (frequencies.size() == 1) { //if only one character appeared (regardless of size)
            Set<Character> keys = frequencies.keySet(); //get the keySet
            ArrayList<Character> keysList = new ArrayList<Character>(keys); //ArrayList of the keys
            BinaryTree<CodeTreeElement> temporary= new BinaryTree<CodeTreeElement>(new CodeTreeElement(frequencies.get(keysList.get(0)), null)); //creates a root binary tree(empty char and frequency of the only character)
            temporary.right = new BinaryTree<>(new CodeTreeElement(frequencies.get(keysList.get(0)), keysList.get(0))); //sets right child of the binary tree to the be the character and the frequency
            temporary.left = new BinaryTree<>(new CodeTreeElement(null, null)); //set left child to a null node
            return temporary; //return this tree
        }
        else{ //more than one character in the frequency map
            Set<Character> keys = frequencies.keySet(); //get the keyset
            ArrayList<Character> keysList = new ArrayList<Character>(keys); //arraylist of keys
            PriorityQueue<BinaryTree<CodeTreeElement>> storage2 = new PriorityQueue<BinaryTree<CodeTreeElement>>(new Comparator<BinaryTree<CodeTreeElement>>() { //make a min priority queue with a custom inline comparator
                public int compare(BinaryTree<CodeTreeElement> o1, BinaryTree<CodeTreeElement> o2) { //min priority
                    if (o1.getData().getFrequency() >= o2.getData().getFrequency()) {
                        return 1;
                    }
                    return -1;
                }
            });
            for (int i = 0; i < keysList.size(); i++) { //for each key in keyList
                BinaryTree<CodeTreeElement> returner = new BinaryTree<CodeTreeElement>(new CodeTreeElement(frequencies.get(keysList.get(i)), keysList.get(i)));
                storage2.add(returner); //add individual node binary trees to the queue
            }
            while (storage2.size() > 1) { //while the queue's size is greater than 1
                BinaryTree<CodeTreeElement> e1 = storage2.remove(); //remove smallest
                BinaryTree<CodeTreeElement> e2 = storage2.remove(); //remove second smallest
                BinaryTree<CodeTreeElement> e5 = new BinaryTree<CodeTreeElement>(new CodeTreeElement(e1.getData().getFrequency() + e2.getData().getFrequency(), Character.MIN_VALUE)); //create a null binary tree node with the frequency of e1+e2
                //MUST CHANGE
                e5.setLeft(e1); // setLeft method was made by us in the Binary Tree class
                e5.setRight(e2); // setRight method was made by us in the Binary Tree class
                storage2.add(e5); //adds binary tree
            }
            return storage2.remove(); //returns the last binary tree in there
        }
    }

    public String codeTreePrint(BinaryTree<CodeTreeElement> codeTree) {
        return codeTreePrintHelper(codeTree, "");
    }  //prints the Tree in a better way
    public String codeTreePrintHelper(BinaryTree<CodeTreeElement> codeTree, String Indent) { //helper method (Based on the Binary Tree Class)
        String res = Indent + codeTree.getData().getChar() + ": " +  codeTree.getData().getFrequency() + "\n";
        if (codeTree.hasLeft()) res += codeTreePrintHelper(codeTree.getLeft(), Indent + "  ");
        if (codeTree.hasRight()) res += codeTreePrintHelper(codeTree.getRight(), Indent + "  ");
        return res;
    }


    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) { // computes codes for each character
        Map<Character, String> returner = new HashMap<Character, String>(); //empty hashMap
        addToComputeCodesHelper(codeTree, returner); //call helper method
        codeString = new StringBuilder(); //restores the codeString
        return returner; //returns hashMap
    }

    /**
     *
     * @param codeTree
     * @param returner
     * Helps add codes to the hashMap from the given codeTree
     */
    private void addToComputeCodesHelper(BinaryTree<CodeTreeElement> codeTree, Map<Character, String> returner) {
        if(codeTree!= null){ //if the codeTree is not null
            if (codeTree.hasLeft() || codeTree.hasRight()) { //if it has either child
                if (codeTree.hasLeft()){ //if it has a left child
                    codeString.append('0'); //append 0 to path code
                    addToComputeCodesHelper(codeTree.getLeft(), returner); //call recursively
                    if(!codeString.isEmpty()){ //if the codeString is not empty
                        codeString.deleteCharAt(codeString.length()-1); //remove last index
                    }
                }
                if (codeTree.hasRight()){ //same process as above but for right child
                    codeString.append('1');
                    addToComputeCodesHelper(codeTree.getRight(), returner);
                    if(!codeString.isEmpty()){
                        codeString.deleteCharAt(codeString.length()-1);
                    }
                }
            }
            else { //if the node is a leaf
                returner.put(codeTree.getData().getChar(), codeString.toString()); //put the leaf in the hashMap with the given code
            }
        }

    }
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException { //compresses a file
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName); //new bitwriter
        BufferedReader input = new BufferedReader(new FileReader(pathName)); //new bufferedreader
        int temp = input.read(); //get first character
        while(temp!=-1){ //while there are characters to do
            String tempCodeString = codeMap.get((char) temp); //get the code for the character
            for(char bit: tempCodeString.toCharArray()){ //for each 0 or 1 in the code
                boolean king = bit=='1';
                if(king){ //if bit is a 1
                    bitOutput.writeBit(true); //write out true
                }
                else{ //if bit is a 0
                    bitOutput.writeBit(false); //write out false
                }
            }
            temp = input.read(); //read a new character and repeat
        }
        bitOutput.close(); //close bitwriter
        input.close(); //close buffered reader
    }


    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException { //decompresses a file
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName); // new bitInput reader
        BinaryTree<CodeTreeElement> tempCodeTree = codeTree; //new temporary binary tree initially set to the codeTree element
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName)); // new buffered writer
        while (bitInput.hasNext()) { //while there are bits to read
            boolean bit = bitInput.readBit(); //read the bit
            if(!bit){ //if bit is 0
                tempCodeTree = tempCodeTree.getLeft(); //go to the left child
            }
            else{ //if bit is 1
                tempCodeTree = tempCodeTree.getRight(); //go to the right child
            }
            if(!tempCodeTree.hasLeft() && !tempCodeTree.hasRight()){ //if this node is a leaf
                output.write(tempCodeTree.getData().getChar()); //write the character associated with this position
                tempCodeTree = codeTree; //reset the value of tempCodeTree to the root
            }
        }
        output.close(); //close writer
        bitInput.close(); //close reader
    }



    public void runHuffman(String pathName){ //method to do all the running
        try{
            String pathNameCompressed = pathName.substring(0, pathName.indexOf(".txt") ).trim() + "_compressed.txt";  //gets the name before the .txt, then adds _compressed.txt
            String pathNameDecompressed = pathName.substring(0, pathName.indexOf(".txt") ).trim() + "_decompressed.txt";  //gets the name before the .txt, then adds _decompressed.txt
            compressFile((HashMap<Character, String>) computeCodes(makeCodeTree((HashMap<Character, Long>) countFrequencies(pathName))), pathName, pathNameCompressed); //runs the compress file for the user
            System.out.println("compressed");
            decompressFile(pathNameCompressed, pathNameDecompressed, makeCodeTree((HashMap<Character, Long>) countFrequencies(pathName))); //runs the decompress file for the user
            System.out.println("decompressed");
        } catch (Exception e) { //if there is an exception in the call above
            System.out.println(e); //print the exception
            System.out.println("Try asking for an existing file in the PS3 folder"); //reminds the user of this important runtime fact
        }
    }

    public static void main(String[] args) throws IOException {
/**
 * these are a bunch of individual test cases for USConstitution, testCaseEmpty, and singleChar

        HuffmanPS3 test = new HuffmanPS3();
        HashMap<Character, Long> test2 = (HashMap<Character, Long>) test.countFrequencies("PS3/USConstitution.txt");
        HashMap<Character, String> test3= (HashMap<Character, String>) test.computeCodes(test.makeCodeTree(test2));
        test.compressFile(test3, "PS3/USConstitution.txt", "PS3/USConstitution_compressed.txt");
        test.decompressFile("PS3/USConstitution_compressed.txt", "PS3/USConstitution_decompressed.txt", test.makeCodeTree(test2));

        HuffmanPS3 test4 = new HuffmanPS3();
        HashMap<Character, Long> test5 = (HashMap<Character, Long>) test4.countFrequencies("PS3/testCaseEmpty.txt");
        HashMap<Character, String> test6= (HashMap<Character, String>) test4.computeCodes(test4.makeCodeTree(test5));

        test4.compressFile(test6, "PS3/testCaseEmpty.txt", "PS3/testCaseEmpty_compressed.txt");
        test4.decompressFile("PS3/testCaseEmpty_compressed.txt", "PS3/testCaseEmpty_decompressed.txt", test4.makeCodeTree(test5));


       HuffmanPS3 WnP = new HuffmanPS3();
        HashMap<Character, Long> wnp2 = (HashMap<Character, Long>) WnP.countFrequencies("PS3/singleChar.txt");
        HashMap<Character, String> wnp3= (HashMap<Character, String>) WnP.computeCodes(WnP.makeCodeTree(wnp2));
        WnP.compressFile(wnp3, "PS3/singleChar.txt", "PS3/singleChar_compressed.txt");
        WnP.decompressFile("PS3/singleChar_compressed.txt", "PS3/singleChar_decompressed.txt", WnP.makeCodeTree(wnp2));
**/
        HuffmanCompression test = new HuffmanCompression();
        //test.runHuffman("PS3/tester.txt");
        //test.runHuffman("PS3/testCaseEmpty.txt");
        //test.runHuffman("PS3/USConstitution.txt");
        test.runHuffman("PS3/WarAndPeace.txt");
        //test.runHuffman("PS3/singleChar.txt");
        //test.runHuffman("PS3/singleCharRepeated.txt"); 


    }
}