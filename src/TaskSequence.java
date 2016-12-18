import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskSequence {
	// Attribute
	private List<Integer> TaskList = new ArrayList<Integer>();  // task list for a day
	private float[][] Distance;
	private float[] ComDistance;	// distance between each task and the company
	private int tasknum;			// number of tasks in a day
	private int[] set;				// for permutation
	private int[] permutationSet;	// for permutation
	private boolean[] used;			// for permutation
	private boolean first = true;
	private float minTravelingT = 5000;
	private int[] idealTaskSequence;
	private List<Integer> finalDaySchedule = new ArrayList<>();  // for output


	// Constructor
	public TaskSequence(List<Integer> TaskList, float[][] distance, float[] comdistance, int totalNum) {
		this.TaskList = TaskList;
		tasknum = TaskList.size();
		Distance = new float[totalNum][totalNum];
		for(int i = 0; i < totalNum; i++){
			for(int j = 0; j < totalNum; j++){
				Distance[i][j] = distance[i][j];
			}
		}
		ComDistance = new float[totalNum];
		for(int i = 0; i < totalNum; i++){
			ComDistance[i] = comdistance[i];
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
				for(int i = 0; i < end; i++) {
					idealTaskSequence[i] = permutationSet[i];
				}
				first = false;
			}
			else if(tempTravelingT < minTravelingT){
				minTravelingT = tempTravelingT;
				for(int i = 0; i < end; i++) {
					idealTaskSequence[i] = permutationSet[i];
				}
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
		// add traveling time from and get back to the company
		temp_travelingt += ComDistance[permutationSet[tasknum - 1] - 1];
		temp_travelingt += ComDistance[permutationSet[0] - 1];

		return temp_travelingt;
	}

	// Decide Task Sequence
	public void doSequence() {
		if(tasknum == 0){
			minTravelingT = 0;
		}
		else if(tasknum == 1){
			finalDaySchedule.add(0);				// company as the starting site
			finalDaySchedule.add(TaskList.get(0));	// add the only task
			finalDaySchedule.add(0);				// company as the ending site
			minTravelingT = 2 * ComDistance[TaskList.get(0) - 1];
		}
		else{
			Permutation(0, tasknum);
			// turn array into list
			for(int i = 0; i < tasknum; i++){
				finalDaySchedule.add(idealTaskSequence[i]);
			}
			finalDaySchedule.add(0, 0);
			finalDaySchedule.add(0);
		}
	}
	
	public List<Integer> getfinalDaySchedule() {
		return finalDaySchedule;
	}

	public float getMinTravelingT() {
		return minTravelingT;
	}
}
