package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ClassGradeManager 用於管理學生成績的類別。
 * 它提供了添加成績、計算加權平均分、統計成績分佈、以及其他與學生成績相關的功能。
 */
public class ClassGradeManager {
    /**
     * 存儲學生成績的映射，外層 Map 的鍵是學生ID，內層 Map 的鍵是科目名稱，值是該科目的成績
     */
    private final Map<String, Map<String, Integer>> studentGrades = new ConcurrentHashMap<>();

    /**
     * 存儲科目權重的映射，鍵是科目名稱，值是對應的權重（如影響最終分數的比例）
     */
    private final Map<String, Double> subjectWeights = new ConcurrentHashMap<>();

    /**
     * 分數閾值：低於60分表示不及格
     */
    private static final int SCORE_THRESHOLD_1 = 60;

    /**
     * 分數閾值：60-69分表示及格
     */
    private static final int SCORE_THRESHOLD_2 = 70;

    /**
     * 分數閾值：70-79分表示良好
     */
    private static final int SCORE_THRESHOLD_3 = 80;

    /**
     * 分數閾值：80-89分表示優秀
     */
    private static final int SCORE_THRESHOLD_4 = 90;

    /**
     * 分數範圍：0-59分，不及格區間
     */
    private static final String RANGE_0_59 = "0-59";

    /**
     * 分數範圍：60-69分，及格區間
     */
    private static final String RANGE_60_69 = "60-69";

    /**
     * 分數範圍：70-79分，良好區間
     */
    private static final String RANGE_70_79 = "70-79";

    /**
     * 分數範圍：80-89分，優秀區間
     */
    private static final String RANGE_80_89 = "80-89";

    /**
     * 分數範圍：90-100分，卓越區間
     */
    private static final String RANGE_90_100 = "90-100";

    /**
     * 設置科目權重
     * @param weights 包含科目名稱與權重的映射
     */
    public void setSubjectWeights(final Map<String, Double> weights) {
        subjectWeights.putAll(weights);
    }

    /**
     * 取得科目權重
     * @param subject 科目名稱
     * @return 該科目的權重
     */
    public double getSubjectWeight(final String subject) {
        return subjectWeights.getOrDefault(subject, 0.0); // 若找不到，回傳預設值 0.0
    }

    /**
     * 添加成績
     * @param studentName 學生姓名
     * @param subject 科目名稱
     * @param grade 成績
     * @throws IllegalArgumentException 如果傳入的資料無效
     */
    public void addGrade(final String studentName, final String subject, final int grade) {
        if (studentName == null || studentName.isEmpty() || subject == null || subject.isEmpty() || grade < 0) {
            throw new IllegalArgumentException("Invalid grade data");
        }
        studentGrades
                .computeIfAbsent(studentName, _ -> new HashMap<>())
                .put(subject, grade);
    }

    /**
     * 計算加權總分的分佈，計算各個區間的學生數量
     * @return 包含每個分數區間學生數量的映射
     */
    public Map<String, Long> calculateWeightedScoreDistribution() {
        final Map<String, Long> distribution = initializeDistribution();
        final List<Double> weightedScores = calculateAllWeightedScores();

        for (final double score : weightedScores) {
            updateDistribution(distribution, score);
        }

        return distribution;
    }

    /**
     * 初始化分數區間分佈
     * @return 初始的分佈映射
     */
    private Map<String, Long> initializeDistribution() {
        final Map<String, Long> distribution = new ConcurrentHashMap<>();
        distribution.put("0-59", 0L);
        distribution.put("60-69", 0L);
        distribution.put("70-79", 0L);
        distribution.put("80-89", 0L);
        distribution.put("90-100", 0L);
        return distribution;
    }

    /**
     * 更新分佈，根據加權總分更新學生數量
     * @param distribution 學生分佈映射
     * @param score 加權總分
     */
    private void updateDistribution(final Map<String, Long> distribution, final double score) {
        if (score < SCORE_THRESHOLD_1) {
            distribution.put(RANGE_0_59, distribution.getOrDefault(RANGE_0_59, 0L) + 1);
        } else if (score < SCORE_THRESHOLD_2) {
            distribution.put(RANGE_60_69, distribution.getOrDefault(RANGE_60_69, 0L) + 1);
        } else if (score < SCORE_THRESHOLD_3) {
            distribution.put(RANGE_70_79, distribution.getOrDefault(RANGE_70_79, 0L) + 1);
        } else if (score < SCORE_THRESHOLD_4) {
            distribution.put(RANGE_80_89, distribution.getOrDefault(RANGE_80_89, 0L) + 1);
        } else {
            distribution.put(RANGE_90_100, distribution.getOrDefault(RANGE_90_100, 0L) + 1);
        }
    }

    /**
     * 計算所有學生的加權總分
     * @return 學生的加權總分列表
     */
    public List<Double> calculateAllWeightedScores() {
        final List<Double> scores = new ArrayList<>();
        for (final String student : studentGrades.keySet()) {
            scores.add(calculateWeightedAverage(student));
        }
        manualSort(scores); // 手動排序
        return scores;
    }

    /**
     * 手動排序分數
     * @param scores 要排序的分數列表
     */
    private void manualSort(final List<Double> scores) {
        for (int i = 0; i < scores.size() - 1; i++) {
            for (int j = i + 1; j < scores.size(); j++) {
                if (scores.get(i) < scores.get(j)) {
                    final double temp = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, temp);
                }
            }
        }
    }

    /**
     * 計算某學生的加權平均分
     * @param studentName 學生姓名
     * @return 該學生的加權平均分
     * @throws IllegalArgumentException 如果找不到該學生的成績資料
     */
    public double calculateWeightedAverage(final String studentName) {
        if (!studentGrades.containsKey(studentName)) {
            throw new IllegalArgumentException("Student not found");
        }
        final Map<String, Integer> grades = studentGrades.get(studentName);
        return calculateStudentWeightedTotal(grades) / calculateStudentWeightSum(grades);
    }

    /**
     * 計算學生的加權總分
     * @param grades 學生的成績
     * @return 學生的加權總分
     */
    private double calculateStudentWeightedTotal(final Map<String, Integer> grades) {
        double total = 0.0;
        for (final Map.Entry<String, Integer> entry : grades.entrySet()) {
            final double weight = subjectWeights.getOrDefault(entry.getKey(), 0.0);
            total += entry.getValue() * weight;
        }
        return total;
    }

    /**
     * 計算學生的總權重
     * @param grades 學生的成績
     * @return 學生的權重總和
     */
    private double calculateStudentWeightSum(final Map<String, Integer> grades) {
        double totalWeight = 0.0;
        for (final String subject : grades.keySet()) {
            totalWeight += subjectWeights.getOrDefault(subject, 0.0);
        }
        return totalWeight;
    }

    /**
     * 根據加權後的成績查找學生名單，並按降序排列
     * @return 包含學生姓名和加權總分的映射列表
     */
    public List<Map.Entry<String, Double>> getSortedWeightedScores() {
        return studentGrades.keySet().stream()
                .map(studentName -> new AbstractMap.SimpleEntry<>(studentName, calculateWeightedAverage(studentName)))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 計算所有學生的加權平均分的平均值
     *
     * @return 所有學生加權平均分的平均值
     */
    public double calculateWeightedAverageScore() {
        // 計算每個學生的加權平均
        final List<Double> weightedAverages = studentGrades.keySet().stream()
                .map(this::calculateWeightedAverage)
                .toList();

        // 計算所有學生加權平均的平均值
        return weightedAverages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * 根據指定的成績範圍查找符合條件的學生名單
     *
     * @param min 成績範圍的最小值
     * @param max 成績範圍的最大值
     * @return 符合條件的學生名單
     */
    public List<String> findStudentsByScoreRange(final double min, final double max) {
        return studentGrades.keySet().stream()
                .filter(student -> {
                    final double score = calculateWeightedAverage(student);
                    return score >= min && score <= max;
                })
                .collect(Collectors.toList());
    }

    /**
     * 計算所有學生加權總分的中位數
     *
     * @return 所有學生加權總分的中位數
     */
    public double calculateWeightedMedianScore() {
        final List<Double> weightedScores = calculateAllWeightedScores();
        Collections.sort(weightedScores);

        final int size = weightedScores.size();
        final double median;

        if (size == 0) {
            median = 0;
        } else if (size % 2 == 0) {
            median = (weightedScores.get(size / 2 - 1) + weightedScores.get(size / 2)) / 2.0;
        } else {
            median = weightedScores.get(size / 2);
        }

        return median;
    }


    /**
     * 計算所有學生加權總分的標準差
     *
     * @return 所有學生加權總分的標準差
     */
    public double calculateWeightedStandardDeviation() {
        final List<Double> weightedScores = calculateAllWeightedScores();
        final double average = calculateWeightedAverageScore();
        final double variance = weightedScores.stream()
                .mapToDouble(score -> Math.pow(score - average, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * 計算所有學生加權總分的變異數
     *
     * @return 所有學生加權總分的變異數
     */
    public double calculateWeightedVariance() {
        final List<Double> weightedScores = calculateAllWeightedScores();
        final double average = calculateWeightedAverageScore();
        return weightedScores.stream()
                .mapToDouble(score -> Math.pow(score - average, 2))
                .average()
                .orElse(0.0);
    }

    /**
     * 計算所有學生加權總分的四分位距 (IQR)
     *
     * @return 加權總分的四分位距
     */
    public double calculateWeightedIQR() {
        final List<Double> weightedScores = calculateAllWeightedScores();
        Collections.sort(weightedScores);

        final double quartile1 = calculateQuantile(weightedScores, 0.25);
        final double quartile3 = calculateQuantile(weightedScores, 0.75);

        return quartile3 - quartile1;
    }

    /**
     * 計算所有學生加權總分的最大值
     *
     * @return 所有學生加權總分的最大值
     */
    public double calculateWeightedMax() {
        return calculateAllWeightedScores().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    /**
     * 計算所有學生加權總分的最小值
     *
     * @return 所有學生加權總分的最小值
     */
    public double calculateWeightedMin() {
        return calculateAllWeightedScores().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * 計算指定成績的百分位數排名 (PR)
     *
     * @param score 目標分數
     * @param scores 所有分數的列表
     * @return 該分數的百分位數排名
     */
    public double calculatePR(final double score, final List<Double> scores) {
        final long countBelow = scores.stream().filter(s -> s < score).count();
        return (countBelow * 100.0) / scores.size();
    }

    /**
     * 計算所有學生加權總分的百分位數排名 (PR)
     *
     * @return 所有學生的PR值
     */
    public Map<String, Double> calculateAllWeightedPRs() {
        final List<Double> weightedScores = calculateAllWeightedScores();
        final Map<String, Double> prs = new ConcurrentHashMap<>();

        for (final String student : studentGrades.keySet()) {
            final double weightedScore = calculateWeightedAverage(student);
            final double pRating = calculatePR(weightedScore, weightedScores);
            prs.put(student, pRating);
        }

        return prs;
    }

    /**
     * 查詢指定學生在特定科目的成績。
     *
     * @param studentName 學生姓名
     * @param subject 科目名稱
     * @return 該學生在指定科目的成績，若無成績則返回 null
     */
    public Integer getGrade(final String studentName, final String subject) {
        return studentGrades.getOrDefault(studentName, Collections.emptyMap()).get(subject);
    }

    /**
     * 查詢所有學生在特定科目的成績，並按成績從高到低排序。
     *
     * @param subject 科目名稱
     * @return 按成績排序的學生姓名與成績列表
     */
    public final List<Map.Entry<String, Integer>> getSortedGradesBySubject(final String subject) {
        return studentGrades.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get(subject)))
                .filter(entry -> entry.getValue() != null)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 計算特定科目的平均分數。
     *
     * @param subject 科目名稱
     * @return 該科目的平均分數，四捨五入到小數點第一位
     */
    public double calculateAverage(final String subject) {
        final double average = studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .average()
                .orElse(0);
        return Math.round(average * 10) / 10.0; // 四捨五入到小數點第一位
    }

    /**
     * 計算特定科目的中位數。
     *
     * @param subject 科目名稱
     * @return 該科目的中位數，四捨五入到小數點第一位
     */
    public double calculateMedian(final String subject) {
        final List<Integer> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .sorted()
                .toList();

        final int size = sortedGrades.size();
        double median = 0.0;

        if (size > 0) {
            if (size % 2 == 0) {
                median = (sortedGrades.get(size / 2 - 1) + sortedGrades.get(size / 2)) / 2.0;
            } else {
                median = sortedGrades.get(size / 2);
            }
        }

        return Math.round(median * 10) / 10.0; // 四捨五入到小數點第一位
    }

    /**
     * 計算特定科目的標準差。
     *
     * @param subject 科目名稱
     * @return 該科目的標準差，四捨五入到小數點第一位
     */
    public double calculateStandardDeviation(final String subject) {
        final double average = calculateAverage(subject);
        final double variance = studentGrades.values().stream()
                .mapToDouble(grades -> Math.pow(grades.getOrDefault(subject, 0) - average, 2))
                .average()
                .orElse(0);

        final double standardDeviation = Math.sqrt(variance);
        return Math.round(standardDeviation * 10) / 10.0; // 四捨五入到小數點第一位
    }

    /**
     * 計算特定科目的四分位距 (IQR)。
     *
     * @param subject 科目名稱
     * @return 該科目的四分位距，四捨五入到小數點第一位
     */
    public double calculateIQR(final String subject) {
        final List<Double> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .map(grade -> (double) grade) // 將 Integer 類型轉換為 Double 類型
                .sorted()
                .collect(Collectors.toList());

        final int size = sortedGrades.size();
        double iqr = 0.0;

        if (size > 0) {
            final double quantile1 = calculateQuantile(sortedGrades, 0.25); // 第一四分位數 (Q1)
            final double quantile3 = calculateQuantile(sortedGrades, 0.75); // 第三四分位數 (Q3)
            iqr = quantile3 - quantile1;
        }

        return Math.round(iqr * 10) / 10.0; // 四捨五入到小數點第一位
    }

    /**
     * 計算分數的指定四分位數（Quantile）。
     *
     * @param sortedScores 已經排序的數值列表，數值可以是任意數字型態。
     * @param quantile     四分位數比例（例如：0.25 表示第一四分位數，0.5 表示中位數）。
     * @return 計算得到的四分位數數值，若列表為空，則返回 0.0。
     */
    private double calculateQuantile(final List<? extends Number> sortedScores, final double quantile) {
        final int totalStudent = sortedScores.size();
        double result = 0.0;

        if (totalStudent > 0) {
            // 計算位置 L
            final double pos = totalStudent * quantile;

            // 如果 L 是整數
            if (pos == Math.floor(pos)) {
                final int index = (int) pos - 1; // L 的位置從 1 開始，因此要減 1
                result = (sortedScores.get(index).doubleValue() + sortedScores.get(index + 1).doubleValue()) / 2.0;
            } else {
                // 如果 L 不是整數，取下一個最近的整數位置
                final int index = (int) Math.ceil(pos) - 1;
                result = sortedScores.get(index).doubleValue();
            }
        }

        return result;
    }

    /**
     * 計算特定科目的變異數。
     *
     * @param subject 科目名稱
     * @return 該科目的變異數，四捨五入到小數點第一位
     */
    public double calculateVariance(final String subject) {
        final double average = calculateAverage(subject);
        final double variance = studentGrades.values().stream()
                .mapToDouble(grades -> Math.pow(grades.getOrDefault(subject, 0) - average, 2))
                .average()
                .orElse(0);
        return Math.round(variance * 10) / 10.0; // 四捨五入到小數點第一位
    }

    /**
     * 計算特定科目的最高分。
     *
     * @param subject 科目名稱
     * @return 該科目的最高分，若無數據則返回 0
     */
    public int calculateMax(final String subject) {
        return studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .max()
                .orElse(0);
    }

    /**
     * 計算特定科目的最低分。
     *
     * @param subject 科目名稱
     * @return 該科目的最低分，若無數據則返回 0
     */
    public int calculateMin(final String subject) {
        return studentGrades.values().stream()
                .mapToInt(grades -> grades.getOrDefault(subject, 0))
                .min()
                .orElse(0);
    }

    /**
     * 計算指定分數在特定科目中的 PR 值（百分等級）。
     *
     * @param subject 科目名稱
     * @param score 分數
     * @return 該分數的 PR 值
     */
    public double calculatePercentileRank(final String subject, final int score) {
        final List<Integer> sortedGrades = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .sorted()
                .toList();

        double percentileRank = 0.0;

        if (!sortedGrades.isEmpty()) {
            final long countBelow = sortedGrades.stream()
                    .filter(grade -> grade < score)
                    .count();

            percentileRank = (countBelow * 100.0) / sortedGrades.size();
        }

        return percentileRank;
    }

    /**
     * 計算特定科目的眾數。
     *
     * @param subject 科目名稱
     * @return 該科目的眾數列表
     */
    public List<Integer> calculateMode(final String subject) {
        final Map<Integer, Long> frequencyMap = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .collect(Collectors.groupingBy(score -> score, Collectors.counting()));

        final long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 計算特定科目的分數分布。
     *
     * @param subject 科目名稱
     * @param interval 分數區間間隔
     * @return 分數區間與學生數量的映射
     */
    public Map<String, Long> calculateSubjectGradeDistribution(final String subject, final int interval) {
        final Map<String, Long> distribution = new ConcurrentHashMap<>();

        // 找到最低和最高分數
        final int minGrade = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .min(Integer::compare)
                .orElse(0);

        final int maxGrade = studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .max(Integer::compare)
                .orElse(0);

        // 初始化所有可能的區段為0
        for (int i = (minGrade / interval) * interval; i <= maxGrade; i += interval) {
            final int rangeEnd = i + interval - 1;
            final String key = i + "-" + rangeEnd;
            distribution.put(key, 0L);
        }

        // 計算實際的分數分布
        studentGrades.values().stream()
                .map(grades -> grades.getOrDefault(subject, 0))
                .forEach(grade -> {
                    final int rangeStart = (grade / interval) * interval;
                    final int rangeEnd = rangeStart + interval - 1;
                    final String key = rangeStart + "-" + rangeEnd;
                    distribution.put(key, distribution.getOrDefault(key, 0L) + 1);
                });

        return distribution;
    }

}
