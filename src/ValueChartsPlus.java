import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

public class ValueChartsPlus extends JPanel{
	
	private static final long serialVersionUID = 1L;
	protected static JFrame frame;
    static JComponent newContentPane;
    public static String datafilename;
    ConstructionView con;
    
    ArrayList<String> vc_files;
    ArrayList<String> csv_files;
    
    String filename;
    static ValueChart chart;
    static int noOfEntries; 

    public ValueChartsPlus(){  
    	prepLists();   
//    	chart = new ValueChart(vc_files);
    	con = new ConstructionView(ConstructionView.FROM_VC);
		con.filename = "SuperUser.vc";
		con.list = vc_files;
		con.setInit(false);
		filename = "SuperUser.vc";
		noOfEntries = countEntries(filename); 
		if (noOfEntries > 10)
			con.setDisplayType(ConstructionView.SIDE_DISPLAY);
		con.showChart(true);
		con.filename = "SuperUser.vc";	//temp holder for a new filename    
	}
 
    //List all the vc files
	void prepLists(){
		vc_files = new ArrayList<String>();
		csv_files = new ArrayList<String>();
	    //String[] filenames;
	    File f = new File(".");
	    String files[] = f.list();
	    for (int i=0; i<files.length; i++){
	    	if (files[i].endsWith(".vc"))
	    		vc_files.add(files[i]);
			else if (files[i].endsWith(".csv"))
				csv_files.add(files[i]);	    	
	    }    	
	}    
	
	//Get the names of the VC files from all the participants
	public Vector getFileNames(String name, Vector list){
    	try{
    		FileReader fr = new FileReader(name);
    		BufferedReader br = new BufferedReader(fr);
    		StreamTokenizer st = new StreamTokenizer(br);
    		st.whitespaceChars(',', ',');
    		while(st.nextToken() != StreamTokenizer.TT_EOF) {
    			if(st.ttype==StreamTokenizer.TT_WORD)
    		    	list.add(st.sval);
    		}
    		fr.close();    		
    	}catch(Exception e) {
    		System.out.println("Exception: " + e);
    	}
    	return list;
    }     
    
	//Count number of alternatives
    public int countEntries(String name){
    	int count=0;
    	try{
    		FileReader fr = new FileReader(name);
    		BufferedReader br = new BufferedReader(fr);
    		StreamTokenizer st = new StreamTokenizer(br);
    		st.whitespaceChars(',', ',');
    		while(st.nextToken() != StreamTokenizer.TT_EOF) {
    			if(st.ttype==StreamTokenizer.TT_WORD)
    				if(st.sval.equals("entry")){
    					count++;
    				}
    		}
    		fr.close();    		
    	}catch(Exception e) {
    		System.out.println("Exception: " + e);
    	}
    	return count;    	
    }
    
    public static void showStartView() {
    	//Create and set up the window.
        frame = new JFrame("ValueCharts Plus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newContentPane = new ValueChartsPlus();
        frame.setContentPane(newContentPane);      
        frame.pack();
        frame.setVisible(false);
        frame.setResizable(false);
    }
    
    //sorts an array for the listview
    void sortArray(String[] strArray)
    {
      if (strArray.length == 1)    // no need to sort one item
        return;
      Collator collator = Collator.getInstance();
      String strTemp;
      for (int i=0;i<strArray.length;i++)
      {
        for (int j=i+1;j<strArray.length;j++)
        {
          if (collator.compare(strArray[i], strArray[j]) > 0)
          {
            strTemp = strArray[i];
            strArray[i] = strArray[j];
            strArray[j] = strTemp;
          }
        }
      }
    }

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showStartView();
            }
        });
    }

	
}


