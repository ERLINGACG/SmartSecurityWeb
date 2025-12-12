package com.erling.controller.redis;

import com.erling.service.json.JsonService;
import com.erling.service.redis.ser.RedisZSetService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class RedisControllerTest {

   private final RedisZSetService redisZSetService;


   private final JsonService jsonService;

    @Autowired
    public RedisControllerTest(RedisZSetService redisZSetService, JsonService jsonService) {
        this.redisZSetService = redisZSetService;
        this.jsonService = jsonService;
    }


    public void Test1(){
        Set<String> strings = redisZSetService.getDataByKey("/topic/images/image");
        System.out.println(strings);
    }

    public void javaTest(){
        Set<String> strings = redisZSetService.getReverseRangeByTimestamp("/topic/images/image",
                1758789625738L,
                1758790015887L);
        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(strings);
        try{

            long startTime=System.currentTimeMillis();
            JsonNode root = objectMapper.readTree(strings.iterator().next());
            Iterator<String> fieldNames = root.fieldNames();
            Map<String, Integer> map = new HashMap<>();
            System.out.println(root);
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode array = root.get(fieldName);
                for (JsonNode jsonNode : array) {
                    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        String key = entry.getKey();
                        int value = entry.getValue().asInt();
                        map.put(key, map.getOrDefault(key, 0) + value);
                    }
                }
            }
            long endTime=System.currentTimeMillis();
            System.out.println("java程序运行时间： "+(endTime-startTime)+"ms");
            System.out.println(map);
        }catch(Exception ei){
            System.out.println(ei.getMessage());
        }
    }

    public void jsonStream(){
        long endTime_ = System.currentTimeMillis();
        long startTime_ = endTime_-3600000;
        Set<String> strings = redisZSetService.getReverseRangeByTimestamp("/topic/image2",
                startTime_, endTime_);
        long startTime = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<>();
        try{
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(strings.toString());
            // 流式解析，性能更好
            String currentCategory = null;
            boolean inCountValue = false;

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();

                if (token == null) break;

                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = parser.getCurrentName();

                    // 检查是否是类别字段（laptop, person, tv等）
                    if (!fieldName.matches("\\d+")) { // 不是数字（时间戳）
                        currentCategory = fieldName;
                        inCountValue = true;
                    }
                } else if (token == JsonToken.VALUE_NUMBER_INT && inCountValue) {
                    int count = parser.getIntValue();
                    map.merge(currentCategory, count, Integer::sum);
                    inCountValue = false;
                }
            }
            parser.close();

            long endTime = System.currentTimeMillis();
            System.out.println("Java流式API处理时间: " + (endTime - startTime) + "ms");

            // 输出结果
            map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));


        }catch(Exception ei){
            System.out.println(ei.getMessage());
        }
    }
    public void cppTest(){
        long endTime_ = System.currentTimeMillis();
        long startTime_ = endTime_-3600000;
        Set<String> strings = redisZSetService.getReverseRangeByTimestamp("/topic/image2",
                startTime_, endTime_);
        long startTime=System.currentTimeMillis();
       String str = jsonService.ToJsonOutput(strings.toString());
        long endTime=System.currentTimeMillis();
        System.out.println("C++程序运行时间： "+(endTime-startTime)+"ms");
        System.out.println(str);
    }
    public void cppTest2(){
        long endTime_ = 1759031333060L;
        long startTime_ =1758945387346L;
        Set<String> strings = redisZSetService.getReverseRangeByTimestamp("/topic/image2",
                startTime_, endTime_);
        System.out.println(strings);
        jsonService.ToJsonTest(strings.toString());

    }




}
