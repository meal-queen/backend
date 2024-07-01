package com.threlease.base.functions.company;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyConnectEntity;
import com.threlease.base.entities.CompanyEntity;
import com.threlease.base.entities.OrderEntity;
import com.threlease.base.enums.AffiliationUserRoles;
import com.threlease.base.enums.UserRoles;
import com.threlease.base.functions.auth.AuthService;
import com.threlease.base.functions.company.dto.*;
import com.threlease.base.functions.pay.PayService;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.GetRandom;
import com.threlease.base.utils.file.FileStore;
import com.threlease.base.utils.file.UploadFile;
import com.threlease.base.utils.kakao.Address;
import com.threlease.base.utils.kakao.KakaoMap;
import com.threlease.base.utils.moneyPin.MoneyPin;
import com.threlease.base.utils.moneyPin.response.BizBaseInfo;
import com.threlease.base.utils.responses.BasicResponse;
import com.threlease.base.utils.responses.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/company")
@Tag(name = "company API", description = "회사 관련 API")
@AllArgsConstructor
public class CompanyController {
    private final AuthService authService;
    private final CompanyService companyService;
    private final PayService payService;
    private final MoneyPin moneyPinService;
    private final KakaoMap kakaoMapService;

    @GetMapping("/codeby")
    @Operation(summary = "가입 코드로 회사 정보 가져오기")
    private ResponseEntity<BasicResponse> findByCode(
            @ParameterObject @ModelAttribute @Valid FindByCodeDto dto
    ) {
        Optional<CompanyEntity> company = companyService.findOneByInviteCode(dto.getCode());

        if (company.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );

        company.get().setPoint(0);

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(company.get()))
                        .build()
        );
    }

    @PostMapping("/join")
    @Operation(summary = "회사 가입", description = "회사 가입")
    private ResponseEntity<BasicResponse> join(
            @RequestBody @Valid JoinCompanyDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByInviteCode(dto.getCode());

        if (company.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );

        Optional<CompanyConnectEntity> connect = companyService.findOneByConnectAuthor(company.get(), user.get());

        if (connect.isPresent())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_USER))
                            .build()
            );

        if (company.get().getInviteCode().equals(dto.getCode())) {
            return ResponseEntity.status(201).body(
                    BasicResponse.builder()
                            .success(true)
                            .message(Optional.of("해당 소속에 가입에 대한 요청을 신청했습니다."))
                            .build()
            );
        } else return ResponseEntity.status(404).body(
                BasicResponse.builder()
                        .success(false)
                        .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                        .build()
        );
    }

    @PostMapping("/create")
    @Operation(summary = "회사 생성", description = "회사 생성")
    private ResponseEntity<BasicResponse> createCompany(
            @RequestBody @Valid CreateCompanyDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);
        Optional<CompanyEntity> company = companyService.findOneByBizNumber(dto.getBizNumber());

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        if (company.isPresent() && company.get().getTeam().equals(dto.getTeam()))
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_COMPANY_CASE_NEW))
                            .data(Optional.empty())
                            .build()
            );

        FileStore fs = new FileStore("/company/profile");

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

        CompanyEntity data = CompanyEntity.builder()
                .name(bizInfo.getValue().getBizName())
                .address(address.getValue().getAddress_name())
                .x(Double.parseDouble(address.getValue().getX()))
                .y(Double.parseDouble(address.getValue().getY()))
                .point(0)
                .team(dto.getTeam())
                .inviteCode(new GetRandom().run("all", 8))
                .path(fs.getFullPath("default.webp"))
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


            data.setPath(fs.getFullPath(upload.getValue().getStoreFilename()));
        }

        companyService.companySave(data);
        CompanyConnectEntity connect = CompanyConnectEntity.builder()
                .company(data)
                .role(AffiliationUserRoles.ROLE_ROOT)
                .author(user.get())
                .build();

        companyService.connectSave(connect);

        return ResponseEntity.status(201).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/update")
    @Operation(summary = "회사 정보 수정", description = "회사 정보 수정")
    private ResponseEntity<BasicResponse> updateCompany(
            @RequestBody @Valid UpdateCompanyDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);
        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        if (company.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );

        Optional<CompanyConnectEntity> connectUser = companyService.findOneByConnectAuthor(company.get(), user.get());

        if (connectUser.isEmpty())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        if (connectUser.get().getRole() != AffiliationUserRoles.ROLE_ADMIN &&
            connectUser.get().getRole() != AffiliationUserRoles.ROLE_ROOT &&
            user.get().getRole() != UserRoles.ROLE_ADMIN
        ) return ResponseEntity.status(401).body(
                BasicResponse.builder()
                        .success(false)
                        .message(Optional.of(Messages.NOT_PERMISSION))
                        .build()
        );

        FileStore fs = new FileStore("/company/profile");

        if (dto.getBizNumber().isPresent()) {
            Failable<BizBaseInfo, String> bizInfo = moneyPinService.getBizBaseInfo(dto.getBizNumber().get());

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

            company.get().setAddress(bizInfo.getValue().getAddress());
            company.get().setName(bizInfo.getValue().getBizName());
            company.get().setBizNumber(dto.getBizNumber().get());
            company.get().setX(Double.parseDouble(address.getValue().getX()));
            company.get().setY(Double.parseDouble(address.getValue().getY()));
        }

        if (dto.getTeam().isPresent())
            company.get().setTeam(dto.getTeam().get());

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

            company.get().setPath(fs.getFullPath(upload.getValue().getStoreFilename()));
        }

        companyService.companySave(company.get());

        return ResponseEntity.status(201).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회사 삭제", description = "회사 삭제 / 주의: 포인트까지 모두 삭제 됨.")
    private ResponseEntity<BasicResponse> deleteCompany(
            @RequestBody @Valid DeleteCompanyDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (company.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );


        Optional<CompanyConnectEntity> connect = companyService.findOneByConnectAuthor(company.get(), user.get());

        if (connect.isEmpty() && user.get().getRole() != UserRoles.ROLE_ADMIN)
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_USER))
                            .build()
            );

        if (
                connect.isEmpty() ||
                (
                        connect.get().getRole() != AffiliationUserRoles.ROLE_ROOT &&
                        user.get().getRole() != UserRoles.ROLE_ADMIN
                )
        )
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        List<CompanyConnectEntity> connects = companyService.findByConnectCompany(company.get());

        connects.forEach(companyService::connectRemove);

        companyService.companyRemove(company.get());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/user/permission")
    @Operation(summary = "권한 수정", description = "회사 내 유저 권한 수정")
    private ResponseEntity<BasicResponse> updatePermission(
            @RequestBody @Valid UpdatePermissionDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<AuthEntity> author = authService.findOneByUUID(dto.getAuthor());

        if (author.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_USER))
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (company.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );

        Optional<CompanyConnectEntity> connectUser = companyService.findOneByConnectAuthor(company.get(), user.get());
        if (connectUser.isEmpty() && user.get().getRole() != UserRoles.ROLE_ADMIN)
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        if (
            connectUser.get().getRole() == AffiliationUserRoles.ROLE_ROOT ||
            connectUser.get().getRole() == AffiliationUserRoles.ROLE_ADMIN
        )
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        Optional<CompanyConnectEntity> connectAuthor = companyService.findOneByConnectAuthor(company.get(), author.get());

        if (connectAuthor.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_USER))
                            .build()
            );

        connectAuthor.get().setRole(dto.getRole());

        companyService.connectSave(connectAuthor.get());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/user/point")
    @Operation(summary = "포인트 지급", description = "회사 내 유저에게 포인트 지급")
    private ResponseEntity<BasicResponse> updatePoint(
            @RequestBody @Valid UpdatePointDto dto,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<AuthEntity> author = authService.findOneByUUID(dto.getAuthor());

        if (author.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_USER))
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (company.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .build()
            );

        Optional<CompanyConnectEntity> connectUser = companyService.findOneByConnectAuthor(company.get(), user.get());
        if (connectUser.isEmpty())
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        if (
                connectUser.get().getRole() != AffiliationUserRoles.ROLE_ROOT &&
                connectUser.get().getRole() != AffiliationUserRoles.ROLE_ADMIN
        )
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        Optional<CompanyConnectEntity> connectAuthor = companyService.findOneByConnectAuthor(company.get(), author.get());

        if (connectAuthor.isEmpty())
            return ResponseEntity.status(404).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.DUPLICATION_USER))
                            .build()
            );
        if (company.get().getPoint() < dto.getPoint())
            return ResponseEntity.status(400).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of("사용하실 포인트가 소속(이)가 보유중인 포인트 보다 많습니다."))
                            .build()
            );

        company.get().setPoint(company.get().getPoint() - dto.getPoint());
        connectAuthor.get().setPoint(connectAuthor.get().getPoint() + dto.getPoint());

        companyService.companySave(company.get());
        companyService.connectSave(connectAuthor.get());

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "회사 정보", description = "회사 정보 GET")
    private ResponseEntity<BasicResponse> getCompany(
            @PathVariable("uuid") String uuid,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(uuid);
        return company.map(companyEntity -> ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(companyEntity))
                        .build()
        )).orElseGet(() -> ResponseEntity.status(404).body(
                BasicResponse.builder()
                        .success(false)
                        .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                        .data(Optional.empty())
                        .build()
        ));
    }

    @GetMapping("/")
    @Operation(summary = "회사 정보", description = "회사 정보 GET")
    private ResponseEntity<BasicResponse> getCompanys(
            @RequestParam("page") int page,
            @RequestParam("take") int take,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        if (user.get().getRole() != UserRoles.ROLE_ADMIN)
            return ResponseEntity.status(401).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_PERMISSION))
                            .build()
            );

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(companyService.findByPagination(PageRequest.of(page, take))))
                        .build()
        );
    }

    @GetMapping("/users")
    @Operation(summary = "회사 내 유저 확인", description = "회사 내 유저 정보 확인")
    private ResponseEntity<BasicResponse> getUsers(
            @ParameterObject @ModelAttribute @Valid GetUsersDto dto,
            @RequestParam("page") int page,
            @RequestParam("take") int take,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (company.isPresent()) {
            Optional<CompanyConnectEntity> companyConnect =
                    companyService.findOneByConnectAuthor(company.get(), user.get());

            if (
                    (companyConnect.isPresent() || user.get().getRole() == UserRoles.ROLE_ADMIN) &&
                            (
                                    companyConnect.get().getRole() == AffiliationUserRoles.ROLE_ADMIN ||
                                    companyConnect.get().getRole() == AffiliationUserRoles.ROLE_ROOT)
            ) {
                Page<CompanyConnectEntity> users =
                        companyService.findOneByConnectCompanyPagination(
                                PageRequest.of(
                                        page,
                                        take
                                ),
                                        company.get()
                        );

                if (dto.getRole() != null) {

                    return ResponseEntity.status(200).body(
                            BasicResponse.builder()
                                    .success(true)
                                    .data(
                                            Optional.of(
                                                users.stream()
                                                        .filter((v) -> v.getRole() == dto.getRole())
                                                        .peek((v) -> {
                                                            v.getAuthor().setSalt(null);
                                                            v.getAuthor().setPassword(null);
                                                        }
                                                ).toList()
                                            )
                                    )
                                    .build()
                    );
                } else
                    return ResponseEntity.status(200).body(
                            BasicResponse.builder()
                                    .success(true)
                                    .data(Optional.of(users))
                                    .build()
                    );

            } else
                return ResponseEntity.status(401).body(
                        BasicResponse.builder()
                                .success(false)
                                .message(Optional.of(Messages.NOT_PERMISSION))
                                .build()
                );

        } else
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .data(Optional.empty())
                            .build()
            );
    }

    @GetMapping("/pay/logs")
    @Operation(summary = "결제 정보", description = "회사 내 유저 결제 정보 확인")
    private ResponseEntity<BasicResponse> getLogs(
            @ModelAttribute @Valid GetLogsDto dto,
            @RequestParam("page") int page,
            @RequestParam("take") int take,
            @RequestHeader("Authorization") String token
    ) {
        Optional<AuthEntity> user = authService.findOneByToken(token);

        if (user.isEmpty())
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.ERROR_SESSION))
                            .data(Optional.empty())
                            .build()
            );

        Optional<CompanyEntity> company = companyService.findOneByUuid(dto.getCompany());

        if (company.isPresent()) {
            Optional<CompanyConnectEntity> companyConnect =
                    companyService.findOneByConnectAuthor(company.get(), user.get());

            if (
                    companyConnect.isPresent() &&
                            (user.get().getRole() == UserRoles.ROLE_ADMIN ||
                                    companyConnect.get().getRole() == AffiliationUserRoles.ROLE_ADMIN ||
                                    companyConnect.get().getRole() == AffiliationUserRoles.ROLE_ROOT)
            ) {
                Page<OrderEntity> logs =
                        payService.findByCompanyPagination(
                                PageRequest.of(
                                        page, take
                                ),
                                company.get()
                        );

                return ResponseEntity.status(200).body(
                        BasicResponse.builder()
                                .success(true)
                                .data(Optional.of(logs.map((v) -> {
                                    v.getAuthor().setSalt(null);
                                    v.getAuthor().setPassword(null);
                                    return v;
                                })))
                                .build()
                );
            } else
                return ResponseEntity.status(401).body(
                        BasicResponse.builder()
                                .success(false)
                                .message(Optional.of(Messages.NOT_PERMISSION))
                                .build()
                );

        } else
            return ResponseEntity.status(403).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.of(Messages.NOT_FOUND_COMPANY))
                            .data(Optional.empty())
                            .build()
            );
    }
}
