import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class PersonalTaxCalculator {
    private double taxThreshold = 5000;
    private int level = 7;
    private final int[] levelThreshold = {0, 3000, 12000, 25000, 35000, 55000, 80000};
    private final double[] taxRate = {0.03, 0.1, 0.2, 0.25, 0.3, 0.35, 0.45};

    public PersonalTaxCalculator() {}

    public static void main(String[] args) {
        PersonalTaxCalculator calculator = new PersonalTaxCalculator();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入您的工资：（如20000.51）");

        double salary = scanner.nextDouble();
        System.out.println("您的工资为：" + String.format("%.2f", salary));
        double tax = calculator.calculateTax(salary);
        System.out.println("您需要缴纳的税款为：" + String.format("%.2f", tax));
        scanner.close();
    }

    public double calculateTax(double salary) {
        // 计算税金逻辑
        salary -= taxThreshold;
        if(salary <= 0.0)
            return 0.0;
        double tax = 0.0;
        for(int i = 1; i < levelThreshold.length; i++) {
            if(salary > levelThreshold[i]) {
                tax += (levelThreshold[i] - levelThreshold[i-1]) * taxRate[i-1];
            }
            else{
                tax += (salary - levelThreshold[i-1]) * taxRate[i-1];
                break;
            }
        }
        if(salary > levelThreshold[level-1]) {
            tax += (salary - levelThreshold[level-1]) * taxRate[level-1];
        }
        return tax;
    }
}