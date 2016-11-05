import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class main {
	private static int[][] Distance = null;

	// Read Test File
	private static List<List> ReadTestFile(String filepath){
		List<List> Detail = new ArrayList<>();
		List<List> OtherData = new ArrayList<>();
		List<Double> Workload = new ArrayList<>();
		List<Double> Gamma = new ArrayList<>();
		List<Integer> Workdays = new ArrayList<>();
		int taskNum = 0;
		int workdays = 0;
		
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			int lines = 1;
			String line;
			while((line = br.readLine()) != null) {
				if(lines == 1){
					String[] number = line.split(" ");
					Workdays.add(Integer.parseInt(number[1]));
					workdays = Integer.parseInt(number[1]);
					taskNum = Integer.parseInt(number[0]);
				}
				else if(lines == 2){
					String[] processTData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						List<Double> DetailData = new ArrayList<>();  // for saving rewards, processingT, penalty, splitN
						DetailData.add(Double.parseDouble(processTData[i]));
						OtherData.add(DetailData);
					}
				}
				else if(lines == 3){
					String[] workloadData = line.split(" ");
					for(int i = 0; i < workdays; i++){
						Workload.add(Double.parseDouble(workloadData[i]));
					}
				}
				else if(lines >= 4 && lines < (4 + taskNum)){
					String[] rewardsData = line.split(" ");
					for(int i = 0; i < workdays; i++){
						Double rewards = Double.parseDouble(rewardsData[i]);
//						System.out.print(OtherData.get(lines - 4));
						OtherData.get(lines - 4).add(i, rewards);
					}
				}
				else if(lines == (4 + taskNum)){
					String[] splitNData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						OtherData.get(i).add(Double.parseDouble(splitNData[i]));
					}
				}
				else if(lines == (5 + taskNum)){
					String[] penaltyData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						OtherData.get(i).add((workdays + 1), Double.parseDouble(penaltyData[i]));
					}
				}

				lines += 1;
		    }

			// no input for gamma...
			String[] gammaData = {"0.8"};
			Gamma.add(Double.parseDouble(gammaData[0]));

			Detail.add(Workload);
			Detail.add(OtherData);
			Detail.add(Gamma);
			Detail.add(Workdays);
			br.close();
		}
		catch(IOException e) {
		    System.out.println(e);
		}
			
		return Detail;
	}

	


	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// Read Test File
//		Scanner reader = new Scanner(System.in);
//		String testfile = reader.next();			// /src/general_input.txt
//		String testpath = root + testfile;
		
		SpecialInput SepcialInput = new SpecialInput();
		// Read Detail File
		String detailfile = "/src/data_1103.txt";
		String detailpath = root + detailfile;

		// Task Assignment
		TaskAssign TaskAssign = new TaskAssign();
//		TaskAssign.TaskAssign(ReadTestFile(testpath));
		TaskAssign.TaskAssign(SepcialInput.GetDetail(detailpath));
		TaskAssign.ExecuteTaskAssign();
		List<List> Schedule = TaskAssign.getSchedule();
		System.out.print("Schedule:\n");
		for(int i = 0; i < Schedule.size(); i++){
			System.out.print("Day " + (i + 1) + ": " + Schedule.get(i) + "\n");
		}
		System.out.print("Unassigned tasks:\n" + TaskAssign.getUnassignedTasks() + "\n");
		System.out.print("TaskPercentages:\n");
		TaskAssign.printTaskPercentages();
		System.out.print("-----------------------------------------------------------" + "\n");
		
		// Read Distance File
		String distancefile = "/src/distance_1103.txt";
		String distancepath = root + distancefile;
		Distance = SepcialInput.GetDistance(distancepath);

		// Decide Task Sequence
		System.out.print("[Task Sequence Results]" + "\n");
		for(int i = 0; i < Schedule.size(); i++){
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i));
			System.out.print("Day " + (i + 1) + ": \n");
			TaskSequence.Sequence(Distance);
		}
	}
} 
