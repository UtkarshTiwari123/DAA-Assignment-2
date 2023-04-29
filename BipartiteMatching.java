import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BipartiteMatching {

	class Edge{
		private static final int defaultEdgeCapacity = 1;	//This Flow network is for bipartite matching so the default capacity is always 1
		private int fromVertex;		//an edge is composed of 2 vertices
		private int toVertex;
		private int capacity;		//edges also have a capacity & a flow
		private int flow;

		//Overloaded constructor to create a generic edge with a default capacity
		public Edge(int fromVertex, int toVertex){
			this(fromVertex, toVertex, defaultEdgeCapacity);
		}

		public Edge(int fromVertex, int toVertex, int capacity){
			this.fromVertex = fromVertex;
			this.toVertex = toVertex;
			this.capacity = capacity;
		}
		
		//Given an end-node, Returns the other end-node (completes the edge)
		public int getOtherEndNode(int vertex){
			if(vertex==fromVertex){
				return toVertex;
			}
			return fromVertex;
		}
		
		public int getCapacity(){
			return capacity;
		}
		
		public int getFlow(){
			return flow;
		}
		
		public int residualCapacityTo(int vertex){
			if(vertex==fromVertex){
				return flow;
			}
			return (capacity-flow);
		}
		
		public void increaseFlowTo(int vertex, int changeInFlow){
			if(vertex==fromVertex){
				flow = flow-changeInFlow;
			}
			else{
				flow = flow+changeInFlow;
			}
		}
		
		//Prints edge using Array indexes, not human readable ID's like "S" or "T"
		@Override
		public String toString(){
			return "(" + fromVertex+" --> "+toVertex + ")";
		}
	}

	private ArrayList<ArrayList<Edge>> graph;		//Graph is represented as an ArrayList of Edges
	// private ArrayList<String> getStringVertexIdFromArrayIndex;	//convert between array indexes (starting from 0) & human readable vertex names
	private int vertexCount;		//How many vertices are in the graph

	//These fields are updated by fordFulkersonMaxFlow and when finding augmentation paths
	private Edge[] edgeTo;
	private boolean[] isVertexMarked;		//array of all vertices, updated each time an augmentation path is found
	private int flow;

	//Constructor initializes graph edge list with number of vertexes, string equivalents for array indexes & adds empty ArrayLists to the graph for how many vertices ther are
	public BipartiteMatching(int vertexCount){
		this.vertexCount = vertexCount;

		graph = new ArrayList<>(vertexCount);		//Populate graph with empty ArrayLists for each vertex
		for(int i=0; i<vertexCount; ++i){
			graph.add(new ArrayList<>());
		}
	}

	public void addEdge(int fromVertex, int toVertex){
		Edge newEdge = new Edge(fromVertex, toVertex);	//create new edge between 2 vertices
		graph.get(fromVertex).add(newEdge);		//Undirected bipartie graph, so add edge in both directions
		graph.get(toVertex).add(newEdge);
	}

	//Adds edges from the source to all vertices in the left half
	public void connectSourceToLeftHalf(int source, int[] leftHalfVertices){
		for(int vertexIndex : leftHalfVertices){
			// System.out.println("addEdge(source, vertexIndex) = ("+source+", "+vertexIndex+")");
			this.addEdge(source, vertexIndex);
		}
	}

	//Adds edges from all vertices in right half to sink
	public void connectSinkToRightHalf(int sink, int[] rightHalfVertices){
		for(int vertexIndex : rightHalfVertices){
			// System.out.println("addEdge(vertexIndex, sink) = ("+vertexIndex+", "+sink+")");
			this.addEdge(vertexIndex, sink);
		}
	}
	
	//Finds max flow / min cut of a graph
	public void fordFulkersonMaxFlow(int source, int sink){
		edgeTo = new Edge[vertexCount];
		while(existsAugmentingPath(source, sink)){
			int flowIncrease = 1;	//default value is 1 since it's a bipartite matching problem with capacities = 1
			
			//Loop over The path from source to sink. (Update max flow & print the other matched vertex)
			for(int i=sink; i!=source; i=edgeTo[i].getOtherEndNode(i)){
				//Loop stops when i reaches the source, so print out the vertex in the path that comes right before the source
				// if(edgeTo[i].getOtherEndNode(i)==source){
				// 	// System.out.println(getStringVertexIdFromArrayIndex.get(i));		//use human readable vertex ID's
				// }
				flowIncrease = Math.min(flowIncrease, edgeTo[i].residualCapacityTo(i));
			}
			
			//Update Residual Capacities
			for(int i=sink; i!=source; i=edgeTo[i].getOtherEndNode(i)){ 
				edgeTo[i].increaseFlowTo(i, flowIncrease);
			}
			flow+=flowIncrease;
		}
		System.out.println("\nMaximum pairs matched = "+flow);
	}
	
	//Calls dfs to find an augmentation path & check if it reached the sink
	public boolean existsAugmentingPath(int source, int sink){
		isVertexMarked = new boolean[vertexCount];		//recreate array of visited nodes each time searching for a path
		isVertexMarked[source] = true;		//visit the source

		// System.out.print("Augmenting Path : S ");
		depthFirstSearch(source, sink);		//attempts to find path from source to sink & updates isVertexMarked
		// System.out.print("T  ");

		return isVertexMarked[sink];	//if it reached the sink, then a path was found
	}
	
	public void depthFirstSearch(int v, int sink){
		if(v==sink){	//No point in finding a path if the starting vertex is already at the sink
			return;
		}
		
		for(Edge edge : graph.get(v)){		//loop over all edges in the graph
			int otherEndNode = edge.getOtherEndNode(v);
			if(!isVertexMarked[otherEndNode] && edge.residualCapacityTo(otherEndNode)>0 ){	//if otherEndNode is unvisited AND if the residual capacity exists at the otherEndNode
				// System.out.print( getStringVertexIdFromArrayIndex.get(otherEndNode) +" ");
				edgeTo[otherEndNode] = edge;		//update next link in edge chain
				isVertexMarked[otherEndNode] = true;		//visit the node
				depthFirstSearch(otherEndNode, sink);		//recursively continue exploring
			}
		}
	}

	public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the number the vertices:");
    int vertexCount = sc.nextInt();
    System.out.println("Enter the vertices on the left side :");
    int left = sc.nextInt();
    System.out.println("Enter the vertices on the right side :");
    int right = sc.nextInt();

		// int vertexCount = 12;
		int vertexCountIncludingSourceAndSink = vertexCount +2;

		int source = vertexCount;	//source & sink as array indexes
		int sink = vertexCount+1;

		//These must be consecutive indexes. rightHalfVertices starts with the next integer after the last item in leftHaldVertices
		int[] leftHalfVertices = new int[left];
    for(int i=0;i<left;i++)
      leftHalfVertices[i] = i;
      int[] rightHalfVertices = new int[right];	
      int count = left;
      for(int i=0;i<right;i++)
    {
      rightHalfVertices[i] = count;
      count++;
    }
		//sink is connected to these vertices

		BipartiteMatching graph1BipartiteMatcher = new BipartiteMatching(vertexCountIncludingSourceAndSink);
    try {
      File file = new File("filename.txt");
      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          String[] line1 = line.split(" ");
          int i = Integer.parseInt(line1[0]);
          int j = Integer.parseInt(line1[1]);    
          graph1BipartiteMatcher.addEdge(i, j);
      }

      scanner.close();
  } catch (FileNotFoundException e) {
      System.out.println("File not found.");
  }
		
		graph1BipartiteMatcher.connectSourceToLeftHalf(source, leftHalfVertices);
		graph1BipartiteMatcher.connectSinkToRightHalf(sink, rightHalfVertices);

		// System.out.println("Running Bipartite Matching on Graph 1");
		graph1BipartiteMatcher.fordFulkersonMaxFlow(source, sink);
		System.out.println("\n");
  sc.close();
	}
}