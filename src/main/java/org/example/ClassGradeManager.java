package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class ClassGradeManager {
    private final Map<String, Integer> studentGrades = new HashMap<>();

    // 添加成績
    public void addGrade(String studentName, int grade) {
        studentGrades.put(studentName, grade);
    }

    // 查詢成績
    public Integer getGrade(String studentName) {
        return studentGrades.get(studentName);
    }

    // 成績排序
    public List<Map.Entry<String, Integer>> getSortedGrades() {
        return studentGrades.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // 計算平均分
    public double calculateAverage() {
        return studentGrades.values()
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    // 計算中位數
    public double calculateMedian() {
        List<Integer> sortedGrades = studentGrades.values()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        int size = sortedGrades.size();
        if (size == 0) return 0;

        if (size % 2 == 0) {
            return (sortedGrades.get(size / 2 - 1) + sortedGrades.get(size / 2)) / 2.0;
        } else {
            return sortedGrades.get(size / 2);
        }
    }

    // 計算標準差
    public double calculateStandardDeviation() {
        double average = calculateAverage();
        double variance = studentGrades.values()
                .stream()
                .mapToDouble(grade -> Math.pow(grade - average, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }
}
