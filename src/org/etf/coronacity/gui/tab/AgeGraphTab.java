package org.etf.coronacity.gui.tab;

import org.etf.coronacity.gui.component.GraphView;
import org.etf.coronacity.helper.Colors;
import org.etf.coronacity.helper.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AgeGraphTab extends JPanel {

    private static final String TEXT_LABEL_CHILD = "Djeca";
    private static final String TEXT_LABEL_ADULT = "Odrasli";
    private static final String TEXT_LABEL_OLD = "Stari";

    public AgeGraphTab(ArrayList<Long> xValues, ArrayList<Long> yValuesChild, ArrayList<Long> yValuesAdult, ArrayList<Long> yValuesOld, boolean infected) {
        initComponents(xValues, yValuesChild, yValuesAdult, yValuesOld, infected);
    }

    //
    // private
    //

    private void initComponents(ArrayList<Long> xValues, ArrayList<Long> yValuesChild, ArrayList<Long> yValuesAdult, ArrayList<Long> yValuesOld, boolean infected) {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(this);
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //
        // init components
        //
        GraphView childGraphView =
                new GraphView(
                        xValues,
                        yValuesChild,
                        infected ? Colors.COLOR_GRAPH_INFECTED : Colors.COLOR_GRAPH_RECOVERED
                );
        GraphView adultGraphView =
                new GraphView(
                        xValues,
                        yValuesAdult,
                        infected ? Colors.COLOR_GRAPH_INFECTED : Colors.COLOR_GRAPH_RECOVERED
                );
        GraphView oldGraphView =
                new GraphView(
                        xValues,
                        yValuesOld,
                        infected ? Colors.COLOR_GRAPH_INFECTED : Colors.COLOR_GRAPH_RECOVERED
                );

        JLabel childLabel = new JLabel(TEXT_LABEL_CHILD);
        childLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel adultLabel = new JLabel(TEXT_LABEL_ADULT);
        adultLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel oldLabel = new JLabel(TEXT_LABEL_OLD);
        oldLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));
        //
        //
        //

        // horizontal
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(childGraphView)
                                .addComponent(childLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(adultGraphView)
                                .addComponent(adultLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(oldGraphView)
                                .addComponent(oldLabel)
                        )
        );

        // vertical
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(childGraphView)
                                .addComponent(childLabel)
                        )
                        .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(adultGraphView)
                                .addComponent(adultLabel)
                        )
                        .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(oldGraphView)
                                .addComponent(oldLabel)
                        )
        );
    }
}
