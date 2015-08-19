package com.mycompany.personalhealthmanagement;

public class CaloriesItems {
    public static final String[] CalItemsFood = {
            "Bacon", "Bagel", "Beef", "Bread", "Burger", "Cheesecake", "Crab", "Fries", "Lobster",
            "Meatball", "Noodle", "Pasta", "Pho", "Pizza", "Pork", "Rice", "Salmon", "Sausage", "Steak", "Taco"
    };
    public static final int[] CalNumOfFood = {
        61, 72, 200, 53, 549, 257, 87, 231, 170, 569, 376, 129, 440, 172, 132, 167, 233, 117, 210, 190
    };

    private final String[] CalItemsBeverage = {
            "Cappuccino", "Cocoa", "Coke", "Frappuccino", "Latte", "Lemonade", "Margaritas", "Milk", "Mocha", "Root Beer"

    };
    private final int[] CalNumOfBeverage = {
        73, 170, 104, 233, 65, 34, 153, 122, 449, 97
    };

    private final String[] CalItemsExercise = {
            "Badminton", "Basketball", "Football", "Hockey", "Jogging", "Running", "Swimming", "Table tennis",
            "Tennis", "Volleyball"
    };
    private final int[] CalNumOfExercise = {
            204, 238, 272, 238, 92, 187, 191, 102, 204, 105
    };

    public CaloriesItems() { }

    public int getFoodNum() {
        return CalItemsFood.length;
    }

    public int getBeverageNum() {
        return CalItemsBeverage.length;
    }

    public int getExerciseNum() {
        return CalItemsExercise.length;
    }

    public String getFoodByPosition(int pos) {
        return CalItemsFood[pos];
    }

    public String getBeverageByPosition(int pos) {
        return CalItemsBeverage[pos];
    }

    public String getExerciseByPosition(int pos) {
        return CalItemsExercise[pos];
    }

    public int getCalByFood(int pos) {
        return CalNumOfFood[pos];
    }

    public int getCalByBeverage(int pos) {
        return CalNumOfBeverage[pos];
    }

    public int getCalByExercise(int pos) {
        return CalNumOfExercise[pos];
    }
}
