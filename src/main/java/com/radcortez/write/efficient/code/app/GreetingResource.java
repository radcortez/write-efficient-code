package com.radcortez.write.efficient.code.app;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import net.datafaker.Faker;
import net.datafaker.providers.base.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Path("/greeting")
public class GreetingResource {
    private final List<String> names;

    public GreetingResource() {
        Faker faker = new Faker();
        Name name = faker.name();
        names = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            names.add(name.firstName());
        }
    }

    @GET
    @Path("/{greet}")
    public Response greet(@PathParam("greet") String greet) {
        return Response.ok(solutionOne(greet, names)).build();
    }

    List<String> solutionOne(
            String prefix,
            List<String> names) {

        return names.stream()
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return new StringBuilder(prefix).append(s).toString();
                    }
                })
                .collect(toList());
    }

    List<String> solutionThree(String prefix, List<String> names) {
        List<String> newNames = new ArrayList<>((int) ((float) names.size() / 0.75f + 1.0f));
        StringBuilder builder = new StringBuilder().append("name").append(".");
        for (String name : names) {
            newNames.add(builder.append(name).toString());
            builder.setLength(5);
        }
        return newNames;
    }
}
