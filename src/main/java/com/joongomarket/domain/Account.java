package com.joongomarket.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean auctionCreatedByEmail;

    private boolean auctionCreatedByWeb;

    private boolean biddingResultByEmail;

    private boolean biddingResultByWeb;

    private boolean auctionUpdatedByEmail;

    private boolean auctionUpdatedByWeb;

}
