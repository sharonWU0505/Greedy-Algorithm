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

	// Read Detail Data File
	private static List<List> ReadDetailFile(String filepath){
		List<List> Detail = new ArrayList<>();
		List<List> OtherData = new ArrayList<>();
		List<Double> Workload = new ArrayList<>();
		List<Double> Gamma = new ArrayList<>();
		List<Integer> Workdays = new ArrayList<>();
			
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("workload")) {
					String[] workloadData = line.split(",");
					Workload.add(Double.parseDouble(workloadData[1]));
				}
				else if(line.contains("gamma")) {
					String[] gammaData = line.split(",");
					Gamma.add(Double.parseDouble(gammaData[1]));
						
				}
				else if(line.contains("taskId")) {
					;
				}
				else {
					List<Double> DetailData = new ArrayList<>();  // rewards, penalty, splitN, processingT
					String[] detailData = line.split(",");
					for(int i = 0; i < detailData.length; i++){
						if(i > 2) {
							DetailData.add(Double.parseDouble(detailData[i]));
						}
					}
					OtherData.add(DetailData);
				}
		    }
			
			// workdays
			String[] workdaysData = {"7"};
			Workdays.add(Integer.parseInt(workdaysData[0]));

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

	// Read Distance File
	private static void ReadDistanceFile(String filepath) {
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			line = br.readLine();							// get the first line data
			String[] firstDistanceData = line.split(",");
			int locnum = firstDistanceData.length;  		// get number of locations
			Distance = new int[locnum][locnum];     		// define Distance array
			for(int i = 0; i < locnum; i++){
				Distance[0][i] = Integer.parseInt(firstDistanceData[i]);	// put the first line into array
			}

			int locindex = 1;
			while((line = br.readLine()) != null) {
				String[] distanceData = line.split(",");
				for(int i = 0; i < locnum; i++){
					Distance[locindex][i] = Integer.parseInt(distanceData[i]);
				}
//				System.out.println(Arrays.toString(Distance[locindex]));
				locindex += 1;
			}
			
			br.close();
		}

		catch(IOException e) {
		    System.out.println(e);
		}
	}

	// Get Distance Data
	private static int[][] GetDistance(){
		return Distance;
	}


	public static void main(String[] args) {
		String root = System.getProperty("user.dir");
		// Read Test File
//		Scanner reader = new Scanner(System.in);
//		String testfile = reader.next();			// /src/general_input.txt
//		String testpath = root + testfile;
		
		// Read Detail File
		Scanner reader1 = new Scanner(System.in);
		String detailfile = reader1.next();			// /src/data_1025.txt
		String detailpath = root + detailfile;
		
		// Task Assignment
		TaskAssign TaskAssign = new TaskAssign();
//		TaskAssign.TaskAssign(ReadTestFile(testpath));
		TaskAssign.TaskAssign(ReadDetailFile(detailpath));
		TaskAssign.ExecuteTaskAssign();
		List<List> Schedule = TaskAssign.getSchedule();
		System.out.print("Schedule:\n");
		for(int i = 0; i < Schedule.size(); i++){
			System.out.print("Day " + (i + 1) + ": " + Schedule.get(i) + "\n");
		}
		System.out.print("Unassigned tasks:\n" + TaskAssign.getUnassignedTasks());
		System.out.print("\n" + "-----------------------------------------------------------" + "\n");
		
		// Read Distance File
//		Scanner reader2 = new Scanner(System.in);
//		String distancefile = reader2.next();			// /src/diatance.txt
//		String distancepath = root + distancefile;
//
//		// Decide Task Sequence
//		System.out.print("[Task Sequence Results]" + "\n");
//		ReadDistanceFile(distancepath);
//		for(int i = 0; i < Schedule.size(); i++){
//			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i));
//			System.out.print("Day " + (i + 1) + ": \n");
//			TaskSequence.Sequence(GetDistance());
//		}
	}
} 
