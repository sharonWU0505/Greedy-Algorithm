import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class main {
	private static float[][] Distance = null;
	private static List<List> Detail = new ArrayList<>();
	private static List<List<Integer>> FinalSchedule = new ArrayList<>();
	private static float[][] FinalTaskPercentage = null;
	
	private static void Excecute() {
		// Assign Tasks
		TaskAssign TaskAssign = new TaskAssign(Detail, Distance);
		TaskAssign.ExecuteTaskAssign();
		List<List<Integer>> Schedule = TaskAssign.getSchedule();
		// get and print Schedule
//		System.out.print("Schedule:\n");
//		for(int i = 0; i < Schedule.size(); i++){
//			System.out.print("Day " + (i + 1) + ": " + Schedule.get(i) + "\n");
//		}
		// get and print UassignedTasks
		System.out.print("Unassigned tasks:\n" + TaskAssign.getUnassignedTasks() + "\n");
		// get and print TaskPercentages
		FinalTaskPercentage = TaskAssign.getTaskPercentages();
//		System.out.print("TaskPercentages:\n");
//		TaskAssign.printTaskPercentages();
		System.out.print("-----------------------------------------------------------" + "\n");

		// Sequencing Tasks
		System.out.print("[Task Sequence Results]" + "\n");
		for(int i = 0; i < Schedule.size(); i++){
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i), Distance, Distance.length);
			FinalSchedule.add(TaskSequence.Sequence());
			System.out.print("Day " + (i + 1) + ": \n");
			List<Float> tasktime = TaskAssign.getProcessingTimeofTasks();
			TaskSequence.printDaySchedule(tasktime.get(i));
		}
	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// GeneralInput
		Scanner reader = new Scanner(System.in);
		String testfile = reader.next();			// /src/general_input.txt
		String testpath = root + testfile;
		GeneralInput GeneralInput = new GeneralInput();
		GeneralInput.ReadTestFile(testpath);
		Detail = GeneralInput.GetDetail();
		Distance = GeneralInput.GetDistance();
		System.out.print("Finish Input. \n");

		// SpecialInput
//		SpecialInput SepcialInput = new SpecialInput();
//		String detailfile = "/src/data_1103.txt";
//		String detailpath = root + detailfile;
//		Detail = SepcialInput.GetDetail(detailpath);		// read detail file
//
//		String distancefile = "/src/distance_1103.txt";
//		String distancepath = root + distancefile;
//		Distance = SepcialInput.GetDistance(distancepath);	// read distance file

		// Execute and Output
		Excecute();
	}
} 
