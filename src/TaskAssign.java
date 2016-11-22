import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskAssign{
	// Attribute
	private List<Float> Workload;
	private List<List> OtherData;
	private float Gamma = 0;
	private int Weekdays = 0;
	private int TaskNum = 0;
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

	// Print Results
	private void PrintResult(){
		System.out.print("---------------------------------------------------------------------------------" + "\n");
		System.out.print("[Task Assignement Results]" + "\n");
		System.out.println("Schedule: " + Schedule);
		System.out.println("Processing Time: " + Arrays.toString(ProcessingT));
		System.out.println(" Traveling Time: " + Arrays.toString(TravelingT));
		System.out.println("     Total Time: " + Arrays.toString(TotalT));
		System.out.println("     	Rewards: " + Arrays.toString(Rewards));
		System.out.println("Unassigned tasks: " + UnassignedTasks);
		System.out.print("---------------------------------------------------------------------------------" + "\n");
	}
	// End Print Results

	public void ExecuteTaskAssign(){
		// First Stage
		FirstStage FirstStage = new FirstStage(Workload, OtherData, Gamma, Weekdays, Schedule, Distance, TaskPercentages);
		FirstStage.FirstStageCheck();
		Schedule = FirstStage.getFirstStageSchedule();
		UnassignedTasks = FirstStage.getFirstStageUnassignedTasks();
		TaskPercentages = FirstStage.getFirstStageTaskPercentages();
		Rewards = FirstStage.getFirstStageRewards();
		ProcessingT = FirstStage.getFirstStageProcessingT();
		TravelingT = FirstStage.getFirstStageTravelingT();
		TotalT = FirstStage.getFirstStageTotalT();
		
		//	Second Stage
		SecondStage SecondStage = new SecondStage(Workload, OtherData, Gamma, Weekdays, Schedule, UnassignedTasks, Rewards, ProcessingT, TravelingT, TotalT, Distance, TaskPercentages);
		SecondStage.SecondStageAssignment();
		Schedule = SecondStage.getSchedule();
		UnassignedTasks = SecondStage.getUnassignedTasks();
		TaskPercentages = SecondStage.getTaskPercentages();
		Rewards = SecondStage.getRewards();
		ProcessingT = SecondStage.getProcessingT();
		TravelingT = SecondStage.getTravelingT();
		TotalT = SecondStage.getTotalT();
		
		// Print Results
		PrintResult();
	}

	public List<List<Integer>> getSchedule(){
		return Schedule;
	}

	public List<Integer> getUnassignedTasks(){
		return UnassignedTasks;
	}
	
	public float[] getProcessingT(){
		return ProcessingT;
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
		