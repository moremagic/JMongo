/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MongoIFrame.java
 *
 * Created on 2011/03/03, 18:24:11
 */

package gui;

import com.mongodb.util.JSON;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import jp.personal.jmongo.DBConContoroller;

/**
 *
 * @author Administrator
 */
public class MongoIFrame extends javax.swing.JInternalFrame {
    private DBConContoroller m_con = null;
    private DefaultTableModel m_tableModel = null;
    private DefaultTreeModel m_treeModel = null;
    private DefaultTreeModel m_treeModel2 = null;

    /** Creates new form MongoIFrame */
    public MongoIFrame(DBConContoroller con) {
        initComponents();
        m_con = con;

        //ツリーパネルの初期化
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(m_con.getDBName());
        m_treeModel = new DefaultTreeModel(root);
        jTree1.setModel(m_treeModel);
        createCollectionTree();

        //表の初期化
        m_tableModel = new DefaultTableModel(null, new String [] {"No", "data"});
        jTable1.setModel(m_tableModel);
        jTable1.getColumnModel().getColumn(1).setMinWidth(800);

        jTable1.setDefaultEditor(Object.class, null);
        jTable1.getTableHeader().setReorderingAllowed(false);

        /**
         * Tree選択位置が変わるときの動的読み込み
         */
        this.jTree1.addTreeSelectionListener(
            new TreeSelectionListener(){
                public void valueChanged(TreeSelectionEvent e){
                    TreePath path = e.getNewLeadSelectionPath();
                    final String nodeName = path.getLastPathComponent().toString();

                    Runnable run = new Runnable(){
                        public void run(){
                            showData(nodeName);
                        }
                    };
                    java.awt.EventQueue.invokeLater(run);

                }
            }
        );

        /**
         * テーブル選択位置が変わる時のイベント設定
         */
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) return;

                String data = "";
                if( jTable1.getSelectedRow() != -1 ){
                    data = ((List)m_tableModel.getDataVector().get(jTable1.getSelectedRow())).get(1).toString();
                    System.out.println( JSON.parse(data) );
                }
                setMongoData(data);
            }
        });
    }

    /**
     * メインツリーパネルの初期化
     */
    private void createCollectionTree() {
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)m_treeModel.getRoot();

            for (String s : m_con.getCollections()) {
                //追加判定
                boolean b = true;
                for(int i = 0 ; i < root.getChildCount() ; i++){
                    if( root.getChildAt(i).toString().equals(s) ){
                        b = false;
                        break;
                    }
                }
                if( b ){
                    DefaultMutableTreeNode collectionReef = new DefaultMutableTreeNode(s);
                    root.add(collectionReef);
                }
            }

            jTree1.updateUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "接続設定を確認してください \n[" + ex.toString() + "]",  "接続に失敗しました", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(MongoIFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * リストをクリアします
     */
    private void clearList(){
        while(m_tableModel.getRowCount() > 0){
            m_tableModel.removeRow(0);
        }
    }

    /**
     * 選択したコレクションのデータを表示する
     * 
     * @param collectionName
     */
    private void showData(String collectionName){
        try {
            clearList();
            String[] datas = m_con.findString(collectionName);
            for (int i = 0; i < datas.length; i++) {
                m_tableModel.addRow(new Object[]{i + 1, datas[i]});
            }

            createCollectionTree();
        } catch (Exception ex) {
            Logger.getLogger(MongoIFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * データの設定
     *
     * @param s DB文字列
     */
    private void setMongoData(String s){
        if(s == null || s.length() == 0)return;

        jTextArea1.setText(s.replaceAll(",", ",\n"));
        if(m_treeModel2 == null){
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Record");
            m_treeModel2 = new DefaultTreeModel(root);
            jTree2.setModel(m_treeModel2);
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)m_treeModel2.getRoot();
        root.removeAllChildren();

        root.add( (DefaultMutableTreeNode)createMongoDataTree(s) );
        jTree2.updateUI();
        expandAll(jTree2);
    }


    private void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }


    /**
     * データtリーの作成
     * 
     * @param s データ文字
     * @return
     */
    public TreeNode createMongoDataTree(String s){
        DefaultMutableTreeNode ret = new DefaultMutableTreeNode();

        Map map = (Map)JSON.parse(s);
        for( String key : (String[])map.keySet().toArray(new String[]{}) ){
            if(map.get(key) instanceof Map){
                ret.add( (DefaultMutableTreeNode)createMongoDataTree(map.get(key).toString()) );
            }else{
                DefaultMutableTreeNode collectionReef = new DefaultMutableTreeNode(key + ":" + map.get(key));
                ret.add(collectionReef);
            }
        }

        return ret;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(200, 322));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setViewportView(jTree1);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(150);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setPreferredSize(new java.awt.Dimension(300, 300));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane4.setViewportView(jTree2);

        jSplitPane3.setRightComponent(jScrollPane4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jPanel5.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jButton1.setText("書き込み");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton1);

        jButton2.setLabel("削除");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton2);

        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jSplitPane3.setLeftComponent(jPanel5);

        jPanel4.add(jSplitPane3, java.awt.BorderLayout.CENTER);

        jSplitPane2.setBottomComponent(jPanel4);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        jSplitPane2.setLeftComponent(jScrollPane1);

        jPanel3.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel3);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        // TODO add your handling code here:
        m_con.close();
    }//GEN-LAST:event_formInternalFrameClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String collection = null;
        String newData = null;
        String rowData = null;

        try{
            collection = jTree1.getSelectionPath().getPath()[jTree1.getSelectionCount()].toString();
            newData = jTextArea1.getText();
            rowData = ((List)m_tableModel.getDataVector().get(jTable1.getSelectedRow())).get(1).toString();
        }catch(Exception err){}

        if(rowData != null){
            if( m_con.update(collection, rowData, newData) ){
                jTextArea1.setText("");
                showData(collection);
            }else{
                JOptionPane.showMessageDialog(null, "書き込みに失敗しました",  "書き込み失敗しました", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            if( m_con.insert(collection, newData) ){
                jTextArea1.setText("");
                showData(collection);
            }else{
                JOptionPane.showMessageDialog(null, "書き込みに失敗しました",  "書き込み失敗しました", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String collection = jTree1.getSelectionPath().getPath()[jTree1.getSelectionCount()].toString();
        String newData = jTextArea1.getText();

        if( m_con.delete(collection, newData) ){
            jTextArea1.setText("");
            showData(collection);
        }else{
            JOptionPane.showMessageDialog(null, "削除に失敗しました",  "削除失敗しました", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    new MongoIFrame(null).setVisible(true);
                }catch(Exception err){}
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    // End of variables declaration//GEN-END:variables

}
