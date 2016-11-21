import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondStage{
	// Attribute
	private List<Float> Workload = new ArrayList<>();
	private List<List> OtherData = new ArrayList<>();
	private float Gamma = 0;
	private int Weekdays = 0;
	private int TaskNum = 0;
	private List<List<Integer>> Schedule = new ArrayList<>();
	private List<Integer> UnassignedTasks = new ArrayList<>();
	private float [] Rewards = {0, 0, 0, 0, 0, 0, 0};
	private float [] ProcessingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TravelingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TotalT = {0, 0, 0, 0, 0, 0, 0};
	private float [] LeftT = {0, 0, 0, 0, 0, 0, 0};
	private float [][] Distance;
	private List<TaskSplit> TaskPercentages = new ArrayList<>();
	private float[][] FinalTaskPercentages;	// for final output
	
	// Constructor
	public SecondStage(List<Float> Workload, List<List> OtherData, float Gamma, int Weekdays, List<List<Integer>> Schedule, List<Integer> UnassignedTasks, float [] Rewards, float [] ProcessingT, float [] TravelingT, float [] TotalT, float [][] distance, List<TaskSplit> TaskPercentages){
		this.Workload = Workload;
		this.OtherData = OtherData;
		this.Gamma = Gamma;
		this.Weekdays = Weekdays;
		this.Schedule = Schedule;
		this.UnassignedTasks = UnassignedTasks;
		this.Rewards = Rewards;
		this.ProcessingT = ProcessingT;
		this.TravelingT = TravelingT;
		this.TotalT = TotalT;
		for(int i = 0; i < Weekdays; i++){
			LeftT[i] = Workload.get(i) * Gamma - TotalT[i];
		}
		TaskNum = OtherData.size();
		Distance = new float[TaskNum][TaskNum];
		for(int i = 0; i < TaskNum; i++){
			for(int j = 0; j < TaskNum; j++){
				this.Distance[i][j] = distance[i][j];
			}
		}
	}
	
	// SecondStageSort: sort unassigned tasks by their maximum possible rewards
	private List<TaskSplit> SecondStageTaskSort(){
		List<TaskSplit> new_unassignedTasks = new ArrayList<>();  // unassigned tasks with more details
		for(int i = 0; i < UnassignedTasks.size(); i++){
			// get the maximum rewards and ideal day of each unassigned task
			// the maximum rewards = (max_rewards * the proportion the task could be done) - the split cost
			// already take traveling time into the calculation of max_rewards
			int taskid = UnassignedTasks.get(i);
			List<Float> task_details = OtherData.get(taskid - 1);
			float split_cost = (float) task_details.get(8);	// split cost
			float new_totalt = 0;
			float max_rewards = 0;
			int ideal_day = -1;
			for(int j = 0; j < Weekdays; j++){
				// calculate new traveling time for checking
				List<Integer> temp_tasklist = new ArrayList<>();
				// add original tasks one by one
				for(int x = 0; x < Schedule.get(j).size(); x++){
					temp_tasklist.add(Schedule.get(j).get(x));
				}
				temp_tasklist.add(taskid);
				// calculate new traveling time for checking
				Greedy Greedy = new Greedy(temp_tasklist, Distance, TaskNum);
				float newTravelingT = Greedy.doGreedy();
				float newTotalT = TotalT[j] + task_details.get(7) - TravelingT[j] + newTravelingT;
				float newLeftT = Workload.get(j) * Gamma - newTotalT;
				if(newLeftT > 0){
					float possi_percentage = newLeftT / task_details.get(7);
					float inner_rewards = (task_details.get(j) * possi_percentage) - split_cost;
					if(inner_rewards >= max_rewards){
						new_totalt = newTotalT;
						max_rewards = inner_rewards;
						ideal_day = j;
					}
				}
			}
			TaskSplit aTask = new TaskSplit(taskid, new_totalt, max_rewards, ideal_day);
			
			// set all unassigned tasks in order by their maximum possible rewards
			if(i == 0){
				new_unassignedTasks.add(aTask);
			}
			else{
				for(int k = 0; k < new_unassignedTasks.size(); k++){
					TaskSplit ptr = new_unassignedTasks.get(k);
					if(max_rewards >= ptr.getMaxRewards()){
						new_unassignedTasks.add(k, aTask);
						break;
					}
					else if(k == new_unassignedTasks.size() - 1){
						new_unassignedTasks.add(aTask);
					}
				}
			}
		}
		// check the correctness of sorting
		for(int i = 0; i < new_unassignedTasks.size(); i++){
			TaskSplit current_task = new_unassignedTasks.get(i);
			System.out.println("Task " + current_task.getTaskId() + ": max_rewards " + current_task.getMaxRewards() + " on day " + (current_task.getIdealDay() + 1));
		}
		return new_unassignedTasks;
	}

	private void SecondStageAssignment(){
		List<TaskSplit> new_unassignedTasks = SecondStageTaskSort();
		List<Integer> final_unassignedTasks = new ArrayList<>();	// the final unassigned tasks
		// check each weekday's capacity left
		int available_days = 0;
		for(int j = 0; j < Weekdays; j++){
			if(LeftT[j] > 0)
				available_days++;
		}

		// Try to split an unassigned task into the day where the max_rewards exists 
		// if the ideal day is out of capacity, re-calculate its max_rewards
		for(int i = 0; i < new_unassignedTasks.size(); i++){
			TaskSplit aTask = new_unassignedTasks.get(i);
			int taskid = aTask.getTaskId();
			int ideal_day = aTask.getIdealDay();
			List<Float> task_details = OtherData.get(taskid - 1);
			
			// if the task should not be split again
			// or doing a task makes no more rewards
			if(aTask.getCounter() >= task_details.get(9) || ideal_day == -1){
				new_unassignedTasks.remove(i);
				i--;
				continue;
			}
			
			boolean re_calculate_rewards = false;
			// calculate new traveling time if the task is added
			if(LeftT[ideal_day] > 0){
				// calculate new traveling time for checking
				List<Integer> temp_tasklist = new ArrayList<>();
				// add original tasks one by one
				for(int x = 0; x < Schedule.get(ideal_day).size(); x++){
					temp_tasklist.add(Schedule.get(ideal_day).get(x));
				}

				float 
				if((temp_travelingt - TravelingT[ideal]) < capacity_left[ideal]){
					TravelingT[ideal] = temp_travelingt;
					float processingT = task_details.get(7);
					float task_left = aTask.getUnfinishedPercentage();
					float percentage;
					if((processingT * task_left + temp_travelingt - TravelingT[ideal]) > capacity_left[ideal]){
						float new_capacity_left = capacity_left[ideal] - (temp_travelingt - TravelingT[ideal]);
						// part of the task left
						percentage = new_capacity_left / processingT;
						ProcessingT[ideal] += capacity_left[ideal];
						capacity_left[ideal] = 0;
						available_days--;	// the ideal day has no more capacity
						
						re_calculate_rewards = true; 
					}
					else{
						// complete the task
						percentage = task_left;
						ProcessingT[ideal] += processingT * percentage;
						capacity_left[ideal] -= processingT * percentage;
						capacity_left[ideal] -= TravelingT[ideal];
						capacity_left[ideal] += temp_travelingt;

						unassiTaskSequence.remove(i);
						i--;
					}
					aTask.splitInto(ideal, percentage);
					Rewards[ideal] += ((float)OtherData.get(taskid - 1).get(ideal)) * percentage;
					// add to schedule
					Schedule.get(ideal).add(taskid);
					TaskPercentages.add(aTask);
					System.out.println("Split Task " + taskid + " into day " + (ideal + 1) + " with percentage = " + percentage + ", left " + aTask.getUnfinishedPercentage());
				}
				else{
					unassiTaskSequence.remove(i);
					i--;
					continue;
				}
			}
			else{
				re_calculate_rewards = true;
			}
	}
}