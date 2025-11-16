package com.backend.ai_nlp_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

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

            // Đọc thẳng file từ classpath: src/main/resources/skills_dict.json
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
        // bỏ dấu câu
        lower = PUNCT.matcher(lower).replaceAll(" ");
        // gộp khoảng trắng
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

                // join tokens[i .. i+n)
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < n; k++) {
                    if (k > 0) sb.append(' ');
                    sb.append(tokens[i + k]);
                }
                String phrase = sb.toString();

                String canonical = PHRASE_TO_CANONICAL.get(phrase);
                if (canonical != null) {
                    found.add(canonical);
                    break; // đã match cụm dài, bỏ qua cụm ngắn ở vị trí i
                }
            }
        }

        List<String> result = new ArrayList<>(found);
        Collections.sort(result);
        return result;
    }
}
