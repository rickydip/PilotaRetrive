/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication5;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 *
 * @author riccardo
 */
public class Retrive {
    
    //oggetto che gestisce la connessione al db
    private ConnDB connessione;
    //vettore che contiene le chiavi da cercare
    private String[] keys;
    //dimensione fisica del vettore keys
    private int n_keys;
    //operatore logico da usare nella query di selezione
    //AND oppure OR
    private String op_logico;
    //rappresenta la tabella kunena_message_text
    private Hashtable <String,String[]> informazioni = new Hashtable <String,String[]>();

    //costruttore
    public Retrive(ConnDB connessione, String[] keys, int n_keys, String op_logico) {
        this.connessione = connessione;
        this.keys = keys;
        this.n_keys = n_keys;
        this.op_logico = op_logico;
        this.informazioni = informazioni;
    }
    
    //costruttore di default
    public Retrive() {
        this.connessione = null;
        this.keys = null;
        this.n_keys = 0;
        this.op_logico = "";
    }

//metodi setter e getter
    public ConnDB getConnessione() {
        return connessione;
    }

    public void setConnessione(ConnDB connessione) {
        this.connessione = connessione;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public int getN_keys() {
        return n_keys;
    }

    public void setN_keys(int n_keys) {
        this.n_keys = n_keys;
    }

    public String getOp_logico() {
        return op_logico;
    }

    public void setOp_logico(String op_logico) {
        this.op_logico = op_logico;
    }

    
    
    

    
    
public void retrive(){
    
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        
String chiave ="";
String[] vett_com =new String[1];//ospita la prima parte di informazioni: message
String[] vett_com2=null;//ospita la seconda parte di informazioni: thread, catid,subject

String[] vett=new String[4];
for(int i=0;i<4;i++){vett[i]="";}
//<String,String[]>
//<id/mesid, [message,thread,catid,subject]>



        try {
            
            con = (Connection) DriverManager.getConnection(getConnessione().getUrl(),getConnessione().getUser(),getConnessione().getPassword());
            
           
            
 
//STEP 1            
            String selectFrom1 = "SELECT * FROM joomla.pbpfz_kunena_messages_text WHERE ";
            String com1 = "", where_cond1 ="";
            for(int i=0;i<getN_keys();i++){
                if(i==0){ com1=" message LIKE '%"+getKeys()[i]+"%' ";}
                else{com1=getOp_logico()+" message LIKE '%"+getKeys()[i]+"%' ";}
            where_cond1=where_cond1+com1;
            }//for 
            where_cond1=where_cond1+";";
            
            //System.out.println(where_cond);
                    
            selectFrom1 = selectFrom1 +where_cond1;
            
            System.out.println(selectFrom1);
                     
            
            pst = con.prepareStatement(selectFrom1);
            
            rs = pst.executeQuery();
                     
            
            while (rs.next()) {
                //setto la chiave (mesid) e il message
                 chiave = Integer.toString(rs.getInt(1));
                 vett[0]=rs.getString(2);//message
                informazioni.put(chiave, vett);
                System.out.print(rs.getInt(1)+": ");
                System.out.println(rs.getString(2));
            }//while
            
          
//STEP 2            
            String selectFrom2 = "SELECT id ,thread,catid,subject FROM joomla.pbpfz_kunena_messages WHERE ";
            String com2 = "", where_cond2 ="";
            for(int i=0;i<getN_keys();i++){
                if(i==0){ com2=" subject LIKE '%"+getKeys()[i]+"%' ";}
                else{com2=getOp_logico()+" subject LIKE '%"+getKeys()[i]+"%' ";}
            where_cond2=where_cond2+com2;
            }//for 
            where_cond2=where_cond2+";";
            
            //System.out.println(where_cond2);
                    
            selectFrom2 = selectFrom2 +where_cond2;
            
            System.out.println(selectFrom2);
                     
            
            pst = con.prepareStatement(selectFrom2);
            
            rs = pst.executeQuery();
                     
            
            while (rs.next()) {
                chiave = Integer.toString(rs.getInt(1));
                //se sono già presenti informazioni                
                if(informazioni.containsKey(chiave)){
                    System.out.println("*)Informazioni sulla chiave : "+chiave+" SONO PRESENTI, le completo)");
                    //le estraggo
                    vett_com =(String[])informazioni.get(chiave);
                     //ne aggiungo altre associate alla chiave
                        vett_com[1]=rs.getString(2);//thread
                        vett_com[2]=rs.getString(3);//catid
                        vett_com[3]=rs.getString(4);//subject
                        
                        //debug
                        System.out.print(chiave+":  ");
                        for(int i=0;i<4;i++){System.out.print(vett_com[i]+",  ");}
                        System.out.println(" .");
                        
                                                
                    //le aggiorno    
                     informazioni.put(chiave, vett_com);
                }//if
                //se non sono già presenti informazioni
                if(!informazioni.containsKey(chiave)){
                   //le estraggo
                   System.out.println("*)Informazioni sulla chiave : "+chiave+" NON SONO PRESENTI, le completo)"); 
                   vett_com2[0]="";//message non presente
                   vett_com2[1]=rs.getString(2);//thread
                   vett_com2[2]=rs.getString(3);//catid
                   vett_com2[3]=rs.getString(4);//subject
                   //le inserisco
                   informazioni.put(chiave, vett_com2);
                   
                   //debug
                   for(int i=0;i<4;i++) System.out.println(vett_com[i]);
                   
                }//if
                
                
                /*
                System.out.print(": "+rs.getInt(1)
                +": "+rs.getString(2)
                +": "+rs.getString(3)
                +": "+rs.getString(4)+"\n");
                */        
                        
            }//while
/////////////////////////////         
            
            
            //ADESSO ho tutte le info sull'hastable
            
            
            int dim = informazioni.size();
            System.out.println("Per la ricerca delle key all'interno dei db, ho trovato occorrenze n°: "+dim);
            
            
            
            
            

        } catch (SQLException ex) {
                System.out.println("ERRORE!!!"+ex);

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("ERRORE!!!"+ex);
            }
        }
    }//retrive    
    
    
    
    
    
    
}//class
