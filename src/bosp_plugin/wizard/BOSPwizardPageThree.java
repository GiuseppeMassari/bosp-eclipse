package bosp_plugin.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class BOSPwizardPageThree extends WizardPage {
	//the number of maximum elements visible in the tables
	private final static int MAX_ELEMENTS = 3;
	//if the user puts a wrong value in the fields of the table default values are used
	//0 --> default resources number
	private final static int DEF_VALUES = 20;
	//indicates how many fields contains the table with path
	public final static int NUM_PATH_PARAM = 3;
	
	//GUI Components
	private Composite container;
	private int ID;
	private boolean thirdOptLoaded;
	private Text platText,idAWMText,nameAWMText,valueAWMText;
	private Table pathTable;
	
	//indicates which is the new row that isn't fully completed yet 
	private int lastRow;
	
	


	/**
	 * @param selection
	 * @param ID the ID of this third type wizard page
	 */
	public BOSPwizardPageThree(ISelection selection,int ID) {
		super("wizardPage");
		setTitle("BOSP Project");
		setDescription("Create a new BOSP Project.");
		this.ID = ID;
		this.lastRow = 0;
		thirdOptLoaded = false;
	}


	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		
		GridData gd;
		Composite composite;
		GridLayout gridLayout,groupLayout;
		Label platLabel,idAWMLabel,valueAWMLabel,nameAWMLabel;
		Group resOptionsGroup;
		TableColumn pathCol1,pathCol2,pathCol3;

		/*****Other******/
		TableItem[] fakeItem = new TableItem[MAX_ELEMENTS];
		
		/*****Platform Description*****/
		platLabel = new Label(container, SWT.NULL);
		platLabel.setText("&Platform:");
		platText = new Text(container, SWT.BORDER | SWT.SINGLE);
		platText.setEditable(false);
		platText.setEnabled(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		platText.setLayoutData(gd);
		/*****Platform Description END*****/
		
		
		/*****AWM Options*****/
		idAWMLabel = new Label(container, SWT.NULL);
		idAWMLabel.setText("&ID:");
	    composite = new Composite(container, SWT.NONE);
	    gridLayout = new GridLayout();
	    gridLayout.numColumns = 5;
	    composite.setLayout(gridLayout);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
	    composite.setLayoutData(gd);
		
	    idAWMText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		idAWMText.setEditable(false);
		idAWMText.setEnabled(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		idAWMText.setLayoutData(gd);
		
		valueAWMLabel = new Label(composite, SWT.NULL);
		valueAWMLabel.setText("&Value:");
		valueAWMText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		valueAWMText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		nameAWMLabel = new Label(composite, SWT.NULL);
		nameAWMLabel.setText("&Name:");
		nameAWMText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameAWMText.setLayoutData(gd);
		nameAWMText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		/*****AWM Options*****/
		
		/***** Resources Options*****/
		adapt(container,1);
		groupLayout = new GridLayout();
		resOptionsGroup = new Group(container, SWT.SHADOW_ETCHED_IN);		
		groupLayout.numColumns = 3;
		groupLayout.verticalSpacing = 9;
		resOptionsGroup.setLayout(groupLayout);
		resOptionsGroup.setText("Resources");

		//A fake label that allows to improve the GUI interface
		Label fakeLabel = new Label(resOptionsGroup, SWT.NULL);
		fakeLabel.setText("&fake:");
		fakeLabel.setVisible(false);
		fakeLabel.pack();
  		
  		pathTable = new Table(resOptionsGroup,SWT.BORDER);
  		pathCol1 = new TableColumn(pathTable, SWT.CENTER);
  		pathCol2 = new TableColumn(pathTable, SWT.CENTER);
  		pathCol3 = new TableColumn(pathTable, SWT.CENTER);
  		pathCol1.setText("Path");
  		pathCol2.setText("Request");
  		pathCol3.setText("Units");
  		pathCol1.setWidth(150);
  		pathCol2.setWidth(75);
  		pathCol3.setWidth(75);
  		pathTable.setHeaderVisible(true);
	    
  		for(int i=0;i< fakeItem.length;i++){
	    	fakeItem[i] = new TableItem(pathTable,SWT.NONE);
	    }
  		pathTable.addListener(SWT.MouseDown, new Listener() {
  		public void handleEvent(Event event) {
  			tableManager(event);
  	      }
  	    });
  		pathTable.pack();
  		
  		//A fake label that allows to improve the GUI interface
  		fakeLabel = new Label(resOptionsGroup, SWT.NULL);
  		fakeLabel.setText("&fake:");
  		fakeLabel.setVisible(false);
  		fakeLabel.pack();
		/***** Resources Options END*****/	    
		dialogChanged();
		setControl(container);


	}
	
	public boolean getOptLoaded() {
		return thirdOptLoaded;
	}

	public void setOptLoaded(boolean optLoaded) {
		this.thirdOptLoaded = optLoaded;
	}
	
	public int getID() {
		return Integer.valueOf(ID);
	}

	/**
	 * Ensures the obligatory field are complete
	 */
	private void dialogChanged() {
		try{
			if (valueAWMText.getText().length() != 0)
				//the AWM Value must be an integer
				Integer.valueOf(valueAWMText.getText());
		}
		catch(Exception ex){
			valueAWMText.setText("");
		}
		this.setPageComplete(dataComplete());
	}
	
	/**
	 * @return true --> all the obligatory field are complete
	 */
	private boolean dataComplete() {
		setErrorMessage(null);	
		if (nameAWMText.getText().length() == 0) {
			setErrorMessage("Name must be specified"); 
			return false;
		}
		if (valueAWMText.getText().length() == 0) {
			setErrorMessage(null);
			setErrorMessage("Value must be specified");
			return false;
		}
		//the page is complete only if both fields are filled
		return(!(valueAWMText.getText().length() == 0 || nameAWMText.getText().length() == 0));
	}
	
	public boolean isFinal(int numAWM) {
		//only the last third type page is final
		if (ID == numAWM-1 && dataComplete())
			return true;
		return false;
	}
	
	/**
	 * loads in this wizard page the parameters set in the second wizard page
	 */
	public void initParams(){
		String platName = Recipe.getPlatNameWith(ID);
		platText.setText(platName);
		idAWMText.setText(String.valueOf(ID));
		pathTable.removeAll();
		//the table is editable if at least one row is present
		@SuppressWarnings("unused")
		TableItem fakeItem = new TableItem(pathTable,SWT.NONE);
		
	}
	
	/**
	 * allows to edit the rows of the table
	 * @param event
	 */
	private void tableManager(Event event) {
  		final TableEditor editor = new TableEditor(pathTable);
  	    editor.horizontalAlignment = SWT.LEFT;
  	    editor.grabHorizontal = true;
  	    
		Rectangle clientArea = pathTable.getClientArea();
        Point pt = new Point(event.x, event.y);
        int rowIndex = pathTable.getTopIndex();
        while (rowIndex < pathTable.getItemCount()) {
          boolean visible = false;
          final TableItem item = pathTable.getItem(rowIndex);
          for (int colIndex = 0; colIndex < pathTable.getColumnCount(); colIndex++) {
            Rectangle rect = item.getBounds(colIndex);
            if (rect.contains(pt)) {
              final int column = colIndex;
              final int row = rowIndex;
              final Text text = new Text(pathTable, SWT.CENTER);
              Listener textListener = new Listener() {
                public void handleEvent(final Event e) {
                  switch (e.type) {
                  //the user has finished to edit the cell of the table
                  case SWT.FocusOut:
                	manageChange(row,column,text,item);  
                    text.dispose();
                    break;
                  case SWT.Traverse:
                    switch (e.detail) {
                    case SWT.TRAVERSE_RETURN:
                  manageChange(row,column,text,item);
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
	 * controls if the inserted values respects the rules of the table
	 * @param row
	 * @param column
	 * @param text
	 * @param item
	 */
	private void manageChange(int row,int column, Text text, TableItem item) {
		item.setText(column,text.getText());
        //check if the number of resources or units is right are correct
      	if(column>0){
      		try{
      			Integer.parseInt(item.getText(column));	
            }
            catch(Exception ex){
            	//the resource value has an incorrect value
            	if(column == 1){
            		item.setText(column,String.valueOf(DEF_VALUES));
            	}
            	//the units field must be valid
            	else{
            		if(!(text.getText().startsWith("K") ||text.getText().startsWith("M") || text.getText().startsWith("G")))
            			item.setText(column,"");
            	}
            }
      	}
      	//in the first column must be a valid path
      	else{
      		if(!SettingsLoader.validResources(item.getText(0).replace('.', ' '))){
      			item.setText(column,"");
      		}
      	}
          
          
          //only if the row is completely filled is possible to insert another one
          if(!(item.getText(0).isEmpty() || item.getText(1).isEmpty() || item.getText(2).isEmpty()) && lastRow == row){
        	@SuppressWarnings("unused")
        	TableItem newItem = new TableItem(pathTable,SWT.CENTER);
          	lastRow++;
          }
		
	}

	public void saveParams() {
		//saving the value and the name of this AWM
		Recipe.setParams(this.ID,Integer.valueOf(valueAWMText.getText()), nameAWMText.getText());
		Recipe.setPaths(pathTable.getItems());
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

	
}
