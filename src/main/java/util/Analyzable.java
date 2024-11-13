package util;

import lombok.NonNull;
import tech.tablesaw.api.Table;

public interface Analyzable {
    Table processResults(double @NonNull [] @NonNull [] results);

    void plotResults(@NonNull Table table);
}
