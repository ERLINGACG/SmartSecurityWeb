package patTest;

import com.erling.utils.pattern.PatternUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PatterTest {
    @Test
    public void test(){
        String str = "{\"1757309380479\":[{\"person\": 1}],\"1757309380505\":[{\"person\": 1}],\"1757309380535\":[{\"person\": 1}],\"1757309380561\":[{\"person\": 1}],\"1757309380585\":[{\"person\": 1}],\"1757309380608\":[{\"person\": 1}],\"1757309380631\":[{\"person\": 1}],\"1757309380654\":[{\"person\": 1}],\"1757309380680\":[{\"person\": 1}],\"1757309380709\":[{\"person\": 1}]}\n";
        String keyWord1 = "person";
        List<String> keyWordList = Arrays.asList("person","car");
        boolean b = PatternUtils.isKeyWord(str, keyWord1);
        boolean b1 = PatternUtils.isListKeyWordOR(str, keyWordList);
        boolean b2 = PatternUtils.isListKeyWordAND(str, keyWordList);
        System.out.println(b);
        System.out.println(b1);
        System.out.println(b2);
    }
}
