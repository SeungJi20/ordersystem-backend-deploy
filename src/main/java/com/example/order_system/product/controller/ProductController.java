package com.example.order_system.product.controller;

import com.example.order_system.common.auth.JwtTokenProvider;
import com.example.order_system.common.dto.CommonDto;
import com.example.order_system.member.dto.MemberResDto;
import com.example.order_system.member.service.MemberService;
import com.example.order_system.product.domain.Product;
import com.example.order_system.product.dto.ProductCreateDto;
import com.example.order_system.product.dto.ProductResDto;
import com.example.order_system.product.dto.ProductSearchDto;
import com.example.order_system.product.dto.ProductUpdateDto;
import com.example.order_system.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@ModelAttribute ProductCreateDto productCreateDto) {
        Long id = productService.save(productCreateDto);
        return new ResponseEntity<>( // 바디와 헤더
                CommonDto.builder()
                        .result(id)
                        .status_code(HttpStatus.OK.value())
                        .status_message("상품등록 완료") // 꺼낼 때 .result.accessToken 이런식으로 꺼내야
                        .build()

                ,HttpStatus.CREATED); // 이게 헤더

    }

    @GetMapping("/list")
    public ResponseEntity<?> findAll(Pageable pageable, ProductSearchDto productSearchDto) { // 파라미터 방식(param?&이거 말하는것)으로 받을것임
        Page<ProductResDto> productResDtoList = productService.findAll(pageable, productSearchDto);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(productResDtoList)
                        .status_code(HttpStatus.OK.value())
                        .status_message("상품목록조회완료")
                        .build() ,HttpStatus.OK);
    }


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        ProductResDto dto = productService.findById(id);
        return new ResponseEntity<>(new CommonDto(dto, HttpStatus.OK.value(), "상품상세조회성공"), HttpStatus.OK);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<?> update(@ModelAttribute ProductUpdateDto productUpdateDto, @PathVariable Long productId) {
        Long id = productService.update(productUpdateDto, productId);
        return new ResponseEntity<>( // 바디와 헤더
                CommonDto.builder()
                        .result(id)
                        .status_code(HttpStatus.OK.value())
                        .status_message("상품변경 완료")
                        .build()
                ,HttpStatus.OK);
    }
}
