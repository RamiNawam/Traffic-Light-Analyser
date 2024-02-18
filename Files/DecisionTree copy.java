import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	
	// Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf;
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf

		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}

		
		// this method takes in a datalist (ArrayList of type datum). It returns the calling DTNode object 
		// as the root of a decision tree trained using the datapoints present in the datalist variable and minSizeDatalist.
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {
			// Check if this should be a leaf node based on size and uniformity
			if (datalist.size() < minSizeDatalist || allLabelsSame(datalist)) {
				this.leaf = true;
				this.label = majorityLabel(datalist);
				return this;
			}

			// Find the best attribute and threshold for splitting
			double bestEntropy = Double.MAX_VALUE;
			int bestAttribute = -1;
			double bestThreshold = Double.MAX_VALUE;
			ArrayList<Datum> bestLeftSplit = new ArrayList<>();
			ArrayList<Datum> bestRightSplit = new ArrayList<>();

			for (int attribute = 0; attribute < datalist.get(0).x.length; attribute++) {
				for (Datum data : datalist) {
					double threshold = data.x[attribute];
					ArrayList<Datum> leftSplit = new ArrayList<>();
					ArrayList<Datum> rightSplit = new ArrayList<>();

					// Split the data based on the current attribute and threshold
					for (Datum d : datalist) {
						if (d.x[attribute] < threshold) {
							leftSplit.add(d);
						} else {
							rightSplit.add(d);
						}
					}

					// Calculate the weighted entropy of the split
					double entropy = weightedEntropy(leftSplit, rightSplit);
					if (entropy < bestEntropy) {
						bestEntropy = entropy;
						bestAttribute = attribute;
						bestThreshold = threshold;
						bestLeftSplit = new ArrayList<>(leftSplit);
						bestRightSplit = new ArrayList<>(rightSplit);
					}
				}
			}

			this.leaf = false;
			this.attribute = bestAttribute;
			this.threshold = bestThreshold;
			this.left = (new DTNode()).fillDTNode(bestLeftSplit);
			this.right = (new DTNode()).fillDTNode(bestRightSplit);

			return this;
		}

		private boolean allLabelsSame(ArrayList<Datum> datalist) {
			if (datalist.isEmpty()) {
				return true;
			}
			int firstLabel = datalist.get(0).y;
			for (Datum data : datalist) {
				if (data.y != firstLabel) {
					return false;
				}
			}
			return true;
		}

		private int majorityLabel(ArrayList<Datum> datalist) {
			int count0 = 0;
			int count1 = 0;
			for (Datum data : datalist) {
				if (data.y == 0) {
					count0++;
				} else {
					count1++;
				}
			}
			return count0 >= count1 ? 0 : 1;
		}

		private double weightedEntropy(ArrayList<Datum> leftSplit, ArrayList<Datum> rightSplit) {
			double totalSize = leftSplit.size() + rightSplit.size();
			if (totalSize == 0) {
				return 0;
			}
			double weightLeft = (double) leftSplit.size() / totalSize;
			double weightRight = (double) rightSplit.size() / totalSize;
			return weightLeft * calcEntropy(leftSplit) + weightRight * calcEntropy(rightSplit);
		}









		// This is a helper method. Given a datalist, this method returns the label that has the most
		// occurrences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.





		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
			int classifyAtNode(double[] xQuery) {
				DTNode currentNode = this;  // Start with the current node

				while (!currentNode.leaf) {  // Continue traversing until a leaf node is reached
					if (xQuery[currentNode.attribute] < currentNode.threshold) {
						currentNode = currentNode.left;  // Go to the left child if the attribute is less than the threshold
					} else {
						currentNode = currentNode.right; // Go to the right child otherwise
					}
				}
				return currentNode.label;  // Once a leaf node is reached, return its label
			}


			//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
			//at DTNode object passed as the parameter
			public boolean equals(Object dt2) {
				// Check if the passed object is an instance of DTNode
				if (dt2 == null || !(dt2 instanceof DTNode)) {
					return false;
				}
				DTNode other = (DTNode) dt2;

				// Check if both nodes are leaf nodes
				if ((this.left == null && this.right == null) && (other.left == null && other.right == null)) {
					// Compare labels of leaf nodes
					return this.label == other.label;
				}

				// Check if one node is a leaf and the other is not
				if ((this.left == null && this.right == null) || (other.left == null && other.right == null)) {
					return false;
				}

				// Check if attributes and thresholds are the same for non-leaf nodes
				try {
					if (this.attribute != other.attribute || this.threshold != other.threshold) {
						return false;
					}
				} catch (Exception e) {
					// If accessing attributes or thresholds causes an exception, nodes are not equal
					return false;
				}

				// Recursively compare left and right children
				return (this.left.equals(other.left) && this.right.equals(other.right));
			}

			// Assuming classifyAtNode method exists and works as intended

	}



	//Given a dataset, this returns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist) {
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001,num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		return this.rootDTNode.classifyAtNode( xQuery );
	}

	// Checks the performance of a DecisionTree on a dataset
	// This method is provided in case you would like to compare your
	// results with the reference values provided in the PDF in the Data
	// section of the PDF
	String checkPerformance( ArrayList<Datum> datalist) {
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
