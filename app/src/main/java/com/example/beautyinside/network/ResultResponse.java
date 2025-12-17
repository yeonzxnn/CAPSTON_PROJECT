package com.example.beautyinside.network;

public class ResultResponse {

    private String ai_summary;
    private String recommend_procedure;
    private String after_image_url;
    private String hospital_name;
    private String hospital_location;
    private String doctor_name;
    private String procedure_detail;
    private String review_text;

    public String getAiSummary() { return ai_summary; }
    public String getRecommendProcedure() { return recommend_procedure; }
    public String getAfterImageUrl() { return after_image_url; }
    public String getHospitalName() { return hospital_name; }
    public String getHospitalLocation() { return hospital_location; }
    public String getDoctorName() { return doctor_name; }
    public String getProcedureDetail() { return procedure_detail; }
    public String getReviewText() { return review_text; }
}
