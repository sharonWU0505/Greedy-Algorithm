import java.util.ArrayList;
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
	
	// Constructor
	public FirstStage(List<Float> Workload, List<List> OtherData, float Gamma, int Weekdays, List<List<Integer>> Schedule, float [][] Distance){
		this.Workload = Workload;
		this.OtherData = OtherData;
		this.Gamma = Gamma;
		this.Weekdays = Weekdays;
		this.Schedule = Schedule;
		TaskNum = OtherData.size();
		Distance = new float[TaskNum][TaskNum];
		for(int i = 0; i < TaskNum; i++){
			for(int j = 0; j < TaskNum; j++){
				this.Distance[i][j] = Distance[i][j];
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
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(j), Distance, TaskNum);
			TaskSequence.Sequence();
			TravelingT[j] = TaskSequence.getMinTravelingT();
			TotalT[j] = ProcessingT[j] + TravelingT[j];
		}
		System.out.println("\n" + "FirstStageAssignment:\n" + "Schedule: " + Schedule);
		System.out.println("---------------------------------------------------------------------------------");
	}
	// End First Stage Assignment
}