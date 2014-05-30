import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

public class ValueChartsPlus extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	protected static JFrame frame;
    static JComponent newContentPane;
    public static String datafilename;
    ConstructionView con;
    
    ArrayList<String> vc_files;
    
    String filename;
    static ValueChart chart;
    static int noOfEntries; 
    
    DefaultListModel<String> listModel;
    JList<String> lstFiles;  
    JScrollPane scrList; 
    JButton btnOpen;
    JButton btnCancel;   
    JPanel pnlButtons;
    private boolean hasSuperUser = false;

    public ValueChartsPlus(){  
        //Set up the File List
        listModel = new DefaultListModel<String>(); 
        lstFiles = new JList<String>(listModel);
        lstFiles.setEnabled(false);
        lstFiles.setVisibleRowCount(10);       
    
        scrList = new JScrollPane(lstFiles);
        lstFiles.setPreferredSize(new Dimension(200,500));
      
        scrList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setList();

        //Set up file Open/Create and Cancel command buttons 
        btnOpen = new JButton("  Open ");
        btnOpen.addActionListener(this);
        btnOpen.setActionCommand("btnOpen");
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        btnCancel.setActionCommand("btnCancel");
        
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.LINE_AXIS));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlButtons.add(btnOpen);
        pnlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlButtons.add(btnCancel);        
 
        //Layout all panels
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        scrList.setAlignmentX(LEFT_ALIGNMENT);
        add(scrList);
        if (!hasSuperUser) {
            JLabel warning = new JLabel("WARNING: SuperUser.vc file not in directory");
            warning.setForeground(Color.red);
            warning.setAlignmentX(LEFT_ALIGNMENT);
            warning.setBorder(BorderFactory.createLineBorder(Color.red));
            add(warning);
        }
        add(Box.createRigidArea(new Dimension(0,5)));
        pnlButtons.setAlignmentX(LEFT_ALIGNMENT);
        add(pnlButtons);

        setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
    }
    
    public void actionPerformed(ActionEvent e) {        
        if ("btnOpen".equals(e.getActionCommand())) {
            if (chart != null)
                chart.closeChart();
            frame.setVisible(false);
        	getList();   
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
        } else if ("btnCancel".equals(e.getActionCommand())) {
            if (chart == null)
                System.exit(0);
            else
                frame.dispose();
        }
	}
    
    // get all selected files
    void getList(){
        vc_files.clear();
        int[] idx = lstFiles.getSelectedIndices();

        // Get all the selected items using the indices
        for (int i = 0; i < idx.length; i++) {
          String file = listModel.getElementAt(idx[i]);
          vc_files.add(file);
        }
        vc_files.add("SuperUser.vc");
    }
 
    //List all the vc files
    void setList() {
        hasSuperUser = false;
        vc_files = new ArrayList<String>();
        // String[] filenames;
        File f = new File(".");
        String files[] = f.list();
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".vc")) {
                if (files[i].equals("SuperUser.vc"))
                    hasSuperUser = true;
                else
                    vc_files.add(files[i]);
            }
        }
        lstFiles.setEnabled(true);
        listModel.clear();
        // sort the list
        for (int i = 0; i < vc_files.size(); i++)
            listModel.addElement(vc_files.get(i).toString());
        int numItems = listModel.getSize();
        String[] a = new String[numItems];
        for (int i = 0; i < numItems; i++) {
            a[i] = listModel.getElementAt(i);
        }
        sortArray(a);
        lstFiles.setListData(a);
        lstFiles.revalidate();

        lstFiles.setSelectedIndex(0);
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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (chart == null) {
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);
        newContentPane = new ValueChartsPlus();
        frame.setContentPane(newContentPane);      
        frame.pack();
        frame.setVisible(true);
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


