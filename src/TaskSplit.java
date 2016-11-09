import java.util.ArrayList;
import java.util.List;


public class TaskSplit{
	private int taskId;
	private float max_rewards;
	private int ideal_day;
	private List<Integer> split_to_days;
	private List<Float> split_percentage;
	private float complete_percentage;
	
	public TaskSplit(int id){
		taskId = id;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
	}
	
	public TaskSplit(int id, float rewards, int day){
		taskId = id;
		max_rewards = rewards;
		ideal_day = day;
		split_to_days = new ArrayList<>();
		split_percentage = new ArrayList<>();
		complete_percentage = 0;
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
	
	public float getCompletePercentage(){
		return complete_percentage;
	}
	
	public void splitInto(int day, float percentage){
		split_to_days.add(day);
		split_percentage.add(percentage);
		complete_percentage += percentage;
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
