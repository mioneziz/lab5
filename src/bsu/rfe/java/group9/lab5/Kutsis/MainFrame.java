package bsu.rfe.java.group9.lab5.Kutsis;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private JFileChooser fileChooser = null;
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem setLockXChangeMenuItem;
    private JCheckBoxMenuItem setLockYChangeMenuItem;
    private JCheckBoxMenuItem showTicksMenuItem;
    private JMenuItem resetGraphicsMenuItem;
    private GraphicsDisplay display = new GraphicsDisplay();
    private boolean fileLoaded = false;

    public MainFrame() {
        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
        setExtendedState(MAXIMIZED_BOTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (MainFrame.this.fileChooser == null) {
                    MainFrame.this.fileChooser = new JFileChooser();
                    MainFrame.this.fileChooser.setCurrentDirectory(new File("."));
                }

                MainFrame.this.fileChooser.showOpenDialog(MainFrame.this);
                MainFrame.this.openGraphics(MainFrame.this.fileChooser.getSelectedFile());
            }
        };

        fileMenu.add(openGraphicsAction);
        Action resetGraphicsAction = new AbstractAction("Отменить все изменения") {
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.display.reset();
            }
        };
        this.resetGraphicsMenuItem =  fileMenu.add(resetGraphicsAction);
        this.resetGraphicsMenuItem.setEnabled(false);
        this.getContentPane().add(this.display, "Center");



        Action saveGraphicsAction = new AbstractAction("Сохранить файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    saveGraphics(fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(saveGraphicsAction);

        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);

        Action showAxisAction = new AbstractAction("Показывать оси координат") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }

        };

        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);

        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);

        Action showTicksAction = new AbstractAction("Показывать отметки на оси") {
            public void actionPerformed(ActionEvent event) {
                display.setShowTicks(showTicksMenuItem.isSelected());
            }
        };
        showTicksMenuItem = new JCheckBoxMenuItem(showTicksAction);
        graphicsMenu.add(showTicksMenuItem);
        showTicksMenuItem.setSelected(true);

        Action lockXChangeAction = new AbstractAction("Не изменять координату по оси Х") {
            public void actionPerformed(ActionEvent event) {
                display.setLockXChange(setLockXChangeMenuItem.isSelected());
            }
        };
        setLockXChangeMenuItem = new JCheckBoxMenuItem(lockXChangeAction);
        graphicsMenu.add(setLockXChangeMenuItem);
        setLockXChangeMenuItem.setSelected(true);

        Action lockYChangeAction = new AbstractAction("Не изменять координату по оси Y") {
            public void actionPerformed(ActionEvent event) {
                display.setLockYChange(setLockYChangeMenuItem.isSelected());
            }
        };
        setLockYChangeMenuItem = new JCheckBoxMenuItem(lockYChangeAction);
        graphicsMenu.add(setLockYChangeMenuItem);
        setLockYChangeMenuItem.setSelected(false);

        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        getContentPane().add(display, BorderLayout.CENTER);
    }

    protected void openGraphics(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new
                    FileInputStream(selectedFile));

            Double[][] graphicsData = new
                    Double[in.available()/(Double.SIZE/8)/2][];

            int i = 0;
            while (in.available()>0) {
                Double x = in.readDouble();
                Double y = in.readDouble();
                graphicsData[i++] = new Double[] {x, y};
            }
            if (graphicsData!=null && graphicsData.length>0) {
                fileLoaded = true;
                display.showGraphics(graphicsData);
                this.resetGraphicsMenuItem.setEnabled(true);
            }
            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный " +
                    "файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения " +
                            "координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

    }

    protected void saveGraphics(File selectedFile){
        try {
            File file = new File(selectedFile.getName());
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(selectedFile.getName(), false);
            DataOutputStream dos = new DataOutputStream(fos);

            for (Double[] d : display.getData()) {
                dos.writeDouble(d[0]);
                dos.writeDouble(d[1]);
            }
            dos.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class GraphicsMenuListener implements MenuListener {

        public void menuSelected(MenuEvent e) {
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}
