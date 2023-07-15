package mju.chatuniv.comment.controller.intergration;

import mju.chatuniv.comment.application.service.EachCommentService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BeanUtils {

    private static final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public static List<EachCommentService> getBeansOfType() {
        Map<String, EachCommentService> beansOfType = new TreeMap<>(applicationContext.getBeansOfType(EachCommentService.class));
        return new ArrayList<>(beansOfType.values());
    }
}
