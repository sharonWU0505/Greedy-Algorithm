import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpecialInput {
	// Attribute
	private static List<List> Detail;
	private static float[][] Distance;

	// Constructor
	public SpecialInput(){
		Detail = new ArrayList<>();
		Distance = null;
	}
	
	// Methods
	// Read Detail Data File
	private void ReadDetailFile(String filepath){
		List<List<Float>> OtherData = new ArrayList<>();
		List<Float> Workload = new ArrayList<>();
		List<Float> Gamma = new ArrayList<>();
		List<Integer> Workdays = new ArrayList<>();
	
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("workload")) {
					String[] workloadData = line.split(",");
					Workload.add(Float.parseFloat(workloadData[1]));
				}
				else if(line.contains("gamma")) {
					String[] gammaData = line.split(",");
					Gamma.add(Float.parseFloat(gammaData[1]));	
				}
				else if(line.contains("taskId")) {
					;
				}
				else {
					List<Float> LineData = new ArrayList<>();  // rewards, penalty, splitN, processingT
					String[] lineData = line.split(",");
					for(int i = 0; i < lineData.length; i++){
						if(i > 2) {
							LineData.add(Float.parseFloat(lineData[i]));
						}
					}
					OtherData.add(LineData);
				}
			}

			// Workdays
			String[] workdaysData = {"7"};
			Workdays.add(Integer.parseInt(workdaysData[0]));

			// for final output
			Detail.add(Workload);
			Detail.add(OtherData);
			Detail.add(Gamma);
			Detail.add(Workdays);
			br.close();
		}
		catch(IOException e) {
			   System.out.println(e);
		}
	}

	// Read Distance File
	private void ReadDistanceFile(String filepath) {
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			line = br.readLine();							// get the first line data
			String[] firstDistanceData = line.split(",");	// arrange the first line data
			int locnum = firstDistanceData.length;  		// get number of locations
			Distance = new float[locnum][locnum];     		// define Distance array
			for(int i = 0; i < locnum; i++){
				Distance[0][i] = Float.parseFloat(firstDistanceData[i]);	// put the first line into array
			}

			int locindex = 1;
			while((line = br.readLine()) != null) {
				String[] distanceData = line.split(",");
				for(int i = 0; i < locnum; i++){
					Distance[locindex][i] = Float.parseFloat(distanceData[i]);
				}
				locindex += 1;
			}
			br.close();
		}

		catch(IOException e) {
			   System.out.println(e);
		}
	}

	// Get Detail Data
	public List<List> GetDetail(String filepath){
		ReadDetailFile(filepath);
		return Detail;
	}

	// Get Distance Data
	public float[][] GetDistance(String filepath){
		ReadDistanceFile(filepath);
		return Distance;
	}
}