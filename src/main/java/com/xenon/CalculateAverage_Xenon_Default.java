package com.xenon;

import com.xenon.utils.Benchmark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class CalculateAverage_Xenon_Default {

    private static final Path FILE = Path.of("measurements.txt");

    public static void main() {
        CalculateAverage_Xenon_Default solution = new CalculateAverage_Xenon_Default();
        Benchmark.execute("Entire process", () -> solution.process(FILE));
    }

    record WeatherStation(String name, double temperature) {
    }

    static class WeatherStationData {
        String name;
        double minTemp;
        double maxTemp;
        double totalTemp;
        int nrOfStations;

        public WeatherStationData(WeatherStation weatherStation) {
            this.name = weatherStation.name();
            this.minTemp = weatherStation.temperature();
            this.maxTemp = weatherStation.temperature();
            this.totalTemp = weatherStation.temperature();
            this.nrOfStations = 1;
        }
    }

    public void process(Path path) {
        HashMap<String, WeatherStationData> weatherStationsData = new HashMap<>();

        try (Stream<String> lines = Files.lines(path)) {
            List<WeatherStation> weatherStations = lines
                    .parallel()
                    .map(this::processLine)
                    .toList();

            for (WeatherStation station : weatherStations) {
                weatherStationsData.compute(station.name(), (_, value) ->
                        value == null
                                ? new WeatherStationData(station)
                                : updatedWeatherStationData(value, station)
                );
            }

            weatherStationsData.forEach((k, v) -> System.out.printf("%s -> min:%.4f, max:%.4f, avg:%.4f (%d times)\n",
                    k, v.minTemp, v.maxTemp, v.totalTemp / v.nrOfStations, v.nrOfStations)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherStation processLine(String line) {
        String[] splits = line.split(";");
        return new WeatherStation(splits[0], Double.parseDouble(splits[1]));
    }

    private WeatherStationData updatedWeatherStationData(WeatherStationData data, WeatherStation station) {
        data.name = station.name();
        data.minTemp = Math.min(data.minTemp, station.temperature());
        data.maxTemp = Math.max(data.maxTemp, station.temperature());
        data.totalTemp += station.temperature();
        data.nrOfStations += 1;
        return data;
    }

}