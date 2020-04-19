package com.c1b1.v2.studyone.studyone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="user_word",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dailyWordId", "userId"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity // This tells Hibernate to make a table out of this class
public class UserWord implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userWordId;

    @ManyToOne
    @JoinColumn(name = "dailyWordId") // this is the name of the FK
    private DailyWord dailyWord;

    @ManyToOne
    @JoinColumn(name = "userId") // this is the name of the FK
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;
}

