package com.sittingspot.tagextractor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sittingspot.tagextractor.DTO.SentimentAnalysisDTO;
import com.sittingspot.tagextractor.models.Review;
import com.sittingspot.tagextractor.models.Label;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class TagExtactorController {

    private List<String> tag_list = Arrays.asList(
                                        "Comfortable","Uncomfortable", "Good", "Bad","nice",
                                            "Clean", "Dirty", "Noisy", "Quiet", "Water", "Fountain",
                                            "Fantastic","Amazing","Perfect","Terrific","Superb","Delightful","Incredible",
                                            "Terrible","Horrible","Broken","Poor","Dreadful","Awful",
                                            "Hate","Love","Like","Dislike");

    @Value("${sittingspot.sittingspotdl.url}")
    private String sittingspotdlurl;

    @PostMapping("/{Id}")
    public List<Label> search(@PathVariable String Id, @RequestBody String corpus) throws IOException, InterruptedException {
        //TODO MAYBE CHANGE FORMAT; SEE WHAT TO DO WITH THE SCORE
        var labels = new ArrayList<Label>(sentimentAnalysis(corpus).labels());
        var labels_array = labels.stream().map(x -> x.value());
        String labels_string = "";

        try {
            labels_string = new ObjectMapper().writeValueAsString(labels_array);
        } catch (JsonProcessingException e) {
            labels_string = "[";
            labels_string.concat(labels_array.collect(Collectors.joining(",")));
            labels_string.concat("]");
        }

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + sittingspotdlurl + "/"+Id))
                .PUT(HttpRequest.BodyPublishers.ofString(labels_string))
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return labels;
    }

    // private SentimentAnalysisDTO sentimentAnalysis(Review review){
    private SentimentAnalysisDTO sentimentAnalysis(String corpus){

        Path path = Paths.get("/AnalyzeSentiment.py");
        System.out.println(path.toAbsolutePath());
        System.out.println(corpus);

        //extracting labels
        Set<Label> extracted_labels = new HashSet<Label>();
        corpus = corpus.toLowerCase();
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
            ProcessBuilder pb = new ProcessBuilder("python3", path.toAbsolutePath().toString(), corpus);
            
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