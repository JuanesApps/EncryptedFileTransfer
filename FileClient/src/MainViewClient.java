import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.io.*;

public class MainViewClient extends JFrame {

    private PanelViewClient panelViewClient;

    private JFileChooser jFileChooser;

    private MainClass client;

    public MainViewClient() {
        initialize();
        center();
        client = new MainClass();
        panelViewClient.setLb_ipserver(new JLabel(client.getHost()));
        panelViewClient.setLb_port(new JLabel(client.getPuerto()+""));
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(""));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
    }

    private void initialize() {

        setTitle("Encryted File Transfer :: CLIENT");
        setSize(400, 400);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        panelViewClient = new PanelViewClient(this);
        add(panelViewClient, BorderLayout.CENTER);

    }

    private void center() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dimension.width - getWidth()) / 2;
        int y = (dimension.height - getHeight()) / 2;
        setLocation(x, y);
    }

    public void selectFile() {
        String filename = "";
        int returnValue = jFileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            filename = file.getAbsolutePath();
            client.setPathname(filename);
            client.transferFile();
        } else
            JOptionPane.showMessageDialog(this, "No file choose");

    }

    public void accept(){
        panelViewClient.getTxt_ipserver().setEnabled(false);
        client.setHost(panelViewClient.getTxt_ipserver().toString());
        panelViewClient.getTxt_port().setEnabled(false);
        client.setPuerto(Integer.parseInt(panelViewClient.getTxt_port().toString()));
    }

    public void edit(){
       // panelViewClient.getTxt_ipserver().setEnabled(true);
       // panelViewClient.getTxt_port().setEnabled(true);
    }

    public static void main(String[] args) {
        MainViewClient mainViewClient = new MainViewClient();
        mainViewClient.setVisible(true);
    }
}
