import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskSequence {
	// Attribute
	private List<Integer> TaskList = new ArrayList<Integer>();  // task list for a day
	private float[][] Distance;
	private int tasknum;			// number of tasks in a day
	private int[] set;				// for permutation
	private boolean[] used;			// for permutation
	private int[] permutationSet;	// for permutation
	private boolean first = true;
	private float minTravelingT = 5000;
	private int[] idealTaskSequence;
	private List<Integer> finalDaySchedule = new ArrayList<>();  // for output


	// Constructor
	public TaskSequence(List<Integer> TaskList, float[][] distance, int totalNum) {
		this.TaskList = TaskList;
		tasknum = TaskList.size();
		Distance = new float[totalNum][totalNum];
		for(int i = 0; i < totalNum; i++){
			for(int j = 0; j < totalNum; j++){
				Distance[i][j] = distance[i][j];
			}
		}
		set = new int[tasknum];
		used = new boolean[tasknum];
		for(int i = 0; i < tasknum; i++) {
			used[i] = false;
		}
		permutationSet = new int[tasknum];
		idealTaskSequence = new int[tasknum];
	}

	// Produce Permutation and Find Minimum Traveling Time
	private void Permutation(int start, int end) {
		if(start == end) {
			for(int i = 0; i < end; i++) {
				permutationSet[i] = TaskList.get(set[i]);
			}
			float tempTravelingT = calTravelingT(permutationSet);
			if(first == true){
				minTravelingT = tempTravelingT;
				idealTaskSequence = permutationSet;
				first = false;
			}
			else if(tempTravelingT < minTravelingT){
				minTravelingT = tempTravelingT;
				idealTaskSequence = permutationSet;
			}
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

	// Calculate Traveling Time
	public float calTravelingT(int[] permutationSet) {
		float temp_travelingt = 0;
		for(int i = 0; i < tasknum - 1; i++){
			int index_i = permutationSet[i] - 1;
			int index_j = permutationSet[i+1] - 1;
			temp_travelingt += Distance[index_i][index_j];
		}
		temp_travelingt += Distance[permutationSet[tasknum - 1]-1][permutationSet[0]-1];

		return temp_travelingt;
	}

	// Decide Task Sequence
	public List<Integer> Sequence() {
		if(tasknum == 0){
			idealTaskSequence = new int[0];
			minTravelingT = 0;
		}
		else if(tasknum == 1){
			idealTaskSequence = new int[1];
			idealTaskSequence[0] = TaskList.get(0);
			minTravelingT = 0;
		}
		else{
			Permutation(0, tasknum);
			for(int i = 0; i < tasknum; i++){
				finalDaySchedule.add(idealTaskSequence[i]);
			}
			finalDaySchedule.add(idealTaskSequence[0]);
		}
		return finalDaySchedule;
	}

	public void printDaySchedule(float tasktime) {
		System.out.print((minTravelingT + tasktime) + " / " + finalDaySchedule + "\n");
	}
	
	public float getMinTravelingT() {
		return minTravelingT;
	}
}
