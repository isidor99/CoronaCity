package org.etf.coronacity.gui.component;

import org.etf.coronacity.model.building.Hospital;

import javax.swing.*;

public class HospitalView extends JComponent {

    private Hospital hospital;


    private static final String TEXT_LABEL_CAPACITY = "Kapacitet";
    private static final String TEXT_LABEL_OCCUPIED = "Popunjeno";

    private static final String FILE_PATH_CAPACITY_ICON = "./res/icons/icon_capacity.png";
    private static final String FILE_PATH_OCCUPIED_ICON = "./res/icons/icon_occupied.png";

    public HospitalView(Hospital hospital) {

        this.hospital = hospital;

        initComponents();
    }

    //
    // private
    //
    private void initComponents() {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(this);
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //
        // init components
        //

        ImageIcon capacityIcon = new ImageIcon(FILE_PATH_CAPACITY_ICON);
        ImageIcon occupiedIcon = new ImageIcon(FILE_PATH_OCCUPIED_ICON);

        JLabel hospitalNameLabel = new JLabel(hospital.getName(), SwingConstants.CENTER);
        JLabel capacityIconLabel = new JLabel(capacityIcon);
        JLabel capacityLabel = new JLabel(TEXT_LABEL_CAPACITY);
        JLabel capacityTextLabel = new JLabel(String.valueOf(hospital.getCapacity()));
        JLabel occupiedIconLabel = new JLabel(occupiedIcon);
        JLabel occupiedLabel = new JLabel(TEXT_LABEL_OCCUPIED);
        JLabel occupiedTextLabel = new JLabel(String.valueOf(hospital.getInfectedCount()));

        //

        // horizontal group
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(hospitalNameLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(capacityIconLabel)
                                        .addComponent(capacityLabel)
                                        .addComponent(capacityTextLabel)
                                )
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(occupiedIconLabel)
                                        .addComponent(occupiedLabel)
                                        .addComponent(occupiedTextLabel)
                                )
                        )
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(hospitalNameLabel)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(capacityIconLabel)
                                .addComponent(occupiedIconLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(capacityLabel)
                                .addComponent(occupiedLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(capacityTextLabel)
                                .addComponent(occupiedTextLabel)
                        )
        );
    }
}
