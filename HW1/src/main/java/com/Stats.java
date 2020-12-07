package com;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Stats {
    private static final int NUM_QUERY = 100;
    private static final String[] QUERY_LIST = new String[]{"A two dollar bill from 1953 is worth what",
            "What is franky jonas 's favorite color",
            "Is there an emergency action plan",
            "How much does medical insurance cost for a single person",
            "What is noah cyrus address so you can send her a fan mail",
            "Have many points did wayne getzky get",
            "How wide can vagina 's get",
            "Calories in a lollipop",
            "Pdf password cracker v 3.1 registration key",
            "Another name for karyokinesis",
            "What continent you indonesia on",
            "Do deers live in savanna",
            "What did lice cause in ww1",
            "What women invented the brush",
            "What is the order of electromagnetic radiation decreasing frequency",
            "New tropicana advert",
            "Where did siddartha Gautama live and teach",
            "2003 GMC yukon ignition",
            "What is the value of 1928 us 50 bill",
            "Where is alpha amylase released",
            "Which Band did Krauss join",
            "What is the use of fruits",
            "Where do sugar gliders like to live",
            "How many championships have lakers",
            "What are china major rivers and deserts",
            "What do dholes eat",
            "Stereo wiring diagram for a 1991 Toyota Camry",
            "Names of divine horses on howrse",
            "What is the population of teachers in the US",
            "How did Fidel V Ramos campaigned",
            "What are this 13 systems of human body",
            "What are the Flickertail 's eating habits",
            "What kind of money does croatia have",
            "What do our circulatory system includes",
            "A vaccination produces what kind of immunity",
            "Where does Justin Timbelake live",
            "Is corllins university a credited legal college",
            "What is a myocaridal infaraction",
            "When did bobsledding first enter",
            "500 mg of cinnamon equal how many teaspoons",
            "How many religions in spain",
            "How many lice did bob marly have",
            "What were the domsetic consequences of world war 1",
            "What is the meaning of multitasking and multiuser operating system",
            "The sun and the celestial bodies that orbit the sunincluding planets satellites asteroids comets dust and gas",
            "Tell you about vampire bats",
            "Specifying categories",
            "How much does a rookie card of babe ruth cost",
            "So what is fair trade",
            "What is the secret ingredient of coke",
            "When and how did Slobodan Milosevic die",
            "What do teardrop tattoos on your eyes symbolize",
            "What colors can a Yorkie be",
            "On neopets how can you put a code in a scroll box without the code being in the same box as another",
            "How much does it cost to rent a limo for the day in Madison WI",
            "Where is the 50 story eiffel tower located",
            "What is the length of an amtrak train",
            "Where to give ideas for an invention",
            "You are a big fan or shakira how you can send her a letter",
            "Code to beanie babies",
            "How much is s in roman numerals",
            "What is the origin of the mineral silver",
            "What is the principle behind the bluetooth",
            "What is transmission spectrum",
            "How many nutrons does radon have",
            "What color is rihanna eyes",
            "Who invented metal coil thermometer",
            "How did Rome really begin",
            "What part of california do corbin live in",
            "What is the instrument called that is used to measure tornado 's",
            "How does the future look on being a lawyer",
            "How do you adjust brightness on your laptop",
            "Whenwhere did william morris die",
            "How do sanction help to keep the global community safe and secure",
            "What is the function of command xargs in linux",
            "How does air pollution affect the food webs",
            "What was the name of the Prime Minister in Fiji in 2009",
            "What kind of meaning do proverbs have",
            "How many planet",
            "What is filters harmful substances from lymph",
            "How did park yong ha died",
            "How did people make dresses in colonial times",
            "How many liters are in a cup of milk",
            "How much does marijuana coast per ounce",
            "What is the purpose of a steak plate",
            "What is the name of the four sperm cells",
            "What is the longitudes of delhi",
            "How is columbia related to venezuela",
            "Fiona wood what is she famous for",
            "What sounds does brakes make",
            "What nationality are the people in germany",
            "What niche do plants have",
            "How many gold medals did mia hamm earn",
            "What did Roger Bannister",
            "How much does a medical abortion cost at three weeks",
            "What is rank 2nd in populated city",
            "What do mexicans houses look like",
            "64 millilitres how many litres",
            "You want to text dirty with your boyfriend what should you say",
            "Can you give me example of anecdotes by the filipino writers"};


    public static void main(String[] args) {
        // write your code here
        try {
            // Step 1: Parse the JSON file
            JSONParser parser = new JSONParser();
            JSONObject ddgJsonObject = (JSONObject) parser.parse(new FileReader("hw1.json"));
            JSONObject googleJsonObject = (JSONObject) parser.parse(new FileReader("google.json"));

            // Step 2: Compute each query and write to file
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("hw1.csv"));
            // ********************* Header **********************
            writer.write("Queries, Number of Overlapping Results, Percent Overlap, Spearman Coefficient \n");


            double totalNumOverlap = 0;
            double totalPercentOverlap = 0;
            double totalSpearmanCoeff = 0;

            for (int i = 0; i < NUM_QUERY; i++) {
                // (2.1) Compare urls and ordering
                JSONArray ddgResultList = (JSONArray) ddgJsonObject.get(QUERY_LIST[i]);
                JSONArray googleResultList = (JSONArray) googleJsonObject.get(QUERY_LIST[i]);
                Iterator<JSONObject> ddgIterator = ddgResultList.iterator();
                Iterator<JSONObject> googleIterator = googleResultList.iterator();

                // construct google ranking map
                Map<String, Integer> googleMap = new HashMap<>(); // <url, rank>
                int googleRank = 1;
                while (googleIterator.hasNext()) {
                    String googleUrl = preprocessString(String.valueOf(googleIterator.next()));
                    if (!googleMap.containsKey(googleUrl)) {
                        googleMap.put(googleUrl, googleRank);
                        googleRank++;
                    }
                }

                int numOverlap = 0;
                int ddgRank = 1;
                double dSquareSum = 0;
                String onlyOverlappedUrl = "";
                int onlyOverlappedUrlRanking = -1;
                while (ddgIterator.hasNext()) {
                    String ddgUrl = preprocessString(String.valueOf(ddgIterator.next()));

                    if (googleMap.containsKey(ddgUrl)) {
                        // 1. update total number of overlaps
                        numOverlap++;
                        if (numOverlap == 1) {
                            onlyOverlappedUrl = ddgUrl;
                            onlyOverlappedUrlRanking = ddgRank;
                        }
                        // 2. calculate sum of squared d
                        dSquareSum += Math.pow(Math.abs(googleMap.get(ddgUrl) - ddgRank), 2);
                    }
                    ddgRank++;
                }

                // (2.2) Statistics to calculate averages
                double percentOverlap = (double) numOverlap / googleMap.size() * 100;
                double spearmanCoeff = numOverlap > 1 ? (1 - 6 * dSquareSum / (numOverlap * (Math.pow(numOverlap, 2) - 1))) : (numOverlap == 0 ? 0.0 : googleMap.get(onlyOverlappedUrl) == onlyOverlappedUrlRanking ? 1.0 : 0.0);
                totalNumOverlap += numOverlap;
                totalPercentOverlap += percentOverlap;
                totalSpearmanCoeff += spearmanCoeff;
                // ********************* Stats **********************
                writer.write("Query " + (i + 1) + ", ");
                writer.write(numOverlap + ", ");
                writer.write(percentOverlap + ", ");
                writer.write(spearmanCoeff + "\n");
            }

            // ********************* Averages **********************
            writer.write("Averages, ");
            writer.write(totalNumOverlap / NUM_QUERY + ", ");
            writer.write(totalPercentOverlap / NUM_QUERY + ", ");
            writer.write(totalSpearmanCoeff / NUM_QUERY + "");
            writer.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String preprocessString(String url) {
        // 1. to lower case
        url = url.toLowerCase();
        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        // 2. remove http header
        url = url.replace("https://", "");
        url = url.replace("http://", "");
        // 3. remove www
        url = url.replace("www.", "");
        return url;
    }
}
