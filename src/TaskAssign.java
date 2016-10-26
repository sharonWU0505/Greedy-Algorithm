import java.util.ArrayList;
import java.util.List;

public class TaskAssign{
	private List<Integer> Workload;
	private List<Float> Gamma;
	private List<List> OtherData;
	private List<List> Schedule;
	private int [] total_processingT = {0, 0, 0, 0, 0, 0, 0, 0};
	
	public TaskAssign(){
		Workload = new ArrayList<>();
		Gamma = new ArrayList<>();
		OtherData = new ArrayList<>();
		Schedule = new ArrayList<>();
	}
		
	public void assign(List<List> Detail){
		Workload = Detail.get(0);
		Gamma = Detail.get(1);
		OtherData = Detail.get(2);
		List<Integer> Monday = new ArrayList<>();
		List<Integer> Tuesday = new ArrayList<>();
		List<Integer> Wednesday = new ArrayList<>();
		List<Integer> Thursday = new ArrayList<>();
		List<Integer> Friday = new ArrayList<>();
		List<Integer> Saturday = new ArrayList<>();
		List<Integer> Sunday = new ArrayList<>();
		List<Integer> Unassigned = new ArrayList<>();
				
		// 無腦排入
		for(int i = 1; i < OtherData.size()+1; i++){
			List<Integer> task_details = OtherData.get(i-1);
			//System.out.println(task_details);
			int max_rewards = 0;
			int assign_to_day = -1;
			for(int j = 0; j < 7; j++){
				if(task_details.get(j) > max_rewards){
					max_rewards = task_details.get(j);
					assign_to_day = j;
				}
			}
			switch(assign_to_day){
				case 0:
					Monday.add(i);
					break;
				case 1:
					Tuesday.add(i);
					break;
				case 2:
					Wednesday.add(i);
					break;
				case 3:
					Thursday.add(i);
					break;
				case 4:
					Friday.add(i);
					break;
				case 5:
					Saturday.add(i);
					break;
				case 6:
					Sunday.add(i);
					break;
			}
			total_processingT[assign_to_day] += task_details.get(7);
		}
		
		Schedule.add(Monday);
		Schedule.add(Tuesday);
		Schedule.add(Wednesday);
		Schedule.add(Thursday);
		Schedule.add(Friday);
		Schedule.add(Saturday);
		Schedule.add(Sunday);
		Schedule.add(Unassigned);	// day 8 for unassigned tasks!
				
		for(int j = 0; j < 7; j++){
			List<Integer> recent_tasks = Schedule.get(j);
			// check if day j is overloading
			while(total_processingT[j] > (Workload.get(j)*Gamma.get(0))){		
				int remove_task = -1, move_to_day = -1;
				int min_loss = 1000;
				int time_change = 0;
				
				// find the task which has the least "opportunity cost" and assign it to the other day
				// if it is before day j, check if the capacity is enough
				for(int k = 0; k < recent_tasks.size(); k++){		
					int index = recent_tasks.get(k);
							
					List<Integer> task_details = OtherData.get(index-1);
					int recent_rewards = task_details.get(j);
					int inner_min_loss = 1000;
					int inner_move_to_day = -1;
					for(int t = 0; t < 7+1; t++){
						if(t == j)
							continue;
						else if(t < j){
							if(task_details.get(7) < ((Workload.get(t)*Gamma.get(0)) - total_processingT[t])){
								int diff = recent_rewards - task_details.get(t);
								if(diff < inner_min_loss){
									inner_min_loss = diff;
									inner_move_to_day = t;
								}
							}
						}
						else{
							int diff = recent_rewards - task_details.get(t);
							if(diff < inner_min_loss){
								inner_min_loss = diff;
								inner_move_to_day = t;
							}
						}
					}
							
					if(inner_min_loss < min_loss){
						min_loss = inner_min_loss;
						remove_task = k;		// 	k is the list index, not taskId
						move_to_day = inner_move_to_day;
						time_change = task_details.get(7);
					}
				}

				int index = recent_tasks.get(remove_task);
				List<Integer> temp = Schedule.get(move_to_day);
				temp.add(index);
				Schedule.set(move_to_day, temp);
				recent_tasks.remove(remove_task);
						
				total_processingT[j] -= time_change;
				total_processingT[move_to_day] += time_change;
						
				System.out.println(index + " from " + j + " to " + move_to_day);
			}
		}
		
		// re-assign the unassigned tasks
		// sequence the tasks in the order of their absolute maximum rewards
		//--------------------------------------------------------------------
		List<Integer> unassignedTasks = getUnassignedTasks();		// will later be updated and replaced 
		List<Integer> new_unassignedTasks = new ArrayList<>();
		List<TaskSplit> unassiTaskSequence = new ArrayList<>();
		List<TaskSplit> splitTasks = new ArrayList<>();
		
		for(int i = 0; i < unassignedTasks.size(); i++){
			int taskId, max_rewards, ideal_day;
			taskId = unassignedTasks.get(i);
			List<Integer> task_details = OtherData.get(taskId-1);
			int max = 0, day = -1;
			for(int j = 0; j < 7; j++){
				if(task_details.get(j) > max){
					max = task_details.get(j);
					day = j;
				}
			}
			max_rewards = max;
			ideal_day = day;
			TaskSplit aTask = new TaskSplit(taskId, max_rewards, ideal_day);
//			System.out.println(taskId + ": max_rewards " + max_rewards + " on day " + ideal_day);
			// put in order
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
				}
			}
		}
		// split the unassigned tasks into the day where the max_rewards happens 
		// if the day is out of capacity, abandon the taskQQ
		for(int i = 0; i < unassiTaskSequence.size(); i++){
			TaskSplit aTask = unassiTaskSequence.get(i);
			int taskId = aTask.getTaskId();
			int ideal = aTask.getIdealDay();
			
			if((Workload.get(ideal)*Gamma.get(0)) > total_processingT[ideal]){
				// capacity left
				List<Integer> task_details = OtherData.get(taskId-1);
				double processingT = task_details.get(7);
				double task_left = 1;
				int split_ctr = 0;
				while(task_left > 0){
					split_ctr++;
					double capacity_left = Workload.get(ideal)*Gamma.get(0) - total_processingT[ideal];
					double percentage;
					if((processingT*task_left) > ((Workload.get(ideal)*Gamma.get(0)) - total_processingT[ideal])){
						percentage = capacity_left / processingT;
						total_processingT[ideal] += capacity_left;
					}
					else{
						percentage = task_left;
						total_processingT[ideal] += processingT*task_left;
					}
					aTask.splitInto(ideal, percentage);
					// add to schedule
					List<Integer> temp = Schedule.get(ideal);
					temp.add(taskId);
					Schedule.set(ideal, temp);
					task_left -= percentage;
					System.out.println("split " + taskId + " into day " + ideal + " with percentage = " + percentage + ", left " + task_left);
					if(task_left > 0){
						if(split_ctr == task_details.get(9)){
							break;
						}
						int pre_max_rewards = task_details.get(ideal);
						int new_max = -1000;
						int temp_choice = -1;
						for(int j = 0; j < 7; j++){
							if(total_processingT[j] == (Workload.get(ideal)*Gamma.get(0))){
								continue;
							}
							if((task_details.get(j) > new_max) && (task_details.get(j) <= pre_max_rewards) && (j != ideal)){
								new_max = task_details.get(j);
								temp_choice = j;
							}
						}
						if(temp_choice != -1){
							ideal = temp_choice;
						}
						else{
							break;	// no way to complete the task
						}
					}
				}
				splitTasks.add(aTask);
			}
			else{
				new_unassignedTasks.add(taskId);
				continue;
			}
		}
		
		Schedule.set(7, new_unassignedTasks);
		
		//---------------------------------------------------------------------	
				
		System.out.print("total_processingT: ");
		for(int j = 0; j < 7; j++){
			System.out.print(total_processingT[j] + " ");
		}
//		System.out.print("\n" + "total rewards: ");
//		for(int j = 0; j < 7; j++){
//			int total_rewards=0;
//			int index;
//			List<Integer> recent_tasks = Schedule.get(j);
//			for(int k = 0; k < recent_tasks.size(); k++){
//				index = recent_tasks.get(k);
//				List<Integer> task_detail = OtherData.get(index-1);
//				total_rewards += task_detail.get(j);
//			}
//			System.out.print(total_rewards + " ");
//		}
		System.out.print("\n\n");

	}
	
	public List<List> getSchedule(){
		return Schedule;
	}
	
	public List<Integer> getUnassignedTasks(){
		return Schedule.get(7);
	}
	
}
		