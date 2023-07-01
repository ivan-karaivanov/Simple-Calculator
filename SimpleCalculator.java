import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimpleCalculator implements ActionListener {

    JFrame frame;
    JTextField inputField;
    JButton restartButton;
    JButton deleteButton; // New delete button

    StringBuilder calculation = new StringBuilder(); // Store the calculation as a string

    public SimpleCalculator() {
        frame = new JFrame("Simple Calculator");
        frame.setSize(350, 380);
        frame.setLayout(new BorderLayout());

        inputField = new JTextField(20);
        inputField.setFont(new Font("Arial", Font.BOLD, 20));
        inputField.setHorizontalAlignment(JTextField.RIGHT);
        inputField.addActionListener(this); // Add ActionListener to handle "Enter" key press
        frame.add(inputField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        String[] buttonLabels = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+"};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 9));
        restartButton.addActionListener(this);
        buttonPanel.add(restartButton);

        deleteButton = new JButton("Delete"); // Create delete button
        deleteButton.setFont(new Font("Arial", Font.BOLD, 9));
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton); // Add delete button to button panel

        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JTextField) {
            calculateResult();
        } else {
            String input = inputField.getText();

            if (e.getActionCommand().equals("C")) {
                inputField.setText("");
                calculation.setLength(0);
            } else if (e.getActionCommand().equals("=")) {
                calculateResult();
            } else if (e.getActionCommand().equals("Restart")) {
                inputField.setText("");
                calculation.setLength(0);
            } else if (e.getActionCommand().equals("Delete")) { // Handle delete button action
                if (!input.isEmpty()) {
                    input = input.substring(0, input.length() - 1);
                    calculation.setLength(calculation.length() - 1);
                    inputField.setText(input);
                }
            } else {
                input += e.getActionCommand();
                calculation.append(e.getActionCommand());
                inputField.setText(input);
            }
        }
    }

    private void calculateResult() {
        try {
            double result = eval(calculation.toString()); // Evaluate the calculation string
            inputField.setText(String.valueOf(result));
            calculation.setLength(0);
            calculation.append(result);
        } catch (Exception ex) {
            inputField.setText("Error");
            calculation.setLength(0);
        }
    }
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }
    public static void main(String[] args) {
        new SimpleCalculator();
    }
}

