package de.tub.dima.policyliner.entities;

import java.util.List;

public class JsonQuasiIdentifiers {
    private List<JsonQuasiIdentifier> quasiIdentifiers;

    public JsonQuasiIdentifiers(List<JsonQuasiIdentifier> quasiIdentifiers) {
        this.quasiIdentifiers = quasiIdentifiers;
    }

    public List<JsonQuasiIdentifier> getQuasiIdentifiers() {
        return quasiIdentifiers;
    }
    public void setQuasiIdentifiers(List<JsonQuasiIdentifier> quasiIdentifiers) {
        this.quasiIdentifiers = quasiIdentifiers;
    }
}
