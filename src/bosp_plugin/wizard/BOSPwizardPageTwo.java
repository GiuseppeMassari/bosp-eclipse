package bosp_plugin.wizard;



import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;



public class BOSPwizardPageTwo extends WizardPage {
	//the number of maximum elements visible in the tables
	private final static int MAX_ELEMENTS = 3;
	//when the inserted number of AWM is wrong this value is put
	private final static String DEF_AWM = "1";

	//GUI components
	private Composite container;
	private Text nameText;
	private Spinner prioritySpinner;
	private Combo platformCombo,pluginCombo;
	private Table platformTable,pluginTable;
	
	
	public BOSPwizardPageTwo(ISelection selection) {
		super("wizardPage");
		setTitle("BarbequeRTRM Application");
		setDescription("Create a new recipe");
		
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		layout.verticalSpacing = 9;
		
		
		GridData gd;
		Label nameLabel,priorityLabel,platformLabel,pluginLabel;
		Button addPlatButton;
		TableColumn plCol1,plCol2;
  		Button addPlugButton;
  		TableColumn plugCol1,plugCol2;
  		
  		/*****Other******/
  		TableItem[] fakeItem = new TableItem[MAX_ELEMENTS];
		
		
		/*****Project Options*****/
		
		nameLabel = new Label(container, SWT.NULL);
		nameLabel.setText("&Name:");
		nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		
		nameText.setEditable(false);
		nameText.setEnabled(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		priorityLabel = new Label(container, SWT.NULL);
		priorityLabel.setText("&Priority:");
		prioritySpinner = new Spinner(container,SWT.WRAP);
		
		/*****Platform Options*****/
		platformLabel = new Label(container, SWT.NULL);
		platformLabel.setText("&Platform:");
		platformCombo = new Combo(container,SWT.DROP_DOWN);// Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		platformCombo.setLayoutData(gd);
		addPlatButton = new Button(container, SWT.PUSH);
		addPlatButton.setText("+");
		addPlatButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addPlatformHandler();
			}

			
		});
		/*it must be inserted a free slot at the end of this line and 
		 * at the start of the next
		 */
		adapt(2);
		
		platformTable = new Table(container,SWT.BORDER);
		plCol1 = new TableColumn(platformTable, SWT.CENTER);
	    plCol2 = new TableColumn(platformTable, SWT.CENTER);
	    plCol1.setText("ID");
	    plCol2.setText("AWM(s)");
	    plCol1.setWidth(240);
	    plCol2.setWidth(100);
	    platformTable.setHeaderVisible(true);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
	    platformTable.setLayoutData(gd);
	    for(int i=0;i< fakeItem.length;i++){
	    	fakeItem[i] = new TableItem(platformTable,SWT.NONE);
	    }
	    adapt(2);
  	    platformTable.addListener(SWT.MouseDown, new Listener() {
  	      public void handleEvent(Event event) {
  	    	  tableManager(event);
  	    	  }
  	    });

	    /*****Plugins Options*****/
  		pluginLabel = new Label(container, SWT.NULL);
  		pluginLabel.setText("&PLugin:");
  		pluginCombo = new Combo(container,SWT.DROP_DOWN);
  		gd = new GridData(GridData.FILL_HORIZONTAL);
  		pluginCombo.setLayoutData(gd);
  		addPlugButton = new Button(container, SWT.PUSH);
  		addPlugButton.setText("+");
		addPlugButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addPluginHandler();
			}
			
		});
  		/*it must be inserted a free slot at the end of this line and 
  		 * at the start of the next
  		 */
  		adapt(2);
  		
  		pluginTable = new Table(container,SWT.BORDER);
  		plugCol1 = new TableColumn(pluginTable, SWT.CENTER);
  	    plugCol2 = new TableColumn(pluginTable, SWT.CENTER);
  	    plugCol1.setText("ID");
  	    plugCol2.setText("Description");
  	    plugCol1.setWidth(100);
  	    plugCol2.setWidth(240);
  	    
  	    pluginTable.setHeaderVisible(true);
  	    gd = new GridData(GridData.FILL_HORIZONTAL);
  	    pluginTable.setLayoutData(gd);

  			    
  	    for(int i=0;i< fakeItem.length;i++){
	    	fakeItem[i] = new TableItem(pluginTable,SWT.NONE);
	    }

  	    //At start the user hasn't inserted any platform
		this.setPageComplete(false);
		setControl(container);

	}
	


	/**
	 * loads the name of the project in the second page
	 * @param projName
	 */
	public void setProjName(String projName) {
		nameText.setText(projName);
	}
	
	/**
	 * loads form settings.xml file all available platforms
	 */
	private void setPlatforms(){
		int num = SettingsLoader.getPlatformNumber();
		for(int i=0;i<num;i++)
			platformCombo.add(SettingsLoader.getPlatformNameAt(i));
		platformCombo.select(0);
	}
	
	/**
	 * loads form settings.xml file all available plugin
	 */
	private void setPlugins(){
		int num = SettingsLoader.getPluginNumber();
		for(int i=0;i<num;i++)
				pluginCombo.add(SettingsLoader.getPluginNameAt(i));
		pluginCombo.select(0);
	}
	
	/**
	 * @param maxPriority the value of priority recovered from settings.xml
	 */
	private void setMaxPriority(int maxPriority) {
		prioritySpinner.setMaximum(maxPriority);
		
	}
	
	
	/**
	 * add a new platform with relative description to the appropriate table
	 */
	private void addPlatformHandler() {
		// recover the selected platform
		platformCombo.getSelectionIndex();
		
		//it's necessary find out if it's already present this platform it the table
		TableItem[] content = platformTable.getItems();
		//in the table isn't present a platform with the same ID as the selected 
		boolean platPresent = false;
		String platId = SettingsLoader.getPlatformIdAt(platformCombo.getSelectionIndex());
		int indexEl=0; 
		while(indexEl<content.length && !platPresent){
			//the ID is in the column with index 0
			platPresent = (content[indexEl].getText(0).compareTo(platId) == 0);		
			indexEl++;		
			
		}
		//if the platform is present it's necessary to modify the AWM value.
		if(platPresent){
			TableItem item  = platformTable.getItem(indexEl - 1);
			item.setText(new String[] { platId, String.valueOf(Integer.valueOf(item.getText(1)) + 1)}); 
		}
		//else it's necessary to add it
		else{
			TableItem newItem = new TableItem(platformTable, SWT.NONE);
			newItem.setText(new String[] { platId, "1"});
		}
		// The user inserted at least one platform
		this.setPageComplete(true);
	}
	
	/**
	 * add a new plugin with relative description to the appropriate table
	 */
	private void addPluginHandler() {
		String plugDescr = SettingsLoader.getPluginDescription(pluginCombo.getSelectionIndex());
		String plugID = SettingsLoader.getPluginIdAt(pluginCombo.getSelectionIndex());
		TableItem newItem = new TableItem(pluginTable, SWT.NONE);
		newItem.setText(new String[] { plugID,plugDescr});
	}
	
	public IWizardPage getNextPage() 
	{ 
		IWizardPage nextPage = super.getNextPage();
		return nextPage; 
	}

	/**
	 * loads in this wizard page the parameters recovered from settings.xml
	 * @param maxPriority
	 */
	public void initParams(int maxPriority) {
		//now the maximum priority was extracted
		setMaxPriority(maxPriority);
		setPlatforms();
		setPlugins();
		//removing the fake items from the two table
		platformTable.removeAll();
		pluginTable.removeAll();
		
	}

	/**
	 * saves the platforms Id and the associated AWM number inserted by the user
	 * also saves the plugin ID 
	 */
	public void saveParams() {
		TableItem[] content = platformTable.getItems();
		Recipe.setPlatNum(content.length);
		for (int row = 0;row < content.length;row++){
			//saving the platform ID(index 0 in the table) and the associated AWM number(index 1)
			Recipe.savePlatform(row,content[row].getText(0),Integer.parseInt(content[row].getText(1)));
		}
		
		content = pluginTable.getItems();
		Recipe.setPlugNum(content.length);
		for (int row = 0;row < content.length;row++){
			//saving the plugin ID(index 0 in the table)
			Recipe.savePlugin(row,content[row].getText(0));
		}
		
		Recipe.setAWMNum();
		
	}
	
	/**
	 * allows to edit some fields of the table
	 * @param event
	 */
	private void tableManager(Event event) {
		final TableEditor editor = new TableEditor(platformTable);
  	    editor.horizontalAlignment = SWT.LEFT;
  	    editor.grabHorizontal = true;
  	    
        Rectangle clientArea = platformTable.getClientArea();
        //the point where the user clicked
        Point pt = new Point(event.x, event.y);
        int rowIndex = platformTable.getTopIndex();
        
        while (rowIndex < platformTable.getItemCount()) {
          boolean visible = false;
          final TableItem item = platformTable.getItem(rowIndex);
          for (int colIndex = 0; colIndex < platformTable.getColumnCount(); colIndex++) {
            Rectangle rect = item.getBounds(colIndex);
            //the cell is editable only if contains the number of AWM
            if (rect.contains(pt) && colIndex == 1) {
              final int column = colIndex;
              final Text text = new Text(platformTable, SWT.CENTER);
              Listener textListener = new Listener() {
                public void handleEvent(final Event e) {
                  switch (e.type) {
                  //the user has finished to edit the cell of the table
                  case SWT.FocusOut:
                	manageChange(column,text,item);  
                    text.dispose();
                    break;
                  //press enter  
                  case SWT.Traverse:
                    switch (e.detail) {
                    case SWT.TRAVERSE_RETURN:
                    manageChange(column,text,item);
                  //press escape 
                  case SWT.TRAVERSE_ESCAPE:
                      text.dispose();
                      e.doit = false;
                    }
                    break;
                  }
                }

			
              };
              text.addListener(SWT.FocusOut, textListener);
              text.addListener(SWT.Traverse, textListener);
              editor.setEditor(text, item, colIndex);
              text.setText(item.getText(colIndex));
              text.selectAll();
              text.setFocus();
              return;
            }
            if (!visible && rect.intersects(clientArea)) {
              visible = true;
            }
          }
          if (!visible)
            return;
          rowIndex++;
         }
        
	}
	
	/**
	 * controls that the inserted value of AWM is a number
	 * @param column 
	 * @param text 
	 * @param item 
	 */
	private void manageChange(int column,Text text, TableItem item) {
		item.setText(column,text.getText());
      	try{
      		Integer.parseInt(item.getText(column));	
        }
        catch(Exception ex){
        	item.setText(column,DEF_AWM);
        }
		
	}
	
	/**
	 * insert a number of invisible layer in the specified composite
	 * @param cont the container in which insert the layers
	 * @param spaces number of layer to insert 
	 */
	private void adapt(int spaces){
		for(int i=0;i<spaces;i++){
			//A fake label that allows to improve the GUI interface
			Label fakeLabel = new Label(container, SWT.NULL);
			fakeLabel.setText("&fake:");
			fakeLabel.setVisible(false);
		}
	}

}
