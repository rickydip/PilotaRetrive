/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PilotaRetrive;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.sun.corba.se.impl.util.Version;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author riccardo
 */
public class ConnDB {
    //attributi della classe
    private String url;
    private String user; 
    private String password;
    
    //costruttore
    public ConnDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    //costruttore di default
    public ConnDB() {
        this.url = "";
        this.user = "";
        this.password = "";
    }
    
    //metori setter e getter
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    /**
     * Test di funzionamento
     * @return 0 se funziona
     */
     public int test (){
        int flag=1;
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
       try {
            con = (Connection) DriverManager.getConnection( getUrl(), getUser(),getPassword());
            st = (Statement) con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                //se lo volessi stampare
                //System.out.println(rs.getString(1));
                flag=0;
            }

        } catch (SQLException ex) {
            System.out.println("ERRORE!!!"+ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("ERRORE!!!"+ex);
            }
        }
 return flag;      
        }//test
    
    
    
}//class
