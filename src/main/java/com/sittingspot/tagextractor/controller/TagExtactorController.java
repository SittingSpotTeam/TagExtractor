package com.sittingspot.tagextractor.controller;

import com.sittingspot.tagextractor.DTO.SentimentAnalysisDTO;
import com.sittingspot.tagextractor.models.Review;
import com.sittingspot.tagextractor.models.Label;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths; 

@RestController
@RequestMapping("tag-extractor/api/v1")
public class TagExtactorController {
    private List<String> tag_list = Arrays.asList(
                                        "Comfortable","Uncomfortable", "Good", "Bad","nice",
                                            "Clean", "Dirty", "Noisy", "Quiet", "Water", "Fountain",
                                            "Fantastic","Amazing","Perfect","Terrific","Superb","Delightful","Incredible",
                                            "Terrible","Horrible","Broken","Poor","Dreadful","Awful",
                                            "Hate","Love","Like","Dislike");

    @PostMapping
    public List<Label> search(@RequestBody Review review) {
        //TODO MAYBE CHANGE FORMAT; SEE WHAT TO DO WITH THE SCORE
        return new ArrayList<Label>(sentimentAnalysis(review).labels());
    }

    // private SentimentAnalysisDTO sentimentAnalysis(Review review){
    private SentimentAnalysisDTO sentimentAnalysis(Review review){

        Path path = Paths.get("/AnalyzeSentiment.py");
        System.out.println(path.toAbsolutePath());
        System.out.println(review);

        //extracting labels
        Set<Label> extracted_labels = new HashSet<Label>();
        String corpus = review.corpus().toLowerCase();
        for (String st : tag_list) {
            if(corpus.contains(st.toLowerCase())){
                extracted_labels.add(new Label(st));
            }
        }
        System.out.println(extracted_labels);

        //computing score
        try {
            // Use ProcessBuilder to start the Python process
            // ProcessBuilder pb = new ProcessBuilder("python3", "src/main/java/com/sittingspot/tagextractor/controller/AnalyzeSentiment.py", review.corpus());
            ProcessBuilder pb = new ProcessBuilder("python3", path.toAbsolutePath().toString(), review.corpus());
            
            pb.redirectErrorStream(true); // Redirect error stream to output stream
            Process process = pb.start();

            // Capture the output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // Wait for the process to complete
            process.waitFor();

            // Extract sentiment from output (assuming JSON or simple text output)
            String sentiment = output.toString().trim(); // Adjust parsing based on actual output format
            String[] sent = sentiment.split("[ ,}]");
            Float score = Float.parseFloat(sent[10]);
            System.out.println("Score:"+score);

            return new SentimentAnalysisDTO(extracted_labels, sentiment);

        } catch (Exception e) {
            System.out.println("An error occurred:");
            e.printStackTrace();
            return new SentimentAnalysisDTO(extracted_labels, null);
        }

    }
}