/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmongo;

import com.mongodb.*;
import java.awt.Color;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import jmongo.gui.MongoGUI;
import jmongo.gui.SplashWindow;
import jmongo.gui.ThanksDlg;

/**
 *
 * @author Administrator
 */
public class MainController {

    private static MainController m_instance = null;
    private MongoGUI m_AppFrame = null;
    
    private MainController() {
    }
    
    public static MainController getInstance() {
        if (m_instance == null) {
            m_instance = new MainController();
        }
        
        return m_instance;
    }
    
//    public void showMainFrame() {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                m_AppFrame = new MongoGUI();
//                m_AppFrame.setVisible(true);
//            }
//        });
//    }
    
    public void showDBConect(DBConContoroller dbcon) {
        m_AppFrame.addFrame(dbcon.getGUI());
    }
    
    public void showGFSDBConect(DBConContoroller dbcon) {
        m_AppFrame.addFrame(dbcon.getGridFSGUI());
    }
    
    public void showThanksDlg() {
        new ThanksDlg(m_AppFrame).setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        createAndShowGUI();
        
    }
    
    public static void createAndShowGUI() {
        final SplashWindow splashScreen = new SplashWindow();
        splashScreen.setVisible(true);
        
        new SwingWorker() {

            @Override
            public Object doInBackground() {
                MainController.getInstance().m_AppFrame = new MongoGUI();
                
                while(splashScreen.isAnimationEnd()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                MainController.getInstance().m_AppFrame.setVisible(true);
                return "Done";
            }

            @Override
            public void done() {
                splashScreen.dispose();
            }
        }.execute();
    }

//*********************<<TEST>>************************
    public static void test() {
        // TODO code application logic here

        System.out.println("unko!");
        
        try {
            Mongo m = new Mongo();
            DB db = m.getDB("mydb");

            //コレクション名のリスト
            Set<String> colls = db.getCollectionNames();
            for (String s : colls) {
                System.out.println(s);
            }

            //コレクションに接続
            DBCollection coll = db.getCollection("testCollection");
            DBCursor cur = coll.find();
            while (cur.hasNext()) {
                
                DBObject obj = cur.next();
                Map map = obj.toMap();
                
                System.out.println(obj);
                for (Object o : map.keySet().toArray()) {
                    System.out.println("→" + o + ":" + map.get(o) + "[" + map.get(o).getClass().getName() + "]");
                }
            }
            System.out.println(coll.find().count());
        } catch (UnknownHostException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void printDBObject(DBObject obj) {
        printDBObject(obj, 0);
    }

    private static void printDBObject(DBObject obj, int cnt) {
        Map map = obj.toMap();
        for (Object o : map.keySet().toArray()) {
            Object value = map.get(o);
            
            for (int i = 0; i < cnt; i++) {
                System.out.print("\t");
            }
            System.out.println("\t→" + o + ":" + value + "[" + value.getClass().getName() + "]");
            
            if (value instanceof DBObject) {
                printDBObject((DBObject) value, cnt + 1);
            }
        }
    }
}
