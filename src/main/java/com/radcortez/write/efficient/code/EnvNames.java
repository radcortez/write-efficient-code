package com.radcortez.write.efficient.code;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.Serializable;
import java.util.Map;

import static io.smallrye.config.common.utils.StringUtil.*;
import static io.smallrye.config.common.utils.StringUtil.isAsciiLetterOrDigit;
import static java.lang.Character.toLowerCase;

@Fork(value = 1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class EnvNames {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[] {"EnvNames"});
    }

    @org.openjdk.jmh.annotations.State(value = Scope.Benchmark)
    public static class StateOriginal {
        String name = "my.configuration.property";
        Map<String, String> properties = Map.of(name, "value");
    }

    @org.openjdk.jmh.annotations.State(value = Scope.Benchmark)
    public static class State {
        String name = "my.configuration.property";
        Map<EnvName, String> properties = Map.of(new EnvName(name), "value");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void getValueOriginal(StateOriginal state, Blackhole blackhole) {
        blackhole.consume(getValueOriginal(state.name, state.properties));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void getValue(State state, Blackhole blackhole) {
        blackhole.consume(getValue(state.name, state.properties));
    }

    private String getValueOriginal(String name, Map<String, String> properties) {
        if (name == null) {
            return null;
        }

        // exact match
        String value = properties.get(name);
        if (value != null) {
            return value;
        }

        // replace non-alphanumeric characters by underscores
        String sanitizedName = replaceNonAlphanumericByUnderscores(name);
        value = properties.get(sanitizedName);
        if (value != null) {
            return value;
        }

        // replace non-alphanumeric characters by underscores and convert to uppercase
        return properties.get(sanitizedName.toUpperCase());
    }

    private String getValue(String name, Map<EnvName, String> properties) {
        return properties.get(new EnvName(name));
    }

    public static final class EnvName implements Serializable {
        private static final long serialVersionUID = -2679716955093904512L;

        private final String name;

        public EnvName(final String name) {
            assert name != null;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final EnvName that = (EnvName) o;
            return equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            int h = 0;
            int length = name.length();
            if (length >= 2) {
                if (name.charAt(length - 1) == '_' && name.charAt(length - 2) == '_') {
                    length = length - 1;
                }
            }

            for (int i = 0; i < length; i++) {
                char c = name.charAt(i);
                if (i == 0 && length > 1) {
                    // The first '%' or '_' is meaninful because it represents a profiled property name
                    if ((c == '%' || c == '_') && isAsciiLetterOrDigit(name.charAt(i + 1))) {
                        h = 31 * h + 31;
                        continue;
                    }
                }

                if (isAsciiLetterOrDigit(c)) {
                    h = 31 * h + toLowerCase(c);
                }
            }
            return h;
        }

        @SuppressWarnings("squid:S4973")
        static boolean equals(final String name, final String other) {
            //noinspection StringEquality
            if (name == other) {
                return true;
            }

            if (name.isEmpty() && other.isEmpty()) {
                return true;
            }

            if (name.isEmpty() || other.isEmpty()) {
                return false;
            }

            char n;
            char o;

            int matchPosition = name.length() - 1;
            for (int i = other.length() - 1; i >= 0; i--) {
                if (matchPosition == -1) {
                    return false;
                }

                o = other.charAt(i);
                n = name.charAt(matchPosition);

                // profile
                if (i == 0 && (o == '%' || o == '_')) {
                    if (n == '%' || n == '_') {
                        return true;
                    }
                }

                if (o == '.') {
                    if (n != '.' && n != '-' && n != '_' && n != '/') {
                        return false;
                    }
                } else if (o == '-') {
                    if (n != '.' && n != '-' && n != '_' && n != '/') {
                        return false;
                    }
                } else if (o == '"') {
                    if (n != '"' && n != '_') {
                        return false;
                    } else if (n == '_' && name.length() - 1 == matchPosition) {
                        matchPosition = name.lastIndexOf("_", matchPosition - 1);
                        if (matchPosition == -1) {
                            return false;
                        }
                    }
                } else if (o == ']') {
                    if (n != ']' && n != '_') {
                        return false;
                    }
                    int beginIndexed = other.lastIndexOf('[', i);
                    if (beginIndexed != -1) {
                        int range = i - beginIndexed - 1;
                        if (name.lastIndexOf('_', matchPosition - 1) == matchPosition - range - 1
                                || name.lastIndexOf('[', matchPosition - 1) == matchPosition - range - 1) {
                            if (isNumeric(other, beginIndexed + range, i)
                                    && isNumeric(name, matchPosition - range, matchPosition)) {
                                matchPosition = matchPosition - range - 2;
                                i = i - range - 1;
                                continue;
                            }
                        }
                    }
                    return false;
                } else if (o == '_') {
                    if (isAsciiLetterOrDigit(n)) {
                        return false;
                    } else if (n == '"' && other.length() - 1 == i) {
                        i = other.lastIndexOf("_", i - 1);
                        if (i == -1) {
                            return false;
                        }
                    }
                } else if (!isAsciiLetterOrDigit(o)) {
                    if (o != n && n != '_') {
                        return false;

                    }
                } else if (toLowerCase(o) != toLowerCase(n)) {
                    return false;
                }
                matchPosition--;
            }

            return matchPosition <= 0;
        }
    }
}
