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
	private int[] output;

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
		permutation = new int[setnum][length];
		output = new int[length + 1];
	}

	// Produce Permutation
	private void Permutation(int start, int end) {
		if(start == end) {
			for(int i = 0; i < end; i++) {
				permutation[index][i] = tasklist.get(set[i]);
			}
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

	// Decide Task Sequence
	public List<Integer> Sequence(double[][] distance) {
		Permutation(0, length);

		double min_distance = 0;
		int min_index = 0;
		for(int i = 0; i < setnum; i++) {
			double temp_distance = 0;
			for(int j = 0; j < length - 1; j++){
				int indexi = permutation[i][j]-1;
				int indexj = permutation[i][j+1]-1;
				temp_distance += distance[indexi][indexj];
			}

			// add the time to get back to the first location
			if(length > 0){
				temp_distance += distance[permutation[i][length - 1]-1][permutation[i][0]-1];
			}

			if(i == 0){
				min_distance = temp_distance;
			}
			else if(min_distance > temp_distance) {
				min_distance = temp_distance;
				min_index = i;
			}
		}
		
		// make a new output array with the same start and end point
		List<Integer> daySchedule = new ArrayList<>();
		for(int i = 0; i < length; i++){
			output[i] = permutation[min_index][i];
			daySchedule.add(output[i]);
		}
		if(length > 0){
			output[length] = permutation[min_index][0];
			daySchedule.add(output[length]);
		}

		System.out.print("Min Traveling Time: " + min_distance + "; ");
		System.out.print("Task sequence: " + Arrays.toString(output) + "\n");
		return daySchedule;
	}
}
