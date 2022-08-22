//importing the java packages and their methods
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//PhoneBook class
public class PhoneBook extends JFrame {

    //    declaring variables globally for table and user data
    JTable table;
    DefaultTableModel model;
    JPanel dataTable;

    JTextField firstNameTxt, secondNameTxt, phoneNo;
    JCheckBox privateNo;
    JRadioButton foreSurName, surForeName;
    JButton clearBtn, updateBtn, addBtn, removeBtn;
    String format;
    Connection connect;
    PhoneBook self = this; //the PhoneBook stored in variable self

    /**
     * PhoneBook constructor to set a frame
     */
    public PhoneBook(){
        setVisible(true);
        setTitle("Phone Book");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(menuBar()); //setting the menu bar to the frame
        setLayout(new GridLayout(1, 2)); //creating grid layout with 1 row and 2 columns
        add(dataTable()); //adding the table to the first column of the frame
        add(mainPanel()); //adding the main panel to the second column

        setResizable(false);

        pack();
        setLocationRelativeTo(null); //to show the GUI in the center of the screen

        try{
            //connecting the database and storing the connection code in a variable
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?useTimezone=true&serverTimezone=UTC", "root", "");
        } catch (SQLException except){
            //message to show in case the database server is not started
            JOptionPane.showMessageDialog(self,
                    "The DataBase Server has not been started. " +
                            "\nThe data you enter are not going to stored." +
                            "\n\nPlease start your DB server to store the user data.",
                    "DataBase Server NOT Started",
                    JOptionPane.WARNING_MESSAGE);
        }

        //to show the data from the server in the table
        reloadTableData();
    }


    /**
     * insert method to insert the user input data in the database table
     * @param firstName - the first name from user input
     * @param secondName - the second name from user input
     * @param phone - the phone number
     * @param privacy - the checkbox value
     */
    private void insert(String firstName, String secondName, long phone, String privacy){
        //writing the query to insert the values to database table
        String query = "INSERT INTO phone_details (first_name, second_name, phone, privacy) VALUES (?, ?, ?, ?)";

        //passing the parameter values to the query
        try {
            PreparedStatement state = connect.prepareStatement(query); //connecting to the database
            state.setString(1, firstName);
            state.setString(2, secondName);
            state.setLong(3, phone);
            state.setString(4, privacy);
            state.executeUpdate(); //executing the query
            state.close(); //closing the connection
        } catch (SQLException except) {
            except.printStackTrace();
        }
    }

    /**
     * method to fetch the data from the database server and show it in the application table
     * This method is called in the beginning and every time any action is performed on the table
     */
    private void reloadTableData(){
        //query to get data from the database table
        String query = "SELECT * from phone_details";

        //putting the values from the database to the application table
        try {
            PreparedStatement state = connect.prepareStatement(query); //connecting to the database
            ResultSet result = state.executeQuery(); //executing the query

            while(result.next()){
                model.addRow(new Object[]{
                    result.getString("first_name"),
                    result.getString("second_name"),
                    result.getLong("phone"),
                    result.getString("privacy"),
                });
            }
            state.close(); //closing the connection
        } catch (SQLException except) {
            except.printStackTrace();
        }
    }

    /**
     * method to update the already existing record in the table
     * @param firstName - the new first name that updates the old value
     * @param secondName - the new second name that updates the old value
     * @param phone - the new phone number
     * @param privacy - the new privacy checking
     * @param oldPhone - the old phone number from the table for the database query (to provide which row has to be updated)
     */
    private void update(String firstName, String secondName, long phone, String privacy, long oldPhone){
        //database query to update the existing data
        String query = "UPDATE phone_details SET first_name = ?, second_name = ?, phone = ?, privacy = ? WHERE phone = ?";

        //passing the parameter values to the query
        try {
            PreparedStatement state = connect.prepareStatement(query); //connecting to the database
            state.setString(1, firstName);
            state.setString(2, secondName);
            state.setLong(3, phone);
            state.setString(4, privacy);
            state.setLong(5, oldPhone);
            state.executeUpdate(); //executing the query
            state.close(); //closing the connection
        } catch (SQLException except) {
            except.printStackTrace();
        }
    }

    /**
     * method to remove the row from the application and database table
     * @param phone - phone number to pass to the query to delete the row that has the phone number
     */
    private void remove( long phone ){
        //database query to delete the row from the database table
        String query = "DELETE FROM phone_details WHERE phone = ?";

        //passing the parameter value to the query and executing the query
        try {
            PreparedStatement state = connect.prepareStatement(query); //connecting to the database
            state.setLong(1, phone);
            state.executeUpdate();
            state.close(); //closing the connection
        } catch (SQLException except) {
            except.printStackTrace();
        }
    }


    /**
     * menuBar method for the menu at the top of the frame
     * @return menuBar object
     */
    private JMenuBar menuBar(){
        JMenuBar menuBar = new JMenuBar();

        //the menus in the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        //creating the sub menus

        //exit sub menu for the file menu
        JMenuItem exitMenu = new JMenuItem("Exit");
        fileMenu.add(exitMenu); //adding exit submenu to the file menu

        //submenus for the edit menu
        JMenuItem clearMenu = new JMenuItem("Clear");
        JMenuItem updateMenu = new JMenuItem("Update");
        JMenuItem addMenu = new JMenuItem("Add");
        JMenuItem removeMenu = new JMenuItem("Remove");
        //adding the clear, update, add and remove submenus to the edit menu
        editMenu.add(clearMenu);
        editMenu.add(updateMenu);
        editMenu.addSeparator(); // adding a separator in the middle
        editMenu.add(addMenu);
        editMenu.add(removeMenu);

        JMenuItem aboutMenu = new JMenuItem("About");
        helpMenu.add(aboutMenu);


//        shortcuts for the menus
        fileMenu.setMnemonic(KeyEvent.VK_F); //CTRL + F for file menu
        editMenu.setMnemonic(KeyEvent.VK_E); //CTRL + E for edit menu
        helpMenu.setMnemonic(KeyEvent.VK_H); //CTRL + H for help menu

//        adding action listener and creating shortcuts for the menu items

        //exit menu closes the application
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //shortcut to exit - CTRL + x
        exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));

        //clear sub menu does the same as the clear button (clears the values from the fields and sets to default)
        clearMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBtn.doClick();
            }
        });
        //shortcut to clear - CTRL + C
        clearMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));

        //update sub menu does same as the update button (updates the values in the fields)
        updateMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBtn.doClick();
            }
        });
        //shortcut to update - CTRL + U
        updateMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));

        //add sub menu does same as the add button (adds the user entry to the table)
        addMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBtn.doClick();
            }
        });
        //shortcut to add - CTRL + A
        addMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));

        //remove sub menu does same as the remove button (removes the row from the table)
        removeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBtn.doClick();
            }
        });
        //shortcut to remove - CTRL + R
        removeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));

        //about sub menu shows a dialog box with a message
        aboutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(self,
                        "It is still a trial version!!",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //shortcut for about sub menu - CTRL + B
        aboutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK));

//        adding the menus in the menuBar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }


    /**
     * the right side Panel (mainPanel) which contains all the other sub-panels
     * @return mainPanel object
     */
    private JPanel mainPanel(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1)); //use of grid layout to create 3 rows and 1 column
        //adding the panels to the main panel
        mainPanel.add(infoPanel());
        mainPanel.add(fileAsPanel());
        mainPanel.add(buttonsPanel());

        return mainPanel;
    }


    /**
     * infoPanel for taking the user data
     * @return infoPanel object
     */
    private JPanel infoPanel(){
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 2)); //use of grid layout
        infoPanel.setBorder(BorderFactory.createTitledBorder("Info:")); //setting a title for the panel

        infoPanel.add(new JLabel("First Name"));
        firstNameTxt = new JTextField(20);
        infoPanel.add(firstNameTxt);
        infoPanel.add(new JLabel("Second Name"));
        secondNameTxt = new JTextField(20);
        infoPanel.add(secondNameTxt);
        infoPanel.add(new JLabel("Phone"));
        phoneNo = new JTextField(20);
        infoPanel.add(phoneNo);
        privateNo =  new JCheckBox("Private");
        infoPanel.add(privateNo);

        return infoPanel;
    }

    /**
     * fileAsPanel to alter the format of the table
     * @return fileAsPanel object
     */
    private JPanel fileAsPanel(){
        JPanel fileAsPanel = new JPanel();
        fileAsPanel.setLayout(new GridLayout(2, 1)); //use of grid layout
        fileAsPanel.setBorder(BorderFactory.createTitledBorder("File As:")); //setting title for the panel

        //creating the radio buttons
        foreSurName = new JRadioButton("Forename, Surname");
        surForeName = new JRadioButton("Surname, Forename");

//        adding the JRadioButtons in a group
        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add(foreSurName);
        btnGrp.add(surForeName);
        fileAsPanel.add(foreSurName);
        fileAsPanel.add(surForeName);

//        making the foreName-SurName format as default and disabling it
        foreSurName.setSelected(true);
        foreSurName.setEnabled(false);

//        action listener to the JRadioButtons so that the selected one is disabled
        foreSurName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foreSurName.setEnabled(false);
                surForeName.setEnabled(true);
                table.moveColumn(0,1); //the columns are interchanged in the table
            }
        });

        surForeName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surForeName.setEnabled(false);
                foreSurName.setEnabled(true);
                table.moveColumn(0,1); //the columns are interchanged in the table
            }
        });

        return fileAsPanel;
    }

    /**
     * buttonsPanel which has the "Clear", "Search", "Add" and "Remove" buttons for the Phone Book functionality
     * @return buttonsPanel object
     */
    private JPanel buttonsPanel(){
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 2)); //use of grid layout

//        making the JButtons
        clearBtn = new JButton("Clear");
        updateBtn = new JButton("Update");
        addBtn = new JButton("Add");
        removeBtn = new JButton("Remove");

//        adding the JButtons to the buttonsPanel
        buttonsPanel.add(clearBtn);
        buttonsPanel.add(updateBtn);
        buttonsPanel.add(addBtn);
        buttonsPanel.add(removeBtn);


//        adding action listener to the buttons to make them functional

        //clearButton clears all the use input from the fields
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstNameTxt.setText("");
                secondNameTxt.setText("");
                phoneNo.setText("");
                privateNo.setSelected(false);
            }
        });


        //search buttons just shows a JOptionPane with an information message
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(self,
                            "Please select a row to update.",
                            "Row not selected.",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    //getting the old phone number from the table to pass the value to databse query
                    long oldPhone = Long.parseLong(model.getValueAt(selectedRow, 2).toString());

                    //the new updated values
                    String firstName = firstNameTxt.getText();
                    String secondName = secondNameTxt.getText();
                    long phone = Long.parseLong(phoneNo.getText());
                    String privacy = "";
                    if(privateNo.isSelected()) {
                        privacy = "Private";
                    }

                    //passing the values to the update method
                    update(firstName, secondName, phone, privacy, oldPhone);
                    clearBtn.doClick();
                    //updating the new data on the table
                    model.setRowCount(0);
                    reloadTableData();
                }
            }
        });

        //addButton adds the user input to the table in a new row
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    //getting the values from the text fields
                    String firstName = firstNameTxt.getText();
                    String secondName = secondNameTxt.getText();
                    String Number = phoneNo.getText();

                    //if block to warn if all the blocks are not filled
                    if(firstName.isEmpty() || secondName.isEmpty() || Number.isEmpty()){
                        JOptionPane.showMessageDialog(self,
                                "Please fill all of the fields.",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    } else { //else block to proceed if all the fields are filled
                        long phoneNumber = 0;
                        phoneNumber = Long.parseLong(phoneNo.getText()); //the phone Number changed to long format from string
                        String privacy = ""; //privacy set to empty if not selected

                        if(privateNo.isSelected()) {
                            privacy = "Private";
                        }

                        //confirming the length of the phoneNumber to be of 10 digits and showing an error message if not
                        if(String.valueOf(phoneNumber).length() != 10){
                            JOptionPane.showMessageDialog(self,
                                    "The phone number you entered does not have 10 digits.",
                                    "Invalid Phone Number",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            //adding the data to the table if all conditions are satisfied
                            insert(firstName, secondName, phoneNumber, privacy);
                            //reloading the table
                            model.setRowCount(0);
                            reloadTableData();
                            //clearing the fields
                            clearBtn.doClick();
                        }
                    }

                    //catch block to catch exception if the entered Phone Number is not in numeric format
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(self,
                            "Please enter phone number in correct format.",
                            "Invalid Phone Number",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

//        adding mouse listener to the removeButton as row has to be selected from table
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                //condition if no row is selected
                if (selectedRow == -1){
                    JOptionPane.showMessageDialog(self,
                            "Please select a row to be removed.",
                            "Row not selected.",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    //else asking for confirmation
                    int confirmation = JOptionPane.showConfirmDialog(self,
                            "Are you sure that you want to delete row?",
                            "Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    //condition to check if "Yes" option is chosen and remove the row if chosen
                    if (confirmation == JOptionPane.YES_OPTION) {
                        //getting the old phone number from the table and passing the value to the query
                        long oldPhone = Long.parseLong(model.getValueAt(selectedRow, 2).toString());
                        remove(oldPhone);
                        //reloading the table
                        model.setRowCount(0);
                        reloadTableData();
                        //clearing the fields
                        clearBtn.doClick();
                    }
                }
            }
        });

        return buttonsPanel;
    }

    /**
     * dataTable where all the user data are stored
     * @return dataTable object
     */
    private JPanel dataTable(){
        dataTable = new JPanel();
        dataTable.setBorder(BorderFactory.createTitledBorder("Name:")); //setting table border
        model = new DefaultTableModel(); //creating a default model for the table
        //setting the column headings of the table
        model.setColumnIdentifiers(new Object[]{"First Name", "Second Name", "Phone", "Private"});

        table = new JTable(model);

//        adding scrollPane to the dataTable
        JScrollPane scrollPane = new JScrollPane(table);
        dataTable.add(scrollPane);

//        adding mouse listener to the dataTable to know which row is selected
        table.addMouseListener(new MouseListener() {

            //method to get the value of each column of selected row and put it into the infoPanel
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                firstNameTxt.setText(model.getValueAt(selectedRow, 0).toString());
                secondNameTxt.setText(model.getValueAt(selectedRow, 1).toString());
                phoneNo.setText(model.getValueAt(selectedRow, 2).toString());
                String privateNumber = model.getValueAt(selectedRow, 3).toString();
                privateNo.setSelected(!privateNumber.equals(""));
            }

            //overridden methods from the mouseListener Interface
            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });

        return dataTable;
    }

    /**
     * the main method to run the program
     */
    public static void main(String[] args) {
        new PhoneBook(); //calling the PhoneBook constructor
    }
}
