import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstStage{
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
	private float [][] Distance;
	private List<TaskSplit> TaskPercentages = new ArrayList<>();;
	
	// Constructor
	public FirstStage(List<Float> Workload, List<List> OtherData, float Gamma, int Weekdays, List<List<Integer>> Schedule, float [][] distance, List<TaskSplit> TaskPercentages){
		this.Workload = Workload;
		this.OtherData = OtherData;
		this.Gamma = Gamma;
		this.Weekdays = Weekdays;
		this.Schedule = Schedule;
		TaskNum = OtherData.size();
		Distance = new float[TaskNum][TaskNum];
		for(int i = 0; i < TaskNum; i++){
			for(int j = 0; j < TaskNum; j++){
				this.Distance[i][j] = distance[i][j];
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
				if(task_details.get(j) >= max_rewards){
					max_rewards = task_details.get(j);
					ideal_day = j;
				}
			}
			if(ideal_day == -1){
				// put the task into the "Unassigned List" 
				UnassignedTasks.add(id);
				continue;
			}
			Schedule.get(ideal_day).add(id);					// add taskId to schedule
			ProcessingT[ideal_day] += task_details.get(7);  	// calculate the processing time
			Rewards[ideal_day] += task_details.get(ideal_day);	// calculate rewards
		}

		// Calculate traveling time
		for(int j = 0; j < Weekdays; j++){
//			TaskSequence TaskSequence = new TaskSequence(Schedule.get(j), Distance, TaskNum);
//			TaskSequence.Sequence();
//			TravelingT[j] = TaskSequence.getMinTravelingT();
//			TotalT[j] = ProcessingT[j] + TravelingT[j];
			Greedy Greedy = new Greedy(Schedule.get(j), Distance, TaskNum);
			TravelingT[j] = Greedy.doGreedy();
			TotalT[j] = ProcessingT[j] + TravelingT[j];
		}

		System.out.println("FirstStageAssignment:\n" + "Schedule: " + Schedule);
		System.out.println("Processing Time: " + Arrays.toString(ProcessingT));
		System.out.println(" Traveling Time: " + Arrays.toString(TravelingT));
		System.out.println("     Total Time: " + Arrays.toString(TotalT));
		System.out.println("---------------------------------------------------------------------------------");
	}

	// FindMinCost: find out the minimum opportunity cost of a day
	private List<Map<Integer, Float>> FindMinCost(){
		List<List<Integer>> oriSchedule = Schedule;
		List<List<Integer>> newSchedule = new ArrayList<>();
		List<Map<Integer, Float>> minOPcost = new ArrayList<>();	// the minimum opportunity cost of each day

		for(int j = 0; j < Weekdays; j++){
			List<Integer> daySchedule = oriSchedule.get(j);
			float day_min_op = 5000;
			for(int i = 0; i < daySchedule.size(); i++){
				int taskid = daySchedule.get(i);
				List<Float> task_details = OtherData.get(taskid - 1);
				float current_rewards = task_details.get(j);
				float task_min_op = 5000;
				for(int k = 0; k < Weekdays; k++){
					if(k == j)
						continue;
					float op = current_rewards - task_details.get(k);
					// Find minimum opportunity cost (task_min_op) of a task
					if(op < task_min_op){
						task_min_op = op;
					}
				}
				// Find minimum opportunity cost (day_min_op) of a day
				if(task_min_op < day_min_op){
					day_min_op = task_min_op;
				}
			}
			Map<Integer, Float> daywithop = new HashMap<Integer, Float>();
			daywithop.put(j, day_min_op);
			minOPcost.add(daywithop);
			daySchedule.add(0, j);	// tag to identify the weekday
			newSchedule.add(daySchedule);
		}

		System.out.println("FirstStageDaySort:\n" + "minOPcost: " + minOPcost);
		Schedule = newSchedule;
		return minOPcost;
	}

	// FirstStageDaySort: decide priority between weekdays by their minimum opportunity cost
	private void FirstStageDaySort(List<Map<Integer, Float>> minOPcost){
		List<Map<Integer, Float>> tempDayPriority = new ArrayList<>();
		// Sort weekdays
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
		// Create new schedule with priority
		List<List<Integer>> newSchedule = new ArrayList<>();
		for(int i = 0; i < Weekdays; i++){
			int dayIndex = (int) tempDayPriority.get(i).keySet().toArray()[0];
			newSchedule.add(Schedule.get(dayIndex));
		}
		System.out.println("Sorted Schedule: " + newSchedule);
		System.out.println("---------------------------------------------------------------------------------");
		Schedule = newSchedule;
	}

	// RecoverScheduleOrder: Arrange the schedule in the order of weekdays, and remove the day tag
	private void RecoverScheduleOrder(){
		List<List<Integer>> oriSchedule = Schedule;
		List<List<Integer>> newSchedule = new ArrayList<>();
		int [] order = {0, 1, 2, 3, 4, 5, 6};
		for(int i = 0; i <  Weekdays; i++){
			int dayIndex = (int) oriSchedule.get(i).get(0);
			order[dayIndex] = i;
		}
		for(int j = 0; j < Weekdays; j++){
			int index = order[j];
			newSchedule.add(oriSchedule.get(index));
			newSchedule.get(j).remove(0);	// remove tag
		}

		Schedule = newSchedule;
	}

	// First Stage Check: check whether the workload is exceeded after first stage assignment and make movements
	public void FirstStageCheck(){
		FirstStageAssignment();
		List<Map<Integer, Float>> minOPcost = FindMinCost();
		FirstStageDaySort(minOPcost);				// sort weekdays
		int [] newOrder = {0, 1, 2, 3, 4, 5, 6};	// ex, Tasks for day i have been moved to the "newOrder[i]" element of the Schedule.
		for(int j = 0; j < Weekdays; j++){
			int dayIndex = (int) Schedule.get(j).get(0);
			newOrder[dayIndex] = j;
		}

		for(int j = 0; j < Weekdays; j++){
			List<Integer> current_tasks = Schedule.get(j);  	  
			int current_day = current_tasks.get(0);
			float workload = Workload.get(current_day) * Gamma;	// available workload

			while(TotalT[current_day] > workload){		
				float min_loss = 5000;
				int remove_task = -1;
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
						int some_other_day = (int) Schedule.get(t).get(0);
						if(t == j)
							continue;
						// case1: if t is before day j, check if the capacity is enough
						else if(t < j){							
							// calculate new traveling time if the task is added
							List<Integer> newTaskList = new ArrayList<>();
							newTaskList = Schedule.get(some_other_day);	// the day a task will be moved to
							newTaskList.add(taskid);
//							TaskSequence TaskSequence = new TaskSequence(newTaskList.subList(1, newTaskList.size()), Distance, TaskNum);
//							TaskSequence.Sequence();
//							float newTravelingT = TaskSequence.getMinTravelingT();
							Greedy Greedy = new Greedy(newTaskList.subList(1, newTaskList.size()), Distance, TaskNum);
							float newTravelingT = Greedy.doGreedy();
							float newTotalT = TotalT[some_other_day] + task_details.get(7) - TravelingT[some_other_day] + newTravelingT;

							float workload_t = Workload.get(some_other_day) * Gamma;
							if(newTotalT < (workload_t - TotalT[some_other_day])){
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
					System.out.print(Arrays.toString(TotalT) + "\n");
				}
				else{
					// move the task to another day and calculate new processing time
					taskid_move = current_tasks.get(remove_task);  // taskid
					Schedule.get(newOrder[move_to_day]).add(taskid_move);
					
					// calculate new traveling time and update total time
					List<Integer> subTaskSequence = Schedule.get(newOrder[move_to_day]).subList(1, Schedule.get(newOrder[move_to_day]).size());
//					TaskSequence TaskSequence = new TaskSequence(subTaskSequence, Distance, TaskNum);
//					TaskSequence.Sequence();
//					TravelingT[move_to_day] = TaskSequence.getMinTravelingT();	// update TravelingT;
					Greedy Greedy = new Greedy(subTaskSequence, Distance, TaskNum);
					TravelingT[move_to_day] = Greedy.doGreedy();
					ProcessingT[move_to_day] += time_change;
					TotalT[move_to_day] = TravelingT[move_to_day] + ProcessingT[move_to_day];
					Rewards[move_to_day] += (float) OtherData.get(taskid_move - 1).get(move_to_day);
					
					System.out.print("Task " + taskid_move + " from day " + (current_day + 1) + " to day " + (move_to_day + 1) + "\n");
					System.out.print(Arrays.toString(TotalT) + "\n");
				}
				
				// remove task from current day
				current_tasks.remove(remove_task);
				
				// calculate new traveling time and update total time
				List<Integer> subTaskSequence = current_tasks.subList(1, current_tasks.size());
//				TaskSequence TaskSequence = new TaskSequence(subTaskSequence, Distance, TaskNum);
//				TaskSequence.Sequence();
//				TravelingT[current_day] = TaskSequence.getMinTravelingT();	// update TravelingT;
				Greedy Greedy = new Greedy(subTaskSequence, Distance, TaskNum);
				TravelingT[current_day] = Greedy.doGreedy();
				ProcessingT[current_day] -= time_change;
				TotalT[current_day] = TravelingT[current_day] + ProcessingT[current_day];
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
		
		System.out.println("\n" + "FirstStageCheck:\n" + Schedule);
		System.out.println("UnassignedTasks: " + UnassignedTasks);
		System.out.print("---------------------------------------------------------------------------------" + "\n");
	}

	public List<List<Integer>> getFirstStageSchedule(){
		return Schedule;
	}
	
	public List<Integer> getFirstStageUnassignedTasks(){
		return UnassignedTasks;
	}
	
	public float[] getFirstStageRewards(){
		return Rewards;
	}
	
	public float[] getFirstStageProcessingT(){
		return ProcessingT;
	}
	
	public float[] getFirstStageTravelingT(){
		return TravelingT;
	}
	
	public float[] getFirstStageTotalT(){
		return TotalT;
	}
	
	public List<TaskSplit> getFirstStageTaskPercentages(){
		return TaskPercentages;
	}
}