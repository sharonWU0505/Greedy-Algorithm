import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private double max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Double> split_percentae;
	private double complete_percentage;
	
	public TaskSplit(int id, double rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentae = new ArrayList<>();
		complete_percentage = 0;
	}

	public int getTaskId(){
		return taskId;
	}
	
	public double getMaxRewards(){
		return max_rewards;
	}
	
	public int getIdealDay(){
		return ideal_day;
	}
	
	public double getCompletePercentage(){
		return complete_percentage;
	}
	
	public void splitInto(int day, double percentage){
		split_to_days.add(day);
		split_percentae.add(percentage);
		
		complete_percentage += percentage;
	}
}
