package random;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class OfficeSpace {
	
	static final int maxsize = 10000; //the side size of the office space
	static int outsidespace = maxsize*maxsize; //the parts of the office space where there aren't rooms (size) 
	static int[][]bins ;//= new int[maxsize][maxsize]; //the 2d grid of cells for the office space
	static Queue<Point> queue; //a queue for breadth first search
	static boolean[][]visited;
	static int maxX, maxY, minX, minY;
	static int currentRoomSize;
	
	public static void main(String[] args) throws FileNotFoundException {
		long startime = System.nanoTime();//for execution time lookup
		maxX = 0;maxY = 0;minX=Integer.MAX_VALUE;minY=Integer.MAX_VALUE;
		ArrayList<Block> rooms = new ArrayList<Block>();
		Scanner s = new Scanner(new File("office_input.dat")); //for reading the file (testing/ presentation only)
		//Scanner s = new Scanner(System.in); //reading from standard in
		int numrooms = s.nextInt(); s.nextLine(); //read the number of room to create and advance cursor
		int set[] = new int[3]; //set of x, y and z : used for storing the rooms to be created and read them back in
		
		for(int i=0;i<numrooms;i++){
			set[0] = s.nextInt(); //used for the x of the starting point
			set[1] = s.nextInt(); //used for the y of the starting point
			set[2] = s.nextInt(); //used for the length/width of the room
			
			rooms.add(new Block(set[0],set[1],set[2])); //store the objects to build a moment later
			
			if(set[0]+set[2]>maxX) maxX = set[0]+set[2];//keep track of the farthest out x
			if(set[1]+set[2]>maxY) maxY = set[1]+set[2];//keep track of the farthest out y
			if(set[0]<minX) minX=set[0]; //keep track of upper left hand corner x
			if(set[1]<minY) minY=set[1]; //keep track of upper left hand corner y
		}
		
		bins = new int[maxX-minX+1][maxY-minY+1]; //build an array as big as necessary
		
		for(int i=1, j=0; i<Math.pow(2, numrooms); i=i*2, j++){//build number of rooms (i represents the value of a bit at subsequent indexes in a binary number)
			Block b = rooms.get(j);
			set[0] = b.x - minX; //offset the x with the minX
			set[1] = b.y - minY; //offset the y with the minY
			set[2] = b.size;
			
			for(int x=0; x<set[2];x++){//for all the x's of the new room
				for(int y=0; y<set[2];y++){ //for all the y's of the new room
					if(bins[x+set[0]][y+set[1]]==0){ //if the cell to fill isn't already part of another room
						outsidespace--; //subtract one from the outside space
						bins[x+set[0]][y+set[1]] = i; //set the cell's value to the current bit
					}
					else bins[x+set[0]][y+set[1]] += i; //and the current cell's value with the current bit
				}//for the y's of the room
			}//for the x's of the room	
		}//build number of rooms

		int numTests = s.nextInt(); //number of tests to perform
		int cx=0; //x of the inquiry point
		int cy=0; //y of the inquiry point
		
		for(int t = 0; t<numTests; t++){//for each inquiry
			
			cx= s.nextInt() - minX;  //the x in the test point //accounting for the offset
			cy = s.nextInt() - minY; //the y in the test point //accounting for the offset
			int test;
			if(cx>=bins.length||cy>=bins[0].length||cx<0||cy<0) test = 0; //bounds checking for trimmed representation
			else test = bins[cx][cy]; //go get the value of the cell
			
			if(test==0){ //if the cell is the outside go get the outside space
				System.out.println(outsidespace);
				continue; //go directly to next test point
			} //other wise do below
			
			queue = new LinkedList<Point>(); //a new queue for each test
			visited = new boolean[maxX-minX+1][maxY-minY+1]; //build a visited array as big as necessary
			queue.add(new Point(cx,cy)); //add inquiry point to start
			currentRoomSize=0; //clear the current count each inquiry
			
			while(!queue.isEmpty()){
				Point p = queue.poll(); //remove the to point of the queue	
				testDirection(p, new Point(-1, 0), test);  //up
				testDirection(p, new Point(1, 0), test);   //down
				testDirection(p, new Point(0, 1), test);   //right
				testDirection(p, new Point(0, -1), test);  //left
			}//while not empty
			
			System.out.println(currentRoomSize); //print the size
		}//for each inquiry
		
		s.close();
		System.out.println((System.nanoTime()-startime)/1e9); //execution time
	}//end of main
	
	public static void testDirection(Point p, Point direction, int testval){
		Point combined = new Point(p.x + direction.x, p.y + direction.y); //the next point to be examined
		if(combined.x<0 || combined.x > maxX-1 || combined.y<0 || combined.y > maxY-1) return; //if the new point would be out of bounds just ignore it
		if(!visited[combined.x][combined.y] && bins[combined.x][combined.y]==testval){ //if it isn't visited and the cell matches the value we are checking for
			queue.add(combined);  //add it to the queue
			visited[combined.x][combined.y] = true; //mark it as visited
			currentRoomSize++; //increment the room size
		}
	}
	
	public static void myToString(){
		for(int x=0; x<14; x++){ //only up to a reasonable amount to print for testing only
			for(int y=0; y<14; y++){
				System.out.printf("%3d",bins[x][y]);
			}
			System.out.println();
		}
	}
	
	public static class Block{
		public Block(int x, int y, int size){
			this.x = x;
			this.y = y;
			this.size = size;
		}
		public int x,y,size;
	}	
}