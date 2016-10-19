import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class main {
	// read file of detail data
	private static List<List> ReadFile(String filepath){
		List<List> Data = new ArrayList<>();
		List<Integer> Workload = new ArrayList<>();
		List<List> OtherData = new ArrayList<>();
		List<Float> Gamma = new ArrayList<>();
		
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("workload")) {
					String[] workloadData = line.split(",");
					Workload.add(Integer.parseInt(workloadData[1]));
				}
				else if(line.contains("gamma")) {
					String[] gammaData = line.split(",");
					Gamma.add(Float.parseFloat(gammaData[1]));
					
				}
				else if(line.contains("index")) {
					;
				}
				else {
					List<Integer> Detail = new ArrayList<>();  // rewards, penalty, etc
					String[] detailData = line.split(",");
					for(int i = 0; i < detailData.length; i++){
						if(i > 2) {
							Detail.add(Integer.parseInt(detailData[i]));
						}
					}
					OtherData.add(Detail);
				}
	        }
			Data.add(Workload);
			Data.add(Gamma);
			Data.add(OtherData);
			br.close();
		}
	    catch(IOException e) {
	    	System.out.println(e);
	    }
		
		return Data;
	}
	// read data for distance file

	
	// other functions, ex: find the largest blah blah blah

	public static void main(String[] args) {
		String filepath = "C:\\Users\\admin\\Desktop\\161018_data\\txt\\161018_real_data.txt";
		System.out.print(ReadFile(filepath));
		
		// do something, greedy algorithm
	}
} 
