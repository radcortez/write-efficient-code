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

@Path("/")
public class GreetingResource {
    private final List<String> names;

    public GreetingResource() {
        Faker faker = new Faker();
        Name name = faker.name();
        names = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            names.add(name.firstName());
        }
    }

    @GET
    @Path("/one/{greet}")
    public Response one(@PathParam("greet") String greet) {
        return Response.ok(solutionOne(greet, names)).build();
    }

    @GET
    @Path("/two/{greet}")
    public Response two(@PathParam("greet") String greet) {
        return Response.ok(solutionTwo(greet, names)).build();
    }

    @GET
    @Path("/three/{greet}")
    public Response three(@PathParam("greet") String greet) {
        return Response.ok(solutionThree(greet, names)).build();
    }

    List<String> solutionOne(
            String prefix,
            List<String> names) {

        return names.stream()
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return new StringBuilder(prefix).append(" ").append(s).toString();
                    }
                })
                .toList();
    }

    List<String> solutionTwo(String prefix, List<String> names) {
        List<String> newNames = new ArrayList<>();
        for (String name : names) {
            newNames.add(new StringBuilder(prefix).append(name).toString());
        }
        return newNames;
    }

    List<String> solutionThree(String prefix, List<String> names) {
        List<String> newNames = new ArrayList<>((int) ((float) names.size() / 0.75f + 1.0f));
        StringBuilder builder = new StringBuilder().append(prefix).append(" ");
        int length = builder.length();
        for (String name : names) {
            newNames.add(builder.append(name).toString());
            builder.setLength(length);
        }
        return newNames;
    }
}
