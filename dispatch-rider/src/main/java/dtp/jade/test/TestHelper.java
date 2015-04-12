package dtp.jade.test;

import dtp.gui.SimLogic;
import dtp.jade.gui.GUIAgent;


public class TestHelper extends SimLogic {


    public TestHelper(GUIAgent agent) {

        super(agent);
    }

    public void displayMessage(String txt) {
        System.out.println(txt);
    }

}
