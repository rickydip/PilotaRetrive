/*
 * Questa classe ricava le informazioni presenti sulle tabelle:
  1)pbpfz_kunena_messages;
  2)pbpfz_kunena_messages_text;
  3)pbpfz_kunena_topic;

Date n chiavi di ricerca, questa classe consente di risalire a tutti i contenuti
del forum dove sono presenti le chiavi. Di questi contenuti, si ricostruisce il 
link url da cui è possibile accedere a talii contenuti e restituisce una struttura
Hashtable contenente <url,contenuto matchato>



 */

package PilotaRetrive;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riccardo Di Pietro
 */
public class Retrive {
    
    //oggetto che gestisce la connessione al db
    private ConnDB connessione;
    //vettore che contiene le chiavi da cercare
    private String[] keys;
    //dimensione fisica del vettore keys
    private int n_keys;
    //operatore logico da usare nella query di selezione
    //AND/OR/NOT
    private String op_logico;
    //contiene le info estratte dalle tabelle di kunena
    private Hashtable <String,String[]> informazioni = new Hashtable <String,String[]>();
    //vettori di comodo che salvano le chiavi mesid/id
    ArrayList<String> vet1 = new ArrayList<String>(0);
    ArrayList<String> vet2 = new ArrayList<String>(0);

    
    
    /**
     * Costruttore
     * @param connessione oggetto con i dati della connessione
     * @param keys vettore delle chiavi
     * @param n_keys dim del vettore delle chiavi
     * @param op_logico operatore logico da mettere nella query
     */
    public Retrive(ConnDB connessione, String[] keys, int n_keys, String op_logico) {
        this.connessione = connessione;
        this.keys = keys;
        this.n_keys = n_keys;
        this.op_logico = op_logico;
        //this.informazioni = informazioni;
    }
    
    /**
     * costruttore di default
     */
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

    
    
    

    
    
    
public Hashtable retrive() throws SQLException{
        
    Hashtable <String,String> output = new Hashtable <String,String>();
    
   
            Connection con = null;//la connessione è unica
            PreparedStatement pst = null,pst1 = null;
            
            ResultSet rs = null, rs1 = null;
            PreparedStatement pst2 = null;
            ResultSet rs2 = null;
            String chiave ="";
            String[] vett_com = new String[1];//ospita la prima parte di informazioni: message
            String[] vett_com2 = new String[4];//ospita la seconda parte di informazioni: thread, catid,subject
            // try {
            try {
                //inizializzo la connessione al db
                con = (Connection) DriverManager.getConnection(getConnessione().getUrl(),getConnessione().getUser(),getConnessione().getPassword());
            } catch (SQLException ex) {
                Logger.getLogger(Retrive.class.getName()).log(Level.SEVERE, null, ex);
            }
//STEP 1    interrogo la tabella  kunena_messages_text
            String selectFrom1 = "SELECT * FROM joomla.pbpfz_kunena_messages_text WHERE ";
            String com1 = "", where_cond1 ="";
            for(int i=0;i<getN_keys();i++){
                if(i==0){ com1=" message LIKE '%"+getKeys()[i]+"%' ";}
                else{com1=getOp_logico()+" message LIKE '%"+getKeys()[i]+"%' ";}
                where_cond1=where_cond1+com1;
            }//for
            where_cond1=where_cond1+";";
            //System.out.println(where_cond);
            //query completa
            selectFrom1 = selectFrom1 +where_cond1;
            System.out.println("#############################################");
            System.out.println(selectFrom1);
            System.out.println("#############################################\n");
            pst = con.prepareStatement(selectFrom1);
            rs = pst.executeQuery();
            while (rs.next()) {
                //setto la chiave (mesid) e il message
                chiave = Integer.toString(rs.getInt(1));
                
                // posizione 0 metto il "message" 
                String[] vett=new String[4];
                // lo inizializzo
                for(int i=0;i<4;i++){vett[i]="";}
                
                // posizione 0 metto il "message"
                vett[0]=rs.getString(2);//message
                
                //inserisco le informazioni nell'hashtable
                informazioni.put(chiave, vett);
                
                vet1.add(chiave);//salvo la chiave 
                vet2.add(chiave);//salvo la chiave
                
                //debug
                System.out.print(rs.getInt(1)+": ");
                System.out.println(rs.getString(2));
                //
                
            }//while
            rs=null;//pulisco 
            pst=null;//pulisco
//STEP 2   interrogo la tabella  kunena_messages
            //var di comodo
            String selectFrom2 = "SELECT id ,thread,catid,subject FROM joomla.pbpfz_kunena_messages WHERE ";
            String com2 = "", where_cond2 ="",com3="",com4="",fine=";";
            int flag_x=0;
            //costruisco dinamicamente la query
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
            //query finale
            selectFrom2 = selectFrom2 +where_cond2;
            System.out.println("#############################################");
            System.out.println(selectFrom2);
            System.out.println("#############################################\n");
            pst = con.prepareStatement(selectFrom2);
            rs = pst.executeQuery();
            while (rs.next()) {
                chiave = Integer.toString(rs.getInt(1));
                //se sono già presenti informazioni con quella chiave                
                if(informazioni.containsKey(chiave)){
                    System.out.println("*)Informazioni sulla chiave : "+chiave+" SONO PRESENTI, le completo)");
                    //le estraggo
                    vett_com =(String[])informazioni.get(chiave);
                    //ne aggiungo altre associate alla chiave
                    System.out.println("####"+vett_com[0]);//era già piena dallo step 1
                    vett_com[1]=rs.getString(2);//thread
                    //
                    String query_catId="SELECT alias FROM joomla.pbpfz_kunena_categories WHERE id ='" +rs.getString(3)+"';";
                    System.out.println(query_catId);
                    pst1 = con.prepareStatement(query_catId);
                    rs1 = pst1.executeQuery();
                    String xx ="";
                    while (rs1.next()) { xx=rs1.getString(1);}
                    
                    vett_com[2]=xx;//catid
                    vett_com[3]=rs.getString(4);//subject
                    
                    //debug
                    System.out.print(chiave+":  ");
                    for(int i=0;i<4;i++){System.out.print(vett_com[i]+",  ");}
                    System.out.println(" .");
                    
                    
                    //inserisco le informazioni nell'hashtable
                    informazioni.put(chiave, vett_com);
                    vett_com = null;    
                }//if
                
                //se non sono già presenti informazioni con quella chiave
                if(!informazioni.containsKey(chiave)){
                    //le estraggo
                    System.out.println("*)Informazioni sulla chiave : "+chiave+" NON SONO PRESENTI, le completo)");
                    vett_com2[0]="";//message non presente, non caricato nello step 1
                    vett_com2[1]=rs.getString(2);//thread
                    
                    vet1.add(chiave);//salvo la chiave
                    //vet2.add(chiave);//salvo la chiave
                    
                    String query_catId="SELECT alias FROM joomla.pbpfz_kunena_categories WHERE id ='" +rs.getString(3)+"';";
                    System.out.println(query_catId);
                    pst1 = con.prepareStatement(query_catId);
                    rs1 = pst1.executeQuery();
                    String xx ="";
                    while (rs1.next()) { xx=rs1.getString(1);}
                    
                    vett_com2[2]=xx;//catid
                    vett_com2[3]=rs.getString(4);//subject
                    
                    //inserisco le informazioni nell'hashtable
                    informazioni.put(chiave, vett_com2);
                    
                    //stampo outpu di debug
                    for(int i=0;i<4;i++) System.out.println(vett_com2[i]);
                    
                }//if
            }//while
            //ADESSO ho tutte le info sull'hashtable
            int dim = informazioni.size();
            System.out.println("L'hashtable contiene n°: "+dim+" elementi.");
            //int dim2 = h.size();
            int dim2 = vet1.size();
            System.out.println("Sono presenti n°: "+dim2+" chiavi diverse");
///STEP 3   Creazione dell'url per risalire alle informazioni presenti nell'hashtable
            /*
            for(int i=0; i<dim2; i++){
            System.out.println(vet.get(i));
            }//for
             */
            //var di comodo
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
                
                output.put(url_finale,info[0]);
                
                info=null;
                
            }//for   
        
        return output;

}    

/**
 * Questa funzione si occupa di effettuare un 
 * @param subject
 * @return 
 */
public String trattaChar(String subject){
    
 //var di comodo   
 String subject_ok = "";  String b = "",s1="";
 
 
//tolgo spazi inizio e fine
subject = subject.trim();
//la trasformo in minuscolo
subject=subject.toLowerCase();
subject = subject.replace(" ", "-");
subject = subject.replace("--", "-");
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
    //se non è un numero o un carattere alfabetico minuscolo
   
    if ((a<48 || a>57) && (a<97 || a>122)&& a!='-') 
       
    nuovo=nuovo;
   else{ nuovo=nuovo+a;}    
} //for

//System.out.println(nuovo);
nuovo = nuovo.replace("--", "-");
//System.out.println(nuovo);
return nuovo;  
}//trattaChar
    
    
    
    
}//class
