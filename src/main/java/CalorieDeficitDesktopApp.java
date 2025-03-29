import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;

public class CalorieDeficitDesktopApp {

    static double tdeeValue = 0;
    static double deficitValue = 0;
    static double totalCaloriesConsumed = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Deficiti Kalorik");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(14, 2, 10, 10));

        JTextField genderField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField heightField = new JTextField();

        String[] activityLevels = {"Sedentar", "Mesatar", "Aktiv i lartë"};
        JComboBox<String> activityBox = new JComboBox<>(activityLevels);

        JButton calculateButton = new JButton("Llogarit");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Ushqime me kalori
        HashMap<String, Double> foodCaloriesMap = new HashMap<>();
        foodCaloriesMap.put("Bukë (100g)", 250.0);
        foodCaloriesMap.put("Vezë (1 copë)", 70.0);
        foodCaloriesMap.put("Qumësht (100ml)", 42.0);
        foodCaloriesMap.put("Mish pule (100g)", 165.0);
        foodCaloriesMap.put("Mollë (1 copë)", 95.0);
        foodCaloriesMap.put("Banane (1 copë)", 105.0);
        foodCaloriesMap.put("Djathë (100g)", 300.0);
        foodCaloriesMap.put("Makarona (100g)", 350.0); // MAKARONA

        String[] foodNames = foodCaloriesMap.keySet().toArray(new String[0]);
        JComboBox<String> foodBox = new JComboBox<>(foodNames);
        JTextField quantityField = new JTextField(); // Sasia

        JButton addFoodButton = new JButton("Shto ushqim");
        JTextArea foodListArea = new JTextArea();
        foodListArea.setEditable(false);

        JButton resetButton = new JButton("Nis Ditën nga e para");

        // GUI Layout
        frame.add(new JLabel("Gjinia (M/F):"));
        frame.add(genderField);
        frame.add(new JLabel("Mosha:"));
        frame.add(ageField);
        frame.add(new JLabel("Pesha (kg):"));
        frame.add(weightField);
        frame.add(new JLabel("Lartësia (cm):"));
        frame.add(heightField);
        frame.add(new JLabel("Aktiviteti:"));
        frame.add(activityBox);
        frame.add(calculateButton);
        frame.add(new JLabel("Rezultati:"));
        frame.add(resultArea);

        frame.add(new JLabel("Zgjidh ushqimin:"));
        frame.add(foodBox);
        frame.add(new JLabel("Sasia (g ose copë):"));
        frame.add(quantityField);
        frame.add(addFoodButton);
        frame.add(new JLabel("Ushqimet e ditës:"));
        frame.add(foodListArea);

        frame.add(resetButton);
        frame.setVisible(true);

        ArrayList<String> foodList = new ArrayList<>();

        // Llogarit TDEE
        calculateButton.addActionListener(e -> {
            try {
                String gender = genderField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                double height = Double.parseDouble(heightField.getText().trim());
                String activity = activityBox.getSelectedItem().toString();

                double bmr;
                if (gender.equalsIgnoreCase("M")) {
                    bmr = 66 + (13.7 * weight) + (5 * height) - (6.8 * age);
                } else {
                    bmr = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
                }

                double activityFactor = switch (activity) {
                    case "Mesatar" -> 1.55;
                    case "Aktiv i lartë" -> 1.725;
                    default -> 1.2;
                };

                double tdee = bmr * activityFactor;
                double deficit = tdee - 500;

                tdeeValue = tdee;
                deficitValue = deficit;

                String result = String.format("BMR: %.2f kcal\nTDEE: %.2f kcal\nDeficit: %.2f kcal", bmr, tdee, deficit);
                resultArea.setText(result);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Plotëso saktë të dhënat!");
            }
        });

        // Shto ushqim
        addFoodButton.addActionListener(e -> {
            try {
                String selectedFood = foodBox.getSelectedItem().toString();
                double baseCalories = foodCaloriesMap.get(selectedFood);

                double quantity = Double.parseDouble(quantityField.getText().trim());
                double caloriesConsumed;

                if (selectedFood.contains("100g") || selectedFood.contains("100ml")) {
                    caloriesConsumed = (baseCalories / 100) * quantity;
                } else {
                    caloriesConsumed = baseCalories * quantity;
                }

                totalCaloriesConsumed += caloriesConsumed;

                String entry = String.format("%s - %.2f g/copë = %.2f kcal", selectedFood, quantity, caloriesConsumed);
                foodList.add(entry);

                StringBuilder sb = new StringBuilder();
                for (String food : foodList) {
                    sb.append(food).append("\n");
                }
                sb.append("\nTotali i kalorive: ").append(totalCaloriesConsumed).append(" kcal\n");

                if (tdeeValue > 0) {
                    if (totalCaloriesConsumed < deficitValue) {
                        sb.append("Je brenda deficitit 👌\n");
                    } else if (totalCaloriesConsumed < tdeeValue) {
                        sb.append("Ke kaluar deficitin, por je nën TDEE 😅\n");
                    } else {
                        sb.append("Ke kaluar TDEE ⚠️\n");
                    }
                } else {
                    sb.append("Llogarit TDEE për statusin ditor.");
                }

                foodListArea.setText(sb.toString());
                quantityField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Fut një sasi të vlefshme (numër)!");
            }
        });

        // Reset
        resetButton.addActionListener(e -> {
            foodList.clear();
            totalCaloriesConsumed = 0;
            foodListArea.setText("");
        });
    }
}
