import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskAssign{
	// attribute
	private List<Float> Workload;
	private float Gamma;
	private int weekdays = 0;
	private List<List> OtherData;
	private List<List> Schedule;
	private List<Integer> Unassigned;
	private List<TaskSplit> TaskPercentages;
	private float [] TotalProcessingT = {0, 0, 0, 0, 0, 0, 0};
	private float [] TotalRewards = {0, 0, 0, 0, 0, 0, 0};
	
	// Constructor
	public TaskAssign(){
		Workload = new ArrayList<>();
		Gamma = 0;
		OtherData = new ArrayList<>();  // rewards, penalty, splitN, processingT
		Schedule = new ArrayList<>();   // schedule for final output
		Unassigned = new ArrayList<>();	// for unassigned tasks
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
	}
	
	// Constructor
	public void TaskAssign(List<List> Detail){
		Workload = Detail.get(0);
		OtherData = Detail.get(1);
		Gamma = (float) Detail.get(2).get(0);
		weekdays = (int) Detail.get(3).get(0);
	}
	
	// First Stage Assignment: use max rewards of a task to decide the best day it should be assigned to
	private void FirstStageAssignment(){
		for(int id = 1; id < OtherData.size() + 1; id++){
			List<Float> task_details = OtherData.get(id - 1);
			float max_rewards = 0;
			int assign_to_day = -1;
			for(int j = 0; j < weekdays; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
					assign_to_day = j;
				}
			}
			Schedule.get(assign_to_day).add(id);  // add the taskId to the schedule
			TotalProcessingT[assign_to_day] += task_details.get(7);  		// calculate processing time
			TotalRewards[assign_to_day] += task_details.get(assign_to_day);	// calculate rewards
		}
		System.out.println("FirstStageAssignment:\n" + Schedule);
		System.out.println("---------------------------------------------------------------------------------");
	}
	// End First Stage Assignment
	
	
	// For each day, sort tasks by "the cost to move that task to another day".
	private void FirstStageTaskSort(){
		List<List> originSchedule = Schedule;
//		System.out.println("originSchedule: " + originSchedule);
		List<List> newSchedule = new ArrayList<>();
		List<Float> minOPcost = new ArrayList<>();	// the minimum opportunity cost of each day
		
		// Find the minimum opportunity costs.
		for(int j = 0; j < weekdays; j++){
			List<Integer> tasksOnDay = originSchedule.get(j);
			float min_loss = 5000;
			for(int i = 0; i < tasksOnDay.size(); i++){
				int taskid = tasksOnDay.get(i);
				List<Float> task_details = OtherData.get(taskid - 1);
				float current_rewards = task_details.get(j);
				for(int k = 0; k < weekdays; k++){
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
		List<List> currentSchedule = Schedule;
		List<List> newSchedule = new ArrayList<>();
		int [] order = {0, 1, 2, 3, 4, 5, 6};
		for(int i = 0; i <  weekdays; i++){
			int day = (int) currentSchedule.get(i).get(0) - 1;
			order[day] = i;
		}
		for(int j = 0; j < weekdays; j++){
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
		for(int j = 0; j < weekdays; j++){
			int day = (int) Schedule.get(j).get(0);
			newOrder[day-1] = j;
		}
//		for(int v = 0; v < weekdays; v++){
//			System.out.print(newOrder[v] + " ");
//		}

		for(int j = 0; j < weekdays; j++){
			List<Integer> current_tasks = Schedule.get(j);  	  
			int current_day = current_tasks.get(0) - 1;
			float workload = Workload.get(current_day) * Gamma;  // calculate workload

			while(TotalProcessingT[current_day] > workload){		
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
					for(int t = 0; t < weekdays; t++){
						int some_other_day = (int) Schedule.get(t).get(0) - 1;
						if(t == j)
							continue;
						// case1: if t is before day j, check if the capacity is enough
						else if(t < j){
							float workload_t = Workload.get(some_other_day) * Gamma;
							if(task_details.get(7) < (workload_t - TotalProcessingT[t])){
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
					Unassigned.add(taskid_move);
					time_change = (float) OtherData.get(taskid_move-1).get(7);
					
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to the Unassigned List\n");
				}
				else{
					// move the task to another day
					taskid_move = current_tasks.get(remove_task);  // taskid
					Schedule.get(newOrder[move_to_day]).add(taskid_move);

					TotalProcessingT[move_to_day] += time_change;
					TotalRewards[move_to_day] += (float) OtherData.get(taskid_move-1).get(move_to_day);
							
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to day " + (move_to_day + 1) + "\n");
				}
				current_tasks.remove(remove_task);
				TotalProcessingT[current_day] -= time_change;
				TotalRewards[current_day] -= (float) OtherData.get(taskid_move-1).get(current_day);
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
		for(int s = 0; s < weekdays; s++){
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

		for(int i = 0; i < Unassigned.size(); i++){
			// get the absolute maximum rewards and ideal day of each unassigned task
			int taskid = Unassigned.get(i);
			List<Float> task_details = OtherData.get(taskid - 1);
			float max_rewards = 0;
			int ideal_day = -1;
			for(int j = 0; j < weekdays; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
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
					TaskSplit current_task = unassiTaskSequence.get(t);
					if(max_rewards >= current_task.getMaxRewards()){
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

		// Try to split an unassigned task into the day where the max_rewards exists 
		// But if the ideal day is out of capacity, the task will be abandoned directly QQ
		boolean[] Available = {true, true, true, true, true, true, true};
		for(int i = 0; i < weekdays; i++){
			float workload = Workload.get(i) * Gamma;
			if(workload <= TotalProcessingT[i]){
				Available[i] = false;
			}
		}

		for(int i = 0; i < unassiTaskSequence.size(); i++){
			TaskSplit aTask = unassiTaskSequence.get(i);
			int taskid = aTask.getTaskId();
			int ideal = aTask.getIdealDay();
			float workload = Workload.get(ideal) * Gamma;
			
			// if there is capacity left on ideal day
			if(Available[ideal] == true){
				List<Float> task_details = OtherData.get(taskid - 1);
				float processingT = task_details.get(7);
				float task_left = 1;
				int split_ctr = 0;
				while(task_left > 0){
					split_ctr += 1;
					float capacity_left = workload - TotalProcessingT[ideal];
					float percentage;
					if((processingT * task_left) > capacity_left){
						percentage = capacity_left / processingT;
						TotalProcessingT[ideal] += capacity_left;
						Available[ideal] = false;	// the ideal day has no more capacity
					}
					else{
						percentage = task_left;
						TotalProcessingT[ideal] += processingT * percentage;
					}
					task_left -= percentage;
					TotalRewards[ideal] += ((float)OtherData.get(taskid - 1).get(ideal)) * percentage;
					aTask.splitInto(ideal, percentage);

					// add to schedule
					Schedule.get(ideal).add(taskid);
					System.out.println("Split Task " + taskid + " into day " + (ideal + 1) + " with percentage = " + percentage + ", left " + task_left);

					// if the task is still left, find new ideal day
					if(task_left > 0){
						// first check whether it can be split more
						if(split_ctr == task_details.get(9)){
							break;
						}
						else {
							float pre_max_rewards = task_details.get(ideal);
							float new_max = -1000;
							int temp_choice = -1;
							for(int j = 0; j < 7; j++){
								if((Available[j] == true) && (j != ideal) && (task_details.get(j) > new_max) && (task_details.get(j) <= pre_max_rewards)){
									new_max = task_details.get(j);
									temp_choice = j;  // the new ideal day
								}
							}
							if(temp_choice != -1){
								ideal = temp_choice;
							}
							else{
								break;	// unable to do the task anymore
							}
						}
					}
				}
				TaskPercentages.add(aTask);
			}
			// no capacity left on ideal day, abandon the task
			else{
				new_unassignedTasks.add(taskid);
				continue;
			}
		}
		
		// updates unassigned tasks
		Unassigned = new_unassignedTasks;
		// put unassigned tasks into the list "TaskPercentages" without dealing days & percentages 
		for(int u = 0; u < Unassigned.size(); u++){
			int taskid = Unassigned.get(u);
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
		for(int j = 0; j < weekdays; j++){
			System.out.print(TotalProcessingT[j] + " ");
		}
		System.out.print("\n" + "Rewards: ");
		for(int j = 0; j < weekdays; j++){
			System.out.print(TotalRewards[j] + " ");
		}
		System.out.print("\n");
	}
	// End Print Results
	
	public List<List> getSchedule(){
		return Schedule;
	}
	
	public float[][] getTaskPercentages(){
		float[][] finalTaskPercentages = new float[OtherData.size()][weekdays];

		for(int i = 0; i < OtherData.size(); i++){
			for(int j = 0; j < weekdays; j++){
				for(int k = 0; k < TaskPercentages.size(); k++){
					TaskSplit aTask = TaskPercentages.get(k);
					int taskid = aTask.getTaskId();
					if(taskid == (i+1)){
						System.out.print(aTask.getPercentage(j) + " ");
						finalTaskPercentages[i][j] = aTask.getPercentage(j);
					}
					else
						continue;
				}
			}
			System.out.print("\n");
		}
		
		return finalTaskPercentages;
	}
	
	public List<Integer> getUnassignedTasks(){
		return Unassigned;
	}
	
	public float[] getTimeUsed(){
		return TotalProcessingT;
	}

	
	
	public void ExecuteTaskAssign(){
		FirstStageAssignment();
		FirstStageCheck();
		SecondStageAssignment();
		PrintResult();
	}
}
		