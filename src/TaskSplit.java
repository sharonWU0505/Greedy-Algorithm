import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private float max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Float> split_percentage;
	private float left_unfinished_percentage;
	private int counter;
	
	public TaskSplit(int id){
		taskId = id;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
		left_unfinished_percentage = 1;
		max_rewards = 0;
		ideal_day = 0;
		counter = 0;
	}
	
	public TaskSplit(int id, float rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
		left_unfinished_percentage = 1;
		counter = 0;
	}

	public int getTaskId(){
		return taskId;
	}
	
	public void setMaxRewards(float rewards){
		max_rewards = rewards;
	}
	
	public float getMaxRewards(){
		return max_rewards;
	}
	
	public void setIdealDay(int day){
		ideal_day = day;
	}
	
	public int getIdealDay(){
		return ideal_day;
	}
	
	public float getUnfinishedPercentage(){
		return left_unfinished_percentage;
	}
	
	public void splitInto(int day, float percentage){
		split_to_days.add(day);
		split_percentage.add(percentage);
		left_unfinished_percentage -= percentage;
		counter++;
	}
	
	public float getPercentage(int day){
		float percentage = 0;
		if(split_to_days.contains(day)){
			percentage = split_percentage.get(split_to_days.indexOf(day));
		}
		return percentage;
	}
	
	public int getCounter(){
		return counter;
	}
}
