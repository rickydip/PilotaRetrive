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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

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
    //contiene le info estratte dalle tabelle di kunena
    private Hashtable <String,String[]> informazioni = new Hashtable <String,String[]>();
    //vettore che salva tutte le chiavi mesid/id
    ArrayList<String> vet1 = new ArrayList<String>(0);
    ArrayList<String> vet2 = new ArrayList<String>(0);

    //costruttore
    public Retrive(ConnDB connessione, String[] keys, int n_keys, String op_logico) {
        this.connessione = connessione;
        this.keys = keys;
        this.n_keys = n_keys;
        this.op_logico = op_logico;
        //this.informazioni = informazioni;
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

    
    
    

    
    
    @SuppressWarnings("empty-statement")
    public void retrive(){
    
        Connection con = null;//la connessione è unica
        
        PreparedStatement pst = null,pst1 = null;;
        ResultSet rs = null, rs1 = null;

        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        
String chiave ="";
String[] vett_com =new String[1];//ospita la prima parte di informazioni: message
String[] vett_com2=null;//ospita la seconda parte di informazioni: thread, catid,subject






        try {
            //inizializzo la connessione al db
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
                 String[] vett=new String[4];
                 for(int i=0;i<4;i++){vett[i]="";}
                 vett[0]=rs.getString(2);//message
                informazioni.put(chiave, vett);
                vet1.add(chiave);//salvo la chiave
                vet2.add(chiave);//salvo la chiave
                System.out.print(rs.getInt(1)+": ");
                System.out.println(rs.getString(2));
                        
            }//while
            rs=null;
          
//STEP 2            
            String selectFrom2 = "SELECT id ,thread,catid,subject FROM joomla.pbpfz_kunena_messages WHERE ";
            String com2 = "", where_cond2 ="",com3="",com4="",fine=";"; int flag_x=0;
            for(int i=0;i<getN_keys();i++){
                if(i==0){ com2=" subject LIKE '%"+getKeys()[i]+"%' ";}
                else{com2=getOp_logico()+" subject LIKE '%"+getKeys()[i]+"%' ";}
                 System.out.println(vet2.size());
                if(vet2.size()!=0 && flag_x!=1){
                    flag_x=1;
                for(int u=0;u<vet2.size();u++){
                com3="OR id = '"+vet2.get(u)+"'";
                com4=com4+com3;
                }//for    
                }//if
            where_cond2=where_cond2+com2;
            }//for 
            where_cond2=where_cond2+com4+fine;
            
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
                        System.out.println("####"+vett_com[0]);
                        vett_com[1]=rs.getString(2);//thread
                        //
                        String query_catId="SELECT alias FROM joomla.pbpfz_kunena_categories WHERE id ='" +rs.getString(3)+"';";
                        System.out.println(query_catId);
                        pst1 = con.prepareStatement(query_catId);
                        rs1 = pst1.executeQuery();
                        String xx ="";
                        while (rs1.next()) { xx=rs1.getString(1);}
                        //
                        
                        vett_com[2]=xx;//catid
                        vett_com[3]=rs.getString(4);//subject
                        
                        
                        //debug
                        System.out.print(chiave+":  ");
                        for(int i=0;i<4;i++){System.out.print(vett_com[i]+",  ");}
                        System.out.println(" .");
                        
                                                
                    //le aggiorno    
                     informazioni.put(chiave, vett_com);
                 vett_com = null;    
                }//if
                //se non sono già presenti informazioni
                if(!informazioni.containsKey(chiave)){
                   //le estraggo
                   System.out.println("*)Informazioni sulla chiave : "+chiave+" NON SONO PRESENTI, le completo)"); 
                   vett_com2[0]="";//message non presente
                   vett_com2[1]=rs.getString(2);//thread
                   //
                   //h.put(chiave,rs.getString(3));
                  
                  
                   vet1.add(chiave);//salvo la chiave
                   //vet2.add(chiave);//salvo la chiave
                  
                   //
                        String query_catId="SELECT alias FROM joomla.pbpfz_kunena_categories WHERE id ='" +rs.getString(3)+"';";
                        System.out.println(query_catId);
                        pst1 = con.prepareStatement(query_catId);
                        rs1 = pst1.executeQuery();
                        String xx ="";
                        while (rs1.next()) { xx=rs1.getString(1);}
                        //
                        
                        //
                        vett_com2[2]=xx;//catid
                   
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
            //int dim2 = h.size();
            int dim2 = vet1.size();
            System.out.println("Il vettore che contiene le chiavi ha elementi n°: "+dim2);
           
            
///STEP 3      costruire l'url
      
      /*
           for(int i=0; i<dim2; i++){
              System.out.println(vet.get(i));
        }//for
      */   
            
            
         String url_base ="http://localhost/Joomla_3.2.1-Stable-Full_Package/index.php/forum/";
         String[] urls = null;
         String url_finale = "";
         String alias = "";
         String thread = "";
         String subject= "";
         String subject_ok = "";
         String id = "";
         
         String[] info = null;
         for(int i=0, y=0; i<dim2; i++,y++){
              System.out.println(vet1.get(i));
              info = informazioni.get(vet1.get(i));
                                      
             // info[0] -> message
             // info[1] -> thread
             // info[2] -> catid-> alias
             // info[3] -> subject
              thread = info[1];
              alias = info[2];
              subject = info[3];
              id = vet1.get(i); //chiave del post
             
              subject_ok=trattaChar(subject);
                      
              url_finale =url_base+alias+"/"+thread+"-"+subject_ok+".html#"+id;
               System.out.println(url_finale);
              //urls[y] = url_finale;
              info=null;
            
               
               
         }//for   
        
       
                
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
            
            
            

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
    
    
public String trattaChar(String subject){
 String subject_ok = "";  String b = "",s1="";
 
 
//tolgo spazi inizio e fine
subject = subject.trim();
//la trasformo in minuscolo
subject=subject.toLowerCase();
//System.out.println(subject);
subject = subject.replace(" ", "-");
//System.out.println(subject);
subject = subject.replace("--", "-");
//System.out.println(subject);
subject = subject.replace("à", "a");
subject = subject.replace("ò", "o");
subject = subject.replace("è", "e");
subject = subject.replace("é", "e");
subject = subject.replace("ù", "u");
subject = subject.replace("ì", "i");
subject = subject.replace("ç", "c");

char a;String nuovo ="";
for(int i=0;i<subject.length();i++){
    a=subject.charAt(i);
   if ((a<48 || a>57) && (a<97 || a>122)&& a!='-') //se non è un numero o un carattere alfabetico minuscolo
       
    nuovo=nuovo;
   else{ nuovo=nuovo+a;}    
 
} //for

//System.out.println(nuovo);
nuovo = nuovo.replace("--", "-");
//System.out.println(nuovo);
return nuovo;  
}//trattaChar
    
    
    
    
}//class
