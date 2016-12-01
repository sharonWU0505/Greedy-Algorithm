import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralInput {
	// Attribute
	private static List<List> Detail;
	private static float[][] Distance;
	private static float[] ComDistance;

	// Constructor
	public GeneralInput() {
		Detail = new ArrayList<>();
		Distance = null;
		ComDistance = null;
	}
	
	// Methods
	// Read Test File
	public void ReadTestFile(String filepath){
		List<List<Float>> OtherData = new ArrayList<>();
		List<Float> Workload = new ArrayList<>();
		List<Float> Gamma = new ArrayList<>();
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
					Gamma.add(Float.parseFloat(number[2]));
					Workdays.add(Integer.parseInt(number[1]));
					workdays = Integer.parseInt(number[1]);
					taskNum = Integer.parseInt(number[0]);
				}
				else if(lines == 2){
					String[] processTData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						List<Float> DetailData = new ArrayList<>();  // for saving rewards, processingT, penalty, splitN
						
						// decimal format test
						float temp = Float.parseFloat(processTData[i]);
						temp = (float) ((int) Math.floor(temp * 10000)) / 10000;
						DetailData.add(temp);
//						DetailData.add(Float.parseFloat(processTData[i]));
						
						OtherData.add(DetailData);
					}
				}
				else if(lines == 3){
					String[] workloadData = line.split(" ");
					for(int i = 0; i < workdays; i++){
						
						// decimal format test
						float temp = Float.parseFloat(workloadData[i]);
						temp = (float) ((int) Math.floor(temp * 10000)) / 10000;
						Workload.add(temp);
						
						
//						Workload.add(Float.parseFloat(workloadData[i]));
					}
				}
				else if(lines >= 4 && lines < (4 + taskNum)){
					String[] rewardsData = line.split(" ");
					for(int i = 0; i < workdays; i++){
						Float rewards = Float.parseFloat(rewardsData[i]);
						OtherData.get(lines - 4).add(i, rewards);
					}
				}
				else if(lines == (4 + taskNum)){
					String[] splitNData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						OtherData.get(i).add(Float.parseFloat(splitNData[i]));
					}
				}
				else if(lines == (5 + taskNum)){
					String[] penaltyData = line.split(" ");
					for(int i = 0; i < taskNum; i++){
						
						// decimal format test
						float temp = Float.parseFloat(penaltyData[i]);
						temp = (float) ((int) Math.floor(temp * 10000)) / 10000;
						OtherData.get(i).add(temp);
//						OtherData.get(i).add((workdays + 1), Float.parseFloat(penaltyData[i]));
						
					}
				}
				else if(lines == (6 + taskNum)){
					String[] distanceData = line.split(" ");
					Distance = new float[taskNum][taskNum];
					int index = 0;
					for(int i = 0; i < taskNum; i++){
						for(int j = i + 1; j < taskNum; j++){
							Distance[i][j] = Float.parseFloat(distanceData[index]);
							index++;
						}
					}
					for(int i = 0; i < taskNum; i++){
						for(int j = 0; j < i + 1; j++){
							if(j == i){
								Distance[i][j] = (float) 0.0;
							}
							Distance[i][j] = Distance[j][i];
						}
					}
				}
				else{
					String[] comdistanceData = line.split(" ");
					ComDistance = new float[taskNum];
					int index = 0;
					for(int i = 0; i < taskNum; i++){
						ComDistance[i] = Float.parseFloat(comdistanceData[index]);
						index++;
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
		
//		System.out.println("Distance: ");
//		for(int i = 0; i < taskNum; i++){
//			for(int j = 0; j < taskNum; j++){
//				System.out.print(Distance[i][j] + " ");
//			}
//			System.out.println();
//		}
		
//		System.out.println("Company Distance: ");
//		for(int i = 0; i < taskNum; i++){
//			System.out.print(ComDistance[i] + " ");
//		}
//		System.out.println();
	}

	// Get Detail Data
	public List<List> GetDetail(){
		return Detail;
	}

	// Get Distance Data
	public float[][] GetDistance(){
		return Distance;
	}
	
	// Get Company Distance Data
	public float[] GetComDistance(){
		return ComDistance;
	}
}