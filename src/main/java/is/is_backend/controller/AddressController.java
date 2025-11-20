package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.addressDto.AddressPageRequestDTO;
import is.is_backend.dto.addressDto.AddressPageResponseDTO;
import is.is_backend.dto.addressDto.AddressRequestDTO;
import is.is_backend.dto.addressDto.AddressResponseDTO;
import is.is_backend.mapper.AddressMapper;
import is.is_backend.models.Address;
import is.is_backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/address")
@AllArgsConstructor
@Tag(
        name = "Addresses API",
        description = "API для управления адресами - создание, чтение, обновление и удаление локаций")
public class AddressController {

    private final AddressService addressService;

    @Operation(
            summary = "Создание новых адресов",
            description = "Создает новый адрес в системе и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Адрес успешно создана",
                        content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
            })
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Parameter(description = "DTO для создания адреса") @Valid @RequestBody
                    AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO response = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Обновление адреса",
            description = "Обновляет существующий адрес по id и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Адрес успешно обновлен",
                        content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @Parameter(description = "ID адреса", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "DTO для обновления адреса") @Valid @RequestBody
                    AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO response = addressService.updateAddress(id, addressRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Удаление адреса", description = "Удаляет адрес по идентификатору и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Адрес успешно удален",
                        content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> deleteAddress(
            @Parameter(description = "ID адреса", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Принудительное удаление адреса без переиспользования локации", example = "False")
                    @RequestParam(required = false)
                    Boolean forceDelete,
            @Parameter(description = "ID адреса для перенаправления локации", example = "2")
                    @RequestParam(required = false)
                    Long redirectToAddressId) {
        AddressResponseDTO response = addressService.deleteAddress(id, forceDelete, redirectToAddressId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Получение всех адресов",
            description = "Возвращает пагинированный список всех адресов с возможностью фильтрации")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список адресов успешно получен",
                        content = @Content(schema = @Schema(implementation = AddressPageResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<AddressPageResponseDTO> getAllAddress(
            @Parameter(description = "Параметры пагинации и фильтрации") @ModelAttribute
                    AddressPageRequestDTO pageRequest) {
        Page<Address> addressPage = addressService.getAllAddresses(pageRequest);
        Page<AddressResponseDTO> dtoPage = addressPage.map(AddressMapper::toResponseDTO);
        AddressPageResponseDTO response = AddressPageResponseDTO.fromPage(dtoPage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Получение адреса по id", description = "Возвращает конкретные адреса по id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Адрес успешно найден",
                        content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddress(
            @Parameter(description = "ID адреса", required = true, example = "1") @PathVariable Long id) {
        Address address = addressService.getAddress(id);
        AddressResponseDTO response = AddressMapper.toResponseDTO(address);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Получение всех адресов, у которых отсутствует локация",
            description = "Возвращает пагинированный список всех адресов с возможностью фильтрации")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список адресов успешно получен",
                        content = @Content(schema = @Schema(implementation = AddressPageResponseDTO.class)))
            })
    @GetMapping("/without-location")
    public ResponseEntity<AddressPageResponseDTO> getAddressesWithoutLocationPaged(
            @Parameter(description = "Параметры пагинации и фильтрации")
                    @PageableDefault(size = 20, sort = "id", page = 0)
                    Pageable pageable) {
        Page<AddressResponseDTO> dtoPage = addressService.getAllAddressesWhereLocationNull(pageable);
        AddressPageResponseDTO response = AddressPageResponseDTO.fromPage(dtoPage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
