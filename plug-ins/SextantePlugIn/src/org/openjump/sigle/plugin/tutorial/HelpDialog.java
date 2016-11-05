package org.openjump.sigle.plugin.tutorial;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openjump.core.ui.swing.DetachableInternalFrame;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.util.FileUtil;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.Logger;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.help.AlgorithmTreeCellRenderer;
import es.unex.sextante.openjump.language.I18NPlug;

public class HelpDialog extends JPanel implements TreeSelectionListener {
    /**
     * Class adapted from HelpOJPlugIn.class from SIGLE OpenJump Viatoris
     * 2015-02-22. Giuseppe Aruta. version 01 2016-11-02. Giuseppe Aruta.
     * version 02
     */
    private static final long serialVersionUID = 1L;
    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;
    private static String help = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteHelpPlugin.help");

    public HelpDialog() {
        // super(new GridLayout(1, 0));

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                Sextante.getText("Help"));
        createNodes(top);
        this.tree = new JTree(top);
        this.tree.getSelectionModel().setSelectionMode(1);
        this.tree.addTreeSelectionListener(this);
        tree.setCellRenderer(new AlgorithmTreeCellRenderer());
        final BorderLayout thisLayout = new BorderLayout();
        this.setLayout(thisLayout);
        this.setPreferredSize(new java.awt.Dimension(800, 500));
        this.setSize(new java.awt.Dimension(800, 500));
        JScrollPane treeViewPane = new JScrollPane(this.tree,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeViewPane.setPreferredSize(new Dimension(300, 450));
        treeViewPane.setMinimumSize(new Dimension(300, 450));
        this.htmlPane = new JEditorPane();
        this.htmlPane.setEditable(false);
        this.htmlPane.getDocument().putProperty("IgnoreCharsetDirective",
                Boolean.TRUE);
        JScrollPane htmlViewPane = new JScrollPane(this.htmlPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        htmlViewPane.setPreferredSize(new Dimension(300, 450));
        htmlViewPane.setMinimumSize(new Dimension(300, 450));
        JSplitPane splitPaneHTML = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                treeViewPane, htmlViewPane);
        JPanel buttonpanel = new JPanel();
        JButton print = new JButton("Print");
        // JButton print = new JButton("Save");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printButton_actionPerformed(e);
            }
        });
        buttonpanel.add(print, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                        0, 0, 0), 0, 0));

        add(splitPaneHTML, BorderLayout.CENTER);
        // add(buttonpanel, BorderLayout.SOUTH);

    }

    protected void printButton_actionPerformed(ActionEvent e) {
        try {
            htmlPane.setContentType("text/html");
            boolean done = htmlPane.print();
            if (done) {
                JUMPWorkbench.getInstance().getFrame()
                        .setStatusMessage("Printing is done");

            } else {
                JUMPWorkbench.getInstance().getFrame()
                        .warnUser("Error while printing");
            }
        } catch (Exception pex) {
            Logger.error(pex);
            JUMPWorkbench
                    .getInstance()
                    .getFrame()
                    .warnUser(
                            I18N.get("org.openjump.core.ui.plugin.mousemenu.SaveDatasetsPlugIn.Error-See-Output-Window"));
            JUMPWorkbench.getInstance().getFrame().getOutputFrame()
                    .createNewDocument();
            JUMPWorkbench
                    .getInstance()
                    .getFrame()
                    .getOutputFrame()
                    .addText(
                            "Print Help Dialog Exception:"
                                    + new Object[] { e.toString() });
            pex.printStackTrace();
        }

    }

    protected void saveButton_actionPerformed(ActionEvent e) {
        JFileChooser chooser;
        File archivo = null;
        chooser = GUIUtil.createJFileChooserWithOverwritePrompting();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(GUIUtil.createFileFilter(
                I18N.get("org.openjump.core.ui.plugin.file.open.SelectFileLoaderPanel.file-type"), new String[] { "htm" })); //$NON-NLS-1$//$NON-NLS-2$
        int returned = chooser.showSaveDialog(JUMPWorkbench.getInstance()
                .getFrame());

        if (returned == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            chooser.getSelectedFile().delete();
            archivo = new File(path);
            archivo = FileUtil.addExtensionIfNone(archivo, "htm");//$NON-NLS-1$

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                htmlPane.setContentType("text/html");
                htmlPane.getDocument();
                HTMLEditorKit hk = new HTMLEditorKit();
                hk.write(baos, htmlPane.getDocument(), 0, htmlPane
                        .getDocument().getLength());

                FileOutputStream fos = new FileOutputStream(new File(
                        archivo.getAbsolutePath()));
                baos.writeTo(fos);
            } catch (Exception e1) {
                Logger.error(e1);
                JUMPWorkbench
                        .getInstance()
                        .getFrame()
                        .warnUser(
                                I18N.get("org.openjump.core.ui.plugin.mousemenu.SaveDatasetsPlugIn.Error-See-Output-Window"));
                JUMPWorkbench.getInstance().getFrame().getOutputFrame()
                        .createNewDocument();
                JUMPWorkbench
                        .getInstance()
                        .getFrame()
                        .getOutputFrame()
                        .addText(
                                "SaveImageToRasterPlugIn Exception:"
                                        + new Object[] { e.toString() });
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree
                .getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            BookInfo book = (BookInfo) nodeInfo;
            displayURL(book.bookURL);
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(this.helpURL);
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }

    public class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            this.bookName = book;
            this.bookURL = HelpDialog.class.getResource(filename);
            if (this.bookURL == null) {
                System.err.println("Couldn't find file: " + filename);
            }
        }

        public String toString() {
            return this.bookName;
        }

    }

    private void displayURL(URL url) {
        try {
            if (url != null) {
                this.htmlPane.setPage(url);
            } else {
                this.htmlPane.setText("File Not Found");
                if (DEBUG) {
                    System.out.println("Attempted to display a null URL.");
                }
            }
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }

    String toolbox = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteToolboxPlugin.Sextante-toolbox");
    String results = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteResultsPlugin.Results");
    String history = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteHistoryPlugin.History");
    String modeler = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteModelerPlugin.Modeler");
    String command_line = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteCommandLinePlugin.Command-line");
    String data_explorer = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextanteDataExplorerPlugin.dataexplorer");
    String coordinates = I18NPlug
            .getI18N("es.unex.sextante.kosmo.extensions.SextantePickCooridnates.pick-coordinates");

    private void createNodes(DefaultMutableTreeNode top) {

        DefaultMutableTreeNode basic_concept = new DefaultMutableTreeNode(
                Sextante.getText("Basic_concepts"));

        DefaultMutableTreeNode tools = new DefaultMutableTreeNode(
                Sextante.getText("Tools"));
        DefaultMutableTreeNode additional_information = new DefaultMutableTreeNode(
                Sextante.getText("Additional_information"));
        DefaultMutableTreeNode Sextante_Serial = new DefaultMutableTreeNode(
                "Sextante Serial number:" + Sextante.getVersionNumber());

        DefaultMutableTreeNode algorithms = new DefaultMutableTreeNode(
                Sextante.getText("Algorithms"));

        basic_concept.add(new DefaultMutableTreeNode(new BookInfo(
                "About Sextante", "/sextante_help/en/general/about.htm")));
        basic_concept.add(new DefaultMutableTreeNode(new BookInfo(
                "Introduction", "/sextante_help/en/general/intro.html")));
        basic_concept.add(new DefaultMutableTreeNode(new BookInfo(
                "OpenJUMP and Sextante data",
                "/sextante_help/en/general/openjump.html")));
        // Sextante_Toolbox
        tools.add(new DefaultMutableTreeNode(new BookInfo(toolbox,
                "/sextante_help/en/general/toolbox.html")));
        // AA Results
        tools.add(new DefaultMutableTreeNode(new BookInfo(results,
                "/sextante_help/en/general/results.html")));

        // Sextante History
        tools.add(new DefaultMutableTreeNode(new BookInfo(history,
                "/sextante_help/en/general/history.html")));

        // Sextante Modeler
        tools.add(new DefaultMutableTreeNode(new BookInfo(modeler,
                "/sextante_help/en/general/modeler.html")));

        // Sextante Command Line
        tools.add(new DefaultMutableTreeNode(new BookInfo(command_line,
                "/sextante_help/en/general/cmd.html")));
        // AA Explorer
        tools.add(new DefaultMutableTreeNode(new BookInfo(data_explorer,
                "/sextante_help/en/general/explorer.html")));
        // AA Pick coordinates
        tools.add(new DefaultMutableTreeNode(new BookInfo(coordinates,
                "/sextante_help/en/general/coordinates.html")));

        additional_information.add(new DefaultMutableTreeNode(new BookInfo(
                Sextante.getText("Batch_processing"),
                "/sextante_help/en/general/batch.html")));
        additional_information.add(new DefaultMutableTreeNode(new BookInfo(
                Sextante.getText("ConfiguringProviders"),
                "/sextante_help/en/general/providers.html")));

        algorithms.add(new DefaultMutableTreeNode(new BookInfo(
                "List of algotithms",
                "/sextante_help/en/general/sextante_algo.html")));

        top.add(basic_concept);
        top.add(tools);
        top.add(additional_information);
        top.add(algorithms);
        top.add(Sextante_Serial);

    }

    public static void createAndShowGUI(PlugInContext context) {

        for (JInternalFrame iFrame : context.getWorkbenchFrame()
                .getInternalFrames()) {
            if (iFrame instanceof SextanteHelpFrame) {
                if (!((SextanteHelpFrame) iFrame).isClosed()) {
                    iFrame.toFront();
                    return;
                }
            }
        }
        final SextanteHelpFrame frame = new SextanteHelpFrame(context);
        frame.setSize(1100, 600);
        frame.setTitle(help);
        context.getWorkbenchFrame().addInternalFrame(frame, false, true);
    }

    public static class SextanteHelpFrame extends DetachableInternalFrame {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SextanteHelpFrame(final PlugInContext context) {
            context.getLayerManager();
            HelpDialog newContentPane = new HelpDialog();

            add(newContentPane, BorderLayout.CENTER);
            setResizable(true);
            setClosable(true);
            setIconifiable(true);
            setName(help);
            setMaximizable(true);
            pack();
            setVisible(true);

        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    /*
     * public void setAlwaysOnTop(boolean b) { }
     */

    public static Icon getIcon() {

        return new ImageIcon(SextanteGUI.class.getClassLoader().getResource(
                "images/sextante.gif"));

    }

}
