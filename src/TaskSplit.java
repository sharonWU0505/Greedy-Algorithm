import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private int max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Double> split_percentae;
	private double complete_percentage;
	
	public TaskSplit(int id, int rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentae = new ArrayList<>();
		complete_percentage = 0;
	}
	
	public TaskSplit(int taskid2, double max_rewards2, int ideal_day2) {
		// TODO Auto-generated constructor stub
	}

	public int getTaskId(){
		return taskId;
	}
	
	public int getMaxRewards(){
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
