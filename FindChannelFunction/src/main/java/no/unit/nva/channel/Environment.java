package no.unit.nva.channel;

import java.util.Optional;

public class Environment {

    public Optional<String> get(String name) {
        String environmentVariable = System.getenv(name);

        if (environmentVariable == null || environmentVariable.isEmpty()) {
            return Optional.empty();
        }

        return  Optional.of(environmentVariable);
    }
}
