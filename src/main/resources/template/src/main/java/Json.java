package {{PACKAGE_NAME}};

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Minimal JSON codec for the generated zero-dependency example. */
public final class Json {
    private Json() {}
    public static Object parse(String input) { Parser p = new Parser(input); Object value = p.value(); p.space(); if (p.pos != input.length()) throw new IllegalArgumentException("Trailing JSON"); return value; }
    public static String stringify(Object value) {
        if (value == null) return "null";
        if (value instanceof String s) return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t").replace("\b", "\\b").replace("\f", "\\f") + "\"";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map<?, ?> m) { List<String> fields = new ArrayList<>(); m.forEach((k,v) -> fields.add(stringify(k.toString()) + ":" + stringify(v))); return "{" + String.join(",", fields) + "}"; }
        if (value instanceof Iterable<?> it) { List<String> fields = new ArrayList<>(); it.forEach(v -> fields.add(stringify(v))); return "[" + String.join(",", fields) + "]"; }
        throw new IllegalArgumentException("Unsupported JSON value: " + value.getClass());
    }
    @SuppressWarnings("unchecked") public static Map<String,Object> object(Object value) { if (!(value instanceof Map<?,?>)) throw new IllegalArgumentException("Expected JSON object"); return (Map<String,Object>) value; }
    @SuppressWarnings("unchecked") public static List<Object> array(Object value) { if (!(value instanceof List<?>)) throw new IllegalArgumentException("Expected JSON array"); return (List<Object>) value; }
    private static final class Parser {
        final String s; int pos;
        Parser(String s) { this.s = s; }
        void space() { while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++; }
        char next() { if (pos >= s.length()) throw new IllegalArgumentException("Unexpected end of JSON"); return s.charAt(pos++); }
        Object value() { space(); char c = next(); return switch (c) {
            case '{' -> object(); case '[' -> array(); case '"' -> string(); case 't' -> literal("rue", true);
            case 'f' -> literal("alse", false); case 'n' -> literal("ull", null);
            default -> { if (c == '-' || Character.isDigit(c)) { pos--; yield number(); } throw new IllegalArgumentException("Invalid JSON at " + (pos - 1)); }
        }; }
        Object literal(String tail, Object v) { for (char c : tail.toCharArray()) if (next() != c) throw new IllegalArgumentException("Invalid JSON literal"); return v; }
        Map<String,Object> object() { Map<String,Object> map = new LinkedHashMap<>(); space(); if (peek('}')) return map; do { space(); if (next() != '"') throw new IllegalArgumentException("Expected key"); String key = string(); space(); if (next() != ':') throw new IllegalArgumentException("Expected colon"); map.put(key, value()); space(); if (peek('}')) return map; if (next() != ',') throw new IllegalArgumentException("Expected comma"); } while (true); }
        List<Object> array() { List<Object> list = new ArrayList<>(); space(); if (peek(']')) return list; do { list.add(value()); space(); if (peek(']')) return list; if (next() != ',') throw new IllegalArgumentException("Expected comma"); } while (true); }
        boolean peek(char c) { if (pos < s.length() && s.charAt(pos) == c) { pos++; return true; } return false; }
        String string() { StringBuilder out = new StringBuilder(); while (true) { char c = next(); if (c == '"') return out.toString(); if (c < 0x20) throw new IllegalArgumentException("Control character in string"); if (c != '\\') { out.append(c); continue; } c = next(); switch (c) { case '"','\\','/' -> out.append(c); case 'b' -> out.append('\b'); case 'f' -> out.append('\f'); case 'n' -> out.append('\n'); case 'r' -> out.append('\r'); case 't' -> out.append('\t'); case 'u' -> { int code = 0; for (int i=0;i<4;i++) { int digit=Character.digit(next(),16); if (digit<0) throw new IllegalArgumentException("Invalid Unicode escape"); code=(code<<4)|digit; } out.append((char)code); } default -> throw new IllegalArgumentException("Invalid escape"); } } }
        Number number() { int start=pos; peek('-'); if (!peek('0')) { int first=pos; digits(); if (s.charAt(first)=='0' && pos-first>1) throw new IllegalArgumentException("Leading zero"); } if (peek('.')) digits(); if (peek('e') || peek('E')) { if (!peek('+')) peek('-'); digits(); } String n=s.substring(start,pos); try { if (n.contains(".") || n.contains("e") || n.contains("E")) return Double.valueOf(n); return Long.valueOf(n); } catch (NumberFormatException ex) { throw new IllegalArgumentException("Invalid number", ex); } }
        void digits() { int start=pos; while(pos<s.length() && Character.isDigit(s.charAt(pos))) pos++; if (start==pos) throw new IllegalArgumentException("Expected digit"); }
    }
}
