package gui.main;

/**
 * @author Jakub Tyrcha
 *         <p/>
 *         Implementacja wzorca singleton opakowujaca WindowGUI.
 *         Za posrednictwem tej klasy odbywa sie komunikacja z gui.
 */
public class SingletonGUI extends WindowGUI {
    private SingletonGUI() {
    }

    public static SingletonGUI getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private final static SingletonGUI instance = new SingletonGUI();
    }

} 
