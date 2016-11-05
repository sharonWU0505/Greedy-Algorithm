import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpecialInput {
	// Attribute
	private static List<List> Detail;
	private static double[][] Distance;

	// Constructor
	public SpecialInput(){
		Detail = new ArrayList<>();
		Distance = null;
	}
	
	// Methods
	// Read Detail Data File
	private void ReadDetailFile(String filepath){
		List<List<Double>> OtherData = new ArrayList<>();
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
					List<Double> LineData = new ArrayList<>();  // rewards, penalty, splitN, processingT
					String[] lineData = line.split(",");
					for(int i = 0; i < lineData.length; i++){
						if(i > 2) {
							LineData.add(Double.parseDouble(lineData[i]));
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
			Distance = new double[locnum][locnum];     		// define Distance array
			for(int i = 0; i < locnum; i++){
				Distance[0][i] = Double.parseDouble(firstDistanceData[i]);	// put the first line into array
			}

			int locindex = 1;
			while((line = br.readLine()) != null) {
				String[] distanceData = line.split(",");
				for(int i = 0; i < locnum; i++){
					Distance[locindex][i] = Double.parseDouble(distanceData[i]);
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
	public double[][] GetDistance(String filepath){
		ReadDistanceFile(filepath);
		return Distance;
	}
}