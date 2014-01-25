/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication5;

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
 * @author riccardo
 */
public class JavaApplication5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //percorso della directory di installazione di joomla
        String pathJoomla = "http://localhost/Joomla_3.2.1-Stable-Full_Package/index.php/forum/";
        //alias della categoria del forum (lo crea di default,MEGLIO impostarlo manualmente)
        String aliasCatForum = ""; 
        //campi interni alla tabella kunena_message_text
        String id ="";
        String thread= "";
        String subject = "";
        //serve per risalire all'alias della categoria del forum
        String catId ="";
    
        String url_= pathJoomla+"/"+aliasCatForum+"/"+thread+"-"+subject+".html"+"#"+id;
    
    
    
                
        String url = "jdbc:mysql://localhost:3306/joomla";
        String user = "root";
        String password = "root";
        
       ConnDB a= new ConnDB(url,user,password);
      int x= a.test();
      if(x==0) System.out.println("Il test sulla connessione al db ha funzionato.");  
      
      //###############################
      //Le chiavi da cercare sono 3 //#
      int n = 3 ;                   //#  
      String[] keys = new String[n];//#
      keys[0]="sassuolo";           //# 
      keys[1]="conte";              //#
      keys[2]="topic";              //#
      String op_logico="OR";        //#
      //###############################
      Retrive b = new Retrive(a,keys,n,op_logico);
      
       b.retrive(); 
    
    }//main
    

    

    
    

 
   
}//class

