package com.backend.ai_nlp_service.service;

import com.backend.ai_nlp_service.dto.JobSkillRequest;
import com.backend.ai_nlp_service.dto.ProcessPostRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SkillExtractor {

    private static final Map<String, String> PHRASE_TO_CANONICAL = new HashMap<>();
    private static int MAX_PHRASE_LEN = 1;

    private static final Pattern PUNCT = Pattern.compile("[\"'.,;:!?()\\-_/\\\\]");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    static {
        loadDictionary();
    }

    private static void loadDictionary() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = SkillExtractor.class
                    .getClassLoader()
                    .getResourceAsStream("skills_dict.json");

            if (is == null) {
                throw new IllegalStateException("Cannot find skills_dict.json on classpath");
            }

            JsonNode root = mapper.readTree(is);
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode info = entry.getValue();

                String canonical = info.has("canonical")
                        ? info.get("canonical").asText()
                        : key;

                JsonNode aliasesNode = info.get("aliases");
                if (aliasesNode != null && aliasesNode.isArray()) {
                    for (JsonNode aliasNode : aliasesNode) {
                        String alias = aliasNode.asText();
                        String normAlias = normalize(alias);
                        PHRASE_TO_CANONICAL.put(normAlias, canonical);

                        int len = normAlias.split(" ").length;
                        if (len > MAX_PHRASE_LEN) {
                            MAX_PHRASE_LEN = len;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load skills_dict.json", e);
        }
    }


    public static String normalize(String text) {
        if (text == null) return "";
        String lower = text.toLowerCase(Locale.ROOT);
        lower = PUNCT.matcher(lower).replaceAll(" ");
        lower = MULTI_SPACE.matcher(lower).replaceAll(" ").trim();
        return lower;
    }

    public static List<String> extractSkills(String text) {
        String norm = normalize(text);
        if (norm.isEmpty()) return Collections.emptyList();

        String[] tokens = norm.split(" ");
        int nTokens = tokens.length;

        Set<String> found = new HashSet<>();

        for (int i = 0; i < nTokens; i++) {
            for (int n = MAX_PHRASE_LEN; n >= 1; n--) {
                if (i + n > nTokens) continue;

                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < n; k++) {
                    if (k > 0) sb.append(' ');
                    sb.append(tokens[i + k]);
                }
                String phrase = sb.toString();

                String canonical = PHRASE_TO_CANONICAL.get(phrase);
                if (canonical != null) {
                    found.add(canonical);
                    break;
                }
            }
        }

        List<String> result = new ArrayList<>(found);
        Collections.sort(result);
        return result;
    }
    private List<String> buildSkillsNorm(ProcessPostRequest req) {

        String text = joinNonBlank(req.getTitle(), req.getPosition(), req.getDescription());
        Set<String> merged = new LinkedHashSet<>();

        if (StringUtils.hasText(text)) {
            merged.addAll(SkillExtractor.extractSkills(text));
        }

        if (req.getSkills() != null) {
            for (JobSkillRequest s : req.getSkills()) {
                if (s == null) continue;

                String manual = s.getSkillName();

                if (StringUtils.hasText(manual)) {
                    String norm = SkillExtractor.normalize(manual);
                    List<String> mapped = SkillExtractor.extractSkills(norm);
                    if (!mapped.isEmpty()) merged.addAll(mapped);
                    else merged.add(manual.trim());
                }
            }
        }

        return merged.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
    private String joinNonBlank(String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(". "));
    }
}
