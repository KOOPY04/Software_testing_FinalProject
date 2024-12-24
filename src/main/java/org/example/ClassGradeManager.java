package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class ClassGradeManager {
    private final Map<String, Map<String, Integer>> studentGrades = new HashMap<>();
    private final Map<String, Double> subjectWeights = new HashMap<>(); // 新增科目權重

    // 設置科目權重
    public void setSubjectWeights(Map<String, Double> weights) {
        subjectWeights.putAll(weights);
    }

    // 取得科目權重
    public double getSubjectWeight(String subject) {
        return subjectWeights.getOrDefault(subject, 0.0); // 若找不到，回傳預設值 0.0
    }

    // 添加成績
    public void addGrade(String studentName, String subject, int grade) {
        if (studentName == null || studentName.isEmpty() || subject == null || subject.isEmpty() || grade < 0) {
            throw new IllegalArgumentException("Invalid grade data");
        }
        studentGrades
                .computeIfAbsent(studentName, _ -> new HashMap<>())
                .put(subject, grade);
    }

    // 計算加權總分的分佈，計算各個區間的學生數量
    public Map<String, Long> calculateWeightedScoreDistribution() {
        Map<String, Long> distribution = initializeDistribution();
        List<Double> weightedScores = calculateAllWeightedScores();

        for (double score : weightedScores) {
            updateDistribution(distribution, score);
        }

        return distribution;
    }

    private Map<String, Long> initializeDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("0-59", 0L);
        distribution.put("60-69", 0L);
        distribution.put("70-79", 0L);
        distribution.put("80-89", 0L);
        distribution.put("90-100", 0L);
        return distribution;
    }

    private void updateDistribution(Map<String, Long> distribution, double score) {
        if (score < 60) {
            distribution.put("0-59", distribution.get("0-59") + 1);
        } else if (score < 70) {
            distribution.put("60-69", distribution.get("60-69") + 1);
        } else if (score < 80) {
            distribution.put("70-79", distribution.get("70-79") + 1);
        } else if (score < 90) {
            distribution.put("80-89", distribution.get("80-89") + 1);
        } else {
            distribution.put("90-100", distribution.get("90-100") + 1);
        }
    }


    // 計算所有學生的加權總分
    public List<Double> calculateAllWeightedScores() {
        List<Double> scores = new ArrayList<>();
        for (String student : studentGrades.keySet()) {
            scores.add(calculateWeightedAverage(student));
        }
        manualSort(scores); // 手動排序
        return scores;
    }

    private void manualSort(List<Double> scores) {
        for (int i = 0; i < scores.size() - 1; i++) {
            for (int j = i + 1; j < scores.size(); j++) {
                if (scores.get(i) < scores.get(j)) {
                    double temp = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, temp);
                }
            }
        }
    }

    // 計算加權平均分
    public double calculateWeightedAverage(String studentName) {
        if (!studentGrades.containsKey(studentName)) {
            throw new IllegalArgumentException("Student not found");
        }
        Map<String, Integer> grades = studentGrades.get(studentName);
        return calculateStudentWeightedTotal(grades) / calculateStudentWeightSum(grades);
    }

    private double calculateStudentWeightedTotal(Map<String, Integer> grades) {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : grades.entrySet()) {
            double weight = subjectWeights.getOrDefault(entry.getKey(), 0.0);
            total += entry.getValue() * weight;
        }
        return total;
    }

    private double calculateStudentWeightSum(Map<String, Integer> grades) {
        double totalWeight = 0.0;
        for (String subject : grades.keySet()) {
            totalWeight += subjectWeights.getOrDefault(subject, 0.0);
        }
        return totalWeight;
    }

    // 排序所有學生的加權總分，按降序排列
    public List<Map.Entry<String, Double>> getSortedWeightedScores() {
        return studentGrades.keySet().stream()
                .map(studentName -> new AbstractMap.SimpleEntry<>(studentName, calculateWeightedAverage(studentName)))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // 計算加權平均分的平均值
    public double calculateWeightedAverageScore() {
        // 計算每個學生的加權平均
        List<Double> weightedAverages = studentGrades.keySet().stream()
                .map(this::calculateWeightedAverage)
                .toList();

        // 計算所有學生加權平均的平均值
        return weightedAverages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // 根據加權後成績查找學生名單
    public List<String> findStudentsByScoreRange(double min, double max) {
        return studentGrades.keySet().stream()
                .filter(student -> {
                    double score = calculateWeightedAverage(student);
                    return score >= min && score <= max;
                })
                .collect(Collectors.toList());
    }

    // 計算加權總分的中位數
    public double calculateWeightedMedianScore() {
        List<Double> weightedScores = calculateAllWeightedScores();
        Collections.sort(weightedScores);

        int size = weightedScores.size();
        if (size == 0) return 0;

        if (size % 2 == 0) {
            return (weightedScores.get(size / 2 - 1) + weightedScores.get(size / 2)) / 2.0;
        } else {
            return weightedScores.get(size / 2);
        }
    }

    // 計算加權總分的標準差
    public double calculateWeightedStandardDeviation() {
        List<Double> weightedScores = calculateAllWeightedScores();
        double average = calculateWeightedAverageScore();
        double variance = weightedScores.stream()
                .mapToDouble(score -> Math.pow(score - average, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    // 計算加權總分的變異數
    public double calculateWeightedVariance() {
        List<Double> weightedScores = calculateAllWeightedScores();
        double average = calculateWeightedAverageScore();
        return weightedScores.stream()
                .mapToDouble(score -> Math.pow(score - average, 2))
                .average()
                .orElse(0.0);
    }

    // 計算加權總分的四分位距 (IQR)
    public double calculateWeightedIQR() {
        List<Double> weightedScores = calculateAllWeightedScores();
        Collections.sort(weightedScores);

        double q1 = calculateQuantile(weightedScores, 0.25);
        double q3 = calculateQuantile(weightedScores, 0.75);

        return q3 - q1;
    }

    // 計算加權總分的最大值
    public double calculateWeightedMax() {
        return calculateAllWeightedScores().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    // 計算加權總分的最小值
    public double calculateWeightedMin() {
        return calculateAllWeightedScores().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    // 計算某分數的 PR 值
    public double calculatePR(double score, List<Double> scores) {
        long countBelow = scores.stream().filter(s -> s < score).count();
        return (countBelow * 100.0) / scores.size();
    }

    // 計算所有學生加權總分的 PR 值
    public Map<String, Double> calculateAllWeightedPRs() {
        List<Double> weightedScores = calculateAllWeightedScores();
        Map<String, Double> prs = new HashMap<>();

        for (String student : studentGrades.keySet()) {
            double weightedScore = calculateWeightedAverage(student);
            double pr = calculatePR(weightedScore, weightedScores);
            prs.put(student, pr);
        }

        return prs;
    }

    // 查詢成績
    public Integer getGrade(String studentName, String subject) {
        return studentGrades.getOrDefault(studentName, Collections.emptyMap()).get(subject);
    }

    // 查詢某科目成績並排序
    public List<Map.Entry<String, Integer>> getSortedGradesBySubject(String subject) {
        return studentGrades.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get(subject)))
                .filter(entry -> entry.getValue() != null)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // 計算某科目平均分
    public double calculateAverage(String subject) {
        double average = studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .average()
                .orElse(0);
        return Math.round(average * 10) / 10.0; // 四捨五入到小數點第一位
    }

    // 計算某科目中位數
    public double calculateMedian(String subject) {
        List<Integer> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .sorted()
                .toList();

        int size = sortedGrades.size();
        if (size == 0) return 0;

        double median;
        if (size % 2 == 0) {
            median = (sortedGrades.get(size / 2 - 1) + sortedGrades.get(size / 2)) / 2.0;
        } else {
            median = sortedGrades.get(size / 2);
        }
        return Math.round(median * 10) / 10.0; // 四捨五入到小數點第一位
    }

    // 計算某科目標準差
    public double calculateStandardDeviation(String subject) {
        double average = calculateAverage(subject);
        double variance = studentGrades.values().stream()
                .mapToDouble(grades -> Math.pow(grades.getOrDefault(subject, 0) - average, 2))
                .average()
                .orElse(0);

        double standardDeviation = Math.sqrt(variance);
        return Math.round(standardDeviation * 10) / 10.0; // 四捨五入到小數點第一位
    }

    // 計算某科目四分位距 (IQR)
    public double calculateIQR(String subject) {
        List<Double> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .map(grade -> (double) grade)  // 將 Integer 類型轉換為 Double 類型
                .sorted()
                .collect(Collectors.toList());

        int size = sortedGrades.size();
        if (size == 0) return 0;

        double q1 = calculateQuantile(sortedGrades, 0.25); // 第一四分位數 (Q1)
        double q3 = calculateQuantile(sortedGrades, 0.75); // 第三四分位數 (Q3)

        return Math.round((q3 - q1) * 10) / 10.0; // 四捨五入到小數點第一位
    }

    // 計算分數的四分位數
    private double calculateQuantile(List<? extends Number> sortedScores, double quantile) {
        int n = sortedScores.size();
        if (n == 0) return 0;

        // 計算位置 L
        double pos = n * quantile;

        // 如果 L 是整數
        if (pos == Math.floor(pos)) {
            int index = (int) pos - 1; // L 的位置從 1 開始，因此要減 1
            return (sortedScores.get(index).doubleValue() + sortedScores.get(index + 1).doubleValue()) / 2.0;
        } else {
            // 如果 L 不是整數，取下一個最近的整數位置
            int index = (int) Math.ceil(pos) - 1;
            return sortedScores.get(index).doubleValue();
        }
    }

    // 計算變異數
    public double calculateVariance(String subject) {
        double average = calculateAverage(subject);
        double variance = studentGrades.values().stream()
                .mapToDouble(grades -> Math.pow(grades.getOrDefault(subject, 0) - average, 2))
                .average()
                .orElse(0);
        return Math.round(variance * 10) / 10.0; // 四捨五入到小數點第一位
    }
    // 計算最大值
    public int calculateMax(String subject) {
        return studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .max()
                .orElse(0);
    }

    // 計算最小值
    public int calculateMin(String subject) {
        return studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .min()
                .orElse(0);
    }

    // PR 值計算
    public double calculatePercentileRank(String subject, int score) {
        List<Integer> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .sorted()
                .toList();

        if (sortedGrades.isEmpty()) {
            return 0.0;
        }

        long countBelow = sortedGrades.stream()
                .filter(grade -> grade < score)
                .count();

        return (countBelow * 100.0) / sortedGrades.size();
    }

    // 計算某科目眾數
    public List<Integer> calculateMode(String subject) {
        Map<Integer, Long> frequencyMap = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .collect(Collectors.groupingBy(score -> score, Collectors.counting()));

        long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 科目排名統計（按分數段分組）
    public Map<String, Long> calculateSubjectGradeDistribution(String subject, int interval) {
        Map<String, Long> distribution = new HashMap<>();
        studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .forEach(grade -> {
                    int rangeStart = (grade / interval) * interval;
                    int rangeEnd = rangeStart + interval - 1;
                    String key = rangeStart + "-" + rangeEnd;
                    distribution.put(key, distribution.getOrDefault(key, 0L) + 1);
                });
        return distribution;
    }
}
