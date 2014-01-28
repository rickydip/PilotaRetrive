/*
 * Questa classe serve per testare le funzionalità 
offerte all'interno della classe Retrive.
 */

package PilotaRetrive;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.sun.corba.se.impl.util.Version;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riccardo Di Pietro 
 */
public class PilotaRetrive {

    
    public static void main(String[] args) {
        //output finale
        Hashtable <String,String> output = new Hashtable <String,String>();
        
        //################################################## 
        //Credenziali di accesso al database
        String url = "jdbc:mysql://localhost:3306/joomla";
        String user = "root";
        String password = "root";
        //#################################################
                
       //la connessione al databse viene gestita da un oggetto creato appositamente       
       ConnDB conn= new ConnDB(url,user,password);
       
       //lancio un test interno
       int x= conn.test();
       if(x==0) System.out.println("Il test sulla connessione al db ha funzionato.");  
      
       
      //##################################
      //Le chiavi da cercare sono n    //#
      int n = 3 ;                      //#  
      //vettore delle chiavi di ricerca//#
      String[] keys = new String[n];   //#
      keys[0]="sassuolo";              //# 
      keys[1]="conte";                 //#
      keys[2]="milan";                 //#
      String op_logico="OR";//qualsiasi//#
      //#################################
      
      
      //L'algoritmo è stato strutturato per funzionare con n key
      //L'operatore logico può essere AND/OR/NOT la query viene composta dinamicamente
      
      //Retrive è l'oggetto dentro il quale si svolgono le operazioni
      Retrive b = new Retrive(conn,keys,n,op_logico);
        try {
            //il metodo che esegue il tutto
            output = b.retrive();
        } catch (SQLException ex) {
            Logger.getLogger(PilotaRetrive.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        System.out.println("[Main]Nell'hashtable ci sono: "+output.size());
        
        
        
    }    

     
}//class

