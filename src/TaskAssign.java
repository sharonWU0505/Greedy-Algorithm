import java.util.ArrayList;
import java.util.List;

public class TaskAssign{
	private List<Integer> Workload;
	private List<Float> Gamma;
	private List<List> OtherData;
	
	private List<List> Schedule;
	private int [] total_processingT = {0, 0, 0, 0, 0, 0, 0, 0};
	
	public TaskAssign(List<List> Detail){
		Workload = Detail.get(0);
		Gamma = Detail.get(1);
		OtherData = Detail.get(2);
		Schedule = new ArrayList<>();
		List<Integer> Unassigned = new ArrayList<>();
		
		//..........這應該有比較好的做法吧.............
		List<Integer> Monday = new ArrayList<>();
		List<Integer> Tuesday = new ArrayList<>();
		List<Integer> Wednesday = new ArrayList<>();
		List<Integer> Thursday = new ArrayList<>();
		List<Integer> Friday = new ArrayList<>();
		List<Integer> Saturday = new ArrayList<>();
		List<Integer> Sunday = new ArrayList<>();
		//........................................
				
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
						
				if(remove_task == -1){
					// no way to solve the overloading on day j 
					// should only happen on Sunday
					System.out.println("overload on day " + (j+1));
					break;
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
		//
		//
		//
		//	
				
		System.out.print("total_processingT: ");
		for(int j = 0; j < 7; j++){
			System.out.print(total_processingT[j] + " ");
		}
		System.out.print("\n" + "total rewards: ");
		for(int j = 0; j < 7; j++){
			int total_rewards=0;
			int index;
			List<Integer> recent_tasks = Schedule.get(j);
			for(int k = 0; k < recent_tasks.size(); k++){
				index = recent_tasks.get(k);
				List<Integer> task_detail = OtherData.get(index-1);
				total_rewards += task_detail.get(j);
			}
			System.out.print(total_rewards + " ");
		}
		System.out.print("\n\n");

	}
	
	public List<List> getSchedule(){
		return Schedule;
	}
	
	public List<Integer> getUnassignedTasks(){
		return Schedule.get(7);
	}
	
	
	
	
}
		