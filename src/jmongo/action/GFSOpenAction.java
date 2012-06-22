/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmongo.action;

import jmongo.config.DBConnectionBean;
import jmongo.gui.DBConectDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import jmongo.DBConContoroller;
import jmongo.MainController;

/**
 * 新規DBのオープンアクション
 * @author Administrator
 */
public class GFSOpenAction extends AbstractAction {
    public GFSOpenAction(){
        putValue(Action.NAME , "open GridFS");
        putValue(Action.SHORT_DESCRIPTION, "open gridfs editor.");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/database_save.png")));
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("GridFS DBOpenAction call!!");

        DBConectDialog dialog = new DBConectDialog();
        dialog.setVisible(true);

        String sConfigName = dialog.getConnectionConfingName();
        DBConnectionBean config = dialog.getConnectionConfing();
        dialog.dispose();

        if(config!= null){
            
            try{
                DBConContoroller con = new DBConContoroller(sConfigName, config.getM_host(), config.getM_port(), config.getM_DBName(), config.getM_DBUser(), config.getM_DBPass());
                MainController.getInstance().showGFSDBConect(con);
            }catch(Exception err){
                err.printStackTrace();
            }
        }
    }

}
