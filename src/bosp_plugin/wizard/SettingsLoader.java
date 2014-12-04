package bosp_plugin.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Combo;

import bosp_plugin.Activator;

public class SettingsLoader {
	//for each platform it's necessary a name and an ID
	private final static int PLAT_PARAMETERS = 2;
	//for each plug-in it's necessary a name, an ID, a description
	private final static int PLUG_PARAMETERS = 3;
	//for the resources it's necessary only the ID
	private final static int RES_PARAMETERS = 2;
	//list with the names and the ID's of all platforms
	private static List<String[]> platforms;
	private static List<String[]> plugins;
	//the path(in AWM page) can be made using only the ID of resources present in this list
	//also is saved the name of the resource
	private static List<String[]> resources;
	//the path of the file from what are extracted some wizard options
	private static String settingsPath = Activator.getDefault().getPreferenceStore().getString("PATH") +
			File.separator + "barbeque" +
			File.separator + "config" + File.separator +
			"templates" + File.separator + "settings.xml";
	private static int maxPriority;
	
	public static String settingsPath(){
		return settingsPath;
	}
	/**
	 * @return true if the languages was extracted; false means that it is a trouble with file
	 */
	public static boolean getLanguages(Combo langCombo){
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(settingsPath));
			String sCurrentLine;
			sCurrentLine = br.readLine();
			//it's also possible to stop when tag </languages> is found
			while ((sCurrentLine != null) && ((sCurrentLine.trim().compareTo("</languages>")) != 0)) {
				// there is a lane with a language
				if (sCurrentLine .contains("language name")){
					//NOTE: substring takes all the characters between startIndex and finishIndex -1
					langCombo.add(sCurrentLine.substring(sCurrentLine.indexOf('"')+1, sCurrentLine.lastIndexOf('"')));
				}
				sCurrentLine = br.readLine();
				
			}
			br.close();
			return true;
 
		} catch (IOException e) {
			return false;
		} 
		
	}
	
	/**
	 * retrieves from the settings file all the options for the second and third pages
	 * it is done only if the user uses a new Recipe
	 * @return true if the information was extracted
	 */
	public static boolean initializeOpt(){
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(settingsPath));
			String sCurrentLine;
			platforms = new ArrayList<String[]>();
			plugins = new ArrayList<String[]>();
			resources = new ArrayList<String[]>();
			
			sCurrentLine = br.readLine();
			while ((sCurrentLine != null) ) {
				if (sCurrentLine.contains("platform id")){
					platforms.add(retrieveParam(sCurrentLine,PLAT_PARAMETERS));
				}
				
				if (sCurrentLine.contains("plugin id")){
					//NOTE: substring takes all the characters between startIndex and finishIndex -1
					plugins.add(retrieveParam(sCurrentLine,PLUG_PARAMETERS));
				}
				
				if (sCurrentLine.contains("resource id")){
					//NOTE: substring takes all the characters between startIndex and finishIndex -1
					resources.add(retrieveParam(sCurrentLine,RES_PARAMETERS));
				}
				
				if (sCurrentLine.contains("priority max-value")){
					maxPriority = Integer.parseInt(sCurrentLine.substring(sCurrentLine.indexOf('"')+1, sCurrentLine.lastIndexOf('"')));				
				}
				sCurrentLine = br.readLine();
				
			}
			br.close();
			return true;
 
		} catch (IOException e) {
			return false;
		} 
	}
	
	public static int getMaxPriority(){
		return maxPriority;
	}
	
	public static List<String[]> getPlatforms(){
		return platforms;
	}
	
	public static String getPlatformIdAt(int index){
		return platforms.get(index)[0];
	}
	public static String getPlatformNameAt(int index){
		return platforms.get(index)[1];
	}
	public static int getPlatformNumber(){
		return platforms.size();
	}
	
	
	public static int getPluginNumber() {
		return plugins.size();
	}
	
	public static String getPluginIdAt(int index){
		return plugins.get(index)[0];
	}
	public static String getPluginNameAt(int index) {
		return plugins.get(index)[1];
	}
	
	public static String getPluginDescription(int index){
		return plugins.get(index)[2];
	}

	
	/**
	 * retrieves parameters from a tag string
	 * in this program the retrieved parameters are name and ID of a specific item
	 * @param tagStr <item id="item id" name="item name" >[</item>]
	 * @param paraNum number of parameters to retrieve
	 * @return an array with name(index 1) an the ID(index 0)
	 */
	public static String[] retrieveParam(String tagStr, int paramNum){
		String platformStr[] = new String[paramNum];
		for (int i=0;i<platformStr.length;i++){
			tagStr = tagStr.trim();
			//this instruction removes all the characters until to the first '"' included 
			tagStr = tagStr.substring(tagStr.indexOf('"')+1);
			//the value of the parameter is all contained before the next '"'
			platformStr[i] =tagStr.substring(0,tagStr.indexOf('"')); 
			//now all the characters until '"'(included) can be removed
			tagStr = tagStr.substring(tagStr.indexOf('"')+1);
		}
		return platformStr;
	}

	/**
	 * @param platformID the id of the platform
	 * @return the name associated to this ID
	 */
	public static String getNameWith(String platformID) {
		int index = 0;
		//it's necessary find out the object with this ID
		while(platformID.compareTo(platforms.get(index)[0]) != 0){
			index++;
		}
		return platforms.get(index)[1];
	}
	/**
	 * @param path a string where resources are separated by " " and not by "."
	 * @return true --> the inserted path contains only available resources
	 */
	public static boolean validResources(String path) {
  		//contains the resources used to generate the path
  		String[] pathComp = path.split(" ");
  		int index = 0;
  		while(index < pathComp.length){
  			int resIndex = 0;
  			boolean present = false;
  			//the selected resource is searched among the available 
  			while(resIndex<resources.size()){
  				//if it's present then it's possible to verify the next one
  				if(resources.get(resIndex)[0].compareTo(pathComp[index]) == 0){
  					present = true;
  					resIndex = resources.size();
  				}
  				resIndex++;
  			}
  			//one of the resources is wrong
  			if(!present) return false;
  			index++;
  		}
  		return true;
	}
}
