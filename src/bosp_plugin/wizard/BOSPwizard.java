package bosp_plugin.wizard;

import java.io.File;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import bosp_plugin.Activator;
import bosp_plugin.preference.BOSPPreferencePage;


public class BOSPwizard extends Wizard implements INewWizard {
	//for each different type of wizard page is created an instance
	protected BOSPwizardPageOne firstPage;
	protected BOSPwizardPageTwo secondPage;
	protected BOSPwizardPageThree thirdPage;
	
	private ISelection selection;
	//true --> the second page fields obtained in the first page were loaded
	private boolean secondOptLoaded;

	//number of third type wizard page that were created
	private int numPageLoaded;
	
	
	/**
	 * Recovers and sets the default path besides the tasks of Wizard constructors 
	 */
	public BOSPwizard() {
		super();
		setNeedsProgressMonitor(true);
		//recovering the default location that was set in preference page
		Activator.getDefault().getPreferenceStore().setDefault("PATH", BOSPPreferencePage.getDefaultPath());
		//the parameters hasn't been loaded yet
		secondOptLoaded = false;
		
	}
	
	/**
	 * Adds the defined page to the wizard.
	 */
	public void addPages() {
		firstPage = new BOSPwizardPageOne(selection);
		secondPage = new BOSPwizardPageTwo(selection);
		//at least one final page is present
		thirdPage = new BOSPwizardPageThree(selection,0);
		numPageLoaded = 1;
		//the pages are loaded to the wizard
		addPage(firstPage);
		addPage(secondPage);
		addPage(thirdPage);
	}
	
	/**
	 * adds as many third type pages as specified in the second page
	 * @param numAWM the overall number of third type pages
	 */
	public void addFinalPages(int numAWM) {
		//NOTE: one final page has been added so it's possible to start from 1
		for(;numPageLoaded<numAWM;numPageLoaded++){
			thirdPage = new BOSPwizardPageThree(selection,numPageLoaded);
			addPage(thirdPage);
		}
	}
	

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		String shellCommand,projLocation;
		
		//with a new recipe the parameters of the last third type page must be saved 
		if(Recipe.isNewRecipe()){
			thirdPage = (BOSPwizardPageThree) getContainer().getCurrentPage();
			thirdPage.saveParams();
		}
		projLocation = Recipe.getAppLocation();
		/*creates the command to execute batch file
		 * inside this file uses a set of parameters: 
		 * 1) the location where the new project will be created
		 * 2) the name of the directory that must be created(it's the name of the 
		 * project inserted by the user)
		 */
		//Windows OS
		if(System.getProperty("os.name").contains("Windows")){
			shellCommand =Activator.getDefault().getPreferenceStore()
		            .getString("PATH") + File.separator + "prova.bat " + 
			 /*1st par*/projLocation + File.separator + " " +
			 /*2nd par*/Recipe.getAppName();	
		}
		//UNIX OS
		else{
			shellCommand = Activator.getDefault().getPreferenceStore()
		            .getString("PATH") + File.separator + 
		            "barbeque" + File.separator +
		            "config" + File.separator +
		            "bbque-layapp " + 
		            /*1st par*/Recipe.getAppName() + " " + 
		            /*2nd par*/Recipe.getAppLanguage();
						
		}
		
		//executing the specified batch file
		try {
			/*only when the new project was created is possible to load id, 
			 * so it's necessary wait until the process ends
			 */
			Runtime.getRuntime().exec(shellCommand).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**Code that allows to import the created project in the package explorer**/
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					//the .project must be under the folder of created project
				    String path = Activator.getDefault().getPreferenceStore()
				            .getString("PATH") + File.separator+"contrib"+File.separator +"user" + 
				    		File.separator +Recipe.getAppName()+ "/.project";
					IPath projectDotProjectFile = new Path(path);
					IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
					
					(workspace.getRoot().getProject(Recipe.getAppName())).create(projectDescription, null);
					//opens the loaded project
					(workspace.getRoot().getProject(Recipe.getAppName())).open(null);
					
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};
		// and now get the workbench to do the work
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(runnable);
		Recipe.printRecipe();
		return true;
	}
	
	@Override
	/**
	 * it's performed every time the page is redrawn 
	 * @return true --> this is the final page of the wizard
	 */
	public boolean canFinish() {
		//loads the parameters of the second page obtained from the first
		if(getContainer().getCurrentPage() == secondPage && !secondOptLoaded){
			secondPage.setProjName(Recipe.getAppName());
			//a new recipe requires all the parameters saved in "settings.xml"
			SettingsLoader.initializeOpt();
			secondPage.initParams(SettingsLoader.getMaxPriority());
			secondOptLoaded = true;

		}
		
		if(getContainer().getCurrentPage() == firstPage)
			return firstPage.isFinalPage() && firstPage.dataComplete();
		
		if(getContainer().getCurrentPage() == secondPage)
			return false;
		
		thirdPage = (BOSPwizardPageThree) getContainer().getCurrentPage();
		if (!thirdPage.getOptLoaded()){
			/*the second page parameters must be saved only if the 
			 * current third page is the first(has ID == 0)
			 */
			if(thirdPage.getID() == 0)
				secondPage.saveParams();
			thirdPage.initParams();
			thirdPage.setOptLoaded(true);
			/*if this isn't the first third type page then it's possible to save the parameters 
			 * of previous AWM
			 * NOTE:the final page parameters aren't saved here
			 */
			if(thirdPage.getID()>0){
				((BOSPwizardPageThree)thirdPage.getPreviousPage()).saveParams();
			}
			//when the first third type page is loaded the number of AWM is known
			this.addFinalPages(Recipe.getAWMnumber());
			
		}
		
		//if all the AWM are set the wizard ends.
		return ((BOSPwizardPageThree) getContainer().getCurrentPage()).isFinal(Recipe.getAWMnumber()/*secondPage.getNumAWM()BOSPwizardPageThree.getNumAWM()*/);
		
		
	}
	
}
