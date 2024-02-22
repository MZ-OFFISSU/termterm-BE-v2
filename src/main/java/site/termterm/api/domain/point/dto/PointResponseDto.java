package site.termterm.api.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.point.entity.PointHistory;

import java.util.List;

public class PointResponseDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PointHistoryResponseDto {
        private String date;
        private List<PointHistoryEachDto> dailyHistories;

        @Getter
        @Setter
        @AllArgsConstructor
        @Builder
        public static class PointHistoryEachDto implements Comparable<PointHistoryEachDto>{
            @JsonIgnore
            private Long id;

            private String detail;
            private String subText;
            private String point;   // "-50", "+200" 등
            private Integer currentMemberPoint;

            public static PointHistoryEachDto of(PointHistory pointHistory){
                return PointHistoryEachDto.builder()
                        .id(pointHistory.getId())
                        .detail(pointHistory.getDetail())
                        .subText(pointHistory.getSubText())
                        .point(pointHistory.getSign().getSign() + pointHistory.getValue())
                        .currentMemberPoint(pointHistory.getMemberPoint())
                        .build();
            }

            @Override
            public int compareTo(PointHistoryEachDto that) {
                if (that.id < this.id){
                    return 1;
                } else if (that.id > this.id) {
                    return -1;
                }
                return 0;
            }
        }

    }
}
