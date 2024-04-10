package com.nextpage.backend.dto.request;

import com.nextpage.backend.entity.Story;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.*;

/* Todo
    @Data를 사용하는 것은 좋지 않습니다.
    이유를 찾아보세요
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorySaveRequest {

    // todo
    // 사용자의 요청은 불변이여야합니다. 바뀌지 않는 것을 보장한다면 더 쉽게 코딩할 수 있을듯 합니다.
    // ex) private final String imageUrl;
    // validaton 하기
    @NotEmpty(message = "imageurl은 필수입니다.")
    private String imageUrl;

    private String content;

    public Story toEntity(){
        return new Story(content, imageUrl);
    }
}
