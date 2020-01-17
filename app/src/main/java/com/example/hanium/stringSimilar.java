package com.example.hanium;

public class stringSimilar {

    public static Point stringsSimilar(String s1, String s2) {
        Point point=new Point();
        int characterChanges = getLevenshteinDistance(s1, s2);
        int wordChanges = getWordChanges(s1, s2);
        int characterCountChange = Math.abs(s1.length() - s2.length());
        int wordCountChange = Math.abs(s1.split(" ").length - s2.split(" ").length);
        int similarity1 = 100 - characterChanges * 100 / s1.length();
        int similarity2 = 100 - wordChanges * 100 / (s1.split(" ").length > s2.split(" ").length ? s1.split(" ").length : s2.split(" ").length);
        int similarity3 = 100 - characterCountChange * 100 / s1.length();
        int similarity4 = 100 - wordCountChange * 100 / s1.split(" ").length;
        int similarity = (int) Math.round(similarity1 + similarity2*2 + similarity3 + similarity4) / 5;
        point.setData(similarity1,similarity2,similarity3,similarity4,similarity);
        return point;
    }
    public static int getLevenshteinDistance(String s1, String s2) {
        int[][] d = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            d[0][j] = j;
        }

        for (int j = 1; j <= s2.length(); j++) {
            for (int i = 1; i <= s1.length(); i++) {
                int cost = 0;
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                d[i][j] = getMinimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        return d[s1.length() - 1][s2.length() - 1];
    }

    public static int getWordChanges(String s1, String s2) {
        int similarityThreshold = 50;
        int wordChanges = 0;

        s1 = s1.toLowerCase().replace(".", "").replace(",", "").replace(";", "");
        s2 = s2.toLowerCase().replace(".", "").replace(",", "").replace(";", "");

        //Loop through each word in s1
        for (int i = 0; i < s1.split(" ").length; i++) {
            boolean exists = false;
            //Search for i'th word in s1 in s2
            for (int j = 0; j < s2.split(" ").length; j++) {
                //Is the word misspelled?
                if ((getLevenshteinDistance(s1.split(" ")[i], s2.split(" ")[j]) * 100 / s1.split(" ")[i].length()) < similarityThreshold) {
                    exists = true;
                    break;
                }
            }

            //If the word does not exist, increment wordChanges
            if (!exists) {
                wordChanges++;
            }
        }

        return wordChanges;
    }

    private static int getMinimum( int i, int j, int k ) {
        if( i < j ) return i < k ? i : k;
        else return j < k ? j : k;
    }
}
