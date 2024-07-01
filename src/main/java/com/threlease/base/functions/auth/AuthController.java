package com.threlease.base.functions.auth;

import com.threlease.base.entities.*;
import com.threlease.base.enums.AffiliationUserRoles;
import com.threlease.base.enums.UserRoles;
import com.threlease.base.functions.auth.dto.*;
import com.threlease.base.functions.company.CompanyService;
import com.threlease.base.functions.restaurant.RestaurantService;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.GetRandom;
import com.threlease.base.utils.Hash;
import com.threlease.base.utils.file.FileStore;
import com.threlease.base.utils.file.UploadFile;
import com.threlease.base.utils.kakao.Address;
import com.threlease.base.utils.kakao.KakaoMap;
import com.threlease.base.utils.moneyPin.MoneyPin;
import com.threlease.base.utils.moneyPin.Token;
import com.threlease.base.utils.moneyPin.response.BizBaseInfo;
import com.threlease.base.utils.responses.BasicResponse;
import com.threlease.base.utils.responses.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth API", description = "계정 관련 API")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CompanyService companyService;
    private final RestaurantService restaurantService;
    private final MoneyPin moneyPinService;
    private final KakaoMap kakaoMapService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인")
    private ResponseEntity<BasicResponse> login(
        @RequestBody @Valid LoginDto dto
    ) {
        Optional<AuthEntity> auth = authService.findOneByUsername(dto.getUsername());

        if (auth.isEmpty())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_USER))
                            .data(Optional.empty())
                            .build()
            );

        if (Objects.equals(auth.get().getPassword(), new Hash().generateSHA512(dto.getPassword() + auth.get().getSalt())))
            return ResponseEntity.status(201).body(
                    BasicResponse.builder()
                            .success(true)
                            .message(Optional.empty())
                            .data(Optional.ofNullable(authService.sign(auth.get())))
                            .build()
            );
        else
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.BAD_REQUEST_LOGIN))
                            .data(Optional.empty())
                            .build()
            );

    }

    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입", description = "일반 유저 회원가입")
    private ResponseEntity<BasicResponse> signUp(
            @RequestBody @Valid SignUpDto dto
    ) {
        Optional<AuthEntity> auth = authService.findOneByUsername(dto.getUsername());

        if (auth.isPresent())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_USER))
                            .data(Optional.empty())
                            .build()
            );

        String salt = new GetRandom().run("all", 32);

        AuthEntity userEntity = AuthEntity.builder()
                .username(dto.getUsername())
                .password(new Hash().generateSHA512(dto.getPassword() + salt))
                .salt(salt)
                .name(dto.getName())
                .role(UserRoles.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        authService.authSave(userEntity);


        return ResponseEntity.status(201).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @PostMapping("/signup/company")
    @Operation(summary = "회사 회원가입", description = "회원가입과 동시에 회사를 만듬")
    private ResponseEntity<BasicResponse> signUpCompany(
            @RequestBody @Valid CompanySignUpDto dto
    ) {
        Optional<AuthEntity> auth = authService.findOneByUsername(dto.getUsername());

        Failable<BizBaseInfo, String> biz = moneyPinService.getBizBaseInfo(dto.getBizNumber());

        if (biz.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(biz.getError()))
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByBizNumber(dto.getBizNumber());

        if (company.isPresent() && company.get().getTeam().equals(dto.getTeam()))
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_COMPANY_CASE_NEW))
                            .data(Optional.empty())
                            .build()
            );


        if (auth.isPresent())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                    .success(false)
                    .message(Optional.of(Messages.DUPLICATION_USER))
                    .data(Optional.empty())
                    .build()
            );

        Failable<BizBaseInfo, String> bizInfo = moneyPinService.getBizBaseInfo(dto.getBizNumber());

        if (bizInfo.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(bizInfo.getError()))
                            .build()
            );

        Failable<Address, String> address = kakaoMapService.addressToPos(biz.getValue().getAddress());

        if (address.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(address.getError()))
                            .build()
            );

        FileStore fs = new FileStore("/company/profile");

        CompanyEntity companyEntity = CompanyEntity.builder()
                .address(bizInfo.getValue().getAddress())
                .x(Double.parseDouble(address.getValue().getX()))
                .y(Double.parseDouble(address.getValue().getY()))
                .point(0)
                .team(dto.getTeam())
                .path(fs.getFullPath("default.webp"))
                .name(bizInfo.getValue().getBizName())
                .inviteCode(new GetRandom().run("all", 8))
                .createdAt(LocalDateTime.now())
                .build();

        if (dto.getFile().isPresent()) {
            Failable<UploadFile, String> upload = fs.storeFile(dto.getFile().get());

            if (upload.isError())
                return ResponseEntity.status(403).body(
                        BasicResponse.builder()
                                .success(false)
                                .message(Optional.of(upload.getError()))
                                .data(Optional.empty())
                                .build()
                );

            companyEntity.setPath(fs.getFullPath(upload.getValue().getStoreFilename()));
        }

        String salt = new GetRandom().run("all", 32);

        AuthEntity userEntity = AuthEntity.builder()
                .username(dto.getUsername())
                .password(new Hash().generateSHA512(dto.getPassword() + salt))
                .salt(salt)
                .name(biz.getValue().getCeoName())
                .role(UserRoles.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        CompanyConnectEntity companyConnect = CompanyConnectEntity.builder()
                .author(userEntity)
                .company(companyEntity)
                .role(AffiliationUserRoles.ROLE_ROOT)
                .build();

        authService.authSave(userEntity);
        companyService.companySave(companyEntity);
        companyService.connectSave(companyConnect);

        BasicResponse response = BasicResponse.builder()
                .success(true)
                .message(Optional.empty())
                .data(Optional.empty())
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/signup/restaurant")
    @Operation(summary = "가맹점 회원가입", description = "회원가입과 동시에 가맹점를 만듬")
    private ResponseEntity<BasicResponse> signUpRestaurant(
            @RequestBody @Valid RestaurantSignUpDto dto
    ) {
        Optional<AuthEntity> auth = authService.findOneByUsername(dto.getUsername());
        Optional<RestaurantEntity> restaurant = restaurantService.findOneByBizNumber(dto.getBizNumber());

        if (restaurant.isPresent())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_RESTAURANT_CASE_NEW))
                            .data(Optional.empty())
                            .build()
            );


        if (auth.isPresent()) {
            BasicResponse response = BasicResponse.builder()
                    .success(false)
                    .message(Optional.of(Messages.DUPLICATION_USER))
                    .data(Optional.empty())
                    .build();

            return ResponseEntity.status(403).body(response);
        }

        Failable<BizBaseInfo, String> bizInfo = moneyPinService.getBizBaseInfo(dto.getBizNumber());

        if (bizInfo.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(bizInfo.getError()))
                            .build()
            );

        Failable<Address, String> address = kakaoMapService.addressToPos(bizInfo.getValue().getAddress());

        if (address.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(address.getError()))
                            .build()
            );

        FileStore fs = new FileStore("/company/profile");

        String salt = new GetRandom().run("all", 32);

        AuthEntity userEntity = AuthEntity.builder()
                .username(dto.getUsername())
                .password(new Hash().generateSHA512(dto.getPassword() + salt))
                .salt(salt)
                .name(bizInfo.getValue().getCeoName())
                .role(UserRoles.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .address(bizInfo.getValue().getAddress())
                .x(Double.parseDouble(address.getValue().getX()))
                .y(Double.parseDouble(address.getValue().getY()))
                .name(bizInfo.getValue().getBizName())
                .path(fs.getFullPath("default.webp"))
                .createdAt(LocalDateTime.now())
                .author(userEntity)
                .build();

        if (dto.getFile().isPresent()) {
            Failable<UploadFile, String> upload = fs.storeFile(dto.getFile().get());
            if (upload.isError())
                return ResponseEntity.status(403).body(
                        BasicResponse.builder()
                                .success(false)
                                .message(Optional.of(upload.getError()))
                                .data(Optional.empty())
                                .build()
                );

            restaurantEntity.setPath(fs.getFullPath(upload.getValue().getStoreFilename()));
        }

        authService.authSave(userEntity);
        restaurantService.restaurantSave(restaurantEntity);

        BasicResponse response = BasicResponse.builder()
                .success(true)
                .message(Optional.empty())
                .data(Optional.empty())
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/@me")
    @Operation(summary = "내 정보", description = "내 정보")
    private ResponseEntity<BasicResponse> verify(
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isPresent()) {
            user.get().setSalt("unknown");
            user.get().setPassword("unknown");

            return ResponseEntity.status(200).body(
                    BasicResponse.builder()
                        .success(true)
                        .message(Optional.empty())
                        .data(Optional.of(user.get()))
                        .build()
            );
        } else {
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );
        }
    }
}
