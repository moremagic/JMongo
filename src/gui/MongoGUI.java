/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MongoGUI.java
 *
 * Created on 2011/03/03, 17:39:30
 */

package gui;

import action.ActionManager;
import action.GFSOpenAction;
import action.OpenAction;
import java.beans.PropertyVetoException;
import javax.swing.Action;
import javax.swing.JInternalFrame;

/**
 *
 * @author Administrator
 */
public class MongoGUI extends javax.swing.JFrame {

    /** Creates new form MongoGUI */
    public MongoGUI() {
        initComponents();
        this.setSize(800, 600);


        //Actionの設定
        Action act1 = ActionManager.getInstance().getAction(OpenAction.class.getCanonicalName());
        Action act2 = ActionManager.getInstance().getAction(GFSOpenAction.class.getCanonicalName());
        
        jMenu1.add(act1);
        jMenu1.add(act2);
    }

    /**
     * 新規コネクションの追加
     */
    public void addFrame(JInternalFrame iframe){
        int count = jDesktopPane1.getAllFrames().length;

        iframe.setLocation(10*count, 10*count);
        iframe.setVisible(true);
        jDesktopPane1.add(iframe);

        try {
            iframe.setSelected(true);
            iframe.setMaximum(true);
        } catch (PropertyVetoException ex) {}
        iframe.setLayer(0);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(jDesktopPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MongoGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables

}
