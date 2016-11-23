import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class main {
	private static float[][] Distance = null;
	private static List<List> Detail = new ArrayList<>();
	
	private static void Excecute() {
		// Assign Tasks
		TaskAssign TaskAssign = new TaskAssign(Detail, Distance);
		TaskAssign.ExecuteTaskAssign();
		List<List<Integer>> Schedule = TaskAssign.getSchedule();

		// Sequencing Tasks
		System.out.print("[Task Sequence Results]" + "\n");
		for(int i = 0; i < Schedule.size(); i++){
//			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i), Distance, Distance.length);
//			TaskSequence.Sequence();
//			System.out.print("Day " + (i + 1) + ": ");
//			float[] processingT = TaskAssign.getProcessingT();
//			TaskSequence.printDaySchedule(processingT[i]);
			Greedy Greedy = new Greedy(Schedule.get(i), Distance, Distance.length);
			float[] processingT = TaskAssign.getProcessingT();
			float totalT = Greedy.doGreedy() + processingT[i];
			System.out.print("Day" + i + ": TotalT: " + totalT + "; Schedule: " + Greedy.getFinalDaySchedule() + "\n");
		}
		
		System.out.println("---------------------------------------------------------------------------------");
		TaskAssign.printTaskPercentages();
	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// GeneralInput
		Scanner reader = new Scanner(System.in);
		String testfile = reader.next();	// /src/general_input.txt
		String testpath = root + testfile;
		GeneralInput GeneralInput = new GeneralInput();
		GeneralInput.ReadTestFile(testpath);
		Detail = GeneralInput.GetDetail();
		Distance = GeneralInput.GetDistance();
		System.out.print("Finish Input. \n");

		// Execute and Output
		Excecute();
	}
} 
