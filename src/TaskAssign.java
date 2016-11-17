import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private float [] TravelingT = {0, 0, 0, 0, 0, 0, 0};
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


	// First Stage Assignment: use max rewards of a task to decide the best day it should be assigned to
	private void FirstStageAssignment(){
		// Find out each task's maximum rewards and ideal day
		for(int id = 1; id < TaskNum + 1; id++){
			List<Float> task_details = OtherData.get(id - 1);
			float max_rewards = 0;	// the maximum rewards of the task
			int ideal_day = -1;		// the ideal day for the day
			for(int j = 0; j < Weekdays; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
					ideal_day = j;
				}
			}
			
			if(ideal_day == -1){
				System.out.println("here!! id = " + id);
				// put the task into the "Unassigned List" 
				UnassignedTasks.add(id);
				System.out.println("UnassignedTasks = " + UnassignedTasks);
				continue;
			}
			
			Schedule.get(ideal_day).add(id);					// add the taskId to the schedule
			ProcessingT[ideal_day] += task_details.get(7);  	// calculate the processing time
			Rewards[ideal_day] += task_details.get(ideal_day);	// calculate rewards
		}

		// Add traveling time to ProcessingT
		float travelingT = 0;
		for(int j = 0; j < Weekdays; j++){
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(j), Distance, TaskNum);
			TaskSequence.Sequence();
			travelingT = TaskSequence.getMinTravelingT();
			TravelingT[j] = travelingT;
			ProcessingT[j] += travelingT;
		}
		System.out.println("FirstStageAssignment:\n" + Schedule);
		System.out.println("---------------------------------------------------------------------------------");
	}
	// End First Stage Assignment

	
	// FirstStageTaskSort: sort tasks by their cost to be moved to another day
	private List<Map<Integer, Float>> FirstStageTaskSort(){
		List<List<Integer>> oriSchedule = Schedule;
		List<List<Integer>> newSchedule = new ArrayList<>();
		List<Map<Integer, Float>> minOPcost = new ArrayList<>();	// the minimum opportunity cost of each day

		for(int j = 0; j < Weekdays; j++){
			List<Integer> daySchedule = oriSchedule.get(j);
			List<Map<Integer, Float>> tempDaySchedule = new ArrayList<>();	// temporary task list for sorting
			List<Integer> newDaySchedule = new ArrayList<>();
			float day_min_op = 5000;
			for(int i = 0; i < daySchedule.size(); i++){
				int taskid = daySchedule.get(i);
				List<Float> task_details = OtherData.get(taskid - 1);
				float current_rewards = task_details.get(j);
				float task_min_op = 5000;
				// Find minimum opportunity cost (task_min_op) of a task
				for(int k = 0; k < Weekdays; k++){
					if(k == j)
						continue;
					float op = current_rewards - task_details.get(k);
					if(op < task_min_op){
						task_min_op = op;
					}
				}
				if(task_min_op < day_min_op){
					day_min_op = task_min_op;
				}
				// Sort tasks by the minimum opportunity cost (task_min_op)
//				if(i == 0){
//					Map<Integer, Float> taskwithop = new HashMap<Integer, Float>();
//					taskwithop.put(taskid, task_min_op);
//					tempDaySchedule.add(taskwithop);
//				}
//				else{
//					boolean insert = false;
//					for(int l = 0; l < tempDaySchedule.size(); l++){
//						if(task_min_op < (float)tempDaySchedule.get(l).values().toArray()[0]){
//							Map<Integer, Float> taskwithop = new HashMap<Integer, Float>();
//							taskwithop.put(taskid, task_min_op);
//							tempDaySchedule.add(l, taskwithop);
//							insert = true;
//							break;
//						}
//					}
//					if(insert == false){
//						Map<Integer, Float> taskwithop = new HashMap<Integer, Float>();
//						taskwithop.put(taskid, task_min_op);
//						tempDaySchedule.add(taskwithop);
//						insert = true;
//					}
//				}
			}
			
			// Make new day schedule and minOPcost list
//			if(tempDaySchedule.size() > 0){
//				// after sorting tasks in a day, arrange the task list
//				for(int l = 0; l < tempDaySchedule.size(); l++){
//					int id = (int) tempDaySchedule.get(l).keySet().toArray()[0];
//					newDaySchedule.add(id);
//				}
//			}
			Map<Integer, Float> daywithop = new HashMap<Integer, Float>();
			daywithop.put(j, day_min_op);
			minOPcost.add(daywithop);
//			newDaySchedule.add(0, j + 1);		// tag to identify the weekday
//			newSchedule.add(newDaySchedule);
			daySchedule.add(0, j + 1);		// tag to identify the weekday
			newSchedule.add(daySchedule);
		}

//		System.out.println("minOPcost: " + minOPcost);
//		System.out.println("newSchedule: " + newSchedule);
		Schedule = newSchedule;
		return minOPcost;
	}
	// End FirstStageTaskSort


	// FirstStageDaySort: decide priority between weekdays by their minimum opportunity cost
	private void FirstStageDaySort(List<Map<Integer, Float>> minOPcost){
		List<Map<Integer, Float>> tempDayPriority = new ArrayList<>();
		for(int i = 0; i < Weekdays; i++){
			if(i == 0){
				tempDayPriority.add(minOPcost.get(0));
			}
			else{
				boolean insert = false;
				for(int j = 0; j < tempDayPriority.size(); j++){
					if((float) minOPcost.get(i).values().toArray()[0] < (float) tempDayPriority.get(j).values().toArray()[0]){
						tempDayPriority.add(j, minOPcost.get(i));
						insert = true;
						break;
					}
				}
				if(insert == false){
					tempDayPriority.add(minOPcost.get(i));
					insert = true;
				}
			}
		}
		// Sort weekdays
		List<List<Integer>> newSchedule = new ArrayList<>();
		for(int i = 0; i < Weekdays; i++){
			int dayIndex = (int) tempDayPriority.get(i).keySet().toArray()[0];
			newSchedule.add(Schedule.get(dayIndex));
		}
//		System.out.println("newSchedule: " + newSchedule);
		Schedule = newSchedule;
	}
	// End FirstStageDaySort


	// RecoverScheduleOrder: Arrange the schedule in the order of weekdays, and remove the day tag
	private void RecoverScheduleOrder(){
		List<List<Integer>> oriSchedule = Schedule;
		List<List<Integer>> newSchedule = new ArrayList<>();
		int [] order = {0, 1, 2, 3, 4, 5, 6};
		for(int i = 0; i <  Weekdays; i++){
			int day = (int) oriSchedule.get(i).get(0) - 1;
			order[day] = i;
		}
		for(int j = 0; j < Weekdays; j++){
			int index = order[j];
			newSchedule.add(oriSchedule.get(index));
			newSchedule.get(j).remove(0);
		}

		Schedule = newSchedule;
	}	// End RecoverScheduleOrder
	

	// First Stage Check: check whether the workload is exceeded after first stage assignment and make movements
	private void FirstStageCheck(){
		// sort tasks and weekdays
		List<Map<Integer, Float>> minOPcost = FirstStageTaskSort();
		FirstStageDaySort(minOPcost);
		int [] newOrder = {0, 1, 2, 3, 4, 5, 6};	// ex, Tasks for day i have been moved to the "newOrder[i]" element of the Schedule.
		for(int j = 0; j < Weekdays; j++){
			int day = (int) Schedule.get(j).get(0);
			newOrder[day-1] = j;
		}

		for(int j = 0; j < Weekdays; j++){
			List<Integer> current_tasks = Schedule.get(j);  	  
			int current_day = current_tasks.get(0) - 1;
			float workload = Workload.get(current_day) * Gamma;	// available workload

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
					// If there's no way to move a task to another weekday, put the task into the "Unassigned List" 
					// The situation will only happen on the task assignment of Sunday 
					remove_task = 1;
					taskid_move = current_tasks.get(remove_task);
					UnassignedTasks.add(taskid_move);
					time_change = (float) OtherData.get(taskid_move-1).get(7);
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to the Unassigned List\n");
//					System.out.print(Arrays.toString(ProcessingT) + "\n");
				}
				else{
					// move the task to another day and calculate new processing time
					taskid_move = current_tasks.get(remove_task);  // taskid
					Schedule.get(newOrder[move_to_day]).add(taskid_move);
					TaskSequence TaskSequence = new TaskSequence(Schedule.get(newOrder[move_to_day]), Distance, TaskNum);
					TaskSequence.Sequence();
					float newTravelingT = TaskSequence.getMinTravelingT();
					ProcessingT[move_to_day] -= TravelingT[move_to_day];
					ProcessingT[move_to_day] += newTravelingT;
					ProcessingT[move_to_day] += time_change;
					TravelingT[move_to_day] = newTravelingT;	// update TravelingT;
					Rewards[move_to_day] += (float) OtherData.get(taskid_move-1).get(move_to_day);
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to day " + (move_to_day + 1) + "\n");
				}
				current_tasks.remove(remove_task);
				TaskSequence TaskSequence = new TaskSequence(current_tasks, Distance, TaskNum);
				TaskSequence.Sequence();
				float newTravelingT = TaskSequence.getMinTravelingT();
				ProcessingT[current_day] -= TravelingT[current_day];
				ProcessingT[current_day] += newTravelingT;
				ProcessingT[current_day] -= time_change;
				TravelingT[current_day] = newTravelingT;	// update TravelingT;
				Rewards[current_day] -= (float) OtherData.get(taskid_move-1).get(current_day);
				System.out.print(Arrays.toString(ProcessingT) + "\n");
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

	public void ExecuteTaskAssign(){
		FirstStageAssignment();
		FirstStageCheck();
		SecondStageAssignment();
		PrintResult();
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
		