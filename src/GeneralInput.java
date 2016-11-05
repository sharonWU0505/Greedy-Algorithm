import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralInput {
	// Attribute
	private static List<List> Detail;
	private static double[][] Distance;

	// Constructor
	public GeneralInput() {
		Detail = new ArrayList<>();
		Distance = null;
	}
	
	// Methods
	// Read Test File
	public void ReadTestFile(String filepath){
		List<List<Double>> OtherData = new ArrayList<>();
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
					Gamma.add(Double.parseDouble(number[2]));
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
				else{
					String[] distanceData = line.split(" ");
					Distance = new double[taskNum][taskNum];
					int index = 0;
					for(int i = 0; i < taskNum; i++){
						for(int j = i + 1; j < taskNum; j++){
							Distance[i][j] = Double.parseDouble(distanceData[index]);
						}
					}
					for(int i = 0; i < taskNum; i++){
						for(int j = 0; j < i + 1; j++){
							if(j == i){
								Distance[i][j] = 0.0;
							}
							Distance[i][j] = Distance[j][i];
						}
					}
				}

				lines += 1;
			}

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

	// Get Detail Data
	public List<List> GetDetail(){
		return Detail;
	}

	// Get Distance Data
	public double[][] GetDistance(){
		return Distance;
	}
}