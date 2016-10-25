import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskSequence {
	// attribute
	private List<Integer> tasklist = new ArrayList<Integer>();  // task list for a day	
	private int length;		// number of tasks in a day
	private int index = 0;  // for permutation
	private int[] set;		// for permutation
	private boolean[] used;	// for permutation
	private int setnum = 1; // for permutation
	private int[][] permutation;

	// Constructor
	public TaskSequence(List<Integer> tasklist) {
		this.tasklist = tasklist;
		length = tasklist.size();
		set = new int[length];
		used = new boolean[length];
		for(int i = 0; i < length; i++) {
			used[i] = false;
		}
		for(int i = 1; i < length + 1; i++){
			setnum *= i;  // get the number of sets
		}
		permutation = new int[setnum][];
		for(int i = 0; i < setnum; i++) {
			permutation[i] = new int[length];
		}
	}
	
	// Set Used to false
//	public void setUsed() {
//		for(int i = 0; i < length; i++) {
//			used[i] = false;
//		}
//	}
	
	// Produce Permutation
	public void Permutation(int start, int end) {
		if(start == end) {
			for(int i = 0; i < end; i++) {
				permutation[index][i] = tasklist.get(set[i]);
			}
//			System.out.println(Arrays.toString(permutation[index]));
			index += 1;
		}
		else {
			for(int i = 0; i < end; i++) {
				if(!used[i]) {
					used[i] = true;
					set[i] = start;
					Permutation(start + 1, end);
					used[i] = false;
				}
			}
		}
	}
	
	// Get Task Sequence
	public int[] Sequence(int[][] distance) {
		Permutation(0, length);

		int min_distance = 0;
		int min_index = 0;
		for(int i = 0; i < setnum; i++) {
			int temp_distance = 0;
			for(int j = 0; j < length - 1; j++){
				int indexi = permutation[i][j]-1;
				int indexj = permutation[i][j+1]-1;
				temp_distance += distance[indexi][indexj];
			}
			if(i == 0){
				min_distance = temp_distance;
			}
			else if(min_distance > temp_distance) {
				min_distance = temp_distance;
				min_index = i;
			}
		}

		System.out.print("Min distance: " + min_distance + "\n");
		System.out.print("Task sequence: " + Arrays.toString(permutation[min_index]) + "\n");
		return permutation[min_index];
	}
	
	// read data for distance file
	public int[][] ReadDistanceFile(String filepath, int locnum) {
		int[][] Distance = new int[locnum][];

		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			int locindex = 0;
	        while((line = br.readLine()) != null) {
	        	Distance[locindex] = new int[locnum];
				String[] distanceData = line.split(",");
				for(int i = 0; i < locnum; i++){
					Distance[locindex][i] = Integer.parseInt(distanceData[i]);
				}
//				System.out.println(Arrays.toString(Distance[locindex]));
				locindex += 1;
	        }
			
			br.close();
		}
	    catch(IOException e) {
	    	System.out.println(e);
	    }
		
		return Distance;
	}

	public static void main(String[] args) {
		List<List> test = new ArrayList<>();
		List<Integer> test1 = new ArrayList<>();
		List<Integer> test2 = new ArrayList<>();
		List<Integer> test3 = new ArrayList<>();
		List<Integer> test4 = new ArrayList<>();
		List<Integer> test5 = new ArrayList<>();
		test1.add(17);
		test1.add(18);
		test1.add(2);
		test1.add(3);
		test.add(test1);
		test2.add(16);
		test2.add(14);
		test2.add(1);
		test.add(test2);
		test3.add(19);
		test3.add(13);
		test3.add(7);
		test3.add(8);
		test.add(test3);
		test4.add(4);
		test4.add(15);
		test4.add(5);
		test4.add(6);
		test.add(test4);
		test5.add(9);
		test5.add(10);
		test5.add(11);
		test5.add(12);
		test.add(test5);
		
		for(int i = 0; i < test.size(); i++){
			TaskSequence TaskSequence = new TaskSequence(test.get(i));
//			TaskSequence.setused();

			String distancepath = "C:\\Users\\admin\\workspace\\greedy\\src\\161018_real_data_distance.txt";
			int length = 19;
			TaskSequence.Sequence(TaskSequence.ReadDistanceFile(distancepath, length));
		}
	}
}