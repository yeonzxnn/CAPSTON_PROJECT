package com.example.beautyinside.network;

import java.util.List;

public class ResultResponse {

    private boolean success;
    private String result_image_url;
    private Analysis analysis;
    private List<Recommendation> recommendations;

    public boolean isSuccess() {
        return success;
    }

    public String getResultImageUrl() {
        return result_image_url;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
}
