import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;


public class main {
	private static float[][] Distance = null;
	private static float[] ComDistance = null;
	private static List<List> Detail = new ArrayList<>();
	
	private static void Excecute() {
		// Assign Tasks
		TaskAssign TaskAssign = new TaskAssign(Detail, Distance, ComDistance);
		TaskAssign.ExecuteTaskAssign();
		List<List<Integer>> Schedule = TaskAssign.getSchedule();

		// Sequencing Tasks
		System.out.print("[Task Sequence Results]" + "\n");
		float[] processingT = TaskAssign.getProcessingT();
		for(int i = 0; i < Schedule.size(); i++){
			// run TaskSequence to get the best traveling path
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i), Distance, ComDistance, Distance.length);
			TaskSequence.doSequence();
			float totalT = TaskSequence.getMinTravelingT() + processingT[i];
			System.out.print("Day" + (i+1) + ": TotalT: " + totalT + "; Schedule: " + TaskSequence.getfinalDaySchedule() + "\n");
		}
	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// GeneralInput
//		Scanner reader = new Scanner(System.in);
//		String testfile = reader.next();	// /src/general_input.txt
		String testfile = "/src/general_input.txt";
		String testpath = root + testfile;
		GeneralInput GeneralInput = new GeneralInput();
		GeneralInput.ReadTestFile(testpath);
		Detail = GeneralInput.GetDetail();
		Distance = GeneralInput.GetDistance();
		ComDistance = GeneralInput.GetComDistance();
		System.out.print("Finish Input. \n");

		// Execute and Output
		Excecute();
	}
} 
