package patTest;

import com.erling.utils.result.Result;
import com.erling.utils.result.ren.VCodeEnum;
import org.junit.jupiter.api.Test;

public class ResultTest {
    @Test
    public void EnumTest() {
        Result<?> result = new Result<>(VCodeEnum.VERIFICATION_CODE_ERROR,"123456");
        System.out.println(result.getCode());
        System.out.println(result.getMessage());
//        System.out.println(result.getCode());
    }
}
