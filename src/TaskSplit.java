import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private float max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Float> split_percentage;
	private float left_unfinished_percentage;
	private float value; // used to calculate and sort the tasks for each stage
	
	public TaskSplit(int id){
		taskId = id;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
		left_unfinished_percentage = 1;
		max_rewards = 0;
		ideal_day = 0;
		value = 0;
	}
	
	public TaskSplit(int id, float rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
		left_unfinished_percentage = 1;
		value = 0;
	}

	public int getTaskId(){
		return taskId;
	}
	
	public float getMaxRewards(){
		return max_rewards;
	}
	
	public int getIdealDay(){
		return ideal_day;
	}
	
	public double getValue(){
		return value;
	}
	public void setValue(float v){
		value = v;
	}
	
	public double getUnfinishedPercentage(){
		return left_unfinished_percentage;
	}
	
	public void splitInto(int day, float percentage){
		split_to_days.add(day);
		split_percentage.add(percentage);
		left_unfinished_percentage -= percentage;
	}
	
	public float getPercentage(int day){
		float percentage = 0;
		for(int i = 0; i< split_to_days.size(); i++){
			if(split_to_days.get(i) == day){
				percentage = split_percentage.get(i);
			}
		}
		return percentage;
	}
}
