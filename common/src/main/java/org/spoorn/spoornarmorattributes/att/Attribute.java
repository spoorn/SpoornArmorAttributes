package org.spoorn.spoornarmorattributes.att;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains generic information of the attributes.
 */
@AllArgsConstructor
public class Attribute {

    // WARNING: Changing the name in the static initialization will break existing Nbt data


    public static Map<String, Attribute> VALUES = new HashMap<>();
    public static List<String> TOOLTIPS = new ArrayList<>();

    public final String name;
    public final double chance;

    public static void init() {

    }
}
