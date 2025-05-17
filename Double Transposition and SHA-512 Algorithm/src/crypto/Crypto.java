package crypto;

import java.util.Arrays;

public class Crypto {
    private String firstKey;
    private String secondKey;

    // firstKey 1st key for 1st time transposition
    // secondKey 2nd key for 2nd time transposition

    // ------------ set key value to class properties --------------
    public Crypto(String firstKey, String secondKey) {
        this.firstKey = firstKey;
        this.secondKey = secondKey;
    }


    // ----------  encrypt the plain text ----------
    public String encrypt(String plaintext) {
        // first transposition
        String intermediateText = doubleTransposition(plaintext, firstKey, true);

        // second transposition
        String finalText =  doubleTransposition(intermediateText, secondKey, true);

        return finalText;
    }


    // ---------- decrypt the cipher text ----------
    public String decrypt(String ciphertext) {
        // reverse the second transposition
        String intermediateText = doubleTransposition(ciphertext, secondKey, false);

        // reverse the first transposition
        String finaldecryptedText = doubleTransposition(intermediateText, firstKey, false);

        return finaldecryptedText;
    }


    // ----------  perform transposition (encrypt/decrypt based on condition ) ----------
    private String doubleTransposition(String text, String key, boolean encrypt) {
        int[] keyOrder = getKeyOrder(key);

        int columns = key.length();
        int rows = (int) Math.ceil((double) text.length() / columns);

        char[][] grid = new char[rows][columns];
        for (char[] row : grid) {
            Arrays.fill(row, ' '); // Fill grid with spaces
        }

        // ----------- encryption ------------
        if (encrypt) {
            // now fill the grid row by row
            int index = 0;
            for (int i = 0; i < rows && index < text.length(); i++) {
                for (int j = 0; j < columns && index < text.length(); j++) {
                    grid[i][j] = text.charAt(index++);
                }
            }

            // now read the grid column by column based on key order
            StringBuilder result = new StringBuilder();
            for (int column : keyOrder) {
                for (int i = 0; i < rows; i++) {
                    result.append(grid[i][column]);
                }
            }
            return result.toString();

            // --------------- decrypt ---------------
        } else {
            // now fill the grid column by column based on key order
            int index = 0;
            for (int column : keyOrder) {
                for (int i = 0; i < rows && index < text.length(); i++) {
                    grid[i][column] = text.charAt(index++);
                }
            }

            // Read the grid row by row
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    result.append(grid[i][j]);
                }
            }
            return result.toString().trim();
        }
    }


    // find key order in sorted key
    private int[] getKeyOrder(String key) {
        Character[] keyArray = new Character[key.length()];
        for (int i = 0; i < key.length(); i++) {
            keyArray[i] = key.charAt(i);
        }

        Character[] sortedKey = keyArray.clone();
        Arrays.sort(sortedKey);

        int[] order = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            order[i] = Arrays.asList(sortedKey).indexOf(keyArray[i]);
            sortedKey[order[i]] = '\0';
        }

        return order;
    }

}
