package mju.chatuniv.comment.controller.intergration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mju.chatuniv.comment.service.service.CommentService;
import org.springframework.context.ApplicationContext;

public class BeanUtils {

    private static final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public static List<CommentService> getBeansOfCommentServiceType() {
        Map<String, CommentService> beansOfType = new HashMap<>(
                applicationContext.getBeansOfType(CommentService.class));
        return new ArrayList<>(beansOfType.values());
    }
}
