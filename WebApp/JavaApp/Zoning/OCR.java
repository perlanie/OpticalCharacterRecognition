import java.io.*;
import java.util.*;

public class OCR{
	public static double[][] trainingData= new double[10][10];
	
	public static int zoning(double[] zoneVector){
		readCharacterData();
		int charVector=0;
		
		double minVectorDistance=calcZoningVector(zoneVector,0);
		//System.out.println("Current Vect Distance: "+minVectorDistance);
		for(int i=1;i<10;i++){
			double curVectorDistance=calcZoningVector(zoneVector,i);
			//System.out.println("Next Vect Distance: "+curVectorDistance+", i="+i );
			if(curVectorDistance<minVectorDistance){
				minVectorDistance=curVectorDistance;
				charVector=i;
			}
		}

		System.out.println("This Character is: "+charVector);
		return charVector;
	}

	public static double calcZoningVector(double[] zoneVector, int trainingDataRow){
		double curVectorDistance=0;
		for(int i=0;i<9;i++){
			//System.out.println("zone vector: "+zoneVector[i]+",trainingData: "+trainingData[trainingDataRow][i]);
			curVectorDistance=curVectorDistance+ Math.pow(zoneVector[i]-trainingData[trainingDataRow][i],2);
		}
		double vectorDistance=Math.sqrt(curVectorDistance);

		return vectorDistance;
	}


	public static void readCharacterData(){
		String line = null;
		BufferedReader reader=null;

		try{
			FileReader file = new FileReader("/Users/Perlanie/Documents/Projects/OCR/Zoning/CharTrainingData/CharData.txt");
			reader = new BufferedReader(file);
		}
		catch(FileNotFoundException f){
			System.out.println("Error: File not found");
		}

		try{
			int rowIndex=0;
			while((line = reader.readLine()) != null){
				if(line!=null){
					String[] dataRow=line.split(",");
					
					for(int c=0;c<10;c++){
						double value=Double.parseDouble(dataRow[c]);
						trainingData[rowIndex][c]=value;
						
					}
					rowIndex++;
				}
			}
			for(int r=0;r<10;r++){
				String row="";
				for(int c=0;c<10;c++){
					row=row+trainingData[r][c]+" ";
				}
				System.out.println(row);
			}
			reader.close();

		}
		catch (IOException e){
			System.out.println("Error: Cannot readfile");
		}
	}

	public static void writeCharacterData(double[] zoneVector, int intChar){
		System.out.println("Write Data...");
		int numIntCharData = (int)trainingData[intChar][9];//gets the number of values stored for the
		double[][] charTrainingData= new double[numIntCharData+1][9];
		String charDataFilePath="/Users/Perlanie/Documents/Projects/OCR/Zoning/CharTrainingData/"+intChar+".txt";

		String line = null;
		BufferedReader reader=null;
		
		//Read the corresponding char data text file
		try{
			FileReader file = new FileReader(charDataFilePath);
			reader= new BufferedReader(file);
		
			int rowIndex=0;
			
			while((line = reader.readLine()) != null){
				//System.out.println(line);
				if(line!=null){
					String[] dataRow=line.split(",");
			
					for(int c=0;c<9;c++){
						if(dataRow[c]!=""){
							double value=Double.parseDouble(dataRow[c]);
							charTrainingData[rowIndex][c]=value;
						}
					}
					rowIndex++;
				}
			}
			for(int i=0;i<9;i++){
				// adds new vector to the charTrainingData so it can be written its corresponding
				// char text file
				charTrainingData[numIntCharData][i]=zoneVector[i];
			}
			reader.close();

		}
		catch (IOException e){
			System.out.println("Error: Cannot readfile");
		}

		//write to the corresponding char data text file
		try {
	        BufferedWriter charDataWriter = new BufferedWriter(new FileWriter(charDataFilePath,true));
	        //write new vector to the char data text file
	    
        	String rowData= "\n"+String.valueOf(charTrainingData[charTrainingData.length-1][0]);
        	for(int colData=1;colData<9;colData++){
        		rowData=rowData+","+charTrainingData[charTrainingData.length-1][colData];
        	}
	        charDataWriter.write(rowData);

	        //erase the data in the file
	        BufferedWriter trainingDataWriter = new BufferedWriter(new FileWriter("/Users/Perlanie/Documents/Projects/OCR/Zoning/CharTrainingData/CharData.txt"));
	        //this allows you 
	        trainingDataWriter = new BufferedWriter(new FileWriter("/Users/Perlanie/Documents/Projects/OCR/Zoning/CharTrainingData/CharData.txt",true));
	        //writes new average to the training data file

	        double[] vectorAverage=getVectorAverage(charTrainingData,intChar);

	        for(int i=0;i<9;i++){
	        	trainingData[intChar][i]=vectorAverage[i];
	        }
	        trainingData[intChar][9]=trainingData[intChar][9]+1.0;

	        for(int row=0;row<10;row++){
	        	String rowTrainingData=String.valueOf(trainingData[row][0]);
	        	for(int col=1;col<10;col++){
	        		rowTrainingData=rowTrainingData+","+trainingData[row][col];
	        	}
	        	if(row!=9){
	        		rowTrainingData=rowTrainingData+"\n";
	        	}
	        	trainingDataWriter.write(rowTrainingData);
	        }
	        charDataWriter.close();
            trainingDataWriter.close();
	        
	    }   
        catch (IOException e) {
        	System.out.println("Error: Cannot write to file.");
        }
	}

	public static double[] getVectorAverage(double[][] zoneCharVectors, int intChar){
		double[] vectorAverage=new double[9];

		for(int c=0;c<9;c++){
			double colSum=0;
			for(int r=0; r<zoneCharVectors.length;r++){
				colSum=colSum+zoneCharVectors[r][c];
			}
			vectorAverage[c]=colSum/zoneCharVectors.length;
		}


		return vectorAverage;

	}
	public static void main(String[] args){
		readCharacterData();
		double[] zoneVector={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		int intChar=zoning(zoneVector);
		writeCharacterData(zoneVector,intChar);

	}

}