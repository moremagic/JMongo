/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * リスト表示のための標準コンポーネント
 *
 * ・ページ遷移機能を持ちます
 * ・ページ遷移アクションをLisnerとして登録できます
 *
 * @author mitsu
 */
public class CollectionListView extends javax.swing.JPanel {
    public static boolean _DEBUG_FLG = true;
    private final String[] _SELECT_STATUS = {"", "▽", "△"};//ヘッダ選択状態保持
    private int m_sortIdx = 0;//ヘッダの選択状態を表すインデクス
    private int m_sortColumn = 0;//ソートが必要なカラム
    
    //テーブルヘッダ保持用
    private List<String> m_columnKey = new ArrayList<String>();

    /**
     * Creates new form CommonListView
     */
    public CollectionListView() {
        initComponents();
        jTable1.setModel(new _DataModel(new Object[0][0], new String[0]));


        jTable1.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                m_sortColumn = jTable1.getTableHeader().getColumnModel().getColumnIndexAtX(e.getX());
                m_sortIdx = (m_sortIdx+1) % _SELECT_STATUS.length;
                String sortValue = m_columnKey.get(m_sortColumn) + " " + _SELECT_STATUS[m_sortIdx];
     
                //ヘッダ名の編集
                for(int i = 0 ; i < jTable1.getColumnCount() ; i++){
                    TableColumn colModel = jTable1.getTableHeader().getColumnModel().getColumn(i);
                    if(i == m_sortColumn){
                        colModel.setHeaderValue(sortValue);                        
                    }else{
                        colModel.setHeaderValue(m_columnKey.get(i));
                    }
                }
                jTable1.getTableHeader().repaint();
                
                //再検索イベントを発行する
                fireActionListener(new ActionEvent(e.getSource(), e.getID(), "mouseClicked"));
            }
        });
    }

    /**
     * ページ送り設定
     *
     * @param bFeed 有効にする場合True
     */
    public void setPageFeeding(boolean bFeed) {
        jPanel2.setVisible(bFeed);
    }

    /**
     * ヘッダを設定します
     *
     * @param columnKey 物理名
     */
    public void setHeader(String[] columnKey) {
        m_columnKey = java.util.Arrays.asList(columnKey);
        jTable1.setModel(new _DataModel(new Object[0][0], m_columnKey.toArray(new String[0])));
    }

    /**
     * ヘッダを取得します
     *
     * {columnName[] 論理名, columnKey[] 物理名}
     */
    public String[][] getHeader() {
        _DataModel model = (_DataModel) jTable1.getModel();
        return new String[][]{model.getColumnName(), model.getColumnKey()};
    }

    /**
     * ページ内の件数上限を取得
     *
     * @return
     */
    public int getCount() {
        return Integer.parseInt(jSpinner3.getValue().toString());
    }

    /**
     * 表示するページを取得
     *
     * @return
     */
    public int getPages() {
        return Integer.parseInt(jSpinner1.getValue().toString());
    }

    /**
     * 表示するページ数を設定します。
     *
     * @param pageNum 設定するページ番号
     */
    public void setPages(int pageNum) {
        jSpinner1.setValue(pageNum);
    }

    /**
     * リストをクリアします
     */
    public void clearList() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
    }
    
    /**
     * ソート順を返却する
     * 
     * @return "" or DESC or ASC
     */
    public String getSortOrder(){
        String ret = "";
        if(m_sortIdx == 1){
            ret = "ASC";
        }else if(m_sortIdx == 2){
            ret = "DESC";
        }
        return ret;
    }

    /**
     * ソートを行うキーを返却する
     * 
     * @return ソートが必要なカラムの物理名
     */
    public String getSortKey(){
        return m_columnKey.get(m_sortColumn);
    }
    
    /**
     * 総ページ数を設定する
     * 
     * @param count 
     */
    public void setAllDataCount(long count){
        long pages = 1;
        pages = count / Integer.parseInt(jSpinner3.getValue().toString());
        if (pages == 0 || count % (pages * Integer.parseInt(jSpinner3.getValue().toString())) != 0) {
            pages++;
        }
        
        jLabel4.setText("" + count);
        jLabel2.setText("" + pages);
    }
    
    /**
     * データリストを設定します
     *
     * @param datas
     */
    public synchronized void setListDatas(final List<Map<String, Object>> datas) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                _setListDatas(datas);
            }
        });
    }
    

    private void _setListDatas(List<Map<String, Object>> datas) {
        //キー情報の設定
        m_columnKey.clear();
        for(Map m : datas){
            for( Object mKey : m.keySet().toArray() ){
                if( !m_columnKey.contains(mKey) ){
                    m_columnKey.add(mKey.toString());
                }
            }
        }
        jTable1.setModel(new _DataModel(new Object[0][0], m_columnKey.toArray(new String[0])));
        
        //データの設定
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        String[] keys = ((_DataModel) model).getColumnKey();
        for (Map m : datas) {
            Object[] lines = new Object[keys.length];
            for (int i = 0; i < keys.length; i++) {
                lines[i] = m.get(keys[i]);
            }
            model.addRow(lines);
        }

        jButton2.setEnabled(Integer.parseInt(jSpinner1.getValue().toString()) != 1);
        jButton3.setEnabled(Integer.parseInt(jLabel2.getText()) > Integer.parseInt(jSpinner1.getValue().toString()));
    }

    /**
     * データリストを返却する
     *
     * @return
     */
    public synchronized List<Map<String, Object>> getListDatas() {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        _DataModel model = (_DataModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            ret.add(model.getRowData(i));
        }
        return ret;
    }
    
    //アクションリスナ管理変数
    private final List<ActionListener> m_actionListener = new ArrayList<ActionListener>();

    /**
     * 選択されているインデックスを返却します
     *
     * @return
     */
    public int getSelectedRow() {
        return jTable1.getSelectedRow();
    }

    /**
     * 選択されている行を返却します
     *
     * @return
     */
    public void removeRow(int idx) {
        ((DefaultTableModel) jTable1.getModel()).removeRow(idx);
    }

    /**
     * 選択されているインデックスを返却します
     *
     * @return
     */
    public Map<String, Object> getRowData(int idx) {
        try {
            return ((_DataModel) jTable1.getModel()).getRowData(idx);
        } catch (Exception th) {
            Logger.getLogger(CollectionListView.class.getName()).log(Level.SEVERE, null, th);
            return null;
        }
    }

    /**
     * 選択をクリアする
     */
    public void clearSelection() {
        jTable1.clearSelection();
    }

    /**
     * アクションリスナの登録
     */
    public synchronized void addActionListener(ActionListener a) {
        m_actionListener.add(a);
    }

    /**
     * アクションリスナの削除
     */
    public synchronized void removeActionListener(ActionListener a) {
        m_actionListener.remove(a);
    }

    /**
     * アクションリスナのクリア
     */
    public synchronized void removeActionListener() {
        m_actionListener.clear();
    }

    /**
     * アクション発火
     *
     * @param evt イベントオブジェクト
     */
    private synchronized void fireActionListener(ActionEvent evt) {
        for (ActionListener act : m_actionListener) {
            act.actionPerformed(evt);
        }
    }

    /**
     * JTable の選択リスナーを追加する
     *
     * @param listener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        jTable1.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * テーブルのPreferredWidthの設定を行います。
     *
     * @param sizeList 設定するサイズのINT配列
     */
    public void setColumnPreferredWidth(int[] sizeList) {
        for (int i = 0; i < sizeList.length; i++) {
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(sizeList[i]);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();

        setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setText("|<");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setText("<");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        jSpinner1.setModel(new SpinnerNumberModel(1, 1, null, 1));
        jSpinner1.setPreferredSize(new java.awt.Dimension(50, 20));
        jPanel2.add(jSpinner1);

        jLabel1.setText("/");
        jPanel2.add(jLabel1);

        jLabel2.setText("***");
        jPanel2.add(jLabel2);

        jButton3.setText(">");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3);

        jButton4.setText(">|");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4);

        jLabel5.setText("検索結果(件)：");
        jPanel2.add(jLabel5);
        jPanel2.add(jLabel4);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("limit");
        jLabel3.setPreferredSize(new java.awt.Dimension(50, 13));
        jPanel2.add(jLabel3);

        jSpinner3.setPreferredSize(new java.awt.Dimension(50, 20));
        jSpinner3.setValue(50);
        jPanel2.add(jSpinner3);

        jPanel1.add(jPanel2);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jSpinner1.setValue(1);
        fireActionListener(evt);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jSpinner1.setValue(jSpinner1.getPreviousValue());
        fireActionListener(evt);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jSpinner1.setValue(jSpinner1.getNextValue());
        fireActionListener(evt);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jSpinner1.setValue(Integer.parseInt(jLabel2.getText()));
        fireActionListener(evt);
    }//GEN-LAST:event_jButton4ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    class _DataModel extends DefaultTableModel {

        String[] m_columnKey = null;//物理名

        /**
         * コンストラクタ
         *
         * @param object 初期データ
         * @param columnKey 物理名
         */
        public _DataModel(Object[][] object, String[] columnKey) {
            super(object, columnKey);
            m_columnKey = columnKey;
        }

        /**
         * カラム物理名を返却します
         *
         * @return
         */
        public String[] getColumnKey() {
            return m_columnKey;
        }

        /**
         * カラム論理名を返却します
         *
         * @return
         */
        public String[] getColumnName() {
            String[] ret = new String[super.getColumnCount()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = super.getColumnName(i);
            }
            return ret;
        }

        /**
         * 指定された行のデータを取得する
         *
         * @param row 指定行
         * @return
         */
        public Map<String, Object> getRowData(int row) {
            Map<String, Object> ret = new HashMap<String, Object>();
            if (getRowCount() > row) {
                for (int i = 0; i < m_columnKey.length; i++) {
                    ret.put(m_columnKey[i], getValueAt(row, i));
                }
            }
            return ret;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            Object o = getValueAt(0, columnIndex);
            return (o != null) ? o.getClass() : Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
