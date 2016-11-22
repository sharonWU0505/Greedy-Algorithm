import java.util.ArrayList;
import java.util.List;

public class ExeSpecialInput{
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
	}

	public static void main(String[] args) {
		String root = System.getProperty("user.dir");

		// SpecialInput
		SpecialInput SepcialInput = new SpecialInput();
		String detailfile = "/src/specialinput/data_1025.txt";
		String detailpath = root + detailfile;
		Detail = SepcialInput.GetDetail(detailpath);		// read detail file

		String distancefile = "/src/specialinput/distance_1025.txt";
		String distancepath = root + distancefile;
		Distance = SepcialInput.GetDistance(distancepath);	// read distance file

		// Execute and Output
		Excecute();
	}
}
