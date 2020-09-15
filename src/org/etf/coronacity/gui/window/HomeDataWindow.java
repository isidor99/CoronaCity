package org.etf.coronacity.gui.window;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Dimensions;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.person.Person;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;

/*
    Show home data
        - home id
        - home position (row and column)
        - persons who live in this home
 */
public class HomeDataWindow extends JFrame {

    private AppData appData;
    private long homeId;


    //
    private static final String TITLE = "Kuca";

    private static final String TEXT_LABEL_WINDOW = "Podaci o kuci";
    private static final String TEXT_LABEL_ID = "Id: ";
    private static final String TEXT_LABEL_PERSONS = "Ukucani";

    public HomeDataWindow(AppData appData, long homeId) {

        this.appData = appData;
        this.homeId = homeId;

        initComponents();
    }

    //
    // private

    /**
     * Set layout and place components on the screen
     */
    private void initComponents() {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);


        String idText = TEXT_LABEL_ID + homeId;
        String positionText =
                "X: " + (appData.getBuildings().get(homeId).getPositionX() + 1) +
                        ", Y: " + (appData.getBuildings().get(homeId).getPositionY() + 1);
        String personsText = TEXT_LABEL_PERSONS + " " + ((Home) appData.getBuildings().get(homeId)).getHosts();

        ArrayList<Person> persons = Utils.getHostsForHome(appData.getPersons(), homeId);

        //
        // init components
        //

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Dimensions.TITLE_FONT_SIZE));

        JLabel idLabel = new JLabel(idText);
        JLabel positionLabel = new JLabel(positionText);
        JLabel personsLabel = new JLabel(personsText);

        JPanel personsPanel = new JPanel(new GridLayout(persons.size(), 1, 0, 8));

        persons.forEach(person -> {

            JLabel label = new JLabel(person.getId() + ", " + person.getName() + " " + person.getSurname() + ", " + person.getBirthYear());
            label.setBorder(
                    new CompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createEmptyBorder()
                    )
            );

            personsPanel.add(label);
        });

        JScrollPane scrollPane = new JScrollPane(personsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //

        // horizontal group
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addComponent(idLabel)
                        .addComponent(positionLabel)
                        .addComponent(personsLabel)
                        .addComponent(scrollPane)
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(idLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(positionLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(personsLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane)
        );

        // pack();
        setTitle(TITLE);
        setSize(new Dimension(Dimensions.MAX_SMALL_WINDOW_SIZE, Dimensions.MAX_SMALL_WINDOW_SIZE));
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }
}
