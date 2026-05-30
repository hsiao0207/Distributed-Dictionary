/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DictionaryManager {
    
    private final ConcurrentHashMap<String, List<String>> dictionary = new ConcurrentHashMap<>();
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private String dictionaryPath;

    public DictionaryManager(String path) {
        this.dictionaryPath = path;
    }

    public void loadDictionary() {
        lock.writeLock().lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String word = parts[0].trim().toLowerCase();
                    String[] meaningsArray = parts[1].split(",");
                    List<String> meaningsList = new ArrayList<>();
                    for (String m : meaningsArray) {
                        meaningsList.add(m.trim());
                    }
                    dictionary.put(word, meaningsList);
                }
            }
            System.out.println("Dictionary loaded successfully, total words: " + dictionary.size() + ".");
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found. A new dictionary data structure will be initialized.");
        } catch (IOException e) {
            System.err.println("Error occurred while reading dictionary file.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveDictionary() {
        lock.readLock().lock();
        try(PrintWriter writer = new PrintWriter(new FileWriter(dictionaryPath))) {
            for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
                String word = entry.getKey();
                String meanings = String.join(",", entry.getValue());
                writer.println(word + ":" + meanings);
            }
        } catch (IOException e) {
            System.err.println("Error occurred while saving dictionary.");
        }finally {
            lock.readLock().unlock();
        }
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    public List<String> query(String word) {
        return dictionary.get(word.toLowerCase());
    }

    public String add(String word, List<String> meanings) {
        if (dictionary.containsKey(word.toLowerCase())) {
            return "DUPLICATE";
        } else {
            if (meanings == null || meanings.isEmpty()) {
                return "INVALID_MEANING";
            }
            dictionary.put(word.toLowerCase(), new ArrayList<>(meanings));
            saveDictionary();
            return "SUCCESS";
        }
    }

    public String remove(String word) {
        if (dictionary.containsKey(word.toLowerCase())) {
            dictionary.remove(word.toLowerCase());
            saveDictionary();
            return "SUCCESS";
        } else {
            return "NOT_FOUND";
        }
    }

    public String addMeaning(String word, String newMeaning) {
        if (dictionary.containsKey(word.toLowerCase())) {
            List<String> oldMeanings = dictionary.get(word.toLowerCase());
            if (oldMeanings.stream().anyMatch(m -> m.equalsIgnoreCase(newMeaning))) {
                return "DUPLICATE";
            } else {
                oldMeanings.add(newMeaning);
                saveDictionary();
                return "SUCCESS";
            }
        } else {
            return "NOT_FOUND";
        }
    }

    public String update(String word, String oldMeaning, String newMeaning) {
        if (dictionary.containsKey(word.toLowerCase())) {
            List<String> existMeanings = dictionary.get(word.toLowerCase());
            for (int i = 0; i < existMeanings.size(); i++) {
                if (existMeanings.get(i).equalsIgnoreCase(oldMeaning)) {
                    existMeanings.set(i, newMeaning);
                    saveDictionary();
                    return "SUCCESS";
                }
            }
            return "NOT_FOUND_OLD_MEANING";
        } else {
            return "NOT_FOUND";
        }
    }
    
}
