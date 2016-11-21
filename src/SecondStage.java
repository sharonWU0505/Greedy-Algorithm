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
			int taskid = UnassignedTasks.get(i);
			List<Float> task_details = OtherData.get(taskid - 1);
			float split_cost = (float) task_details.get(8);	// split cost
			float max_rewards = 0;
			int ideal_day = -1;
			for(int j = 0; j < Weekdays; j++){
				float inner_rewards = 0;
				float possi_percentage = LeftT[j] / task_details.get(7);
				inner_rewards = (task_details.get(j) * possi_percentage) - split_cost;
				if(inner_rewards >= max_rewards){
					max_rewards = inner_rewards;
					ideal_day = j;
				}
			}
			TaskSplit aTask = new TaskSplit(taskid, max_rewards, ideal_day);
			
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
		return new_unassignedTasks;
	}

	private void SecondStageAssignment(){
		List<TaskSplit> new_unassignedTasks = SecondStageTaskSort();
		List<Integer> final_unassignedTasks = new ArrayList<>();	// the final unassigned tasks
		
		
	}
}