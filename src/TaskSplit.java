import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private double max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Double> split_percentae;
	private double left_unfinished_percentage;
	private double value; // used to calculate and sort the tasks for each stage
	
	public TaskSplit(int id){
		taskId = id;
		split_to_days = new ArrayList<>();
		split_percentae = new ArrayList<>();
		left_unfinished_percentage = 1;
		max_rewards = 0;
		ideal_day = 0;
		value = 0;
	}
	
	public TaskSplit(int id, double rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentae = new ArrayList<>();
		left_unfinished_percentage = 1;
		value = 0;
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
	
	public double getValue(){
		return value;
	}
	public void setValue(double v){
		value = v;
	}
	
	public double getUnfinishedPercentage(){
		return left_unfinished_percentage;
	}
	
	public void splitInto(int day, double percentage){
		split_to_days.add(day);
		split_percentae.add(percentage);
		left_unfinished_percentage -= percentage;
	}
	
	public double getPercentage(int day){
		double percentage = 0;
		for(int i = 0; i< split_to_days.size(); i++){
			if(split_to_days.get(i) == day){
				percentage = split_percentae.get(i);
			}
		}
		return percentage;
	}
}
