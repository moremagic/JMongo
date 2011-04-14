/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package action;

import gui.DBConectDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import jp.personal.jmongo.DBConContoroller;
import jp.personal.jmongo.MainController;

/**
 * 新規DBのオープンアクション
 * @author Administrator
 */
public class OpenAction extends AbstractAction {
    public OpenAction(){
        putValue(Action.NAME , "open");
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("OpenAction call!!");

        DBConectDialog dialog = new DBConectDialog();
        dialog.setVisible(true);

        String sConfig = dialog.getConnectionConfing();
        dialog.dispose();

        if(sConfig!= null && sConfig.length() > 0){
            String host = sConfig.substring(0, sConfig.indexOf(":"));
            String port = sConfig.substring(sConfig.indexOf(":")+1, sConfig.indexOf("/"));
            String dbname = sConfig.substring(sConfig.indexOf("/")+1);
            try{
                DBConContoroller con = new DBConContoroller(Integer.parseInt(port), host, dbname);
                MainController.getInstance().showDBConect(con);
            }catch(Exception err){
                err.printStackTrace();
            }
        }
    }

}
