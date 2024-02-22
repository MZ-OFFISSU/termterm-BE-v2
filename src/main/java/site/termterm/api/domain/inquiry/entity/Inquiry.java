package site.termterm.api.domain.inquiry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@Entity
@ToString
@DynamicUpdate
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INQUIRY_ID")
    private Long id;

    private String email;

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.WAITING;

    @Enumerated(EnumType.STRING)
    private InquiryType type;

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime modifiedDate;


    public Inquiry setStatus(InquiryStatus status){
        this.status = status;
        return this;
    }
}
