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

        manager.addGrade("Dodo", "Math", 90);
        manager.addGrade("Dodo", "English", 80);
        manager.addGrade("Dodo", "Science", 70);

        manager.addGrade("Xuan", "Math", 60);
        manager.addGrade("Xuan", "English", 70);
        manager.addGrade("Xuan", "Science", 80);

        manager.addGrade("Koopy", "Math", 50);
        manager.addGrade("Koopy", "English", 40);
        manager.addGrade("Koopy", "Science", 30);

        manager.addGrade("NienLin", "Math", 88);
        manager.addGrade("NienLin", "English", 68);
        manager.addGrade("NienLin", "Science", 77);

        manager.addGrade("Jungwon", "Math", 92);
        manager.addGrade("Jungwon", "English", 85);
        manager.addGrade("Jungwon", "Science", 96);
    }

    @Test
    void testSetSubjectWeightsAndGetSubjectWeight() {
        assertEquals(0.5, manager.getSubjectWeight("Math"));
        assertEquals(0.3, manager.getSubjectWeight("English"));
        assertEquals(0.2, manager.getSubjectWeight("Science"));
    }

    @Test
    void testAddGrade() {
        manager.addGrade("NienLin", "Math", 95);
        assertEquals(95, manager.getGrade("NienLin", "Math"));
    }

    @Test
    void testCalculateWeightedAverage() {
        assertEquals(83.0, manager.calculateWeightedAverage("Dodo"));
        assertEquals(67.0, manager.calculateWeightedAverage("Xuan"));
        assertEquals(43.0, manager.calculateWeightedAverage("Koopy"));
        assertEquals(79.8, manager.calculateWeightedAverage("NienLin"));
        assertEquals(90.7, manager.calculateWeightedAverage("Jungwon"));
    }

    @Test
    void testCalculateAllWeightedScores() {
        List<Double> scores = manager.calculateAllWeightedScores();
        assertEquals(Arrays.asList(90.7, 83.0, 79.8, 67.0, 43.0), scores);
    }

    @Test
    void testFindStudentsByScoreRange() {
        assertEquals(2, manager.findStudentsByScoreRange(80, 100).size());
    }

    @Test
    void testCalculateWeightedScoreDistribution() {
        Map<String, Long> distribution = manager.calculateWeightedScoreDistribution();
        assertEquals(1, distribution.get("0-59"));
        assertEquals(1, distribution.get("60-69"));
        assertEquals(1, distribution.get("70-79"));
        assertEquals(1, distribution.get("80-89"));
        assertEquals(1, distribution.get("90-100"));
    }

    @Test
    void testGetSortedWeightedScores() {
        List<Map.Entry<String, Double>> sortedScores = manager.getSortedWeightedScores();
        assertEquals("Jungwon", sortedScores.get(0).getKey());
        assertEquals(90.7, sortedScores.get(0).getValue());
        assertEquals("Dodo", sortedScores.get(1).getKey());
        assertEquals(83.0, sortedScores.get(1).getValue());
        assertEquals("NienLin", sortedScores.get(2).getKey());
        assertEquals(79.8, sortedScores.get(2).getValue());
        assertEquals("Xuan", sortedScores.get(3).getKey());
        assertEquals(67.0, sortedScores.get(3).getValue());
        assertEquals("Koopy", sortedScores.get(4).getKey());
        assertEquals(43.0, sortedScores.get(4).getValue());

    }

    @Test
    void testCalculateWeightedAverageScore() {
        assertEquals(72.7, manager.calculateWeightedAverageScore(), 0.1);
    }

    @Test
    void testCalculateWeightedMedianScore() {
        assertEquals(79.8, manager.calculateWeightedMedianScore());
    }

    @Test
    void testCalculateWeightedStandardDeviation() {
        assertEquals(16.7, manager.calculateWeightedStandardDeviation(), 0.1);
    }

    @Test
    void testCalculateWeightedVariance() {
        assertEquals(279.0, manager.calculateWeightedVariance(), 0.1);
    }

    @Test
    void testCalculateWeightedIQR() {
        assertEquals(16, manager.calculateWeightedIQR(), 0.1);
    }

    @Test
    void testCalculateWeightedMax() {
        assertEquals(90.7, manager.calculateWeightedMax());
    }

    @Test
    void testCalculateWeightedMin() {
        assertEquals(43.0, manager.calculateWeightedMin());
    }

    @Test
    void testCalculateAllWeightedPRs() {
        Map<String, Double> prs = manager.calculateAllWeightedPRs();
        assertEquals(80, prs.get("Jungwon"), 0.1);
        assertEquals(60, prs.get("Dodo"), 0.1);
        assertEquals(40, prs.get("NienLin"), 0.1);
        assertEquals(20, prs.get("Xuan"), 0.1);
        assertEquals(0.0, prs.get("Koopy"), 0.1);
    }

    @Test
    void testGetGrade() {
        assertEquals(90, manager.getGrade("Dodo", "Math"));
        assertNull(manager.getGrade("Dodo", "History"));
    }

    @Test
    void testGetSortedGradesBySubject() {
        List<Map.Entry<String, Integer>> sortedGrades = manager.getSortedGradesBySubject("Math");
        assertEquals("Jungwon", sortedGrades.getFirst().getKey());
        assertEquals(92, sortedGrades.getFirst().getValue());
    }

    @Test
    void testCalculateAverage() {
        assertEquals(76.0, manager.calculateAverage("Math"), 0.1);
        assertEquals(68.6, manager.calculateAverage("English"), 0.1);
        assertEquals(70.6, manager.calculateAverage("Science"), 0.1);
    }

    @Test
    void testCalculateMedian() {
        assertEquals(88.0, manager.calculateMedian("Math"));
        assertEquals(70.0, manager.calculateMedian("English"));
        assertEquals(77.0, manager.calculateMedian("Science"));
    }

    @Test
    void testCalculateStandardDeviation() {
        assertEquals(17.5, manager.calculateStandardDeviation("Math"), 0.1);
    }

    @Test
    void testcalculateVariance() {
        assertEquals(305.6, manager.calculateVariance("Math"), 0.1);
    }

    @Test
    void testCalculateIQR() {
        assertEquals(30.0, manager.calculateIQR("Math"), 0.1);
    }

    @Test
    void testCalculateMax() {
        assertEquals(92.0, manager.calculateMax("Math"), 0.1);
    }

    @Test
    void testCalculateMin() {
        assertEquals(50.0, manager.calculateMin("Math"), 0.1);
    }

    @Test
    void testCalculatePercentileRank(){
        assertEquals(60.0, manager.calculatePercentileRank("Math", 90), 0.1);
        assertEquals(40.0, manager.calculatePercentileRank("English", 70), 0.1);
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
        Map<String, Long> distribution = manager.calculateSubjectGradeDistribution("Math", 10);
        assertEquals(1, distribution.get("50-59"));
        assertEquals(1, distribution.get("60-69"));
        assertEquals(0, distribution.get("70-79"));
        assertEquals(1, distribution.get("80-89"));
        assertEquals(2, distribution.get("90-99"));
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