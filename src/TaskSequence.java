import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskSequence {
	// attribute
	private List<Integer> TaskList = new ArrayList<Integer>();  // task list for a day
	private int[][] Permutation;
	private int Length;		// number of tasks in a day
	private int Index = 0;  // for permutation
	private int SetNum = 1; // for permutation
	private int[] Set;		// for permutation
	private boolean[] Used;	// for permutation

	// constructor
	public TaskSequence(List<Integer> TaskList) {
		this.TaskList = TaskList;
		Length = TaskList.size();
		Set = new int[Length];
		Used = new boolean[Length];
		for(int i = 1; i < Length + 1; i++){
			SetNum *= i;
		}
		Permutation = new int[SetNum][];
		for(int i = 0; i < SetNum; i++) {
			Permutation[i] = new int[Length];
		}
	}
	
	// get Sequence
	public int[] Sequence(int[][] Distance) {
		Permutation(0, Length);

		int min_distance = 0;
		int min_index = 0;
		for(int i = 0; i < SetNum; i++) {
			int temp_distance = 0;
			for(int j = 0; j < Length - 1; j++){
				int indexi = Permutation[i][j]-1;
				int indexj = Permutation[i][j+1]-1;
				temp_distance += Distance[indexi][indexj];
			}
			if(i == 0){
				min_distance = temp_distance;
			}
			else if(min_distance > temp_distance) {
				min_distance = temp_distance;
				min_index = i;
			}
		}
		System.out.println(min_distance);
		System.out.println(Arrays.toString(Permutation[min_index]));
		return Permutation[min_index];
	}
	
	// set used
	public void setUsed() {
		for(int i = 0; i < Length; i++) {
			Used[i] = false;
		}
	}
	
	// read data for distance file
	private int[][] ReadDistanceFile(String filepath, int locnum) {
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

	public void Permutation(int start, int end) {
		if(start == end) {
			for(int i = 0; i < end; i++) {
				Permutation[Index][i] = TaskList.get(Set[i]);
			}
//			System.out.println(Arrays.toString(Permutation[index]));
			Index += 1;
		}
		else {
			for(int i = 0; i < end; i++) {
				if(!Used[i]) {
					Used[i] = true;
					Set[i] = start;
					Permutation(start + 1, end);
					Used[i] = false;
				}
			}
		}
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
			TaskSequence.setUsed();

			String distancepath = "C:\\Users\\admin\\workspace\\greedy\\src\\161018_real_data_distance.txt";
			int length = 19;
			TaskSequence.Sequence(TaskSequence.ReadDistanceFile(distancepath, length));
		}
	}
}