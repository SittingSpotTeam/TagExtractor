package com.sittingspot.tagextractor.DTO;
import java.util.*;

import com.sittingspot.tagextractor.models.Label;

// public record SentimentAnalysisDTO(HashSet<Label> labels, float score) {
// }

public record SentimentAnalysisDTO(Set<Label> labels, String score) {
    public SentimentAnalysisDTO(Set<Label> labels, String score){
        this.labels=labels;
        this.score=score;
    }
}
