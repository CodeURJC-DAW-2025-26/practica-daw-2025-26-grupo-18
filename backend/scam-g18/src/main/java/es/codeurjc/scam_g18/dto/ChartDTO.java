package es.codeurjc.scam_g18.dto;

import java.util.List;

public record ChartDTO(
        String chartTitle,
        String chartType,
        List<String> chartLabels,
        List<Double> chartValues
) {}
