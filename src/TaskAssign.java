import java.util.ArrayList;
import java.util.List;

public class TaskAssign{
	// Attribute
	private List<Float> Workload;
	private float Gamma = 0;
	private int Weekdays = 0;
	private int TaskNum = 0;
	private List<List> OtherData;
	private List<List<Integer>> Schedule;   // for final output
	private List<Integer> UnassignedTasks;	// for final output
	private List<TaskSplit> TaskPercentages;
	private float[][] FinalTaskPercentages;	// for final output 
	private float [] ProcessingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] Rewards = {0, 0, 0, 0, 0, 0, 0};
	
	// Constructor
	public TaskAssign(List<List> Detail){
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
	}


	// First Stage Assignment: use max rewards of a task to decide the best day it should be assigned to
	private void FirstStageAssignment(){
		// Find out each task's maximum rewards and ideal day
		for(int id = 1; id < TaskNum + 1; id++){
			List<Float> task_details = OtherData.get(id - 1);
			float max_rewards = 0;		// the maximum rewards of the task
			int ideal_day = -1;		// the ideal day for the day
			for(int j = 0; j < Weekdays; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
					ideal_day = j;
				}
			}
			Schedule.get(ideal_day).add(id);							// add the taskId to the schedule
			ProcessingT[ideal_day] += task_details.get(7);  		// calculate the processing time
			Rewards[ideal_day] += task_details.get(ideal_day);	// calculate rewards
		}

		// Add traveling time to ProcessingT
		float travelingT = 0;
		for(int j = 0; j < Weekdays; j++){
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(j));
			travelingT = TaskSequence.getMinTravelingT();
			ProcessingT[j] += travelingT;
		}
		System.out.println("FirstStageAssignment:\n" + Schedule);
		System.out.println("---------------------------------------------------------------------------------");
	}
	// End First Stage Assignment

	
	// For each day, sort tasks by "the cost to move that task to another day".
	private void FirstStageTaskSort(){
		List<List<Integer>> originSchedule = Schedule;
//		System.out.println("originSchedule: " + originSchedule);
		List<List<Integer>> newSchedule = new ArrayList<>();
		List<Float> minOPcost = new ArrayList<>();	// the minimum opportunity cost of each day
		
		// Find the minimum opportunity costs.
		for(int j = 0; j < Weekdays; j++){
			List<Integer> tasksOnDay = originSchedule.get(j);
			float min_loss = 5000;
			for(int i = 0; i < tasksOnDay.size(); i++){
				int taskid = tasksOnDay.get(i);
				List<Float> task_details = OtherData.get(taskid - 1);
				float current_rewards = task_details.get(j);
				for(int k = 0; k < Weekdays; k++){
					if(k == j)
						continue;
					float loss = current_rewards - task_details.get(k);
					if(loss < min_loss){
						min_loss = loss;
					}
				}
			}
			tasksOnDay.add(0, j+1);	// tag to identify the weekday of which the tasks belong to
			
			// Sort tasks by the minimum opportunity costs.
			if(j == 0){
				newSchedule.add(tasksOnDay);
				minOPcost.add(min_loss);
				continue;
			}
			boolean insert = false;
			for(int s = 0; s < minOPcost.size(); s++){
				if(min_loss <= minOPcost.get(s)){
					insert = true;
					for(int u = 0; u < newSchedule.size(); u++){
						int day = (int) newSchedule.get(u).get(0);
						if(day == s+1){
							newSchedule.add(u, tasksOnDay);
							break;
						}
					}
				}
				else if(s == minOPcost.size()-1){
					newSchedule.add(tasksOnDay);
				}
				if(insert)
					break;
			}
			minOPcost.add(min_loss);
		}
//		System.out.println("minOPcost: " + minOPcost);
//		System.out.println("newSchedule: " + newSchedule);
		
		Schedule = newSchedule;
	}
	// End FirstStageTaskSort
	
	// Arrange the schedule in the order of the workdays the tasks assigned to, and remove the tag which is at the first element of each list. 
	private void RecoverScheduleOrder(){
		List<List<Integer>> currentSchedule = Schedule;
		List<List<Integer>> newSchedule = new ArrayList<>();
		int [] order = {0, 1, 2, 3, 4, 5, 6};
		for(int i = 0; i <  Weekdays; i++){
			int day = (int) currentSchedule.get(i).get(0) - 1;
			order[day] = i;
		}
		for(int j = 0; j < Weekdays; j++){
			int index = order[j];
			newSchedule.add(currentSchedule.get(index));
			newSchedule.get(j).remove(0);
		}

		Schedule = newSchedule;
	}	// End RecoverScheduleOrder
	

	// First Stage Check: check whether the workload is exceeded after first stage assignment
	private void FirstStageCheck(){
		// Sort tasks by "the cost to move that task to another day".
		FirstStageTaskSort();
		int [] newOrder = {0, 1, 2, 3, 4, 5, 6};	// ex, Tasks for day i have been moved to the "newOrder[i]" element of the Schedule.
		for(int j = 0; j < Weekdays; j++){
			int day = (int) Schedule.get(j).get(0);
			newOrder[day-1] = j;
		}


		for(int j = 0; j < Weekdays; j++){
			List<Integer> current_tasks = Schedule.get(j);  	  
			int current_day = current_tasks.get(0) - 1;
			float workload = Workload.get(current_day) * Gamma;  // calculate workload

			while(ProcessingT[current_day] > workload){		
				int remove_task = -1;
				float min_loss = 5000;
				int move_to_day = -1;
				float time_change = 0;

				// Find the task which has the least "opportunity cost" and assign it to the other day
				for(int k = 1; k < current_tasks.size(); k++){		
					int taskid = current_tasks.get(k);
					List<Float> task_details = OtherData.get(taskid - 1);
					float current_rewards = task_details.get(current_day);
					float inner_min_loss = 5000;
					int inner_move_to_day = -1;
					for(int t = 0; t < Weekdays; t++){
						int some_other_day = (int) Schedule.get(t).get(0) - 1;
						if(t == j)
							continue;
						// case1: if t is before day j, check if the capacity is enough
						else if(t < j){
							float workload_t = Workload.get(some_other_day) * Gamma;
							if(task_details.get(7) < (workload_t - ProcessingT[t])){
								float diff = current_rewards - task_details.get(some_other_day);
								if(diff < inner_min_loss){
									inner_min_loss = diff;
									inner_move_to_day = some_other_day;
								}
							}
						}
						// case2: if t is after day j, suppose that it could be moved to day t
						else{
							float diff = current_rewards - task_details.get(some_other_day);
							if(diff < inner_min_loss){
								inner_min_loss = diff;
								inner_move_to_day = some_other_day;
							}
						}
					}

					// get the min_loss and the task to be removed
					if(inner_min_loss < min_loss){
						min_loss = inner_min_loss;
						remove_task = k;	// k is the list index, not taskId
						move_to_day = inner_move_to_day;
						time_change = task_details.get(7);
					}
				}
				
				int taskid_move;
				if(move_to_day == -1){
					// If there's no way to move a task to another workday, put the first task into the "Unassigned List" 
					// The situation will only happen on the task assignment of Sunday 
					remove_task = 1;
					taskid_move = current_tasks.get(remove_task);
					UnassignedTasks.add(taskid_move);
					time_change = (float) OtherData.get(taskid_move-1).get(7);
					
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to the Unassigned List\n");
				}
				else{
					// move the task to another day
					taskid_move = current_tasks.get(remove_task);  // taskid
					Schedule.get(newOrder[move_to_day]).add(taskid_move);

					ProcessingT[move_to_day] += time_change;
					Rewards[move_to_day] += (float) OtherData.get(taskid_move-1).get(move_to_day);
							
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to day " + (move_to_day + 1) + "\n");
				}
				current_tasks.remove(remove_task);
				List<Integer> tasklist = new ArrayList<>();
				tasklist = current_tasks.subList(1, current_tasks.size());
				TaskSequence TaskSequence = new TaskSequence(tasklist);
				float temp_trvelingt = ProcessingT[current_day] - travelingT;
				travelingT = TaskSequence.getMinTravelingT();
				ProcessingT[current_day] = temp_trvelingt + travelingT;
				ProcessingT[current_day] -= time_change;
				Rewards[current_day] -= (float) OtherData.get(taskid_move-1).get(current_day);
			}
			
			// check correctness of current result
//			System.out.print("Day " + (j + 1) + "\n");
//			for(int d = 0; d < current_tasks.size(); d++){
//				System.out.print(current_tasks.get(d) + ",");
//			}
//			System.out.print("\n");
		}
		
		RecoverScheduleOrder();
		// add tasks with their percentage 1 on their working day to the list "TaskPercentages"
		for(int s = 0; s < Weekdays; s++){
			List<Integer> daily_schedule = Schedule.get(s);
			for(int t = 0; t < daily_schedule.size(); t++){
				int taskid = daily_schedule.get(t);
				TaskSplit aTask = new TaskSplit(taskid);
				aTask.splitInto(s, 1);	// the percentages are all 1 in the first stage
				TaskPercentages.add(aTask);
			}
		}
		
		System.out.print("---------------------------------------------------------------------------------" + "\n");
	}
	// End First Stage Check

	
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
				if(inner_rewards > max_rewards){
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
				float processingT = task_details.get(7);
				float task_left = aTask.getUnfinishedPercentage();
				float percentage;
				if((processingT * task_left) > capacity_left[ideal]){
					// part of the task left
					percentage = capacity_left[ideal] / processingT;
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

	// Print Results
	private void PrintResult(){
		System.out.print("---------------------------------------------------------------------------------" + "\n");
		System.out.print("[Task Assignement Results]" + "\n");
		System.out.print("Processing Time: ");
		for(int j = 0; j < Weekdays; j++){
			System.out.print(ProcessingT[j] + " ");
		}
		System.out.print("\n" + "Rewards: ");
		for(int j = 0; j < Weekdays; j++){
			System.out.print(Rewards[j] + " ");
		}
		System.out.print("\n");
	}
	// End Print Results
	
	public List<List<Integer>> getSchedule(){
		return Schedule;
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

	public List<Integer> getUnassignedTasks(){
		return UnassignedTasks;
	}
	
	public float[] getTimeUsed(){
		return ProcessingT;
	}

	public void ExecuteTaskAssign(){
		FirstStageAssignment();
		FirstStageCheck();
		SecondStageAssignment();
		PrintResult();
	}
}
		