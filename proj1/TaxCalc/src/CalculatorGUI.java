import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class CalculatorGUI extends JFrame implements ActionListener {
    // UI组件
    private final JTextArea displayField;
    private final JTextArea resultField;
    private final PersonalTaxCalculator calculator = new PersonalTaxCalculator();
    private final String inputExpression = "_";
    private int indexOfInput = 0;
    // 构造方法
    public CalculatorGUI() {
        // 设置窗口标题
        super("个人所得税计算器");
        // 设置窗口大小
        setSize(300, 400);
        // 设置窗口关闭方式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口布局
        setLayout(new BorderLayout());

        // 创建显示框
        displayField = new JTextArea(2, 15);
        displayField.setEditable(false);
        Font font = new Font("Arial", Font.BOLD, 30); // 设置字体为Arial，粗体
        displayField.setFont(font); // 设置输入框的字体和大小
        JScrollPane scrollPane = new JScrollPane(displayField);
        add(scrollPane, BorderLayout.NORTH);

        resultField = new JTextArea(1, 15);
        resultField.setEditable(false);
        resultField.setFont(font); // 设置输入框的字体和大小
        add(resultField, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4));

        // 定义按钮上的标签
        String[] buttonLabels = {
                "7", "8", "9", "C",
                "4", "5", "6", "<-",
                "1", "2", "3", "|<-",
                "0", ".", "=", "->|"
        };

        // 创建按钮
        JButton[] buttons = new JButton[buttonLabels.length];
        for (int i = 0; i < buttonLabels.length; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setPreferredSize(new Dimension(100, 55));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);

            int keyCode = switch (buttonLabels[i]) {
                case "C" -> KeyEvent.VK_C;
                case "<-" -> KeyEvent.VK_BACK_SPACE;
                case "|<-" -> KeyEvent.VK_A;
                case "->|" -> KeyEvent.VK_D;
                case "=" -> KeyEvent.VK_ENTER;
                case "." -> KeyEvent.VK_PERIOD;
                default -> KeyEvent.VK_0 + Integer.parseInt(buttonLabels[i]);
            };

            buttons[i].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), "clickButton" + i);
            int finalI = i;
            buttons[i].getActionMap().put("clickButton" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttons[finalI].doClick();
                }
            });
        }
        // 添加按钮面板
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        clear();
    }

    private void clear() {
        displayField.setText(inputExpression);
        indexOfInput = 0;
        resultField.setText("");
    }

    private void insert(String text) {
        String expression = displayField.getText();
        displayField.setText(expression.substring(0, indexOfInput) + text + inputExpression + expression.substring(indexOfInput + 1));
        indexOfInput++;
        displayField.setCaretPosition(displayField.getDocument().getLength());
    }

    private void delete() {
        String expression = displayField.getText();
        if(checkIndexOfInput())
            throw new RuntimeException("InputIndexError");
        if(indexOfInput > 0) {
            displayField.setText(expression.substring(0, indexOfInput - 1) + expression.substring(indexOfInput));
            indexOfInput--;
        }
        displayField.setCaretPosition(displayField.getDocument().getLength());
    }

    private void shift(boolean shift_left) {
        String text = displayField.getText();
        if(checkIndexOfInput())
            throw new RuntimeException("InputIndexError");
        if(shift_left) {
            if(indexOfInput > 0) {
                displayField.setText(text.substring(0, indexOfInput-1) + inputExpression + text.charAt(indexOfInput-1) +
                        text.substring(indexOfInput+1));
                indexOfInput--;
            }
        }
        else {
            if(indexOfInput < text.length()-1) {
                displayField.setText(text.substring(0, indexOfInput) + text.charAt(indexOfInput+1) + inputExpression +
                        text.substring(indexOfInput+2));
                indexOfInput++;
            }
        }
        displayField.setCaretPosition(indexOfInput);
    }

    private boolean checkIndexOfInput() {
        return indexOfInput < 0 || indexOfInput != displayField.getText().indexOf(inputExpression);
    }

    // 处理按钮点击事件
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(checkIndexOfInput()) {
            throw new RuntimeException("InputIndexError");
        }
        if (command.equals("=")) {
            // 获取显示框中的表达式
            String expression = displayField.getText();
            if(indexOfInput != expression.length()-1) {
                displayField.setText(expression.substring(0, indexOfInput) + expression.substring(indexOfInput+1) + inputExpression);
                indexOfInput = expression.length()-1;
            }
            // 计算表达式结果
            if(expression.length() > 1) {
                double salary = evaluateExpression(expression.replace(inputExpression, ""));
                if (salary < 0.0) {
                    resultField.setText("必须输入大于0的数字");
                } else {
                    double tax = calculator.calculateTax(salary);
                    resultField.setText(String.format("%.2f", tax));
                }
            }
        } else if(command.matches("[0-9]")) {
            // 追加按钮上的标签到显示框
            insert(command);
        } else if(command.equals("C")) {
            // 清空显示框
            clear();
        } else if(command.equals("<-")) {
            delete();
        } else if(command.equals("|<-")) {
            shift(true);
        } else if(command.equals("->|")) {
            shift(false);
        } else if(command.equals(".")) {
            insert(command);
        }
    }

    // 计算表达式结果
    private double evaluateExpression(String expression) {
        double salary = -1.0;
        try {
            salary = Double.parseDouble(expression);
        } catch(NumberFormatException e) {
            return salary;
        }
        return salary;
    }

    // 主方法
    public static void main(String[] args) {
        // 创建计算器窗口
        CalculatorGUI calculatorGUI = new CalculatorGUI();
        // 设置窗口可见
        calculatorGUI.setVisible(true);
    }
}

