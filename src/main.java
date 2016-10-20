import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class main {
	// read file of detail data
	private static List<List> ReadFile(String filepath){
		List<List> Detail = new ArrayList<>();
		List<Integer> Workload = new ArrayList<>();
		List<List> OtherData = new ArrayList<>();
		List<Float> Gamma = new ArrayList<>();
		
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("workload")) {
					String[] workloadData = line.split(",");
					Workload.add(Integer.parseInt(workloadData[1]));
				}
				else if(line.contains("gamma")) {
					String[] gammaData = line.split(",");
					Gamma.add(Float.parseFloat(gammaData[1]));
					
				}
				else if(line.contains("index")) {
					;
				}
				else {
					List<Integer> DetailData = new ArrayList<>();  // rewards, penalty, etc
					String[] detailData = line.split(",");
					for(int i = 0; i < detailData.length; i++){
						if(i > 2) {
							DetailData.add(Integer.parseInt(detailData[i]));
						}
					}
					OtherData.add(DetailData);
				}
	        }
			Detail.add(Workload);
			Detail.add(Gamma);
			Detail.add(OtherData);
			br.close();
		}
	    catch(IOException e) {
	    	System.out.println(e);
	    }
		
		return Detail;
	}

	// read data for distance file
	private static List<List> ReadDistanceFile(String filepath, int length) {
		List<List> Distance = new ArrayList<List>();

		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			line = br.readLine();
	        while((line = br.readLine()) != null) {
	        	List<Integer> SingleDistance = new ArrayList<Integer>();
				String[] distanceData = line.split(",");
				for(int i = 0; i < distanceData.length; i++){
					SingleDistance.add(Integer.parseInt(distanceData[i]));
				}
				Distance.add(SingleDistance);
	        }
			
			br.close();
		}
	    catch(IOException e) {
	    	System.out.println(e);
	    }
		
		return Distance;
	}


	private static List<List> TaskAssign(List<List> Detail){
		List<Integer> Workload = Detail.get(0);
		List<Float> Gamma = Detail.get(1);
		List<List> OtherData = Detail.get(2);
		
		List<List> Schedule = new ArrayList<>();
		int [] total_processingT = {0, 0, 0, 0, 0, 0, 0};
		
		//..........這應該有比較好的做法吧.............
		List<Integer> Monday = new ArrayList<>();
		List<Integer> Tuesday = new ArrayList<>();
		List<Integer> Wednesday = new ArrayList<>();
		List<Integer> Thursday = new ArrayList<>();
		List<Integer> Friday = new ArrayList<>();
		List<Integer> Saturday = new ArrayList<>();
		List<Integer> Sunday = new ArrayList<>();
		//........................................
		
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
		
		for(int j = 0; j < 7; j++){
			List<Integer> recent_tasks = Schedule.get(j);
			
			while(total_processingT[j] > (Workload.get(j)*Gamma.get(0))){		// 某天的分配超出負荷
				int remove_task = -1, move_to_day = -1;
				int min_loss = 1000;
				int time_change = 0;
				for(int k = 0; k < recent_tasks.size(); k++){		// 原排於j天的所有任務k
					int index = recent_tasks.get(k);
					
					List<Integer> task_details = OtherData.get(index-1);
					int recent_rewards = task_details.get(j);
					int inner_min_loss = 1000;
					int inner_move_to_day = -1;
					for(int t = 0; t < 7; t++){
						if(t == j)
							continue;
						if(task_details.get(7) < ((Workload.get(t)*Gamma.get(0)) - total_processingT[t])){
							int diff = recent_rewards - task_details.get(t);
							if(diff < inner_min_loss){
								inner_min_loss = diff;
								inner_move_to_day = t;
							}
						}
					}
					
					if(inner_min_loss < min_loss){
						min_loss = inner_min_loss;
						remove_task = k;		// 	原該日排入的第k個任務
						move_to_day = inner_move_to_day;
						time_change = task_details.get(7);
					}
				}
				
				if(remove_task == -1){
					// 找不到可以移去其他日子的任務
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
		
		System.out.print("\n" + "total_processinT: ");
		for(int j = 0; j < 7; j++){
			System.out.print(total_processingT[j] + " ");
		}
//		System.out.print("\n" + "total rewards: ");
//		for(int j = 0; j < 7; j++){
//			System.out.print( + " ");
//		}
		System.out.print("\n");
		
		return Schedule;
	}


	public static void main(String[] args) {
		// read detail file
		String detailpath = "C:\\Users\\admin\\workspace\\greedy\\src\\161018_real_data.txt";
//		String detailpath = "/Users/linda/Desktop/專題/greedy/src/161018_real_data.txt";
		System.out.print(ReadFile(detailpath));
		
		// task assignment
		List<List> Schedule = TaskAssign(ReadFile(detailpath));
		System.out.println(Schedule);
		
		for(int i = 0; i < Schedule.size(); i++){
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i));
			TaskSequence.setUsed();

			String distancepath = "C:\\Users\\admin\\workspace\\greedy\\src\\161018_real_data_distance.txt";
			int length = 19;
			TaskSequence.Sequence(TaskSequence.ReadDistanceFile(distancepath, length));
		}
	}
} 
