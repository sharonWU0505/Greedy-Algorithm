import java.util.ArrayList;
import java.util.List;


public class Greedy{
	// Attribute
	private List<Integer> TaskList = new ArrayList<Integer>();  // task list for a day
	private float[][] Distance;
	private float[] ComDistance;	// distance between each task and the company
	private int tasknum;			// number of tasks in a day
	private float minTravelingT = 0;
	private List<Integer> finalDaySchedule = new ArrayList<>();  // for output
	
	// Constructor
	public Greedy(List<Integer> TaskList, float[][] distance, float[] comdistance, int totalNum){
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
	}
	
	public float doGreedy(){
		if(tasknum > 1){
			List<Integer> passedTasks = new ArrayList<>();
			List<Integer> leftTasks = new ArrayList<>();
			for(int i = 0; i < tasknum; i++){
				leftTasks.add(TaskList.get(i));
			}
			int count = 1;
			passedTasks.add(leftTasks.get(0));	// put the first task
			leftTasks.remove(0);				// remove task
			while(count < tasknum){
				float timeadded = 10000;
				int nexttask = -1;
				int remove_index = 0;
				int pre_task = passedTasks.get(passedTasks.size() - 1);
				for(int i = 0; i < leftTasks.size(); i++){
					if(Distance[pre_task - 1][leftTasks.get(i) - 1] < timeadded){
						timeadded = Distance[pre_task - 1][leftTasks.get(i) - 1];
						nexttask = leftTasks.get(i);
						remove_index = i;
					}
				}
				passedTasks.add(nexttask);
//				System.out.print(passedTasks);
				leftTasks.remove(remove_index);
				minTravelingT += timeadded;
				count++;
			}
			// add the company to be the starting point and the ending point
			// number 0 as the company site
			passedTasks.add(0, 0);	// company as the starting point
			int last_site = passedTasks.get(passedTasks.size() - 1);
			passedTasks.add(0);		// company as the ending point
			finalDaySchedule = passedTasks;
			minTravelingT += ComDistance[passedTasks.get(1) - 1];
			minTravelingT += ComDistance[last_site - 1];
		}
		else if(tasknum == 1){
			minTravelingT += 2 * ComDistance[TaskList.get(0) - 1];
			finalDaySchedule.add(TaskList.get(0));
			finalDaySchedule.add(0, 0);
			finalDaySchedule.add(0);
		}
		
		return minTravelingT;
	}
	
	public List<Integer> getFinalDaySchedule(){
		return finalDaySchedule;
	}
}