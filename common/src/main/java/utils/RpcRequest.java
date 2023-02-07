package utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Projectname: sfrpc
 * @Filename: utils.RpcRequest
 * @Author: SpringForest
 * @Data:2023/2/3 10:30
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}
