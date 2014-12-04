package bosp_plugin.wizard;

import java.io.File;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bosp_plugin.Activator;

public class BOSPwizardPageOne extends WizardPage {

	//GUI components
	private Composite container;
	private Text nameText,locText;
	private Combo langCombo;
	private Button recipeButton,apiButton,browseButton;
	
	//PROJECT ATTRIBUTES
	//is the location set by the system
	private String defaultLocation;
	
	
	protected BOSPwizardPageOne(ISelection selection) {
		super("wizardPage");
		setTitle("BarbequeRTRM Application");
		setDescription("Create a new application.");
		//the default location is obtained from the Preferences/BOSP Preference
	    defaultLocation = Activator.getDefault().getPreferenceStore()
	            .getString("PATH") + File.separator + "contrib" + File.separator 
	            + "user" ;
		
	}	
	
	@Override
	public void createControl(Composite parent) {
		
		GridLayout mainLayout, secLayout;
		Composite composite;
		GridData gd;
		Label nameLabel,locLabel,langLabel;
		Button defLocButton;
		Group advOptionsGroup;
		
		
		container = new Composite(parent, SWT.NONE);
		mainLayout = new GridLayout();
		container.setLayout(mainLayout);
		mainLayout.numColumns = 2;
		mainLayout.verticalSpacing = 9;
		
		/*****Application Options*****/
		nameLabel = new Label(container, SWT.NULL);
		nameLabel.setText("&Name:");
		nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		
		defLocButton = new Button(container, SWT.CHECK);
		defLocButton.setText("Use Default Location");
		defLocButton.setSelection(true);
		defLocButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				manageLocationListener(((Button)e.getSource()));
			}

		});
		//in this row is not present a second element
		adapt(container,1);
		
		
		locLabel = new Label(container, SWT.NULL);
		locLabel.setText("&Location:");

		composite = new Composite(container, SWT.NONE);
	    secLayout = new GridLayout();
	    secLayout.numColumns = 2;
	    gd = new GridData(GridData.FILL_HORIZONTAL);
	    composite.setLayout(secLayout);
	    composite.setLayoutData(gd);
	    
  		locText = new Text(composite, SWT.BORDER | SWT.SINGLE);
  		locText.setEditable(false);
  		gd = new GridData(GridData.FILL_HORIZONTAL);
  		locText.setLayoutData(gd);
	    
  		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
		    @Override

		    public void widgetSelected(SelectionEvent e) {
		    	selectLocation(e);
		    }
		});
		
		
		/***** Programming language Options*****/ 
		langLabel = new Label(container, SWT.NULL);
		langLabel.setText("&Language:");
		langCombo = new Combo(container,SWT.DROP_DOWN );
		gd = new GridData(GridData.FILL_HORIZONTAL);
		langCombo.setLayoutData(gd);
		//the options for this field are extracted from settings.xml file
		SettingsLoader.getLanguages(langCombo);
		langCombo.select(0);
		
		/***** Advanced Options*****/
		GridLayout groupLayout = new GridLayout();
		advOptionsGroup = new Group(container, SWT.SHADOW_ETCHED_IN);		
		groupLayout.numColumns = 1;
		groupLayout.verticalSpacing = 9;
		advOptionsGroup.setLayout(groupLayout);
		advOptionsGroup.setText("Advanced Options");
		advOptionsGroup.setLayoutData(new GridData(GridData.FILL));
		
		
		apiButton = new Button(advOptionsGroup, SWT.CHECK);
		apiButton.setText("Use Monitor Library API");
		apiButton.pack();
		Recipe.setUseLibrary(false);
		apiButton.addSelectionListener(new SelectionAdapter() {
		    @Override

		    public void widgetSelected(SelectionEvent e) {
     
		    		apiButtonPushed();

		        }


		});
		recipeButton = new Button(advOptionsGroup, SWT.CHECK);
		recipeButton.setText("Create a new Recipe");
		recipeButton.addSelectionListener(new SelectionAdapter() {
		    @Override

		    public void widgetSelected(SelectionEvent e) {
     
		    		dialogChanged();

		        }

		});
		

		recipeButton.pack();
		advOptionsGroup.pack();
		
		//the default location check is on, the obligatory field may be set
		manageLocationListener(defLocButton);
		//check the state of the page
		dialogChanged();
		setControl(container);

	}
	
	/**
	 * set the location value depending on user wish
	 * @param source the button that was pushed 
	 */
	private void manageLocationListener(Button source) {
		
		//the default location is choose
		if(source.getSelection()){
			locText.setText(defaultLocation);
			locText.setEnabled(false);
			browseButton.setEnabled(false);
			
		}
		//the path is set by the user
		else{
			locText.setEnabled(true);
			browseButton.setEnabled(true);
		}

	}
	
	/**
	 * verifies the state of the page after the user perform one of this action 
	 * -changed the projName 
	 * -select/disabled newRecipe option 
	 */
	private void dialogChanged() {
		//page complete -> it's possible to forward to next page
		this.setPageComplete(dataComplete() && recipeButton.getSelection());
		Recipe.setNewRecipe(recipeButton.getSelection());
	}
	
	private void apiButtonPushed() {
		Recipe.setUseLibrary(apiButton.getSelection());
	}
	
	public IWizardPage getNextPage() 
	{ 
	IWizardPage nextPage = super.getNextPage(); 
	return nextPage; 
	}
	
	
	/**
	 * @return true --> this page has finish button active
	 */
	public boolean isFinalPage(){
		return !(recipeButton.getSelection());
	}

	/**
	 * @return true--> the user insert all the data needed 
	 */
	public boolean dataComplete() {
		setErrorMessage(null);
		//the name of the project must be specified and not contain spaces
		if (nameText.getText().length() == 0 || nameText.getText().indexOf(' ') != -1) {
			setErrorMessage(null);
			setErrorMessage("Project Name must be specified (NO SPACES)");
			return false;
		}
		else{
			String camelCase;
			String firstChar;
			firstChar  = String.valueOf(nameText.getText().charAt(0));
			if(!(firstChar.toUpperCase().compareTo(firstChar) == 0)){
				camelCase = nameText.getText().replaceFirst(firstChar, firstChar.toUpperCase());
				nameText.setText(camelCase);
				//the edit pointer is moved after the first char 
				nameText.setSelection(1);
			}
		}
		saveAll();
		return true;
	}

	
	
	/**
	 * insert a number of invisible layer in the specified composite
	 * @param cont the container in which insert the layers
	 * @param spaces number of layer to insert 
	 */
	private void adapt(Object cont,int spaces){
		for(int i=0;i<spaces;i++){
			//A fake label that allows to improve the GUI interface
			Label fakeLabel = new Label((Composite) cont, SWT.NULL);
			fakeLabel.setText("&fake:");
			fakeLabel.setVisible(false);
		}
	}
	
	private void selectLocation(SelectionEvent e) {
		DirectoryDialog directoryDialog = new DirectoryDialog(container.getShell());
		directoryDialog.setMessage("Please select a directory and click OK");
		//the start directory is the default, then user can chose an another
		directoryDialog.setFilterPath(locText.getText() + File.separator);
		
		String dir = directoryDialog.open();
		if(dir != null) {
			locText.setText(dir);
		}
		saveAll();	
	}
	
	private void saveAll() {
		Recipe.setAppName(nameText.getText());
		//The saved location mustn't contain spaces at the end.
		Recipe.setAppLocation(locText.getText().trim());
		Recipe.setAppLanguage(langCombo.getItem(langCombo.getSelectionIndex()));
	}

}
