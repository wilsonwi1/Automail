package Auto;
/*
 * realworldretail
 * William Wilson
 * 05/11/2015
 */
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 *   Extracts excel sheet info then request report from tableau 
 *   to be saved at a local location then send email using fabooti mail 
 *   with report attached
*/
public class AutoEmail {
	static boolean fail = false;
	static String Log, logLoc, fails[][];
	static int noCC = 5, failCnt=0;	
	static Exel E;
	static String output;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	static Date date = new Date();
	/*
	 *   Main  
	*/
	public static void main(String[] args) throws FileNotFoundException, IOException{
		logLoc = Exel.Location()+ args[0]+dateFormat.format(date)+"Log.txt";
		E = new Exel(args[0]+".xlsx", noCC);
		E.readCofig();
		int R = E.getNoRows();
		System.out.println(R);
		for(int row=1;row<R;row++){		
			if(Exel.ReadNextRow(row)){												//Reading excel row	
				DelPrevRep(E.getReport());											//Delete previous report 
				ExcuteCmd(E.getTabLoginCmd());										//Tableau Login command
				ExcuteCmd(E.getTabRepReqCmd());										//Tableau report request command
				Check(E.getReport());												//Check File Created
				ExcuteCmd(E.getFabCmd());											//fabooti email command	
			}
		}	
		System.out.println("Fin");			
	}
	/*
	 *   Executes given command string
	*/
	public static void ExcuteCmd(String cmd){
		try{
			Runtime rt = Runtime.getRuntime();
			//System.out.println(cmd);
			Process proc = rt.exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			//System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
			    output = s;
			}	
			if(output.contains("e-mail(s) sent without errors")){
				System.out.println("E-mail sent Succesfully");
				fail = false;
			}
			else if(output.contains("Succeeded")){
				System.out.println("Login Succeeded");
			}
			else if(output.contains("Saved")){
				System.out.println("File Creation Succeeded");
			}
			else{
				fail = true;
				System.out.println(output);
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println("Here is the standard error of the command (if any):\n");
			    System.out.println(s);						// any errors from the attempted command			    
			}
		}
		catch(Exception e){
            e.printStackTrace();
		}
	}
	/*
	 *   Deletes previous pdf report to ensure correct report sent 
	*/	
	static void DelPrevRep(String s) {
	      boolean bool = false;
	      File f = null;
	      try{
	    	 f = new File(s);
	         bool = f.delete();	
	         System.out.println("File deleted: "+E.getParam1()+": "+bool);		            
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }
	   }
	/*
	 *   Checks if Tableau created the pdf report 
	*/	
	static void Check(String s) {
	      boolean bool = false;
	      File f = null;
	      try{
	    	 f = new File(s);
	         bool = f.exists();
	         System.out.println("File Created: "+E.getParam1()+": "+bool);		            
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }
	   }
	/*
	 *   
	*/
	static void CreateErrorLog() {
		File f = null;	            
		try{
			f = new File(logLoc);
			f.delete();	
		    f.createNewFile();
		}catch(Exception e){
	         e.printStackTrace();
	    }	    
	}
}