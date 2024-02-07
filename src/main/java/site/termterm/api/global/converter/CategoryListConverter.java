package site.termterm.api.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.io.IOException;
import java.util.List;

public class CategoryListConverter implements AttributeConverter<List<CategoryEnum>, String>{
    private static final ObjectMapper om = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

    @Override
    public String convertToDatabaseColumn(List<CategoryEnum> attribute) {
        try{
            return om.writeValueAsString(attribute);
        }catch (JsonProcessingException e){
            throw new CustomApiException("리스트를 문자열로 convert 하는 과정에서 오류가 발생하였습니다.");
        }
    }

    @Override
    public List<CategoryEnum> convertToEntityAttribute(String dbData) {
        TypeReference<List<CategoryEnum>> typeReference = new TypeReference<>() {};

        try{
            return om.readValue(dbData, typeReference);
        }catch (IOException e){
            throw new CustomApiException("Category DB Data 를 리스트로 convert 하는 과정에서 오류가 발생하였습니다.");
        }
    }
}
