package org.etf.coronacity.gui.tab;

import org.etf.coronacity.gui.component.GraphView;
import org.etf.coronacity.helper.Colors;
import org.etf.coronacity.helper.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
 * This class is JPanel for JTabbedPane in StatisticsWindow.java class
 * Contains two graph components (GraphView.java class) arranged horizontally
 * Graphs contains data for male and female respectively
 */
public class GenderGraphTab extends JPanel {

    private static final String TEXT_LABEL_MALE = "Muskarci";
    private static final String TEXT_LABEL_FEMALE = "Zene";

    public GenderGraphTab(ArrayList<Long> xValues, ArrayList<Long> yValuesMale, ArrayList<Long> yValuesFemale, boolean infected) {
        initComponents(xValues, yValuesMale, yValuesFemale, infected);
    }

    //
    // private
    //

    /**
     * Set layout and place components on the screen
     */
    private void initComponents(ArrayList<Long> xValues, ArrayList<Long> yValuesMale, ArrayList<Long> yValuesFemale, boolean infected) {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(this);
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //
        // init components
        //

        GraphView maleGraphView =
                new GraphView(
                        xValues,
                        yValuesMale,
                        infected ? Colors.COLOR_GRAPH_INFECTED : Colors.COLOR_GRAPH_RECOVERED
                );
        GraphView femaleGraphView =
                new GraphView(
                        xValues,
                        yValuesFemale,
                        infected ? Colors.COLOR_GRAPH_INFECTED : Colors.COLOR_GRAPH_RECOVERED
                );

        JLabel maleLabel = new JLabel(TEXT_LABEL_MALE);
        maleLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel femaleLabel = new JLabel(TEXT_LABEL_FEMALE);
        femaleLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        //

        // horizontal
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(maleGraphView)
                                .addComponent(maleLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(femaleGraphView)
                                .addComponent(femaleLabel)
                        )
        );

        // vertical
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(maleGraphView)
                                .addComponent(femaleGraphView)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(maleLabel)
                                .addComponent(femaleLabel)
                        )
        );
    }
}