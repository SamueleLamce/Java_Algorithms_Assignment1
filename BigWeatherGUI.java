import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public class BigWeatherGUI extends JFrame {

    private JButton loadFileButton;
    private JTextArea outputArea;
    private GraphPanel graphPanel;

    private BigWeatherSolver.MyListOfLists adjacencyList;
    private BigWeatherSolver.MyListOfLists components;

    public BigWeatherGUI() {
        super("BigWeatherSolver Visualization");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        loadFileButton = new JButton("Load Input File");
        topPanel.add(loadFileButton);
        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        graphPanel = new GraphPanel();
        graphPanel.setBackground(Color.WHITE);
        add(graphPanel, BorderLayout.CENTER);

        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                runBackendAndVisualize(file);
            }
        });
    }

    private void runBackendAndVisualize(File file) {
        try {
            String backendOutput = BigWeatherSolver.solve(file);
            outputArea.setText(backendOutput);

            this.adjacencyList = BigWeatherSolver.adjacencyList;
            this.components = BigWeatherSolver.components;

            graphPanel.setGraph(adjacencyList, components);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "File not found: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BigWeatherGUI gui = new BigWeatherGUI();
            gui.setVisible(true);
        });
    }

    class GraphPanel extends JPanel {

        private BigWeatherSolver.MyListOfLists adjacencyList;
        private BigWeatherSolver.MyListOfLists components;

        public void setGraph(BigWeatherSolver.MyListOfLists adjacencyList, BigWeatherSolver.MyListOfLists components) {
            this.adjacencyList = adjacencyList;
            this.components = components;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (adjacencyList == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (components == null || components.size() == 0) {

                int n = adjacencyList.size() - 1;
                if (n <= 0) return;

                int spacingX = panelWidth / (n + 1);
                int y = panelHeight / 2;
                int nodeDiameter = 30;

                for (int i = 1; i <= n; i++) {
                    int x = spacingX * i;
                    g2.setColor(Color.ORANGE);
                    g2.fillOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);

                    String label = String.valueOf(i);
                    FontMetrics fm = g2.getFontMetrics();
                    int labelWidth = fm.stringWidth(label);
                    int labelHeight = fm.getAscent();
                    g2.drawString(label, x - labelWidth / 2, y + labelHeight / 2);
                }

            } else {
                int compCount = components.size();
                int spacingX = panelWidth / (compCount + 1);

                for (int i = 0; i < compCount; i++) {
                    BigWeatherSolver.MyList component = components.get(i);
                    if (component.size() == 0) continue;

                    int root = component.get(0);
                    int startX = spacingX * (i + 1);
                    int startY = 80;

                    Set<Integer> visited = new HashSet<>();
                    drawTree(g2, root, startX, startY, panelWidth / 6, visited, root);
                }
            }
        }

        private void drawTree(Graphics2D g2, int current, int x, int y, int xSpacing, Set<Integer> visited, int root) {
            visited.add(current);

            int nodeDiameter = 30;

            boolean isBucket = (current == root);

            g2.setColor(isBucket ? Color.RED : Color.ORANGE);
            g2.fillOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);

            g2.setColor(Color.BLACK);
            g2.drawOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);

            String label = String.valueOf(current);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();
            g2.drawString(label, x - labelWidth / 2, y + labelHeight / 2);

            BigWeatherSolver.MyList neighbors = adjacencyList.get(current);
            int childrenCount = 0;
            for (int i = 0; i < neighbors.size(); i++) {
                int neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    childrenCount++;
                }
            }

            int drawnChildren = 0;
            for (int i = 0; i < neighbors.size(); i++) {
                int neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    drawnChildren++;
                    int childX = x - xSpacing / 2 * (childrenCount - 1) + xSpacing * (drawnChildren - 1);
                    int childY = y + 100;

                    g2.setColor(Color.BLUE);
                    g2.drawLine(x, y, childX, childY);

                    drawTree(g2, neighbor, childX, childY, xSpacing / 2, visited, root);
                }
            }
        }
    }
}
