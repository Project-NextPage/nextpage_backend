package com.nextpage.backend.dto.request;

import com.nextpage.backend.entity.Story;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorySaveRequest {
    private String imageUrl;
    private String content;

    public Story toEntity(){
        return new Story(content, imageUrl);
    }
}
