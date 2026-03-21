package org.torinelli.parser;

import java.util.List;
import java.util.Map;

public interface HandProcessor {

    void process(HandProcessingContext ctx, Map<String, List<String>> sections);
}

