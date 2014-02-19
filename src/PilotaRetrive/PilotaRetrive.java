/*
 * Questa classe serve per testare le funzionalità 
offerte all'interno della classe Retrive.
 */

package PilotaRetrive;



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
        try {
            //output finale
            Hashtable <String,String> output = new Hashtable <String,String>();
            Hashtable <String,String> output2 = new Hashtable <String,String>();
            
            //##################################################
            //Credenziali di accesso al database
            String url = "jdbc:mysql://localhost:3306/";
            String user = "root";
            String password = "root";
            //#################################################
            
            //la connessione al databse viene gestita da un oggetto creato appositamente
            ConnDB conn= new ConnDB(url,user,password);
            
            //lancio un test interno
            int x= conn.test();
            if(x!=0) System.out.println("Il test sulla connessione al db non ha funzionato.");
            
            
            //##################################
            //Le chiavi da cercare sono n    //#
            int n = 3 ;                      //#
            //vettore delle chiavi di ricerca//#
            String[] keys = new String[n];   //#
            keys[0]="sassuolo";              //#
            keys[1]="conte";                 //#
            keys[2]="alfo";                 //#
            String op_logico="OR";//qualsiasi//#
            //#################################
            
            
            //L'algoritmo è stato strutturato per funzionare con n key
            //L'operatore logico può essere AND/OR/NOT la query viene composta dinamicamente
            
            //Retrive è l'oggetto dentro il quale si svolgono le operazioni
            Retrive b = new Retrive(conn,keys,n,op_logico);
            output = b.retrive_all();
                        
            System.out.println("[Main]Nell'hashtable ci sono: "+output.size()+" elementi.");
        } //main
        catch (SQLException ex) {
            Logger.getLogger(PilotaRetrive.class.getName()).log(Level.SEVERE, null, ex);
        }

        

    }
     

}//class
