import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskAssign{
	// attribute
	private List<Integer> Workload;
	private float Gamma;
	private List<List> OtherData;
	private List<List> Schedule;
	private int [] TotalProcessingT = {0, 0, 0, 0, 0, 0, 0, 0};
	private int weekdays = 7;
	
	// Constructor
	public TaskAssign(){
		Workload = new ArrayList<>();
		Gamma = 0;
		OtherData = new ArrayList<>();  // rewards, penalty, splitN, processingT
		Schedule = new ArrayList<>();   // schedule for final output
		List<Integer> Monday = new ArrayList<>();
		List<Integer> Tuesday = new ArrayList<>();
		List<Integer> Wednesday = new ArrayList<>();
		List<Integer> Thursday = new ArrayList<>();
		List<Integer> Friday = new ArrayList<>();
		List<Integer> Saturday = new ArrayList<>();
		List<Integer> Sunday = new ArrayList<>();
		List<Integer> Unassigned = new ArrayList<>();	// for unassigned tasks
		Schedule.add(Monday);
		Schedule.add(Tuesday);
		Schedule.add(Wednesday);
		Schedule.add(Thursday);
		Schedule.add(Friday);
		Schedule.add(Saturday);
		Schedule.add(Sunday);
		Schedule.add(Unassigned);
	}
	
	// Constructor
	public void TaskAssign(List<List> Detail){
		Workload = Detail.get(0);
		Gamma = (float) Detail.get(1).get(0);
		OtherData = Detail.get(2);
		
	}
	
	// First Stage Assignment: use max rewards of a task to decide the best day it should be assigned to
	private void FirstStageAssignment(){
		for(int id = 1; id < OtherData.size() + 1; id++){
			List<Integer> task_details = OtherData.get(id - 1);
			// System.out.println(task_details);
			int max_rewards = 0;
			int assign_to_day = -1;
			for(int j = 0; j < weekdays; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
					assign_to_day = j;
				}
			}
			Schedule.get(assign_to_day).add(id);  // add the taskId to the schedule
			TotalProcessingT[assign_to_day] += task_details.get(7);  // calculate processing time
		}
	}
	// End First Stage Assignment

	// First Stage Check: check whether the workload is exceeded after first stage assignment
	private void FirstStageCheck(){
		for(int j = 0; j < weekdays; j++){
			List<Integer> recent_tasks = Schedule.get(j);  	  // maybe current_tasks is better?
			float workload = Workload.get(j) * Gamma;  // calculate workload

			while(TotalProcessingT[j] > workload){		
				int remove_task = -1;
				int min_loss = 1000;
				int move_to_day = -1;
				int time_change = 0;

				// Find the task which has the least "opportunity cost" and assign it to the other day
				for(int k = 0; k < recent_tasks.size(); k++){		
					int taskid = recent_tasks.get(k);
					List<Integer> task_details = OtherData.get(taskid - 1);
					int recent_rewards = task_details.get(j);
					int inner_min_loss = 1000;
					int inner_move_to_day = -1;
					for(int t = 0; t < weekdays + 1; t++){
						if(t == j)
							continue;
						// case1: if t is before day j, check if the capacity is enough
						else if(t < j){
							float workload_t = Workload.get(t) * Gamma;
							if(task_details.get(7) < (workload_t - TotalProcessingT[t])){
								int diff = recent_rewards - task_details.get(t);
								if(diff < inner_min_loss){
									inner_min_loss = diff;
									inner_move_to_day = t;
								}
							}
						}
						// case2: if t is after day j, suppose that it could be moved to day t
						else{
							int diff = recent_rewards - task_details.get(t);
							if(diff < inner_min_loss){
								inner_min_loss = diff;
								inner_move_to_day = t;
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

				// move the task to another day
				int taskid_move = recent_tasks.get(remove_task);  // taskid
				Schedule.get(move_to_day).add(taskid_move);
				recent_tasks.remove(remove_task);
//				List<Integer> temp = Schedule.get(move_to_day);
//				temp.add(task_to_move);
//				Schedule.set(move_to_day, temp);
//				recent_tasks.remove(remove_task);
						
				TotalProcessingT[j] -= time_change;
				TotalProcessingT[move_to_day] += time_change;
						
				System.out.print("Task " + taskid_move + " from day " + (j + 1) + " to day " + (move_to_day + 1) + "\n");
			}
			
			// check correctness of current result
//			System.out.print("Day " + (j + 1) + "\n");
//			for(int d = 0; d < recent_tasks.size(); d++){
//				System.out.print(recent_tasks.get(d) + ",");
//			}
//			System.out.print("\n");
		}
		System.out.print("----------------------------------------------------" + "\n");
	}
	// End First Stage Check
	
	// Second Stage Assignment: try to assign those unassigned tasks
	private void SecondStageAssignment(){
		List<Integer> unassignedTasks = getUnassignedTasks();	 // will later be updated and replaced 
		List<Integer> new_unassignedTasks = new ArrayList<>();	 // the final unassigned tasks
		List<TaskSplit> unassiTaskSequence = new ArrayList<>();  // unassigned tasks with more info
		List<TaskSplit> splitTasks = new ArrayList<>();

		for(int i = 0; i < unassignedTasks.size(); i++){
			// get the absolute maximum rewards and ideal day of each unassigned task
			int taskid = unassignedTasks.get(i);
			List<Integer> task_details = OtherData.get(taskid - 1);
			int max_rewards = 0;
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
					// forget to sort by ideal date
					if(max_rewards >= current_task.getMaxRewards()){
						unassiTaskSequence.add(t, aTask);
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
				List<Integer> task_details = OtherData.get(taskid - 1);
				double processingT = task_details.get(7);
				double task_left = 1;
				int split_ctr = 0;
				while(task_left > 0){
					split_ctr += 1;
					double capacity_left = workload - TotalProcessingT[ideal];
					double percentage;
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
					aTask.splitInto(ideal, percentage);

					// add to schedule
					Schedule.get(ideal).add(taskid);
//					List<Integer> temp = Schedule.get(ideal);
//					temp.add(taskId);
//					Schedule.set(ideal, temp);
					System.out.println("Split Task " + taskid + " into day " + (ideal + 1) + " with percentage = " + percentage + ", left " + task_left);

					// if the task is still left, find new ideal day
					if(task_left > 0){
						// first check whether it can be split more
						if(split_ctr == task_details.get(9)){
							break;
						}
						else {
							int pre_max_rewards = task_details.get(ideal);
							int new_max = -1000;
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
				splitTasks.add(aTask);
			}
			// no capacity left on ideal day, abandon the task
			else{
				new_unassignedTasks.add(taskid);
				continue;
			}
		}
		
		// updates unassigned tasks
		Schedule.set(7, new_unassignedTasks);
	}
	// End Second Stage Assignment

	// Print Results
	private void PrintResult(){
		System.out.print("----------------------------------------------------------" + "\n");
		System.out.print("[Task Assignement Results]" + "\n");
		System.out.print("Processing Time: ");
		for(int j = 0; j < weekdays; j++){
			System.out.print(TotalProcessingT[j] + " ");
		}
		System.out.print("\n" + "Rewards: ");
		for(int j = 0; j < weekdays; j++){
			int total_rewards=0;
			int index;
			List<Integer> recent_tasks = Schedule.get(j);
			for(int k = 0; k < recent_tasks.size(); k++){
				index = recent_tasks.get(k);
				List<Integer> task_detail = OtherData.get(index - 1);
				total_rewards += task_detail.get(j);
			}
			System.out.print(total_rewards + " ");
		}
		System.out.print("\n");
	}
	// End Print Results
	
	public List<List> getSchedule(){
		return Schedule;
	}
	
	public List<Integer> getUnassignedTasks(){
		return Schedule.get(7);
	}
	
	public void ExecuteTaskAssign(){
		FirstStageAssignment();
		FirstStageCheck();
		SecondStageAssignment();
		PrintResult();
	}
}
		