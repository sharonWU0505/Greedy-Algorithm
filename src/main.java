import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class main {
	private static float[][] Distance = null;
	private static float[] ComDistance = null;
	private static List<List> Detail = new ArrayList<>();
	
	private static void Excecute() {
		// Assign Tasks
		TaskAssign TaskAssign = new TaskAssign(Detail, Distance, ComDistance);
		TaskAssign.ExecuteTaskAssign();
		List<List<Integer>> Schedule = TaskAssign.getSchedule();

		System.out.print("[Task Sequence Results]" + "\n");
		float[] processingT = TaskAssign.getProcessingT();
		for(int i = 0; i < Schedule.size(); i++){
			// run TaskSequence to get the best traveling path
			TaskSequence TaskSequence = new TaskSequence(Schedule.get(i), Distance, ComDistance, Distance.length);
			TaskSequence.doSequence();
			float totalT = TaskSequence.getMinTravelingT() + processingT[i];
//			System.out.print("Day" + (i+1) + ": TotalT: " + totalT + "; Schedule: " + TaskSequence.getfinalDaySchedule() + "\n");
		}
	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");
		
		// read test data files
		String testdata = root + "/src/testdata.txt";
		try {
			FileReader fr = new FileReader(testdata);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				String testfile = line;
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
