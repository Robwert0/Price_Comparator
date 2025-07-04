package com.example.price_comparator.service;

import com.example.price_comparator.model.PriceAlert;
import com.example.price_comparator.model.PriceEntry;
import com.example.price_comparator.util.CsvLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

    private final CsvLoader csvLoader;

    public List<PriceEntry> getAllPrices() {
        List<PriceEntry> allEntries = new ArrayList<>();

        allEntries.addAll(csvLoader.loadPriceEntries("lidl_2025-05-01.csv", "Lidl", LocalDate.of(2025, 5, 1)));
        allEntries.addAll(csvLoader.loadPriceEntries("kaufland_2025-05-01.csv", "Kaufland", LocalDate.of(2025, 5, 1)));
        allEntries.addAll(csvLoader.loadPriceEntries("profi_2025-05-01.csv", "Profi", LocalDate.of(2025, 5, 1)));
        allEntries.addAll(csvLoader.loadPriceEntries("lidl_2025-05-08.csv", "Lidl", LocalDate.of(2025, 5, 8)));
        allEntries.addAll(csvLoader.loadPriceEntries("kaufland_2025-05-08.csv", "Kaufland", LocalDate.of(2025, 5, 8)));
        allEntries.addAll(csvLoader.loadPriceEntries("profi_2025-05-08.csv", "Profi", LocalDate.of(2025, 5, 8)));

        return allEntries;
    }

    public List<PriceEntry> getBestValueAlternative(String productName){
        List<PriceEntry> allPrices = getAllPrices();

        Optional<PriceEntry> original = allPrices.stream()
                .filter(p -> p.getProduct().getName().toLowerCase().contains(productName.toLowerCase()))
                .findFirst();

        if (original.isEmpty()) return new ArrayList<>();

        String targetCategory = original.get().getProduct().getCategory();

        return allPrices.stream()
                .filter(p -> p.getProduct().getCategory().equalsIgnoreCase(targetCategory))
                .sorted(Comparator.comparing(PriceEntry::getValuePerUnit))
                .limit(5)
                .collect(Collectors.toList());
    }

    public PriceEntry getBestBuy(String productName){
        List<PriceEntry> allPrices = getAllPrices();

        return allPrices.stream()
                .filter(p -> p.getProduct().getName().toLowerCase().contains(productName.toLowerCase()))
                .min(Comparator.comparing(PriceEntry::getValuePerUnit))
                .orElse(null);
    }

    public List<PriceEntry> checkAlerts(PriceAlert alert){
        List<PriceEntry> allPrices = getAllPrices();

        return allPrices.stream()
                .filter(p -> p.getProduct().getName().toLowerCase().contains(alert.getProductName().toLowerCase()))
                .filter(p -> p.getPrice() <= alert.getTargetPrice())
                .collect(Collectors.toList());
    }

    @Getter
    private final List<PriceAlert> alerts = new ArrayList<>();

    public void addAlert(PriceAlert alert){
        alerts.add(alert);
        System.out.println(alert);
    }
}
