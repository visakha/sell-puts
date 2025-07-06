package com.putsellersim.service;

import com.putsellersim.model.PutOption;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionDataService {

    public List<PutOption> loadOptions() {
        try (var reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/options.csv")))) {
            return reader.lines()
                    .skip(1)
                    .map(line -> {
                        var tokens = line.split(",");
                        return new PutOption(
                                tokens[0],
                                LocalDate.parse(tokens[1]),
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3])
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CSV", e);
        }
    }
}
