package com.radcortez.write.efficient.code;

import net.datafaker.Faker;
import net.datafaker.providers.base.Name;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Fork(value = 1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class Prefixes {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[] {"Prefixes"});
    }

    @org.openjdk.jmh.annotations.State(value = Scope.Benchmark)
    public static class State {
        String prefix = "Hi, ";
        List<String> names = generate();

        static List<String> generate() {
            List<String> names = new ArrayList<>();
            Faker faker = new Faker();
            Name name = faker.name();
            for (int i = 0; i < 10000; i++) {
                names.add(name.firstName());
            }
            return names;
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void solutionOne(State state, Blackhole blackhole) {
        blackhole.consume(solutionOne(state.prefix, state.names));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void solutionTwo(State state, Blackhole blackhole) {
        blackhole.consume(solutionTwo(state.prefix, state.names));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void solutionThree(State state, Blackhole blackhole) {
        blackhole.consume(solutionThree(state.prefix, state.names));
    }

    List<String> solutionOne(
            String prefix,
            List<String> names) {
        return names.stream()
                .map(name -> prefix + "." + name)
                .collect(toList());
    }

    List<String> solutionTwo(String prefix, List<String> names) {
        List<String> newNames = new ArrayList<>();
        for (String name : names) {
            newNames.add(new StringBuilder(prefix).append(".").append(name).toString());
        }
        return newNames;
    }

    List<String> solutionThree(String prefix, List<String> names) {
        List<String> newNames = new ArrayList<>((int) ((float) names.size() / 0.75f + 1.0f));
        StringBuilder builder = new StringBuilder().append(prefix).append(" ");
        for (String name : names) {
            newNames.add(builder.append(name).toString());
            builder.setLength(5);
        }
        return newNames;
    }
}
