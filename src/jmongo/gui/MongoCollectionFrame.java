/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MongoIFrame.java
 *
 * Created on 2011/03/03, 18:24:11
 */

package jmongo.gui;

import com.mongodb.util.JSON;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import jmongo.DBConContoroller;

/**
 *
 * @author Administrator
 */
public class MongoCollectionFrame extends javax.swing.JInternalFrame {
    private DBConContoroller m_con = null;
    private DefaultTreeModel m_treeModel = null;
    private DefaultTreeModel m_treeModel2 = null;

    /** Creates new form MongoIFrame */
    public MongoCollectionFrame(DBConContoroller con) {
        initComponents();
        m_con = con;
        
        //Frame icon
        this.setFrameIcon(new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/database_gear.png")));

        //ツリーパネルの初期化
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        ImageIcon icon = new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/table.png"));
        ImageIcon closeIcon = new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/world_add.png"));
        ImageIcon openIcon = new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/world.png"));
        renderer.setLeafIcon(icon);
        renderer.setClosedIcon(closeIcon);
        renderer.setOpenIcon(openIcon);
        jTree1.setCellRenderer(renderer);
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(m_con.getDBName());
        m_treeModel = new DefaultTreeModel(root);
        jTree1.setModel(m_treeModel);
        createCollectionTree();

        /**
         * Tree選択位置が変わるときの動的読み込み
         */
        this.jTree1.addTreeSelectionListener(
            new TreeSelectionListener(){
                public void valueChanged(TreeSelectionEvent e){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            final String nodeName = jTree1.getLastSelectedPathComponent().toString();
                            showData(nodeName);
                        }
                    });
                }
            }
        );

        collectionListView1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        final String nodeName = jTree1.getLastSelectedPathComponent().toString();
                        showData(nodeName);
                    }
                });
            }
        });
        
        /**
         * テーブル選択位置が変わる時のイベント設定
         */
        collectionListView1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) return;

                String data = JSON.serialize( collectionListView1.getRowData(collectionListView1.getSelectedRow()) );
                setMongoData(data);
            }
        });
    }

    /**
     * メインツリーパネルの初期化
     */
    private synchronized void createCollectionTree() {
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
            Logger.getLogger(MongoCollectionFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * リストをクリアします
     */
    private void clearList(){
        collectionListView1.clearList();
    }

    /**
     * 選択したコレクションのデータを表示する
     * 
     * @param collectionName
     */
    private void showData(){
        showData(getCollectionName());
    }

    /**
     * 指定したコレクションのデータを表示する
     *
     * @param collectionName
     */
    private synchronized void showData(String collectionName){
        try {
            //データのクリア
            clearList();
            
            //データ総件数の設定
            long colSize = m_con.getCollectionDataCount(collectionName);
            collectionListView1.setAllDataCount(colSize);  
            
            //検索条件の設定
            Map<String, Object> query = new HashMap<String, Object>();
            if(collectionListView1.getFindValue().length() > 0){
                query.put(collectionListView1.getFindKey(), Pattern.compile(collectionListView1.getFindValue() + ".*"));
            }
            
            List<Map<String, Object>> datas = m_con.findCollection(collectionName, query, (collectionListView1.getPages()-1) * collectionListView1.getCount(), collectionListView1.getCount());
            collectionListView1.setListDatas(datas);
            
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    createCollectionTree();
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(MongoCollectionFrame.class.getName()).log(Level.SEVERE, null, ex);
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

        root.add( (DefaultMutableTreeNode)createMongoDataTree("root", s) );
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
     * データツリーの作成
     * 
     * @param s データ文字
     * @return
     */
    public TreeNode createMongoDataTree(String treeKey, String s){
        DefaultMutableTreeNode ret = new DefaultMutableTreeNode(treeKey);

        Map map = (Map)JSON.parse(s);
        String[] keys = (map == null)?new String[0]:(String[])map.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for( String key : keys ){
            if(map.get(key) instanceof Map){
                ret.add( (DefaultMutableTreeNode)createMongoDataTree(key, map.get(key).toString()) );
            }else{
                DefaultMutableTreeNode collectionReef = new DefaultMutableTreeNode(key + ":" + map.get(key));
                ret.add(collectionReef);
            }
        }

        return ret;
    }

    /**
     * 現在選択されているコレクション名を取得する
     * @return コレクション名
     */
    public String getCollectionName(){
        return jTree1.getLastSelectedPathComponent().toString();
    }

    /**
     * ユーザが編集したデータを取得する
     * @return
     */
    public String getEditData(){
        return jTextArea1.getText();
    }

    /**
     * 現在選択されているデータを取得する
     * 選択されていない場合Null
     * 
     * @return
     */
    public String getSelectedData(){
        try{
            return JSON.serialize( collectionListView1.getRowData(collectionListView1.getSelectedRow()) );
        }catch(Exception err){
            return null;
        }
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
        jPanel7 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
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
        collectionListView1 = new jmongo.gui.CollectionListView();

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

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(jTree1);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jButton5.setText("import");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton5);

        jButton4.setText("dump");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton4);

        jButton3.setText("Drop");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton3);

        jPanel2.add(jPanel7, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(150);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setPreferredSize(new java.awt.Dimension(300, 300));

        jPanel4.setLayout(new java.awt.BorderLayout());

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree2.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane4.setViewportView(jTree2);

        jSplitPane3.setRightComponent(jScrollPane4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jPanel5.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jButton1.setText("update");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton1);

        jButton2.setText("delete");
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
        jSplitPane2.setLeftComponent(collectionListView1);

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
        if (m_con.update()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    jTextArea1.setText("");
                }
            });
            showData();
        } else {
            JOptionPane.showMessageDialog(null, "書き込みに失敗しました", "書き込み失敗しました", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if( m_con.delete() ){
            jTextArea1.setText("");
            showData();
        }else{
            JOptionPane.showMessageDialog(null, "削除に失敗しました",  "削除失敗しました", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if( m_con.drop() ){
            jTextArea1.setText("");
            showData();
        }else{
            JOptionPane.showMessageDialog(this, "削除に失敗しました",  "削除失敗しました", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser filechooser = new JFileChooser();

        int selected = filechooser.showSaveDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION){
            File file = filechooser.getSelectedFile();
            if(m_con.dump(file)){
                JOptionPane.showMessageDialog(this, "保存に成功しました",  "保存に成功しました", JOptionPane.INFORMATION_MESSAGE);
                jTextArea1.setText("");
                showData();
            }else{
                JOptionPane.showMessageDialog(this, "保存に失敗しました",  "保存に失敗しました", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JFileChooser filechooser = new JFileChooser();

        int selected = filechooser.showOpenDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION){
            File file = filechooser.getSelectedFile();
            if(m_con.load(file)){
                JOptionPane.showMessageDialog(this, "取り込みに成功しました",  "取り込みに成功しました", JOptionPane.INFORMATION_MESSAGE);
                jTextArea1.setText("");
                showData();
            }else{
                JOptionPane.showMessageDialog(this, "取り込みに失敗しました",  "取り込みに失敗しました", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed



    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    new MongoCollectionFrame(null).setVisible(true);
                }catch(Exception err){}
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jmongo.gui.CollectionListView collectionListView1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    // End of variables declaration//GEN-END:variables

}
