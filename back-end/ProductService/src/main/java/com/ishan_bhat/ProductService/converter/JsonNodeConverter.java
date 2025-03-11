package com.ishan_bhat.ProductService.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter // Removed autoApply = true
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonNodeConverter.class);

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        logger.info("convertToDatabaseColumn: Input JsonNode = {}", attribute); // ADD LOGGING
        try {
            String jsonString = objectMapper.writeValueAsString(attribute);
            logger.info("convertToDatabaseColumn: Output JSON String = {}", jsonString); // ADD LOGGING
            return jsonString;
        } catch (JsonProcessingException e) {
            logger.error("JSON Processing Exception during conversion to database column: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        logger.info("convertToEntityAttribute: Input DB Data String = {}", dbData); // ADD LOGGING
        try {
            JsonNode jsonNode = objectMapper.readTree(dbData);
            logger.info("convertToEntityAttribute: Output JsonNode = {}", jsonNode); // ADD LOGGING
            return jsonNode;
        } catch (IOException e) {
            logger.error("IO Exception during conversion to entity attribute: {}", e.getMessage());
            return null;
        }
    }
}