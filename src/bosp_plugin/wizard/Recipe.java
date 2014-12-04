package bosp_plugin.wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TableItem;

/**
 * The parameters inserted by the user during an BOSP New Application wizard are saved here
 * Contains only get and set methods for each of this variables
 * @author Russello Andrei
 * @version 1.1
 */
public class Recipe {
	
	/*** FIRST PAGE PARAMETERS***/
	//the name chosen by the user for his project
	private static String appName;
	//the location of this project
	private static String appLocation;
	//the application is developed using the language specified in this language
	private static String appLanguage;
	//true if user select to use API libraries
	private static boolean useLibrary;
	//true --> the user wants to create a new Recipe
	private static boolean newRecipe;

	/*** SECOND PAGE PARAMETERS***/
	//the ID and the AWM number for each inserted platform
	private static String[] platformsID;
	//the number of AWM present on index i is associated to the platform ID on the same index 
	private static int[] numAWM;
	//the ID of each inserted plug-in
	private static String[] pluginsID;
	private static boolean numAWMok = false;
	
	/*** THIRD PAGE PARAMETERS***/
	//contains the value of the AWM with the ID equal to the position in this array
	private static int valueAWM[];
	//contains the value of the AWM with the ID equal to the position in this array
	private static String nameAWM[];
	//contains all the info put in the table of each third type page
	//first dimension --> represents the number of elements that contains the table of the selected page
	//second dimension --> the number of field of the table
	private static List<String[][]> pathInfo;
	/*** THIRD PAGE PARAMETERS END***/
	
	/**
	 * @return the name given by the user to his application
	 */
	public static String getAppName() {
		return appName;
	}

	public static void setAppName(String appName) {
		Recipe.appName = appName;
	}

	
	public static String getAppLocation() {
		return appLocation;
	}
	
	public static boolean getNumAWMok(){
		return numAWMok;
	}

	/**
	 * @return the location selected by the user for the application
	 */
	public static void setAppLocation(String appLocation) {
		Recipe.appLocation = appLocation;
	}
	
	public static String getAppLanguage() {
		return appLanguage;
	}

	public static void setAppLanguage(String appLanguage) {
		Recipe.appLanguage = appLanguage;
	}
	public static boolean isNewRecipe() {
		return newRecipe;
	}

	public static void setNewRecipe(boolean newRecipe) {
		Recipe.newRecipe = newRecipe;
	}

	
	
	public static String getPlatNameWith(int idAWM){
		//the name of the platform associated to the specified AWM
		String platName;
		//the number of AWM until the considered row
		int numAWM = 0;
		//used to find out the row where the AWM is
		int rowAWM = 0; 
		//the row next to the AWM's row has the sum of AWM bigger the the pageID
		do{
			numAWM += Recipe.numAWM[rowAWM];
			rowAWM++;
		}
		while(idAWM >=numAWM && rowAWM < Recipe.numAWM.length);
		//the platform must be recovered using this Id
		platName = SettingsLoader.getNameWith(Recipe.platformsID[rowAWM - 1]);
		
		return platName;
	}

	/**
	 * once the second page is complete the number of used platforms are known
	 * @param length
	 */
	public static void setPlatNum(int length) {
		numAWM = new int[length];
		platformsID = new String[length];
		Recipe.numAWMok = true;
		
	}

	public static void savePlatform(int index, String platformId, int numAWM) {
		Recipe.platformsID[index] = platformId;
		Recipe.numAWM[index] = numAWM;
		
	}
	

	public static void savePlugin(int row, String pluginId) {
		Recipe.pluginsID[row] = pluginId;
	}

	
	public static int getAWMnumber(){
		int totAWM = 0;
		for(int index =0;index<numAWM.length;index++)
			totAWM += numAWM[index];
		return totAWM;
	}

	/**
	 * initialize the arrays containing AWM parameters
	 * NOTE: it can be performed only when second page is complete
	 */
	public static void setAWMNum() {
		//each AWM has a name and a value
		Recipe.valueAWM = new int[Recipe.getAWMnumber()];
		Recipe.nameAWM = new String[Recipe.getAWMnumber()];
		Recipe.pathInfo  = new ArrayList<String[][]>(); 
		
	}

	
	public static boolean usesLibrary() {
		return useLibrary;
	}

	public static void setUseLibrary(boolean useLibrary) {
		Recipe.useLibrary = useLibrary;
	}
	
	public static void setPlugNum(int length) {
		pluginsID = new String[length];
	}
	
	/**
	 * save the parameters of the specified third type wizard page
	 * @param id
	 * @param value
	 * @param name
	 */
	public static void setParams(int id,int value, String name) {
		Recipe.valueAWM[id] = value;
		Recipe.nameAWM[id] = name;
	}

	public static void setPaths(TableItem[] items) {
		//contains all the paths and associated data inserted in the page that called this method
		String[][] tableContent = new String[items.length-1][BOSPwizardPageThree.NUM_PATH_PARAM];
		for(int row=0;row<tableContent.length;row++){
			for(int col=0;col<tableContent[row].length;col++){
				tableContent[row][col] = items[row].getText(col);
			}
		}
		//saving the data
		pathInfo.add(tableContent);
		
	}

	/**
	 * writes on a file all the info memorized in this class
	 */
	public static void printRecipe() {
		if(newRecipe){
			BufferedWriter writer;
			try {
		    
		    	writer = new BufferedWriter(new FileWriter(Recipe.appLocation + File.separator +
						Recipe.appName+ File.separator  + "recipes" + File.separator + Recipe.appName 
						+ ".recipe",false));
		    	//the id of considered AWM
		    	int idAWM = 0;
		    	//the number of AWM that were already save in the .recipe file
		    	int savedAWM = 0;
			    writer.write("<BarbequeRTRM recipe_version=\"0.8\">\n" +
			    		"\t<application priority=\"4\">\n");
			    //for all the platforms defined by the user
			    for(int i=0;i< platformsID.length;i++){
			    	//writing platform ID
			    	writer.write("\t\t<platform id=\"" + platformsID[i] + "\">\n");
			    	//and the defined AWM
			    	writer.write("\t\t\t<awms>\n");
			    	/*in this cycle the number of saved AWM will be increased by an amount 
			    	 *equal to the number of AWM associated to this platform*/
			    	savedAWM += numAWM[i];
			    	for(;idAWM<savedAWM;idAWM++){
			    		writer.write("\t\t\t\t<awm id=\"" + idAWM + "\" name=\"" +
			    				nameAWM[idAWM] + "\" value=\"" + valueAWM[idAWM] + "\">\n");
				    	writer.write("\t\t\t\t\t<resources>\n");
				    	//the table content for this AWM
				    	String[][] tableContent = pathInfo.get(idAWM);
				    	for(int row = 0;row<tableContent.length;row++){
				    		String[] pathSplited = split(tableContent[row][0]);
				    		/**!!!!!!!!!!!!in  order to recover Requests and units fields 
				    		 * the following instruction are necessary
				    		 */
				    		/*int requests =Integer.valueOf(tableContent[row][1]);
				    		String units = tableContent[row][2];*/
				    		//writing on the recipe the resources present in this path
				    		for(int pathRes = 0;pathRes<pathSplited.length;pathRes++){
				    			for(int ind=0;ind<pathRes;ind++)
				    				writer.write("\t");
				    			writer.write("\t\t\t\t\t\t<" + pathSplited[pathRes] + " id=\"0\">\n");
				    			
				    		}
				    		
				    		writer.write("\t\t\t\t\t\t\t\t<pe qty=\"50\"/>\n");
					    	writer.write("\t\t\t\t\t\t\t\t<mem units=\"Mb\" qty=\"2\"/>\n");
				    		
				    		//printing the tags end
				    		for(int pathRes = pathSplited.length-1;pathRes>=0;pathRes--){
				    			for(int ind=0;ind<pathRes;ind++)
				    				writer.write("\t");
				    			writer.write("\t\t\t\t\t\t</" + pathSplited[pathRes] + ">\n");
				    			
				    		}
				    	}
				    	writer.write("\t\t\t\t\t</resources>\n");
				    	writer.write("\t\t\t\t</awm>\n");
			    	}
			    	writer.write("\t\t\t</awms>\n");
			    	writer.write("\t\t</platform>\n");
			    }
			    
			    writer.write("\t</application>\n");
		    	writer.write("</BarbequeRTRM>\n" +
		    	"<!--  vim: set tabstop=4 filetype=xml :  -->");
		    	
		    	writer.close();
		    
		    }
			 catch (IOException ex) {
				
			}
		} 
		
	}

	/**
	 * split the given path using the character "."
	 * @param path 
	 * @return an array with all the part of the string
	 */
	private static String[] split(String path) {
		//the number of Components present in this path
		int pathComp = 0;
		int index;
		String temp = path;
		do{
			index = temp.indexOf(".");
			if(index != -1)
				temp = temp.substring(path.indexOf('.') + 1);
			pathComp++;
		}
		while(index!=-1);
		String[] splitted = new String[pathComp];
		for(int i=0;i<splitted.length;i++){
			
			if(path.indexOf('.') != -1){
				splitted[i] = path.substring(0,path.indexOf('.'));
				path = path.substring(path.indexOf('.') + 1);
			}	
			else
				splitted[i] = path;
		}
		return splitted;
	}

}
