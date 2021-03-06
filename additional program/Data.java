import java.util.*;
import java.awt.Window;
import java.io.*;
import java.lang.Math;

class Node {
	public int attri, level, posNum;
	public List<Integer> listAtt;
	public List<Integer> listRow;
	public double entropy;
	public double total;
	public int number;
	public boolean isLeaf;
	public Node left;
	public Node right;
	public Node parent;
	public int type;
	boolean testGain;
	int label;
	
	public Node() {
		listAtt = new ArrayList<Integer>();
		listRow = new ArrayList<Integer>();		
	}
	
	public Node(int a) {
		attri = a;
		level = posNum = number = 0;
		entropy = 0.0;
		isLeaf = false;		
	}
	
	public Node(int a, int b, int c, double d) {
		attri = a;
		level = b;
		posNum = 0;
		number = c;
		entropy = d;
		total = 0.0;
		isLeaf = false;	
		listAtt = new ArrayList<Integer>();
		listRow = new ArrayList<Integer>();
		testGain = true;
	}	
}

class Attribute {
	public int attriNum;
	public int posNum, dP;
	public int negNum, dN;	
	public double entropyPos;
	public double entropyNeg;
	public double gain;
	public List<Integer> listRowPos;
	public List<Integer> listRowNeg;
	boolean testGain;
	
	public Attribute() {
		attriNum = posNum = dP = negNum = dN = 0;
		entropyPos = entropyNeg = gain = 0.0;
		listRowPos = new ArrayList<Integer>();
		listRowNeg = new ArrayList<Integer>();
		testGain = true;
	}
}

class DecisionTree {
	public Node root;
	public int nodeNum;
	public int leafNum;
	public int leafDepth;
	
	public DecisionTree() {}
	
	public static boolean isPure(int arr[][], int col, int row) {
		int count = 0;
		for (int i=0; i<row; i++) {
			if (arr[i][col] == 1)
				count++;
		}
		if (count == 0 || count == row)
			return true;
		return false;		
	}
	
	public void buildRoot(int arr[][], int col, int row) {			
		double entro = Data.calEntropy(col, arr);		
		Node newNode = new Node(col, 0, row, entro);
		for (int i = 0; i < row; i++) newNode.listRow.add(i);	
		root = newNode;
	}
	
	public void printTree(Node node) {
		if (node != null) {
			printTree(node.left);
			nodeNum++;			
			printTree(node.right);
		}
	}
		
	public void printLeaf(Node node) {
			if (node != null) {
				printLeaf(node.left);
				if (node.isLeaf == true) {
					leafNum++;
					leafDepth += node.level;
					node.label = leafNum;
				} 			
				printLeaf(node.right);
			}		
	}
	
	public void printDT(Node node) {
		if (node != null ) {
			if ( node != root && node != root.right) {
				for (int i = node.level; i > 1; i--) {
					System.out.print("|");
				}				
				if (node.attri < 0) {
					if (node.isLeaf == true) {
						System.out.println(Data.head1[-node.attri - 1] + " = 0 : " + node.type);
					}
					else 
						System.out.println(Data.head1[-node.attri - 1] + " = 0 : ");
					}				
				else {
					if (node.isLeaf == true) {
						System.out.println(Data.head1[node.attri - 1] + " = 1 : " + node.type);
					}
					else 
						System.out.println(Data.head1[node.attri - 1] + " = 1 : ");
				}												
			}
			printDT(node.left);
			printDT(node.right);	
		}		
	}
	
	public void printNodeRight(Node node) {
		if (node.isLeaf == true) {
			System.out.println(Data.head1[node.attri - 1] + " = 1 : " + node.type);
		}
		else System.out.println(Data.head1[node.attri - 1] + " = 1 : ");
		
	}
	
	public void addNode(int arr[][], int col, Node node, double enGain, double ent, int method) {
		
		if (node.isLeaf == true) return;

		if (node.level == col -1 || node.testGain == false || node.entropy < ent) {
			node.isLeaf = true;
			return;
		}		
					
		int a = node.level;
		
		Attribute maxAttri;
		
		if (method ==1) {
			maxAttri = Data.findRandom(Data.calculateEntropy(node, arr),node);
		}
		else {
			maxAttri = Data.findMax(Data.calculateEntropy(node, arr), enGain);
		}
		
		//if (node.listAtt.contains(maxAttri.attriNum)) return;

		Node newNodeR = new Node(maxAttri.attriNum, a + 1, 
								maxAttri.posNum, maxAttri.entropyPos);
		for (int i = 0; i < node.listAtt.size(); i++){
			newNodeR.listAtt.add(node.listAtt.get(i));
		}
	
		newNodeR.listAtt.add(maxAttri.attriNum);
		newNodeR.listRow = maxAttri.listRowPos;
		newNodeR.testGain = maxAttri.testGain;
		newNodeR.posNum = maxAttri.dP;
		newNodeR.total = (double)maxAttri.posNum;
		
		if (newNodeR.posNum/newNodeR.total >= 0.5) 
			newNodeR.type = 1;
		newNodeR.parent = node;		
		node.right = newNodeR;
		
		Node newNodeL = new Node(-maxAttri.attriNum, a + 1, 
				maxAttri.negNum, maxAttri.entropyNeg);
		for (int i = 0; i < node.listAtt.size(); i++){
			newNodeL.listAtt.add(node.listAtt.get(i));
		}
		newNodeL.listAtt.add( - maxAttri.attriNum);
		newNodeL.listRow = maxAttri.listRowNeg;
		newNodeL.testGain = maxAttri.testGain;
		newNodeL.posNum = maxAttri.dN;
		newNodeL.total = (double)maxAttri.negNum;

		if (newNodeL.posNum/newNodeL.total <= 0.5) 
			newNodeL.type = 1;
		newNodeL.parent = node;		
		node.left = newNodeL;
		
		if (method == 1) {
			addNode(arr, col, newNodeR, enGain, ent, 1);
			addNode(arr, col, newNodeL, enGain, ent, 1);
		}
		else {
			addNode(arr, col, newNodeR, enGain, ent, 0);
			addNode(arr, col, newNodeL, enGain, ent, 0);
		}		
				
		if (node.left == null && node.right == null) return;
	}
	
	public static void deleteLeafNode (DecisionTree dt, Node node, int a) {
		
		if (node == null) return; 
		
		if (node.isLeaf == true && node.label == a) {
			if (node.attri < 0) {
				if (node.parent.right == null) {
					node.parent.isLeaf = true;
					node.parent.label = a;
					node.parent.left = null;
					return;
				} else {
					dt.leafNum--;
					node.parent.left = null;
					return;
				}										
			}
			else if (node.parent.left == null) {
				node.parent.isLeaf = true;
				node.parent.label = a;
				node.parent.right = null;
				return;
			} else {
				dt.leafNum--;
				node.parent.right = null;
				return;
			}
		}
		else if (node.isLeaf == true) return;
		else {			
			deleteLeafNode(dt, node.left, a);
			deleteLeafNode(dt, node.right, a);
		}						
	}
	
	public static void pruneTree (DecisionTree dt, Node node, double pruneFac) {
		
		int numDelete = (int)(pruneFac*dt.nodeNum);
		
		if( numDelete > dt.leafNum) numDelete = dt.leafNum;
		
		for (int i = 1; i < numDelete + 1; i++) {
			deleteLeafNode(dt, node, i);			
		}
	}

}

public class Data {

	static int trainData[][];
	static int testData[][];
	static int colNum = 0;
	static int rowNum = -1;
	static int posNum;
	static String head1[];
	static String head2[];
	static int match;
		
	public static int countLines(String filename) throws IOException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
	    try 
	    {
	    	byte c[] = new byte[1024];
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = inputStream.read(c)) != -1){
	          empty = false;
	          for (int i = 0; i < readChars; i++){
	            if (c[i] == '\n') 
	            	rowNum++;
	          }
        }
        return (rowNum == -1 && !empty) ? 1 : rowNum;
	    } finally {
	    	inputStream.close();
	    }
	}
	
	public static Attribute[] calculateEntropy(Node node, int arr[][]) {

		Attribute attribute[] = new Attribute[colNum];
		for (int i = 1; i < colNum; i++) {
			attribute[i] = new Attribute();
			attribute[i].attriNum = i;
		}		
		
		for (int i = 0; i < node.listRow.size(); i++) {
			for (int j = 1; j < colNum; j++) {
				if (!node.listAtt.contains(j) && !node.listAtt.contains(-j)) {
					if (arr[node.listRow.get(i)][j] == 1) {
						attribute[j].posNum++;
						attribute[j].listRowPos.add(node.listRow.get(i));
						if (arr[node.listRow.get(i)][colNum] == 1) attribute[j].dP++;
					}
					else {
						attribute[j].negNum++;
						attribute[j].listRowNeg.add(node.listRow.get(i));
						if (arr[node.listRow.get(i)][colNum] == 0) attribute[j].dN++;
					} 
				}
				else attribute[j].gain = -1;
			}
		}
		
		int a[] = new int[colNum];
		double b[] = new double[colNum];
		int c[] = new int[colNum];
		double d[] = new double[colNum];
		
		for (int i = 1; i < colNum; i++) {
			if (!(attribute[i].gain <0)) {
				a[i] = attribute[i].dP;
				b[i] = (double)attribute[i].posNum;
				c[i] = attribute[i].dN;
				d[i] = (double)attribute[i].negNum;
				
				if (a[i] == 0 || a[i] == (int)(b[i])) attribute[i].entropyPos = 0;
				else
					attribute[i].entropyPos = - (a[i]/b[i]*Math.log(a[i]/b[i]) + 
							(1 - a[i]/b[i])*Math.log(1 - a[i]/b[i])) / Math.log(2);
				
				if (c[i] == 0 || c[i] == (int)(d[i])) attribute[i].entropyNeg = 0;
				else
					attribute[i].entropyNeg = - (c[i]/d[i]*Math.log(c[i]/d[i]) + 
							(1 - c[i]/d[i])*Math.log(1 - c[i]/d[i])) / Math.log(2);				
				
				attribute[i].gain = node.entropy - b[i]/(b[i] + d[i])*attribute[i].entropyPos - 
						d[i]/(b[i] + d[i])*attribute[i].entropyNeg;		
			}			
		}
		return attribute;
	}
	
	public static double calEntropy(int a, int arr[][]) {
		posNum = 0;
		double entropy = 0.0;
		for (int i = 0; i < rowNum; i++) {
			if(arr[i][colNum] == 1)
				posNum++;
		}
		double fraction = ((double)posNum) / rowNum;
		
		entropy = -(((fraction*Math.log(fraction)) + 
				(1-fraction)*Math.log(1-fraction)) / Math.log(2));

		return entropy;
	}
	
	public static Attribute findMax(Attribute attr[], double a) {
		
		Attribute max = attr[1];
		
		for (int i = 1; i < colNum; i++) {
			if (attr[i].gain > max.gain) {
				max = attr[i];
			} 
		}
		if (max.gain < a) max.testGain = false;

		return max;		
	}
	
	public static Attribute findRandom(Attribute attr[], Node node) {
		
		Attribute ranAtt;
				
		int rand;
		
		do {
			rand = randomNumGer(colNum);
		} while (node.listAtt.contains(rand) || node.listAtt.contains(-rand));
		
		ranAtt = attr[rand];

		return ranAtt;		
	}
	
	public static int randomNumGer(int a) {
		
		Random random = new Random();
		
		int randomNum = random.nextInt((a - 1 - 1) + 1) + 1;
		
		return randomNum;		
	}
	
	
	
	public static void parseTrain(String fn) throws FileNotFoundException {
		
		colNum = 0;
		rowNum = -1;
		String filename = fn;
		
		try {
			rowNum = countLines(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File file = new File(filename);
		Scanner inputFile = new Scanner(file);
		String line = inputFile.nextLine();
		
		StringTokenizer st = new StringTokenizer(line);
		String heading = "";
		while(st.hasMoreTokens()){
			heading += st.nextToken() + " ";
			colNum++;	
		}
		
		head1 = heading.split("\\ ");
		trainData = new int[rowNum][colNum + 1];

		for (int i = 0; i< rowNum; i++ ) {
			line = inputFile.nextLine();
			st = new StringTokenizer(line);
			for(int j = 1; j < colNum + 1; j++){
				trainData[i][j] = Integer.parseInt(st.nextToken());
			}			
		}
		inputFile.close();
	}
	
	public static void parseTest(String fn) throws FileNotFoundException {
		
		colNum = 0;
		rowNum = -1;
		String filename = fn;
		
		try {
			rowNum = countLines(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File file = new File(filename);
		Scanner inputFile = new Scanner(file);
		String line = inputFile.nextLine();
		
		StringTokenizer st = new StringTokenizer(line);
		String heading = "";
		while(st.hasMoreTokens()){
			heading += st.nextToken() + " ";
			colNum++;	
		}
		
		head2 = heading.split("\\ ");
		
		testData = new int[rowNum][colNum + 1];

		for (int i = 0; i < rowNum; i++ ) {
			line = inputFile.nextLine();
			st = new StringTokenizer(line);
			for(int j = 1; j < colNum + 1; j++){
				testData[i][j] = Integer.parseInt(st.nextToken());
			}			
		}
		inputFile.close();
	}
	
	public static void test(Node node, int a) {
		
		if (node.isLeaf == true) {
			if (node.attri < 0) {
				if (testData[a][-node.attri] == 0) {
					if (testData[a][colNum] == node.type) {
						match++;
					}
				}
				else if (node.parent.right != null) {
					test(node.parent.right, a);
				}				
			}
			else if (testData[a][node.attri] == 1) {
				if (testData[a][colNum] == node.type) {
					match++;
				}
			}
		}
		else if (node.attri < 0) {
			if (testData[a][-node.attri] == 0) {
				if (node.left != null) {
					test(node.left, a);
				}
				else {
					test(node.right, a);
				}
			}
			else if (node.parent.right != null) {
				test(node.parent.right, a);
			}
		}
		else if (testData[a][node.attri] == 1) {
			if (node.left != null) {
				test(node.left, a);
			}
			else {
				test(node.right, a);
			}
		}		
	}
	
	public static void outputTrain(DecisionTree dt) {
		
		System.out.println("\nNumber of training instances = " + rowNum);
		System.out.println("Number of training attributes = " + (colNum - 1));
		System.out.println("Total number of nodes in the tree = " + dt.nodeNum);
		System.out.println("Number of leaf nodes in the tree = " + dt.leafNum);
		System.out.printf("Average leaf depth of the tree = %.1f", ((double)dt.leafDepth/dt.leafNum));		
		System.out.printf("\nAccuracy of the model on the training dataset = %.1f", (double)match/rowNum*100);
		System.out.print("%\n");			
	}
	
	public static void outputTest() {

		System.out.println("\nNumber of testing instances = " + rowNum);
		System.out.println("Number of testing attributes = " + (colNum - 1));
		System.out.printf("Accuracy of the model on the testing dataset = %.1f", (double)match/rowNum*100);
		System.out.print("%\n\n");
	}
	
	public static void main(String[] args) throws IOException {
		
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Please input the complete path of the training dataset: ");
		
		String strTrain = keyboard.nextLine();
		
		System.out.println("Please input the complete path of the testing dataset: ");
		
		String strTest = keyboard.nextLine();
		
		System.out.println("--------------- ID3 ---------------");
		
		Data.parseTrain(strTrain);
		
		DecisionTree dt = new DecisionTree();
		dt.buildRoot(trainData, colNum, rowNum);
		dt.addNode(trainData, colNum, dt.root, 0.00001, 0.001, 0);
		dt.printTree(dt.root);
		dt.leafDepth = 0;
		dt.printLeaf(dt.root);
		System.out.println();
		//dt.printDT(dt.root);
		//dt.printNodeRight(dt.root.right);
		
		Data.parseTest(strTrain);
		
		match = 0;
		
		System.out.println("match = " + match);
		System.out.println("rowNum = " + rowNum);
		
		for (int i = 0; i < rowNum; i++) {
			test(dt.root.left, i);
		}
		
		System.out.println("match = " + match);
		
		outputTrain(dt);
		
		Data.parseTest(strTest);
		
		match = 0;
		
		System.out.println("match = " + match);
		System.out.println("rowNum = " + rowNum);
		
		for (int i = 0; i < rowNum; i++) {
			test(dt.root.left, i);
		}
		
		System.out.println("match = " + match);
		
		outputTest();
		
		Data.parseTrain(strTrain);
		
		System.out.println("--------------- RAS ---------------");
		
		DecisionTree dtRand = new DecisionTree();
		dtRand.buildRoot(trainData, colNum, rowNum);
		dtRand.addNode(trainData, colNum, dtRand.root, 0.00001, 0.001, 1);
		dtRand.printTree(dtRand.root);
		dtRand.leafDepth = 0;
		dtRand.printLeaf(dtRand.root);
		//dtRand.printDT(dtRand.root);
		//dtRand.printNodeRight(dtRand.root.right);
		
		Data.parseTest(strTrain);
		
		match = 0;
		
		System.out.println("match = " + match);
		System.out.println("rowNum = " + rowNum);
		
		for (int i = 0; i < rowNum; i++) {
			test(dtRand.root.left, i);
		}
		
		System.out.println("match = " + match);
		
		outputTrain(dtRand);
		
		Data.parseTest(strTest);
		
		match = 0;
		
		System.out.println("match = " + match);
		System.out.println("rowNum = " + rowNum);
		
		for (int i = 0; i < rowNum; i++) {
			test(dtRand.root.left, i);
		}
		
		System.out.println("match = " + match);
		
		outputTest();			
		
		
		keyboard.close();
		
		System.out.println("\nProgram Terminated. Have a great day!\n");
	}
}

