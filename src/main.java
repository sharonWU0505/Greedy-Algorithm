import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class main {
	private static float[][] Distance = null;
	private static float[] ComDistance = null;
	private static List<List> Detail = new ArrayList<>();
	
	private static void Excecute() {
		// Assign Tasks
		TaskAssign TaskAssign = new TaskAssign(Detail, Distance, ComDistance);
		TaskAssign.ExecuteTaskAssign();
		List<List<Integer>> Schedule = TaskAssign.getSchedule();

//		System.out.println("---------------------------------------------------------------------------------");
//		TaskAssign.printTaskPercentages();
		
		// Sequencing Tasks
//		System.out.print("[Task Sequence Results]" + "\n");
		for(int i = 0; i < Schedule.size(); i++){
//			Greedy Greedy = new Greedy(Schedule.get(i), Distance, ComDistance, Distance.length);
//			float[] processingT = TaskAssign.getProcessingT();
//			float totalT = Greedy.doGreedy() + processingT[i];
//			System.out.print("Day" + (i+1) + ": TotalT: " + totalT + "; Schedule: " + Greedy.getFinalDaySchedule() + "\n");
			
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i), Distance, ComDistance, Distance.length);
			float[] processingT = TaskAssign.getProcessingT();
			TaskSequence.doSequence();
			float totalT = TaskSequence.getMinTravelingT() + processingT[i];
//			System.out.print("Day" + (i+1) + ": TotalT: " + totalT + "; Schedule: " + TaskSequence.getfinalDaySchedule() + "\n");

			// output for shouko
//			List<Integer> result = Greedy.getFinalDaySchedule();
//			for(int x = 0; x < result.size(); x++){
//				System.out.print(result.get(x) + " ");
//			}
//			System.out.println();
		}
		

	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// GeneralInput
//		Scanner reader = new Scanner(System.in);
//		String testfile = reader.next();	// /src/general_input.txt
//		String testpath = root + testfile;
//		GeneralInput GeneralInput = new GeneralInput();
//		GeneralInput.ReadTestFile(testpath);
//		Detail = GeneralInput.GetDetail();
//		Distance = GeneralInput.GetDistance();
//		ComDistance = GeneralInput.GetComDistance();
////		System.out.print("Finish Input. \n");
//
//		// Execute and Output
//		Excecute();
		
		
		
		// testdata
		String testdata = root + "/src/testdata.txt";
		try {
			FileReader fr = new FileReader(testdata);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				String testfile = line;
//				System.out.print("[" + testfile + "] ");
				String testpath = root + "/src/testdata_sample/" + testfile;
				
				GeneralInput GeneralInput = new GeneralInput();
				GeneralInput.ReadTestFile(testpath);
				Detail = GeneralInput.GetDetail();
				Distance = GeneralInput.GetDistance();
				ComDistance = GeneralInput.GetComDistance();

				// Execute and Output
				Excecute();
			}
		}
		catch(IOException e) {
			System.out.println(e);
		}
		
	}
} 
