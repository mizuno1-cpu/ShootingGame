package rank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RankingManager {
    private static final String FILE_NAME = "ranking.csv";
    private static RankingManager instance;
    private List<ScoreEntry> scores = new ArrayList<>();
    private final int MAX_ENTRIES = 7;

    private RankingManager() {
        loadFromFile();  // 起動時に読み込み
    }

    public static RankingManager getInstance() {
        if (instance == null) {
            instance = new RankingManager();
        }
        return instance;
    }

    public void addScore(ScoreEntry entry) {
        scores.add(entry);
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        if (scores.size() > MAX_ENTRIES) {
            scores = scores.subList(0, MAX_ENTRIES);
        }
        saveToFile();
    }

    public List<ScoreEntry> getScores() {
        if (scores == null) {
            return new ArrayList<>(); // ← null の代わりに空リストを返す
        }
        return new ArrayList<>(scores);
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            scores.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new ScoreEntry(name, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("ランキング読み込み失敗: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry entry : scores) {
                bw.write(entry.getName() + "," + entry.getScore());
                bw.newLine();
                System.err.println("保存済の数値: " + entry.getName() + "," + entry.getScore());
            }
        } catch (IOException e) {
            System.err.println("ランキング保存失敗: " + e.getMessage());
        }
    }
}