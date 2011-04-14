/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.personal.jmongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import gui.MongoGUI;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Administrator
 */
public class MainController {
    private static MainController m_instance =null;
    private MongoGUI m_AppFrame = null;

    private MainController(){
    }

    public static MainController getInstance(){
        if(m_instance == null){
            m_instance = new MainController();
        }

        return m_instance;
    }

    public void showMainFrame(){
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run(){
                m_AppFrame = new MongoGUI();
                m_AppFrame.setVisible(true);
            }
        });
    }

    public void showDBConect(DBConContoroller dbcon){
        m_AppFrame.addFrame(dbcon.getGUI());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }


        MainController.getInstance().showMainFrame();
    }


//*********************************************


    public static void test() {
        // TODO code application logic here

        System.out.println("unko!");

        try{
            Mongo m = new Mongo();
            DB db = m.getDB( "mydb" );

            //コレクション名のリスト
            Set<String> colls = db.getCollectionNames();
            for (String s : colls) {
                System.out.println(s);
            }

            //コレクションに接続
            DBCollection coll = db.getCollection("testCollection");
//            for(int i = 0 ; i < 10 ; i++){
//                BasicDBObject doc = new BasicDBObject();
//                doc.put("name", "MongoDB_" + i);
//                doc.put("type", "database_" + i);
//                doc.put("count", i);
//
//                BasicDBObject info = new BasicDBObject();
//                info.put("x", 203 + i);
//                info.put("y", 102 + i);
//
//                doc.put("info", info);
//                coll.insert(doc);
//            }

//            BasicDBObject doc = new BasicDBObject();
//            doc.put("name", "TEST_1");
//            for(int i = 0 ; i < 10 ; i++){
//                BasicDBObject info = new BasicDBObject();
//                info.put("x", 203 + i);
//                info.put("y", 102 + i);
//
//                doc.put("info_" + i, info);
//            }
//            coll.insert(doc);

//            System.out.println(coll.find().next());
//
            DBCursor cur = coll.find();
            while(cur.hasNext()) {

                DBObject obj = cur.next();
                Map map = obj.toMap();

                System.out.println(obj);
                for(Object o:map.keySet().toArray()){
                    System.out.println("→" + o + ":" + map.get(o) + "[" + map.get(o).getClass().getName() + "]" );
                }
            }
            System.out.println(coll.find().count());



            //System.out.println(coll.find(new DBObject("name", "")).count());
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void find() {
        try{
            Mongo m = new Mongo();
            DB db = m.getDB( "mydb" );
            DBCollection coll = db.getCollection("testCollection");
            DBCursor cur = coll.find();


            while(cur.hasNext()) {
                DBObject obj = cur.next();
                printDBObject((DBObject)obj);
            }
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void printDBObject(DBObject obj){
        printDBObject(obj, 0);
    }
    private static void printDBObject(DBObject obj, int cnt){
        Map map = obj.toMap();
        for(Object o:map.keySet().toArray()){
            Object value = map.get(o);

            for(int i = 0 ; i < cnt ; i++){
                System.out.print("\t");
            }
            System.out.println("\t→" + o + ":" + value + "[" + value.getClass().getName() + "]" );

            if(value instanceof DBObject){
                printDBObject((DBObject)value, cnt+1);
            }
        }
    }
}
