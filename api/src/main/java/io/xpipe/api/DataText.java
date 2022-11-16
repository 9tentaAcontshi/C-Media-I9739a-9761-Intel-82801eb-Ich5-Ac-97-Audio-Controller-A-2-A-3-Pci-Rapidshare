package io.xpipe.api;

import java.util.List;
import java.util.stream.Stream;

public interface DataText extends DataSource {

    List<String> readAllLines();

    List<String> readLines(int maxLines);

    Stream<String> lines();

    String readAll();

    String read(int maxCharacters);
}
