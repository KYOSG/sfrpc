package utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Projectname: sfrpc
 * @Filename: RpcResponse
 * @Author: SpringForest
 * @Data:2023/2/3 11:07
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse {
    private String message;
}
