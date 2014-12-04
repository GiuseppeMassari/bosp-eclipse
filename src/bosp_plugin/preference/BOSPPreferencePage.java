package bosp_plugin.preference;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bosp_plugin.Activator;


public class BOSPPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	//the default location of the sources
	private static String defaultPath = System.getProperty("user.home") + 
										File.separator + "BOSP"; 

	
	/** 
	 * @return the default location of the sources project
	 */
	public static String getDefaultPath(){
		return defaultPath;
	}
	
	public BOSPPreferencePage() {
		super(GRID);
		/*****Source File Default Location*****/ 
		Activator.getDefault().getPreferenceStore().setDefault("PATH", defaultPath);
	}
	

	/**
	 * defines and adds all GUI components
	 */
	public void createFieldEditors() {
		DirectoryFieldEditor pathField = new DirectoryFieldEditor("PATH", "&Directory preference:",
		        getFieldEditorParent());
		addField(pathField);  
	}

	  @Override
	public void init(IWorkbench workbench) {
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Source File Path");
	}

}
