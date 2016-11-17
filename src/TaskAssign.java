import java.util.ArrayList;
import java.util.List;

public class TaskAssign{
	// Attribute
	private List<Float> Workload;
	private float Gamma = 0;
	private int Weekdays = 0;
	private int TaskNum = 0;
	private List<List> OtherData;
	private List<List<Integer>> Schedule;
	private List<Integer> UnassignedTasks;
	private List<TaskSplit> TaskPercentages;
	private float[][] FinalTaskPercentages;	// for final output 
	private float [] Rewards = {0, 0, 0, 0, 0, 0, 0};
	private float [] ProcessingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TravelingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TotalT = {0, 0, 0, 0, 0, 0, 0};
	private float [][] Distance;
	
	// Constructor
	public TaskAssign(List<List> Detail, float[][] distance){
		Workload = new ArrayList<>();
		OtherData = new ArrayList<>();  // rewards, penalty, splitN, processingT
		Schedule = new ArrayList<>();
		UnassignedTasks = new ArrayList<>();
		TaskPercentages = new ArrayList<>();
		List<Integer> Monday = new ArrayList<>();
		List<Integer> Tuesday = new ArrayList<>();
		List<Integer> Wednesday = new ArrayList<>();
		List<Integer> Thursday = new ArrayList<>();
		List<Integer> Friday = new ArrayList<>();
		List<Integer> Saturday = new ArrayList<>();
		List<Integer> Sunday = new ArrayList<>();
		Schedule.add(Monday);
		Schedule.add(Tuesday);
		Schedule.add(Wednesday);
		Schedule.add(Thursday);
		Schedule.add(Friday);
		Schedule.add(Saturday);
		Schedule.add(Sunday);
		Workload = Detail.get(0);
		OtherData = Detail.get(1);
		Gamma = (float) Detail.get(2).get(0);
		Weekdays = (int) Detail.get(3).get(0);
		TaskNum = OtherData.size();
		FinalTaskPercentages = new float[TaskNum][Weekdays];
		Distance = new float[TaskNum][TaskNum];
		for(int i = 0; i < TaskNum; i++){
			for(int j = 0; j < TaskNum; j++){
				Distance[i][j] = distance[i][j];
			}
		}
	}

	// Second Stage Assignment: try to assign those unassigned tasks
	private void SecondStageAssignment(){
		List<Integer> new_unassignedTasks = new ArrayList<>();	 // the final unassigned tasks
		List<TaskSplit> unassiTaskSequence = new ArrayList<>();  // unassigned tasks with more info
		
		float [] capacity_left = {0, 0, 0, 0, 0, 0, 0};
		for(int j = 0; j < Weekdays; j++){
			float workload = Workload.get(j) * Gamma;
			capacity_left[j] = workload - ProcessingT[j];
		}

		for(int i = 0; i < UnassignedTasks.size(); i++){
			// get the maximum rewards and ideal day of each unassigned task
			// the maximum rewards = (max_rewards * the proportion the task may be done) - the split cost 
			int taskid = UnassignedTasks.get(i);
			List<Float> task_details = OtherData.get(taskid - 1);
			float cost = (float) task_details.get(8);	// split cost
			
			float max_rewards = 0;
			int ideal_day = -1;
			
			for(int j = 0; j < Weekdays; j++){
				float inner_rewards;
				inner_rewards = (task_details.get(j) * capacity_left[j]) - cost;
				if(inner_rewards >= max_rewards){
					max_rewards = inner_rewards;
					ideal_day = j;
				}
			}
			TaskSplit aTask = new TaskSplit(taskid, max_rewards, ideal_day);
			
			// set all unassigned tasks in order
			if(i == 0){
				unassiTaskSequence.add(aTask);
				continue;
			}
			else{
				for(int t = 0; t < unassiTaskSequence.size(); t++){
					TaskSplit ptr = unassiTaskSequence.get(t);
					if(max_rewards >= ptr.getMaxRewards()){
						unassiTaskSequence.add(t, aTask);
						break;
					}
					else if(t == unassiTaskSequence.size()-1){
						unassiTaskSequence.add(aTask);
						break;
					}
				}
			}
		}
		
		// check the correctness of sorting
//		for(int i = 0; i < unassiTaskSequence.size(); i++){
//			TaskSplit current_task = unassiTaskSequence.get(i);
//			System.out.println("Task " + current_task.getTaskId() + ": max_rewards " + current_task.getMaxRewards() + " on day " + (current_task.getIdealDay() + 1));
//		}

		int available_days = 0;
		for(int j = 0; j < Weekdays; j++){
			if(capacity_left[j] > 0)
				available_days++;
		}
		// Try to split an unassigned task into the day where the max_rewards exists 
		// if the ideal day is out of capacity, re-calculate its max_rewards
		for(int i = 0; i < unassiTaskSequence.size(); i++){
			TaskSplit aTask = unassiTaskSequence.get(i);
			int taskid = aTask.getTaskId();
			int ideal = aTask.getIdealDay();
			List<Float> task_details = OtherData.get(taskid - 1);
//			System.out.print("taskid = " + taskid);
//			System.out.print(", ideal = " + (ideal+1));
//			System.out.print(", rewards = " + aTask.getMaxRewards() + "\n");
			
			// if the task should not be split again
			if(aTask.getCounter() >= task_details.get(9) || ideal == -1){
				unassiTaskSequence.remove(i);
				i--;
				continue;
			}
			
			boolean re_calculate_rewards = false;
			
			// if there is capacity left on the ideal day
			if(capacity_left[ideal] > 0){
				List<Integer> temp_tasklist = Schedule.get(ideal);
				temp_tasklist.add(aTask.getTaskId());
				TaskSequence TaskSequence = new TaskSequence(temp_tasklist, Distance, Distance.length);
				TaskSequence.Sequence();
				float temp_travelingt = TaskSequence.getMinTravelingT();
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
			
			if(re_calculate_rewards){
				float cost = (float) task_details.get(8);	// split cost
				float max_rewards = 0;
				int ideal_day = -1;
				
				for(int j = 0; j < Weekdays; j++){
					if(capacity_left[j] == 0)
						continue;

					float inner_rewards = 0;
					if(aTask.getUnfinishedPercentage() < capacity_left[j]){
						float task_left = aTask.getUnfinishedPercentage();
						inner_rewards = (task_details.get(j) * task_left) - cost;
					}
					else{
						inner_rewards = (task_details.get(j) * capacity_left[j]) - cost;
					}
					
					if(inner_rewards > max_rewards){
						max_rewards = inner_rewards;
						ideal_day = j;
					}
//					System.out.println("inner = " + inner_rewards);
				}
				if(ideal_day == -1){
					// no way to get positive rewards
					if(aTask.getUnfinishedPercentage() == 1)
						new_unassignedTasks.add(taskid);
					unassiTaskSequence.remove(i);
					i--;
					continue;
				}
				
				aTask.setMaxRewards(max_rewards);
				aTask.setIdealDay(ideal_day);
				// put it back to the sequence
				for(int t = 0; t < unassiTaskSequence.size(); t++){
					TaskSplit ptr = unassiTaskSequence.get(t);
					if(max_rewards >= ptr.getMaxRewards()){
						unassiTaskSequence.add(t, aTask);
						break;
					}
					else if(t == unassiTaskSequence.size()-1){
						unassiTaskSequence.add(aTask);
						break;
					}
				}
				unassiTaskSequence.remove(i);
				i--;
			}
			
			// achieve the limited workload 
			if(available_days == 0)
				break;
		}
		
		// updates unassigned tasks
		UnassignedTasks = new_unassignedTasks;
		// put unassigned tasks into the list "TaskPercentages" without dealing days & percentages 
		for(int u = 0; u < UnassignedTasks.size(); u++){
			int taskid = UnassignedTasks.get(u);
			TaskSplit aUnassignedTask = new TaskSplit(taskid);
			TaskPercentages.add(aUnassignedTask);
		}
	}
	// End Second Stage Assignment

	public List<Float> getProcessingTimeofTasks(){
		List<Float> taskstime = new ArrayList<>();
		for(int i = 0; i < Weekdays; i++){
			float totaltime = 0;
			for(int j = 0; j < Schedule.get(i).size(); j++){
				int taskindex = Schedule.get(i).get(j) - 1;
				List<Float> task_details = OtherData.get(taskindex);
				totaltime += task_details.get(7);
			}
			taskstime.add(totaltime);
		}
		return taskstime;
	}
	
	// Print Results
	private void PrintResult(){
		System.out.print("---------------------------------------------------------------------------------" + "\n");
		System.out.print("[Task Assignement Results]" + "\n");
		System.out.println("Schedule: " + Schedule);
		System.out.print("Rewards: ");
		for(int j = 0; j < Weekdays; j++){
			System.out.print(Rewards[j] + " / ");
		}
		System.out.print("\n");
		System.out.println("Unassigned tasks: " + UnassignedTasks);
		System.out.print("---------------------------------------------------------------------------------" + "\n");
	}
	// End Print Results

	public void ExecuteTaskAssign(){
		FirstStage FirstStage = new FirstStage(Workload, OtherData, Gamma, Weekdays, Schedule, Distance, TaskPercentages);
		FirstStage.FirstStageCheck();
		Schedule = FirstStage.getFirstStageSchedule();
		UnassignedTasks = FirstStage.getFirstStageUnassignedTasks();
		TaskPercentages = FirstStage.getFirstStageTaskPercentages();
		Rewards = FirstStage.getFirstStageRewards();
		ProcessingT = FirstStage.getFirstStageProcessingT();
		TravelingT = FirstStage.getFirstStageTravelingT();
		TotalT = FirstStage.getFirstStageTotalT();
		
//		SecondStageAssignment();
//		PrintResult();
	}

	public List<List<Integer>> getSchedule(){
		return Schedule;
	}

	public List<Integer> getUnassignedTasks(){
		return UnassignedTasks;
	}
	
	public float[][] getTaskPercentages(){
		for(int i = 0; i < OtherData.size(); i++){
			for(int j = 0; j < Weekdays; j++){
				for(int k = 0; k < TaskPercentages.size(); k++){
					TaskSplit aTask = TaskPercentages.get(k);
					int taskid = aTask.getTaskId();
					if(taskid == (i+1)){
						FinalTaskPercentages[i][j] = aTask.getPercentage(j);
					}
					else
						continue;
				}
			}
		}
		return FinalTaskPercentages;
	}
	
	public void printTaskPercentages(){
		for(int i = 0; i < TaskNum; i++){
			for(int j = 0; j < Weekdays; j++){
				System.out.print(FinalTaskPercentages[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
}
		