package com.hmg.web;


import com.hmg.model.dto.MessagesDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class MessagesController {

    @Value("classpath:/i18n/messages.properties")
    private Resource propertiesFile;

    @GetMapping("/messages-{type}")
    @ResponseBody
    public Map<String, List<MessagesDTO>> getValidations(@PathVariable String type) throws IOException {

        String propertiesContent = StreamUtils.copyToString(propertiesFile.getInputStream(), StandardCharsets.UTF_8);

        if (type.equals("validation")) {
            return getValidationsJSON(propertiesContent);
        }

        return null;
    }

    private Map<String, List<MessagesDTO>> getValidationsJSON(String propertiesContent) {

        int startIndex = propertiesContent.indexOf("VALIDATIONS") + 13;
        int endIndex = propertiesContent.indexOf("ERROR") - 5;

        String[] validations = propertiesContent.substring(startIndex, endIndex).trim().split("\r\n");
        List<MessagesDTO> jsonList = new ArrayList<>();

        Arrays.stream(validations).forEach(e -> {
            String[] spitedElement = e.split("=");
            MessagesDTO message = new MessagesDTO();
            message.setKey(spitedElement[0]);
            message.setValue(spitedElement[1]);
            jsonList.add(message);
        });

        System.out.println();


        return Collections.singletonMap("response", jsonList);
    }
}