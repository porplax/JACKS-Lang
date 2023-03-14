package jacks;

import java.util.HashMap;

public class SymbolTableManager {
    private static final HashMap<Integer, HashMap<String, HashMap<String, Object>>> SymbolTable = new HashMap<>();

    public static HashMap<Integer, HashMap<String, HashMap<String, Object>>> getSymbolTable() {
        return SymbolTable;
    }
    public static int CurrentScope = 0;

    public static void setSymbolTable(int id) {
        CurrentScope = id;
    }

    public static HashMap<String, HashMap<String, Object>> allocate(int id) {
        if (SymbolTable.containsKey(id)) {
            return SymbolTable.get(id);
        }
        CurrentScope = id;
        SymbolTable.put(id, new HashMap<>());
        return SymbolTable.get(id);
    }

    public static void free(String name) {
        SymbolTable.get(CurrentScope).remove(name);
    }

    public static HashMap<String, Object> lookup(String name) {
        return SymbolTable.get(CurrentScope).get(name);
    }

    public static void insert(String name) {
        SymbolTable.get(CurrentScope).put(name, new HashMap<>());
    }

    public static void set_attr(String name, String attr, Object value) {
        SymbolTable.get(CurrentScope).get(name).put(attr, value);
    }

    public static Object get_attr(String name, String attr) {
        return SymbolTable.get(CurrentScope).get(name).get(attr);
    }

}
