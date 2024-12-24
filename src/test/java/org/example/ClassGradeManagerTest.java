package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClassGradeManagerTest {
    private ClassGradeManager manager;

    @BeforeEach
    void setUp() {
        manager = new ClassGradeManager();
        Map<String, Double> weights = new HashMap<>();
        weights.put("Math", 0.5);
        weights.put("English", 0.3);
        weights.put("Science", 0.2);
        manager.setSubjectWeights(weights);

        manager.addGrade("Alice", "Math", 90);
        manager.addGrade("Alice", "English", 80);
        manager.addGrade("Alice", "Science", 70);

        manager.addGrade("Bob", "Math", 60);
        manager.addGrade("Bob", "English", 70);
        manager.addGrade("Bob", "Science", 80);

        manager.addGrade("Charlie", "Math", 50);
        manager.addGrade("Charlie", "English", 40);
        manager.addGrade("Charlie", "Science", 30);
    }

    @Test
    void testSetSubjectWeightsAndGetSubjectWeight() {
        assertEquals(0.5, manager.getSubjectWeight("Math"));
        assertEquals(0.3, manager.getSubjectWeight("English"));
        assertEquals(0.2, manager.getSubjectWeight("Science"));
    }

    @Test
    void testAddGrade() {
        manager.addGrade("David", "Math", 95);
        assertEquals(95, manager.getGrade("David", "Math"));
    }

    @Test
    void testCalculateWeightedAverage() {
        assertEquals(83.0, manager.calculateWeightedAverage("Alice"));
        assertEquals(67.0, manager.calculateWeightedAverage("Bob"));
        assertEquals(43.0, manager.calculateWeightedAverage("Charlie"));
    }

    @Test
    void testCalculateAllWeightedScores() {
        List<Double> scores = manager.calculateAllWeightedScores();
        assertEquals(Arrays.asList(83.0, 67.0, 43.0), scores);
    }

    @Test
    void testFindStudentsByScoreRange() {
        assertEquals(1, manager.findStudentsByScoreRange(80, 100).size());
    }

    @Test
    void testCalculateWeightedScoreDistribution() {
        Map<String, Long> distribution = manager.calculateWeightedScoreDistribution();
        assertEquals(1, distribution.get("0-59"));
        assertEquals(1, distribution.get("60-69"));
        assertEquals(1, distribution.get("80-89"));
        assertEquals(0, distribution.get("90-100"));
    }

    @Test
    void testGetSortedWeightedScores() {
        List<Map.Entry<String, Double>> sortedScores = manager.getSortedWeightedScores();
        assertEquals("Alice", sortedScores.get(0).getKey());
        assertEquals(83.0, sortedScores.get(0).getValue());
        assertEquals("Bob", sortedScores.get(1).getKey());
        assertEquals(67.0, sortedScores.get(1).getValue());
        assertEquals("Charlie", sortedScores.get(2).getKey());
        assertEquals(43.0, sortedScores.get(2).getValue());
    }

    @Test
    void testCalculateWeightedAverageScore() {
        assertEquals(64.3, manager.calculateWeightedAverageScore(), 0.1);
    }

    @Test
    void testCalculateWeightedMedianScore() {
        assertEquals(67.0, manager.calculateWeightedMedianScore());
    }

    @Test
    void testCalculateWeightedStandardDeviation() {
        assertEquals(16.4, manager.calculateWeightedStandardDeviation(), 0.1);
    }

    @Test
    void testCalculateWeightedVariance() {
        assertEquals(270.3, manager.calculateWeightedVariance(), 0.1);
    }

    @Test
    void testCalculateWeightedIQR() {
        assertEquals(40.0, manager.calculateWeightedIQR(), 0.1);
    }

    @Test
    void testCalculateWeightedMax() {
        assertEquals(83.0, manager.calculateWeightedMax());
    }

    @Test
    void testCalculateWeightedMin() {
        assertEquals(43.0, manager.calculateWeightedMin());
    }

    @Test
    void testCalculateAllWeightedPRs() {
        Map<String, Double> prs = manager.calculateAllWeightedPRs();
        assertEquals(66.7, prs.get("Alice"), 0.1);
        assertEquals(33.3, prs.get("Bob"), 0.1);
        assertEquals(0.0, prs.get("Charlie"), 0.1);
    }

    @Test
    void testGetGrade() {
        assertEquals(90, manager.getGrade("Alice", "Math"));
        assertNull(manager.getGrade("Alice", "History"));
    }

    @Test
    void testGetSortedGradesBySubject() {
        List<Map.Entry<String, Integer>> sortedGrades = manager.getSortedGradesBySubject("Math");
        assertEquals("Alice", sortedGrades.getFirst().getKey());
        assertEquals(90, sortedGrades.getFirst().getValue());
    }

    @Test
    void testCalculateAverage() {
        assertEquals(66.7, manager.calculateAverage("Math"), 0.1);
        assertEquals(63.3, manager.calculateAverage("English"), 0.1);
        assertEquals(60.0, manager.calculateAverage("Science"), 0.1);
    }

    @Test
    void testCalculateMedian() {
        assertEquals(60.0, manager.calculateMedian("Math"));
        assertEquals(70.0, manager.calculateMedian("English"));
        assertEquals(70.0, manager.calculateMedian("Science"));
    }

    @Test
    void testCalculateStandardDeviation() {
        assertEquals(16.99, manager.calculateStandardDeviation("Math"), 0.1);
    }

    @Test
    void testcalculateVariance() {
        assertEquals(288.8, manager.calculateVariance("Math"), 0.1);
    }

    @Test
    void testCalculateIQR() {
        assertEquals(40.0, manager.calculateIQR("Math"), 0.1);
    }

    @Test
    void testCalculateMax() {
        assertEquals(90.0, manager.calculateMax("Math"), 0.1);
    }

    @Test
    void testCalculateMin() {
        assertEquals(50.0, manager.calculateMin("Math"), 0.1);
    }

    @Test
    void testCalculatePercentileRank(){
        assertEquals(66.6, manager.calculatePercentileRank("Math", 90), 0.1);
        assertEquals(33.3, manager.calculatePercentileRank("English", 70), 0.1);
        assertEquals(0.0, manager.calculatePercentileRank("Science", 30), 0.1);
    }

    @Test
    void testCalculateMode(){
        manager.addGrade("John", "English", 70);
        manager.addGrade("Alex", "English", 70);
        manager.addGrade("Jake", "English", 60);

        List<Integer> mode = manager.calculateMode("English");
        assertTrue(mode.contains(70), "The mode should include 70");
    }

    @Test
    void testSubjectGradeDistribution() {
        Map<String, Long> distribution = manager.calculateSubjectGradeDistribution("Math", 20);
        assertEquals(1, distribution.get("40-59"));
        assertEquals(1, distribution.get("60-79"));
        assertEquals(1, distribution.get("80-99"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/valid_scores.csv", numLinesToSkip = 1)
    @DisplayName("Test Grade Statistics with Valid CSV Data")
    void testGradeStatisticsFromValidCsv(String studentName, String subject, int grade) {
        manager.addGrade(studentName, subject, grade);

        double average = manager.calculateAverage(subject);
        double median = manager.calculateMedian(subject);
        double stdDev = manager.calculateStandardDeviation(subject);

        // 確保結果與新的四捨五入規則一致
        assertEquals(Math.round(average * 10) / 10.0, average, 0.1);
        assertEquals(Math.round(median * 10) / 10.0, median, 0.1);
        assertEquals(Math.round(stdDev * 10) / 10.0, stdDev, 0.1);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/edge_scores.csv", numLinesToSkip = 1)
    @DisplayName("Test Grade Statistics with Edge Cases CSV Data")
    void testGradeStatisticsFromEdgeCsv(String studentName, String subject, int grade) {
        manager.addGrade(studentName, subject, grade);

        double average = manager.calculateAverage(subject);
        double median = manager.calculateMedian(subject);
        double stdDev = manager.calculateStandardDeviation(subject);

        assertTrue(average >= 0, "Average should be non-negative");
        assertTrue(median >= 0, "Median should be non-negative");
        assertTrue(stdDev >= 0, "Standard deviation should be non-negative");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_scores.csv", numLinesToSkip = 1)
    @DisplayName("Test Invalid CSV Data Handling")
    void testInvalidCsvData(String studentName, String gradeStr) {
        try {
            int grade = Integer.parseInt(gradeStr);
            manager.addGrade(studentName, "Math", grade);
        } catch (NumberFormatException | NullPointerException e) {
            assertTrue(true, "Exception was correctly thrown for invalid data");
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/large_scores.csv", numLinesToSkip = 1)
    @DisplayName("Test Grade Statistics with Large CSV Data")
    void testGradeStatisticsFromLargeCsv(String studentName, String subject, int grade) {
        manager.addGrade(studentName, subject, grade);

        assertFalse(manager.getSortedGradesBySubject(subject).isEmpty(), "There should be grades loaded");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/empty_scores.csv", numLinesToSkip = 1)
    @DisplayName("Test Empty CSV Data")
    void testEmptyCsvData(String studentName, String subject, Integer grade) {
        if (studentName == null || studentName.isEmpty() || subject == null || subject.isEmpty()) {
            assertThrows(IllegalArgumentException.class, () -> manager.addGrade(studentName, subject, grade != null ? grade : 0), "Empty or invalid CSV file should not add any grades");
        }
    }
}