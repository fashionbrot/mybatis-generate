package com.github.fashionbrot.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:dataType.properties","classpath:vm.properties"})
public class PropertiesConfig {

}
