package org.etf.coronacity.gui.window;

import org.etf.coronacity.gui.component.HospitalView;
import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.model.building.Hospital;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class HospitalsWindow extends JFrame {

    private ArrayList<Hospital> hospitals;
    private int residents;

    private JButton addHospitalButton;
    private JPanel hospitalsPanel;

    private Consumer<Hospital> hospitalCreateListener;

    private static final String TEXT_LABEL_WINDOW = "Pregled stanja bolnica";
    private static final String TEXT_LABEL_ADD_HOSPITAL = "Kreiraj novu bolnicu";

    public HospitalsWindow(ArrayList<Hospital> hospitals, int residents) {
        super();

        this.hospitals = hospitals;
        this.residents = residents;

        initComponents();
        setListeners();
    }

    public void setHospitalCreateListener(Consumer<Hospital> hospitalCreateListener) {
        this.hospitalCreateListener = hospitalCreateListener;
    }

    //
    // private
    //
    private void initComponents() {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //
        // init components
        //

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Constants.DIMENSION_TITLE_FONT_SIZE));

        hospitalsPanel = new JPanel();
        addHospitalButton = new JButton(TEXT_LABEL_ADD_HOSPITAL);

        createComponentsForHospital();

        //

        // horizontal group
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addComponent(hospitalsPanel)
                        .addComponent(addHospitalButton)
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addComponent(hospitalsPanel)
                        .addComponent(addHospitalButton)
        );

        //

        checkHospitals();
        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void createComponentsForHospital() {

        hospitalsPanel.setLayout(new GridLayout(1 + hospitals.size() / 4, 4));
        hospitals.forEach(hospital -> hospitalsPanel.add(new HospitalView(hospital)));
    }

    private void setListeners() {

        addHospitalButton.addActionListener(event -> {

            // open window to add new hospital
            CreateHospitalWindow createHospitalWindow = new CreateHospitalWindow(residents);
            createHospitalWindow.setHospitalCreateListener(hospital -> {

                // when new hospital is created
                hospitalsPanel.add(new HospitalView(hospital));
                hospitalCreateListener.accept(hospital);
                pack();
            });

        });
    }

    private void checkHospitals() {

        boolean full = hospitals.stream().anyMatch(hospital -> hospital.getCapacity() == hospital.getInfectedCount());

        if (!full)
            addHospitalButton.setVisible(false);
    }
}
