package org.spoorn.spoornarmorattributes.config;

import lombok.extern.log4j.Log4j2;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@Log4j2
public class Expressions {
    
    public static void init() {
        ModConfig modConfig = ModConfig.get();
    }
}
