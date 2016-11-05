import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpecialInput {
	// Attribute
	private static int[][] Distance;
	private static List<List> Detail;
	private List<List<Double>> OtherData = new ArrayList<>();

	// Constructor
	public SpecialInput(){
		Distance = null;
		Detail = null;
	}
	
	// Methods
	// Read Detail Data File
	private void ReadDetailFile(String filepath){
		List<List<Double>> OtherData = new ArrayList<>();
		List<Double> Workload = new ArrayList<>();
		List<Double> Gamma = new ArrayList<>();
		List<Double> Workdays = new ArrayList<>();
	
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
			Workdays.add(Double.parseDouble(workdaysData[0]));

			// for final output
			System.out.print(Workload.get(0));
			System.out.print(OtherData.get(0));
			System.out.print(Gamma.get(0));
			System.out.print(Workdays.get(0));
			Detail.add(Workload);
			Detail.add(OtherData);
			Detail.add(Gamma);
			Detail.add(Workdays);
			System.out.print(Detail);
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
	public int[][] GetDistance(String filepath){
		ReadDistanceFile(filepath);
		return Distance;
	}
}