import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
public class OfficeSpace {
	
	static short[][] room;//create int array 10,000 x 10,000
	
	public static void main(String[] args) throws IOException{
		long startime = System.nanoTime();//used for execution time
		
		Scanner in = new Scanner(new File("officespace_input.txt"));//read the input from the file
		int room_size = 10000;//set the starting room size
		int total_size = room_size*room_size;//determine total size
		int test_cast = 1;//test case starts at 1
		boolean done = false;//test cases are not done
		
		while(!done){//as long as there are more test cases continue.
			
			if(test_cast > 1){//only if the room needs reset (test case > 1) fill with 0s and reset total room size.
				for(int x=0;x<room.length;x++){
					for(int y=0;y<room[0].length;y++){
						room[x][y] = 0;
					}
				}
				total_size = room_size*room_size;
			}
			
			int num_rooms = in.nextInt();//get the number of rooms.
			int maxX = 0, maxY = 0;//set maxX and maxY to smallest possible value to use greedy method to find "smallest so far".
			int minX = room_size, minY = room_size;//set minX and minY to largest possible value to use greedy method to find "largest so far".
			
			Cube[] cubes = new Cube[num_rooms];//need a list of cubes for their positions and size.
			for(int i=0;i<num_rooms;i++){//for each room, get its position and size.
				int x = in.nextInt();//get the x coord for the top of the room
				int y = in.nextInt();//get the y coord for the left of the room
				int size = in.nextInt();//get the length and width (its the same) for the room
				cubes[i] = new Cube(x,y,size);//add the room to our list of cubes.
				
				//finding the min and max x and y to shrink the array to those boundaries.
				if(x+size > maxX) maxX = x+size;
				if(y+size > maxY) maxY = y+size;
				if(x<minX) minX = x;
				if(y<minY) minY = y;
			}
			int offsetX = minX, offsetY = minY;//define the minx and miny as the offsets for everything else
			
			room = new short[maxX-offsetX][maxY-offsetY];//define our room size array as the farthest away point for both X and Y minus the smallest X and Y (pushes everything into the top left of the 2d array)
			
			for(int i=0;i<cubes.length;i++){//for all the cubes, start at their top left coordinate and fill square with room's number
				Cube c = cubes[i];
				for(int sx=0;sx<c.size;sx++){//now fill that square space with the room number.
					for(int sy=0;sy<c.size;sy++){
						if(room[c.x+sx-offsetX][c.y+sy-offsetY] == 0){
							room[c.x+sx-offsetX][c.y+sy-offsetY] = (short)(i+1);//if the room was previously empty floor, replace with room number
							total_size--;//remove from total room size;
						}
						else room[c.x+sx-offsetX][c.y+sy-offsetY] *= (num_rooms);//else, this area was already previously a room, combine room numbers to make new unique room
					}
				}
			}
			
			System.out.printf("Office #%d:\n",test_cast);
			int questions = in.nextInt();//get number of points to query room sizes.
			boolean[][] visited_ref;//boolean array to reference for determining which cells have been visited.
			
			for(int k=0;k<questions;k++){//for each question, create a breadth search queue and find all spaces with the same room number.
				int test_x = in.nextInt()-offsetX;//the x coordinate to test
				int test_y = in.nextInt()-offsetY;//the y coordinate to test
				Point p = new Point(test_x,test_y);//create a point to hold values.
				
				int lookingFor;
				if(p.x >= room.length || p.x < 0 || p.y >= room[0].length || p.y < 0)
					lookingFor = 0;//if the point is at a known non-room location (outside our array) just pretend its 0 (outside space)
				else
					lookingFor = room[p.x][p.y];//find out what room number we're looking for.
				
				if(lookingFor == 0)
					System.out.println(total_size);//just print out the max room size minus the area of the rooms
				else{//else we gotta breadth first search for cells with the same number as lookingFor
					Queue<Point> crawler = new LinkedList<Point>();//our queue that will crawl the adjacent spaces.
					crawler.add(p);//put it in the queue.
					visited_ref = new boolean[maxX-offsetX][maxY-offsetY];//define the array reference of points we have visited
					int num_visited = 0;//start a counter of the number of cells that we have visited.
					
					while(!crawler.isEmpty()){//as long as the queue has more points to visit, continue looking for adjacent points.
						Point current = crawler.poll();//remove from queue.
						if(visited_ref[current.x][current.y])//if the point were looking at has already been visited, just skip it.
							continue;
						else{
							visited_ref[current.x][current.y]=true;//mark the position as being visited
							num_visited++;//increase the number of visited cells.
						}
						//look up, while performing some bounds and prev-visited checking
						if(current.x-1 >= 0 && !visited_ref[current.x-1][current.y] && room[current.x-1][current.y] == lookingFor)
							crawler.add(new Point(current.x-1,current.y));
						//look down, while performing some bounds and prev-visited checking
						if(current.x+1 < room.length && !visited_ref[current.x+1][current.y] && room[current.x+1][current.y] == lookingFor)
							crawler.add(new Point(current.x+1,current.y));
						//look left, while performing some bounds and prev-visited checking
						if(current.y-1 >= 0 && !visited_ref[current.x][current.y-1] && room[current.x][current.y-1] == lookingFor)
							crawler.add(new Point(current.x,current.y-1));
						//look right, while performing some bounds and prev-visited checking
						if(current.y+1 < room[0].length && !visited_ref[current.x][current.y+1] && room[current.x][current.y+1] == lookingFor)
							crawler.add(new Point(current.x,current.y+1));
					}
					//determine the final size of the room.
					System.out.println(num_visited);//print out the size of the room for the given point.
				}
			}
			test_cast++;//increment the test case counter
			if(!in.hasNextLine()) done = true;//if there are more lines, assume there are more test cases, otherwise we are done.
		}
		in.close();//close the scanner.
		long end = System.nanoTime();//used for execution time
		System.out.printf("%.9f seconds\n",((float)end-(float)startime)/1e9); //print execution time
	}//end main
	public static class Cube extends Point{
		public int size;
		public Cube(int x, int y, int size){
			super(x,y);
			this.size = size;
		}
	}
}//end class