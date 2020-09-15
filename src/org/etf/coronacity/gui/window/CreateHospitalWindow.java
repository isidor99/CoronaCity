package org.etf.coronacity.gui.window;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Dimensions;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.building.Hospital;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/*
    Creating new hospital
 */
public class CreateHospitalWindow extends JFrame {

    private JTextField nameTextField;
    private JButton createButton;
    private int residents;

    private Consumer<Hospital> hospitalCreateListener;

    private static final String TITLE = "Nova bolnica";

    private static final String TEXT_LABEL_WINDOW = "Kreiranje nove bolnice";
    private static final String TEXT_LABEL_NAME = "Ime bolnice";
    private static final String TEXT_LABEL_CREATE = "Kreiraj";

    private static final String ERROR_MESSAGE = "Morate unijeti ime bolnice";

    public CreateHospitalWindow(int residents) {
        super();

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

    /**
     * All components and layout manager are created here and set properly
     */
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
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Dimensions.TITLE_FONT_SIZE));

        JLabel nameLabel = new JLabel(TEXT_LABEL_NAME);

        nameTextField = new JTextField();
        createButton = new JButton(TEXT_LABEL_CREATE);
        //


        // horizontal group
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(nameLabel)
                                .addComponent(nameTextField, Dimensions.DEFAULT_WIDTH, Dimensions.DEFAULT_WIDTH, Dimensions.DEFAULT_WIDTH)
                        )
                        .addComponent(createButton)
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(nameLabel)
                                .addComponent(nameTextField)
                        )
                        .addComponent(createButton)
        );

        setTitle(TITLE);
        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Add action listeners for option buttons
     */
    private void setListeners() {

        createButton.addActionListener(event -> {

            String name = nameTextField.getText();

            if (!name.isEmpty()) {

                int capacity = Utils.getCapacityFromResidentsNumber(residents);

                Hospital hospital = new Hospital(name, capacity);
                hospitalCreateListener.accept(hospital);
                dispose();

            } else
                // show dialog
                JOptionPane.showMessageDialog(
                        this,
                        ERROR_MESSAGE,
                        Constants.ERROR_MESSAGE,
                        JOptionPane.ERROR_MESSAGE
                );
        });
    }
}
