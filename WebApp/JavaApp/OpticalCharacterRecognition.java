import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.lang.Math;

public class OpticalCharacterRecognition{

	public static int colourBG = -1;
	public static double[][] trainingData= new double[10][10];

	public OpticalCharacterRecognition(){ 

	}

	public static void main (String[] args){
		boolean inUse=true;
		Scanner scan = new Scanner(System.in);

		while(inUse){
			System.out.println("\nPlease Choose an action (q to quit):");			
			System.out.println("======================================");
			System.out.println(" 1. Count Characters in image");
			System.out.println(" 2. Count Black Pixels in each character");
			System.out.println(" 3. Convolution");
			System.out.println(" 4. Scale down characters in image");
			System.out.println(" 5. Edge detector");
			System.out.println(" 6. Thinning");
			System.out.println(" 7. Get Zoning Vector");
			System.out.println(" 8. Write Character Training Data");
			System.out.println(" 9. Classify image with zoning");

			String usersAction=scan.nextLine();
			
			switch(usersAction){
				case "1": {//count regions
					System.out.println("Enter an image path: ");
					String path=scan.nextLine();
					if(path!=""){
						try{
							File imagePath = new File(path);
							BufferedImage image = ImageIO.read(imagePath);
							System.out.print("\nThere are "+ count(image).length + " characters in this image.\n \n");
						
						}
						catch(IOException er){
							System.out.print("Error: Could not find specified image.\n");
						}
					
					}
					else{
						System.out.print("No path specified. Please specify path.\n");
					}
					break;
				}
				case "2": {//counts the number of black pixels in a region
					System.out.println("Enter an image path: ");
					String path=scan.nextLine();
					if(path!=""){
						try{
							File imagePath = new File(path);
							BufferedImage image = ImageIO.read(imagePath);
							Integer[] pixelCountArray = count(image);
							for (int i = 0; i < pixelCountArray.length; i++){
								System.out.println("There are " + pixelCountArray[i] +  " black pixels in symbol " + (i+1));
							}
						}
						catch(IOException er){
							System.out.print("Error: Could not find specified image.\n");
						}
					
					}
					else{
						System.out.print("No path specified. Please specify path\n");
					}
					break;
				}
				case "3": {//convolution
					System.out.println("Enter an image path: ");
					String imagePath=scan.nextLine();
					if(imagePath!=""){
						convolution(imagePath);
						
					}
					else{
						System.out.print("No path specified. Please specify path\n");
					}
					break;
				}
				case "4": {//scaling
					System.out.println("Enter an image path: ");
					String path=scan.nextLine();
					System.out.println("Enter a scaling factor: ");
					String sFactor=scan.nextLine();
					if(path!="" && sFactor!=""){
						double sclFactor=Double.parseDouble(sFactor);
						resize(path,sclFactor);
					}
					else{
						System.out.print("Error: Make sure to enter correct information.\n");
					}

					break;
				}
				
				case "5": {//edge detection 
					System.out.println("Enter an image path: ");
					String imagePath=scan.nextLine();
					if(imagePath!=""){
						edgeDetection(imagePath);	
					}
					else{
						System.out.print("Error: Make sure to enter correct information.\n");
					}
					break;
				}
				case "6": {// thinning
					System.out.println("Enter a image path: ");
					String imagePath=scan.nextLine();
					
					try{
						File imageFile = new File(imagePath);
						BufferedImage image = ImageIO.read(imageFile);
						thinning(image);
						
					}
					catch(IOException er){
						System.out.print("Error: Could not find specified image.\n");
					}
					
					break;
				}
				case "7": {//subimage
					System.out.println("Enter a image path: ");
					String imagePath=scan.nextLine();
					if(imagePath!=""){
						try{
							File imageFile = new File("/Users/Perlanie/Documents/Projects/OCR/JavaApp/images/numbers.bmp");//imagePath);
							BufferedImage image = ImageIO.read(imageFile);
							BufferedImage thinned=thinning(image);
							BufferedImage[] images=getSegmentss(thinned);
							
							//showSingleImage(image.getSubimage(17,18,75,178),"seg2");
							for(int i=0;i<images.length;i++){
								//showSingleImage(images[i],"Segments");
								//BufferedImage thinned=thinning(images[i]);
								BufferedImage[][] subImages=getSubImages(thinning(images[i]), 3);
								double[] zoneVector=getZoneVector(subImages);
								String vector="["+zoneVector[0];
								for(int j=1;j<9;j++){
									vector=vector+","+zoneVector[j];
								}
								vector=vector+"]\n";
								System.out.println(vector);

							}

						}
						catch(IOException er){
							System.out.print("Error: Could not find specified image.\n");
						}
						
					}
					else{
						System.out.print("Error: Make sure to enter correct information.\n");
					}

					break;
				}
				case "8": {//writes vector data to the training file
					System.out.println("Enter zoning vector: ");
					String zv=scan.nextLine();
					System.out.println("Enter corresponding character: ");
					String enteredChar=scan.nextLine();
					if(zv!="" && enteredChar!=""){
						readCharacterData();
						double[] zoneVector=new double[9];
						String [] vector=zv.split(",");
						for(int i=0;i<9;i++){
							zoneVector[i]=Double.parseDouble(vector[i]);
						}
						int intChar=Integer.parseInt(enteredChar);
						writeCharacterData(zoneVector,intChar);
						System.out.print("Write to the following files completed: CharData.txt,"+intChar+".txt\n");
					}
					else{
						System.out.print("Error: Make sure to enter correct information.\n");
					}

					break;
				}
				case "9": {//zoning to classify character
					System.out.println("Enter a image path: ");
					String imagePath=scan.nextLine();
					if(imagePath!=""){
						try{
							File imageFile = new File(imagePath);//imagePath);
							BufferedImage image = ImageIO.read(imageFile);
							BufferedImage thinned=thinning(image);
							BufferedImage[][] subImages=getSubImages(thinned, 3);

							

							double[] zoneVector=getZoneVector(subImages);
							int intChar=zoning(zoneVector);
							boolean running=true;
							while(running){
								System.out.print("Is this correct (Y/N)?\n");
								String correct=scan.nextLine();

								switch(correct){
									case "Y":{
										writeCharacterData(zoneVector,intChar);
										System.out.print("Thank you for the new data.\n");
										running=false;
										break;
									}
									case "N":{
										System.out.print("What is the correct character (0-9)?\n");
										String correctChar=scan.nextLine();
										intChar=Integer.parseInt(correctChar);
										writeCharacterData(zoneVector,intChar);
										running=false;
										
										break;
									}
									default:{
										System.out.print("Error: Invalid Option. Please select Y/N.\n");
									}			
								}
								
							}
						
						}
						catch(IOException e){

						}
					}
					else{
						System.out.print("Error: Make sure to enter correct information.\n");
					}
					break;
				}
				case "q":{
					inUse=false;
					System.out.println("EXIT\n");
					scan.close();
					System.exit(0);
					break;
				}
				default:{
					System.out.print("Error: Invalid Option. Please try again.\n");
				}

			}
		}

	}
	public static Integer[] count(BufferedImage image){
		int pixelColour;
		int currentLabel = 1;
		int imgHeight = image.getHeight(); // height of image
		int imgWidth = image.getWidth(); // width of image
		int[][] labels = new int[imgWidth][imgHeight]; //2d integer array used to store lables of pixels
		LinkedList<Integer> counts = new LinkedList<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		counts.add(0);
		for (int y = 0; y < imgHeight; y++){ // check all rows
			for (int x = 0; x < imgWidth; x++){ // check all pixels the row
				
				pixelColour = image.getRGB(x,y); // colour of the pixel

				if (pixelColour != colourBG && labels[x][y] == 0){ // if pixel is not part of the background and if pixel is not already labeled
						labels[x][y] = currentLabel; // give pixel current label
						counts.set(currentLabel-1, counts.get(currentLabel-1) + 1);
						queue.addFirst(x);// add x coordinate to queue 
						queue.addFirst(y);// and add y coordinate to queue
				}
				
				while (queue.size() != 0){ // while there are pixels in the queue
					
					int curY = queue.removeFirst();// get y coordinate of the first pixel in the queue
					int curX = queue.removeFirst();// get x coordinate of the first pixel in the queue
					
					for (int i = -1; i <= 1; i++){ // check adjacent pixels on x axis
						for (int k = -1; k <= 1; k ++){ // check adjacent pixels on y axis
							if (curX + i >= 0 && curX + i <= imgWidth - 1 && curY + k >= 0 && curY + k <= imgHeight-1){ // make sure that adjacent pixel is in bounds
								if (image.getRGB(curX+i,curY+k) != colourBG && labels[curX + i][curY + k] == 0){ // if adjacent pixel is not part of the backgound and is not already labeled
									labels[curX+i][curY+k] = currentLabel; // give pixel current label
									queue.addFirst(curX+i);// add x coordinate to queue 
									queue.addFirst(curY+k);// and add y coordinate to queue
									counts.set(currentLabel-1, counts.get(currentLabel-1) + 1);							
								}
							}	
						}
					}
					if (queue.size() == 0){ // if the current symbol has no more pixels in it
						currentLabel ++; // increment label
						counts.add(0);
					}
				}
			}
		}
		counts.remove(counts.size() - 1);
		return counts.toArray(new Integer[counts.size()]);
	}

	
	/*============================================================
	imageConvolution: gets the convolution of the image specified
	with {0.2,0.2,0.2,0.2,0.2} horizontally and vertically.
	=============================================================*/
	public static int[][] convolution(String path){
		double [] convolution={0.2,0.2,0.2,0.2,0.2};// convolution matrix
		int width=0; //intializes width of photo
		int height=0;//intializes height of photo
		int[][] orgImage={{0},{0}}; //2D array to store original image pixel information
		int[][] conImage={{0},{0}};//2D array to store convoluted image pixel information (horiztonal)
		int[][] conImageFinal={{0},{0}};//2D array to store convoluted image pixel information (vertical)

		try{
			File imagePath=new File(path);
			BufferedImage image = ImageIO.read(imagePath);
			width =image.getWidth();
			height =image.getHeight();
			orgImage=new int [height][width];
			conImage=new int [height][width];
			conImageFinal=new int [height][width];

			int red, green, blue;
			//gets the pixel informtion from the original image and stores it in the designated 2D array
			for (int r=0; r<height; r++){
				for (int c = 0; c< width; c++) {
					int pixelColour=image.getRGB(c, r);
					red=(pixelColour >> 16) & 0XFF ;
					green=(pixelColour>>8)& 0XFF;
					blue=pixelColour & 0XFF;

					//orgImage[r][c] = image.getRGB(c, r);
					orgImage[r][c] = (int)((red+green+blue)/3);

				}
			}

			//applies convolution matrix to the original image 2D array and stores new values in conImage 2D array first iteration
			for (int row = 0; row < height; row++) {		
				//String pixels="";

		        for (int column = 0; column < width; column++) {
		        	double total=0;//stores total value for the image

		        	if ((column)<2 || column>(width-3) || (row)<2 || row>(height-3)){//if it pixel is close to the edge
		        		if((column)==0){
		        			//total = total + (orgImage[row][column+1] * convolution[3]);
		        			//total = total + (orgImage[row][column+2] * convolution[4]);
		        		}
		        		else if((column)==1){
		        			total = total + (orgImage[row][column-1] * convolution[1]);
		        			total = total + (orgImage[row][column+1] * convolution[3]);
		        			total = total + (orgImage[row][column+2] * convolution[4]);
		        		}
		        		else if(((width-1)-(column))==1){
		        			total = total + (orgImage[row][column-2] * convolution[0]);
		        			total = total + (orgImage[row][column-1] * convolution[1]);
		        			total = total + (orgImage[row][column+1] * convolution[3]);
		        		}
		        		else if((width-1)==column){
		        			//total = total + (orgImage[row][column-2] * convolution[0]);
		        			//total = total + (orgImage[row][column-1] * convolution[1]);
		        		 }
		        		total=orgImage[row][column];

		        	}
		        	else{//other pixels
		        		total = total + (orgImage[row][column-2] * convolution[0]);
		        		total = total + (orgImage[row][column-1] * convolution[1]);
		        		total = total + (orgImage[row][column] * convolution[2]);
		        		total = total + (orgImage[row][column+1] * convolution[3]);
		        		total = total + (orgImage[row][column+2] * convolution[4]);

		        	}
		        	//total = total + (orgImage[row][column] * convolution[2]);
		        	conImage[row][column]=(int)total;

		        	//pixels= pixels+conImage[row][column]+ " ";//used for debugging
		    	}
		        //System.out.println(pixels);//used for debugging
      		}
      		
      		//applies convolution matrix to the original image 2D array and stores new values in conImage 2D array final iteration

		    for (int columnNext = 0; columnNext < width; columnNext++) {
		    	//String pixels="";
		    	for (int rowNext = 0; rowNext < height; rowNext++) {		
				
		        	double total=0;//stores total value for the image

		        	if ((rowNext)<2 || rowNext>(height-3)){//if it pixel is close to the edge
		        		if((rowNext)==0){
		        			total = total + (conImage[rowNext+1][columnNext] * convolution[3]);
		        			total = total + (conImage[rowNext+2][columnNext] * convolution[4]);
		        		}
		        		else if((rowNext)==1){
		        			total = total + (conImage[rowNext-1][columnNext] * convolution[1]);
		        			total = total + (conImage[rowNext+1][columnNext] * convolution[3]);
		        			total = total + (conImage[rowNext+2][columnNext] * convolution[4]);
		        		}
		        		else if(((height-1)-(rowNext))==1){
		        			total = total + (conImage[rowNext-2][columnNext] * convolution[0]);
		        			total = total + (conImage[rowNext-1][columnNext] * convolution[1]);
		        			total = total + (conImage[rowNext+1][columnNext] * convolution[3]);
		        		}
		        		else if((height-1)==rowNext){
		        			total = total + (conImage[rowNext-2][columnNext] * convolution[0]);
		        			total = total + (conImage[rowNext-1][columnNext] * convolution[1]);
		        		}
		        		total=conImage[rowNext][columnNext];

		        	}
		        	else{//other pixels
		        		total = total + (conImage[rowNext-2][columnNext] * convolution[0]);
		        		//System.out.println(conImage[rowNext-2][columnNext] +" * "+convolution[0]);
		        		total = total + (conImage[rowNext-1][columnNext] * convolution[1]);
		        		//System.out.println(conImage[rowNext-1][columnNext] +" * "+convolution[1]);
		        		total = total + (conImage[rowNext+1][columnNext] * convolution[3]);
		        		//System.out.println(conImage[rowNext+1][columnNext] +" * "+convolution[3]);
		        		total = total + (conImage[rowNext+2][columnNext] * convolution[4]);
		        		//System.out.println(conImage[rowNext+2][columnNext] +" * "+convolution[4]);

		        	}
		        	total = total + (conImage[rowNext][columnNext] * convolution[2]);
		  
		        		conImageFinal[rowNext][columnNext]=(int)total;

		        	//pixels= pixels+conImage[rowNext][columnNext]+ " ";//used for debugging
		    	}
		        //System.out.println(pixels);//used for debugging
      		}
      		


      		
      		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//creates image to be displayed
      		
      		//sets the pixels to their corresponding pixel values for the image
      		for(int i=0;i<width;i++){
      			for(int j=0;j<height;j++){

      				//red, green, blue
      				int rgb = conImageFinal[j][i]<<16 | conImageFinal[j][i] << 8 | conImageFinal[j][i];
        			img.setRGB(i, j, rgb);

      			}
      		}

    
      		//window settings for image to be displayed 
      		showImage(image,img,"Convolution Applied to Image");
		}
		catch(IOException e){
			System.out.println("Cannot read file: Does not exist or wrong format.");
		}
		
		return conImageFinal;

	}

	public static void resize(String url, double scalingfactor){
		int width;
		int height;
		int scaledwidth;
		int scaledheight;
		
		try{
			File path = new File(url);
			BufferedImage image = ImageIO.read(path);
			width = image.getWidth();
			height = image.getHeight();
			System.out.println("Height: "+ height+ ", Width: " + width);
			int [][] imageArray = new int [width][height];
			scaledwidth = (int)(width * scalingfactor);
			scaledheight = (int)(height * scalingfactor);
			System.out.println("Scaled Height: "+ scaledheight+ ", Scaled Width: " + scaledwidth);
			
			
			for (int i=0;i < height;i++){ //looking down vertically
				for (int j=0;j < width; j++) //looking horizontally
					imageArray[i][j] = image.getRGB(i,j);//getting all current pixel colour of current
			}
      		
			int pixelsToCountX = (int)(width / scaledwidth);
			int pixelsToCountY = (int)(height / scaledheight);

			int remainderX=width%scaledwidth;
			int remainderY=height%scaledheight;

			if(remainderX!=0){
				scaledwidth=scaledwidth +(remainderX/pixelsToCountX);
			}	
			if(remainderY!=0){
				scaledheight=scaledheight+(remainderY/pixelsToCountY);
			}
		
			int [][] newArray = new int [scaledwidth][scaledheight];

			for (int col=0; col < scaledwidth; col++){ //new scaled height
				for (int row = 0; row < scaledheight; row++){ //new scaled width
					int pixelSum = 0;
					
					for(int c=(col*pixelsToCountX); c<((col*pixelsToCountX)+pixelsToCountX);c++){
						for(int r=(row*pixelsToCountY); r<((row*pixelsToCountY)+pixelsToCountY);r++){
							pixelSum=pixelSum+ imageArray[r][c];
						}
					}
					int colour = (int)(pixelSum / (pixelsToCountX*pixelsToCountY));
					int ignoreEdge=2;
					if(scalingfactor>=0.7){
						ignoreEdge=8;
					}else if (scalingfactor>=0.3) {
						ignoreEdge=5;
					}
					if(colour < (-8388608) && !(row>(scaledwidth-ignoreEdge) || row<(ignoreEdge) )){
								 			
		        		newArray[col][row]=-16777216;	
		        	}
		       
		        	else{
		        		newArray[col][row]=-1;
		        	}
					
				}
			}

			BufferedImage img = new BufferedImage(width, height, 3);//creates image to be displayed
      		
      		//sets the pixels to their corresponding pixel values for the image
      		for(int i=0;i<scaledwidth;i++){
      			for(int j=0;j<scaledheight;j++){
      				
      				int rgb = newArray[j][i]<<16 | newArray[j][i] << 8 | newArray[j][i];
        			img.setRGB(i, j, rgb);

      			}
      		}

      		showImage(image,img, "Scaled Image");
      		
		}
		catch(IOException e){
			System.out.println("Cannot open file");
		}
	}

	public static void showImage(BufferedImage ogImg, BufferedImage img, String title){
		int width=img.getWidth();
		int height=img.getHeight();

        JFrame imgWindow= new JFrame(title);
  		imgWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  		imgWindow.setPreferredSize(new Dimension(width*4,height+50));

  		JPanel imgPanel= new JPanel();
  		imgPanel.add(new JLabel("Orginal Image: "));
  		imgPanel.add(new JLabel(new ImageIcon(ogImg)));
  		imgPanel.add(new JLabel("      "));
  		imgPanel.add(new JLabel("New Image: "));
  		JLabel imgDisplay= new JLabel(new ImageIcon(img));
  		imgPanel.add(imgDisplay);

  		imgWindow.getContentPane().add(imgPanel);
  		imgWindow.pack();
        imgWindow.setLocationRelativeTo(null);
        imgWindow.setVisible(true);
        //ImageIO.write(bufferedImage, "png", new File("a.png"));
        System.out.println("\nPlease refer to displayed image.\n");
	}
	public static void showSingleImage(BufferedImage img, String title){
		JFrame imgWindow= new JFrame(title);
		System.out.println("\n"+img.getWidth() + "x" + img.getHeight()+"\n");       
  		JLabel imgDisplay= new JLabel(new ImageIcon(img));
  		JPanel imgPanel= new JPanel();
		
  		imgWindow.setPreferredSize(new Dimension(img.getHeight() + 50, img.getWidth() + 50));
  		imgPanel.setPreferredSize(new Dimension(img.getHeight() + 50, img.getWidth() + 50));
  		imgDisplay.setPreferredSize(new Dimension(img.getHeight(), img.getWidth()));
		
  		imgPanel.add(imgDisplay);
  		imgWindow.getContentPane().add(imgPanel);
  		imgWindow.pack();
		imgWindow.setLocationRelativeTo(null);
        imgWindow.setVisible(true);

        //System.out.println("\nPlease refer to the updated image.\n");
	}
	

	public static void edgeDetection(String path){
		double[][] kernal = {{-1,0,1},{-1,0,1},{-1,0,1}};
		int[][] orgImage={{0},{0}}; //2D array to store original image pixel information
		int[][] edgeImage={{0},{0}};//2D array to store edge detection image pixel information 
		int width=0;
		int height=0;

		try{
			File imagePath=new File(path);
			BufferedImage image = ImageIO.read(imagePath);
			width=image.getWidth();
			height=image.getHeight();
			orgImage=new int [height][width];
			edgeImage=new int [height][width];
			//conImageFinal=new int [height][width];

			int red, green, blue;
			//gets the pixel informtion from the original image and stores it in the designated 2D array
			for (int r=0; r<height; r++){
				for (int c = 0; c< width; c++) {
					int pixelColour=image.getRGB(c, r);
					red=(pixelColour >> 16) & 0XFF ;
					green=(pixelColour>>8)& 0XFF;
					blue=pixelColour & 0XFF;
					orgImage[r][c] = (int)((red+green+blue)/3);

				}
			}

			for (int row = 0; row < height; row++) {		
				//String pixels="";
		        for (int column = 0; column < width; column++) {
		        	double total=0;//stores total value for the image
		        	//int[] neighbours=getNeighbours(row,column);
		        	if ((column)>1 && column<(width-2) && (row)>1 && row<(height-2)){//if it pixel is close to the edge
		        		total = total + (orgImage[row][column] * kernal[1][1]);//P1
		        		total = total + (orgImage[row-1][column] * kernal[0][1]);//P2
		        		total = total + (orgImage[row+1][column-1] * kernal[0][2]);//P3
		        		total = total + (orgImage[row][column+1] * kernal[1][2]);//P4
		        		total = total + (orgImage[row+1][column+1] * kernal[2][2]);//P5
		        		total = total + (orgImage[row+1][column] * kernal[2][1]);//P6
		        		total = total + (orgImage[row+1][column-1] * kernal[2][0]);//P7
		        		total = total + (orgImage[row][column-1] * kernal[1][0]);//P8
		        		total = total + (orgImage[row-1][column-1] * kernal[0][0]);//P9

		        	}
		        	else{
		        		total=orgImage[row][column];

		        	}
		        	edgeImage[row][column]=(int)total;

		        	//pixels= pixels+conImage[row][column]+ " ";//used for debugging
		    	}
		        //System.out.println(pixels);//used for debugging
      		}


			//gets image ready to be displayed
			BufferedImage edgeImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//creates image to be displayed
      		
      		//sets the pixels to their corresponding pixel values for the image
      		for(int i=0;i<width;i++){
      			for(int j=0;j<height;j++){

      				//red, green, blue
      				int rgb = edgeImage[j][i]<<16 | edgeImage[j][i] << 8 | edgeImage[j][i];
        			edgeImg.setRGB(i, j, rgb);

      			}
      		}

    
      		//window settings for image to be displayed 
      		showImage(image,edgeImg,"Image After Edge Detection Applied");
		}
		catch(IOException e){
			System.out.println("Cannot read file: Does not exist or wrong format.");
		}

	}
	
	public static int[] getNeighbours(BufferedImage img, int x, int y){
		int[] neighbours = new int[8];
		neighbours[0] = img.getRGB(x,y-1); //P2
		neighbours[1] = img.getRGB(x+1,y-1); //P3
		neighbours[2] = img.getRGB(x+1,y); //P4
		neighbours[3] = img.getRGB(x+1,y+1); //P5
		neighbours[4] = img.getRGB(x,y+1); //P6
		neighbours[5] = img.getRGB(x-1,y+1); //P7
		neighbours[6] = img.getRGB(x-1,y); //P8
		neighbours[7] = img.getRGB(x-1,y-1); //P9
		return neighbours;
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
	 	ColorModel cm = bi.getColorModel();
	 	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	 	WritableRaster raster = bi.copyData(null);
	 	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage thinning(BufferedImage img){ //implementation of ZS thinning algorithm
		
		BufferedImage ogImg=deepCopy(img);
		int imgHeight = img.getHeight(); // height of image
		int imgWidth = img.getWidth(); // width of image
		boolean cont;
		
		// bufferedImage
		do{
			cont = false;
			LinkedList<Integer> marked = new LinkedList<Integer>();
		
			//step 1 
			for (int y = 1; y < imgHeight -1; y++){ // check all rows
				for (int x = 1; x < imgWidth -1; x++){ // check all pixels the row
					if (img.getRGB(x,y) != colourBG){ // check if pixel is black
						
						int neighbours[] = getNeighbours(img,x,y);
						int blackNeighbours = 0;
						int transitions = 0;
						
						//find number of black neighbours
						for (int i = 0; i < 8; i++){
							if (neighbours[i] != colourBG){
								blackNeighbours++;
							}
						}
						
						//find number of transitions
						for (int i = 0; i < 7; i++){
							if (neighbours[i] == colourBG && neighbours[i+1] != colourBG){
								transitions++;
							}
						}
						if (neighbours[7] == colourBG && neighbours[0] != colourBG){
							transitions++;
						}
						
						//check conditions		
						if (neighbours[0] == colourBG || neighbours[2] == colourBG || neighbours[4] == colourBG){ //if P2 or P4 or P6 is white
							if (neighbours[2] == colourBG || neighbours[4] == colourBG || neighbours[6] == colourBG){ //if P4 or P6 or P8 is white
								if (blackNeighbours >=2 && blackNeighbours <= 6){ // 2 <= B(P1) <= 6
									if (transitions == 1){ // A(P1) = 1
										cont = true;
										marked.addFirst(x); // add x value for current pixel to queue
										marked.addFirst(y); // add y value for current pixel to queue
									}
								}
							}
						}				
					} 
				}
			}
			
			//set all marked pixels to white
			while(marked.size() != 0){
				int curY = marked.removeFirst();// get y coordinate of the first pixel in the queue
				int curX = marked.removeFirst();// get x coordinate of the first pixel in the queue'
				img.setRGB(curX,curY,colourBG);
			}
			
			//step 2
			for (int y = 1; y < imgHeight -1; y++){ // check all rows
			for (int x = 1; x < imgWidth -1; x++){ // check all pixels the row
				if (img.getRGB(x,y) != colourBG){ // check if pixel is black
					
					int neighbours[] = getNeighbours(img,x,y);
					int blackNeighbours = 0;
					int transitions = 0;
					
					//find number of black neighbours
					for (int i = 0; i < 8; i++){
						if (neighbours[i] != colourBG){
							blackNeighbours++;
						}
					}
					
					//find number of transitions
					for (int i = 0; i < 7; i++){
						if (neighbours[i] == colourBG && neighbours[i+1] != colourBG){
							transitions++;
						}
					}
					if (neighbours[7] == colourBG && neighbours[0] != colourBG){
						transitions++;
					}
					
					//check conditions		
					if (neighbours[0] == colourBG || neighbours[2] == colourBG || neighbours[6] == colourBG){ //if P2 or P4 or P8 is white
						if (neighbours[0] == colourBG || neighbours[4] == colourBG || neighbours[6] == colourBG){ //if P2 or P6 or P8 is white
							if (blackNeighbours >=2 && blackNeighbours <= 6){ // 2 <= B(P1) <= 6
								if (transitions == 1){ // A(P1) = 1
									cont = true;
									marked.addFirst(x); // add x value for current pixel to queue
									marked.addFirst(y); // add y value for current pixel to queue
								}
							}
						}
					}				
				} 
			}
		}
		
			//set all marked pixels to white
			while(marked.size() != 0){
				int curY = marked.removeFirst();// get y coordinate of the first pixel in the queue
				int curX = marked.removeFirst();// get x coordinate of the first pixel in the queue'
				img.setRGB(curX,curY,colourBG);
			}
		}while(cont);
		
		showImage(ogImg,img, "Thinned Image");
		return img;
	}


	public static BufferedImage[] getSegments(BufferedImage image){
		int [][] segmentLabels = getSegmentLabels(image);
		// for (int i = 0; i < segmentLabels.length; i++){
		// 	for (int j = 0; j < segmentLabels[0].length; j++)
		// 		System.out.print(segmentLabels[i][j] + ", ");
		// 	System.out.println();
		// }

		LinkedList<int[][]> segmentDimensions = new LinkedList<int[][]>();
		for (int i=0; i < segmentLabels.length; i++)
			for (int j=0; j < segmentLabels[0].length; j++){
				int label = segmentLabels[i][j];
				if (label < segmentDimensions.size()){
					int[] topLeft = segmentDimensions.get(label)[0];
					int[] bottomRight = segmentDimensions.get(label)[1];
					if(i < topLeft[0]){
						topLeft[0] = i;
					}
						
					if(j < topLeft[1]){
						topLeft[1] = j;
					}
						
					if(i > bottomRight[0]){
						bottomRight[0] = i;
					}
					if (j > bottomRight[1]){
						bottomRight[1] = j;
					}
					segmentDimensions.set(label, new int[][]{{topLeft[0],topLeft[1]}, {bottomRight[0],bottomRight[1]}});
					// if (label == 1)
					// 	System.out.println(topLeft[0] + ", " + topLeft[1] + ", " + bottomRight[0] + ", " + bottomRight[1]);

				}
				else{
					segmentDimensions.addLast(new int[][]{{i,j}, {i,j}});
				}
				
					
			}

			//showSingleImage(image.getSubimage(10,17,78,133),"seg");
		BufferedImage[] segments = new BufferedImage[segmentDimensions.size()];
		for (int seg = 0; seg < segments.length; seg++){
			int[] topLeft = segmentDimensions.get(seg)[0];
			int[] bottomRight = segmentDimensions.get(seg)[1];
			//System.out.println(topLeft[0]+","+topLeft[1]+","+ bottomRight[0]+","+bottomRight[1]);
			segments[seg] = getSubImage(image,topLeft[0],topLeft[1], bottomRight[0],bottomRight[1]);
			
			for (int i = 0; i < segments[seg].getHeight(); i++){
				for (int j = 0; j < segments[seg].getWidth(); j++){
					if (segmentLabels[i+topLeft[0]][j+topLeft[1]] != seg){
						BufferedImage img=segments[seg];//.getRGB(i,j)=(-16777216);
						img.setRGB(i,j,-1);
					}
				}
			}
		}
		return segments;
	}

	public static int[][] getSurroundingPxls(BufferedImage img, int r, int c){
		int[][] surroundingPxls = new int[8][2];
		int height=img.getHeight();
		int width=img.getWidth();

		if(r>0 && c>0){
			surroundingPxls[7] = new int[]{r-1,c-1};
		}
		if(r>0){
			surroundingPxls[6] = new int[]{r-1,c};
		}
		if(r>0 && c<width-1){
			surroundingPxls[5] = new int[]{r-1,c+1};
		}
		if(c<width-1){
			surroundingPxls[4] = new int[]{r,c+1};
		}
		if(r<height-1 && c<width-1){
			surroundingPxls[3] = new int[]{r+1,c+1};
		}
		if(r<width-1){
			surroundingPxls[2] = new int[]{r+1,c};
		}
		if(r<height-1 && c>0){
			surroundingPxls[1] = new int[]{r+1,c-1};
		}
		if(c>0){
			surroundingPxls[0] = new int[]{r,c-1};
		}
		 
		return surroundingPxls;
	}
	private static int[][] getSegmentLabels(BufferedImage image){
		int currentLabel = 0;
		int height = image.getHeight();
		int width = image.getWidth();
		int[][] segmentLabels = new int[height][width];
		LinkedList<int[]> locations= new LinkedList<int[]>();
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){
				if (image.getRGB(i,j)==-16777216 && segmentLabels[i][j] == 0){//if black and not assigned
					currentLabel++;
					segmentLabels[i][j] = currentLabel;
					locations.addFirst(new int[]{i,j});
				}
				while (!locations.isEmpty()){
					int[] location = locations.removeLast();
					int [][] surroundingPxls=getSurroundingPxls(image,location[0],location[1]);
					for (int[] adjLoc : surroundingPxls){
						if (image.getRGB(adjLoc[0],adjLoc[1])==-16777216){
							int r = adjLoc[0];
							int c = adjLoc[1];
							if (segmentLabels[r][c] == 0){
								segmentLabels[r][c] = currentLabel;
								locations.addFirst(adjLoc);
							}
						}
					}
				}
			}
		}
		return segmentLabels;
	}



	public static BufferedImage[][] getSubImages(BufferedImage img, int rootSize){
		float rowStep = img.getHeight() / rootSize;
		float colStep = img.getWidth() / rootSize;
		System.out.println(String.valueOf(img.getHeight()) + "x" + String.valueOf(img.getWidth()));
		BufferedImage[][] subImages = new BufferedImage[rootSize][rootSize];
		for (int i = 0; i < rootSize; i++){
			for (int j = 0; j < rootSize; j++){
				int startRow = i*Math.round(rowStep);
				int startCol = j*Math.round(colStep);
				//int endRow = (i+1)*Math.round(rowStep) - 1;
				//int endCol = (j+1)*Math.round(colStep) - 1;
				int height=Math.round(rowStep);
				int width=Math.round(colStep);
				if(height+startRow>img.getHeight()){
					height=height-1-startRow;
				}
				if(width+startCol>img.getWidth()){
					width=width-1-startCol;

				}
				System.out.println("[" + String.valueOf(startRow) + "," + String.valueOf(startCol) + "]" + "=> [" + String.valueOf(startRow +height) + "," + String.valueOf(startCol +width) + "]");
				//subImages[i][j] = getSubImage(img, startRow, startCol, endRow, endCol);
				subImages[i][j]=img.getSubimage(startCol,startRow,width,height);
			}
		}
		return subImages;
	}

	public static BufferedImage getSubImage(BufferedImage original, int startRow, int startCol, int endRow, int endCol){
		int r = endRow - startRow + 1;
		int c = endCol - startCol + 1;
		BufferedImage img = new BufferedImage(r,c,3);
		for (int i = 0; i < r; i++)
			for (int j = 0; j < c; j++)
	        	img.setRGB(j, i, original.getRGB(j + startCol, i + startRow)<<16 | original.getRGB(j + startCol, i + startRow) << 8 | original.getRGB(j + startCol, i + startRow));
		return img;
	}

	public static BufferedImage[] getSegmentss(BufferedImage img){
		String[] nums={"0","1","4","6","7","8","9"};
		BufferedImage[] segments=new BufferedImage[nums.length];
		int index=0;
		for(String num: nums){
			try{
				File imageFile = new File("./images/"+num+".bmp");//imagePath);
				BufferedImage image = ImageIO.read(imageFile);
				segments[index]=image;
				index++;

			}
			catch(IOException e){

			}
			
		}
		
		
		return segments;
	}
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
			FileReader file = new FileReader("./CharTrainingData/CharData.txt");
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
		int numIntCharData = (int)trainingData[intChar][9];//gets the number of values stored for the num of times saved
		//System.out.println("num stored: "+numIntCharData);
		double[][] charTrainingData= new double[numIntCharData+1][9];
		String charDataFilePath="./CharTrainingData/"+intChar+".txt";

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
	        BufferedWriter trainingDataWriter = new BufferedWriter(new FileWriter("./CharTrainingData/CharData.txt"));
	        //this allows you 
	        trainingDataWriter = new BufferedWriter(new FileWriter("./CharTrainingData/CharData.txt",true));
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

	public static double[] getZoneVector(BufferedImage[][] subImages){
		double [] zoneVector=new double[9];
		int index=0;

		for(int r=0;r<3;r++){
			//System.out.println("stuck here");
			for(int c=0;c<3;c++){
				BufferedImage image=subImages[r][c];
				int numBlackPxls=0;
				Integer[] countBlkPxls=count(image);
				for(int i: countBlkPxls){
					numBlackPxls=numBlackPxls+i;

					
				}
				//System.out.println(numBlackPxls);
				int numWhitePxls=(image.getHeight()*image.getWidth())-numBlackPxls;
				
				double ratio=(double)numBlackPxls/numWhitePxls;	
				
				
				zoneVector[index]=round(ratio,2);
				//System.out.println(zoneVector[index]);
				index++;
			}

		}


		return zoneVector;
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
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

}