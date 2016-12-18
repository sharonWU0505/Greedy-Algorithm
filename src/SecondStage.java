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
	private List<Integer> UnfinishedTasks = new ArrayList<>();
	private float [] Rewards = {0, 0, 0, 0, 0, 0, 0};
	private float [] Penalty = {0, 0, 0, 0, 0, 0, 0};
	private float [] ProcessingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TravelingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TotalT = {0, 0, 0, 0, 0, 0, 0};
	private float [] LeftT = {0, 0, 0, 0, 0, 0, 0};
	private float [][] Distance;
	private float [] ComDistance;
	private List<TaskSplit> TaskPercentages = new ArrayList<>();
	
	// Constructor
	public SecondStage(List<Float> Workload, List<List> OtherData, float Gamma, int Weekdays, List<List<Integer>> Schedule, List<Integer> UnassignedTasks, float [] Rewards, float [] ProcessingT, float [] TravelingT, float [] TotalT, float [][] distance, float [] comdistance, List<TaskSplit> TaskPercentages){
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
		ComDistance = new float[TaskNum];
		for(int i = 0; i < TaskNum; i++){
			this.ComDistance[i] = comdistance[i];
		}
		this.TaskPercentages = TaskPercentages;
	}
	
	// SecondStageSort: sort unassigned tasks by their maximum possible rewards
	private List<TaskSplit> SecondStageTaskSort(){
		System.out.println("SecondStageTaskSort:");
//		System.out.println("Left Time: " + Arrays.toString(LeftT));

		List<TaskSplit> new_unassignedTasks = new ArrayList<>();  // unassigned tasks with more details
		for(int i = 0; i < UnassignedTasks.size(); i++){
			// get the maximum rewards and ideal day of each unassigned task
			// the maximum rewards = (max_rewards * the proportion the task could be done) - the split cost
			// already take traveling time into the calculation of max_rewards
			int taskid = UnassignedTasks.get(i);
			List<Float> task_details = OtherData.get(taskid - 1);
			float split_cost = (float) task_details.get(8);	// split cost
			float new_totalt = 0;
			float new_travelingt = 0;
			float max_rewards = 0;
			int ideal_day = -1;
			float possi_percentage = 0;
			for(int j = 0; j < Weekdays; j++){
				if(LeftT[j] > 0){
					// calculate new traveling time for checking
					List<Integer> temp_tasklist = new ArrayList<>();
					// add original tasks one by one
					for(int x = 0; x < Schedule.get(j).size(); x++){
						temp_tasklist.add(Schedule.get(j).get(x));
					}
					temp_tasklist.add(taskid);
					// calculate new traveling time for checking
					Greedy Greedy = new Greedy(temp_tasklist, Distance, ComDistance, TaskNum);
					new_travelingt = Greedy.doGreedy();
					new_totalt = TotalT[j] - TravelingT[j] + new_travelingt;	// new total time used except partial processing time
					float newLeftT = Workload.get(j) * Gamma - new_totalt;			// time left for doing more tasks
					
//					System.out.print(temp_tasklist + ": ");
//					System.out.println("new_travelingt = " + new_travelingt + " ,newLeftT = " + newLeftT);
					
					if(newLeftT > 0){
						possi_percentage = newLeftT / task_details.get(7);
						if(possi_percentage >= 1){
							possi_percentage = 1;
						}
						float inner_rewards = (task_details.get(j) * possi_percentage) - split_cost;
						if(inner_rewards >= max_rewards){
							new_totalt += task_details.get(7) * possi_percentage;	// new total time left plus partial processing time
							max_rewards = inner_rewards;
							ideal_day = j;
						}
					}
				}
				else{
					continue;
				}
			}
			TaskSplit aTask = new TaskSplit(taskid, max_rewards, ideal_day, possi_percentage);
			 
			// set all unassigned tasks in order by their maximum possible rewards
			if(i == 0){
				new_unassignedTasks.add(aTask);
			}
			else{
				boolean insert = false;
				for(int k = 0; k < new_unassignedTasks.size(); k++){
					TaskSplit ptr = new_unassignedTasks.get(k);
					if(max_rewards >= ptr.getMaxRewards()){
						new_unassignedTasks.add(k, aTask);
						insert = true;
						break;
					}
				}
				if(insert == false){
					new_unassignedTasks.add(aTask);
				}
			}
		}
		// check the correctness of sorting
		for(int i = 0; i < new_unassignedTasks.size(); i++){
			TaskSplit current_task = new_unassignedTasks.get(i);
			System.out.println("Task " + current_task.getTaskId() + ": max_rewards " + current_task.getMaxRewards() + " on day " + (current_task.getIdealDay() + 1));
//			System.out.println(current_task.getPossiPercentage() + " / " + current_task.getTravelingt() + " / " + current_task.getTotalt());
		}
		return new_unassignedTasks;
	}

	public void SecondStageAssignment(){
		List<TaskSplit> new_unassignedTasks = SecondStageTaskSort();
		List<Integer> final_unassignedTasks = new ArrayList<>();	// the final unassigned tasks

		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("SecondStageAssignment:");
		// Try to split an unassigned task into the day where the max_rewards exists 
		// if the ideal day is out of capacity or a task can't reach its maximum possible rewards
		// re-calculate its max_rewards
		for(int i = 0; i < new_unassignedTasks.size(); i++){
			TaskSplit aTask = new_unassignedTasks.get(i);
			int taskid = aTask.getTaskId();
			int ideal_day = aTask.getIdealDay();
			List<Float> task_details = OtherData.get(taskid - 1);
			
			// if the task could not be split again
			// or doing a task makes will do harm to rewards
			// never try to assign this task
			if(aTask.getCounter() >= task_details.get(9) || ideal_day == -1){
				if(aTask.getCounter() >= task_details.get(9)){
					UnfinishedTasks.add(taskid);
				}else{
					final_unassignedTasks.add(taskid);
				}
				new_unassignedTasks.remove(i);
				i--;
			}
			else{
				boolean re_calculate_rewards = false;
				
				// calculate new traveling time for checking
				List<Integer> temp_tasklist = new ArrayList<>();
				// add original tasks one by one
				for(int x = 0; x < Schedule.get(ideal_day).size(); x++){
					temp_tasklist.add(Schedule.get(ideal_day).get(x));
				}
				temp_tasklist.add(taskid);
				Greedy Greedy = new Greedy(temp_tasklist, Distance, ComDistance, TaskNum);
				float new_travelingt = Greedy.doGreedy();
				float temp_leftT = LeftT[ideal_day] + TravelingT[ideal_day] - new_travelingt;
				// if time left for at least adding traveling time
				if(LeftT[ideal_day] > 0 && temp_leftT > 0){
					float percentage = aTask.getPossiPercentage();
					float processingT = task_details.get(7) * percentage;
					float time_needed = new_travelingt - TravelingT[ideal_day] + processingT;
					// complete the task
					if(LeftT[ideal_day] > time_needed){
						TotalT[ideal_day] += time_needed;
						ProcessingT[ideal_day] += processingT;
						TravelingT[ideal_day] = new_travelingt;
						LeftT[ideal_day] -= time_needed;
						Rewards[ideal_day] += task_details.get(ideal_day) * aTask.getUnfinishedPercentage();
					}
					// cannot complete, split the task and add a new task to the UassignedTasks
					else{
						percentage = temp_leftT / task_details.get(7);
						ProcessingT[ideal_day] += task_details.get(7) * percentage;
						TravelingT[ideal_day] = new_travelingt;
						TotalT[ideal_day] = ProcessingT[ideal_day] + TravelingT[ideal_day];
						LeftT[ideal_day] = 0;
						Rewards[ideal_day] += task_details.get(ideal_day) * percentage;
						
						re_calculate_rewards = true;
					}
					// update Schedule
					aTask.splitInto(ideal_day, percentage);
					Schedule.get(ideal_day).add(taskid);
					if(percentage < 1){
						Penalty[ideal_day] += task_details.get(8);
					}
					TaskPercentages.add(aTask);
					
					System.out.print("Split Task " + taskid + " into day " + (ideal_day + 1) + " with percentage = " + percentage + ", left " + aTask.getUnfinishedPercentage() + "\n");
				}
				// no time left
				else{
					re_calculate_rewards = true;
				}
				
				// remove this task
				new_unassignedTasks.remove(i);
				i--;
				
				if(re_calculate_rewards){
					// make a new task
					reCalculateRewards(aTask);
					
					if(aTask.getIdealDay() == -1){
						// no way to get positive rewards
						if(aTask.getUnfinishedPercentage() == 1){
							final_unassignedTasks.add(taskid);
						}
						else{
							UnfinishedTasks.add(taskid);
						}
					}
					else{
						// put it back to the sequence
						boolean insert = false;
						for(int t = 0; t < new_unassignedTasks.size(); t++){
							TaskSplit ptr = new_unassignedTasks.get(t);
							if(aTask.getMaxRewards() >= ptr.getMaxRewards()){
								new_unassignedTasks.add(t, aTask);
								insert = true;
								break;
							}
						}
						if(insert == false){
							new_unassignedTasks.add(aTask);	// add at the end
						}
					}
				}
			}
		}

		// updates unassigned tasks
		UnassignedTasks = final_unassignedTasks;

		// put unassigned tasks into the list "TaskPercentages" without dealing days & percentages 
		for(int u = 0; u < UnassignedTasks.size(); u++){
			int taskid = UnassignedTasks.get(u);
			TaskSplit aUnassignedTask = new TaskSplit(taskid);
			TaskPercentages.add(aUnassignedTask);
		}
	}
	
	private void reCalculateRewards(TaskSplit aTask){
		int taskid = aTask.getTaskId();
		List<Float> task_details = OtherData.get(taskid - 1);
		float split_cost = (float) task_details.get(8);	// split cost
		float new_totalt = 0;
		float new_travelingt = 0;
		float max_rewards = 0;
		int ideal_day = -1;
		float possi_percentage = 0;
		
		for(int j = 0; j < Weekdays; j++){
			if(LeftT[j] == 0){
				continue;
			}
			// calculate new traveling time for checking
			List<Integer> temp_tasklist = new ArrayList<>();
			// add original tasks one by one
			for(int x = 0; x < Schedule.get(j).size(); x++){
				temp_tasklist.add(Schedule.get(j).get(x));
			}
			temp_tasklist.add(taskid);
			// calculate new traveling time for checking
			Greedy Greedy = new Greedy(temp_tasklist, Distance, ComDistance, TaskNum);
			new_travelingt = Greedy.doGreedy();
			new_totalt = TotalT[j] - TravelingT[j] + new_travelingt;
			float newLeftT = Workload.get(j) * Gamma - new_totalt;
			if(newLeftT > 0){
				possi_percentage = newLeftT / task_details.get(7);
				if(aTask.getUnfinishedPercentage() < possi_percentage){
					possi_percentage = aTask.getUnfinishedPercentage();
				}
				float inner_rewards = (task_details.get(j) * possi_percentage) - split_cost;
				if(inner_rewards >= max_rewards){
					new_totalt += task_details.get(7) * possi_percentage;
					max_rewards = inner_rewards;
					ideal_day = j;
				}
			}
		}
		aTask.setDetails(max_rewards, ideal_day, possi_percentage);
	}
	
	public List<List<Integer>> getSchedule(){
		return Schedule;
	}
	
	public List<Integer> getUnassignedTasks(){
		return UnassignedTasks;
	}
	
	public List<Integer> getUnfinishedTasks(){
		return UnfinishedTasks;
	}

	public float[] getRewards(){
		return Rewards;
	}
	
	public float[] getPenalty(){
		return Penalty;
	}
	
	public float[] getProcessingT(){
		return ProcessingT;
	}
	
	public float[] getTravelingT(){
		return TravelingT;
	}
	
	public float[] getTotalT(){
		return TotalT;
	}
	
	public List<TaskSplit> getTaskPercentages(){
		return TaskPercentages;
	}
}