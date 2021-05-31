import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

public class GUIStopwatch extends JPanel {
    // holds elapsed times in reverse order (since beginning or latest reset)
    private static final LinkedList<Long> elapsedTimes = new LinkedList<>();

    private static final Font timefont = new Font("Monospaced", Font.BOLD, 48);
    private static final int REFRESH_MILLIS = 16; // 62.5 r/sec
    private static final String DEF_DATA_STRING = "__:__.___";

    private static final DateFormat df = new SimpleDateFormat("mm:ss.SSS");
    private static final javax.swing.Timer timer
        = new javax.swing.Timer(REFRESH_MILLIS, null);

    private static final JLabel noteLabel
        = new JLabel("Press space or any alphanumeric to start/stop.");
    private static final JLabel timeLabel
        = new JLabel(df.format(0), JLabel.CENTER);

    private static final JLabel lAvgDesc    = new JLabel("Avg all:  ");
    private static final JLabel lAvg3Desc   = new JLabel("Avg 3:  ");
    private static final JLabel lAvg5Desc   = new JLabel("Avg 5:  ");
    private static final JLabel lAvg10Desc  = new JLabel("Avg 10:  ");
    private static final JLabel lMedDesc    = new JLabel("Median:  ");
    private static final JLabel lBestDesc   = new JLabel("Best:  ");
    private static final JLabel lWorstDesc  = new JLabel("Worst:  ");
    private static final JLabel lLastDesc   = new JLabel("Last:  ");
    private static final JLabel l3Of5Desc   = new JLabel("3 of 5:  ");
    private static final JLabel l10Of12Desc = new JLabel("10 of 12:  ");

    private static final JLabel lAvgData    = new JLabel(DEF_DATA_STRING);
    private static final JLabel lAvg3Data   = new JLabel(DEF_DATA_STRING);
    private static final JLabel lAvg5Data   = new JLabel(DEF_DATA_STRING);
    private static final JLabel lAvg10Data  = new JLabel(DEF_DATA_STRING);
    private static final JLabel lMedData    = new JLabel(DEF_DATA_STRING);
    private static final JLabel lBestData   = new JLabel(DEF_DATA_STRING);
    private static final JLabel lWorstData  = new JLabel(DEF_DATA_STRING);
    private static final JLabel lLastData   = new JLabel(DEF_DATA_STRING);
    private static final JLabel l3Of5Data   = new JLabel(DEF_DATA_STRING);
    private static final JLabel l10Of12Data = new JLabel(DEF_DATA_STRING);

    private static final JButton resetButton = new JButton("Reset");

    private long start;
    private boolean running;
    private static long lastRun;

    public GUIStopwatch() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // configure static timer, formatting, etc
        GUIStopwatch.df.setTimeZone(TimeZone.getTimeZone("GMT"));
        GUIStopwatch.timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIStopwatch.timeLabel.setText(df.format(e.getWhen() - start));
            }
        });

        GUIStopwatch.noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        GUIStopwatch.timeLabel.setFont(GUIStopwatch.timefont);
        GUIStopwatch.timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GUIStopwatch.resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        GUIStopwatch.resetButton.setFocusable(false);
        GUIStopwatch.resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { GUIStopwatch.reset(); }
        });

        // add things to JPanel
        this.add(Box.createVerticalStrut(10));
        this.add(GUIStopwatch.noteLabel);
        this.add(GUIStopwatch.timeLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(GUIStopwatch.getDataPanel());
        this.add(Box.createVerticalStrut(5));
        this.add(GUIStopwatch.resetButton);
        this.add(Box.createVerticalStrut(10));

        // timer starts (at 0) upon key release, stops on key press
        this.addKeyListener(new KeyListener() {
            public void keyPressed (KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isSpaceChar(c) || Character.isLetterOrDigit(c)))
                    return;
                if (!running) return;
                GUIStopwatch.timer.stop();
                GUIStopwatch.resetButton.setEnabled(true);
                long elapsed = e.getWhen() - start;
                GUIStopwatch.timeLabel.setText(df.format(elapsed));
                GUIStopwatch.updateData(elapsed);
            }
            public void keyReleased(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isSpaceChar(c) || Character.isLetterOrDigit(c)))
                    return;
                running = !running;
                if (!running) return;
                GUIStopwatch.timer.start();
                GUIStopwatch.resetButton.setEnabled(false);
                if (lastRun != 0)
                    GUIStopwatch.lLastData.setText(df.format(lastRun));
                start = e.getWhen();
                //char c = e.getKeyChar();
                GUIStopwatch.timeLabel.setText(df.format(0));
            }
            public void keyTyped   (KeyEvent e) { }
        });

        //this.setPreferredSize(new Dimension(320,240));
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private static JPanel getDataPanel() {
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));

        // 4 panels: avg, data, best, data
        JPanel avgPanel  = new JPanel();
        avgPanel.setLayout(new BoxLayout(avgPanel,  BoxLayout.Y_AXIS));
        JPanel avgData   = new JPanel();
        avgData.setLayout(new BoxLayout(avgData,   BoxLayout.Y_AXIS));
        JPanel bestPanel = new JPanel();
        bestPanel.setLayout(new BoxLayout(bestPanel, BoxLayout.Y_AXIS));
        JPanel bestData  = new JPanel();
        bestData.setLayout(new BoxLayout(bestData,  BoxLayout.Y_AXIS));

        lAvgDesc   .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lAvgData   .setAlignmentX(Component.LEFT_ALIGNMENT);
        lAvg3Desc  .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lAvg3Data  .setAlignmentX(Component.LEFT_ALIGNMENT);
        lAvg5Desc  .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lAvg5Data  .setAlignmentX(Component.LEFT_ALIGNMENT);
        lAvg10Desc .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lAvg10Data .setAlignmentX(Component.LEFT_ALIGNMENT);
        lMedDesc   .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lMedData   .setAlignmentX(Component.LEFT_ALIGNMENT);
        lBestDesc  .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lBestData  .setAlignmentX(Component.LEFT_ALIGNMENT);
        lWorstDesc .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lWorstData .setAlignmentX(Component.LEFT_ALIGNMENT);
        lLastDesc  .setAlignmentX(Component.RIGHT_ALIGNMENT);
        lLastData  .setAlignmentX(Component.LEFT_ALIGNMENT);
        l3Of5Desc  .setAlignmentX(Component.RIGHT_ALIGNMENT);
        l3Of5Data  .setAlignmentX(Component.LEFT_ALIGNMENT);
        l10Of12Desc.setAlignmentX(Component.RIGHT_ALIGNMENT);
        l10Of12Data.setAlignmentX(Component.LEFT_ALIGNMENT);

        avgPanel.add(lAvgDesc);
        avgData .add(lAvgData);
        avgPanel.add(lAvg3Desc);
        avgData .add(lAvg3Data);
        avgPanel.add(lAvg5Desc);
        avgData .add(lAvg5Data);
        avgPanel.add(lAvg10Desc);
        avgData .add(lAvg10Data);
        avgPanel.add(lMedDesc);
        avgData .add(lMedData);
        bestPanel.add(lBestDesc);
        bestData .add(lBestData);
        bestPanel.add(lWorstDesc);
        bestData .add(lWorstData);
        bestPanel.add(lLastDesc);
        bestData .add(lLastData);
        bestPanel.add(l3Of5Desc);
        bestData .add(l3Of5Data);
        bestPanel.add(l10Of12Desc);
        bestData .add(l10Of12Data);

        dataPanel.add(Box.createHorizontalStrut(10));
        dataPanel.add(avgPanel);
        dataPanel.add(Box.createHorizontalStrut(10));
        dataPanel.add(avgData);
        dataPanel.add(Box.createHorizontalStrut(25));
        dataPanel.add(bestPanel);
        dataPanel.add(Box.createHorizontalStrut(10));
        dataPanel.add(bestData);
        dataPanel.add(Box.createHorizontalStrut(10));

        dataPanel.setPreferredSize(new Dimension(320,100));
        return dataPanel;
    }

    /**
     * Performs one O(n) pass through all recorded times, except for
     *   a simple O(n log n) median at the end
     */
    private static void updateData(long elapsed) {
        // add to front of LinkedList. need to iterate by latest first
        GUIStopwatch.elapsedTimes.addFirst(Long.valueOf(elapsed));

        int index = 0; // eventually, GUIStopwatch.elapsedTimes.size();
        long numer = 0L; // numerator for dividing by index to find averages
        long bestTime = elapsed, worstTime = elapsed;

        for (Long l : GUIStopwatch.elapsedTimes) {
            numer += l;
            bestTime = Math.min(bestTime, l);
            worstTime = Math.max(worstTime, l);

            if (index == 2) // seen 3, calculate avg3
                GUIStopwatch.lAvg3Data.setText(df.format(numer / (index + 1)));

            if (index == 4) { // seen 5, calculate avg5 3of5
                GUIStopwatch.lAvg5Data.setText(df.format(numer / (index + 1)));
                GUIStopwatch.l3Of5Data.setText(
                    df.format((numer - bestTime - worstTime) / 3));
            }

            if (index == 9) // seen 10, calculate avg10
                GUIStopwatch.lAvg10Data.setText(df.format(numer / (index + 1)));

            if (index == 11) { // seen 12, calculate 10of12
                GUIStopwatch.l10Of12Data.setText(
                    df.format((numer - bestTime - worstTime) / 10));
            }

            ++index;
        }

        if (index <= 3) // seen 3 or less, then always calc avg3 anyway
            GUIStopwatch.lAvg3Data.setText(df.format(numer / index));
        if (index <= 5) // seen 5 or less, then always calc avg5 anyway
            GUIStopwatch.lAvg5Data.setText(df.format(numer / index));
        if (index <= 10) // seen 10 or less, then always calc avg10 anyway
            GUIStopwatch.lAvg10Data.setText(df.format(numer / index));

        GUIStopwatch.lastRun = elapsed;
        GUIStopwatch.lAvgData.setText(df.format(numer / index));
        GUIStopwatch.lBestData.setText(df.format(bestTime));
        GUIStopwatch.lWorstData.setText(df.format(worstTime));

        /* Using the simple O(n log n) median alg due to list brevity
         *   despite that rest of method is O(n) optimized */
        ArrayList<Long> sortedList = new ArrayList<>(index);
        sortedList.addAll(GUIStopwatch.elapsedTimes);
        Collections.sort(sortedList);

        long median = index % 2 == 1 ? sortedList.get(index/2)
            : (sortedList.get(index/2 - 1) + sortedList.get(index/2)) / 2;
        GUIStopwatch.lMedData.setText(df.format(median));
    }

    private static void reset() {
        GUIStopwatch.timer.stop();
        GUIStopwatch.elapsedTimes.clear();
        GUIStopwatch.lastRun = 0L;
        GUIStopwatch.timeLabel  .setText(df.format(0));
        GUIStopwatch.lAvgData   .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lAvg3Data  .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lAvg5Data  .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lAvg10Data .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lMedData   .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lBestData  .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lWorstData .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.l3Of5Data  .setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.l10Of12Data.setText(GUIStopwatch.DEF_DATA_STRING);
        GUIStopwatch.lLastData  .setText(GUIStopwatch.DEF_DATA_STRING);
    }

    public static final void main(final String[] args) {
        final JFrame jf = new JFrame("GUIStopwatch");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(new GUIStopwatch());
        jf.pack();
        jf.setVisible(true);
    }

}
