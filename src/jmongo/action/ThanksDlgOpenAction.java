/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmongo.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import jmongo.MainController;

/**
 * ThanksDlgオープンアクション
 * @author Administrator
 */
public class ThanksDlgOpenAction extends AbstractAction {
    public ThanksDlgOpenAction(){
        putValue(Action.NAME , "thank you.");
        putValue(Action.SHORT_DESCRIPTION, "thank you.");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jmongo/icon/famfamfam_silk_icons_v013/icons/rainbow.png")));
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("ThanksDlg OpenAction call!!");
        MainController.getInstance().showThanksDlg();
    }

}
